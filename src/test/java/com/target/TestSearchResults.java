package com.target;

import com.target.domain.SearchResult;
import org.junit.Assert;
import org.junit.Test;

public class TestSearchResults {
    @Test
    public void testSearchResult() throws Exception {
        SearchResult actualSearchResult = new SearchResult();
        actualSearchResult.setFileName("Test.txt");
        actualSearchResult.setSearchCount(1);

        SearchResult expectedSearchResult = new SearchResult("Test.txt", 1);
        Assert.assertEquals(actualSearchResult.getFileName(), expectedSearchResult.getFileName());
        Assert.assertEquals(actualSearchResult.getSearchCount(), expectedSearchResult.getSearchCount());

    }
}
