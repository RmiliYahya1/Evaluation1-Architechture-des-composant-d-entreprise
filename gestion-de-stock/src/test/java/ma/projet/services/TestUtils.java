package ma.projet.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.*;

public final class TestUtils {
    private TestUtils() {}

    public static void injectSessionFactory(Object service, SessionFactory sessionFactory) {
        try {
            Field f = service.getClass().getDeclaredField("sessionFactory");
            f.setAccessible(true);
            f.set(service, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SessionFactory buildSessionFactoryFake(Map<String, Object> behavior) {
        InvocationHandler sessionHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                if ("save".equals(name) || "delete".equals(name) || "update".equals(name)) {
                    return null;
                }
                if ("get".equals(name)) {
                    Map<Integer, Object> byId = (Map<Integer, Object>) behavior.getOrDefault("findById:" + ((Class<?>) args[0]).getSimpleName(), Collections.emptyMap());
                    return byId.get(args[1]);
                }
                if ("createQuery".equals(name)) {
                    String hql = (String) args[0];
                    Class<?> resultClass = (Class<?>) args[1];
                    return buildQueryFake(hql, resultClass, behavior);
                }
                if ("createNamedQuery".equals(name)) {
                    String qname = (String) args[0];
                    Class<?> resultClass = (Class<?>) args[1];
                    return buildNamedQueryFake(qname, resultClass, behavior);
                }
                throw new UnsupportedOperationException("Session method not supported in fake: " + name);
            }
        };

        InvocationHandler sessionFactoryHandler = (proxy, method, args) -> {
            if ("getCurrentSession".equals(method.getName())) {
                return Proxy.newProxyInstance(
                        Session.class.getClassLoader(),
                        new Class[]{Session.class},
                        sessionHandler
                );
            }
            throw new UnsupportedOperationException("SessionFactory method not supported in fake: " + method.getName());
        };

        return (SessionFactory) Proxy.newProxyInstance(
                SessionFactory.class.getClassLoader(),
                new Class[]{SessionFactory.class},
                sessionFactoryHandler
        );
    }

    private static <T> Query<T> buildQueryFake(String hql, Class<T> resultClass, Map<String, Object> behavior) {
        Map<String, Object> params = new HashMap<>();
        InvocationHandler qh = (proxy, method, args) -> {
            String m = method.getName();
            if ("setParameter".equals(m)) {
                params.put((String) args[0], args[1]);
                return proxy;
            }
            if ("list".equals(m)) {
                if (hql.equals("from " + resultClass.getSimpleName())) {
                    List<?> list = (List<?>) behavior.getOrDefault("findAll:" + resultClass.getSimpleName(), Collections.emptyList());
                    return list;
                }
                if (hql.contains("p.categorie = :categorie")) {
                    Object cat = params.get("categorie");
                    Map<Object, List<?>> map = (Map<Object, List<?>>) behavior.getOrDefault("produitsByCategorie", Collections.emptyMap());
                    return map.getOrDefault(cat, Collections.emptyList());
                }
                if (hql.contains("between :d1 and :d2")) {
                    LocalDate d1 = (LocalDate) params.get("d1");
                    LocalDate d2 = (LocalDate) params.get("d2");
                    Map<String, List<?>> map = (Map<String, List<?>>) behavior.getOrDefault("produitsBetweenDates", Collections.emptyMap());
                    String key = d1 + "|" + d2;
                    return map.getOrDefault(key, Collections.emptyList());
                }
                if (hql.contains("join l.produit") && !resultClass.equals(Object[].class)) {
                    return Collections.emptyList();
                }
                if (hql.contains("join l.produit") && resultClass.equals(Object[].class)) {
                    Integer idCmd = (Integer) params.get("idCommande");
                    Map<Integer, List<?>> map = (Map<Integer, List<?>>) behavior.getOrDefault("produitsByCommande", Collections.emptyMap());
                    return map.getOrDefault(idCmd, Collections.emptyList());
                }
                return Collections.emptyList();
            }
            if (method.getReturnType().isInstance(proxy)) return proxy;
            if (method.getReturnType().equals(void.class)) return null;
            throw new UnsupportedOperationException("Query method not supported in fake: " + m);
        };
        return (Query<T>) Proxy.newProxyInstance(Query.class.getClassLoader(), new Class[]{Query.class}, qh);
    }

    private static <T> Query<T> buildNamedQueryFake(String name, Class<T> resultClass, Map<String, Object> behavior) {
        InvocationHandler qh = (proxy, method, args) -> {
            String m = method.getName();
            if ("list".equals(m)) {
                if ("Produit.findPrixSup100".equals(name)) {
                    return behavior.getOrDefault("prixSup100", Collections.emptyList());
                }
                return Collections.emptyList();
            }
            if (method.getReturnType().isInstance(proxy)) return proxy;
            if (method.getReturnType().equals(void.class)) return null;
            throw new UnsupportedOperationException("NamedQuery method not supported in fake: " + m);
        };
        return (Query<T>) Proxy.newProxyInstance(Query.class.getClassLoader(), new Class[]{Query.class}, qh);
    }
}
