package com.target.searchengine.controller;

import com.target.searchengine.domain.SearchResult;
import com.target.searchengine.service.SearchService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
public class SearchController {


    @Autowired
    private SearchService searchService;

    @ApiOperation( value = "Search Term by Search Methods",notes = "Enter the search term and select the search method. 1) String Match 2) Regular Expression 3) Indexed"
    , response = SearchResult.class, responseContainer = "List")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<SearchResult> search(@RequestParam @NotNull String searchTerm, @RequestParam @Min(1) @Max(3) Integer searchMethod) throws IOException {
        return searchService.search(searchTerm, searchMethod);
    }

}
