package ma.projet.services;

import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class MariageService implements IDao<Mariage> {

    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean create(Mariage o) {
        currentSession().save(o);
        return true;
    }

    @Override
    public boolean delete(Mariage o) {
        currentSession().delete(o);
        return true;
    }

    @Override
    public boolean update(Mariage o) {
        currentSession().update(o);
        return true;
    }

    @Override
    public Mariage findById(int id) {
        return currentSession().get(Mariage.class, id);
    }

    @Override
    public List<Mariage> findAll() {
        return currentSession().createQuery("from Mariage", Mariage.class).getResultList();
    }
}
