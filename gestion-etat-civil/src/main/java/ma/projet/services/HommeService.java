package ma.projet.services;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class HommeService implements IDao<Homme> {

    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean create(Homme o) {
        Session s = currentSession();
        s.save(o);
        return true;
    }

    @Override
    public boolean delete(Homme o) {
        Session s = currentSession();
        s.delete(o);
        return true;
    }

    @Override
    public boolean update(Homme o) {
        Session s = currentSession();
        s.update(o);
        return true;
    }

    @Override
    public Homme findById(int id) {
        return currentSession().get(Homme.class, id);
    }

    @Override
    public List<Homme> findAll() {
        return currentSession().createQuery("from Homme", Homme.class).getResultList();
    }

    public List<Femme> getEpousesEntreDates(int hommeId, Date dateDebut, Date dateFin) {
        String hql = "select distinct m.femme from Mariage m where m.homme.id = :hid and m.dateDebut >= :d1 and m.dateDebut <= :d2";
        Query<Femme> q = currentSession().createQuery(hql, Femme.class);
        q.setParameter("hid", hommeId);
        q.setParameter("d1", dateDebut);
        q.setParameter("d2", dateFin);
        return q.getResultList();
    }

    public long countHommesMarieAQuatreFemmesEntre(Date dateDebut, Date dateFin) {
        Session s = currentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();

        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Mariage> m = cq.from(Mariage.class);
        Join<Mariage, Homme> h = m.join("homme");
        Join<Mariage, Femme> f = m.join("femme");

        Predicate between = cb.between(m.get("dateDebut"), dateDebut, dateFin);

        cq.select(h.get("id"));
        cq.where(between);
        cq.groupBy(h.get("id"));
        cq.having(cb.equal(cb.countDistinct(f.get("id")), 4L));

        List<Integer> ids = s.createQuery(cq).getResultList();
        return ids == null ? 0L : ids.size();
    }

    public String afficherMariagesHomme(int hommeId) {
        Homme h = findById(hommeId);
        if (h == null) {
            return "Homme introuvable (id=" + hommeId + ")";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        String nomComplet = (h.getNom() != null ? h.getNom() : "") + " " + (h.getPrenom() != null ? h.getPrenom() : "");
        sb.append("Nom : ").append(nomComplet.toUpperCase()).append("\n");

        List<Mariage> enCours = currentSession()
                .createQuery("from Mariage m where m.homme.id = :hid and m.dateFin is null order by m.dateDebut asc", Mariage.class)
                .setParameter("hid", hommeId)
                .getResultList();

        sb.append("Mariages En Cours :\n");
        int i = 1;
        for (Mariage m : enCours) {
            Femme f = m.getFemme();
            String nomF = f != null ? ((f.getNom() != null ? f.getNom() : "") + " " + (f.getPrenom() != null ? f.getPrenom() : "")) : "";
            sb.append(i++).append(". Femme : ")
              .append(nomF.toUpperCase())
              .append("   Date Début : ")
              .append(m.getDateDebut() != null ? sdf.format(m.getDateDebut()) : "")
              .append("    Nbr Enfants : ")
              .append(m.getNbrEnfant())
              .append("\n");
        }

        List<Mariage> echoues = currentSession()
                .createQuery("from Mariage m where m.homme.id = :hid and m.dateFin is not null order by m.dateDebut asc", Mariage.class)
                .setParameter("hid", hommeId)
                .getResultList();

        sb.append("\nMariages échoués :\n");
        i = 1;
        for (Mariage m : echoues) {
            Femme f = m.getFemme();
            String nomF = f != null ? ((f.getNom() != null ? f.getNom() : "") + " " + (f.getPrenom() != null ? f.getPrenom() : "")) : "";
            sb.append(i++).append(". Femme : ")
              .append(nomF.toUpperCase())
              .append("  Date Début : ")
              .append(m.getDateDebut() != null ? sdf.format(m.getDateDebut()) : "")
              .append("    \nDate Fin : ")
              .append(m.getDateFin() != null ? sdf.format(m.getDateFin()) : "")
              .append("    Nbr Enfants : ")
              .append(m.getNbrEnfant())
              .append("\n");
        }

        return sb.toString();
    }
}
