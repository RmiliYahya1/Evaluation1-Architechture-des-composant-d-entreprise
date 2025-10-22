package ma.projet.services;

import ma.projet.classes.Tache;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TacheService extends AbstractService<Tache> {
    public TacheService() {
        super(Tache.class);
    }

    public List<Tache> listerTachesPrixSup1000() {
        try (Session session = sessionFactory.openSession()) {
            Query<Tache> q = session.createNamedQuery("Tache.findPrixSup1000", Tache.class);
            return q.list();
        }
    }


    public List<Tache> listerTachesRealiseesEntre(Date dateDebut, Date dateFin) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tache> q = session.createQuery(
                    "select distinct et.tache from EmployeTache et " +
                    "where et.dateDebutReelle is not null and (" +
                    "(et.dateDebutReelle between :d1 and :d2) or (et.dateFinReelle between :d1 and :d2)" +
                    ") order by et.tache.dateDebut",
                    Tache.class);
            q.setParameter("d1", dateDebut);
            q.setParameter("d2", dateFin);
            return q.list();
        }
    }
}
