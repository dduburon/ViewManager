package com.viewmanager.pojo;

import com.viewmanager.config.ViewMConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class ViewPojo {
    String name;
    String fileName;
    Type type = Type.V;
    List<ViewPojo> dependentViews;

    public enum Type {
        V, // Normal View
        M, // Materialized View
        F; // Function
    }


    public ViewPojo(String name) {
        setName(parseViewName(name));
        setFileName(ViewMConfig.getViewsLoc() + File.separator + getName() + ViewMConfig.getViewFileSuffix());
    }

    public ViewPojo(String name, String fileName) {
        setName(parseViewName(name));
        setFileName(ViewMConfig.getViewsLoc() + File.separator + fileName);
    }

    public ViewPojo(ViewPojo vp) {
        setName(vp.getName() + "");
        setFileName(vp.getFileName() + "");
        setType(vp.getType());
        setDependentViews(vp.getDependentViews());
    }

    public String parseViewName(String name) {
        String nameNoSuffix = name.substring(2);
        if(name.startsWith("m_")) {
            setType(Type.M);
            return nameNoSuffix;
        }
        if(name.startsWith("f_")) {
            setType(Type.F);
            return nameNoSuffix;
        }
        return name;
    }

    public String getNameWithType() {
        if (getType().equals(Type.V)) {
            return getName();
        } else {
            return getType().name().toLowerCase() + "_" + getName();
        }
    }
}
