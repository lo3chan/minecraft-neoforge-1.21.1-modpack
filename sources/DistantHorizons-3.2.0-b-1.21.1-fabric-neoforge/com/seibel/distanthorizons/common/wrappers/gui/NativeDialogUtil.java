package com.seibel.distanthorizons.common.wrappers.gui;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class NativeDialogUtil {
   public static void showDialog(String title, String message, String dialogType, String iconType) {
      String unsafeCharsRegex = "['\"`]";
      title = title.replaceAll(unsafeCharsRegex, "");
      message = message.replaceAll(unsafeCharsRegex, "");
      TinyFileDialogs.tinyfd_messageBox(title, message, dialogType, iconType, false);
   }
}
