package ma.projet.services;

import ma.projet.classes.Categorie;
import ma.projet.dao.IDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategorieService implements IDao<Categorie> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean create(Categorie o) {
        sessionFactory.getCurrentSession().save(o);
        return true;
    }

    @Override
    public boolean delete(Categorie o) {
        sessionFactory.getCurrentSession().delete(o);
        return true;
    }

    @Override
    public boolean update(Categorie o) {
        sessionFactory.getCurrentSession().update(o);
        return true;
    }

    @Override
    public Categorie findById(int id) {
        return sessionFactory.getCurrentSession().get(Categorie.class, id);
    }

    @Override
    public List<Categorie> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Categorie", Categorie.class)
                .list();
    }
}
