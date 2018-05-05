package praktikum.pengolahan.citra.processors;

import javafx.scene.image.Image;
import praktikum.pengolahan.citra.contracts.ExecutionDetail;
import praktikum.pengolahan.citra.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static praktikum.pengolahan.citra.processors.ImageProcessor.colorsToImage;
import static praktikum.pengolahan.citra.processors.ImageProcessor.imageToColors;


public class Editor implements Runnable {
  private File file;
  private int[][][] colorState;
  private int[][][] colorOriginalState;
  private int[][][] colorPreviewState;
  private int[][][] colorOriginalPreviewState;
  private int[][][] colorPreviewPlaceholder;
  private ExecutionDetail executionDetail;

  public Editor(File file) {
    this.file = file;
  }

  public Editor(File file, ExecutionDetail executionDetail) {
    this.file = file;
    this.executionDetail = executionDetail;
  }

  public int[][][] getColorState() {
    return this.colorState;
  }

  public void setColorState(int[][][] colorState) {
    this.colorState = colorState;
  }

  public int[][][] getColorPreviewState() {
    return colorPreviewState;
  }

  public void setColorPreviewState(int[][][] colorPreviewState) {
    this.colorPreviewState = colorPreviewState;
  }

  private void executePre() {
    if (executionDetail != null)
      executionDetail.preExecution();
  }

  private void executePost() {
    if (executionDetail != null)
      executionDetail.postExecution();
  }

  public void returnToOriginalState() {
    returnColorStateToOriginal();
    returnColorPreviewStateToOriginal();
  }

  public void returnColorStateToOriginal() {
    ColorOperation.performOperationsTo(colorOriginalState, (row, column) ->
        colorState[row][column] = colorOriginalState[row][column].clone());
  }

  public void returnColorPreviewStateToOriginal() {
    ColorOperation.performOperationsTo(colorOriginalPreviewState, (row, column) ->
        colorPreviewState[row][column] = colorOriginalPreviewState[row][column].clone());
  }

  public void addGray() {
    Effects.grayScale(getColorState());
    Effects.grayScale(getColorPreviewState());
  }

  void addContrast(int alpha) {

  }

  void addBlackWhite() {

  }

  public void addBrightness(int beta) {
    Effects.addBrightness(beta, getColorState());
    Effects.addBrightness(beta, getColorPreviewState());
  }

  public Image getEditedImage() {
    return colorsToImage(getColorState());
  }

  public Image getPreviewImage() {
    return colorsToImage(getColorPreviewState());
  }

  @Override
  public void run() {
    executePre();
    setColorState(imageToColors(this.file));
    colorOriginalState = new int[colorState.length][colorState[0].length][colorState[0][0].length];
    ColorOperation.performOperationsTo(colorState, (row, column) ->
        colorOriginalState[row][column] = colorState[row][column].clone());
    try {
      setColorPreviewState(imageToColors(new Image(new FileInputStream(this.file), Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT, false, false)));
      colorOriginalPreviewState = new int[colorPreviewState.length][colorPreviewState[0].length][colorPreviewState[0][0].length];
      colorPreviewPlaceholder = new int[colorPreviewState.length][colorPreviewState[0].length][colorPreviewState[0][0].length];
      ColorOperation.performOperationsTo(colorPreviewState, (row, column) ->
      {
        colorOriginalPreviewState[row][column] = colorPreviewState[row][column].clone();
        colorPreviewPlaceholder[row][column] = colorPreviewState[row][column].clone();
      });
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    executePost();
  }
}
