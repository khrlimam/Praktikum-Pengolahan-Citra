package praktikum.pengolahan.citra.processors;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageProcessor {

  public static int[][][] imageToColors(Image image) {
    int[][][] colors = new int[(int) image.getHeight()][(int) image.getWidth()][4];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        Color color = image.getPixelReader().getColor(x, y);
        color.grayscale();
        colors[y][x] = new int[]{
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255),
            (int) (color.getOpacity())};
      }
    }
    return colors;
  }

  public static int[][][] imageToColors(File imageFile) {
    try {
      Image image = new Image(new FileInputStream(imageFile));
      return imageToColors(image);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Image colorsToImage(int[][][] colors) {
    int width = ColorOperation.getWidth(colors);
    int height = ColorOperation.getHeight(colors);
    WritableImage writableImage = new WritableImage(width, height);
    PixelWriter pixelWriter = writableImage.getPixelWriter();
    ColorOperation.performOperationsTo(colors, ((row, column) -> {
      Color color = new Color(
          ColorOperation.getRed(colors, row, column) / 255d,
          ColorOperation.getGreen(colors, row, column) / 255d,
          ColorOperation.getBlue(colors, row, column) / 255d,
          ColorOperation.getAlpha(colors, row, column));
      pixelWriter.setColor(column, row, color);
    }));
    return writableImage;
  }
}