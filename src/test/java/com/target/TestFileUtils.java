package com.target;

import com.target.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestFileUtils {

    @Test
    public void testFileList() {
        Assert.assertTrue(Integer.valueOf(3).equals((FileUtils.getFilesAsResource()).length));
    }
}
