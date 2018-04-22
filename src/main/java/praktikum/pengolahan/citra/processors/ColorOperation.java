package praktikum.pengolahan.citra.processors;

import praktikum.pengolahan.citra.contracts.PerformOperationsTo;

public class ColorOperation {


  public static void performOperationsTo(int[][][] colors, PerformOperationsTo performOperationsTo) {
    int width = getWidth(colors);
    int height = getHeight(colors);
    for (int row = 0; row < height; row++) {
      for (int column = 0; column < width; column++) {
        performOperationsTo.pixelOn(row, column);
      }
    }
  }

  public static int getWidth(int[][][] data) {
    return data[0].length;
  }

  public static int getHeight(int[][][] data) {
    return data.length;
  }

  public static int getRed(int[][][] colors, int x, int y) {
    return colors[x][y][0];
  }

  public static void setRed(int[][][] colors, int x, int y, int newValue) {
    colors[x][y][0] = newValue;
  }

  public static int getGreen(int[][][] colors, int x, int y) {
    return colors[x][y][1];
  }

  public static void setGreen(int[][][] colors, int x, int y, int newValue) {
    colors[x][y][1] = newValue;
  }

  public static int getBlue(int[][][] colors, int x, int y) {
    return colors[x][y][2];
  }

  public static void setBlue(int[][][] colors, int x, int y, int newValue) {
    colors[x][y][2] = newValue;
  }

  public static int getAlpha(int[][][] colors, int x, int y) {
    return colors[x][y][3];
  }
}
