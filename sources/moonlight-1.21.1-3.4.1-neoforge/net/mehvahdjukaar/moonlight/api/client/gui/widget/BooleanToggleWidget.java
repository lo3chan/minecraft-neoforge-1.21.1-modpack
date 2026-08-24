package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BooleanToggleWidget extends AbstractButton {
   private static final int ICON_SIZE = 12;
   private static final int ITEM_SIZE = 16;
   private static final int ITEM_GAP = 2;
   private final ResourceLocation onIcon;
   private final ResourceLocation offIcon;
   private boolean value;
   private final Consumer<Boolean> onChange;
   @Nullable
   private final BooleanToggleWidget.ExtraIcon iconRenderer;

   public BooleanToggleWidget(int width, int height, ResourceLocation onIcon, ResourceLocation offIcon, boolean initial, Consumer<Boolean> onChange) {
      this(width, height, onIcon, offIcon, initial, onChange, null);
   }

   public BooleanToggleWidget(
      int width,
      int height,
      ResourceLocation onIcon,
      ResourceLocation offIcon,
      boolean initial,
      Consumer<Boolean> onChange,
      @Nullable BooleanToggleWidget.ExtraIcon iconRenderer
   ) {
      super(0, 0, width, height, Component.empty());
      this.onIcon = onIcon;
      this.offIcon = offIcon;
      this.value = initial;
      this.onChange = onChange;
      this.iconRenderer = iconRenderer;
   }

   public void set(boolean v) {
      this.value = v;
   }

   public void onPress() {
      this.value = !this.value;
      this.onChange.accept(this.value);
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderWidget(graphics, mouseX, mouseY, partialTick);
      int cy = this.getY() + this.getHeight() / 2;
      boolean hasIcon = this.iconRenderer != null && this.iconRenderer.available();
      int groupWidth = hasIcon ? 30 : 12;
      int x = this.getX() + (this.getWidth() - groupWidth) / 2;
      if (hasIcon) {
         this.iconRenderer.render(graphics, x, cy - 8, 16, this.isHovered(), this.active);
         x += 18;
      }

      graphics.blitSprite(this.value ? this.onIcon : this.offIcon, x, cy - 6, 12, 12);
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
      this.defaultButtonNarrationText(narrationElementOutput);
      narrationElementOutput.add(NarratedElementType.USAGE, this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
   }

   public interface ExtraIcon {
      boolean available();

      void render(GuiGraphics var1, int var2, int var3, int var4, boolean var5, boolean var6);
   }
}
