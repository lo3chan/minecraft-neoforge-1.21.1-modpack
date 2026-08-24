package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.SendOwlCourierPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import org.joml.Matrix4f;

public class OwlCourierSendScreen extends Screen {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/owl_courier_delivery_gui.png");
   public final OwlEntity owl;
   private List<OwlCourierSendScreen.ListButton> listButtons = new ArrayList<>();
   private OwlCourierSendScreen.ListButton sendButton;
   int ticks = 0;
   float scroll = 0.0F;
   float scrollOld = 0.0F;
   float scrollTarget = 0.0F;
   boolean scrollClicked = false;
   double scrollClickedPos = 0.0;
   int img_width = 124;
   int img_height = 164;
   int left;
   int top;
   int button_height = 16;
   int button_space = 3;
   int button_selected = 0;
   OwlCourierSendScreen.ScissorArea scissorArea;
   InteractionHand hand;

   public OwlCourierSendScreen(OwlEntity owl, InteractionHand hand, int selected) {
      super(Component.translatable("hexerei.owl_message.destination"));
      this.minecraft = Minecraft.getInstance();
      this.owl = owl;
      this.hand = hand;
      if (this.minecraft.player != null && selected != this.minecraft.player.getInventory().selected) {
         this.onClose();
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.listButtons.size() > 6) {
         this.scrollTarget = (float)(
            this.scrollTarget
               + (this.button_height + this.button_space + (this.button_height + this.button_space) * Mth.abs(this.scrollTarget - this.scroll))
                     / (this.listButtons.size() * this.button_height - this.scissorArea.height)
                  * -scrollY
         );
      }

      return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (pMouseX >= this.left + 250 - 149
         && pMouseX < this.left + 250 - 149 + 10
         && pMouseY >= this.top + 21 + this.scroll * 101.0F
         && pMouseY < this.top + 21 + 5 + this.scroll * 101.0F) {
         this.scrollClicked = true;
         this.scrollClickedPos = pMouseY - (this.top + 21 + this.scroll * 101.0F);
         return true;
      } else {
         if (pMouseX >= this.left + 250 - 149 && pMouseX < this.left + 250 - 149 + 8 && pMouseY >= this.top + 21 && pMouseY < this.top + 21 + 106) {
            this.scroll = (float)(pMouseY - (this.top + 23)) / 101.0F;
            this.scrollClicked = true;
            this.scrollClickedPos = pMouseY - (this.top + 21 + this.scroll * 101.0F);
         }

         for (OwlCourierSendScreen.ListButton button : this.listButtons) {
            if (button.isHovered && button.mouseClicked(pMouseX, pMouseY, pButton)) {
               return true;
            }
         }

         return this.sendButton != null && this.sendButton.isHovered && this.sendButton.mouseClicked(pMouseX, pMouseY, pButton)
            ? true
            : super.mouseClicked(pMouseX, pMouseY, pButton);
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      this.scrollClicked = false;

      for (OwlCourierSendScreen.ListButton button : this.listButtons) {
         if (button.isHovered && button.mouseReleased(pMouseX, pMouseY, pButton)) {
            return true;
         }
      }

      return this.sendButton != null && this.sendButton.isHovered && this.sendButton.mouseReleased(pMouseX, pMouseY, pButton)
         ? true
         : super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   protected void init() {
      this.left = this.width / 2 - this.img_width / 2;
      this.top = this.height / 2 - this.img_height / 2;
      this.scissorArea = new OwlCourierSendScreen.ScissorArea(this.left + 163 - 149, this.top + 21, 94, 106);
      this.listButtons.clear();
      int i = 0;
      int listSize = ClientOwlCourierDepotData.getDepots().size();
      if (this.minecraft != null && this.minecraft.player != null) {
         ClientPacketListener handler = this.minecraft.player.connection;
         listSize += handler.getOnlinePlayers().size();

         for (PlayerInfo playerInfo : handler.getOnlinePlayers()) {
            int compare = this.minecraft.player.getUUID().compareTo(playerInfo.getProfile().getId());
            if (compare != 0) {
               this.listButtons
                  .add(
                     new OwlCourierSendScreen.ListButton(
                        this.left + 163 - 149,
                        this.top + 21 + (this.button_height + this.button_space) * i,
                        listSize < 7 ? 94 : 88,
                        this.button_height,
                        button -> {
                           for (OwlCourierSendScreen.ListButton lb : this.listButtons) {
                              lb.isSelected = false;
                           }

                           button.isSelected = true;
                           this.button_selected = button.index;
                        },
                        button -> {
                           this.onDone();
                           HexereiPacketHandler.sendToServer(new SendOwlCourierPacket(this.owl, playerInfo.getProfile().getId(), this.hand));
                        },
                        this.scissorArea,
                        playerInfo.getProfile().getName(),
                        true,
                        false,
                        i
                     )
                  );
               i++;
            }
         }
      }

      for (Entry<GlobalPos, OwlCourierDepotData> entry : ClientOwlCourierDepotData.getDepots().entrySet()) {
         this.listButtons
            .add(
               new OwlCourierSendScreen.ListButton(
                  this.left + 164 - 149, this.top + 21 + (this.button_height + this.button_space) * i, listSize < 7 ? 94 : 88, this.button_height, button -> {
                     for (OwlCourierSendScreen.ListButton lb : this.listButtons) {
                        lb.isSelected = false;
                     }

                     button.isSelected = true;
                     this.button_selected = button.index;
                  }, button -> {
                     this.onDone();
                     HexereiPacketHandler.sendToServer(new SendOwlCourierPacket(this.owl, entry.getKey(), this.hand));
                  }, this.scissorArea, entry.getValue().name, false, entry.getValue().isFull(), i
               )
            );
         i++;
      }

      if (this.listButtons.isEmpty()) {
         this.minecraft.player.sendSystemMessage(Component.translatable("screen.hexerei.owl_send_screen.no_destinations"));
         this.onDone();
      }

      this.sendButton = new OwlCourierSendScreen.ListButton(
         this.left + 186 - 149,
         this.top + 133,
         50,
         this.button_height,
         button -> this.listButtons.get(this.button_selected).onComplete.onPress(this.listButtons.get(this.button_selected)),
         button -> {},
         new OwlCourierSendScreen.ScissorArea(this.left + 186 - 149, this.top + 133, 50, this.button_height),
         "Send",
         false,
         false,
         i
      ) {
         @Override
         public int getY(float partialTicks) {
            return this.y;
         }

         @Override
         public void tooltip(List<Component> list) {
            if (this.isHovered() && !this.isDisabled()) {
               list.add(
                  Component.translatable(
                        "screen.hexerei.owl_send_screen.send_to",
                        new Object[]{OwlCourierSendScreen.this.listButtons.get(OwlCourierSendScreen.this.button_selected).name}
                     )
                     .withStyle(Style.EMPTY.withColor(11184810))
               );
            }
         }

         @Override
         public boolean isDisabled() {
            return OwlCourierSendScreen.this.listButtons.get(OwlCourierSendScreen.this.button_selected).isDisabled;
         }
      };
   }

   public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(0.0F, 0.0F, 5.0F);
      if (this.listButtons.size() > 6) {
         if (this.scrollClicked) {
            this.scroll = Mth.clamp((float)(pMouseY - this.top - 21 - this.scrollClickedPos) / 101.0F, 0.0F, 1.0F);
            this.scrollTarget = this.scroll;
            this.scrollOld = this.scroll;
         }
      } else {
         this.scroll = 0.0F;
      }

      float scrollLerp = Mth.lerp(pPartialTick, this.scrollOld, this.scroll);
      Lighting.setupForFlatItems();
      pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.top + 4, 3355443);

      for (OwlCourierSendScreen.ListButton button : this.listButtons) {
         button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
      }

      if (this.sendButton != null) {
         this.sendButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
      }

      pGuiGraphics.blit(this.GUI, this.left, this.top, 0, 0, this.img_width, this.img_height);
      if (this.listButtons.size() > 6) {
         pGuiGraphics.blit(this.GUI, this.left + 252 - 149, this.top + 21, 127, 177, 6, 53);
         pGuiGraphics.blit(this.GUI, this.left + 252 - 149, this.top + 21 + 53, 134, 177, 6, 53);
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(0.0F, 0.0F, 5.0F);
         pGuiGraphics.blit(this.GUI, this.left + 250 - 149, this.top + 21 + (int)(101.0F * scrollLerp), 127, 171, 10, 5);
         pGuiGraphics.pose().popPose();
      }

      List<Component> tooltipLines = new ArrayList<>();

      for (OwlCourierSendScreen.ListButton button : this.listButtons) {
         button.tooltip(tooltipLines);
      }

      if (this.sendButton != null) {
         this.sendButton.tooltip(tooltipLines);
      }

      if (!tooltipLines.isEmpty()) {
         pGuiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), pMouseX, pMouseY);
      }

      Lighting.setupFor3DItems();
      pGuiGraphics.pose().popPose();
      super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderTransparentBackground(guiGraphics);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   private void onDone() {
      this.minecraft.setScreen(null);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      this.ticks++;
      this.scrollOld = this.scroll;
      if (this.scroll == 1.0F && this.scrollTarget > 1.0F) {
         this.scrollTarget = 1.0F;
      }

      if (this.scroll == 0.0F && this.scrollTarget < 0.0F) {
         this.scrollTarget = 0.0F;
      }

      float scrollDist = this.listButtons.size() * (this.button_height + this.button_space) - this.scissorArea.height;
      if (this.listButtons.size() > 0) {
         float dist = Mth.abs(scrollDist * this.scrollTarget - scrollDist * this.scroll);
         float scale = dist / scrollDist;
         float speed = scale / 10.0F;
         this.scroll = Mth.clamp(HexereiUtil.moveTo(this.scroll, this.scrollTarget, Math.max(speed, 0.002F)), 0.0F, 1.0F);
      } else {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      }

      if (!this.isValid()) {
         this.onDone();
      }
   }

   private boolean isValid() {
      return this.minecraft != null && this.minecraft.player != null && this.owl != null && this.owl.distanceTo(this.minecraft.player) < 8.0F;
   }

   private static void bufferQuad(
      ResourceLocation atlasLocation,
      GuiGraphics guiGraphics,
      float x,
      float y,
      float z,
      float width,
      float height,
      float uOffset,
      float vOffset,
      int uWidth,
      int vHeight,
      int spriteWidth,
      int spriteHeight,
      float alpha
   ) {
      blit(guiGraphics, atlasLocation, x, x + width, y, y + height, z, uWidth, vHeight, uOffset, vOffset, spriteWidth, spriteHeight, alpha);
   }

   private static void blit(
      GuiGraphics guiGraphics,
      ResourceLocation atlasLocation,
      float x1,
      float x2,
      float y1,
      float y2,
      float blitOffset,
      int uWidth,
      int vHeight,
      float uOffset,
      float vOffset,
      int textureWidth,
      int textureHeight,
      float alpha
   ) {
      innerBlit(
         guiGraphics,
         atlasLocation,
         x1,
         x2,
         y1,
         y2,
         blitOffset,
         (uOffset + 0.0F) / textureWidth,
         (uOffset + uWidth) / textureWidth,
         (vOffset + 0.0F) / textureHeight,
         (vOffset + vHeight) / textureHeight,
         alpha
      );
   }

   private static void innerBlit(
      GuiGraphics guiGraphics,
      ResourceLocation atlasLocation,
      float x1,
      float x2,
      float y1,
      float y2,
      float blitOffset,
      float minU,
      float maxU,
      float minV,
      float maxV,
      float alpha
   ) {
      Matrix4f matrix4f = guiGraphics.pose().last().pose();
      if (alpha == 1.0F) {
         RenderSystem.setShaderTexture(0, atlasLocation);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset)
            .setUv(minU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset)
            .setUv(minU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset)
            .setUv(maxU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset)
            .setUv(maxU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      } else {
         VertexConsumer vertexBuilder = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucentCull(atlasLocation));
         vertexBuilder.addVertex(matrix4f, x1, y1, blitOffset)
            .setUv(minU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         vertexBuilder.addVertex(matrix4f, x1, y2, blitOffset)
            .setUv(minU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         vertexBuilder.addVertex(matrix4f, x2, y2, blitOffset)
            .setUv(maxU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         vertexBuilder.addVertex(matrix4f, x2, y1, blitOffset)
            .setUv(maxU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
      }
   }

   public static void nineSlice(
      ResourceLocation atlasLocation,
      GuiGraphics guiGraphics,
      float posX,
      float posY,
      float posZ,
      float width,
      float height,
      int sliceLeftWidth,
      int sliceRightWidth,
      int sliceTopHeight,
      int sliceBottomHeight,
      int spriteWidth,
      int spriteHeight,
      float uOffset,
      float vOffset,
      int uWidth,
      int vHeight,
      float alpha
   ) {
      int middleTextureWidth = uWidth - sliceLeftWidth - sliceRightWidth;
      int middleTextureHeight = vHeight - sliceBottomHeight - sliceTopHeight;
      float topV1 = vOffset + sliceTopHeight;
      float leftU1 = uOffset + sliceLeftWidth;
      float rightU0 = uOffset + uWidth - sliceRightWidth;
      float bottomV0 = vOffset + vHeight - sliceBottomHeight;
      float middleU0 = leftU1 + 1.0F;
      float middleU1 = rightU0 + 1.0F;
      float middleV0 = vOffset + (sliceTopHeight + 1);
      float middleV1 = vOffset + vHeight - (sliceBottomHeight + 1);
      float leftX = posX + sliceLeftWidth;
      float rightX = posX + width - sliceRightWidth;
      float topY = posY + sliceTopHeight;
      float bottomY = posY + height - sliceBottomHeight;
      float middleWidth = rightX - leftX;
      float middleHeight = bottomY - topY;
      bufferQuad(
         atlasLocation,
         guiGraphics,
         posX,
         posY,
         posZ,
         sliceLeftWidth,
         sliceTopHeight,
         uOffset,
         vOffset,
         sliceLeftWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         rightX,
         posY,
         posZ,
         sliceRightWidth,
         sliceTopHeight,
         rightU0,
         vOffset,
         sliceRightWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         posX,
         bottomY,
         posZ,
         sliceLeftWidth,
         sliceBottomHeight,
         uOffset,
         bottomV0,
         sliceRightWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         rightX,
         bottomY,
         posZ,
         sliceRightWidth,
         sliceBottomHeight,
         rightU0,
         bottomV0,
         sliceRightWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         leftX,
         posY,
         posZ,
         middleWidth,
         sliceTopHeight,
         middleU0,
         vOffset,
         middleTextureWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         leftX,
         bottomY,
         posZ,
         middleWidth,
         sliceBottomHeight,
         middleU0,
         bottomV0,
         middleTextureWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         posX,
         topY,
         posZ,
         sliceLeftWidth,
         middleHeight,
         uOffset,
         middleV0,
         sliceLeftWidth,
         middleTextureHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         rightX,
         topY,
         posZ,
         sliceRightWidth,
         middleHeight,
         rightU0,
         middleV0,
         sliceLeftWidth,
         middleTextureHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         leftX,
         topY,
         posZ,
         middleWidth,
         middleHeight,
         middleU0,
         middleV0 + 1.0F,
         middleTextureWidth,
         middleTextureHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      guiGraphics.bufferSource().endBatch();
   }

   private class ListButton {
      protected int width;
      protected int height;
      private int x;
      public int y;
      private boolean isHovered = false;
      private boolean isDisabled;
      private boolean isSelected;
      private OwlCourierSendScreen.ListButton.OnPress onPress;
      private OwlCourierSendScreen.ListButton.OnPress onComplete;
      private OwlCourierSendScreen.ScissorArea scissorArea;
      private String name;
      private boolean isPlayerButton;
      private int index;

      public ListButton(
         int x,
         int y,
         int width,
         int height,
         OwlCourierSendScreen.ListButton.OnPress onPress,
         OwlCourierSendScreen.ListButton.OnPress onComplete,
         OwlCourierSendScreen.ScissorArea scissorArea,
         String name,
         boolean isPlayerButton,
         boolean isDisabled,
         int index
      ) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.onPress = onPress;
         this.onComplete = onComplete;
         this.scissorArea = scissorArea;
         this.name = name;
         this.isPlayerButton = isPlayerButton;
         this.isDisabled = isDisabled;
         this.index = index;
         this.isSelected = index == OwlCourierSendScreen.this.button_selected;
      }

      public boolean isHovered() {
         return this.isHovered;
      }

      public boolean isDisabled() {
         return this.isDisabled;
      }

      public boolean isSelected() {
         return this.isSelected;
      }

      public int getX() {
         return this.x;
      }

      public int getY(float partialTicks) {
         float scrollLerp = Mth.lerp(partialTicks, OwlCourierSendScreen.this.scrollOld, OwlCourierSendScreen.this.scroll);
         return this.y
            - (int)(
               scrollLerp
                  * (
                     OwlCourierSendScreen.this.listButtons.size() * (OwlCourierSendScreen.this.button_height + OwlCourierSendScreen.this.button_space)
                        - this.scissorArea.height
                        - OwlCourierSendScreen.this.button_space
                  )
            );
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public void tooltip(List<Component> list) {
         if (this.isHovered()) {
            if (this.isDisabled()) {
               list.add(Component.translatable("screen.hexerei.owl_send_screen.depot_too_full").withStyle(Style.EMPTY.withColor(16755370)));
            } else {
               list.add(
                  Component.translatable(
                        this.isPlayerButton ? "screen.hexerei.owl_send_screen.to_player" : "screen.hexerei.owl_send_screen.to_depot", new Object[]{this.name}
                     )
                     .withStyle(Style.EMPTY.withColor(11184810))
               );
            }
         }
      }

      public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
         if (pMouseX >= this.scissorArea.x
            && pMouseY >= this.scissorArea.y
            && pMouseX < this.scissorArea.x + this.scissorArea.width
            && pMouseY < this.scissorArea.y + this.scissorArea.height) {
            this.isHovered = pMouseX >= this.getX()
               && pMouseY >= this.getY(pPartialTick)
               && pMouseX < this.getX() + this.width
               && pMouseY < this.getY(pPartialTick) + this.height;
         } else {
            this.isHovered = false;
         }

         float alpha = 1.0F;
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(0.0F, 0.0F, 4.0F);
         pGuiGraphics.enableScissor(
            this.scissorArea.x, this.scissorArea.y, this.scissorArea.x + this.scissorArea.width + 1, this.scissorArea.y + this.scissorArea.height
         );
         Minecraft minecraft = Minecraft.getInstance();
         pGuiGraphics.setColor(1.0F, this.isPlayerButton ? 0.75F : 1.0F, 1.0F, alpha);
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         int offset = this.getTextureOffset();
         OwlCourierSendScreen.nineSlice(
            OwlCourierSendScreen.this.GUI,
            pGuiGraphics,
            this.getX(),
            this.getY(pPartialTick),
            0.0F,
            this.getWidth(),
            this.getHeight(),
            4,
            4,
            4,
            4,
            256,
            256,
            139 + offset,
            165.0F,
            9,
            9,
            alpha
         );
         if (this.isDisabled()) {
            pGuiGraphics.setColor(0.5F, 0.5F, 0.5F, 0.5F);
         } else {
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         }

         int i = 15658734;
         this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(alpha * 255.0F) << 24, pPartialTick);
         pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         pGuiGraphics.disableScissor();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         pGuiGraphics.pose().popPose();
      }

      public void renderString(GuiGraphics pGuiGraphics, Font pFont, int pColor, float partialTicks) {
         this.renderScrollingString(pGuiGraphics, pFont, 4, pColor, partialTicks);
      }

      private int getTextureOffset() {
         int i = 0;
         if (this.isSelected()) {
            return i + 20;
         } else if (this.isDisabled()) {
            return i + 10;
         } else {
            return this.isHovered() ? i + 30 : i;
         }
      }

      protected static void renderScrollingString(
         GuiGraphics pGuiGraphics, Font pFont, Component pText, int pMinX, int pMinY, int pMaxX, int pMaxY, int pColor, float ticks
      ) {
         int i = pFont.width(pText);
         int j = (pMinY + pMaxY - 9) / 2 + 1;
         int k = pMaxX - pMinX;
         if (i > k) {
            int l = i - k;
            double d0 = ticks / 20.0;
            double d1 = Math.max(l * 0.5, 3.0);
            double d2 = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d0 / d1) + 3.141592653589793) / 2.0 + 0.5;
            double d3 = Mth.lerp(d2, 0.0, l);
            pGuiGraphics.enableScissor(pMinX - 1, pMinY, pMaxX + 1, pMaxY);
            pGuiGraphics.drawString(pFont, pText, pMinX - (int)d3, j, pColor);
            pGuiGraphics.disableScissor();
         } else {
            pGuiGraphics.drawCenteredString(pFont, pText, (pMinX + pMaxX) / 2, j, pColor);
         }
      }

      protected void renderScrollingString(GuiGraphics pGuiGraphics, Font pFont, int pWidth, int pColor, float partialTicks) {
         int i = this.getX() + pWidth;
         int j = this.getX() + this.getWidth() - pWidth;
         renderScrollingString(
            pGuiGraphics,
            pFont,
            Component.literal(this.name),
            i,
            this.getY(partialTicks),
            j,
            this.getY(partialTicks) + this.getHeight(),
            pColor,
            OwlCourierSendScreen.this.ticks + partialTicks
         );
      }

      public void onClick(double pMouseX, double pMouseY) {
         this.onPress.onPress(this);
      }

      public void onRelease(double pMouseX, double pMouseY) {
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         if (this.isValidClickButton(pButton)) {
            boolean flag = this.clicked(pMouseX, pMouseY);
            if (flag) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               this.onClick(pMouseX, pMouseY);
               return true;
            }
         }

         return false;
      }

      public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
         if (this.isValidClickButton(pButton)) {
            this.onRelease(pMouseX, pMouseY);
            return true;
         } else {
            return false;
         }
      }

      protected boolean isValidClickButton(int pButton) {
         return pButton == 0;
      }

      protected boolean clicked(double pMouseX, double pMouseY) {
         return this.isHovered() && !this.isDisabled();
      }

      public interface OnPress {
         void onPress(OwlCourierSendScreen.ListButton var1);
      }
   }

   private class ScissorArea {
      protected int width;
      protected int height;
      private int x;
      private int y;

      public ScissorArea(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
}
