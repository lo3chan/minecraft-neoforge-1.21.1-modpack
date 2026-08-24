package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class TConfigEntryCustomScreenOpener extends TConfigEntry {
   private final Supplier<Screen> screenSupplier;
   private final Supplier<Boolean> savedSupplier;
   private final Runnable setValuesDefault;
   private final Runnable resetValuesToInitial;
   private final boolean screenIsSingleton;
   private final Button button;
   private Screen screen = null;

   public TConfigEntryCustomScreenOpener(
      @Translatable String text,
      @Translatable String tooltip,
      Supplier<Screen> screenSupplier,
      Supplier<Boolean> savedSupplier,
      Runnable setValuesDefault,
      Runnable resetValuesToInitial,
      boolean screenIsSingleton
   ) {
      super(text, tooltip);
      this.screenSupplier = screenSupplier;
      this.savedSupplier = savedSupplier;
      this.screenIsSingleton = screenIsSingleton;
      this.setValuesDefault = setValuesDefault;
      this.resetValuesToInitial = resetValuesToInitial;
      this.button = Button.builder(this.getText(), button -> Minecraft.getInstance().setScreen(this.getScreen()))
         .bounds(0, 0, 0, 0)
         .tooltip(this.getTooltip())
         .build();
   }

   public TConfigEntryCustomScreenOpener(
      @Translatable String text,
      Supplier<Screen> screenSupplier,
      Supplier<Boolean> savedSupplier,
      Runnable setValuesDefault,
      Runnable resetValuesToInitial,
      boolean screenIsSingleton
   ) {
      this(text, null, screenSupplier, savedSupplier, setValuesDefault, resetValuesToInitial, screenIsSingleton);
   }

   public TConfigEntryCustomScreenOpener(@Translatable String text, @Translatable String tooltip, Supplier<Screen> screenSupplier, boolean screenIsSingleton) {
      this(text, tooltip, screenSupplier, () -> false, () -> {}, () -> {}, screenIsSingleton);
   }

   public TConfigEntryCustomScreenOpener(@Translatable String text, Supplier<Screen> screenSupplier, boolean screenIsSingleton) {
      this(text, null, screenSupplier, screenIsSingleton);
   }

   private Screen getScreen() {
      if (!this.screenIsSingleton) {
         return this.screenSupplier.get();
      } else {
         if (this.screen == null) {
            this.screen = this.screenSupplier.get();
         }

         return this.screen;
      }
   }

   @Override
   public AbstractWidget getWidget(int x, int y, int width, int height) {
      this.button.setRectangle(width, height, x, y);
      return this.button;
   }

   @Override
   boolean saveValuesToConfig() {
      return this.savedSupplier.get();
   }

   @Override
   void setValuesToDefault() {
      this.setValuesDefault.run();
   }

   @Override
   void resetValuesToInitial() {
      this.resetValuesToInitial.run();
   }

   @Override
   boolean hasChangedFromInitial() {
      return false;
   }
}
