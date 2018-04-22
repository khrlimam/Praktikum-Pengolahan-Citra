package praktikum.pengolahan.citra.runnables;

import praktikum.pengolahan.citra.contracts.UpdateUI;
import praktikum.pengolahan.citra.processor.ImageProcessor;

import java.io.File;


public class ImageToNDArray implements Runnable {
  private File file;
  private int[][][] imageToColors;
  private UpdateUI updateUI;

  public ImageToNDArray(File file) {
    this.file = file;
  }

  public ImageToNDArray(File file, UpdateUI updateUI) {
    this.file = file;
    this.updateUI = updateUI;
  }

  public int[][][] getImageToColors() {
    return imageToColors;
  }

  public void setImageToColors(int[][][] imageToColors) {
    this.imageToColors = imageToColors;
  }

  @Override
  public void run() {
    this.imageToColors = ImageProcessor.imageToColors(this.file);
    if (updateUI != null) updateUI.update();
  }
}
