package ma.projet.services;

import ma.projet.classes.LigneCommandeProduit;
import ma.projet.dao.IDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LigneCommandeService implements IDao<LigneCommandeProduit> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean create(LigneCommandeProduit o) {
        sessionFactory.getCurrentSession().save(o);
        return true;
    }

    @Override
    public boolean delete(LigneCommandeProduit o) {
        sessionFactory.getCurrentSession().delete(o);
        return true;
    }

    @Override
    public boolean update(LigneCommandeProduit o) {
        sessionFactory.getCurrentSession().update(o);
        return true;
    }

    @Override
    public LigneCommandeProduit findById(int id) {
        return sessionFactory.getCurrentSession().get(LigneCommandeProduit.class, id);
    }

    @Override
    public List<LigneCommandeProduit> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from LigneCommandeProduit", LigneCommandeProduit.class)
                .list();
    }
}
