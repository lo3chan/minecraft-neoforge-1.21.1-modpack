package top.theillusivec4.curios.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import top.theillusivec4.curios.common.network.client.CPacketToggleCosmetics;

public class CosmeticButton extends ImageButton {
   public static final WidgetSprites OFF = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("curios", "cosmetic_off"), ResourceLocation.fromNamespaceAndPath("curios", "cosmetic_off_highlighted")
   );
   public static final WidgetSprites ON = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("curios", "cosmetic_on"), ResourceLocation.fromNamespaceAndPath("curios", "cosmetic_on_highlighted")
   );
   private final CuriosScreen parentGui;

   CosmeticButton(CuriosScreen parentGui, int xIn, int yIn, int widthIn, int heightIn) {
      super(xIn, yIn, widthIn, heightIn, OFF, button -> {
         ((CuriosContainer)parentGui.getMenu()).toggleCosmetics();
         PacketDistributor.sendToServer(new CPacketToggleCosmetics(((CuriosContainer)parentGui.getMenu()).containerId), new CustomPacketPayload[0]);
      });
      this.parentGui = parentGui;
      this.setTooltip(Tooltip.create(Component.translatable("gui.curios.toggle.cosmetics")));
   }

   public void renderWidget(@Nonnull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
      WidgetSprites sprites1;
      if (((CuriosContainer)this.parentGui.getMenu()).isViewingCosmetics) {
         sprites1 = ON;
      } else {
         sprites1 = OFF;
      }

      this.setX(this.parentGui.getGuiLeft() - 27);
      this.setY(this.parentGui.getGuiTop() - 18);
      ResourceLocation resourcelocation = sprites1.get(this.isActive(), this.isHoveredOrFocused());
      guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
   }
}
