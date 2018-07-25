package praktikum.pengolahan.citra.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import praktikum.pengolahan.citra.App;
import praktikum.pengolahan.citra.processors.ColorOperation;

import java.io.IOException;
import java.net.URL;

public class Utils {

  public static URL getUiResource(String filename) {
    return getAppResource(String.format("uis/%s", filename));
  }

  public static URL getImageResource(String imageName) {
    return getAppResource(String.format("img/%s", imageName));
  }

  public static URL getAppResource(String name) {
    return App.class.getClassLoader().getResource(name);
  }

  public static FXMLLoader loader(URL FXMLLocation) {
    return new FXMLLoader(FXMLLocation);
  }

  public static Stage makeDialogStage(FXMLLoader loader, String title, Stage owner) {
    try {
      AnchorPane pane = loader.load();
      Stage stage = new Stage();
      stage.setTitle(title);
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(owner);
      Scene scene = new Scene(pane);
      stage.setScene(scene);
      return stage;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static int getBoundedColor(int color) {
    if (color < 0) return 0;
    else if (color > 255) return 255;
    else return color;
  }

  public static int setThreshold(int color, int threshold) {
    if (color <= threshold) return 0;
    return 255;
  }

  public static int setThreshold(double color, int threshold) {
    if (color <= threshold) return 10;
    return 255;
  }

  public static int binaryImageBound(int color) {
    int bw = setThreshold(color, 200);
    return bw / 255;
  }

  public static int binaryImageBound(double color) {
    int bw = setThreshold(color, 200);
    return bw / 255;
  }

  public static int[][][] balanceInputMatrix(int[][][] inputMatrix) {
    int inputWidth = ColorOperation.getWidth(inputMatrix);
    int inputHeight = ColorOperation.getHeight(inputMatrix);

    int[][][] balancedMatrix = new int[132][71][1];

    int colNeeded = Constants.EXPECTED_WIDTH - inputWidth;
    int rowNeeded = Constants.EXPECTED_HEIGHT - inputHeight;

    if (colNeeded > 0 && rowNeeded > 0) {

    }
    return balancedMatrix;
  }

}
