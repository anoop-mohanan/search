package com.target.indexer.analyzer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Component
public class WordAnalyzer implements ContentAnalyzer {

    private Analyzer analyzer;

    @Override
    public List<String> analyze(String content) throws IOException {
        List<String> tokens = new ArrayList<>();
        TokenStream tokenStream = this.analyzer.tokenStream("Content", content);
        CharTermAttribute attribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            tokens.add(attribute.toString());
        }
        tokenStream.close();
        return tokens;
    }
}
