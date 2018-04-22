package praktikum.pengolahan.citra.processor;

import javafx.scene.image.Image;

import static praktikum.pengolahan.citra.utils.ColorOperations.*;

public class Effects {

  public static int[][][] addContrast(int beta, int[][][] colors) {
    performOperationsTo(colors, (row, column) -> {
      int newRed = (getRed(colors, row, column) + beta) & 255;
      setRed(colors, row, column, newRed);
      int newGreen = (getGreen(colors, row, column) + beta) & 255;
      setGreen(colors, row, column, newGreen);
      int newBlue = (getBlue(colors, row, column) + beta) & 255;
      setBlue(colors, row, column, newBlue);
    });
    return colors;
  }

  public static int[][][] grayScale(int[][][] colors) {
    performOperationsTo(colors, (row, column) -> {
      int red = getRed(colors, row, column);
      int green = getGreen(colors, row, column);
      int blue = getBlue(colors, row, column);
      int newGrayScale = (red + green + blue) / 3;
      setRed(colors, row, column, newGrayScale);
      setGreen(colors, row, column, newGrayScale);
      setBlue(colors, row, column, newGrayScale);
    });
    return colors;
  }

  public static Image grayScale(Image inputImage) {
    int[][][] colors = ImageProcessor.imageToColors(inputImage);
    int[][][] grayScaled = grayScale(colors);
    Image newImage = ImageProcessor.colorsToImage(grayScaled);
    return newImage;
  }


}
