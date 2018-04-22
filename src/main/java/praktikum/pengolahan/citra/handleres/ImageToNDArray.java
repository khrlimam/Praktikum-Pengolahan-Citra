package praktikum.pengolahan.citra.handleres;

import praktikum.pengolahan.citra.contracts.ExecutionDetail;

import java.io.File;

import static praktikum.pengolahan.citra.processors.ImageProcessor.imageToColors;


public class ImageToNDArray implements Runnable {
  private File file;
  private int[][][] imageToColors;
  private ExecutionDetail executionDetail;

  public ImageToNDArray(File file) {
    this.file = file;
  }

  public ImageToNDArray(File file, ExecutionDetail executionDetail) {
    this.file = file;
    this.executionDetail = executionDetail;
  }

  public int[][][] getImageToColors() {
    return imageToColors;
  }

  private void executePre() {
    if (executionDetail != null)
      executionDetail.preExecution();
  }

  private void executePost() {
    if (executionDetail != null)
      executionDetail.postExecution();
  }

  @Override
  public void run() {
    executePre();
    this.imageToColors = imageToColors(this.file);
    executePost();
  }
}
