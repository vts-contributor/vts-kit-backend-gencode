package vn.com.viettel.gencode;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import vn.com.viettel.gencode.entities.ObjectEntity;
import vn.com.viettel.gencode.gen.*;
import vn.com.viettel.gencode.utils.FunctionCommon;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GenCodeApplication {

    private static final Logger LOGGER = Logger.getLogger(GenCodeApplication.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            String strPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
            Reader reader = Files.newBufferedReader(Paths.get(strPath + "template.json"));
            /*ObjectEntity itemObject = (ObjectEntity) FunctionCommon.convertJsonToObject(reader, ObjectEntity.class);
            reader.close();*/

            List<Object> itemList = FunctionCommon.convertJsonToListObject(reader, ObjectEntity.class);
            reader.close();

            for (Object item: itemList){
                ObjectEntity itemObject = (ObjectEntity) item;
                System.out.println("Object json : " + itemObject.toString());

                //thuc hien gen class controller, dto
                GenController.writeClassController(itemObject);
                GenDTO.writeClassDTO(itemObject);

                //thuc hien gen class service, serviceImpl
                GenService.writeClassService(itemObject);
                GenServiceImpl.writeClassServiceImpl(itemObject);

                //thuc hien gen class repository, repositoryImpl
                GenRepository.writeClassRepository(itemObject);
                GenRepositoryImpl.writeClassRepositoryImpl(itemObject);

                //thuc hien gen class JPA
                GenServiceJPA.writeClassServiceJPA(itemObject);
                GenRepositoryJPA.writeClassRepositoryJPA(itemObject);

                //thuc hien gen class entity
                GenEntity.writeClassEntity(itemObject);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            LOGGER.error(e);
        }
    }

}
