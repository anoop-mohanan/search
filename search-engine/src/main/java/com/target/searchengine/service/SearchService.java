package com.target.searchengine.service;

import com.target.searchengine.domain.SearchResult;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    public List<SearchResult> search(String searchTerm, Integer searchMethod) throws IOException;
}
