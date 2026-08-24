package com.aetherteam.cumulus.client.gui.screen;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.jetbrains.annotations.Nullable;

public class DynamicMenuButton extends BuilderMadeButton {
   private final int originX;
   private List<ConfigValue<Boolean>> displayConfigs;
   private List<ConfigValue<Boolean>> offsetConfigs;
   public boolean enabled = true;

   public DynamicMenuButton(Builder builder) {
      super(builder.createNarration(DEFAULT_NARRATION));
      this.originX = this.getX();
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      if (this.shouldRender()) {
         this.enabled = true;
         this.setX(this.getOriginX() + this.gatherOffsets(this.offsetConfigs));
         super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
      } else {
         this.enabled = false;
      }

      this.active = this.enabled;
      this.visible = this.enabled;
   }

   private boolean shouldRender() {
      for (ConfigValue<Boolean> value : this.displayConfigs) {
         if (!(Boolean)value.get()) {
            return false;
         }
      }

      return true;
   }

   private int gatherOffsets(@Nullable List<ConfigValue<Boolean>> configs) {
      int offset = 0;
      if (configs != null) {
         for (ConfigValue<Boolean> value : configs) {
            if ((Boolean)value.get()) {
               offset -= 24;
            }
         }
      }

      return offset;
   }

   @SafeVarargs
   public final void setDisplayConfigs(ConfigValue<Boolean>... displayConfigs) {
      this.displayConfigs = List.of(displayConfigs);
   }

   @SafeVarargs
   public final void setOffsetConfigs(ConfigValue<Boolean>... offsetConfigs) {
      this.offsetConfigs = List.of(offsetConfigs);
   }

   public int getOriginX() {
      return this.originX;
   }
}
