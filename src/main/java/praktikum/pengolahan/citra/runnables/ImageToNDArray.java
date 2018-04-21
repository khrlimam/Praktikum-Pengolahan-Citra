package praktikum.pengolahan.citra.runnables;

import praktikum.pengolahan.citra.contracts.UpdateUI;
import praktikum.pengolahan.citra.processor.ImageProcessor;

import java.io.File;


public class ImageToNDArray implements Runnable {
  private File file;
  private int[][][] img;
  private UpdateUI updateUI;

  public ImageToNDArray(File file) {
    this.file = file;
  }

  public ImageToNDArray(File file, UpdateUI updateUI) {
    this.file = file;
    this.updateUI = updateUI;
  }

  public int[][][] getImg() {
    return img;
  }

  @Override
  public void run() {
    this.img = ImageProcessor.imgToColorMapped(this.file);
    if (updateUI != null) updateUI.update();
  }
}
