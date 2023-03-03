package com.viewmanager.util;

import com.viewmanager.config.ViewMOrderedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public class FileUtil {
    final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void touchFile(String path) {
        Path p = Path.of(path);
        try {
            if (Files.exists(p)) {
                Files.setLastModifiedTime(p, FileTime.from(Instant.now()));
            } else {
                Files.createFile(p);
            }
        } catch (IOException e) {
            logger.error("Error attempting to touch file.",e);
        }
    }
}
