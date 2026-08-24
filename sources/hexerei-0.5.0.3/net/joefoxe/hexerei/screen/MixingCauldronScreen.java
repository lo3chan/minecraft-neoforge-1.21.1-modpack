package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Optional;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.integration.HexereiModNameTooltipCompat;
import net.joefoxe.hexerei.integration.jei.HexereiJei;
import net.joefoxe.hexerei.screen.renderer.FluidStackRenderer;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.DrainCauldronToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;

public class MixingCauldronScreen extends AbstractContainerScreen<MixingCauldronContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/mixing_cauldron_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   public static final ResourceLocation MOON_PHASES = HexereiUtil.getResource("textures/gui/moon_phases.png");
   private final FluidStackRenderer renderer;
   private float dumpOffset;
   public boolean clickedDump;
   public MixingCauldronTile mixingCauldron;
   private static final int FRONT_BLIT_LAYER = 1;
   private static final int BACK_BLIT_LAYER = 0;

   public MixingCauldronScreen(MixingCauldronContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      if (screenContainer.tileEntity instanceof MixingCauldronTile mixingCauldronTile) {
         this.mixingCauldron = mixingCauldronTile;
      }

      this.titleLabelY = 4;
      this.titleLabelX = 28;
      this.inventoryLabelY = 108;
      this.inventoryLabelX = 12;
      this.renderer = new FluidStackRenderer(2000, true, 16, 32);
      this.dumpOffset = 0.0F;
      this.clickedDump = false;
   }

   private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY) {
      return isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, this.renderer.getWidth(), this.renderer.getHeight());
   }

   public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
      return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY;
   }

   private void drawFont(GuiGraphics guiGraphics, Component component, float x, float y, int z, int color, float scalePercent, boolean shadow) {
      guiGraphics.pose().pushPose();
      if (scalePercent != 0.0F) {
         guiGraphics.pose().scale(1.0F / scalePercent, 1.0F / scalePercent, 1.0F / scalePercent);
      }

      guiGraphics.pose().translate(x, y, z);
      guiGraphics.drawString(this.minecraft.font, component, 0, 0, color, shadow);
      guiGraphics.pose().popPose();
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int pMouseX, int pMouseY) {
      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      int i = this.leftPos;
      int j = this.topPos;
      if (isMouseOver(pMouseX, pMouseY, i + 20 - (int)this.dumpOffset, j + 56, 10 + (int)this.dumpOffset, 32)) {
         this.dumpOffset = HexereiUtil.moveTo(this.dumpOffset, 31.0F, 2.0F * ((32.0F - this.dumpOffset) / 31.0F));
      } else {
         this.dumpOffset = HexereiUtil.moveTo(this.dumpOffset, 0.0F, Math.abs(2.0F * ((-1.0F - this.dumpOffset) / 31.0F)));
      }

      Component component = Component.translatable("Dump");
      float width = this.font.width(component.getVisualOrderText());
      guiGraphics.blit(this.GUI, i + 20 - (int)this.dumpOffset, j + 56, 0, 216.0F, 61.0F, 40, 32, 256, 256);
      if (this.clickedDump) {
         guiGraphics.blit(this.GUI, i + 20 - (int)this.dumpOffset + 9, j + 56 + 9, 0, 226.0F, 47.0F, 30, 14, 256, 256);
      }

      float lineHeight = 9.0F / 2.0F;
      if (width > 20.0F) {
         float percent = width / 20.0F;
         this.drawFont(
            guiGraphics,
            component,
            (i + 44.5F - width / percent / 2.0F - (int)this.dumpOffset) * percent + (this.clickedDump ? 1.0F : 0.0F),
            (j + 68 + lineHeight) * percent - 4.5F + (this.clickedDump ? 1.0F : 0.0F),
            0,
            -12566464,
            percent,
            false
         );
      } else {
         this.drawFont(
            guiGraphics,
            component,
            i + 44.5F - width / 2.0F - (int)this.dumpOffset + (this.clickedDump ? 1.0F : 0.0F),
            j + 68 + lineHeight - 4.5F + (this.clickedDump ? 1.0F : 0.0F),
            0,
            -10461088,
            1.0F,
            false
         );
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      guiGraphics.blit(this.GUI, i, j, 1, 0.0F, 0.0F, 207, 127, 256, 256);
      guiGraphics.blit(this.GUI, i + 150, j + 52, 1, 220.0F, 0.0F, (int)(22.0F * ((MixingCauldronContainer)this.menu).getCraftPercent()), 8, 256, 256);
      guiGraphics.blit(this.GUI, i + 109, j + 46, 1, 242.0F, 0.0F, 8, (int)(22.0F * ((MixingCauldronContainer)this.menu).getCraftPercent()), 256, 256);
      if (this.mixingCauldron.usingRecipeNeedsHeat && !this.mixingCauldron.hasHeatSource) {
         if (ClientEvents.getClientTicksWithoutPartial() % 20.0F < 10.0F) {
            guiGraphics.blit(this.GUI, i + 102, j + 62, 1, 233.0F, 94.0F, 22, 14, 256, 256);
         }
      } else if (this.mixingCauldron.hasHeatSource) {
         guiGraphics.blit(this.GUI, i + 102, j + 62, 1, 233.0F, 94.0F, 22, 14, 256, 256);
      }

      if (this.mixingCauldron.usingRecipeNeedsMoonPhase != MoonPhases.MoonCondition.NONE
         && (
            ClientEvents.getClientTicksWithoutPartial() % 20.0F >= 10.0F
                  && MoonPhases.MoonCondition.getMoonPhase(this.mixingCauldron.getLevel()) != this.mixingCauldron.usingRecipeNeedsMoonPhase
               || MoonPhases.MoonCondition.getMoonPhase(this.mixingCauldron.getLevel()) == this.mixingCauldron.usingRecipeNeedsMoonPhase
         )) {
         guiGraphics.pose().pushPose();
         float scale = 1.5F;
         guiGraphics.pose().scale(scale, scale, scale);
         guiGraphics.pose().translate((i + 107) / scale, (j + 35) / scale, 10.0F);
         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.FULL_MOON) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 12.0F, 12.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WANING_GIBBOUS) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 44.0F, 12.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.LAST_QUARTER) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 76.0F, 12.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WANING_CRESCENT) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 108.0F, 12.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.NEW_MOON) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 12.0F, 44.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WAXING_CRESCENT) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 44.0F, 44.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.FIRST_QUARTER) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 76.0F, 44.0F, 8, 8, 256, 256);
         }

         if (this.mixingCauldron.usingRecipeNeedsMoonPhase == MoonPhases.MoonCondition.WAXING_GIBBOUS) {
            guiGraphics.blit(MOON_PHASES, 0, 0, 1, 108.0F, 44.0F, 8, 8, 256, 256);
         }

         guiGraphics.pose().popPose();
      }

      RenderSystem.setShaderTexture(0, this.INVENTORY);
      guiGraphics.blit(this.INVENTORY, i + 6, j + 102, 1, 0.0F, 0.0F, 176, 100, 256, 256);
      if (((MixingCauldronContainer)this.menu).getRenderedFluid() != null) {
         FluidStack stack = ((MixingCauldronContainer)this.menu).getRenderedFluid().copy();
         this.renderer.render(guiGraphics, i + 42, j + 56, stack);
      }

      RenderSystem.disableDepthTest();
      guiGraphics.renderItem(new ItemStack(((MixingCauldron)ModBlocks.MIXING_CAULDRON.get()).asItem()), this.leftPos + 86, this.topPos - 25);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderTexture(0, this.GUI);
      guiGraphics.blit(this.GUI, i + 81, j - 30, 1, 230.0F, 21.0F, 26, 26, 256, 256);
      guiGraphics.blit(this.GUI, i + 42, j + 56, 1, 208.0F, 12.0F, 16, 32, 256, 256);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      if (this.isMouseAboveArea(mouseX, mouseY, x, y, 42, 56)) {
         ArrayList<Component> tooltip = new ArrayList<>(
            this.renderer
               .getTooltip(
                  ((MixingCauldronContainer)this.menu).getRenderedFluid() != null
                     ? ((MixingCauldronContainer)this.menu).getRenderedFluid()
                     : ((MixingCauldronContainer)this.menu).getFluid(),
                  Default.NORMAL
               )
         );
         PotionContents contents = (PotionContents)((MixingCauldronContainer)this.menu).getFluid().get(DataComponents.POTION_CONTENTS);
         if (contents != null) {
            contents.addPotionTooltip(tooltip::add, 1.0F, 20.0F);
         }

         FluidStack fluidStack = ((MixingCauldronContainer)this.menu).getFluid();
         if (!fluidStack.isEmpty()) {
            String modId = HexereiUtil.getRegistryName(fluidStack.getFluid()).getNamespace();
            String modName = HexereiUtil.getModNameForModId(modId);
            MutableComponent modNameComponent = Component.translatable(modName);
            modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
            if (!HexereiModNameTooltipCompat.LOADED) {
               tooltip.add(modNameComponent);
            }
         }

         guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (pButton == 0) {
         this.clickedDump = false;
      }

      return super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int button) {
      boolean mouseClicked = super.mouseClicked(pMouseX, pMouseY, button);
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      if (this.isMouseAboveArea((int)pMouseX, (int)pMouseY, x, y, 42, 56) && ModList.get().isLoaded("jei")) {
         if (button == 0) {
            HexereiJei.showRecipe(this.mixingCauldron.getFluidStack());
         }

         if (button == 1) {
            HexereiJei.showUses(this.mixingCauldron.getFluidStack());
         }
      }

      if (isMouseOver(pMouseX, pMouseY, this.leftPos + 20 - (int)this.dumpOffset + 9, this.topPos + 56 + 9, 30, 14) && this.dumpOffset > 20.0F) {
         this.clickedDump = true;
         if (this.mixingCauldron != null && this.mixingCauldron.getLevel() != null && this.mixingCauldron.getLevel().isClientSide) {
            HexereiPacketHandler.sendToServer(new DrainCauldronToServer(this.mixingCauldron.getPos()));
         }
      }

      return mouseClicked;
   }
}
