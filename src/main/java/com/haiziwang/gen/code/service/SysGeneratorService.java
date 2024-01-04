package com.haiziwang.gen.code.service;

import com.haiziwang.gen.code.dao.mapper.BasicGenetatorMapper;
import com.haiziwang.gen.code.dao.mapper.OracleGeneratorMapper;
import com.haiziwang.gen.code.dao.mapper.MysqlGeneratorMapper;
import com.haiziwang.gen.code.infra.utils.constant.comm.Constant;
import com.haiziwang.gen.code.infra.utils.GenUtils;
import com.haiziwang.gen.code.service.vo.UserSettingsVO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Service
public class SysGeneratorService {
    @Value("${packageName}")
    private String packageName;
    @Value("${author}")
    private String author;
    @Value("${email}")
    private String email;
    @Value("${tablePrefix}")
    private String tablePrefix;
    @Value("${dbType}")
    private String dbType;
    @Value("${templatePackage}")
    private String templatePackage;

    private final Logger logger = LoggerFactory.getLogger(SysGeneratorService.class);

    @Autowired
    private OracleGeneratorMapper oracleGenetatorMapper;

    @Autowired
    private MysqlGeneratorMapper mysqlGenetatorMapper;

    private BasicGenetatorMapper genetatorMapper;

    @PostConstruct
    public void init(){
        switch (dbType){
            case Constant.MYSQL:
                genetatorMapper = mysqlGenetatorMapper;
                break;
            case Constant.ORACLE:
                genetatorMapper = oracleGenetatorMapper;
                break;
            default:
                genetatorMapper = mysqlGenetatorMapper;
        }
    }

    private Map<String, String> queryTable(String tableName) {
        return genetatorMapper.queryTable(tableName);
    }

    private List<Map<String, String>> queryColumns(String tableName) {
        return genetatorMapper.queryColumns(tableName);
    }

    public List<Map<String, Object>> list(Map<String, Object> map) {
        return genetatorMapper.queryList(map);
    }

    public int count(Map<String, Object> map) {
        return genetatorMapper.queryTotal(map);
    }

    public byte[] generatorCode(String[] tableNames) {
        UserSettingsVO userSettings = new UserSettingsVO(packageName, author, email, tablePrefix, dbType);
        userSettings.setTemplatePackage(templatePackage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GenUtils.generatorCode(table, columns, zip, userSettings);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}
