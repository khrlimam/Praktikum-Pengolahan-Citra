package praktikum.pengolahan.citra.controllers;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import praktikum.pengolahan.citra.contracts.LineNodeListener;
import praktikum.pengolahan.citra.custom.HoveredLineChartNode;
import praktikum.pengolahan.citra.pojos.ColorHistogram;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistogramController implements Initializable {

  @FXML
  NumberAxis yAxis;

  @FXML
  CategoryAxis xAxis;

  @FXML
  BarChart<String, Number> chart;

  @FXML
  Label lblHistogram;

  private List<Integer> reds, greens, blues;


  private XYChart<Number, Number> chartSeries;
  private int[][][] colors;
  private ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
  private XYChart.Series<String, Number> redScaleCount;
  private XYChart.Series<String, Number> greenScaleCount;
  private XYChart.Series<String, Number> blueScaleCount;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    xAxis.setAutoRanging(false);
//    xAxis.setTickUnit(8d);
//    xAxis.setLowerBound(-10d);
//    xAxis.setUpperBound(265d);

    redScaleCount = new XYChart.Series<>();
    greenScaleCount = new XYChart.Series<>();
    blueScaleCount = new XYChart.Series<>();

    redScaleCount.setName("Red Channel");
    greenScaleCount.setName("Green Channel");
    blueScaleCount.setName("Blue Channel");

    data.add(redScaleCount);
    data.add(greenScaleCount);
    data.add(blueScaleCount);

    chart.setData(data);
  }

  public void drawChart(Observable<ColorHistogram> colorObservable) {
    colorObservable.subscribe(thisColor -> {
      XYChart.Data redCount = new XYChart.Data(thisColor.getBit() + "", thisColor.getCountRed());
      redCount.setNode(new HoveredLineChartNode(showDetail("Red", thisColor.getBit(), thisColor.getCountRed())));
      redScaleCount.getData().add(redCount);

      XYChart.Data greenCount = new XYChart.Data(thisColor.getBit() + "", thisColor.getCountGreen());
      greenCount.setNode(new HoveredLineChartNode(showDetail("Green", thisColor.getBit(), thisColor.getCountGreen())));
      greenScaleCount.getData().add(greenCount);

      XYChart.Data blueCount = new XYChart.Data(thisColor.getBit() + "", thisColor.getCountBlue());
      blueCount.setNode(new HoveredLineChartNode(showDetail("Blue", thisColor.getBit(), thisColor.getCountBlue())));
      blueScaleCount.getData().add(blueCount);
    });
  }

  public Observable<ColorHistogram> colorHistogramObservable() {
    return Observable.create(observableEmitter -> emitColor(observableEmitter, this.colors));
  }

  private void emitColor(ObservableEmitter<ColorHistogram> observableEmitter, int[][][] colors) {
    for (int bit = 0; bit < 9; bit++) {
      observableEmitter.onNext(fetchHistogramFor(bit));
    }
  }

  private ColorHistogram fetchHistogramFor(int bit) {
    ColorHistogram colorHistogram = new ColorHistogram();
    colorHistogram.setBit(bit);
    colorHistogram.setCountRed(countIntegerOccurences(this.reds, bit));
    colorHistogram.setCountGreen(countIntegerOccurences(this.greens, bit));
    colorHistogram.setCountBlue(countIntegerOccurences(this.blues, bit));
    return colorHistogram;
  }

  private LineNodeListenerHolder showDetail(String color, int x, int y) {
    return new LineNodeListenerHolder(color, x, y);
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
    this.reds = getReds();
    this.greens = getGreens();
    this.blues = getBlues();
  }

  private class LineNodeListenerHolder implements LineNodeListener {
    private String colorChannel;
    private int bit, count;

    public LineNodeListenerHolder(String colorChannel, int bit, int count) {
      this.colorChannel = colorChannel;
      this.bit = bit;
      this.count = count;
    }

    public String getColorChannel() {
      return this.colorChannel;
    }

    public int getBit() {
      return bit;
    }

    public int getCount() {
      return count;
    }

    public String getLabel() {
      return String.format("Channel: %s, Bit: %s, Count: %s",
          getColorChannel(),
          getBit(),
          getCount());
    }

    @Override
    public void onMouseEntered() {
      lblHistogram.setVisible(true);
      lblHistogram.setText(getLabel());
    }

    @Override
    public void onMouseExited() {
      lblHistogram.setVisible(false);
      lblHistogram.setText("");
    }
  }

}
