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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LynisParser {
    private static Pattern testPattern = Pattern.compile("\\s*\u001B\\[(\\d|\\w|\\s)*-\\s* ((?<desc>.*)?\u001B)\\[\\d*\\w*\\s*\\[\\s*\u001B(\\[(?<color>\\d*;\\d*)m(?<value>.*)?\u001B\\[0).*");
    private static Pattern categoryPattern = Pattern.compile("\\s*\\[\\+\\]\\s*\u001B\\[\\d*;\\d*m(?<desc>.*)?\u001B\\[0m\\s*");
    private static Pattern rankPattern = Pattern.compile("\\s*\u001B\\[\\d*;\\d*mHardening\\s*index\u001B\\[0m\\s*:\\s*\u001B\\[(?<color>\\d*;\\d*)m(?<rank>\\d*).*");


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
//List<String> lines = Files.readAllLines(Paths.get("./duke.txt"), Charset.defaultCharset());

        String content = new String(Files.readAllBytes(Paths.get(file)));
        //String content = new String(Files.readAllBytes(Paths.get("/home/koji/lynis_reports/fedora")));

        try (BufferedReader br = new BufferedReader(new StringReader(content))) {
            String line;

            while ((line = br.readLine()) != null) {

                Matcher categoryMatcher = categoryPattern.matcher(line);
                if (categoryMatcher.matches())
                    category1 = parseCategory(categoryMatcher);

                Matcher testMatcher = testPattern.matcher(line);
                if (testMatcher.matches() && category1 != null){
                    TestReport testReport = parseTest(testMatcher, category1);
                    testReport.setReport(report);
                    report.getTests().add(testReport);
                }


                Matcher rankMatcher = rankPattern.matcher(line);
                if (rankMatcher.matches()) {
                    report.setRank(Integer.parseInt(rankMatcher.group("rank").trim()));
                    report.setSeverity(convertColorCodeToSeverity(rankMatcher.group("color")));
                }

            }

            session.save(report);
            session.getTransaction().commit();

        } catch (IOException e) {
            e.printStackTrace();
            session.getTransaction().rollback();


        } finally {

            session.close();
          //  session.disconnect();

        }
        return report;
    }

    public Object generateComparative() throws IOException {
      /*  List<Report> reports= repository.generateReport(session,"/home/koji/lynis.csv");
        return reports;
       */

        List<Report> reports= repository.generateReportText(session,"/home/koji/lynis-comparative.csv");
        return reports;
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
        testReport.setResult(matcher.group("value").trim());
        testReport.setTest(test);
        testReport.setSeverity(convertColorCodeToSeverity(matcher.group("color")));


        return testReport;
    }


    public static Severities convertColorCodeToSeverity(String color) {
        switch (color.trim()) {
            case "1;37":
                return Severities.OK;
            case "1;32":
                return Severities.LOW;
            case "1;33":
                return Severities.MEDIUM;
            case "1;31":
                return Severities.HIGH;
            default:
                return Severities.LOW;
        }
    }

    public static void createDB(){
        if (!Files.exists(Paths.get("~/lynis.h2db"))) {
            Repository create = new Repository(true);
            create.makeDatabase();
            create.shutdown();
        }
    }
    public static void importAll(String path) throws IOException {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("importing " + file.getName());
                LynisParser parser = new LynisParser();
                parser.parseLog("","",file.getAbsolutePath());


            }
        }
        System.out.println("complete!" );
    }



    public static void main(String[] args) throws IOException {
        //LynisParser.createDB
        //LynisParser.importAll("/home/koji/lynis_reports/");



      new LynisParser().generateComparative();

System.exit(0);


    }
}
