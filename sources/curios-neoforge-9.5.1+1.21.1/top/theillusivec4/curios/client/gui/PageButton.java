package top.theillusivec4.curios.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import top.theillusivec4.curios.common.network.client.CPacketPage;

public class PageButton extends Button {
   private final CuriosScreen parentGui;
   private final PageButton.Type type;
   private static final ResourceLocation CURIO_INVENTORY = ResourceLocation.fromNamespaceAndPath("curios", "textures/gui/curios/inventory.png");

   public PageButton(CuriosScreen parentGui, int xIn, int yIn, int widthIn, int heightIn, PageButton.Type type) {
      super(
         xIn,
         yIn,
         widthIn,
         heightIn,
         CommonComponents.EMPTY,
         button -> PacketDistributor.sendToServer(
            new CPacketPage(((CuriosContainer)parentGui.getMenu()).containerId, type == PageButton.Type.NEXT), new CustomPacketPayload[0]
         ),
         DEFAULT_NARRATION
      );
      this.parentGui = parentGui;
      this.type = type;
   }

   public void renderWidget(@Nonnull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
      int xText = this.type == PageButton.Type.NEXT ? 43 : 32;
      int yText = 25;
      if (this.type == PageButton.Type.NEXT) {
         this.setX(this.parentGui.getGuiLeft() - 17);
         this.active = ((CuriosContainer)this.parentGui.getMenu()).currentPage + 1 < ((CuriosContainer)this.parentGui.getMenu()).totalPages;
      } else {
         this.setX(this.parentGui.getGuiLeft() - 28);
         this.active = ((CuriosContainer)this.parentGui.getMenu()).currentPage > 0;
      }

      if (!this.isActive()) {
         yText += 12;
      } else if (this.isHoveredOrFocused()) {
         xText += 22;
      }

      if (this.isHovered()) {
         guiGraphics.renderTooltip(
            Minecraft.getInstance().font,
            Component.translatable(
               "gui.curios.page",
               new Object[]{((CuriosContainer)this.parentGui.getMenu()).currentPage + 1, ((CuriosContainer)this.parentGui.getMenu()).totalPages}
            ),
            x,
            y
         );
      }

      guiGraphics.blit(CURIO_INVENTORY, this.getX(), this.getY(), xText, yText, this.width, this.height);
   }

   public static enum Type {
      NEXT,
      PREVIOUS;
   }
}
