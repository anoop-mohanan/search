package com.target.searchengine.service;

import com.target.searchengine.domain.SearchResult;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface SearchProcessor {

    public List<SearchResult> search(String keyword) throws IOException;

}
