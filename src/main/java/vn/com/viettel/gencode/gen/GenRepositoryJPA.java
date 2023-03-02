package vn.com.viettel.gencode.gen;

import org.apache.log4j.Logger;
import vn.com.viettel.gencode.dao.CommonDataBaseDao;
import vn.com.viettel.gencode.entities.ObjectEntity;
import vn.com.viettel.gencode.entities.VariableEntity;
import vn.com.viettel.gencode.utils.Constants;
import vn.com.viettel.gencode.utils.FunctionCommon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vn.com.viettel.gencode.gen.GenEntity.getListPrivateKey;

/**
 * Gen class RepositoryJPA
 */
public class GenRepositoryJPA {

    private static final Logger LOGGER = Logger.getLogger(GenRepositoryJPA.class);

    private static Integer numberOfTable = 0;

    public static void writeClassRepositoryJPA(ObjectEntity itemObject) {

        //thuc hien gen class
        if (itemObject != null) {
            //thuc hien kiem tra trong cau sql lay ra danh sach bang
            List<String> listTableName = FunctionCommon.getListTableFromSql(itemObject);
            if (listTableName == null || listTableName.size() == 0) {
                return;
            } else {
                numberOfTable = listTableName.size();
            }
            for (String stringTableName : listTableName) {
                List<String> listPr = getListPrivateKey(stringTableName);
                if (listPr == null || listPr.size() == 0) {
                    continue;
                }
                generateFileClassRepositoryJPA(stringTableName, itemObject);
            }

        }
    }

    public static void generateFileClassRepositoryJPA(String stringTableName, ObjectEntity itemObject) {
        try {
            //thuc hien gen class
            if (stringTableName != null) {
                String stringTableNameStr = Character.toUpperCase(stringTableName.charAt(0)) + FunctionCommon.camelcasify(stringTableName.substring(1));
                String strClassRepositoryJPA = stringTableNameStr + "RepositoryJPA";
                String pathRepositoryJPA = new StringBuilder().
                        append("src/main/java").
                        append(Constants.PACKAGE_NAME_PATH).
                        append("repositories").
                        append("/").
                        append("jpa").
                        append("/").
                        append(strClassRepositoryJPA).
                        append(".java").toString();

                File file = new File(pathRepositoryJPA);
                if (file.exists()) {
                    return;
                } else {
                    file.getParentFile().mkdirs();
                    FileWriter fileWriterAction = new FileWriter(pathRepositoryJPA);
                    try (PrintWriter printWriteAction = new PrintWriter(fileWriterAction)) {
                        StringBuilder strContentCodeAction = generateClassRepositoryJPA(stringTableName, itemObject);
                        printWriteAction.print(strContentCodeAction);
                    }
                }
                System.out.println("Generate File RepositoryJPA = " + strClassRepositoryJPA);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            LOGGER.error(e);
        }
    }

    private static HashMap<String, String> getEntityFromSQLStr(String sql) {
        HashMap<String, String> variableEntitiesMap = new HashMap<>();
        String[] variableEntitiesList = sql.split(" ");
        for (int i = 0; i < variableEntitiesList.length; i++) {
            variableEntitiesMap.putIfAbsent(variableEntitiesList[i], variableEntitiesList[i]);
        }

        return variableEntitiesMap;
    }

    private static String getCharacter(String s) {
        if (s.contains("("))
            return "(";
        if (s.contains(")"))
            return ")";
        if (s.contains(","))
            return ",";

        return "";
    }

    private static String genStrSQL(String strSQLMapping, String target, String replacement) {
        String[] list = strSQLMapping.split(" ");
        String sql = "";
        for (int i = 0; i < list.length; i++) {
            String getCharacterStr = getCharacter(list[i].trim());
            if (getCharacterStr.length() > 0) {
                int indexOfCharacter = list[i].trim().indexOf(getCharacterStr);
                if ((list[i].replaceAll("[,()%^]*", "")).equals(target)) {
                    if (indexOfCharacter == 0) {
                        list[i] = getCharacterStr + replacement;
                    } else {
                        list[i] = replacement + getCharacterStr;
                    }
                }
            } else {
                if ((list[i].replaceAll("[,()%^]*", "")).equals(target)) {
                    list[i] = replacement;
                }
            }
            sql += (list[i] + " ");
        }

        return sql;
    }

    /**
     * thuc hien autogencode dao
     *
     * @param stringTableName
     * @return
     */
    private static StringBuilder generateClassRepositoryJPA(String stringTableName, ObjectEntity itemObject) {
        List<VariableEntity> variableTables = GenEntity.getListVariableFromSql(stringTableName);
        stringTableName = Character.toUpperCase(stringTableName.charAt(0)) + FunctionCommon.camelcasify(stringTableName.substring(1));
        String strClassEntity = FunctionCommon.camelcasify(stringTableName) + "Entity";
//        String strClassEntity = FunctionCommon.camelcasify(stringTableName);
        String strClassDTO = itemObject.getClassName() + "DTO";
        String strClassRepository = stringTableName + "RepositoryJPA";
        String varClassDTO = Character.toLowerCase(itemObject.getClassName().charAt(0)) + FunctionCommon.camelcasify(itemObject.getClassName().substring(1));
        String strClassRepositoryCamel = FunctionCommon.camelcasify(strClassRepository);
        String strDescClass = "Create Repository For Table Name " + stringTableName;
        StringBuilder strContentCodeAction = new StringBuilder();

        // File RepositoryJPA
        //==============chen header import======================================
        strContentCodeAction.append("package ").append(Constants.PACKAGE_NAME).append(".repositories.jpa;").append("\r\r");
        strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".entities.").append(strClassEntity).append(";\r");
        strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".dto.").append(strClassDTO).append(";\r");
        strContentCodeAction.append("import org.springframework.stereotype.Repository;").append("\r");
        strContentCodeAction.append("import org.springframework.data.domain.Page;").append("\r");
        strContentCodeAction.append("import org.springframework.data.domain.Pageable;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.JpaRepository;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.Query;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.Modifying;").append("\r");
        strContentCodeAction.append("import org.springframework.data.repository.query.Param;").append("\r");
        strContentCodeAction.append("import org.springframework.transaction.annotation.Transactional;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.Query;").append("\r");


        //thuc hien gen comment
        strContentCodeAction.append("/**").append("\r");
        strContentCodeAction.append(" * ").append("Autogen class Repository Interface: ").append(strDescClass).append("\r");
        strContentCodeAction.append(" * ").append("\r");
        strContentCodeAction.append(" * @author ToolGen").append("\r");
        strContentCodeAction.append(" * @date ").append(new Date()).append("\r");
        strContentCodeAction.append(" */").append("\r");

        //thuc hien code phan content class
        strContentCodeAction.append("@Repository").append("\r");
        strContentCodeAction.append("public interface ").append(strClassRepositoryCamel).append(" extends JpaRepository<").append(strClassEntity).append(", Long> {").append("\r");

        // Gen method Repository JPA
        List<VariableEntity> variableEntities = GenDTO.getListVariableFrom(itemObject, true);
        CommonDataBaseDao commonDataBaseDao = new CommonDataBaseDao();
        itemObject.getListMethod().forEach((method) -> {
            Map<String, String> variableEntitiesMap = getEntityFromSQLStr(method.getSql());
            StringBuilder strParams = new StringBuilder();
            if (method.getJpa() != null && method.getJpa()) {
                String prefixFirstEntity = String.valueOf(Character.toLowerCase(strClassEntity.charAt(0)));
                String strSQLMapping = method.getSql();
                if (method.getParams() != null) {
                    // Replace param and column
                    strSQLMapping = strSQLMapping.toLowerCase();
                    for (String param : method.getParams()) {
                        VariableEntity variableEntity = variableEntities.stream().filter(variable -> param.equalsIgnoreCase(variable.getColumnName())).findAny().orElse(null);
//                        VariableEntity variableEntity = variableEntities.stream().filter(variable -> param.contains(variable.getColumnName())).findAny().orElse(null);
                        if (variableEntity != null) {
//                            strSQLMapping = strSQLMapping.replace(":" + param.toLowerCase(), ":#{#" + varClassDTO + "." + variableEntity.getColumnName() + "}");
                            strSQLMapping = genStrSQL(strSQLMapping, ":" + param.toLowerCase(), ":#{#" + varClassDTO + "." + variableEntity.getColumnName() + "}");
                        }
                    }

                    if (method.getValue() != null && method.getValue().trim().length() > 0) {
                        List<String> listParams = FunctionCommon.getListParamsFromUrl(method.getValue());
                        for (String param : listParams) {
                            if (param != null && param.trim().length() > 0) {
                                VariableEntity variableEntity = variableEntities.stream().filter(variable -> param.equalsIgnoreCase(variable.getColumnName())).findAny().orElse(null);
//                                VariableEntity variableEntity = variableEntities.stream().filter(variable -> param.contains(variable.getColumnName())).findAny().orElse(null);
                                if (variableEntity != null) {
//                                    strSQLMapping = strSQLMapping.replace(":" + param.toLowerCase(), ":#{#" + varClassDTO + "." + variableEntity.getColumnName() + "}");
                                    strSQLMapping = genStrSQL(strSQLMapping, ":" + param.toLowerCase(), ":#{#" + varClassDTO + "." + variableEntity.getColumnName() + "}");
                                }
                            }
                        }
                    }
                    for (VariableEntity variableEntity : variableEntities) {
//                        if (variableEntity.getColumnNameOrigin() != null && strSQLMapping.contains(variableEntity.getColumnNameOrigin().toLowerCase())) {
//                        String columnNameOrigin = variableEntity.getColumnNameOrigin().toLowerCase();
                        if (variableEntity.getColumnNameOrigin() != null && variableEntitiesMap.containsKey(variableEntity.getColumnNameOrigin().toLowerCase())) {
//                            String variableJPa = prefixFirstEntity + "." + Character.toLowerCase(variableEntity.getColumnName().charAt(0)) + FunctionCommon.camelcasify(variableEntity.getColumnName().substring(1));
//                            char temp = Character.toLowerCase(variableEntity.getColumnName().charAt(0));
//                            String temp1 = FunctionCommon.camelcasify(variableEntity.getColumnName().substring(1));
                            String variableJPa = "";
//                            strSQLMapping = strSQLMapping.replace(variableEntity.getColumnNameOrigin(), variableJPa);
                            if (commonDataBaseDao.getDatabaseName().equals("mysql")) {
                                variableJPa += prefixFirstEntity + "." + variableEntity.getColumnNameOrigin().toLowerCase();
                            } else {
                                variableJPa += prefixFirstEntity + "." + variableEntity.getColumnName();
                            }
                            strSQLMapping = genStrSQL(strSQLMapping, variableEntity.getColumnNameOrigin(), variableJPa);

                        }
                    }
                    strParams.append("@Param(\"").append(varClassDTO).append("\") ").append(strClassDTO).append(" ").append(varClassDTO).append(", ");

                }
                if (numberOfTable > 1) {

                } else {
                    String strSqlWhere = "";
                    if (strSQLMapping.toLowerCase().contains("where")) {
                        strSqlWhere = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("where"));
                    }

                    if (method.getSql() != null && method.getSql().trim().length() > 0) {
                        if (commonDataBaseDao.getDatabaseName().equals("mysql")) {
                            String strClassEntityTemp = getTableNameOrigin(strSQLMapping);
                            String sqlCommand = method.getSql().toLowerCase().trim().replaceAll("( )+", " ");
                            if (sqlCommand.startsWith("insert into") || sqlCommand.startsWith("update") || sqlCommand.startsWith("delete")) {
                                strContentCodeAction.append("    @Transactional\r");
                                strContentCodeAction.append("    @Modifying\r");
                                if (sqlCommand.startsWith("insert into")) {
                                    strSQLMapping = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("values"));
                                    strContentCodeAction.append("    @Query(value =\"INSERT INTO ").append(strClassEntityTemp).append(" ").append(prefixFirstEntity).append(" ").append(strSQLMapping).append("\", nativeQuery = true)\r");
                                } else if (sqlCommand.startsWith("update")) {
                                    strSQLMapping = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("set"));
                                    strContentCodeAction.append("    @Query(value =\"UPDATE ").append(strClassEntityTemp).append(" ").append(prefixFirstEntity).append(" ").append(strSQLMapping).append("\", nativeQuery = true)\r");
                                } else if (sqlCommand.startsWith("delete")) {
                                    strContentCodeAction.append("    @Query(value =\"DELETE FROM ").append(strClassEntityTemp).append(" ").append(prefixFirstEntity).append(" ").append(strSqlWhere).append("\", nativeQuery = true)\r");
                                }
                                strContentCodeAction.append("    int ").append(method.getName()).append("(").append(strParams.substring(0, strParams.length() - 2)).append(");\r");
                            } else {
                                strContentCodeAction.append("    @Query(value =\"SELECT * FROM ").append(strClassEntityTemp).append(" ").append(prefixFirstEntity).append(" ").append(strSqlWhere).append("\", nativeQuery = true)\r");
                                strContentCodeAction.append("    Page<").append(strClassEntity).append("> ").append(method.getName()).append("(").append(strParams).append("Pageable pageable);\r");
                            }
                        } else {
                            String sqlCommand = method.getSql().toLowerCase().trim().replaceAll("( )+", " ");
                            if (sqlCommand.startsWith("insert into") || sqlCommand.startsWith("update") || sqlCommand.startsWith("delete")) {
                                strContentCodeAction.append("    @Transactional\r");
                                strContentCodeAction.append("    @Modifying\r");
                                if (sqlCommand.startsWith("insert into")) {
                                    strSQLMapping = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("values"));
                                    strContentCodeAction.append("    @Query(value =\"INSERT INTO ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSQLMapping).append("\")\r");
                                } else if (sqlCommand.startsWith("update")) {
                                    strSQLMapping = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("set"));
                                    strContentCodeAction.append("    @Query(value =\"UPDATE ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSQLMapping).append("\")\r");
                                } else if (sqlCommand.startsWith("delete")) {
                                    strContentCodeAction.append("    @Query(value =\"DELETE FROM ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSqlWhere).append("\")\r");
                                }
                                strContentCodeAction.append("    int ").append(method.getName()).append("(").append(strParams.substring(0, strParams.length() - 2)).append(");\r");
                            } else {
                                strContentCodeAction.append("    @Query(value =\"FROM ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSqlWhere).append("\")\r");
                                strContentCodeAction.append("    Page<").append(strClassEntity).append("> ").append(method.getName()).append("(").append(strParams).append("Pageable pageable);\r");
                            }
                        }
                    }
                }
            }
        });

        strContentCodeAction.append("\n}");
        return strContentCodeAction;
    }

    private static String getTableNameOrigin(String strSQLMapping) {
        String[] strings = strSQLMapping.split(" ");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].toLowerCase().equals("from"))
                return strings[i + 1];
            if (strings[i].toLowerCase().equals("update"))
                return strings[i + 1];
            if (strings[i].toLowerCase().equals("into"))
                return strings[i + 1];
        }
        return "";
    }
}
