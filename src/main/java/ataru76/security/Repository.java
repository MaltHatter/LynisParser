package ataru76.security;

import ataru76.security.entity.Category;
import ataru76.security.entity.Report;
import ataru76.security.entity.Test;
import ataru76.security.entity.TestReport;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
            sources = getMetadata_h2(enableCreate);

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


    private static MetadataSources getMetadata_h2(boolean create) {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.h2.Driver");
        settings.put("dialect", "org.hibernate.dialect.H2Dialect");
        settings.put("hibernate.connection.url", "jdbc:h2:~/lynis.db");
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

    private static MetadataSources getMetadata_Sqlite(boolean create) {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.sqlite.JDBC");
        settings.put("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
        settings.put("hibernate.archive.autodetection", "class");
        settings.put("hibernate.connection.url", "jdbc:sqlite:/home/koji/lynis.db");
        settings.put("hibernate.connection.user", "");
        settings.put("hibernate.connection.username", "");
        settings.put("hibernate.connection.autocommit", "false");
        //settings.put("hibernate.flushMode", "");
        settings.put("hibernate.cache.use_second_level_cache", "false");
        settings.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

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

    private static MetadataSources getMetadata_HSQL(boolean create) {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        settings.put("dialect", "org.hibernate.dialect.HSQLDialect");
        settings.put("hibernate.connection.url", "jdbc:hsqldb:file:/home/koji/lynis.db;sql.enforce_strict_size=true;hsqldb.tx=mvcc");
        settings.put("hibernate.connection.username", "sa");
        settings.put("hibernate.connection.password", "sa");
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


    private static String writeStatus(TestReport test) {
        switch (test.getSeverity()) {
            case OK:
                return "";
            case LOW:
                return "\u2713";

            case MEDIUM:
                return "\u26A0";
            case HIGH:
                return "x";
            default:
                return "";
        }


    }

    public List<Report> generateReport(Session session, String path) throws IOException {
        List<Test> tests = getAll(session, Test.class);
        if (tests == null || tests.isEmpty()) {
            System.out.println("No reports found!");
            return null;
        }

        Files.deleteIfExists(Paths.get(path));


        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            String header = "test," + tests.get(0).getTestReports().stream().map(r -> r.getReport().getName()).collect(Collectors.joining(","));

            writer.write(header);
            writer.newLine();
            for (Test test : tests) {
                String line = test.getDescription() + "," + test.getTestReports().stream().map(r -> writeStatus(r)).collect(Collectors.joining(","));
                writer.write(line);
                writer.newLine();
            }
        }
        return null;
    }


    public List<Report> generateReportText(Session session, String path) throws IOException {
        List<Category> categories = getAll(session, Category.class);

        if (categories == null || categories.isEmpty()) {
            System.out.println("No reports found!");
            return null;
        }


        Files.deleteIfExists(Paths.get(path));

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {

            for (Category c : categories) {
                String cat = c.getDescription().toUpperCase().replaceAll("[\\n*\\t*\\s*,]", " ");

                if ("Initializing program".equalsIgnoreCase(cat) || cat.startsWith("PLUGIN") || cat.contains("(CUSTOM)"))
                    continue;

                //writer.write(c.getDescription().toUpperCase() + "\n");
                writer.write(cat + "," + categories.get(0).getTests().get(0).getTestReports().stream().map(r -> r.getReport().getName()).collect(Collectors.joining(",")) + "\n");

                for (Test test : c.getTests()) {
                    writer.write("  " + test.getDescription().replaceAll("[\\n*\\t*\\s*,]", " ") + "," + test.getTestReports().stream().map(r -> writeStatus(r)).collect(Collectors.joining(",")) + "\n");
                }
            }

        }
        return null;
    }


}



