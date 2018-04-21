package praktikum.pengolahan.citra.utils;

import praktikum.pengolahan.citra.App;

import java.net.URL;

public class Utils {

  public static URL getUiResource(String filename) {
    return getAppResource(String.format("uis/%s", filename));
  }

  public static URL getImageResource(String imageName) {
    return getAppResource(String.format("img/%s", imageName));
  }

  public static URL getAppResource(String name) {
    return App.class.getClassLoader().getResource(name);
  }

}
