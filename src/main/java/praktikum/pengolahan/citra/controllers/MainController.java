package praktikum.pengolahan.citra.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import praktikum.pengolahan.citra.Statics;
import praktikum.pengolahan.citra.contracts.ApplyEffect;
import praktikum.pengolahan.citra.contracts.UpdateUI;
import praktikum.pengolahan.citra.processor.Effects;
import praktikum.pengolahan.citra.processor.ImageProcessor;
import praktikum.pengolahan.citra.runnables.ImageToNDArray;
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


public class MainController implements Initializable, EventHandler<MouseEvent> {

  @FXML
  Label lblTitle, lblWait, lblResolution, lblCoordinat;

  @FXML
  HBox hbEffectContainer, pImagePropertiesContainer;

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

  private Image imageFileToEdit;
  private File editedImageFile;
  private boolean ifPictureExists;
  private List<ImageView> effectThumbs = new ArrayList<>();
  private HashMap<String, ApplyEffect> effects = new HashMap<>();
  private Thread thread;
  private ImageToNDArray imageToNDArray;

  public void initialize(URL location, ResourceBundle resources) {
    lblTitle.setText(Statics.APP_NAME);
    initEffects();
    setDefaultImage();
  }

  private EventHandler<? super MouseEvent> onMouseMoved() {
    return event -> {
      PixelReader pixelReader = imageFileToEdit.getPixelReader();
      int x = (int) event.getX();
      int y = (int) event.getY();
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

  private void registerEffects(ImageView thumbnail, ApplyEffect effectToApply) {
    thumbnail.setOnMouseClicked(this);
    effectThumbs.add(thumbnail);
    effects.put(thumbnail.getId(), effectToApply);
  }

  private void initEffects() {
    registerEffects(ivOriginal, () -> applyOriginal());
    registerEffects(ivGreyEffect, () -> applyGrey());
    registerEffects(ivContrastEffect, () -> applyContrast());
    registerEffects(ivBlackWhiteEffect, () -> applyBlackWhite());
    registerEffects(ivBrightnessEffect, () -> applyBrightness());
  }

  private void applyOriginal() {
    ivImageToEdit.setImage(imageFileToEdit);
  }

  private void applyGrey() {
    int[][][] imageToColors = imageToNDArray.getImageToColors();
    int[][][] grayScaledColors = Effects.grayScale(imageToColors);
    Image grayScaledColors_toImage = ImageProcessor.colorsToImage(grayScaledColors);
    ivImageToEdit.setImage(grayScaledColors_toImage);
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
      ivImageToEdit.setOnMouseMoved(onMouseMoved());
    }
  }


  @FXML
  private void choosePicture() {
    File file = FileUtils.showChoseImageFileDialog();
    showWaitContainer();
    setIvImages(file);
    ifPictureExists = true;
    toggleEffectContainer();
    imageToNDArray = new ImageToNDArray(file, hideWaitContainer());
    thread = new Thread(imageToNDArray);
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
    showWaitContainer();
    Dragboard dragboard = event.getDragboard();
    File firstDraggedFile = dragboard.getFiles().get(0);
    setIvImages(firstDraggedFile);
    ifPictureExists = true;
    toggleEffectContainer();
    imageToNDArray = new ImageToNDArray(firstDraggedFile, hideWaitContainer());
    thread = new Thread(imageToNDArray);
    thread.start();
  }

  private void setIvImages(File imageFile) {
    setIvImageToEditImage(imageFile);
    setThumbnailEffectImage(imageFile);
  }

  private void setIvImageToEditImage(File imageFile) {
    try {
      imageFileToEdit = new Image(new FileInputStream(imageFile));
      String width = String.format("%spx", String.valueOf((int) imageFileToEdit.getWidth()));
      String height = String.format("%spx", String.valueOf((int) imageFileToEdit.getHeight()));
      lblResolution.setText(String.format("%s%s", width, height));
      ivImageToEdit.setImage(imageFileToEdit);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void setThumbnailEffectImage(File imageFile) {
    try {
      Image image = new Image(new FileInputStream(imageFile),
          Statics.THUMBNAIL_EFFECT_WIDTH,
          Statics.THUMBNAIL_EFFECT_HEIGHT,
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

  private void showWaitContainer() {
    apWaitContainer.setVisible(true);
  }

  private UpdateUI hideWaitContainer() {
    return () -> {
      apWaitContainer.setVisible(false);
    };
  }

}
