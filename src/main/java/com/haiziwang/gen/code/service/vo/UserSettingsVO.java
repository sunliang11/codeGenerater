package com.haiziwang.gen.code.service.vo;


/**
 * Created by root on 2018-03-13.
 */
public class UserSettingsVO {
    private String packageName;
    private String author;
    private String email;
    private String tablePrefix;
    private String dbType;
    private String templatePackage;

    public UserSettingsVO(String packageName, 
                          String author,
                          String email, 
                          String tablePrefix,
                          String dbType) {
        this.packageName = packageName;
        this.author = author;
        this.email = email;
        this.tablePrefix = tablePrefix;
        this.dbType = dbType;
    }

    public String getTemplatePackage() {
        return templatePackage;
    }

    public void setTemplatePackage(String templatePackage) {
        this.templatePackage = templatePackage;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }
}
