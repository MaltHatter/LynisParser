package ataru76.security;

import ataru76.security.entity.Category;
import ataru76.security.entity.Report;
import ataru76.security.entity.Test;
import ataru76.security.entity.TestReport;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.security.auth.login.Configuration;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Repository {
    private StandardServiceRegistry registry;
    private SessionFactory sessionFactory;
    private MetadataSources sources;


    public Repository(boolean enableCreate) {
        try {
            // Create Metadata
             sources = getMetadata(enableCreate);

            Metadata metadata = sources.getMetadataBuilder().build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();

        } catch (Exception e) {
            e.printStackTrace();
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }
    }


    public void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }


    public void makeDatabase() {
        try {
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setHaltOnError(true);
            schemaExport.setFormat(true);
            schemaExport.setDelimiter(";");
            schemaExport.setOutputFile("db-schema.sql");
            schemaExport.execute(EnumSet.of(TargetType.DATABASE), SchemaExport.Action.BOTH, sources.buildMetadata());


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("created!");
    }


    private static MetadataSources getMetadata(boolean create) {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.h2.Driver");
        settings.put("dialect", "org.hibernate.dialect.H2Dialect");
        settings.put("hibernate.connection.url", "jdbc:h2:~/lynis.h2db");
        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "");
        settings.put("show_sql", "true");
        settings.put("format_sql", "true");
        if (create)
            settings.put("hibernate.hbm2ddl.auto", "create-drop");


        StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources metadata = new MetadataSources(standardServiceRegistry);

        metadata.addAnnotatedClass(Test.class);
        metadata.addAnnotatedClass(Category.class);
        metadata.addAnnotatedClass(TestReport.class);
        metadata.addAnnotatedClass(Report.class);
        return metadata;

    }

    public Session getSession() {
        return sessionFactory.openSession();

    }


    public void saveReport(Report report) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(report);

        session.getTransaction().commit();
        session.close();
    }


    public static void main(String[] args) throws IOException {
        Repository create = new Repository(true);
        create.makeDatabase();
        create.shutdown();

    }


    public <T> List<T> getAll(Session session, Class<T> clazz) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T> myObjectRoot = criteria.from(clazz);

        criteria.select(myObjectRoot);

        Query<T> q = session.createQuery(criteria);
        return q.getResultList();

    }


    public <T> T getSingle(Session session, Class<T> clazz, String name, String value) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T> root = criteria.from(clazz);

        criteria.select(root);
        criteria.where(builder.equal(root.get(name), value));

        Query<T> q = session.createQuery(criteria);

        List<T> list = q.getResultList();

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);

    }


    public List<Report> generateReport(Session session) {
       List<Test> tests = getAll(session,Test.class);
       if(tests==null||tests.isEmpty()){
           System.out.println("No reports found!" );
           return null;
       }


        System.out.println("test\t"  + tests.get(0).getTestReports().stream().map(r -> r.getReport().getName()).collect(Collectors.joining("\t")));
        System.out.println("rank\t"  + tests.get(0).getTestReports().stream().map(r ->  Integer.toString(r.getReport().getRank())).collect(Collectors.joining("\t")));




        for (Test test:tests) {
            System.out.print(test.getDescription()  + "\t" + test.getTestReports().stream().map(r -> r.getResult()).collect(Collectors.joining("\t")) + "\n" );
        }


        return null;

    }

}



