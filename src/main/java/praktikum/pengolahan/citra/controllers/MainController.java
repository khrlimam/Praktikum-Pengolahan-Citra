package praktikum.pengolahan.citra.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import praktikum.pengolahan.citra.contracts.ApplyEffect;
import praktikum.pengolahan.citra.contracts.ExecutionDetail;
import praktikum.pengolahan.citra.contracts.ReactTo;
import praktikum.pengolahan.citra.handleres.ImageToNDArray;
import praktikum.pengolahan.citra.handleres.RealImageCoordinat;
import praktikum.pengolahan.citra.processors.Effects;
import praktikum.pengolahan.citra.utils.Constants;
import praktikum.pengolahan.citra.utils.FileUtils;
import praktikum.pengolahan.citra.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static praktikum.pengolahan.citra.processors.ImageProcessor.colorsToImage;


public class MainController implements Initializable, EventHandler<MouseEvent> {

  @FXML
  Label
      lblTitle,
      lblWait,
      lblResolution,
      lblCoordinat;

  @FXML
  HBox
      hbEffectContainer,
      pImagePropertiesContainer;

  @FXML
  AnchorPane apWaitContainer;

  @FXML
  Pane pColor;

  @FXML
  ImageView
      ivImageToEdit,
      ivOriginal,
      ivGreyEffect,
      ivContrastEffect,
      ivBlackWhiteEffect,
      ivBrightnessEffect;

  private Stage histogramStage;
  private HistogramController histogramController;
  private Image originalImage;
  private int[][][] originalColors;
  private boolean ifPictureExists;
  private List<ImageView> effectThumbs = new ArrayList<>();
  private HashMap<String, ApplyEffect> effects = new HashMap<>();
  private Thread thread;
  private ImageToNDArray imageToNDArray;

  public void initialize(URL location, ResourceBundle resources) {
    lblTitle.setText(Constants.APP_NAME);
    initEffects();
    setDefaultImage();
  }

  private void initHistogram() {
    FXMLLoader histogramFXMLLoader = Utils.loader(Utils.getUiResource("histogram.fxml"));
    histogramStage = Utils.makeDialogStage(histogramFXMLLoader, "HistogramController", null);
    histogramStage.setMinWidth(600d);
    histogramStage.setMinHeight(600d);
    histogramController = histogramFXMLLoader.getController();
  }

  private void registerEffects(ImageView thumbnail, ApplyEffect effectToApply) {
    thumbnail.setOnMouseClicked(this);
    effectThumbs.add(thumbnail);
    effects.put(thumbnail.getId(), effectToApply);
  }

  private void initEffects() {
    registerEffects(ivOriginal, () -> applyOriginal());
    registerEffects(ivGreyEffect, () -> applyGray());
    registerEffects(ivContrastEffect, () -> applyContrast());
    registerEffects(ivBlackWhiteEffect, () -> applyBlackWhite());
    registerEffects(ivBrightnessEffect, () -> applyBrightness());
  }

  private void applyOriginal() {
    ivImageToEdit.setImage(originalImage);
  }

  private void applyGray() {
    int[][][] imageToColors = imageToNDArray.getImageToColors();
    int[][][] grayScaledColors = Effects.grayScale(imageToColors);
    ivImageToEdit.setImage(colorsToImage(grayScaledColors));
  }

  private void applyContrast() {
    System.out.println("adding contrast effect!");
  }

  private void applyBlackWhite() {
    System.out.println("adding black white effect!");
  }

  private void applyBrightness() {
    System.out.println("adding brightness effect!");
  }

  private void toggleEffectContainer() {
    hbEffectContainer.setVisible(ifPictureExists);
    pImagePropertiesContainer.setVisible(ifPictureExists);
    if (ifPictureExists) {
      ivImageToEdit.setOnMouseMoved(event -> RealImageCoordinat.getRealCoordinat(event, reactTo()));
    }
  }

  @FXML
  private void choosePicture() {
    File file = FileUtils.showChoseImageFileDialog();
    setIvImages(file);
    ifPictureExists = true;
    toggleEffectContainer();
    imageToNDArray = new ImageToNDArray(file, waitThen());
    thread = new Thread(imageToNDArray);
    if (file != null)
      thread.start();
  }

  @FXML
  private void onDragOver(DragEvent event) {
    Dragboard dragboard = event.getDragboard();
    if (dragboard.hasFiles())
      event.acceptTransferModes(TransferMode.ANY);
  }

  @FXML
  private void onDragDropped(DragEvent event) {
    Dragboard dragboard = event.getDragboard();
    File firstDraggedFile = dragboard.getFiles().get(0);
    setIvImages(firstDraggedFile);
    ifPictureExists = true;
    toggleEffectContainer();
    imageToNDArray = new ImageToNDArray(firstDraggedFile, waitThen());
    thread = new Thread(imageToNDArray);
    thread.start();
  }

  @FXML
  private void showHistogram() {
    histogramController.setColors(originalColors);
    histogramStage.show();
    histogramController
        .drawChart(histogramController
            .colorHistogramObservable());
  }

  @FXML
  private void saveEditedImage() {
    System.out.println("Saving image");
  }

  private void setIvImages(File imageFile) {
    setIvImageToEditImage(imageFile);
    setThumbnailEffectImage(imageFile);
  }

  private void setIvImageToEditImage(File imageFile) {
    try {
      originalImage = new Image(new FileInputStream(imageFile));
      ivImageToEdit.setImage(originalImage);
      showImageResolution();
      initHistogram();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void showImageResolution() {
    String width = String.format("%spx", String.valueOf((int) originalImage.getWidth()));
    String height = String.format("%spx", String.valueOf((int) originalImage.getHeight()));
    lblResolution.setText(String.format("%s%s", width, height));
  }

  private void setThumbnailEffectImage(File imageFile) {
    try {
      Image image = new Image(new FileInputStream(imageFile),
          Constants.THUMBNAIL_EFFECT_WIDTH,
          Constants.THUMBNAIL_EFFECT_HEIGHT,
          false,
          false);

      effectThumbs.forEach(imageView -> imageView.setImage(image));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void setDefaultImage() {
    try {
      ivImageToEdit.setImage(new Image(Utils.getImageResource("drag-image.png").openStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void handle(MouseEvent event) {
    String id = event.getPickResult().getIntersectedNode().getId();
    effects.get(id).apply();
  }

  private ReactTo reactTo() {
    return (x, y) -> {
      Image imageSource = ivImageToEdit.getImage();
      PixelReader pixelReader = imageSource.getPixelReader();
      Color color = pixelReader.getColor(x, y);
      String coordinat = String.format("(%d, %d)", x, y);
      lblCoordinat.setText(coordinat);
      int red = (int) (color.getRed() * 255);
      int green = (int) (color.getGreen() * 255);
      int blue = (int) (color.getBlue() * 255);
      String hexColor = String.format("#%s%s%s", Integer.toHexString(red), Integer.toHexString(green), Integer.toHexString(blue));
      String backgroundColor = String.format("-fx-background-color: %s", hexColor);
      pColor.setStyle(backgroundColor);
    };
  }

  private ExecutionDetail waitThen() {
    return new ExecutionDetail() {
      @Override
      public void preExecution() {
        apWaitContainer.setVisible(true);
      }

      @Override
      public void postExecution() {
        originalColors = imageToNDArray.getImageToColors();
        apWaitContainer.setVisible(false);
      }
    };
  }
}
