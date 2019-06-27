package com.target.service;

import com.target.domain.SearchResult;
import com.target.util.FileUtils;
import com.target.util.Preprocessor;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class encapsulates the logic of searching the keyword in files.
 */
public class SearchProcessor {

    private final static Resource[] resources = FileUtils.getFilesAsResource();


    final static Preprocessor resourcePreprocessor = () -> {
        return Stream.of(resources).collect(Collectors.toMap(r -> r.getFilename(), r -> {
            Pattern pattern = Pattern.compile("\\w+");
            Matcher matcher = null;
            String matchedString = null;
            Map<String, Integer> map = new HashMap<>();
            try {
                String content = readFromInputStream(r.getInputStream());
                matcher = pattern.matcher(content);
                while (matcher.find()) {
                    matchedString = matcher.group();
                    if (map.get(matchedString) == null) {
                        map.put(matchedString, 1);
                    } else {
                        map.put(matchedString, map.get(matchedString) + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }));
    };

    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


    final static Map<String, Map<String, Integer>> indexedTerms = resourcePreprocessor.preprocess();

    /**
     * This class encapsulates the logic for searching by text.
     */
    public SearchService stringSearchService = (keyword, resource) -> {
        String content = readFromInputStream(resource.getInputStream());
        String[] words = content.split(" ");
        int count = 0;
        for (String word : words) {
            if (keyword.equals(word)) {
                count++;
            }
        }
        return count;
    };

    /**
     * This class encapsulates the logic for searching by regular expression.
     */
    public SearchService stringRegexService = (keyword, resource) -> {
        String content = readFromInputStream(resource.getInputStream());
        Pattern pattern = Pattern.compile(" " + keyword + " ");

        int sum = 0;
        Matcher matcher = pattern.matcher(content);
        while(matcher.find())
            sum++;
        return sum;
    };

    /**
     * This class encapsulates the logic for searchin by Index.
     */
    public SearchService stringIndexService = (keyword, resource) -> {
        return indexedTerms.get(resource.getFilename()).get(keyword);
    };

    public BiConsumer<String, SearchService> process = (keyword, service) -> Optional.ofNullable(getSearchResults(keyword, service)).map(Collection::stream).orElseGet(Stream::empty).sorted(Comparator.comparing(SearchResult::getSearchCount).reversed()).forEach(System.out::println);


    /**
     * This method calls the appropriate search method and prints the outcome.
     * @param keyword - Search Keyword
     * @param searchMethod - Search Method
     */
        public void search(String keyword, String searchMethod) {

        switch (searchMethod) {
            case "1":
                process.accept(keyword, stringSearchService);
                break;
            case "2":
                process.accept(keyword, stringRegexService);
                break;
            case "3":
                process.accept(keyword, stringIndexService);
                break;
            default:
                System.out.println("Invalid search method");
        }
    }

    /**
     * This method returns the count of word.
     *
     * @param searchTerm
     * @param service
     * @return
     */
   private  List<SearchResult> getSearchResults(String searchTerm, SearchService service) {
        return Arrays.asList(resources).stream().map(resource ->
        {
            int sum = 0;
            try {
                sum = service.search(searchTerm, resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new SearchResult(resource.getFilename(), sum);
        }).collect(Collectors.toList());
    }

}
