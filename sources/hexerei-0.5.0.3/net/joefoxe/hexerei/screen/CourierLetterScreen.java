package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CourierLetterUpdatePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class CourierLetterScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/courier_letter_gui.png");
   private final String[] messages;
   private OwlEntity.MessageText text;
   private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
   @Nullable
   private TextFieldHelper messageField;
   int frame;
   int line;
   int img_width = 172;
   int img_height = 164;
   int left;
   int top;
   int slotIndex;
   InteractionHand hand;
   boolean dirty = false;
   boolean clicked = false;
   int clickedTicks = 0;
   int clickedTicksOld = 0;
   boolean sealed = false;

   public CourierLetterScreen(int slotIndex, InteractionHand hand, ItemStack stack) {
      super(Component.translatable("screen.hexerei.letter"));
      this.text = new OwlEntity.MessageText();
      CustomData tag = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
      if (tag != null) {
         if (tag.contains("Message")) {
            OwlEntity.MessageText.DIRECT_CODEC
               .parse(NbtOps.INSTANCE, tag.copyTag().getCompound("Message"))
               .resultOrPartial(LOGGER::error)
               .ifPresent(message -> this.text = this.loadLines(message));
         }

         if (tag.contains("Sealed")) {
            this.sealed = tag.copyTag().getBoolean("Sealed");
         }
      }

      this.minecraft = Minecraft.getInstance();
      this.messages = IntStream.range(0, 12).mapToObj(p_277214_ -> this.text.getMessage(p_277214_)).map(Component::getString).toArray(String[]::new);
      this.frame = 0;
      this.line = 0;
      this.slotIndex = slotIndex;
      this.hand = hand;
   }

   public boolean isEmpty() {
      for (String str : this.messages) {
         if (!str.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private OwlEntity.MessageText loadLines(OwlEntity.MessageText pText) {
      for (int i = 0; i < 12; i++) {
         Component component = pText.getMessage(i);
         pText = pText.setMessage(i, component);
      }

      return pText;
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   public void setChanged() {
      this.setDirty(true);
   }

   public boolean mouseClicked(double x, double y, int pButton) {
      if (pButton == 0) {
         for (int i = 0; i < 12; i++) {
            if (x > this.left + 5
               && x < this.left + 5 + 162
               && y > this.top + 25 + this.getTextLineHeight() * i
               && y < this.top + 25 + this.getTextLineHeight() + this.getTextLineHeight() * i) {
               this.line = i;
               this.messageField.setCursorToEnd();
               return true;
            }
         }

         if (!this.sealed && x > this.left + 71 && x < this.left + 71 + 30 && y > this.top + 151 && y < this.top + 151 + 15 && !this.isEmpty()) {
            this.clicked = true;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.25F));
            return true;
         }
      }

      return super.mouseClicked(x, y, pButton);
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (!this.sealed && this.clicked && pButton == 0 && !this.isEmpty()) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.25F));
         this.clicked = false;
      }

      return super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   public boolean mouseDragged(double x, double y, int pButton, double pDragX, double pDragY) {
      if (!this.sealed
         && this.clicked
         && pButton == 0
         && !this.isEmpty()
         && (!(x > this.left + 71) || !(x < this.left + 71 + 30) || !(y > this.top + 151) || !(y < this.top + 151 + 15))) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.25F));
         this.clicked = false;
      }

      return super.mouseDragged(x, y, pButton, pDragX, pDragY);
   }

   protected void init() {
      this.left = this.width / 2 - this.img_width / 2;
      this.top = this.height / 2 - this.img_height / 2;
      this.messageField = new TextFieldHelper(
         () -> this.messages[this.line],
         this::setMessage,
         TextFieldHelper.createClipboardGetter(this.minecraft),
         TextFieldHelper.createClipboardSetter(this.minecraft),
         string -> this.minecraft.font.width(string) <= this.getMaxTextLineWidth()
      );
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (pKeyCode == this.minecraft.options.keyInventory.getKey().getValue() && this.sealed) {
         this.onClose();
         return true;
      } else if (pKeyCode == 265) {
         this.line = (this.line - 1 + 12) % 12;
         this.messageField.setCursorToEnd();
         return true;
      } else if (pKeyCode == 258) {
         this.messageField.insertText("    ");
         return true;
      } else if (pKeyCode != 264 && pKeyCode != 257 && pKeyCode != 335) {
         return this.messageField.keyPressed(pKeyCode) ? true : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      } else {
         this.line = (this.line + 1 + 12) % 12;
         this.messageField.setCursorToEnd();
         return true;
      }
   }

   public boolean charTyped(char pCodePoint, int pModifiers) {
      this.messageField.charTyped(pCodePoint);
      return true;
   }

   public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      Lighting.setupForFlatItems();
      this.renderTransparentBackground(pGuiGraphics);
      pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.top + 4, 3355443);
      pGuiGraphics.blit(this.GUI, this.left, this.top, 0, 0, this.img_width, this.img_height);
      if (!this.sealed) {
         if (!this.isEmpty()) {
            if (this.clicked) {
               pGuiGraphics.blit(this.GUI, this.left + 71, this.top + 151, 1, 224, 30, 15);
            } else if (pMouseX > this.left + 71 && pMouseX < this.left + 71 + 30 && pMouseY > this.top + 151 && pMouseY < this.top + 151 + 15) {
               pGuiGraphics.blit(this.GUI, this.left + 71, this.top + 151, 1, 240, 30, 15);
            } else {
               pGuiGraphics.blit(this.GUI, this.left + 71, this.top + 151, 1, 192, 30, 15);
            }

            float alpha = 1.0F - Mth.clamp(Mth.lerp(pPartialTick, this.clickedTicksOld, this.clickedTicks) / 15.0F, 0.0F, 1.0F);
            if (alpha > 0.05F) {
               pGuiGraphics.pose().pushPose();
               pGuiGraphics.pose().translate(this.left, this.top, 2.0F);
               pGuiGraphics.drawString(
                  this.font, Component.translatable("hexerei.package_letter.seal"), 75, 155, HexereiUtil.getColorValueAlpha(0.2F, 0.2F, 0.2F, alpha), false
               );
               pGuiGraphics.pose().popPose();
            }

            float xOffset = Mth.clamp(31.0F * (Mth.lerp(pPartialTick, this.clickedTicksOld, this.clickedTicks) / 12.0F), 0.0F, 30.0F);
            float xOffset2 = Mth.clamp(7.0F * (Mth.lerp(pPartialTick, this.clickedTicksOld - 12, this.clickedTicks - 12) / 3.0F), 0.0F, 6.0F);
            float xOffset3 = Mth.clamp(7.0F * (Mth.lerp(pPartialTick, this.clickedTicksOld - 5, this.clickedTicks - 5) / 5.0F), 0.0F, 6.0F);
            float yOffset = Mth.clamp(10.0F * (Mth.lerp(pPartialTick, this.clickedTicksOld - 16, this.clickedTicks - 16) / 2.0F), 0.0F, 9.0F);
            this.floatBlit(
               pGuiGraphics,
               this.GUI,
               this.left + 71,
               this.left + 71 + xOffset,
               this.top + 151,
               this.top + 160,
               3.0F,
               0.00390625F,
               (1.0F + xOffset) / 256.0F,
               0.66796875F,
               0.703125F
            );
            this.floatBlit(
               pGuiGraphics,
               this.GUI,
               this.left + 92 + 6 - xOffset2,
               this.left + 92 + 6,
               this.top + 159,
               this.top + 164,
               4.0F,
               (23.0F - xOffset2) / 256.0F,
               0.08984375F,
               0.7109375F,
               0.73046875F
            );
            this.floatBlit(
               pGuiGraphics,
               this.GUI,
               this.left + 91 - 17,
               this.left + 91 - 17 + xOffset3,
               this.top + 158,
               this.top + 158 + xOffset3,
               4.0F,
               0.0390625F,
               (10.0F + xOffset3) / 256.0F,
               0.7109375F,
               (182.0F + xOffset3) / 256.0F
            );
            this.floatBlit(
               pGuiGraphics,
               this.GUI,
               this.left + 82,
               this.left + 82 + 8,
               this.top + 154,
               this.top + 154 + yOffset,
               5.0F,
               0.00390625F,
               0.03515625F,
               0.7109375F,
               (182.0F + yOffset) / 256.0F
            );
         } else {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(this.left, this.top, 2.0F);
            pGuiGraphics.drawString(this.font, Component.translatable("hexerei.package_letter.seal"), 75, 155, 3355443, false);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.blit(this.GUI, this.left + 71, this.top + 151, 1, 208, 30, 15);
         }
      }

      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(this.left + 86, this.top + 85, 4.0F);
      pGuiGraphics.pose().scale(TEXT_SCALE.x(), TEXT_SCALE.y(), TEXT_SCALE.z());
      int col = DyeColor.BLACK.getTextColor();
      boolean flag = this.frame / 6 % 2 == 0;
      int cursorPos = this.messageField.getCursorPos();
      int selectionPos = this.messageField.getSelectionPos();
      int l = 12 * this.getTextLineHeight() / 2;
      int i1 = this.line * this.getTextLineHeight() - l;

      for (int i = 0; i < this.messages.length; i++) {
         String str = this.messages[i];
         if (str != null) {
            if (this.font.isBidirectional()) {
               str = this.font.bidirectionalShaping(str);
            }

            pGuiGraphics.drawString(this.font, str, -75, i * this.getTextLineHeight() - l, col, false);
            if (i == this.line && cursorPos >= 0 && flag && !this.sealed && cursorPos >= str.length()) {
               pGuiGraphics.drawString(this.font, "_", this.font.width(str.substring(0, Math.max(Math.min(cursorPos, str.length()), 0))) - 75, i1, col, false);
            }
         }
      }

      for (int k3 = 0; k3 < this.messages.length; k3++) {
         String s1 = this.messages[k3];
         if (s1 != null && k3 == this.line && cursorPos >= 0) {
            int l3 = this.font.width(s1.substring(0, Math.max(Math.min(cursorPos, s1.length()), 0)));
            int i4 = l3 - 75;
            if (flag && cursorPos < s1.length() && !this.sealed) {
               pGuiGraphics.fill(i4, i1 - 1, i4 + 1, i1 + this.getTextLineHeight(), 0xFF000000 | col);
            }

            if (selectionPos != cursorPos) {
               int j4 = Math.min(cursorPos, selectionPos);
               int j2 = Math.max(cursorPos, selectionPos);
               int k2 = this.font.width(s1.substring(0, j4)) - 75;
               int l2 = this.font.width(s1.substring(0, j2)) - 75;
               int i3 = Math.min(k2, l2);
               int j3 = Math.max(k2, l2);
               pGuiGraphics.fill(RenderType.guiTextHighlight(), i3, i1, j3, i1 + this.getTextLineHeight(), -16776961);
            }
         }
      }

      pGuiGraphics.pose().popPose();
      List<Component> tooltipLines = new ArrayList<>();
      if (!this.clicked
         && !this.sealed
         && pMouseX > this.left + 71
         && pMouseX < this.left + 71 + 30
         && pMouseY > this.top + 151
         && pMouseY < this.top + 151 + 15) {
         tooltipLines.add(Component.translatable("hexerei.package_letter.letter_seal_tooltip").withStyle(ChatFormatting.DARK_GRAY));
         tooltipLines.add(Component.translatable("hexerei.package_letter.letter_seal_tooltip2").withStyle(ChatFormatting.DARK_GRAY));
      }

      if (!tooltipLines.isEmpty()) {
         pGuiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), pMouseX, pMouseY);
      }

      Lighting.setupFor3DItems();
   }

   void floatBlit(
      GuiGraphics guiGraphics,
      ResourceLocation pAtlasLocation,
      float pX1,
      float pX2,
      float pY1,
      float pY2,
      float pBlitOffset,
      float pMinU,
      float pMaxU,
      float pMinV,
      float pMaxV
   ) {
      RenderSystem.setShaderTexture(0, pAtlasLocation);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      Matrix4f matrix4f = guiGraphics.pose().last().pose();
      BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.addVertex(matrix4f, pX1, pY1, pBlitOffset).setUv(pMinU, pMinV);
      bufferbuilder.addVertex(matrix4f, pX1, pY2, pBlitOffset).setUv(pMinU, pMaxV);
      bufferbuilder.addVertex(matrix4f, pX2, pY2, pBlitOffset).setUv(pMaxU, pMaxV);
      bufferbuilder.addVertex(matrix4f, pX2, pY1, pBlitOffset).setUv(pMaxU, pMinV);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
   }

   public Component getTitle() {
      return super.getTitle();
   }

   public int getTextLineHeight() {
      return 10;
   }

   public int getMaxTextLineWidth() {
      return 150;
   }

   private void onDone() {
      this.minecraft.setScreen(null);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      this.frame++;
      if (!this.sealed) {
         this.clickedTicksOld = this.clickedTicks;
         if (this.clicked) {
            if (this.clickedTicks % 8 == 0) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_INSERT, 1.5F, 0.75F));
            }

            this.clickedTicks++;
            if (this.clickedTicks > 25) {
               this.clicked = false;
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_DROP_CONTENTS, 1.8F, 0.75F));
               OwlEntity.MessageText.DIRECT_CODEC
                  .encodeStart(NbtOps.INSTANCE, this.text)
                  .resultOrPartial(LOGGER::error)
                  .ifPresent(tag -> HexereiPacketHandler.sendToServer(new CourierLetterUpdatePacket(this.slotIndex, (CompoundTag)tag, true)));
               this.onClose();
               return;
            }
         } else {
            this.clickedTicks--;
            if (this.clickedTicks < 0) {
               this.clickedTicks = 0;
            }
         }

         if (this.frame % 5 == 0 && this.dirty) {
            OwlEntity.MessageText.DIRECT_CODEC
               .encodeStart(NbtOps.INSTANCE, this.text)
               .resultOrPartial(LOGGER::error)
               .ifPresent(tag -> HexereiPacketHandler.sendToServer(new CourierLetterUpdatePacket(this.slotIndex, (CompoundTag)tag, false)));
            this.dirty = false;
         }
      }

      if (!this.isValid()) {
         this.onDone();
      }
   }

   private boolean isValid() {
      return this.minecraft != null && this.minecraft.player != null && Inventory.isHotbarSlot(this.slotIndex)
         ? this.minecraft.player.getInventory().selected == this.slotIndex
         : false;
   }

   private void setMessage(String string) {
      if (!this.sealed) {
         this.messages[this.line] = string;
         this.text = this.text.setMessage(this.line, Component.literal(string));
         this.setChanged();
      }
   }
}
