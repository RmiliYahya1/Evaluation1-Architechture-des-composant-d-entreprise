package ma.projet.services;

import ma.projet.beans.Femme;
import ma.projet.dao.IDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class FemmeService implements IDao<Femme> {

    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean create(Femme o) {
        currentSession().save(o);
        return true;
    }

    @Override
    public boolean delete(Femme o) {
        currentSession().delete(o);
        return true;
    }

    @Override
    public boolean update(Femme o) {
        currentSession().update(o);
        return true;
    }

    @Override
    public Femme findById(int id) {
        return currentSession().get(Femme.class, id);
    }

    @Override
    public List<Femme> findAll() {
        return currentSession().createQuery("from Femme", Femme.class).getResultList();
    }

    public long countEnfantsEntreDates(int femmeId, Date dateDebut, Date dateFin) {
        Object single = currentSession()
                .getNamedNativeQuery("Femme.countEnfantsEntreDates")
                .setParameter("femmeId", femmeId)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .uniqueResult();
        if (single == null) return 0L;
        if (single instanceof Number) return ((Number) single).longValue();
        return Long.parseLong(single.toString());
    }

    public List<Femme> findMarieesDeuxFois() {
        Query<Femme> q = currentSession().createNamedQuery("Femme.findMarieesDeuxFois", Femme.class);
        return q.getResultList();
    }
}
