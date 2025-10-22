package ma.projet.services;

import ma.projet.classes.Tache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TacheServiceTest {

    private TacheService service;
    private SessionFactory sf;
    private Session session;

    @Before
    public void setup() {
        service = new TacheService();
        sf = Mockito.mock(SessionFactory.class);
        session = Mockito.mock(Session.class);
        when(sf.openSession()).thenReturn(session);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testListerTachesPrixSup1000() {
        @SuppressWarnings("unchecked")
        Query<Tache> query = (Query<Tache>) Mockito.mock(Query.class);
        List<Tache> expected = Arrays.asList(new Tache(), new Tache());
        when(session.createNamedQuery(eq("Tache.findPrixSup1000"), eq(Tache.class))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<Tache> result = service.listerTachesPrixSup1000();

        assertThat(result, is(expected));
        verify(session).createNamedQuery("Tache.findPrixSup1000", Tache.class);
        verify(query).list();
    }

    @Test
    public void testListerTachesRealiseesEntre() {
        @SuppressWarnings("unchecked")
        Query<Tache> query = (Query<Tache>) Mockito.mock(Query.class);
        List<Tache> expected = Arrays.asList(new Tache());
        when(session.createQuery(anyString(), eq(Tache.class))).thenReturn(query);
        Date d1 = new Date(123, 0, 1);
        Date d2 = new Date(123, 11, 31);
        when(query.setParameter(eq("d1"), eq(d1))).thenReturn(query);
        when(query.setParameter(eq("d2"), eq(d2))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<Tache> result = service.listerTachesRealiseesEntre(d1, d2);

        assertThat(result, is(expected));
        verify(session).createQuery("select distinct et.tache from EmployeTache et where et.dateDebutReelle is not null and ((et.dateDebutReelle between :d1 and :d2) or (et.dateFinReelle between :d1 and :d2)) order by et.tache.dateDebut", Tache.class);
        verify(query).setParameter("d1", d1);
        verify(query).setParameter("d2", d2);
        verify(query).list();
    }
}
