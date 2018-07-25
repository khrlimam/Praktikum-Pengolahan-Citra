package praktikum.pengolahan.citra;

import praktikum.pengolahan.citra.processors.ColorOperation;
import praktikum.pengolahan.citra.processors.ImageProcessor;
import praktikum.pengolahan.citra.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class PlatReader {
  private static double[][] models = null;
  private static URI[] numbers;
  public static int ROW = 10;
  public static int COLUMN = 9504;

  static {
    try {
      numbers = new URI[]{
          Utils.getAppResource("numbers/0.png").toURI(),
          Utils.getAppResource("numbers/1.png").toURI(),
          Utils.getAppResource("numbers/2.png").toURI(),
          Utils.getAppResource("numbers/3.png").toURI(),
          Utils.getAppResource("numbers/4.png").toURI(),
          Utils.getAppResource("numbers/5.png").toURI(),
          Utils.getAppResource("numbers/6.png").toURI(),
          Utils.getAppResource("numbers/7.png").toURI(),
          Utils.getAppResource("numbers/8.png").toURI(),
          Utils.getAppResource("numbers/9.png").toURI(),
      };
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public static double[] flatten(double[][][] colors) {
    double[] flattened = new double[COLUMN];
    ColorOperation.performOperationsTo(colors, (row, column) -> {
      int index = 10 * row + column;
      // get 2 bits
      double grey = (ColorOperation.getRed(colors, row, column)
          + ColorOperation.getGreen(colors, row, column)
          + ColorOperation.getBlue(colors, row, column)) / 3;
      int element = Utils.binaryImageBound(grey);
      if (element <= 200) element = 10;
      else element = 255;
      flattened[index] = element;
    });
    return flattened;
  }

  public static double[][] getModels() {
    if (models == null) {
      models = new double[ROW][COLUMN];
      for (int index = 0; index < ROW; index++) {
        models[index] = flatten(ImageProcessor.imageToColorsDoubled(Paths.get(numbers[index]).toFile()));
      }
    }
    return models;
  }

}
