package ma.projet.services;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProjetServiceTest {

    private ProjetService service;
    private SessionFactory sf;
    private Session session;

    @Before
    public void setup() {
        service = new ProjetService();
        sf = Mockito.mock(SessionFactory.class);
        session = Mockito.mock(Session.class);
        when(sf.openSession()).thenReturn(session);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testListerTachesPlanifieesPourProjet() {
        @SuppressWarnings("unchecked")
        Query<Tache> query = (Query<Tache>) Mockito.mock(Query.class);
        List<Tache> expected = Arrays.asList(new Tache());
        when(session.createQuery(anyString(), eq(Tache.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(1))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<Tache> result = service.listerTachesPlanifieesPourProjet(1);

        assertThat(result, is(expected));
        verify(session).createQuery("from Tache t where t.projet.id = :id order by t.dateDebut", Tache.class);
        verify(query).setParameter("id", 1);
        verify(query).list();
    }

    @Test
    public void testListerTachesRealiseesAvecDates() {
        @SuppressWarnings("unchecked")
        Query<EmployeTache> query = (Query<EmployeTache>) Mockito.mock(Query.class);
        List<EmployeTache> expected = Arrays.asList(new EmployeTache(), new EmployeTache());
        when(session.createQuery(anyString(), eq(EmployeTache.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(7))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<EmployeTache> result = service.listerTachesRealiseesAvecDates(7);

        assertThat(result, is(expected));
        verify(session).createQuery("from EmployeTache et where et.tache.projet.id = :id and et.dateDebutReelle is not null order by et.dateDebutReelle", EmployeTache.class);
    }

    @Test
    public void testAfficherDetailsProjetAvecTachesReelles() {
        Projet p = new Projet();
        p.setId(4);
        p.setNom("Gestion de stock");
        p.setDateDebut(new Date(113, 0, 14));
        when(session.get(eq(Projet.class), eq(4))).thenReturn(p);

        // Mock query list
        @SuppressWarnings("unchecked")
        Query<EmployeTache> query = (Query<EmployeTache>) Mockito.mock(Query.class);
        when(session.createQuery(anyString(), eq(EmployeTache.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(4))).thenReturn(query);

        // Create some EmployeTache entries with dates
        EmployeTache et1 = new EmployeTache();
        Tache t1 = new Tache(); t1.setId(12); t1.setNom("Analyse");
        et1.setTache(t1);
        et1.setDateDebutReelle(new Date(113,1,10));
        et1.setDateFinReelle(new Date(113,1,20));

        EmployeTache et2 = new EmployeTache();
        Tache t2 = new Tache(); t2.setId(13); t2.setNom("Conception");
        et2.setTache(t2);
        et2.setDateDebutReelle(new Date(113,2,10));
        et2.setDateFinReelle(new Date(113,2,15));

        EmployeTache et3 = new EmployeTache();
        Tache t3 = new Tache(); t3.setId(14); t3.setNom("Développement");
        et3.setTache(t3);
        et3.setDateDebutReelle(new Date(113,3,10));
        et3.setDateFinReelle(new Date(113,3,25));

        List<EmployeTache> l = Arrays.asList(et1, et2, et3);
        when(query.list()).thenReturn(l);

        String output = service.afficherDetailsProjetAvecTachesReelles(4);

        assertThat(output, containsString("Projet : 4"));
        assertThat(output, containsString("Nom : Gestion de stock"));
        assertThat(output, containsString("2013"));
        assertThat(output, containsString("12"));
        assertThat(output, containsString("Analyse"));
        assertThat(output, containsString("10/02/2013"));
        assertThat(output, containsString("20/02/2013"));
        assertThat(output, containsString("13"));
        assertThat(output, containsString("Conception"));
        assertThat(output, containsString("10/03/2013"));
        assertThat(output, containsString("15/03/2013"));
        assertThat(output, containsString("14"));
        assertThat(output, containsString("Développement"));
        assertThat(output, containsString("10/04/2013"));
        assertThat(output, containsString("25/04/2013"));
    }
}
