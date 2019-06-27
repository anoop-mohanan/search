package com.target;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestFiles {
    @Test
    public void testFilePresent() {
        File wrapDriveFile = new File(TestFiles.class.getClassLoader().getResource("files/warp_drive.txt").getFile());
        File hitchhikersFile = new File(TestFiles.class.getClassLoader().getResource("files/hitchhikers.txt").getFile());
        File frenchArmedForcesFile = new File(TestFiles.class.getClassLoader().getResource("files/french_armed_forces.txt").getFile());


        Assert.assertTrue(wrapDriveFile.exists());
        Assert.assertTrue(hitchhikersFile.exists());
        Assert.assertTrue(frenchArmedForcesFile.exists());
    }
}
