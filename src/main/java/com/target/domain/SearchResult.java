package com.target.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchResult {
    private String fileName;
    private Integer searchCount;

    public String toString() {
        return fileName + " : "  + searchCount + " matches";
    }
}
