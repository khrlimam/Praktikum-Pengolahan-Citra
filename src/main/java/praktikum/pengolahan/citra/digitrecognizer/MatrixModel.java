package praktikum.pengolahan.citra.digitrecognizer;

import praktikum.pengolahan.citra.digitrecognizer.pojos.ModelHolder;
import praktikum.pengolahan.citra.processors.ColorOperation;
import praktikum.pengolahan.citra.processors.ImageProcessor;
import praktikum.pengolahan.citra.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MatrixModel {
  private static double[][] models = null;
  public static int ROW = Objects.requireNonNull(modelSource()).size();
  public static int COLUMN = 71 * 132;

  public static List<ModelHolder> modelSource() {
    try {
      return Arrays.asList(
          new ModelHolder(0, Utils.getAppResource("numbers/tests/0.jpeg").openStream()),
          new ModelHolder(1, Utils.getAppResource("numbers/tests/1.jpeg").openStream()),
          new ModelHolder(2, Utils.getAppResource("numbers/tests/2.jpeg").openStream()),
          new ModelHolder(3, Utils.getAppResource("numbers/tests/3.jpeg").openStream()),
          new ModelHolder(4, Utils.getAppResource("numbers/tests/4.jpeg").openStream()),
          new ModelHolder(5, Utils.getAppResource("numbers/tests/5.jpeg").openStream()),
          new ModelHolder(6, Utils.getAppResource("numbers/tests/6.jpeg").openStream()),
          new ModelHolder(7, Utils.getAppResource("numbers/tests/7.jpeg").openStream()),
          new ModelHolder(8, Utils.getAppResource("numbers/tests/8.jpeg").openStream()),
          new ModelHolder(9, Utils.getAppResource("numbers/tests/9.jpeg").openStream())
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  public static double[] flatten(double[][][] colors) {
    double[] flattened = new double[COLUMN];
    ColorOperation.performOperationsTo(colors, (row, column) -> {
      int index = 10 * row + column;
      // make every pixel to grey to scattered the colors
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
        models[index] = flatten(ImageProcessor.imageToColorsDoubled(modelSource().get(index).getImage()));
      }
    }
    return models;
  }

}
