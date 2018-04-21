package praktikum.pengolahan.citra.processor;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageProcessor {

  public static int[][][] imgToColorMapped(File imageFile) {
    Image image = null;
    try {
      image = new Image(new FileInputStream(imageFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    int[][][] colors = new int[(int) image.getHeight()][(int) image.getWidth()][4];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        Color color = image.getPixelReader().getColor(x, y);
        colors[y][x] = new int[]{
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255),
            (int) (color.getOpacity())};
      }
    }
    return colors;
  }

  public static int[][][] addContrast(int beta, int[][][] color) {
    int width = color[0].length;
    int height = color.length;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int red = color[i][j][0];
        color[i][j][0] = (red + beta) & 255;
        int green = color[i][j][1];
        color[i][j][1] = (green + beta) & 255;
        int blue = color[i][j][2];
        color[i][j][2] = (blue + beta) & 255;
      }
    }
    return color;
  }

  public static File colorMappedToImageFile(double[][][] colors) {
    return new File("./mantap.jpg");
  }

}
