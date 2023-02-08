package vn.com.viettel.gencode.gen;

import org.apache.log4j.Logger;
import vn.com.viettel.gencode.entities.MethodEntity;
import vn.com.viettel.gencode.entities.ObjectEntity;
import vn.com.viettel.gencode.entities.VariableEntity;
import vn.com.viettel.gencode.utils.FunctionCommon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

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
                String pathRepositoryJPA= FunctionCommon.getPropertiesValue("src.url.create.code")
                        + File.separator + "src"
                        + File.separator + "main"
                        + File.separator + "java"
                        + File.separator + "vn"
                        + File.separator + "com"
                        + File.separator + "viettel"
                        + File.separator + "repositories"
                        + File.separator + "jpa"
                        + File.separator + strClassRepositoryJPA + ".java";
                File file = new File(pathRepositoryJPA);
                if (file.exists()) {
                    StringBuilder strString = new StringBuilder();
                    strString.append(FunctionCommon.readLineByLine(pathRepositoryJPA));
                    String strSubLast = strString.substring(0, strString.toString().trim().lastIndexOf("}"));

                    StringBuilder strFullCode = addMethodIfNotExits(stringTableName,strSubLast, itemObject);
                    FileWriter fileWriterAction = new FileWriter(pathRepositoryJPA);
                    try (PrintWriter printWriteAction = new PrintWriter(fileWriterAction)) {
                        printWriteAction.print(strFullCode);
                    }
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

    /**
     * thuc hien autogencode dao
     * @param stringTableName
     * @return
     */
    private static StringBuilder generateClassRepositoryJPA(String stringTableName, ObjectEntity itemObject) {
        List<VariableEntity> variableTables = GenEntity.getListVariableFromSql(stringTableName);
        stringTableName = Character.toUpperCase(stringTableName.charAt(0)) + FunctionCommon.camelcasify(stringTableName.substring(1));
        String strClassEntity = FunctionCommon.camelcasify(stringTableName) + "Entity";
        String strClassDTO = itemObject.getClassName() + "DTO";
        String strClassRepository = stringTableName + "RepositoryJPA";
        String varClassDTO = Character.toLowerCase(itemObject.getClassName().charAt(0)) + FunctionCommon.camelcasify(itemObject.getClassName().substring(1));
        String strClassRepositoryCamel = FunctionCommon.camelcasify(strClassRepository);
        String strDescClass = "Create Repository For Table Name " + stringTableName;
        StringBuilder strContentCodeAction = new StringBuilder();

        // File RepositoryJPA
        //==============chen header import======================================
        strContentCodeAction.append("package vn.com.viettel.repositories.jpa;").append("\r\r");
        strContentCodeAction.append("import vn.com.viettel.entities.").append(strClassEntity).append(";\r");
        strContentCodeAction.append("import vn.com.viettel.dto.").append(strClassDTO).append(";\r");
        strContentCodeAction.append("import org.springframework.stereotype.Repository;").append("\r");
        strContentCodeAction.append("import org.springframework.data.domain.Page;").append("\r");
        strContentCodeAction.append("import org.springframework.data.domain.Pageable;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.JpaRepository;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.Query;").append("\r");
        strContentCodeAction.append("import org.springframework.data.jpa.repository.Modifying;").append("\r");
        strContentCodeAction.append("import org.springframework.data.repository.query.Param;").append("\r");
        strContentCodeAction.append("import org.springframework.transaction.annotation.Transactional;").append("\r");

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
        itemObject.getListMethod().forEach((method) -> {
            StringBuilder strParams = new StringBuilder();
            if (method.getJpa() != null && method.getJpa()) {
                String prefixFirstEntity = String.valueOf(Character.toLowerCase(strClassEntity.charAt(0)));
                String strSQLMapping = method.getSql();
                if (method.getParams() != null) {
                    // Replace param and column
                    for (VariableEntity variableEntity : variableTables) {
                        if (variableEntity.getColumnNameOrigin() != null && strSQLMapping.contains(variableEntity.getColumnNameOrigin().toUpperCase())) {
                            String variableJPa = prefixFirstEntity + "." + Character.toLowerCase(variableEntity.getColumnName().charAt(0)) + FunctionCommon.camelcasify(variableEntity.getColumnName().substring(1));
                            strSQLMapping = strSQLMapping.replace(variableEntity.getColumnNameOrigin(), variableJPa);
                        }
                    }
                    strSQLMapping = strSQLMapping.toLowerCase();
                    for (String param : method.getParams()) {
                        VariableEntity variableEntity = variableEntities.stream().filter(variable -> param.equalsIgnoreCase(variable.getColumnName())).findAny().orElse(null);
                        if (variableEntity != null) {
                            strSQLMapping = strSQLMapping.replace(":" + param.toLowerCase(), ":#{#" + varClassDTO + "." + variableEntity.getColumnName() + "}");
                        }
                    }

                    if (method.getValue() != null && method.getValue().trim().length() > 0) {
                        List<String> listParams = FunctionCommon.getListParamsFromUrl(method.getValue());
                        for (String param : listParams) {
                            if (param != null && param.trim().length() > 0) {
                                VariableEntity variableEntity = variableEntities.stream().filter(variable -> param.equalsIgnoreCase(variable.getColumnName())).findAny().orElse(null);
                                if (variableEntity != null) {
                                    strSQLMapping = strSQLMapping.replace(":" + param.toLowerCase(), ":#{#" + varClassDTO + "." + variableEntity.getColumnName() + "}");
                                }
                            }
                        }
                    }


                    strParams.append("@Param(\"").append(varClassDTO).append("\") ").append(strClassDTO).append(" ").append(varClassDTO).append(", ");

                }
                if ( numberOfTable > 1) {

                } else {
                    strContentCodeAction.append(generateFunctionJPA(method,strParams,strClassEntity));
                }
            }
        });

        strContentCodeAction.append("\n}");
        return strContentCodeAction;
    }

    private static StringBuilder generateFunctionJPA(MethodEntity method, StringBuilder strParams, String strClassEntity){
        if (method.getJpa() != null && !method.getJpa()) return new StringBuilder();
        StringBuilder strFunctionJPA = new StringBuilder();
        String prefixFirstEntity = String.valueOf(Character.toLowerCase(strClassEntity.charAt(0)));
        String strSQLMapping = method.getSql();
        String strSqlWhere= "";
        if (strSQLMapping.toLowerCase().contains("where")) {
            strSqlWhere = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("where"));
        }

        if (strSQLMapping != null && strSQLMapping.trim().length() > 0) {
            String sqlCommand = strSQLMapping.toLowerCase().trim().replaceAll("( )+", " ");
            if (sqlCommand.startsWith("insert into") || sqlCommand.startsWith("update") || sqlCommand.startsWith("delete")) {
                strFunctionJPA.append("    @Transactional\r");
                strFunctionJPA.append("    @Modifying\r");
                if (sqlCommand.startsWith("insert into")) {
                    strSQLMapping = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("values"));
                    strFunctionJPA.append("    @Query(\"INSERT INTO ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSQLMapping).append("\")\r");
                } else if (sqlCommand.startsWith("update")) {
                    strSQLMapping = strSQLMapping.substring(strSQLMapping.toLowerCase().lastIndexOf("set"));
                    strFunctionJPA.append("    @Query(\"UPDATE ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSQLMapping).append("\")\r");
                } else if (sqlCommand.startsWith("delete")) {
                    strFunctionJPA.append("    @Query(\"DELETE FROM ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSqlWhere).append("\")\r");
                }
                strFunctionJPA.append("    int ").append(method.getName()).append("(").append(strParams.substring(0, strParams.length() - 2)).append(");\r");
            } else {
                strFunctionJPA.append("    @Query(\"FROM ").append(strClassEntity).append(" ").append(prefixFirstEntity).append(" ").append(strSqlWhere).append("\")\r");
                strFunctionJPA.append("    Page<").append(strClassEntity).append("> ").append(method.getName()).append("(").append(strParams).append("Pageable pageable);\r");
            }
        }
        return strFunctionJPA;
    }

    private static StringBuilder addMethodIfNotExits(String stringTableName,String strSubLast, ObjectEntity itemObject) {
        stringTableName = Character.toUpperCase(stringTableName.charAt(0)) + FunctionCommon.camelcasify(stringTableName.substring(1));
        String strClassEntity = FunctionCommon.camelcasify(stringTableName) + "Entity";
        String varClassDTO = Character.toLowerCase(itemObject.getClassName().charAt(0)) + FunctionCommon.camelcasify(itemObject.getClassName().substring(1));
        String strClassDTO = itemObject.getClassName() + "DTO";
        StringBuilder strParams = new StringBuilder();
        strParams.append("@Param(\"").append(varClassDTO).append("\") ").append(strClassDTO).append(" ").append(varClassDTO).append(", ");

        StringBuilder strContentCodeAction = new StringBuilder(strSubLast);
        itemObject.getListMethod().forEach((method) -> {

            String strMethodName = " " + method.getName().toLowerCase() + "(";
            String strMethodName1 = " " + method.getName().toLowerCase() + " (";

            String strContenFile = strContentCodeAction.toString().replaceAll("\\s{2,}", " ").toLowerCase();


            if (!strContenFile.contains(strMethodName) && !strContenFile.contains(strMethodName1)) {
                strContentCodeAction.append(generateFunctionJPA(method, strParams, strClassEntity));
            }

        });
        //add lai ky tu dong class
        strContentCodeAction.append("}");
        return strContentCodeAction;
    }
}
