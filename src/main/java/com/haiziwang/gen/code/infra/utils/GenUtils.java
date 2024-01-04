package com.haiziwang.gen.code.infra.utils;

import com.haiziwang.gen.code.dao.entity.ColumnEntity;
import com.haiziwang.gen.code.dao.entity.TableEntity;
import com.haiziwang.gen.code.infra.utils.constant.OracleNumberTranstion.OracleNumberTranstion;
import com.haiziwang.gen.code.infra.utils.constant.comm.Constant;
import com.haiziwang.gen.code.service.vo.UserSettingsVO;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author chenjunfei
 * @email chenjunfei@youbangsoft.com
 * @date 2018年03月13日
 */
public class GenUtils {

    //todo 采用模板方法模式

    /**
     * 获取模板
     *
     * @return
     */
    private static List<String> getTemplates(String templatePackage) {
        List<String> templates = new ArrayList<String>();
        String templateDir = "template/";
        String templateFontEndDir = "template/font-end/";
        if (StringUtils.isNotEmpty(templatePackage)) {
            templateDir = "template/" + templatePackage + "/";
            templateFontEndDir = "template/" + templatePackage + "/font-end/";
        }
        if (templatePackage.equals("suning/pop")) {
            templates.add(templateDir + "Controller.java.vm");
            templates.add(templateDir + "Entity.java.vm");
            templates.add(templateDir + "Dao.java.vm");
            templates.add(templateDir + "SnMapper.xml.vm");
            templates.add(templateDir + "Service.java.vm");
            templates.add(templateDir + "VO.java.vm");

            templates.add(templateFontEndDir + "index.ftl.vm");
            templates.add(templateFontEndDir + "list.ftl.vm");
            templates.add(templateFontEndDir + "table.ftl.vm");
            templates.add(templateFontEndDir + "edit.ftl.vm");
            templates.add(templateFontEndDir + "item.ftl.vm");
        } else if(templatePackage.equals("haiziwang/perm")){
            templates.add(templateDir + "Controller.java.vm");
            templates.add(templateDir + "DBO.java.vm");
            templates.add(templateDir + "Mapper.java.vm");
            templates.add(templateDir + "Mapper.xml.vm");
            templates.add(templateDir + "ServiceImpl.java.vm");
            templates.add(templateDir + "ServiceInterface.java.vm");
            templates.add(templateDir + "VO.java.vm");
            templates.add(templateDir + "QueryBean.java.vm");
        }
        else if(templatePackage.equals("chuangjiyun/ims")){
            templates.add(templateDir + "Controller.java.vm");
            templates.add(templateDir + "DBO.java.vm");
            templates.add(templateDir + "Mapper.java.vm");
            templates.add(templateDir + "Mapper.xml.vm");
            templates.add(templateDir + "ServiceImpl.java.vm");
            templates.add(templateDir + "ServiceInterface.java.vm");
            templates.add(templateDir + "VO.java.vm");
            templates.add(templateDir + "QueryBean.java.vm");
        }
        else {
            templates.add(templateDir + "Controller.java.vm");
            templates.add(templateDir + "DO.java.vm");
            templates.add(templateDir + "Mapper.java.vm");
            templates.add(templateDir + "Mapper.xml.vm");
            templates.add(templateDir + "Service.java.vm");
            templates.add(templateDir + "VO.java.vm");
        }
        return templates;
    }

    /**
     * 获取表实体数据
     *
     * @param table
     * @param columns
     * @param config
     * @param refHasBigDecimal
     * @return
     */
    private static TableEntity getTableEntity(Map<String, String> table,
                                              List<Map<String, String>> columns,
                                              Configuration config,
                                              Ref<Boolean> refHasBigDecimal,
                                              UserSettingsVO userSettings) {

        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), userSettings.getTablePrefix());
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        addColums(columns, tableEntity, config, refHasBigDecimal, userSettings);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        return tableEntity;
    }

    private static String getDataType(Map<String, String> column) {
        String originalDataType = column.get("dataType");
        if (!Constant.OORACLE_NUMBER_TYPE.equals(originalDataType)) {
            return originalDataType;
        }
        String numberLenStr = column.get("numberLen");
        String numberScaleStr = column.get("numberScale");
        if (StringUtils.isNotBlank(numberScaleStr) && !numberScaleStr.equals("0")) {
            return OracleNumberTranstion.NUMBER_DECIMAL;
        }
        if (StringUtils.isBlank(numberLenStr)) {
            return OracleNumberTranstion.NUMBER_DECIMAL;
        }
        Integer numberLen = Integer.parseInt(numberLenStr);
        if (numberLen == 1) {
            return OracleNumberTranstion.NUMBER_BIT;
        } else if (numberLen == 2) {
            return OracleNumberTranstion.NUMBER_BYTE;
        } else if (numberLen == 3 || numberLen == 4) {
            return OracleNumberTranstion.NUMBER_SHORT;
        } else if (numberLen >= 5 && numberLen <= 9) {
            return OracleNumberTranstion.NUMBER_INT;
        } else if (numberLen >= 10 && numberLen <= 18) {
            return OracleNumberTranstion.NUMBER_LONG;
        } else if (numberLen >= 19 && numberLen <= 38) {
            return OracleNumberTranstion.NUMBER_DECIMAL;
        } else {
            return OracleNumberTranstion.NUMBER_DECIMAL;
        }
    }

    private static void addColums(List<Map<String, String>> columns,
                                  TableEntity tableEntity,
                                  Configuration config,
                                  Ref<Boolean> refHasBigDecimal,
                                  UserSettingsVO userSettings) {
        Boolean hasBigDecimal = refHasBigDecimal.getT();
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            String dataType = getDataType(column);
            columnEntity.setDataType(dataType);
            columnEntity.setComments(column.get("columnComment") != null ? column.get("columnComment") : "");
            columnEntity.setExtra(column.get("extra"));
            //列名转换成Java属性名 order_dtl --> orderDtl
            String attrName = columnToJava(columnEntity.getColumnName());
            if (userSettings.getTemplatePackage().equals("suning/pop")) {
                attrName = WordUtils.capitalize(columnEntity.getColumnName());
            }
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                hasBigDecimal = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }
            String jdbcType = config.getString(columnEntity.getDataType() + "-jdbc", "unknowType");
            columnEntity.setJdbcType(jdbcType);

            columsList.add(columnEntity);
        }
        refHasBigDecimal.setT(hasBigDecimal);
        tableEntity.setColumns(columsList);
    }

    /**
     * 列名转换成Java属性名
     */
    //todo 后续采用模板方法模式
    private static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    private static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
            tableName = tableName.replace(tablePrefix, "");
        }
        String upperTablePrefix = tablePrefix.toUpperCase();
        if (tableName.startsWith(upperTablePrefix)) {
            tableName = tableName.replace(upperTablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取数据库与java的映射表
     */
    private static Configuration getConfig(String dbType) {
        try {
            if (Constant.ORACLE.equals(dbType)) {
                return new PropertiesConfiguration("oracleMapping.properties");
            }
            return new PropertiesConfiguration("mysqlMapping.properties");
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    private static String getFileName(String template, String className, String packageName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName + File.separator;
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "web" + File.separator + className + "Controller.java";
        }
        if (template.contains("DO.java.vm")) {
            return packagePath + "dao" + File.separator + "entity" + File.separator + className + "DO.java";
        }
        if (template.contains("Entity.java.vm")) {
            return packagePath + "dao" + File.separator + "entity" + File.separator + className + "Entity.java";
        }
        if (template.contains("Mapper.java.vm")) {
            return packagePath + "dao" + File.separator + "mapper" + File.separator + className + "Mapper.java";
        }
        if (template.contains("Dao.java.vm")) {
            return packagePath + "dao" + File.separator + "mapper" + File.separator + className + "Dao.java";
        }
        if (template.contains("SnMapper.xml.vm")) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + "sqlMap_" + className + ".xml";
        }
        if (template.contains("Mapper.xml.vm")) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml";
        }
        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }
        if (template.contains("VO.java.vm")) {
            return packagePath + "service" + File.separator + "vo" + File.separator + className + "VO.java";
        }
        if (template.contains("DBO.java.vm")) {
            return packagePath + "dao" + File.separator + "entity" + File.separator + className + "DBO.java";
        }
        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + "I" + className + "Service.java";
        }
        String ftlPath = "main" + File.separator
                + "webapp" + File.separator
                + "WEB-INF" + File.separator
                + "freemarker/" + File.separator
                + "views/" + File.separator;
        if (template.contains("index.ftl.vm")) {
            return ftlPath + className + "Index.ftl";
        }
        if (template.contains("list.ftl.vm")) {
            return ftlPath + className + "List.ftl";
        }
        if (template.contains("table.ftl.vm")) {
            return ftlPath + className + "Table.ftl";
        }
        if (template.contains("edit.ftl.vm")) {
            return ftlPath + className + "Edit.ftl";
        }
        if (template.contains("item.ftl.vm")) {
            return ftlPath + className + "Item.ftl";
        }
        return null;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(Map<String, String> table,
                                     List<Map<String, String>> columns,
                                     ZipOutputStream zip,
                                     UserSettingsVO userSettings) {
        //配置信息
        Configuration config = getConfig(userSettings.getDbType());

        //获取表实体
        Ref<Boolean> refHasBigDecimal = new Ref<Boolean>(false);
        TableEntity tableEntity = getTableEntity(table, columns, config, refHasBigDecimal, userSettings);
        boolean hasBigDecimal = refHasBigDecimal.getT();

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);

        map.put("package", userSettings.getPackageName());
        map.put("author", userSettings.getAuthor());
        map.put("email", userSettings.getEmail());
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates(userSettings.getTemplatePackage());

        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
//                String fileName = getFileName(template, tableEntity.getClassName(), userSettings.getPackageName());
                String fileName = getFileName(template, tableEntity.getClassName(), userSettings.getPackageName());

                fileName = null;
                if (fileName == null) {
                    fileName = FastGenUtils.getFastFileName(template, tableEntity.getClassName(), userSettings.getPackageName(), userSettings.getTemplatePackage());
                }
                //添加到zip
                zip.putNextEntry(new ZipEntry(fileName));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RRException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }

}

