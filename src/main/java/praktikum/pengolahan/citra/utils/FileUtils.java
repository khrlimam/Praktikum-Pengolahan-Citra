package praktikum.pengolahan.citra.utils;

import javafx.stage.FileChooser;
import org.apache.commons.io.IOUtils;
import praktikum.pengolahan.citra.App;

import java.io.*;

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

  public static File tmpFileFromInputStream(InputStream inputStream) throws IOException {
    final File tempFile = File.createTempFile("tmp", "img");
    tempFile.deleteOnExit();
    try (FileOutputStream out = new FileOutputStream(tempFile)) {
      IOUtils.copy(inputStream, out);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tempFile;
  }

}
