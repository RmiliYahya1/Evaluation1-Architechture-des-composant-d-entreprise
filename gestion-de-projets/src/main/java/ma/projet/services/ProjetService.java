package ma.projet.services;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ProjetService extends AbstractService<Projet> {

    public ProjetService() {
        super(Projet.class);
    }

    public List<Tache> listerTachesPlanifieesPourProjet(int projetId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tache> q = session.createQuery(
                    "from Tache t where t.projet.id = :id order by t.dateDebut",
                    Tache.class);
            q.setParameter("id", projetId);
            return q.list();
        }
    }

    public List<EmployeTache> listerTachesRealiseesAvecDates(int projetId) {
        try (Session session = sessionFactory.openSession()) {
            Query<EmployeTache> q = session.createQuery(
                    "from EmployeTache et where et.tache.projet.id = :id and et.dateDebutReelle is not null order by et.dateDebutReelle",
                    EmployeTache.class);
            q.setParameter("id", projetId);
            return q.list();
        }
    }

    public String afficherDetailsProjetAvecTachesReelles(int projetId) {
        try (Session session = sessionFactory.openSession()) {
            Projet p = session.get(Projet.class, projetId);
            if (p == null) return "Projet introuvable";

            SimpleDateFormat dfHeader = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            StringBuilder sb = new StringBuilder();
            sb.append("Projet : ").append(p.getId())
              .append("      Nom : ").append(p.getNom() == null ? "" : p.getNom())
              .append("     Date début : ").append(p.getDateDebut() == null ? "" : dfHeader.format(p.getDateDebut()))
              .append("\n");
            sb.append("Liste des tâches:\n");
            sb.append("Num Nom            Date Début Réelle   Date Fin Réelle\n");

            Query<EmployeTache> q = session.createQuery(
                    "from EmployeTache et where et.tache.projet.id = :id and et.dateDebutReelle is not null order by et.tache.id",
                    EmployeTache.class);
            q.setParameter("id", projetId);
            List<EmployeTache> l = q.list();

            for (EmployeTache et : l) {
                String idStr = String.valueOf(et.getTache().getId());
                String nom = et.getTache().getNom() == null ? "" : et.getTache().getNom();
                String paddedNom = String.format("%-14s", nom);
                String dd = et.getDateDebutReelle() == null ? "" : df.format(et.getDateDebutReelle());
                String dfR = et.getDateFinReelle() == null ? "" : df.format(et.getDateFinReelle());
                sb.append(String.format("%s  %s  %s          %s\n", idStr, paddedNom, dd, dfR));
            }
            return sb.toString();
        }
    }
}
