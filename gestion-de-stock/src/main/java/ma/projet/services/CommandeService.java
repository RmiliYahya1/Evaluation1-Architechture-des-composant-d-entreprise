package ma.projet.services;

import ma.projet.classes.Commande;
import ma.projet.dao.IDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommandeService implements IDao<Commande> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean create(Commande o) {
        sessionFactory.getCurrentSession().save(o);
        return true;
    }

    @Override
    public boolean delete(Commande o) {
        sessionFactory.getCurrentSession().delete(o);
        return true;
    }

    @Override
    public boolean update(Commande o) {
        sessionFactory.getCurrentSession().update(o);
        return true;
    }

    @Override
    public Commande findById(int id) {
        return sessionFactory.getCurrentSession().get(Commande.class, id);
    }

    @Override
    public List<Commande> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Commande", Commande.class)
                .list();
    }
}
