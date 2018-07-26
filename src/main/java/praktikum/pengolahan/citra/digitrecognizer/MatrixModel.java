package praktikum.pengolahan.citra.digitrecognizer;

import praktikum.pengolahan.citra.processors.ColorOperation;
import praktikum.pengolahan.citra.processors.ImageProcessor;
import praktikum.pengolahan.citra.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MatrixModel {
  private static double[][] models = null;
  public static int ROW = 10;
  public static int COLUMN = 9504;
  private static Map<Integer, URI> modelSource;

  public static Map<Integer, URI> modelSource() {
    if (modelSource == null) {
      modelSource = new HashMap<>();
      try {
        modelSource.put(0, Utils.getAppResource("numbers/tests/0.jpeg").toURI());
        modelSource.put(1, Utils.getAppResource("numbers/tests/1.jpeg").toURI());
        modelSource.put(2, Utils.getAppResource("numbers/tests/2.jpeg").toURI());
        modelSource.put(3, Utils.getAppResource("numbers/tests/3.jpeg").toURI());
        modelSource.put(4, Utils.getAppResource("numbers/tests/4.jpeg").toURI());
        modelSource.put(5, Utils.getAppResource("numbers/tests/5.jpeg").toURI());
        modelSource.put(6, Utils.getAppResource("numbers/tests/6.jpeg").toURI());
        modelSource.put(7, Utils.getAppResource("numbers/tests/7.jpeg").toURI());
        modelSource.put(8, Utils.getAppResource("numbers/tests/8.jpeg").toURI());
        modelSource.put(9, Utils.getAppResource("numbers/tests/9.jpeg").toURI());
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
    return modelSource;
  }

  public static double[] flatten(double[][][] colors) {
    double[] flattened = new double[COLUMN];
    ColorOperation.performOperationsTo(colors, (row, column) -> {
      int index = 10 * row + column;
      // make every pixel to grey to scattered th ecolors
      double grey = (ColorOperation.getRed(colors, row, column)
          + ColorOperation.getGreen(colors, row, column)
          + ColorOperation.getBlue(colors, row, column)) / 3;
      flattened[index] = Utils.setThreshold(grey, 200);
    });
    return flattened;
  }

  public static double[][] getModels() {
    if (models == null) {
      models = new double[ROW][COLUMN];
      for (int index = 0; index < ROW; index++) {
        models[index] = flatten(ImageProcessor.imageToColorsDoubled(Paths.get(modelSource().get(index)).toFile()));
      }
    }
    return models;
  }

}
