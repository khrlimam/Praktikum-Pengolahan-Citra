package praktikum.pengolahan.citra.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import praktikum.pengolahan.citra.Statics;
import praktikum.pengolahan.citra.contracts.ApplyEffect;
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
  Label lblTitle;

  @FXML
  HBox hbEffectContainer;

  @FXML
  ImageView
      ivImageToEdit,
      ivGreyEffect,
      ivContrastEffect,
      ivBlackWhiteEffect,
      ivBrightnessEffect;

  private File imageFileToEdit;
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
    initEvent();
  }

  private void appendEffect(ImageView thumbnail, ApplyEffect effectToApply) {
    effectThumbs.add(thumbnail);
    effects.put(thumbnail.getId(), effectToApply);
  }

  private void initEffects() {
    appendEffect(ivGreyEffect, () -> applyGrey());
    appendEffect(ivContrastEffect, () -> applyContrast());
    appendEffect(ivBlackWhiteEffect, () -> applyBlackWhite());
    appendEffect(ivBrightnessEffect, () -> applyBrightness());
  }

  private void initEvent() {
    ivGreyEffect.setOnMouseClicked(this);
    ivContrastEffect.setOnMouseClicked(this);
    ivBlackWhiteEffect.setOnMouseClicked(this);
    ivBrightnessEffect.setOnMouseClicked(this);
  }

  private void applyGrey() {
    int[][][] img = imageToNDArray.getImg();
    int width = (int) ivImageToEdit.getImage().getWidth();
    int height = (int) ivImageToEdit.getImage().getHeight();

    WritableImage writableImage = new WritableImage(width, height);
    PixelWriter pixelWriter = writableImage.getPixelWriter();

    int beta = 2;
    int[][][] addedContrast = ImageProcessor.addContrast(beta, img);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Color color = new Color(
            addedContrast[y][x][0] / 255d,
            addedContrast[y][x][1] / 255d,
            addedContrast[y][x][2] / 255d,
            addedContrast[y][x][3]);
        pixelWriter.setColor(x, y, color);
      }
      ivImageToEdit.setImage(writableImage);
    }

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
  }


  @FXML
  private void choosePicture() {
    File file = FileUtils.showChoseImageFileDialog();
    setIvImages(file);
    ifPictureExists = true;
    toggleEffectContainer();
    imageToNDArray = new ImageToNDArray(file, () -> {
      System.out.println("updating ui");
    });
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
    Dragboard dragboard = event.getDragboard();
    File firstDraggedFile = dragboard.getFiles().get(0);
    setIvImages(firstDraggedFile);
    ifPictureExists = true;
    toggleEffectContainer();
    imageToNDArray = new ImageToNDArray(firstDraggedFile, () -> {
      System.out.println("updating ui");
    });
//    imageToNDArray.setUpdateUI(() -> {
//      System.out.println("Updating UI");
//    });
    thread = new Thread(imageToNDArray);
    thread.start();
  }

  private void setIvImages(File imageFile) {
    this.imageFileToEdit = imageFile;
    try {
      ivImageToEdit.setImage(new Image(new FileInputStream(imageFile)));
      setThumbnailEffectImage(imageFile);
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

}
