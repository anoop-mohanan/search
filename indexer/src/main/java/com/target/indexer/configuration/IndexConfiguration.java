package com.target.indexer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndexConfiguration {

    @Bean
    public String indexSettings() {
        return "{\"settings\" : {\"number_of_shards\" : 1},\"mappings\" : { \"properties\" : {\"keyword\" : { \"type\" : \"text\" }, \"filename\" : {\"type\" : \"text\", \"fielddata\" : true} } } }";
    }
}
