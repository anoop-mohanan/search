package com.target.util;

import java.util.Map;

@FunctionalInterface
public interface Preprocessor {
    public Map<String, Map<String, Integer>> preprocess();
}
