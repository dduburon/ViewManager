package pojo;

import config.ViewMConfig;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.View;
import java.io.File;

@Getter
@Setter
public class ViewPojo {
    String name;
    String fileName;

    public ViewPojo(String name) {
        setName(name);
        setFileName(ViewMConfig.getViewsLoc() + File.separator + name + ViewMConfig.getViewFileSuffix());
    }

    public ViewPojo(String name, String fileName) {
        setName(name);
        setFileName(ViewMConfig.getViewsLoc() + File.separator + fileName);
    }

    public ViewPojo(ViewPojo vp) {
        setName(vp.getName() + "");
        setFileName(vp.getFileName() + "");
    }
}
