package praktikum.pengolahan.citra.custom;

import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import praktikum.pengolahan.citra.contracts.LineNodeListener;

public class HoveredLineChartNode extends Pane {
  private LineNodeListener lineNodeListener;

  public HoveredLineChartNode(LineNodeListener lineNodeListener) {
    this.lineNodeListener = lineNodeListener;
    setOnMouseEntered(mouseEvent -> {
      setCursor(Cursor.CROSSHAIR);
      lineNodeListener.onMouseEntered();
    });
    setOnMouseExited(mouseEvent -> lineNodeListener.onMouseExited());
  }
}