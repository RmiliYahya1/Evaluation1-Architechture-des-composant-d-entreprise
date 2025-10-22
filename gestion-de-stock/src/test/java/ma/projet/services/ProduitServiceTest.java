package ma.projet.services;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class ProduitServiceTest {
    private ProduitService service;
    private Categorie catA;
    private Categorie catB;
    private Produit p1;
    private Produit p2;
    private Produit p3;

    @Before
    public void setup() {
        service = new ProduitService();
        Map<String, Object> behavior = new HashMap<>();

        catA = new Categorie("A", "Alpha"); catA.setId(1);
        catB = new Categorie("B", "Beta"); catB.setId(2);

        p1 = new Produit("REF1", 50f, catA); p1.setId(1);
        p2 = new Produit("REF2", 150f, catA); p2.setId(2);
        p3 = new Produit("REF3", 200f, catB); p3.setId(3);

        Map<Integer, Object> byId = new HashMap<>();
        byId.put(1, p1);
        byId.put(2, p2);
        byId.put(3, p3);
        behavior.put("findById:Produit", byId);
        behavior.put("findAll:Produit", Arrays.asList(p1, p2, p3));

        Map<Object, List<?>> produitsByCategorie = new HashMap<>();
        produitsByCategorie.put(catA, Arrays.asList(p1, p2));
        produitsByCategorie.put(catB, Collections.singletonList(p3));
        behavior.put("produitsByCategorie", produitsByCategorie);

        String keyDates = LocalDate.of(2023,1,1) + "|" + LocalDate.of(2023,12,31);
        behavior.put("produitsBetweenDates", Collections.singletonMap(keyDates, Arrays.asList(p1, p3)));

        List<Object[]> cmd1Rows = Arrays.asList(
                new Object[]{"REF1", p1.getPrix(), 2},
                new Object[]{"REF2", p2.getPrix(), 1}
        );
        Map<Integer, List<?>> byCmd = new HashMap<>();
        byCmd.put(1, cmd1Rows);
        behavior.put("produitsByCommande", byCmd);

        behavior.put("prixSup100", Arrays.asList(p2, p3));

        SessionFactory sf = TestUtils.buildSessionFactoryFake(behavior);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testCreateReturnsTrue() {
        Produit p = new Produit("NEW", 10f, catA);
        assertTrue(service.create(p));
    }

    @Test
    public void testDeleteReturnsTrue() {
        assertTrue(service.delete(p1));
    }

    @Test
    public void testUpdateReturnsTrue() {
        p1.setPrix(60f);
        assertTrue(service.update(p1));
    }

    @Test
    public void testFindById() {
        Produit p = service.findById(2);
        assertNotNull(p);
        assertEquals("REF2", p.getReference());
    }

    @Test
    public void testFindAll() {
        List<Produit> list = service.findAll();
        assertEquals(3, list.size());
    }

    @Test
    public void testFindByCategorie() {
        List<Produit> listA = service.findByCategorie(catA);
        assertEquals(2, listA.size());
        assertEquals("REF1", listA.get(0).getReference());
    }

    @Test
    public void testFindProduitsBetweenDates() {
        List<Produit> list = service.findProduitsBetweenDates(LocalDate.of(2023,1,1), LocalDate.of(2023,12,31));
        assertEquals(2, list.size());
    }

    @Test
    public void testFindProduitsByCommande() {
        List<Object[]> rows = service.findProduitsByCommande(1);
        assertEquals(2, rows.size());
        assertEquals("REF1", rows.get(0)[0]);
        assertEquals(2, rows.get(0)[2]);
    }

    @Test
    public void testFindPrixSup100() {
        List<Produit> list = service.findPrixSup100();
        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(p -> p.getPrix() > 100));
    }
}