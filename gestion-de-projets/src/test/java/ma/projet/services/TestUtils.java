package ma.projet.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.lang.reflect.Field;

class TestUtils {
    static void injectSessionFactory(Object service, SessionFactory sessionFactory) {
        try {
            Field f = AbstractService.class.getDeclaredField("sessionFactory");
            f.setAccessible(true);
            f.set(service, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
