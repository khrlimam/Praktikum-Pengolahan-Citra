package praktikum.pengolahan.citra.processor;

import javafx.scene.image.Image;

import static praktikum.pengolahan.citra.processor.ColorOperations.*;

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

  public static Image grayScale(Image inputImage) {
    int[][][] colors = ImageProcessor.imageToColors(inputImage);
    int[][][] grayScaled = grayScale(colors);
    return ImageProcessor.colorsToImage(grayScaled);
  }


}
