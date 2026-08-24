package traben.entity_texture_features.config;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;

public abstract class ETFConfigWarning {
   public abstract boolean isConditionMet();

   public abstract String getTitle();

   public abstract String getSubTitle();

   public abstract String getID();

   public abstract void testWarningAndApplyFixIfEnabled();

   protected boolean isEnabled() {
      return this.isConditionMet() && !ETFConfigScreenWarnings.getIgnoredWarnings().contains(this.getID());
   }

   public abstract boolean doesShowDisableButton();

   public static class Simple extends ETFConfigWarning {
      public final String TITLE_TRANSLATION_KEY;
      public final String SUB_TITLE_TRANSLATION_KEY;
      public final String ID;
      private final Supplier<Boolean> CONDITION;
      @Nullable
      private final Runnable FIX;

      public Simple(
         String id,
         Supplier<Boolean> condition,
         @Translatable String title_translation_key,
         @Translatable String sub_title_translation_key,
         @Nullable Runnable fix
      ) {
         this.ID = id;
         this.CONDITION = condition;
         this.TITLE_TRANSLATION_KEY = title_translation_key;
         this.SUB_TITLE_TRANSLATION_KEY = sub_title_translation_key;
         this.FIX = fix;
      }

      public Simple(
         String id, String modName, @Translatable String title_translation_key, @Translatable String sub_title_translation_key, @Nullable Runnable fix
      ) {
         this.ID = id;
         this.CONDITION = () -> ETF.isThisModLoaded(modName);
         this.TITLE_TRANSLATION_KEY = title_translation_key;
         this.SUB_TITLE_TRANSLATION_KEY = sub_title_translation_key;
         this.FIX = fix;
      }

      @Override
      public boolean isConditionMet() {
         return this.CONDITION.get();
      }

      @Override
      public String getTitle() {
         return this.TITLE_TRANSLATION_KEY;
      }

      @Override
      public String getSubTitle() {
         return this.SUB_TITLE_TRANSLATION_KEY;
      }

      @Override
      public String getID() {
         return this.ID;
      }

      @Override
      public void testWarningAndApplyFixIfEnabled() {
         if (this.FIX != null && this.isEnabled()) {
            this.FIX.run();
         }
      }

      @Override
      public boolean doesShowDisableButton() {
         return this.FIX != null;
      }
   }
}
