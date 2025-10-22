package ma.projet.services;

import ma.projet.classes.Produit;
import ma.projet.classes.Categorie;
import ma.projet.dao.IDao;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProduitService implements IDao<Produit> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean create(Produit o) {
        sessionFactory.getCurrentSession().save(o);
        return true;
    }

    @Override
    public boolean delete(Produit o) {
        sessionFactory.getCurrentSession().delete(o);
        return true;
    }

    @Override
    public boolean update(Produit o) {
        sessionFactory.getCurrentSession().update(o);
        return true;
    }

    @Override
    public Produit findById(int id) {
        return sessionFactory.getCurrentSession().get(Produit.class, id);
    }

    @Override
    public List<Produit> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Produit", Produit.class)
                .list();
    }


    public List<Produit> findByCategorie(Categorie categorie) {
        String hql = "from Produit p where p.categorie = :categorie";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Produit.class)
                .setParameter("categorie", categorie)
                .list();
    }

    public List<Produit> findProduitsBetweenDates(LocalDate date1, LocalDate date2) {
        String hql = """
            select distinct l.produit
            from LigneCommandeProduit l
            where l.commande.date between :d1 and :d2
        """;
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Produit.class)
                .setParameter("d1", date1)
                .setParameter("d2", date2)
                .list();
    }

    public List<Object[]> findProduitsByCommande(int idCommande) {
        String hql = """
            select p.reference, p.prix, l.quantite
            from LigneCommandeProduit l
            join l.produit p
            where l.commande.id = :idCommande
        """;
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Object[].class)
                .setParameter("idCommande", idCommande)
                .list();
    }

    public List<Produit> findPrixSup100() {
        Query<Produit> query = sessionFactory.getCurrentSession()
                .createNamedQuery("Produit.findPrixSup100", Produit.class);
        return query.list();
    }
}
