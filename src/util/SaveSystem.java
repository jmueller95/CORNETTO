package util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Zeth on 15.07.2017.
 *
 * implements the load and savesystem
 * classes that want to use this need to implement serializable!
 *
 *
 */
public class SaveSystem {

    /**
     * saves Objects to a file
     *
     * @param filePathToSaveTo where to save the file
     * @param listOfObjects the files to save to, must be a type of Collection!
     * @throws IOException
     */
    public void saveObjects(String filePathToSaveTo, Collection<?> listOfObjects) throws IOException{
        File fileToSaveTo = new File(filePathToSaveTo);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileToSaveTo));
        objectOutputStream.writeObject(listOfObjects);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /**
     *
     * @param filePathToLoadFrom path of the file that you want to load from
     * @param classToLoadTo need to pass a class, so that you can cast the type!
     * @param <T> Generic
     * @return
     * @throws IOException
     */
    public <T> List<T>  loadObjects(String filePathToLoadFrom, Class<T> classToLoadTo) throws IOException{
        File fileToLoadFrom = new File(filePathToLoadFrom);
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileToLoadFrom));
        List<T> listOfReadObjects = new ArrayList<>();
        try {
            Object result = objectInputStream.readObject();
            List<Object> temporaryObjectList = new ArrayList<>();
            temporaryObjectList.add(result);
            listOfReadObjects = castListToGeneric(classToLoadTo, temporaryObjectList);

        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return listOfReadObjects;
    }

    /**
     * casts the Objects to the class
     *
     * @param tClass class to cast to
     * @param objectList list of objects
     * @param <T>
     * @return
     */
    private <T> List<T> castListToGeneric(Class<T> tClass, List<Object> objectList){
        List<T> list = new ArrayList<>();
        for (Object object : objectList){
            T t = tClass.cast(object);
            list.add(t);
        }
        return list;
    }

}
