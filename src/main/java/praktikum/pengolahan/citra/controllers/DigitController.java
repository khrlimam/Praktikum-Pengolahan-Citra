package praktikum.pengolahan.citra.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DigitController implements Initializable {

  @FXML
  private Label lblScore, lblDigit, lblOtherDigits;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public Label getLblScore() {
    return lblScore;
  }

  public void setLblScore(Label lblScore) {
    this.lblScore = lblScore;
  }

  public Label getLblDigit() {
    return lblDigit;
  }

  public void setLblDigit(Label lblDigit) {
    this.lblDigit = lblDigit;
  }

  public Label getLblOtherDigits() {
    return lblOtherDigits;
  }

  public void setLblOtherDigits(Label lblOtherDigits) {
    this.lblOtherDigits = lblOtherDigits;
  }
}
