package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.types.enums.EConfigCommentTextPosition;
import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigUIComment extends AbstractConfigBase<String> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public String parentConfigPath = null;
   @Nullable
   public EConfigCommentTextPosition textPosition = null;

   public ConfigUIComment(String parentConfigPath, @Nullable EConfigCommentTextPosition textPosition) {
      super(EConfigEntryAppearance.ONLY_IN_GUI, "");
      this.parentConfigPath = parentConfigPath;
      this.textPosition = textPosition;
   }

   @Override
   public void setAppearance(EConfigEntryAppearance newAppearance) {
   }

   public void set(String newValue) {
   }

   public static class Builder extends AbstractConfigBase.Builder<String, ConfigUIComment.Builder> {
      public String tempParentConfigPath = null;
      @Nullable
      public EConfigCommentTextPosition tempTextPosition = null;

      @Deprecated
      public ConfigUIComment.Builder setAppearance(EConfigEntryAppearance newAppearance) {
         return this;
      }

      @Deprecated
      public ConfigUIComment.Builder set(String newValue) {
         return this;
      }

      public ConfigUIComment.Builder setParentConfigClass(@NotNull Class<?> parentConfigClass) {
         String packageName = parentConfigClass.getPackage().getName();
         String fullName = parentConfigClass.getName();

         try {
            String configPath = fullName.substring(packageName.length() + 1 + Config.class.getSimpleName().length() + 1);
            this.tempParentConfigPath = convertPackageNameToLangPath(configPath);
         } catch (Exception var5) {
            this.tempParentConfigPath = parentConfigClass.getSimpleName();
            ConfigUIComment.LOGGER
               .warn(
                  "Failed to parse config class: [" + fullName + "], error: [" + var5.getMessage() + "], defaulting to: [" + this.tempParentConfigPath + "].",
                  var5
               );
         }

         return this;
      }

      public static String convertPackageNameToLangPath(String input) {
         StringBuilder result = new StringBuilder(input.length());

         for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (i == 0) {
               result.append(Character.toLowerCase(ch));
            } else if (ch == '$') {
               result.append('.');
            } else {
               char lastCh = input.charAt(i - 1);
               if (lastCh == '$') {
                  result.append(Character.toLowerCase(ch));
               } else {
                  result.append(ch);
               }
            }
         }

         return result.toString();
      }

      public ConfigUIComment.Builder setTextPosition(EConfigCommentTextPosition textPosition) {
         this.tempTextPosition = textPosition;
         return this;
      }

      public ConfigUIComment build() {
         return new ConfigUIComment(this.tempParentConfigPath, this.tempTextPosition);
      }
   }
}
