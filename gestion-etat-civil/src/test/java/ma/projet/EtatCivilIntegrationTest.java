package ma.projet;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.services.FemmeService;
import ma.projet.services.HommeService;
import ma.projet.services.MariageService;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class EtatCivilIntegrationTest {

    private static AnnotationConfigApplicationContext ctx;
    private static FemmeService femmeService;
    private static HommeService hommeService;
    private static MariageService mariageService;
    private static SessionFactory sessionFactory;
    private static HibernateTransactionManager txManager;
    private static TransactionStatus txStatus;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final List<Femme> femmes = new ArrayList<>();
    private static final List<Homme> hommes = new ArrayList<>();
    private static Homme hommeAvec4Epouses;
    private static Femme femmeAvecDeuxMariages;

    @BeforeClass
    public static void setupDatabaseAndData() throws Exception {
        ctx = new AnnotationConfigApplicationContext(HibernateUtil.class);
        // Acquire only SessionFactory from Spring context, instantiate services manually
        sessionFactory = ctx.getBean(SessionFactory.class);
        femmeService = new FemmeService();
        hommeService = new HommeService();
        mariageService = new MariageService();
        injectSessionFactory(femmeService);
        injectSessionFactory(hommeService);
        injectSessionFactory(mariageService);

        txManager = ctx.getBean(HibernateTransactionManager.class);
        txStatus = txManager.getTransaction(new DefaultTransactionDefinition());

        cleanupDatabase();

        seedPersons();
        seedMariages();

        txManager.commit(txStatus);
    }

    @Before
    public void beginTx() {
        txStatus = txManager.getTransaction(new DefaultTransactionDefinition());
    }

    @After
    public void commitTx() {
        if (txStatus != null && !txStatus.isCompleted()) {
            txManager.commit(txStatus);
        }
    }

    private static void injectSessionFactory(Object service) {
        try {
            Field field = service.getClass().getDeclaredField("sessionFactory");
            field.setAccessible(true);
            field.set(service, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException("Injection du SessionFactory a échoué", e);
        }
    }

    private static void cleanupDatabase() {
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.createQuery("delete from Mariage").executeUpdate();
            s.createQuery("delete from Femme").executeUpdate();
            s.createQuery("delete from Homme").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        } finally {
            s.close();
        }
    }

    private static Date d(String str) {
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void seedPersons() {
        // 10 femmes
        for (int i = 0; i < 10; i++) {
            Femme f = new Femme();
            f.setNom("Femme" + (i + 1));
            f.setPrenom("PrenomF" + (i + 1));
            f.setDateNaissance(d(String.format("0%d/01/197%d", (i % 9) + 1, (i < 5 ? 0 : 1))));
            f.setAdresse("AddrF" + (i + 1));
            f.setTelephone("0600000" + i);
            assertTrue(femmeService.create(f));
            femmes.add(f);
        }

        for (int i = 0; i < 5; i++) {
            Homme h = new Homme();
            h.setNom("Homme" + (i + 1));
            h.setPrenom("PrenomH" + (i + 1));
            h.setDateNaissance(d(String.format("0%d/02/196%d", (i % 9) + 1, (i < 3 ? 2 : 3))));
            h.setAdresse("AddrH" + (i + 1));
            h.setTelephone("0700000" + i);
            assertTrue(hommeService.create(h));
            hommes.add(h);
        }
        hommeAvec4Epouses = hommes.get(0);
    }

    private static void seedMariages() {
        Femme f1 = femmes.get(0);
        Femme f2 = femmes.get(1);
        Femme f3 = femmes.get(2);
        Femme f4 = femmes.get(3);

        createMariage(hommeAvec4Epouses, f1, d("03/09/1989"), d("03/09/1990"), 0);

        createMariage(hommeAvec4Epouses, f2, d("03/09/1990"), null, 4);

        createMariage(hommeAvec4Epouses, f3, d("03/09/1995"), null, 2);

        createMariage(hommeAvec4Epouses, f4, d("04/11/2000"), null, 3);

        createMariage(hommes.get(1), femmes.get(4), d("01/01/2010"), null, 1);

        femmeAvecDeuxMariages = femmes.get(4);
        createMariage(hommes.get(2), femmeAvecDeuxMariages, d("01/01/2001"), d("01/01/2005"), 1);
        createMariage(hommes.get(3), femmeAvecDeuxMariages, d("02/02/2006"), null, 2);
    }

    private static void createMariage(Homme h, Femme f, Date start, Date end, int children) {
        Mariage m = new Mariage();
        m.setHomme(h);
        m.setFemme(f);
        m.setDateDebut(start);
        m.setDateFin(end);
        m.setNbrEnfant(children);
        assertTrue(mariageService.create(m));
    }

    @Test
    public void testCreationCounts() {
        assertTrue(femmeService.findAll().size() >= 10);
        assertTrue(hommeService.findAll().size() >= 5);
    }

    @Test
    public void testAfficherListeFemmes() {
        List<Femme> list = femmeService.findAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        List<String> names = list.stream().map(Femme::getNom).collect(Collectors.toList());
        assertTrue(names.contains("Femme1"));
    }

    @Test
    public void testFemmeLaPlusAgee() {
        Femme oldest = femmeService.findAll().stream()
                .min(Comparator.comparing(Femme::getDateNaissance))
                .orElse(null);
        assertNotNull(oldest);
        assertEquals("Femme1", oldest.getNom());
    }

    @Test
    public void testEpousesDunHommeEntreDeuxDates() {
        List<Femme> epouses = hommeService.getEpousesEntreDates(hommeAvec4Epouses.getId(), d("01/01/1980"), d("31/12/2010"));
        assertEquals(4, epouses.size());
    }

    @Test
    public void testNombreEnfantsFemmeEntreDeuxDates() {
        Femme femme2 = femmes.get(1);
        long enfants = femmeService.countEnfantsEntreDates(femme2.getId(), d("01/01/1989"), d("31/12/2000"));
        assertEquals(4L, enfants);
    }

    @Test
    public void testFemmesMarieesDeuxFoisOuPlus() {
        List<Femme> result = femmeService.findMarieesDeuxFois();
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(f -> f.getId() == femmeAvecDeuxMariages.getId()));
    }

    @Test
    public void testHommesMarieAQuatreFemmesEntreDeuxDates() {
        long count = hommeService.countHommesMarieAQuatreFemmesEntre(d("01/01/1989"), d("31/12/2005"));
        assertEquals(1L, count);
    }

    @Test
    public void testAfficherMariagesHommeDetails() {
        String details = hommeService.afficherMariagesHomme(hommeAvec4Epouses.getId());
        assertNotNull(details);
        assertTrue(details.contains("Mariages En Cours"));
        assertTrue(details.toUpperCase().contains(femmes.get(1).getNom().toUpperCase()));
        assertTrue(details.toUpperCase().contains(femmes.get(2).getNom().toUpperCase()));
        assertTrue(details.toUpperCase().contains(femmes.get(3).getNom().toUpperCase()));
        assertTrue(details.contains("Mariages échoués"));
        assertTrue(details.toUpperCase().contains(femmes.get(0).getNom().toUpperCase()));
    }
}
