package ma.projet.services;

import ma.projet.classes.Categorie;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CategorieServiceTest {
    private CategorieService service;

    @Before
    public void setup() {
        service = new CategorieService();
        Map<String, Object> behavior = new HashMap<>();

        // Data fixtures
        Categorie c1 = new Categorie("C1", "Cat 1"); c1.setId(1);
        Categorie c2 = new Categorie("C2", "Cat 2"); c2.setId(2);

        Map<Integer, Object> byId = new HashMap<>();
        byId.put(1, c1);
        byId.put(2, c2);
        behavior.put("findById:Categorie", byId);
        behavior.put("findAll:Categorie", Arrays.asList(c1, c2));

        SessionFactory sf = TestUtils.buildSessionFactoryFake(behavior);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testCreateReturnsTrue() {
        Categorie c = new Categorie("C3", "Cat 3");
        assertTrue(service.create(c));
    }

    @Test
    public void testDeleteReturnsTrue() {
        Categorie c = new Categorie("C1", "Cat 1"); c.setId(1);
        assertTrue(service.delete(c));
    }

    @Test
    public void testUpdateReturnsTrue() {
        Categorie c = new Categorie("C2", "Cat 2"); c.setId(2);
        assertTrue(service.update(c));
    }

    @Test
    public void testFindById() {
        Categorie c = service.findById(1);
        assertNotNull(c);
        assertEquals("C1", c.getCode());
    }

    @Test
    public void testFindAll() {
        java.util.List<Categorie> list = service.findAll();
        assertEquals(2, list.size());
    }
}