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
import praktikum.pengolahan.citra.App;
import praktikum.pengolahan.citra.contracts.ApplyEffect;
import praktikum.pengolahan.citra.contracts.ApplyWithParams;
import praktikum.pengolahan.citra.contracts.ExecutionDetail;
import praktikum.pengolahan.citra.contracts.ReactTo;
import praktikum.pengolahan.citra.processors.Editor;
import praktikum.pengolahan.citra.handleres.RealImageCoordinat;
import praktikum.pengolahan.citra.processors.ImageProcessor;
import praktikum.pengolahan.citra.utils.Constants;
import praktikum.pengolahan.citra.utils.FileUtils;
import praktikum.pengolahan.citra.utils.Log;
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

  private Stage histogramStage, brightnessStage;
  private HistogramController histogramController;
  private BrightnessSettingController brightnessSettingController;
  private Image originalImage;
  private boolean ifPictureExists;
  private List<ImageView> effectThumbs = new ArrayList<>();
  private HashMap<String, ApplyEffect> effects = new HashMap<>();
  private Thread thread;
  private Editor editor;

  public void initialize(URL location, ResourceBundle resources) {
    lblTitle.setText(Constants.APP_NAME);
    initEffects();
    setDefaultImage();
    initDialogs();
  }

  private void initDialogs() {
    FXMLLoader brightnessControllerLoader = Utils.loader(Utils.getUiResource("brightness.fxml"));
    brightnessStage = Utils.makeDialogStage(brightnessControllerLoader, "Brightness Setting", App.APP_STAGE);
    brightnessStage.setMinWidth(484d);
    brightnessStage.setMaxWidth(484d);
    brightnessStage.setMinHeight(441d);
    brightnessStage.setMaxHeight(441d);
    brightnessSettingController = brightnessControllerLoader.getController();
    brightnessSettingController.setApplyWithParams(applyBrightness());
    brightnessStage.setOnCloseRequest(event -> editor.returnColorPreviewStateToOriginal());
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
    registerEffects(ivBrightnessEffect, () -> showBrightnessSetting());
  }

  private void applyOriginal() {
    editor.returnToOriginalState();
    ivImageToEdit.setImage(editor.getEditedImage());
  }

  private void applyGray() {
    editor.addGray();
    ivImageToEdit.setImage(editor.getEditedImage());
  }

  private void applyContrast() {
    System.out.println("adding contrast effect!");
  }

  private void applyBlackWhite() {
    System.out.println("adding black white effect!");
  }

  private void showBrightnessSetting() {
    brightnessSettingController.setIvPreviewImage(editor.getPreviewImage());
    brightnessSettingController.setColorPreview(editor.getColorPreviewState());
    System.out.println(brightnessSettingController.swap);
    brightnessStage.showAndWait();
  }

  private ApplyWithParams applyBrightness() {
    return (param) -> {
      Log.i(getClass(), param+"");
      editor.addBrightness(param);
      ivImageToEdit.setImage(editor.getEditedImage());
      brightnessStage.close();
    };
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
    editor = new Editor(file, waitThen());
    thread = new Thread(editor);
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
    editor = new Editor(firstDraggedFile, waitThen());
    thread = new Thread(editor);
    thread.start();
  }

  @FXML
  private void showHistogram() {
    histogramController.setColors(editor.getColorState());
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
      Log.i(getClass(), String.format("%s %s", image.getWidth(), image.getHeight()));
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
        apWaitContainer.setVisible(false);
      }
    };
  }
}
