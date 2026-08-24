package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import traben.tconfig.gui.TConfigScreenList;

public class TConfigEntryCategory extends TConfigEntry {
   private final LinkedHashMap<String, TConfigEntry> options = new LinkedHashMap<>();
   private final String translationKey;
   protected boolean fullWidthBackgroundEvenIfSmaller = false;
   private TConfigScreenList screen = null;
   private Tooltip emptyTooltip = Tooltip.create(Component.translatable("config.entity_features.empty"));
   private TConfigScreenList.Align align = TConfigScreenList.Align.CENTER;
   private TConfigScreenList.Renderable renderFeature = null;

   public TConfigEntryCategory(@Translatable String text, @Translatable String tooltip) {
      super(text, tooltip);
      this.translationKey = text;
   }

   public TConfigEntryCategory(@Translatable String text) {
      super(text, null);
      this.translationKey = text;
   }

   public void setAlign(TConfigScreenList.Align align) {
      this.align = align;
   }

   public LinkedHashMap<String, TConfigEntry> getOptions() {
      return this.options;
   }

   public TConfigScreenList getScreen() {
      if (this.screen == null) {
         this.screen = new TConfigScreenList(
            this.translationKey,
            Minecraft.getInstance().screen,
            this.options.values().toArray(new TConfigEntry[0]),
            this::setValuesToDefault,
            this::resetValuesToInitial,
            this.align
         );
         this.screen.setRenderFeature(this.renderFeature);
         if (this.fullWidthBackgroundEvenIfSmaller) {
            this.screen.setWidgetBackgroundToFullWidth();
         }
      }

      return this.screen;
   }

   @Override
   public AbstractWidget getWidget(int x, int y, int width, int height) {
      return new TConfigEntryCategory.CategoryButton(x, y, width, height, this.getText(), button -> Minecraft.getInstance().setScreen(this.getScreen()));
   }

   @Override
   public boolean saveValuesToConfig() {
      boolean found = false;

      for (TConfigEntry option : this.options.values()) {
         found |= option.saveValuesToConfig();
      }

      return found;
   }

   public void setWidgetBackgroundToFullWidth() {
      this.fullWidthBackgroundEvenIfSmaller = true;
   }

   @Override
   public void setValuesToDefault() {
      for (TConfigEntry option : this.options.values()) {
         option.setValuesToDefault();
      }
   }

   @Override
   public void resetValuesToInitial() {
      for (TConfigEntry option : this.options.values()) {
         option.resetValuesToInitial();
      }
   }

   public TConfigEntryCategory add(TConfigEntry... option) {
      for (TConfigEntry tConfigEntry : option) {
         this.add(tConfigEntry);
      }

      return this;
   }

   public TConfigEntryCategory addAll(Collection<TConfigEntry> option) {
      if (option != null) {
         option.forEach(this::add);
      }

      return this;
   }

   public TConfigEntryCategory add(TConfigEntry option) {
      if (option == null) {
         return this;
      } else if (option instanceof TConfigEntryCategory category) {
         return this.addOrMerge(category);
      } else {
         this.options.put(option.getText().getString(), option);
         return this;
      }
   }

   private TConfigEntryCategory addOrMerge(TConfigEntryCategory category) {
      String categoryKey = category.getText().getString();
      if (this.options.containsKey(categoryKey) && this.options.get(categoryKey) instanceof TConfigEntryCategory existingCategory) {
         category.options.values().forEach(existingCategory::add);
      } else {
         this.options.put(categoryKey, category);
      }

      return this;
   }

   @Override
   boolean hasChangedFromInitial() {
      return this.options.values().stream().anyMatch(TConfigEntry::hasChangedFromInitial);
   }

   public TConfigEntryCategory setEmptyTooltip(@NotNull @Translatable String emptyTooltipKey) {
      this.emptyTooltip = Tooltip.create(Component.translatable(emptyTooltipKey));
      return this;
   }

   public void setRenderFeature(TConfigScreenList.Renderable renderFeature) {
      this.renderFeature = renderFeature;
   }

   private class CategoryButton extends Button {
      protected CategoryButton(final int x, final int y, final int width, final int height, final Component message, final OnPress onPress) {
         super(x, y, width, height, message, onPress, Supplier::get);
         this.active = !TConfigEntryCategory.this.options.isEmpty();
         if (!this.active) {
            this.setTooltip(TConfigEntryCategory.this.emptyTooltip);
         }
      }

      @NotNull
      public Component getMessage() {
         return TConfigEntryCategory.this.hasChangedFromInitial() ? Component.nullToEmpty("§a" + super.getMessage().getString()) : super.getMessage();
      }
   }

   public static class Empty extends TConfigEntryCategory {
      public Empty() {
         super("", null);
      }

      @Override
      public AbstractWidget getWidget(int x, int y, int width, int height) {
         return null;
      }
   }
}
