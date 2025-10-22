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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class EmployeServiceTest {

    private EmployeService service;
    private SessionFactory sf;
    private Session session;

    @Before
    public void setup() {
        service = new EmployeService();
        sf = Mockito.mock(SessionFactory.class);
        session = Mockito.mock(Session.class);
        when(sf.openSession()).thenReturn(session);
        TestUtils.injectSessionFactory(service, sf);
    }

    @Test
    public void testListerTachesRealiseesParEmploye() {
        @SuppressWarnings("unchecked")
        Query<EmployeTache> query = (Query<EmployeTache>) Mockito.mock(Query.class);
        List<EmployeTache> expected = Arrays.asList(new EmployeTache(), new EmployeTache());
        when(session.createQuery(anyString(), eq(EmployeTache.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(5))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<EmployeTache> result = service.listerTachesRealiseesParEmploye(5);

        assertThat(result, is(expected));
        verify(session).createQuery("from EmployeTache et where et.employe.id = :id and et.dateDebutReelle is not null", EmployeTache.class);
        verify(query).setParameter("id", 5);
        verify(query).list();
    }

    @Test
    public void testListerProjetsGeresParEmploye() {
        @SuppressWarnings("unchecked")
        Query<Projet> query = (Query<Projet>) Mockito.mock(Query.class);
        List<Projet> expected = Arrays.asList(new Projet());
        when(session.createQuery(anyString(), eq(Projet.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(2))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<Projet> result = service.listerProjetsGeresParEmploye(2);

        assertThat(result, is(expected));
        verify(session).createQuery("from Projet p where p.chef.id = :id", Projet.class);
        verify(query).setParameter("id", 2);
        verify(query).list();
    }

    @Test
    public void testListerTachesPlanifieesParEmploye() {
        @SuppressWarnings("unchecked")
        Query<Tache> query = (Query<Tache>) Mockito.mock(Query.class);
        List<Tache> expected = Arrays.asList(new Tache(), new Tache(), new Tache());
        when(session.createQuery(anyString(), eq(Tache.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(9))).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<Tache> result = service.listerTachesPlanifieesParEmploye(9);

        assertThat(result, is(expected));
        verify(session).createQuery("select distinct et.tache from EmployeTache et where et.employe.id = :id", Tache.class);
        verify(query).setParameter("id", 9);
        verify(query).list();
    }
}
