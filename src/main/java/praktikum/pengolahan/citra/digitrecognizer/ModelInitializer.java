package praktikum.pengolahan.citra.digitrecognizer;

import Jama.Matrix;
import praktikum.pengolahan.citra.utils.Constants;
import praktikum.pengolahan.citra.utils.Log;

import java.io.*;

public class ModelInitializer {

  private static Matrix MODEL;

  public static void save(Serializable model) {
    try {
      FileOutputStream outputStream = new FileOutputStream(new File(Constants.MODEL_NAME));
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
      FileInputStream fileInputStream = new FileInputStream(new File(Constants.MODEL_NAME));
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      ModelInitializer.MODEL = (Matrix) objectInputStream.readObject();
      fileInputStream.close();
      objectInputStream.close();
      Log.i(ModelInitializer.class.getName(), "Digit space loaded from disk");
    } catch (Exception e) {
      Log.i(ModelInitializer.class.getName(), "Digit space created");
      ModelInitializer.MODEL = new Matrix(MatrixModel.getModels());
      Log.i(ModelInitializer.class.getName(), "Digit space saved to disk");
      ModelInitializer.save(MODEL);
    }
    return MODEL;
  }

}
