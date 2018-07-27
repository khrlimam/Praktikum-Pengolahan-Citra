package praktikum.pengolahan.citra.digitrecognizer.pojos;

import java.io.InputStream;

public class ModelHolder {
  private int label;
  private InputStream image;

  public int getLabel() {
    return label;
  }

  public void setLabel(int label) {
    this.label = label;
  }

  public InputStream getImage() {
    return image;
  }

  public void setImage(InputStream image) {
    this.image = image;
  }

  public ModelHolder(int label, InputStream image) {

    this.label = label;
    this.image = image;
  }
}
