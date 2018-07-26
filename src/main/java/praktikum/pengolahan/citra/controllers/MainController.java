package praktikum.pengolahan.citra.controllers;

import Jama.Matrix;
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
import org.apache.commons.lang3.StringUtils;
import praktikum.pengolahan.citra.App;
import praktikum.pengolahan.citra.contracts.ApplyEffect;
import praktikum.pengolahan.citra.contracts.ApplyWithParams;
import praktikum.pengolahan.citra.contracts.ExecutionDetail;
import praktikum.pengolahan.citra.contracts.ReactTo;
import praktikum.pengolahan.citra.digitrecognizer.MatrixModel;
import praktikum.pengolahan.citra.digitrecognizer.SimilarityProcess;
import praktikum.pengolahan.citra.digitrecognizer.pojos.SimilarityHolder;
import praktikum.pengolahan.citra.handleres.RealImageCoordinat;
import praktikum.pengolahan.citra.processors.Editor;
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
import java.util.stream.Collectors;


public class MainController implements Initializable, EventHandler<MouseEvent> {

  private static final String TAG = MainController.class.getSimpleName();

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
      ivReadDigit,
      ivBlackWhiteEffect,
      ivBrightnessEffect,
      ivConvertToGreen;

  private Stage histogramStage, brightnessStage;
  private HistogramController histogramController;
  private DigitController digitController;
  private BrightnessSettingController brightnessSettingController;
  private Image originalImage;
  private boolean ifPictureExists;
  private List<ImageView> effectThumbs = new ArrayList<>();
  private HashMap<String, ApplyEffect> effects = new HashMap<>();
  private Thread thread;
  private Editor editor;
  private File inputDigitImage;
  private double[] newComerMatrix;
  private SimilarityProcess similarityProcess;
  private Stage digitStage;

  public void initialize(URL location, ResourceBundle resources) {
    lblTitle.setText(Constants.APP_NAME);
    initEffects();
    setDefaultImage();
    initDialogs();
    similarityProcess = new SimilarityProcess();
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

    FXMLLoader digitLoader = Utils.loader(Utils.getUiResource("viewdigits.fxml"));
    digitStage = Utils.makeDialogStage(digitLoader, "Digit", App.APP_STAGE);
    digitController = digitLoader.getController();
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
    registerEffects(ivReadDigit, () -> readDigit());
    registerEffects(ivBlackWhiteEffect, () -> applyBlackWhite());
    registerEffects(ivBrightnessEffect, () -> showBrightnessSetting());
    registerEffects(ivConvertToGreen, () -> applyToGreen());
  }

  private void applyToGreen() {
    editor.addGreen();
    ivImageToEdit.setImage(editor.getEditedImage());
  }

  private void applyOriginal() {
    editor.returnToOriginalState();
    ivImageToEdit.setImage(editor.getEditedImage());
  }

  private void applyGray() {
    editor.addGray();
    ivImageToEdit.setImage(editor.getEditedImage());
  }

  private void readDigit() {
    System.out.println("Reading digit!");
    Matrix newComer = new Matrix(this.newComerMatrix, 1);
    List<SimilarityHolder> sorted = similarityProcess.findSimilarityAndSort(newComer);
    double highestScore = sorted.get(0).getScore();
    int gottenNumber = sorted.get(0).getNumbLabel();
    List<Integer> otherSimilarNumber = sorted.stream()
        .filter(similarityHolder ->
            (highestScore - similarityHolder.getScore() < 0.001d)
                && similarityHolder.getNumbLabel() != gottenNumber)
        .map(SimilarityHolder::getNumbLabel).collect(Collectors.toList());

    System.out.println("Similarities");
    sorted.stream().forEach(similarityHolder ->
        System.out.println(String
            .format("Number: %d, Score: %.9f. %f",
                similarityHolder.getNumbLabel(),
                similarityHolder.getScore(),
                highestScore - similarityHolder.getScore())));

    digitController.getLblDigit().setText(String.valueOf(gottenNumber));
    digitController.getLblScore().setText(String.format("Score: %.10f", highestScore));
    digitController.getLblOtherDigits().setText(StringUtils.join(otherSimilarNumber.toArray(), ", "));
    digitStage.showAndWait();
  }

  private void applyBlackWhite() {
    editor.addBlackWhite();
    ivImageToEdit.setImage(editor.getEditedImage());
  }

  private void showBrightnessSetting() {
    brightnessSettingController.setIvPreviewImage(editor.getPreviewImage());
    brightnessSettingController.setColorPreview(editor.getColorPreviewState());
    System.out.println(brightnessSettingController.swap);
    brightnessStage.showAndWait();
  }

  private ApplyWithParams applyBrightness() {
    return (param) -> {
      Log.i(TAG, param + "");
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
    try {
      File file = FileUtils.showChoseImageFileDialog();
      setIvImages(file);
      ifPictureExists = true;
      toggleEffectContainer();
      this.inputDigitImage = file;
      newComerMatrix = MatrixModel.flatten(ImageProcessor.imageToColorsDoubled(inputDigitImage));
      editor = new Editor(file, waitThen());
//      thread = new Thread(editor);
      editor.run();
//      thread.start();
    } catch (Exception e) {
      Log.i(TAG, "Ignore all exception");
    }
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
      int red = (int) (color.getRed() * 255);
      int green = (int) (color.getGreen() * 255);
      int blue = (int) (color.getBlue() * 255);

      String hexColor = String.format("#%s%s%s", Integer.toHexString(red), Integer.toHexString(green), Integer.toHexString(blue));
      String coordinatAndColorDetail = String.format("(%d, %d), (%d, %d, %d)", x, y, red, green, blue);
      String backgroundColor = String.format("-fx-background-color: %s", hexColor);

      lblCoordinat.setText(coordinatAndColorDetail);
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
