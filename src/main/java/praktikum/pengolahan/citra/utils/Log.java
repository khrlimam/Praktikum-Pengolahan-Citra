package praktikum.pengolahan.citra.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

  public static void i(String TAG, String message) {
    Logger.getLogger(TAG).log(Level.INFO, message);
  }

  public static void w(String TAG, String message) {
    Logger.getLogger(TAG).log(Level.WARNING, message);
  }
}