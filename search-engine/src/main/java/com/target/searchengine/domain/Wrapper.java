package com.target.searchengine.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@JsonRootName("")
@Data
@JsonIgnoreProperties(ignoreUnknown =  true)
public class Wrapper {
    @JsonProperty("aggregations")
    Aggregations aggregations;
}
