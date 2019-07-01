package com.target.searchengine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SearchResult {

    private String fileName;
    private Integer count;

    public String toString() {
        return fileName + " : "  + count + " matches";
    }
}
