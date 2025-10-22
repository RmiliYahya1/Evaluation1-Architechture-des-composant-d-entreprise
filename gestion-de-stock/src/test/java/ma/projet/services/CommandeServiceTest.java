package ma.projet.services;

import ma.projet.classes.Commande;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class CommandeServiceTest {
    private CommandeService service;

    @Before
    public void setup() {
        service = new CommandeService();
        Map<String, Object> behavior = new HashMap<>();

        // Data fixtures
        Commande c1 = new Commande(LocalDate.of(2023,1,1)); c1.setId(1);
        Commande c2 = new Commande(LocalDate.of(2024,2,2)); c2.setId(2);

        Map<Integer, Object> byId = new HashMap<>();
        byId.put(1, c1);
        byId.put(2, c2);
        behavior.put("findById:Commande", byId);
        behavior.put("findAll:Commande", Arrays.asList(c1, c2));

        SessionFactory sf = TestUtils.buildSessionFactoryFake(behavior);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testCreateReturnsTrue() {
        Commande c = new Commande(LocalDate.now());
        assertTrue(service.create(c));
    }

    @Test
    public void testDeleteReturnsTrue() {
        Commande c = new Commande(LocalDate.now()); c.setId(1);
        assertTrue(service.delete(c));
    }

    @Test
    public void testUpdateReturnsTrue() {
        Commande c = new Commande(LocalDate.now()); c.setId(2);
        assertTrue(service.update(c));
    }

    @Test
    public void testFindById() {
        Commande c = service.findById(1);
        assertNotNull(c);
        assertEquals(LocalDate.of(2023,1,1), c.getDate());
    }

    @Test
    public void testFindAll() {
        java.util.List<Commande> list = service.findAll();
        assertEquals(2, list.size());
    }
}