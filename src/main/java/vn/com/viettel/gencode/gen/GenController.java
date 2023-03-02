package vn.com.viettel.gencode.gen;

import org.apache.log4j.Logger;
import vn.com.viettel.gencode.entities.MethodEntity;
import vn.com.viettel.gencode.entities.ObjectEntity;
import vn.com.viettel.gencode.entities.VariableEntity;
import vn.com.viettel.gencode.utils.Constants;
import vn.com.viettel.gencode.utils.FunctionCommon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static vn.com.viettel.gencode.gen.GenEntity.getListVariableFromSql;

/**
 * Gen class Controller
 */
public class GenController {

    private static final Logger LOGGER = Logger.getLogger(GenController.class);

    public static void writeClassController(ObjectEntity itemObject) {
        try {
            if (itemObject != null) {
                String strClassController = itemObject.getClassName() + "Controller";
                String pathFileController = new StringBuilder().
                        append("src/main/java").
                        append(Constants.PACKAGE_NAME_PATH).
                        append("controllers").
                        append("/").
                        append(strClassController).
                        append(".java").toString();

                File file = new File(pathFileController);

                if (file.exists()) {
                    // File da ton tai thi check xem thieu variale nao se tien hanh them vao cuoi file
                    StringBuilder strString = new StringBuilder();
                    strString.append(FunctionCommon.readLineByLine(pathFileController));
                    String strSubLast = strString.substring(0, strString.toString().trim().lastIndexOf("}"));

                    StringBuilder strFullCode = addMethodIfNotExitsFile(strSubLast, itemObject);
                    FileWriter fileWriterAction = new FileWriter(pathFileController);
                    try (PrintWriter printWriteAction = new PrintWriter(fileWriterAction)) {
                        printWriteAction.print(strFullCode);
                    }
                    return;
                } else {
                    // File chua ton tai thi tao moi file tu dau
                    file.getParentFile().mkdirs();
                    FileWriter fileWriterAction = new FileWriter(pathFileController);
                    try (PrintWriter printWriteAction = new PrintWriter(fileWriterAction)) {
                        StringBuilder strContentCodeAction = generateClassController(itemObject);
                        printWriteAction.print(strContentCodeAction);
                    }
                }
                System.out.println("Generate File Controller = " + strClassController);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            LOGGER.error(e);
        }
    }

    public static StringBuilder generateClassController(ObjectEntity itemObject) {
        List<String> listTableName = FunctionCommon.getListTableFromSql(itemObject);
        String strClassController = itemObject.getClassName() + "Controller";
        String strClassService = itemObject.getClassName() + "Service";
        String strClassDTO = itemObject.getClassName() + "DTO";
        String strDescClass = itemObject.getDesc();
        StringBuilder strContentCodeAction = new StringBuilder();

        // File Controller
        //==============chen header import======================================
        strContentCodeAction.append("package ").append(Constants.PACKAGE_NAME).append(".controllers;").append("\r\r");
        strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".dto.").append(strClassDTO).append(";\r");
        strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".services.").append(strClassService).append(";\r");
        if (listTableName != null && !listTableName.isEmpty()) {
            for (String varTableName : listTableName) {
                String strClassServiceJPA = FunctionCommon.camelcasify(Character.toUpperCase(varTableName.charAt(0)) + varTableName.substring(1)) + "ServiceJPA";
                strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".services.jpa.").append(strClassServiceJPA).append(";\r");
            }
        }
        strContentCodeAction.append("import lombok.RequiredArgsConstructor;").append("\r");
        strContentCodeAction.append("import org.springframework.http.MediaType;").append("\r");
        strContentCodeAction.append("import org.springframework.security.core.Authentication;").append("\r");
        strContentCodeAction.append("import org.springframework.http.ResponseEntity;").append("\r");
        strContentCodeAction.append("import org.springframework.web.bind.annotation.*;").append("\r");
        strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".utils.Constants;").append("\r");
        strContentCodeAction.append("import ").append(Constants.PACKAGE_NAME).append(".utils.ResponseUtils;").append("\r\r");

        //thuc hien gen comment
        strContentCodeAction.append("/**").append("\r");
        strContentCodeAction.append(" * ").append("Autogen class: ").append(strDescClass).append("\r");
        strContentCodeAction.append(" * ").append("\r");
        strContentCodeAction.append(" * @author ToolGen").append("\r");
        strContentCodeAction.append(" * @date ").append(new Date()).append("\r");
        strContentCodeAction.append(" */").append("\r");

        //thuc hien code phan content class
        strContentCodeAction.append("@RestController").append("\r");
        strContentCodeAction.append("@RequestMapping(Constants.REQUEST_MAPPING_PREFIX)").append("\r");
        strContentCodeAction.append("@RequiredArgsConstructor").append("\r");
        strContentCodeAction.append("public class ").append(strClassController).append(" {").append("\r");

        //thuc hien gen method trong khai bao
        Map<String, String> checkMap = new HashMap<>();
        List<String> classServiceList = new ArrayList<>();
        for (MethodEntity method : itemObject.getListMethod()) {
            if (method.getJpa() != null && method.getJpa()) {
                if (listTableName != null && !listTableName.isEmpty()) {
                    for (String varTableName : listTableName) {
                        String strClassServiceJPA = FunctionCommon.camelcasify(Character.toUpperCase(varTableName.charAt(0)) + varTableName.substring(1)) + "ServiceJPA";
                        String variableServiceJPA = Character.toLowerCase(strClassServiceJPA.charAt(0)) + strClassServiceJPA.substring(1);
                        if (!checkMap.containsKey(strClassServiceJPA)) {
                            strContentCodeAction.append("    private final ").append(strClassServiceJPA).append(" ").append(variableServiceJPA).append(";\r");
                            checkMap.put(strClassServiceJPA, strClassServiceJPA);
                        }
                        classServiceList.add(strClassServiceJPA);
                    }
                }
            } else {
                String variableService = Character.toLowerCase(strClassService.charAt(0)) + strClassService.substring(1);
                if (!checkMap.containsKey(strClassService)) {
                    strContentCodeAction.append("    private final ").append(strClassService).append(" ").append(variableService).append(";\r");
                    checkMap.put(strClassService, strClassService);
                }
                classServiceList.add(strClassService);
            }
        }
        for (int i = 0; i < itemObject.getListMethod().size(); i++) {
            strContentCodeAction.append(generateFunctionController(strClassDTO, classServiceList.get(i), itemObject.getListMethod().get(i), itemObject));
        }
        strContentCodeAction.append("\n}");
        return strContentCodeAction;
    }

    public static String cleanString(String s, String characterReplace) {
        s = s.replaceAll("[^\\w\\s]", "");
        s = s.toLowerCase().trim();
        s = s.replaceAll(" ", characterReplace);

        return s;
    }

    /**
     * @param strClassDTO
     * @param serviceClass
     * @param strMethodName
     * @return
     */
    public static StringBuilder generateFunctionController(String strClassDTO, String serviceClass, MethodEntity strMethodName, ObjectEntity itemObject) {
        String variableService = Character.toLowerCase(serviceClass.charAt(0)) + serviceClass.substring(1);
        String strVariableClassDTO = Character.toLowerCase(strClassDTO.charAt(0)) + FunctionCommon.camelcasify(strClassDTO.substring(1));
        StringBuilder strContentCodeAction = new StringBuilder();
        //thuc hien gen cac ham trong class
        strContentCodeAction.append("    \r\r");
        strContentCodeAction.append("    /**").append("\r");
        strContentCodeAction.append("     * ").append(strMethodName.getDesc()).append("\r");
        strContentCodeAction.append("     * ").append("\r");
        strContentCodeAction.append("     * @param authentication:").append(" thong tin nguoi dung").append("\r");
        strContentCodeAction.append("     * @param ").append(strVariableClassDTO).append(": params client").append("\r");
        strContentCodeAction.append("     * @return ").append("\r");
        strContentCodeAction.append("     */").append("\r");
        //thuc hien gen method theo type duoc cau hinh
        String strTypeMethod = "";
        if (strMethodName.getType() != null && strMethodName.getType().trim().length() > 0) {
            strTypeMethod = strMethodName.getType().trim().toLowerCase();
        }
        List<String> listParams;
        StringBuilder strParams = new StringBuilder();
        if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
            listParams = FunctionCommon.getListParamsFromUrl(strMethodName.getValue());

            Map<String, String> paramAndType = new HashMap<>();
            if (itemObject != null) {
                //thuc hien kiem tra trong cau sql lay ra danh sach bang
                List<String> listTableName = FunctionCommon.getListTableFromSql(itemObject);
//                String tableName = "";
//                for (String stringTableName : listTableName) {
////                    List<VariableEntity> listVariable = getListVariableFromSql(stringTableName);
//                    if (stringTableName.contains(strClassDTO.replace("DTO", "").toLowerCase())) {
//                        tableName += listTableName;
//                        break;
//                    }
//                }
                List<VariableEntity> listVariable = getListVariableFromSql(listTableName.get(0));
                for (VariableEntity variableEntity : listVariable) {
                    paramAndType.put(cleanString(variableEntity.getColumnName(), "").toLowerCase(), variableEntity.getTypeVariable());
                }

            }

            for (String itemParams : listParams) {
                if (itemParams != null && itemParams.trim().length() > 0) {
//                    if (itemParams.toLowerCase().endsWith("id")) {
//                        strParams.append("@PathVariable Integer ").append(itemParams);
//                    } else {
//                        strParams.append("@PathVariable String ").append(itemParams);
//                    }
                    String type = paramAndType.get(itemParams.toLowerCase());
                    strParams.append("@PathVariable ").append(type).append(" ").append(itemParams);
                }
            }
        }

        switch (strTypeMethod) {
            case "post":
                //gen phuong thuc dang get data
                String strValue = strMethodName.getName();
                if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
                    strValue = strMethodName.getValue();
                }
                //lay danh sach params truyen theo url
                strContentCodeAction.append("    @PostMapping(value = \"").append(strValue).append("\", produces = MediaType.APPLICATION_JSON_VALUE)").append("\r");
                strContentCodeAction.append("    public ResponseEntity<Object> ").append(strMethodName.getName()).append("(Authentication authentication, ").append("\r");
                if (strParams.toString().trim().length() > 0) {
                    strContentCodeAction.append("                                              ").append(strParams.toString().trim()).append(",\r");
                }
                strContentCodeAction.append("                                              @RequestBody ").append(strClassDTO).append(" ").append(strVariableClassDTO).append(") {").append("\r");
                break;
            case "put":
                //gen phuong thuc dang get data
                strValue = strMethodName.getName();
                if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
                    strValue = strMethodName.getValue();
                }
                //lay danh sach params truyen theo url
                strContentCodeAction.append("    @PutMapping(value = \"").append(strValue).append("\", produces = MediaType.APPLICATION_JSON_VALUE)").append("\r");
                strContentCodeAction.append("    public ResponseEntity<Object> ").append(strMethodName.getName()).append("(Authentication authentication, ").append("\r");
                if (strParams.toString().trim().length() > 0) {
                    strContentCodeAction.append("                                              ").append(strParams.toString().trim()).append(",\r");
                }
                strContentCodeAction.append("                                              @RequestBody ").append(strClassDTO).append(" ").append(strVariableClassDTO).append(") {").append("\r");
                break;
            case "delete":
                strValue = strMethodName.getName();
                if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
                    strValue = strMethodName.getValue();
                }
                //lay danh sach params truyen theo url
                strContentCodeAction.append("    @DeleteMapping(value = \"").append(strValue).append("\", produces = MediaType.APPLICATION_JSON_VALUE)").append("\r");
                strContentCodeAction.append("    public ResponseEntity<Object> ").append(strMethodName.getName()).append("(Authentication authentication, ").append("\r");
                if (strParams.toString().trim().length() > 0) {
                    strContentCodeAction.append("                                              ").append(strParams.toString().trim()).append(",\r");
                }
                strContentCodeAction.append("                                              @RequestBody ").append(strClassDTO).append(" ").append(strVariableClassDTO).append(") {").append("\r");
                break;
            case "get":
                strValue = strMethodName.getName();
                if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
                    strValue = strMethodName.getValue();
                }
                //gen phuong thuc dang get data
                strContentCodeAction.append("    @GetMapping(value = \"").append(strValue).append("\", produces = MediaType.APPLICATION_JSON_VALUE)").append("\r");
                strContentCodeAction.append("    public ResponseEntity<Object> ").append(strMethodName.getName()).append("(Authentication authentication, ").append("\r");
                if (strParams.toString().trim().length() > 0) {
                    strContentCodeAction.append("                                              ").append(strParams.toString().trim()).append(",");
                }
                strContentCodeAction.append("                                              ").append(strClassDTO).append(" ").append(strVariableClassDTO).append(") {").append("\r");
                break;
            default:
                if (strMethodName.getName().startsWith("get")) {
                    strValue = strMethodName.getName();
                    if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
                        strValue = strMethodName.getValue();
                    }
                    //gen phuong thuc dang get data
                    strContentCodeAction.append("    @GetMapping(value = \"").append(strValue).append("\", produces = MediaType.APPLICATION_JSON_VALUE)").append("\r");
                    strContentCodeAction.append("    public ResponseEntity<Object> ").append(strMethodName.getName()).append("(Authentication authentication, ").append("\r");
                    if (strParams.toString().trim().length() > 0) {
                        strContentCodeAction.append("                                             ").append(strParams.toString().trim()).append(",").append("\r");
                    }
                    strContentCodeAction.append("                                              ").append(strClassDTO).append(" ").append(strVariableClassDTO).append(") {").append("\r");
                } else {
                    strValue = strMethodName.getName();
                    if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
                        strValue = strMethodName.getValue();
                    }
                    //gen phuong thuc dang post data
                    strContentCodeAction.append("    @PostMapping(value = \"").append(strValue).append("\", produces = MediaType.APPLICATION_JSON_VALUE)").append("\r");
                    strContentCodeAction.append("    public ResponseEntity<Object> ").append(strMethodName.getName()).append("(Authentication authentication, ").append("\r");
                    if (strParams.toString().trim().length() > 0) {
                        strContentCodeAction.append("                                              ").append(strParams.toString().trim()).append(",\r");
                    }
                    strContentCodeAction.append("                                              @RequestBody ").append(strClassDTO).append(" ").append(strVariableClassDTO).append(") {").append("\r");
                }
                break;
        }

        strContentCodeAction.append("        /*").append("\r");
        strContentCodeAction.append("        ==========================================================").append("\r");
        strContentCodeAction.append("        authentication: user info and role").append("\r");
        strContentCodeAction.append("        ").append(strVariableClassDTO).append(": danh sach bien client co the truyen len").append("\r");
        strContentCodeAction.append("        ==========================================================").append("\r");
        strContentCodeAction.append("        */").append("\r");

        //khai bao class service de goi
        if (strMethodName.getValue() != null && strMethodName.getValue().trim().length() > 0) {
            listParams = FunctionCommon.getListParamsFromUrl(strMethodName.getValue());
            for (String itemParams : listParams) {
                if (itemParams != null && itemParams.trim().length() > 0) {
                    String nameParam = Character.toUpperCase(itemParams.charAt(0)) + itemParams.substring(1);
                    strContentCodeAction.append("        ").append(strVariableClassDTO).append(".set").append(nameParam).append("(").append(itemParams).append(");\r");
                }
            }
        }
        strContentCodeAction.append("        ").append("Object result = ").append(variableService).append(".").append(strMethodName.getName()).append("(").append(strVariableClassDTO);
        strContentCodeAction.append(");").append("\r");
        strContentCodeAction.append("        return ").append("ResponseUtils.getResponseEntity(result);").append("\r");
        strContentCodeAction.append("    }");
        return strContentCodeAction;
    }

    private static StringBuilder addMethodIfNotExitsFile(String strSubLast, ObjectEntity itemObject) {
        StringBuilder strContentCodeAction = new StringBuilder(strSubLast);
        itemObject.getListMethod().forEach((method) -> {
            String strMethodName = " " + method.getName().toLowerCase() + "(";
            String strMethodName1 = " " + method.getName().toLowerCase() + " (";

            String strContenFile = strContentCodeAction.toString().replaceAll("\\s{2,}", " ").toLowerCase();


            if (!strContenFile.contains(strMethodName) && !strContenFile.contains(strMethodName1)) {
                LOGGER.info("method= " + method.getName());
                String strClassService = itemObject.getClassName() + "Service";
                String strClassDTO = itemObject.getClassName() + "DTO";
                //Neu khong co phuong thuc trong class thi add them phuong thuc
                strContentCodeAction.append(generateFunctionController(strClassDTO, strClassService, method, itemObject)).append("\r");
            }
            //kiem tra xem co du phuong thuc post get hay ko
            List<MethodEntity> listMethod = itemObject.getListMethod();
            for (MethodEntity methodEntity : listMethod) {
                if (!methodEntity.getName().toLowerCase().startsWith("get")) {
                    break;
                }
            }

        });
        //add lai ky tu dong class
        strContentCodeAction.append("}");
        return strContentCodeAction;
    }
}
