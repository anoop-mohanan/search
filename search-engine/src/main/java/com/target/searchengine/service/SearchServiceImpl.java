package com.target.searchengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.target.searchengine.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SearchServiceImpl implements SearchService {

    //Class level logger.
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    private static final String SLASH = "/";

    private static final String SEARCH_OPERATOR = "_search";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${data.directory}")
    private String dataDirectory;

    @Value("${spring.elasticsearch.rest.uris}")
    private String elasticUrl;

    @Value("${spring.elasticsearch.index}")
    private String indexName;

    @Autowired
    private String searchQuery;

    @Override
    public List<SearchResult> search(String searchTerm, Integer searchMethod) throws IOException {
        List<SearchResult> searchResults = new ArrayList<>();
        switch (searchMethod) {
            case 1: searchResults = stringSearchService.search(searchTerm);
                    break;
            case 2: searchResults = stringRegexService.search(searchTerm);
                    break;
            case 3: searchResults = stringIndexService.search(searchTerm);
                    break;
        }
        return searchResults;
    }

    /**
     * This class encapsulates the logic for searching by text.
     */
    public SearchProcessor stringSearchService = (searchTerm) -> {
        final List<SearchResult> results = new ArrayList<>();
        Files.list(Paths.get(dataDirectory)).filter(Files::isRegularFile).forEach(
                path -> {
                    try {
                        String content = Files.readString(path);
                        String[] words = content.replaceAll("[^a-zA-Z0-9 \\n]", "").toLowerCase().split("\\s+");
                        int count = 0;
                        for (String word : words) {
                            if (searchTerm.equals(word)) {
                                count++;
                            }
                        }
                        results.add(new SearchResult(path.toString().substring(path.toString().lastIndexOf(SLASH) + 1), count));
                    } catch(IOException ioEx) {
                        LOGGER.error("Exception occurred while reading file : {}", path.getFileName());
                    }
                }
        );
        return results;
    };

    /**
     * This class encapsulates the logic for searching by regular expression.
     */
    public SearchProcessor stringRegexService = (searchTerm) -> {
        final List<SearchResult> results = new ArrayList<>();
        Files.list(Paths.get(dataDirectory)).filter(Files::isRegularFile).forEach(
            path -> {
                try {
                    String content = Files.readString(path);
                    content = content.replaceAll("[^a-zA-Z0-9 \\n]", "").toLowerCase();
                    Pattern pattern = Pattern.compile("\\b("+searchTerm+")\\b");
                    int count = 0;
                    Matcher matcher = pattern.matcher(content);
                    while (matcher.find())
                        count++;
                    results.add(new SearchResult(path.toString().substring(path.toString().lastIndexOf(SLASH) + 1), count));
                } catch(IOException ioEx) {
                    LOGGER.error("Exception occurred while reading file : {}", path.getFileName());
                }
            });
        return results;
    };

    /**
     * This class encapsulates the logic for searchin by Index.
     */
    public SearchProcessor stringIndexService = (searchTerm) -> {
        List<SearchResult> results = new ArrayList<>();
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(elasticUrl);
            urlBuilder.append(SLASH);
            urlBuilder.append(indexName);
            urlBuilder.append(SLASH);
            urlBuilder.append(SEARCH_OPERATOR);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String query = String.format(searchQuery, searchTerm);
            HttpEntity<String> queryHttpEntity = new HttpEntity<>(query, httpHeaders);

            ResponseEntity<Wrapper> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, queryHttpEntity, Wrapper.class);
            Wrapper wrapper = response.getBody();
            if(wrapper != null && wrapper.getAggregations() != null && wrapper.getAggregations().getGroupByTerm() != null && wrapper.getAggregations().getGroupByTerm().getBuckets() != null) {
                wrapper.getAggregations().getGroupByTerm().getBuckets().forEach(bucket -> {
                    SearchResult searchResult = new SearchResult(bucket.getKey(), Integer.valueOf(bucket.getDocCount()));
                    results.add(searchResult);
                });
            }
        }catch(RestClientException ex) {
            LOGGER.error("Exception occurred while querying term");
        }

        return results;
    };


    /*public static void main(String [] args) throws Exception {
        String str = "{\"took\":20,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":99,\"relation\":\"eq\"},\"max_score\":2.441254,\"hits\":[{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"020ca79b-8445-4b85-bc04-e2c6c339845c\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"0f10bd07-4f63-4b99-ad64-e2051be4f725\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"60b17957-c716-457e-8bcb-5a4f795b85f0\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"2a1f2977-1743-4b65-bf87-a216ccb5e22d\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"4e75fa21-eab2-456a-8bb1-254524f3b571\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"4df907e9-9559-4118-ac6b-0a3b5a5e4e02\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"ac866366-964f-414e-8fab-7b514901434e\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"bc2bff4c-9199-4ff5-9da8-a34252cc4e47\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"46ae3b82-3ca2-4bc5-bff0-00f2c32522ce\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}},{\"_index\":\"search_terms\",\"_type\":\"_doc\",\"_id\":\"9ed33a7b-7d55-45d0-9b65-e755f4922d9e\",\"_score\":2.441254,\"_source\":{\"term\":\"the\",\"filename\":\"french_armed_forces.txt\"}}]},\"aggregations\":{\"group_by_term\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"french_armed_forces.txt\",\"doc_count\":64},{\"key\":\"hitchhikers.txt\",\"doc_count\":29},{\"key\":\"warp_drive.txt\",\"doc_count\":6}]}}}";
        ObjectMapper objectMapper = new ObjectMapper();
        Aggregations aggregations = new Aggregations();

        Bucket bucket1 = new Bucket();
        bucket1.setDocCount("10");
        bucket1.setKey("tests.txt");

        Bucket bucket2 = new Bucket();
        bucket1.setDocCount("12");
        bucket1.setKey("tts.txt");

        Bucket bucket3 = new Bucket();
        bucket1.setDocCount("30");
        bucket1.setKey("te.txt");

        List<Bucket> buckets = new ArrayList<>();
        buckets.add(bucket1);
        buckets.add(bucket2);
        buckets.add(bucket3);

        GroupByTerm groupByTerm = new GroupByTerm();
        groupByTerm.setBuckets(buckets);

        aggregations.setGroupByTerm(groupByTerm);

        Wrapper wrapper = new Wrapper();
        wrapper.setAggregations(aggregations);

        Wrapper testWrapper = new Wrapper();
        testWrapper = objectMapper.readValue(str, Wrapper.class);
        System.out.println(testWrapper);
    }*/

}
