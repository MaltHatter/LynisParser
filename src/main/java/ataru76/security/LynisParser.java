package ataru76.security;

import ataru76.security.entity.Category;
import ataru76.security.entity.Report;
import ataru76.security.entity.Test;
import ataru76.security.entity.TestReport;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LynisParser {
    private static Pattern testPattern = Pattern.compile("-(?<desc>[\\w\\s]+)\\[(?<status>[\\w\\s]+)\\]");
    private static Pattern categoryPattern = Pattern.compile("\\[(\\+)\\](?<desc>[\\w\\s]+)");
    private static Pattern rankPattern = Pattern.compile("Hardening\\s*index\\s*:\\s*(?<rank>\\d*)\\s*\\[[#\\s]*\\]");


    private Repository repository;
    private Session session;


    public LynisParser() {
        repository = new Repository(false);
        session = repository.getSession();
    }


    public Report parseLog(String name, String auditor, String file) throws IOException {
        String category = "";

        session.beginTransaction();

        Report report = new Report();
        report.setAuditor(auditor);
        report.setName(Paths.get(file).getFileName().toString());

        Category category1 = null;
        String content = new String(Files.readAllBytes(Paths.get("/home/koji/lynis_reports/fedora"))).replaceAll("\u001B\\[(\\d*;)?\\d*\\w","") ;



        try (BufferedReader br = new BufferedReader(new StringReader(content))) {
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {

                sCurrentLine= sCurrentLine.trim();
                TestReport test = null;
                Matcher categoryMatcher = categoryPattern.matcher(sCurrentLine);
                if (categoryMatcher.matches())
                    category1 = parseCategory(categoryMatcher);

                Matcher testMatcher = testPattern.matcher(sCurrentLine);
                if (testMatcher.matches() && category1 != null) {
                    test = parseTest(testMatcher, category1);

                    report.getTests().add(test);
                }

                Matcher rankMatcher = rankPattern.matcher(sCurrentLine);
                if (rankMatcher.matches())
                    report.setRank( Integer.parseInt(rankMatcher.group("rank").trim()));

            }


            session.save(report);
            session.getTransaction().commit();

        } catch (IOException e) {
            e.printStackTrace();
            session.getTransaction().rollback();


        } finally {

            session.close();


        }
        return report;
    }

    private Category parseCategory(Matcher matcher) {
        String newCat = matcher.group("desc").trim();
        Category cat = repository.getSingle(session, Category.class, "description", newCat);
        if (cat == null) {
            session.persist(new Category(newCat));
            cat = repository.getSingle(session, Category.class, "description", newCat);
        }
        return cat;
    }


    public TestReport parseTest(Matcher matcher, Category category) {
        String t = matcher.group("desc").trim().replaceFirst("(Checking(\\sfor)?(\\sa\\s)?|Detecting|Searching(\\sfor)?(\\sa\\s)?|Installed|presence)\\s*", "");
        Test test = repository.getSingle(session, Test.class, "description", t);
        if (test == null) {
            session.persist(new Test(t, category));
            test = repository.getSingle(session, Test.class, "description", t);
        }

        TestReport testReport = new TestReport();
        testReport.setResult(matcher.group("status").trim());
        testReport.setTest(test);


        return testReport;
    }


  /*  public Test getTest(String test, String category, Repository repository, Session session) {
        Test o = repository.getSingle(session, Test.class, "description", test);

        if (o == null) {
            Category cat = repository.getSingle(session, Category.class, "description", category);
            o = new Test(test, (cat != null) ? cat : new Category(category));
        }

        return o;
    }


    private static String parseCategory(String value, String current) {
        Matcher matcher = categoryPattern.matcher(value);
        if (!matcher.matches())
            return null;
        String newCat = matcher.group("desc").trim();


        return (newCat != null) ? newCat : current;
    }

    private static String parseTest(String value, String current) {
        Matcher matcher = categoryPattern.matcher(value);
        if (!matcher.matches())
            return null;
        String newCat = matcher.group("desc").trim();


        return (newCat != null) ? newCat : current;
    }
*/

    public static void main(String[] args) throws IOException {

        Repository repository = new Repository(false);

        repository.generateReport(repository.getSession());



        /*

        File folder = new File("/home/koji/lynis_reports/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("importing " + file.getName());
                LynisParser parser = new LynisParser();
                parser.parseLog("","",file.getAbsolutePath());

            }
        }
*/






        //Report report = parser.parseLog("", "", "/home/koji/lynis_reports/fedora");



       //Report report = parser.parseLog("", "", "/home/koji/test");

    }
}
