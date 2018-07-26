package praktikum.pengolahan.citra.digitrecognizer;

import Jama.Matrix;
import praktikum.pengolahan.citra.utils.Log;
import praktikum.pengolahan.citra.utils.Utils;

import java.io.*;
import java.nio.file.FileSystemNotFoundException;

public class ModelInitializer {

  private static Matrix MODEL;

  public static void save(Serializable model) {
    try {
      FileOutputStream outputStream = new FileOutputStream(Utils.createModelFile());
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(model);
      outputStream.close();
      objectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Matrix getModel() {
    try {
      FileInputStream fileInputStream = new FileInputStream(Utils.createModelFile());
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      ModelInitializer.MODEL = (Matrix) objectInputStream.readObject();
      fileInputStream.close();
      objectInputStream.close();
      Log.i(ModelInitializer.class.getName(), "Digit space loaded from disk");
    } catch (FileSystemNotFoundException e) {
      createModel();
    } catch (Exception e) {
      createModel();
    }
    return MODEL;
  }

  private static void createModel() {
    Log.i(ModelInitializer.class.getName(), "Digit space created");
    ModelInitializer.MODEL = new Matrix(MatrixModel.getModels());
    Log.i(ModelInitializer.class.getName(), "Digit space saved to disk");
    ModelInitializer.save(MODEL);
  }

}
