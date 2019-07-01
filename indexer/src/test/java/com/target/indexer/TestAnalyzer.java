package com.target.indexer;

import com.target.indexer.analyzer.ContentAnalyzer;
import com.target.indexer.analyzer.WordAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class TestAnalyzer {


    private ContentAnalyzer contentAnalyzer;

    private Analyzer analyzer;

    @Before
    public void setup() {
        analyzer = new StandardAnalyzer();
        contentAnalyzer = new WordAnalyzer(analyzer);
    }

    @Test
    public void testContentAnalyzer() throws Exception {
        List<String> tokenList = this.contentAnalyzer.analyze("This is a test string.");
        List<String> expectedTokensList = new ArrayList<>();
        expectedTokensList.add("string");
        expectedTokensList.add("this");
        Assert.assertTrue(tokenList.containsAll(expectedTokensList));

    }
}
