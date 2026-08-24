package vazkii.psi.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.TileCADAssembler;
import vazkii.psi.common.block.tile.container.ContainerCADAssembler;

public class GuiCADAssembler extends AbstractContainerScreen<ContainerCADAssembler> {
   private static final ResourceLocation texture = ResourceLocation.parse("psi:textures/gui/cad_assembler.png");
   private final Player player;
   private final TileCADAssembler assembler;

   public GuiCADAssembler(ContainerCADAssembler containerCADAssembler, Inventory inventory, Component component) {
      super(containerCADAssembler, inventory, component);
      this.player = inventory.player;
      this.assembler = containerCADAssembler.assembler;
      this.imageWidth = 256;
      this.imageHeight = 225;
   }

   public void render(@NotNull GuiGraphics graphics, int x, int y, float pTicks) {
      this.renderBackground(graphics, x, y, pTicks);
      super.render(graphics, x, y, pTicks);
      this.renderTooltip(graphics, x, y);
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      int color = 4210752;
      String name = new ItemStack((ItemLike)ModBlocks.cadAssembler.get()).getHoverName().getString();
      graphics.drawString(this.font, name, this.imageWidth / 2 - this.font.width(name) / 2, 10, color, false);
      ItemStack cad = this.assembler.getCachedCAD(this.player);
      if (!cad.isEmpty()) {
         color = 16777215;
         int i = 0;
         ICAD cadItem = (ICAD)cad.getItem();
         String stats = I18n.get("psimisc.stats", new Object[0]);
         String s = ChatFormatting.BOLD + stats;
         graphics.drawString(this.font, s, 213.0F - this.font.width(s) / 2.0F, 32.0F, color, true);

         for (EnumCADStat stat : EnumCADStat.class.getEnumConstants()) {
            s = (Psi.magical ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.AQUA)
               + I18n.get(stat.getName(), new Object[0])
               + ChatFormatting.RESET
               + ": "
               + cadItem.getStatValue(cad, stat);
            graphics.drawString(this.font, s, 179, 45 + i * 10, color, true);
            i++;
         }
      }
   }

   protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      graphics.blit(texture, x, y, 0, 0, this.imageWidth, this.imageHeight);

      for (int i = 0; i < 12; i++) {
         if (!this.assembler.isBulletSlotEnabled(i)) {
            graphics.blit(texture, x + 17 + i % 3 * 18, y + 57 + i / 3 * 18, 16, this.imageHeight, 16, 16);
         }
      }
   }
}
