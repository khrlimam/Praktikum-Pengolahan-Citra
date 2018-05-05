package praktikum.pengolahan.citra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import praktikum.pengolahan.citra.utils.Constants;
import praktikum.pengolahan.citra.utils.Utils;

public class App extends Application {

  public static String MAIN_LAYOUT = "main.fxml";
  public static Stage APP_STAGE;

  @Override
  public void start(Stage primaryStage) throws Exception {
    APP_STAGE = primaryStage;
    Parent root = FXMLLoader.load(Utils.getUiResource(MAIN_LAYOUT));
    primaryStage.setTitle(Constants.APP_NAME);
    primaryStage.setScene(new Scene(root));
    primaryStage.setMinHeight(630d);
    primaryStage.setMinWidth(800d);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
