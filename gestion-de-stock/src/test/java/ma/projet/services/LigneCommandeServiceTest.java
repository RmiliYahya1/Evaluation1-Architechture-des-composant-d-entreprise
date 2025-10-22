package ma.projet.services;

import ma.projet.classes.LigneCommandeProduit;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class LigneCommandeServiceTest {
    private LigneCommandeService service;

    @Before
    public void setup() {
        service = new LigneCommandeService();
        Map<String, Object> behavior = new HashMap<>();

        LigneCommandeProduit l1 = new LigneCommandeProduit(); l1.setId(1);
        LigneCommandeProduit l2 = new LigneCommandeProduit(); l2.setId(2);

        Map<Integer, Object> byId = new HashMap<>();
        byId.put(1, l1);
        byId.put(2, l2);
        behavior.put("findById:LigneCommandeProduit", byId);
        behavior.put("findAll:LigneCommandeProduit", Arrays.asList(l1, l2));

        SessionFactory sf = TestUtils.buildSessionFactoryFake(behavior);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testCreateReturnsTrue() {
        LigneCommandeProduit l = new LigneCommandeProduit();
        assertTrue(service.create(l));
    }

    @Test
    public void testDeleteReturnsTrue() {
        LigneCommandeProduit l = new LigneCommandeProduit(); l.setId(1);
        assertTrue(service.delete(l));
    }

    @Test
    public void testUpdateReturnsTrue() {
        LigneCommandeProduit l = new LigneCommandeProduit(); l.setId(2);
        assertTrue(service.update(l));
    }

    @Test
    public void testFindById() {
        LigneCommandeProduit l = service.findById(1);
        assertNotNull(l);
        assertEquals(1, l.getId());
    }

    @Test
    public void testFindAll() {
        java.util.List<LigneCommandeProduit> list = service.findAll();
        assertEquals(2, list.size());
    }
}