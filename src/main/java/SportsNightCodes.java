import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SportsNightCodes {

    private static final ArrayList<String> allPossibleCodes = new ArrayList<>();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static void generateAllPossibleCodes(){
        // generate an array of all possible codes, in the format of XXX301
        // each X is a letter of the alphabet but 301 does not change
        for (int i = 0; i < ALPHABET.length(); i++) {
            for (int j = 0; j < ALPHABET.length(); j++) {
                for (int k = 0; k < ALPHABET.length(); k++) {
                    allPossibleCodes.add(ALPHABET.charAt(i) + String.valueOf(ALPHABET.charAt(j)) + ALPHABET.charAt(k) + "301");
                }
            }
        }
    }

    public static String checkPageForCode(Document page){
        // use jsoup to find the tickets
        // find the first div with class "event_tickets" then the child divs with class "event_ticket"
        // if there are 2 tickets, return true, else return false
        Element outerDiv = page.selectFirst("div.event_tickets");
        if (outerDiv == null) return null;
        Elements innerDiv = outerDiv.select("div.event_ticket");
        if (innerDiv.size() == 2) {
            // inside the first div, get the span
            // format: <span>£6.00 (Society Name)</span>
            Element span = innerDiv.get(0).selectFirst("span");
            if (span == null) return null;
            String spanText = span.text();
            // remove the £6.00 and the brackets
            return spanText.substring(7, spanText.length() - 1);
        }
        return null;
    }

    public static void processCode(String code) throws IOException, InterruptedException {
        Connection connection = Jsoup.connect("https://www.guildofstudents.com/ents/event/7668/?code=" + code);
        Response page = connection.execute();

        if (page.statusCode() != 200) {
            System.out.println("Code: " + code + " returned status code: " + page.statusCode());
            return;
        }

        Document doc = Jsoup.parse(page.body());
        String societyName = checkPageForCode(doc);

        if (societyName != null) {
            if (CodeHandler.insertCode(code, societyName)) {
                System.out.println("Inserted code: " + code + " for society: " + societyName);
            } else {
                System.out.println("Failed to insert code: " + code + " for society: " + societyName);
            }
        } else {
            System.out.println("Code: " + code + " has no tickets");
        }
    }

    public static void processAllCodes() throws IOException, InterruptedException {
        // loop over all possible codes, get the page, check if it has 2 tickets
        for (String code : allPossibleCodes) {
            processCode(code);
        }
    }

    public static void processAllCodesConcurrent() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                16,
                32,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        for (String code : allPossibleCodes) {
            executor.submit(() -> {
                try {
                    processCode(code);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();

        if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
            System.out.println("[INFO] All threads finished normally");
        } else {
            System.out.println("[WARN] Some threads did not finish and were forcibly terminated");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        boolean useConcurrent = true;

        generateAllPossibleCodes();

        // ask the user if they wish to use concurrency
        System.out.println("Do you wish to disable concurrency? (y/n)");

        Scanner input = new Scanner(System.in);
        String inputString = input.nextLine().toLowerCase();

        if (inputString.equals("y")) {
            useConcurrent = false;
        } else {
            System.out.println("Defaulting to concurrency");
        }

        if (useConcurrent) processAllCodesConcurrent();
        else processAllCodes();

        // wait for the threads to finish

        CodeHandler.sortCodes();
        CodeHandler.printSortedCodes();
        FileHandler.saveSortedCodesToJSON(CodeHandler.getSortedCodes(), "codes.json");

        long endTime = System.currentTimeMillis();

        System.out.println("[INFO] Finished in " + (endTime - startTime) + "ms");
    }
}
