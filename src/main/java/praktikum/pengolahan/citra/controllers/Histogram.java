package praktikum.pengolahan.citra.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;

import java.net.URL;
import java.util.ResourceBundle;

public class Histogram implements Initializable {

  @FXML
  BarChart<Number, Number> bcHistogram;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public BarChart<Number, Number> getBcHistogram() {
    return this.bcHistogram;
  }
}
