package ataru76.security;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LynisParser {
    private static Pattern testPattern = Pattern.compile("-(?<desc>[\\w\\s]+)\\[(?<status>[\\w\\s]+)\\]");
    private static Pattern categoryPattern = Pattern.compile("\\[(\\+)\\](?<desc>[\\w\\s]+)");


    public void parseLog(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String category=  parseCategory(sCurrentLine);
                if(category!=null)
                    System.out.println(String.format("-------- %s --------", category));

                Matcher matcher = testPattern.matcher(sCurrentLine);

                if (matcher.matches()) {
                    String desc = matcher.group("desc").trim();
                    String status = matcher.group("status").trim();
                    desc = desc.replaceFirst("(Checking(\\sfor)?(\\sa\\s)?|Detecting|Searching(\\sfor)?(\\sa\\s)?|Installed|presence)\\s*", "");
                    System.out.println(String.format("trovato %s -> %s", desc, status));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String parseCategory(String value) {
        Matcher matcher = categoryPattern.matcher(value);
        if (!matcher.matches())
            return null;

        return matcher.group("desc").trim();
    }


    public static void main(String[] args) {
        LynisParser parser = new LynisParser();
        parser.parseLog("/home/koji/test");

    }
}
