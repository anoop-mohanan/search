package com.target.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

public class FileUtils {


    /**
     * This method will return the files.
     * @return
     */
    public static Resource[] getFilesAsResource() {
        Resource[] resources = null;
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            resources = resolver.getResources("file:*.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resources;
    }


}
