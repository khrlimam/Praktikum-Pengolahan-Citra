package praktikum.pengolahan.citra.handleres;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import praktikum.pengolahan.citra.contracts.ReactTo;

public class RealImageCoordinat {

  public static void getRealCoordinat(MouseEvent event, ReactTo reactTo) {
    double x = event.getX();
    double y = event.getY();
    ImageView imageViewSource = (ImageView) event.getSource();
    Image imageSource = imageViewSource.getImage();

    Bounds bounds = imageViewSource.getLayoutBounds();
    double xScale = bounds.getWidth() / imageSource.getWidth();
    double yScale = bounds.getHeight() / imageSource.getHeight();

    x /= xScale;
    y /= yScale;

    int realX = (int) x;
    int realY = (int) y;
    imageViewSource.getX();

    reactTo.pixelOn(realX, realY);
  }
}
