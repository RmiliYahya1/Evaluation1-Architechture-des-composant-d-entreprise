package ma.projet.services;

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
import static org.mockito.Mockito.*;

public class AbstractServiceGenericTest {

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
    public void testFindAllDelegatesToHqlFromEntity() {
        @SuppressWarnings("unchecked")
        Query<Tache> query = (Query<Tache>) Mockito.mock(Query.class);
        List<Tache> expected = Arrays.asList(new Tache(), new Tache(), new Tache());
        when(session.createQuery("from Tache", Tache.class)).thenReturn(query);
        when(query.list()).thenReturn(expected);

        List<Tache> result = service.findAll();

        assertThat(result, is(expected));
        verify(session).createQuery("from Tache", Tache.class);
        verify(query).list();
    }
}
