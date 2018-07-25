package praktikum.pengolahan.citra.processors;

import javafx.scene.image.Image;
import praktikum.pengolahan.citra.utils.Utils;

import static praktikum.pengolahan.citra.processors.ColorOperation.*;
import static praktikum.pengolahan.citra.processors.ImageProcessor.colorsToImage;
import static praktikum.pengolahan.citra.processors.ImageProcessor.imageToColors;

public class Effects {

  public static int[][][] addBrightness(int beta, int[][][] colors) {
    performOperationsTo(colors, (row, column) -> {
      int newRed = Utils.getBoundedColor(getRed(colors, row, column) + beta);
      setRed(colors, row, column, newRed);
      int newGreen = Utils.getBoundedColor(getGreen(colors, row, column) + beta);
      setGreen(colors, row, column, newGreen);
      int newBlue = Utils.getBoundedColor(getBlue(colors, row, column) + beta);
      setBlue(colors, row, column, newBlue);
    });
    return colors;
  }

  public static int[][][] grayScale(int[][][] inputColors) {
    performOperationsTo(inputColors, (row, column) -> {
      int red = getRed(inputColors, row, column);
      int green = getGreen(inputColors, row, column);
      int blue = getBlue(inputColors, row, column);
      int newGrayScale = (red + green + blue) / 3;
      setRed(inputColors, row, column, newGrayScale);
      setGreen(inputColors, row, column, newGrayScale);
      setBlue(inputColors, row, column, newGrayScale);
    });
    return inputColors;
  }

  public static int[][][] toGreen(int[][][] inputColors) {
    performOperationsTo(inputColors, (row, column) -> {
      // deactivate red channel
      setRed(inputColors, row, column, 0);
      // deactivate blue channel
      setBlue(inputColors, row, column, 0);
    });
    return inputColors;
  }

  public static Image grayScale(Image inputImage) {
    int[][][] colors = imageToColors(inputImage);
    int[][][] grayScaled = grayScale(colors);
    return colorsToImage(grayScaled);
  }

  public static int[][][] blackWhite(int[][][] inputColors) {
    performOperationsTo(inputColors, (row, column) -> {
      //all color channels are the same value so we only take one channel, this case is the first index (RED)
      int greyColored = getRed(inputColors, row, column);
      int blackOrWhite = Utils.binaryImageBound(greyColored)*255;
      setRed(inputColors, row, column, blackOrWhite);
      setGreen(inputColors, row, column, blackOrWhite);
      setBlue(inputColors, row, column, blackOrWhite);
    });
    return inputColors;
  }
}
