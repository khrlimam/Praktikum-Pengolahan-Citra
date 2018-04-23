package praktikum.pengolahan.citra.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import praktikum.pengolahan.citra.custom.HoveredLineChartNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Histogram implements Initializable {

  @FXML
  NumberAxis xAxis, yAxis;

  @FXML
  LineChart<Number, Number> chart;

  private XYChart<Number, Number> chartSeries;
  private int[][][] colors;
  private ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void populateChart() {
    chart.setData(getData());
  }

  private ObservableList<XYChart.Series<Number, Number>> getData() {
    System.out.println(getReds());
    System.out.println(getGreens());
    System.out.println(getBlues());
    XYChart.Series<Number, Number> redScaleCount = new XYChart.Series<>();
    XYChart.Series<Number, Number> greenScaleCount = new XYChart.Series<>();
    XYChart.Series<Number, Number> blueScaleCount = new XYChart.Series<>();

    for (int x = 0; x < 256; x++) {
      int countRed = countIntegerOccurences(getReds(), x);
      XYChart.Data redCount = new XYChart.Data(x, countRed);
      redCount.setNode(new HoveredLineChartNode(getPopup(x, countRed)));
      redScaleCount.getData().add(redCount);

      int countGreen = countIntegerOccurences(getGreens(), x);
      XYChart.Data greenCount = new XYChart.Data(x, countGreen);
      greenCount.setNode(new HoveredLineChartNode(getPopup(x, countGreen)));
      redScaleCount.getData().add(greenCount);

      int countBlue = countIntegerOccurences(getBlues(), x);
      XYChart.Data blueCount = new XYChart.Data(x, countBlue);
      blueCount.setNode(new HoveredLineChartNode(getPopup(x, countBlue)));
      redScaleCount.getData().add(blueCount);
    }

    data.addAll(redScaleCount, greenScaleCount, blueScaleCount);

    redScaleCount.setName("Red Channel");
    greenScaleCount.setName("Green Channel");
    blueScaleCount.setName("Blue Channel");

    return data;
  }

  private String getPopup(int x, int y) {
    return String.format("Warna: %d\nJumlah: %d", x, y);
  }

  private List<Integer> getReds() {
    List<Integer> reds = new ArrayList<>();
    Arrays.stream(colors).forEach(ints -> Arrays.stream(ints).forEach(ints1 -> reds.add(ints1[0])));
    return reds;
  }

  private List<Integer> getGreens() {
    List<Integer> greens = new ArrayList<>();
    Arrays.stream(colors).forEach(ints -> Arrays.stream(ints).forEach(ints1 -> greens.add(ints1[1])));
    return greens;
  }

  private List<Integer> getBlues() {
    List<Integer> blues = new ArrayList<>();
    Arrays.stream(colors).forEach(ints -> Arrays.stream(ints).forEach(ints1 -> blues.add(ints1[2])));
    return blues;
  }

  private int countIntegerOccurences(List<Integer> data, int count) {
    return data.parallelStream().filter(integer -> integer == count).collect(Collectors.toList()).size();
  }

  public void setColors(int[][][] colors) {
    this.colors = colors;
  }
}
