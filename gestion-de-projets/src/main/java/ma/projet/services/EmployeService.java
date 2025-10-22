package ma.projet.services;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeService extends AbstractService<Employe> {

    public EmployeService() {
        super(Employe.class);
    }

    public List<EmployeTache> listerTachesRealiseesParEmploye(int employeId) {
        try (Session session = sessionFactory.openSession()) {
            Query<EmployeTache> q = session.createQuery(
                    "from EmployeTache et where et.employe.id = :id and et.dateDebutReelle is not null",
                    EmployeTache.class);
            q.setParameter("id", employeId);
            return q.list();
        }
    }

    public List<Projet> listerProjetsGeresParEmploye(int employeId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Projet> q = session.createQuery(
                    "from Projet p where p.chef.id = :id",
                    Projet.class);
            q.setParameter("id", employeId);
            return q.list();
        }
    }

    public List<Tache> listerTachesPlanifieesParEmploye(int employeId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tache> q = session.createQuery(
                    "select distinct et.tache from EmployeTache et where et.employe.id = :id",
                    Tache.class);
            q.setParameter("id", employeId);
            return q.list();
        }
    }
}
