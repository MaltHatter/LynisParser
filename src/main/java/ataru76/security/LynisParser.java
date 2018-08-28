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
  //  private static Pattern testPattern = Pattern.compile("-(?<desc>[\\w\\s]+)\\[(?<color>[\\w\\s]+)#(?<status>[\\w\\s]+)\\]");
    //private static Pattern testPattern =Pattern.compile("\\[\\d*\\w*-\\s*(?<desc>(\\w|\\s)*)\u001B\\[41C \\[ \u001B\\[\\d*;(?<color>\\d*)\\w\\s*(?<status>\\w+)\u001B\\[0m \\]");
    private static Pattern testPattern =Pattern.compile("\u001B\\[(\\d|\\w|\\s)*-\\s* ((?<desc>.*)?\u001B)\\[\\d*\\w*\\s*\\[\\s*\u001B(\\[(?<color>\\d*;\\d*)m(?<value>.*)?\u001B\\[0).*");




  //  private static Pattern categoryPattern = Pattern.compile("\\[(\\+)\\](?<desc>[\\w\\s]+)");
    private static Pattern rankPattern = Pattern.compile("Hardening\\s*index\\s*:\\s*(?<rank>\\d*)\\s*\\[[#\\s]*\\]");


    private static Pattern categoryPattern =Pattern.compile("\\[(\\+)\\]\\s*\u001B\\[(\\d*;)?\\d*m(?<desc>[\\w\\s]+)\u001B\\[0m");
//Pattern.compile("\\[(\\+)\\]\\s*\u001B\\[(\\d*;)?\\d*m(?<desc>[\\w\\s]+)\u001B\\[0m").matcher(line).matches()


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
        String content = new String(Files.readAllBytes(Paths.get("/home/koji/lynis_reports/fedora")));

    /*   //fix status color
        content = content.replaceAll("^(?!\\[+\\])\u001B\\[1;37m", "white#").replaceAll("\u001B\\[1;32m", "green#").replaceAll("\u001B\\[1;33m", "yellow#").replaceAll("\u001B\\[1;31m", "red#");
        //remove others codes
        content = content.replaceAll("\u001B\\[(\\d*;)?\\d*\\w", "");

*/



        try (BufferedReader br = new BufferedReader(new StringReader(content))) {
            String line;

            while ((line = br.readLine()) != null) {
                //line = line.trim();
                //fix status color
/*
                content = content.replaceAll("^(?!\\[+\\])\u001B\\[1;37m", "white#").replaceAll("\u001B\\[1;32m", "green#").replaceAll("\u001B\\[1;33m", "yellow#").replaceAll("\u001B\\[1;31m", "red#");
                //remove others codes
                content = content.replaceAll("\u001B\\[(\\d*;)?\\d*\\w", "");
*/

                Matcher testMatcher = testPattern.matcher(line);
                if (testMatcher.matches() ) {
                    String d= testMatcher.group("desc");
                    String v= testMatcher.group("value");
                    String c= testMatcher.group("color");
                    System.out.println(String.format("importing %s => %s  %s" ,d,v,c));


                }




               /* TestReport test = null;
                Matcher categoryMatcher = categoryPattern.matcher(line);
                if (categoryMatcher.matches())
                    category1 = parseCategory(categoryMatcher);

                Matcher testMatcher = testPattern.matcher(line);
                if (testMatcher.matches() && category1 != null) {
                    test = parseTest(testMatcher, category1);

                    report.getTests().add(test);
                }

                Matcher rankMatcher = rankPattern.matcher(line);
                if (rankMatcher.matches())
                    report.setRank(Integer.parseInt(rankMatcher.group("rank").trim()));*/

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


        switch(matcher.group("color").trim()){
            case "37":testReport.setSeverity(0);break;
            case "32":testReport.setSeverity(1);break;
            case "33":testReport.setSeverity(2);break;
            case "31":testReport.setSeverity(3);break;
            default:
            testReport.setSeverity(1);
        }
        return testReport;
    }

    /*private String cleanText(String text){
     */

    /**
     * [1;37m bianco
     * [1;32m verde
     * [1;33m giallo
     * [1;31m rosso
     *//*
    /


}
    */


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
        File file = new File("/home/koji/lynis_reports/fedora");
        LynisParser parser = new LynisParser();
        parser.parseLog("", "", file.getAbsolutePath());


     /*   Repository repository = new Repository(false);

        repository.generateReport(repository.getSession());
*/


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
