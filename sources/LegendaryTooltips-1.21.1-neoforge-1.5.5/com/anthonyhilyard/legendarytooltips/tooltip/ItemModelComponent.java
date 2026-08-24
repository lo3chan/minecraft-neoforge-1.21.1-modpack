package com.anthonyhilyard.legendarytooltips.tooltip;

import com.anthonyhilyard.iceberg.events.client.RegisterTooltipComponentFactoryEvent;
import com.anthonyhilyard.iceberg.renderer.CustomItemRenderer;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.prism.text.DynamicColor;
import com.anthonyhilyard.prism.util.ColorUtil;
import com.anthonyhilyard.prism.util.ConfigHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class ItemModelComponent implements TooltipComponent, ClientTooltipComponent {
   private static CustomItemRenderer customItemRenderer = null;
   private static float rotationTimer = 0.0F;
   private final ItemStack itemStack;
   public static final int PADDING = 2;

   public static void updateTimer(float partialTick) {
      double rotationInterval = LegendaryTooltipsConfig.getInstance().modelRotationSpeed.get();
      if (rotationInterval > 0.0) {
         rotationTimer += partialTick;
         if (rotationTimer > rotationInterval) {
            rotationTimer = (float)(rotationTimer - rotationInterval);
         }
      } else {
         rotationTimer = 0.0F;
      }
   }

   public ItemModelComponent(ItemStack itemStack) {
      this.itemStack = itemStack;
      if (customItemRenderer == null) {
         customItemRenderer = CustomItemRenderer.getInstance();
      }
   }

   public static int getRenderHeight() {
      return 22;
   }

   public static int getRenderWidth() {
      return 22;
   }

   public int getHeight() {
      return 4;
   }

   public int getWidth(Font p_169952_) {
      return -(getRenderWidth() + 6);
   }

   public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
      graphics.flush();
      y--;
      x--;
      int z = 0;
      int margin = 2;
      DynamicColor borderStartColor = DynamicColor.fromRgb(Tooltips.currentColors.borderColorStart());
      DynamicColor borderEndColor = DynamicColor.fromRgb(Tooltips.currentColors.borderColorEnd());
      DynamicColor backgroundStartColor = DynamicColor.fromRgb(Tooltips.currentColors.backgroundColorStart());
      DynamicColor backgroundEndColor = ConfigHelper.applyModifiers(List.of("v+35", "s+10"), DynamicColor.fromRgb(Tooltips.currentColors.backgroundColorEnd()));
      int borderColor = ColorUtil.combineARGB(
         (int)(borderStartColor.alpha() * 0.35F), borderStartColor.red(), borderStartColor.green(), borderStartColor.blue()
      );
      int backgroundStart = ColorUtil.combineARGB(
         (int)(backgroundStartColor.alpha() * 0.15F), backgroundStartColor.red(), backgroundStartColor.green(), backgroundStartColor.blue()
      );
      int backgroundEnd = ColorUtil.combineARGB(
         (int)(backgroundEndColor.alpha() * 0.6F), backgroundEndColor.red(), backgroundEndColor.green(), backgroundEndColor.blue()
      );
      PoseStack poseStack = graphics.pose();
      Matrix4f matrix = poseStack.last().pose();
      GuiHelper.drawGradientRect(matrix, z, x + 2 + 1, y + 2 + 1, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2 - 1, backgroundStart, backgroundEnd);
      GuiHelper.drawGradientRect(matrix, z, x + 2 + 1, y + 2 + 1, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2 - 1, backgroundEnd, backgroundStart);
      GuiHelper.drawGradientRectHorizontal(
         matrix, z, x + 2 + 1, y + 2 + 1, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2 - 1, backgroundStart, backgroundEnd
      );
      GuiHelper.drawGradientRectHorizontal(
         matrix, z, x + 2 + 1, y + 2 + 1, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2 - 1, backgroundEnd, backgroundStart
      );
      GuiHelper.drawGradientRect(matrix, z, x + 2 + 1, y + 2, x + getRenderWidth() - 2 - 1, y + 2 + 1, borderColor, borderColor);
      GuiHelper.drawGradientRect(
         matrix, z, x + 2 + 1, y + getRenderHeight() - 2 - 1, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2, borderColor, borderColor
      );
      GuiHelper.drawGradientRect(matrix, z, x + 2, y + 2 + 1, x + 2 + 1, y + getRenderHeight() - 2 - 1, borderColor, borderColor);
      GuiHelper.drawGradientRect(
         matrix, z, x + getRenderWidth() - 2 - 1, y + 2 + 1, x + getRenderWidth() - 2, y + getRenderHeight() - 2 - 1, borderColor, borderColor
      );
      borderColor = ColorUtil.combineARGB(
         (int)(borderStartColor.alpha() * 0.15F),
         (int)((borderStartColor.red() + borderEndColor.red()) * 0.5F),
         (int)((borderStartColor.green() + borderEndColor.green()) * 0.5F),
         (int)((borderStartColor.blue() + borderEndColor.blue()) * 0.5F)
      );
      GuiHelper.drawGradientRect(matrix, z, x + 2 + 1, y + 2 + 1, x + getRenderWidth() - 2 - 1, y + 2 + 2, borderColor, borderColor);
      GuiHelper.drawGradientRect(
         matrix, z, x + 2 + 1, y + getRenderHeight() - 2 - 2, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2 - 1, borderColor, borderColor
      );
      GuiHelper.drawGradientRect(matrix, z, x + 2 + 1, y + 2 + 2, x + 2 + 2, y + getRenderHeight() - 2 - 2, borderColor, borderColor);
      GuiHelper.drawGradientRect(
         matrix, z, x + getRenderWidth() - 2 - 2, y + 2 + 2, x + getRenderWidth() - 2 - 1, y + getRenderHeight() - 2 - 2, borderColor, borderColor
      );
      borderColor = ColorUtil.combineARGB((int)(borderStartColor.alpha() * 0.05F), borderEndColor.red(), borderEndColor.green(), borderEndColor.blue());
      GuiHelper.drawGradientRect(matrix, z, x + 2 + 2, y + 2 + 2, x + getRenderWidth() - 2 - 2, y + getRenderHeight() - 2 - 3, borderColor, borderColor);
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.mul(matrix);
      modelViewStack.translate(x + 2 - 1, y + 2 - 1, -120.0F);
      modelViewStack.scale(1.25F, 1.25F, 1.0F);
      RenderSystem.applyModelViewMatrix();
      float rotationAngle = 0.0F;
      if (LegendaryTooltipsConfig.getInstance().modelRotationSpeed.get() > 0.0) {
         rotationAngle = Mth.lerp(rotationTimer / LegendaryTooltipsConfig.getInstance().modelRotationSpeed.get().floatValue(), 0.0F, 360.0F);
      }

      customItemRenderer.renderDetailModelIntoGUI(this.itemStack, 0, 0, Axis.YP.rotationDegrees(rotationAngle), graphics);
      modelViewStack.popMatrix();
      RenderSystem.applyModelViewMatrix();
   }

   public static void registerFactory() {
      RegisterTooltipComponentFactoryEvent.EVENT
         .register(
            ItemModelComponent.class,
            (RegisterTooltipComponentFactoryEvent)data -> data instanceof ItemModelComponent itemModelComponent ? itemModelComponent : null
         );
   }
}
