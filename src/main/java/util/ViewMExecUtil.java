package util;

import config.ViewMConfig;
import config.ViewMOrderedList;
import org.apache.log4j.Logger;
import pojo.ViewPojo;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static util.ViewMVerifier.verifyConfig;

public class ViewMExecUtil {

    final static Logger logger = Logger.getLogger(ViewMExecUtil.class);

    public static void verify() {
        verifyConfig();
    }
}
