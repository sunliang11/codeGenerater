package com.haiziwang.gen.code.infra.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-03-13 14:34
 */
public class FastGenUtils extends GenUtils {
    public static List<String> getFastBackendTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("template/backend/FastController.java.vm");
        templates.add("template/backend/FastDO.java.vm");
        templates.add("template/backend/FastMapper.java.vm");
        templates.add("template/backend/FastMapper.xml.vm");
        templates.add("template/backend/FastService.java.vm");
        templates.add("template/backend/FastVO.java.vm");
        return templates;
    }

    public static List<String> getFastFrontendTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("template/frontend/FastInfo.html.vm");
        templates.add("template/frontend/FastInfoCtrl.js.vm");
        return templates;
    }

    public static String getFastFileName(String template, String className, String packageName, String templatePackage) {
        String packagePath = "fast";

        String name = template
                .replaceAll("template/" + templatePackage + "/", "")
                .replaceAll("\\.vm", "");
        if (name.contains("Interface")) {
            name = name.replaceAll("Interface", "");
            return packagePath + File.separator + className + File.separator + "I" + className + name;
        }

        return packagePath + File.separator + className + File.separator + className + name;
    }
}
