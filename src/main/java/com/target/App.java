package com.target;

import com.target.service.SearchProcessor;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

/**
 * Main App
 */
public class App {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("-------------------------------------Search---------------------------------------");
        System.out.println("Enter the search term: ");
        String keyword = scanner.nextLine();
        System.out.println("Search select method:");
        System.out.println("[1] String Match");
        System.out.println("[2] Regular Expression");
        System.out.println("[3] Indexed");
        String searchMethod = "";
        while (!searchMethod.matches("[123]")) {
            searchMethod = scanner.nextLine();
        }

        SearchProcessor processor = new SearchProcessor();
        System.out.println("Search Results : ");
        Instant begin = Instant.now();
        processor.search(keyword, searchMethod);
        Instant end = Instant.now();
        System.out.printf("Elapsed Time: %d (ms)", Duration.between(begin, end).toMillis());
    }
}
