package praktikum.pengolahan.citra.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import praktikum.pengolahan.citra.contracts.ApplyWithParams;
import praktikum.pengolahan.citra.processors.Effects;

import java.net.URL;
import java.util.ResourceBundle;

import static praktikum.pengolahan.citra.processors.ImageProcessor.colorsToImage;

public class BrightnessSettingController implements Initializable {

  @FXML
  Label lblBetaLevel;

  @FXML
  ImageView ivPreview;

  @FXML
  Slider sBetaLevel;

  private ApplyWithParams applyWithParams;
  private int[][][] colorPreview;
  int beta = 0, swap = 0, diff;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    sBetaLevel.valueProperty().addListener((observable, oldValue, newValue) -> {
      int newValueInt = newValue.intValue();
      System.out.println("nw "+newValueInt);
      System.out.println("sw "+swap);
      diff = newValueInt - swap;
      swap = newValueInt;
      beta = diff;
      lblBetaLevel.setText(String.valueOf(newValueInt));
      ivPreview.setImage(colorsToImage(Effects.addBrightness(beta, colorPreview)));
    });
  }

  void setIvPreviewImage(Image image) {
    this.ivPreview.setImage(image);
  }

  void setColorPreview(int[][][] colorPreview) {
    this.colorPreview = colorPreview;
  }

  void setApplyWithParams(ApplyWithParams applyWithParams) {
    this.applyWithParams = applyWithParams;
  }

  @FXML
  private void applyBrightness() {
    applyWithParams.apply(beta);
  }
}
