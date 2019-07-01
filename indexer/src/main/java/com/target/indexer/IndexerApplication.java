package com.target.indexer;

import com.target.indexer.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
public class IndexerApplication {

    //Class level logger.
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerApplication.class);

    @Autowired
    private IndexService indexService;

    @Value("${spring.elasticsearch.index}")
    private String indexName;

    @Value("${data.directory}")
    private String dataDir;

    @Autowired
    private String indexSettings;

    private static IndexService staticIndexService;
    private static String staticIndexName;
    private static String staticDataDir;
    private static String staticSettings;

    @PostConstruct
    public void init() {
        IndexerApplication.staticIndexService = indexService;
        IndexerApplication.staticIndexName = indexName;
        IndexerApplication.staticDataDir = dataDir;
        IndexerApplication.staticSettings = indexSettings;
    }

    /**
     * Main method executing the logic.
     * a. Check if the index exists.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, RestClientException {
        SpringApplication springApplication = new SpringApplication();
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(IndexerApplication.class, args);


        try {
            if (staticIndexService.isIndexExists(staticIndexName)) {
                staticIndexService.deleteIndex(staticIndexName);
            }
        } catch (RestClientException rcEx) {
            LOGGER.error("Exception occurred while checking index : {}, exception : {}", staticIndexName, rcEx);
        }
        staticIndexService.createIndex(staticIndexName, staticSettings);

        staticIndexService.index(staticDataDir, staticIndexName);

        LOGGER.info("Indexing of files complete.");
        SpringApplication.exit(context, () -> 0);
    }

}
