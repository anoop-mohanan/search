package com.target.indexer.service;

import com.target.indexer.analyzer.ContentAnalyzer;
import com.target.indexer.domain.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class IndexServiceImpl implements IndexService {

    //Class level logger.
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Value("${data.directory}")
    private String dataDirectory;

    @Value("${spring.elasticsearch.rest.uris}")
    private String elasticUrl;

    @Value("${spring.elasticsearch.cat.indices.searchterms.endpoint}")
    private String searchTermsIndexUrl;

    private static final String SLASH = "/";

    @Autowired
    private ContentAnalyzer contentAnalyzer;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void index(final String folderLocation, final String index) throws IOException {
        Files.list(Paths.get(folderLocation)).filter(Files::isRegularFile).forEach(
                path -> {
                    try {
                        String contents = Files.readString(path);
                        List<String> tokens = contentAnalyzer.analyze(contents);
                        tokens.forEach(token -> {
                            postToken(token, path.toString().substring(path.toString().lastIndexOf(SLASH) + 1), index);
                        });
                    } catch (IOException ioEx) {
                        LOGGER.error("Exception occurred while reading/posting contents of file : {}", path.getFileName());
                    }
                }
        );

    }


    /**
     * This method encapsulates the logic of creating a document and posting it to elasticsearch.
     * @param token - Token
     * @param filename - Filename
     * @param index - index
     * @throws RestClientException
     */
    private void postToken(final String token, final String filename, final String index) throws RestClientException {

        Document document = new Document();
        document.setFilename(filename);
        document.setTerm(token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Document> documentHttpEntity = new HttpEntity<>(document, httpHeaders);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(elasticUrl);
        urlBuilder.append(SLASH);
        urlBuilder.append(index);
        urlBuilder.append(SLASH);
        urlBuilder.append("_doc");
        urlBuilder.append(SLASH);
        urlBuilder.append(UUID.randomUUID().toString());

        try {
            restTemplate.exchange(urlBuilder.toString(), HttpMethod.PUT, documentHttpEntity, Void.class);
            LOGGER.debug("Successfully posted document : {}", document);
        } catch (RestClientException rcEx) {
            LOGGER.error("Exception occurred while posting to elastic URL : {}, exception : {}", urlBuilder.toString(), rcEx.getMessage());
            throw rcEx;
        }
    }


    @Override
    public boolean isIndexExists(final String index) throws RestClientException {
        boolean indexExists = false;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(elasticUrl);
        urlBuilder.append(searchTermsIndexUrl);
        urlBuilder.append(SLASH);
        urlBuilder.append(index);

        try {
            String response = restTemplate.getForObject(urlBuilder.toString(), String.class);
            if (response.contains(index)) {
                indexExists = true;
            }
        } catch (RestClientException rcEx) {
            LOGGER.error("Exception occurred while checking index : {}, Exception : {}", index, rcEx.getMessage());
            throw rcEx;
        }

        return indexExists;
    }

    @Override
    public boolean deleteIndex(final String index) throws RestClientException {
        boolean indexDeleted = false;

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(elasticUrl);
        urlBuilder.append(SLASH);
        urlBuilder.append(index);

        try {
            restTemplate.delete(urlBuilder.toString());
            indexDeleted = true;
        } catch (RestClientException rcEx) {
            LOGGER.error("Exception occurred while deleting index: {}, exception: {}", index, rcEx);
            throw rcEx;
        }
        return indexDeleted;
    }

    @Override
    public boolean createIndex(final String index, final String setting) throws RestClientException {
        boolean indexCreated = false;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(elasticUrl);
        urlBuilder.append(SLASH);
        urlBuilder.append(index);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> settingHttpEntity = new HttpEntity<>(setting, httpHeaders);

        try {
            restTemplate.exchange(urlBuilder.toString(), HttpMethod.PUT, settingHttpEntity, Void.class);
            LOGGER.debug("Successfully created index : {}", index);
        } catch (RestClientException rcEx) {
            LOGGER.error("Exception occurred while creating index : {}, exception : {}", index, rcEx.getMessage());
            throw rcEx;
        }

        return indexCreated;
    }
}
