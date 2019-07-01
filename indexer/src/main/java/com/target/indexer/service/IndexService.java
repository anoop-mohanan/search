package com.target.indexer.service;

import org.springframework.web.client.RestClientException;

import java.io.IOException;

/**
 * This interface defines the contract for the indexing service.
 */
public interface IndexService {

    /**
     * This method will index each term in the file into elastic search.
     * @param folderLocations - Location of the folder from where the files will be picked up.
     * @param index - Name of the elastic search index.
     * @throws IOException
     */
    public void index(String folderLocations, String index) throws IOException;

    /**
     * This method will check if the index is present in elastic search.
     * @param index - index to the chcked.
     * @return - true if present, false otherwise.
     */
    public boolean isIndexExists(String index) throws RestClientException;

    /**
     * This method will delete the specified index.
     * @param index - index to be deleted
     * @return - true, if deleted, false otherwise.
     */
    public boolean deleteIndex(String index) throws RestClientException;

    /**
     * This method will create an index with the specified setting,
     * @param index - Index name
     * @return - true if created, false otherwise.
     */
    public boolean createIndex(String index, String setting) throws RestClientException;
}
