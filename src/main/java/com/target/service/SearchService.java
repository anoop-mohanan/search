package com.target.service;

import org.springframework.core.io.Resource;

import java.io.IOException;

@FunctionalInterface
public interface SearchService {

    public int search(String searchMethod, Resource resource) throws IOException;

}
