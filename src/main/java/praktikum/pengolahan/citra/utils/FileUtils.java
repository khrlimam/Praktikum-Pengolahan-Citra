package praktikum.pengolahan.citra.utils;

import javafx.stage.FileChooser;
import praktikum.pengolahan.citra.App;

import java.io.File;

public class FileUtils {

  private static FileChooser fileChooser;
  public static final FileChooser.ExtensionFilter ALLOWED_IMAGE = new FileChooser.ExtensionFilter("File gambar", "*.png", "*.jpg", "*.jpeg");

  public static File showChoseImageFileDialog() {
    if (fileChooser == null) {
      fileChooser = new FileChooser();
      fileChooser.getExtensionFilters().add(ALLOWED_IMAGE);
      fileChooser.setTitle(Constants.IMAGE_CHOOSER_DIALOG_TITLE);
    }
    return fileChooser.showOpenDialog(App.APP_STAGE);
  }
}
