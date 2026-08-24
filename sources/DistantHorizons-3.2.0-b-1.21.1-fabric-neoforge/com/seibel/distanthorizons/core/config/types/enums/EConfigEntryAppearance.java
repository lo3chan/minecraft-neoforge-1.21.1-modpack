package com.seibel.distanthorizons.core.config.types.enums;

public enum EConfigEntryAppearance {
   ALL(true, true),
   ONLY_IN_GUI(true, false),
   ONLY_IN_FILE(false, true),
   ONLY_IN_API(false, false);

   public final boolean showInGui;
   public final boolean showInFile;

   private EConfigEntryAppearance(boolean showInGui, boolean showInFile) {
      this.showInGui = showInGui;
      this.showInFile = showInFile;
   }
}
