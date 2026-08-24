package mezz.jei.library.render.batch;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.render.ItemStackRenderer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ItemStackBatchRenderer {
   private final List<ElementWithModel> useBlockLight = new ArrayList<>();
   private final List<ElementWithModel> noBlockLight = new ArrayList<>();
   private final List<BatchRenderElement<ItemStack>> customRender = new ArrayList<>();

   public ItemStackBatchRenderer(Minecraft minecraft, List<BatchRenderElement<ItemStack>> elements) {
      ClientLevel level = minecraft.level;
      ItemRenderer itemRenderer = minecraft.getItemRenderer();

      for (BatchRenderElement<ItemStack> element : elements) {
         ItemStack itemStack = element.ingredient();
         if (!itemStack.isEmpty()) {
            BakedModel bakedmodel = itemRenderer.getModel(itemStack, level, null, 0);
            if (bakedmodel.isCustomRenderer()) {
               this.customRender.add(element);
            } else if (bakedmodel.usesBlockLight()) {
               ElementWithModel elementWithModel = new ElementWithModel(bakedmodel, itemStack, element.x(), element.y());
               this.useBlockLight.add(elementWithModel);
            } else {
               if (!bakedmodel.isGui3d()) {
                  IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
                  bakedmodel = renderHelper.createLimitedQuadItemModel(bakedmodel);
               }

               ElementWithModel elementWithModel = new ElementWithModel(bakedmodel, itemStack, element.x(), element.y());
               this.noBlockLight.add(elementWithModel);
            }
         }
      }
   }

   public void render(GuiGraphics guiGraphics, Minecraft minecraft, ItemRenderer itemRenderer, ItemStackRenderer itemStackRenderer) {
      if (!this.noBlockLight.isEmpty()) {
         Lighting.setupForFlatItems();

         for (ElementWithModel element : this.noBlockLight) {
            this.renderItem(guiGraphics, itemRenderer, element.model(), element.stack(), element.x(), element.y());
         }

         guiGraphics.flush();
         Lighting.setupFor3DItems();
      }

      if (!this.useBlockLight.isEmpty()) {
         Lighting.setupFor3DItems();

         for (ElementWithModel element : this.useBlockLight) {
            this.renderItem(guiGraphics, itemRenderer, element.model(), element.stack(), element.x(), element.y());
         }

         guiGraphics.flush();
      }

      IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();

      for (ElementWithModel element : this.useBlockLight) {
         ItemStack ingredient = element.stack();
         Font font = renderHelper.getFontRenderer(minecraft, ingredient);
         guiGraphics.renderItemDecorations(font, ingredient, element.x(), element.y());
      }

      for (ElementWithModel element : this.noBlockLight) {
         ItemStack ingredient = element.stack();
         Font font = renderHelper.getFontRenderer(minecraft, ingredient);
         guiGraphics.renderItemDecorations(font, ingredient, element.x(), element.y());
      }

      RenderSystem.disableBlend();

      for (BatchRenderElement<ItemStack> element : this.customRender) {
         ItemStack ingredient = element.ingredient();
         itemStackRenderer.render(guiGraphics, ingredient, element.x(), element.y());
         RenderSystem.disableBlend();
      }

      RenderSystem.disableBlend();
   }

   private void renderItem(GuiGraphics guiGraphics, ItemRenderer itemRenderer, BakedModel bakedmodel, ItemStack itemStack, int x, int y) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(x + 8, y + 8, 150.0F);
      poseStack.scale(16.0F, -16.0F, 16.0F);

      try {
         itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, guiGraphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
      } catch (Throwable var11) {
         CrashReport crashreport = CrashReport.forThrowable(var11, "Rendering item");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
         crashreportcategory.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
         crashreportcategory.setDetail("Item Components", () -> String.valueOf(itemStack.getComponents()));
         crashreportcategory.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
         throw new ReportedException(crashreport);
      }

      poseStack.popPose();
   }
}
