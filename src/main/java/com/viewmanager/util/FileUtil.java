package com.viewmanager.util;

import com.viewmanager.config.ViewMConfig;
import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

public class FileUtil {
    final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static FileBasedConfigurationBuilder toProperties(ListToProp propBuilder, String filePath) {
        String fileName = ViewMEnvUtil.CONFIG_LOC + filePath;
        backupExistingFile(fileName);
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = getPropertyWriter(fileName);
        Configuration config = null;
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            logger.error("Error building property writer.", e);
        }
        propBuilder.add(config);
        return builder;
    }

    private static void backupExistingFile(String filename) {
        File file = new File(filename);
        int index = 1;
        while(file.exists()) {
            String ext = filename.substring(filename.lastIndexOf('.'));
            file = new File(filename.replace(ext, index + ext));
            index++;
        }
        new File(filename).renameTo(file.getAbsoluteFile());
        FileUtil.touchFile(filename);
    }

    public static FileBasedConfigurationBuilder<FileBasedConfiguration> getPropertyWriter(String filename) {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(filename));
        return builder;
    }

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

    public interface ListToProp {
        void add(Configuration config);
    }
}
