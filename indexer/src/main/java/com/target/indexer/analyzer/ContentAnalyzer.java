package com.target.indexer.analyzer;

import java.io.IOException;
import java.util.List;

public interface ContentAnalyzer {

    public List<String> analyze(String content) throws IOException;

}
