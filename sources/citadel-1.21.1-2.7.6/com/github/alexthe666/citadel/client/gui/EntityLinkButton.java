package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityLinkButton extends Button {
   private static final Map<String, Entity> renderedEntites = new HashMap<>();
   private static final Quaternionf ENTITY_ROTATION = new Quaternionf().rotationXYZ((float)Math.toRadians(30.0), (float)Math.toRadians(130.0), 3.1415927F);
   private final EntityLinkData data;
   private final GuiBasicBook bookGUI;

   public EntityLinkButton(GuiBasicBook bookGUI, EntityLinkData linkData, int k, int l, OnPress o) {
      super(
         k + linkData.getX() - 12,
         l + linkData.getY(),
         (int)(24.0 * linkData.getScale()),
         (int)(24.0 * linkData.getScale()),
         CommonComponents.EMPTY,
         o,
         DEFAULT_NARRATION
      );
      this.data = linkData;
      this.bookGUI = bookGUI;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      int lvt_5_1_ = 0;
      int lvt_6_1_ = 30;
      float f = (float)this.data.getScale();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(this.getX(), this.getY(), 0.0F);
      guiGraphics.pose().scale(f, f, 1.0F);
      this.drawBtn(false, guiGraphics, 0, 0, lvt_5_1_, lvt_6_1_, 24, 24);
      Entity model = null;
      EntityType type = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(this.data.getEntity()));
      model = renderedEntites.putIfAbsent(this.data.getEntity(), type.create(Minecraft.getInstance().level));
      guiGraphics.enableScissor(
         this.getX() + Math.round(f * 4.0F), this.getY() + Math.round(f * 4.0F), this.getX() + Math.round(f * 20.0F), this.getY() + Math.round(f * 20.0F)
      );
      if (model != null) {
         model.tickCount = Minecraft.getInstance().player.tickCount;
         float renderScale = (float)(this.data.getEntityScale() * f * 10.0);
         this.renderEntityInInventory(
            guiGraphics,
            11 + (int)(this.data.getOffset_x() * this.data.getEntityScale()),
            22 + (int)(this.data.getOffset_y() * this.data.getEntityScale()),
            renderScale,
            ENTITY_ROTATION,
            model
         );
      }

      guiGraphics.disableScissor();
      byte var11;
      if (this.isHovered) {
         this.bookGUI.setEntityTooltip(this.data.getHoverText());
         var11 = 48;
      } else {
         var11 = 24;
      }

      this.drawBtn(!this.isHovered, guiGraphics, 0, 0, var11, lvt_6_1_, 24, 24);
      guiGraphics.pose().popPose();
   }

   public void drawBtn(
      boolean color, GuiGraphics guiGraphics, int p_238474_2_, int p_238474_3_, int p_238474_4_, int p_238474_5_, int p_238474_6_, int p_238474_7_
   ) {
      if (color) {
         int widgetColor = this.bookGUI.getWidgetColor();
         int r = (widgetColor & 0xFF0000) >> 16;
         int g = (widgetColor & 0xFF00) >> 8;
         int b = widgetColor & 0xFF;
         BookBlit.blitWithColor(
            guiGraphics,
            this.bookGUI.getBookWidgetTexture(),
            p_238474_2_,
            p_238474_3_,
            0,
            p_238474_4_,
            p_238474_5_,
            p_238474_6_,
            p_238474_7_,
            256,
            256,
            r,
            g,
            b,
            255
         );
      } else {
         guiGraphics.blit(this.bookGUI.getBookWidgetTexture(), p_238474_2_, p_238474_3_, 0, p_238474_4_, p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
      }
   }

   public void renderEntityInInventory(GuiGraphics guiGraphics, int xPos, int yPos, float scale, Quaternionf rotation, Entity entity) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(xPos, yPos, 50.0);
      guiGraphics.pose().mulPose(new Matrix4f().scaling(scale, scale, -scale));
      guiGraphics.pose().mulPose(rotation);
      Vector3f light0 = new Vector3f(1.0F, -1.0F, -1.0F).normalize();
      Vector3f light1 = new Vector3f(-1.0F, 1.0F, 1.0F).normalize();
      RenderSystem.setShaderLights(light0, light1);
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      entityrenderdispatcher.setRenderShadow(false);
      RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880));
      guiGraphics.flush();
      entityrenderdispatcher.setRenderShadow(true);
      guiGraphics.pose().popPose();
      Lighting.setupFor3DItems();
   }
}
