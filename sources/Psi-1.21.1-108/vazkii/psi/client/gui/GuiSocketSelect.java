package vazkii.psi.client.gui;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.cad.ISocketableController;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.client.core.handler.KeybindHandler;
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageChangeControllerSlot;
import vazkii.psi.common.network.message.MessageChangeSocketableSlot;

public class GuiSocketSelect extends Screen {
   final Minecraft mc;
   float timeIn = 0.0F;
   int slotSelected = -1;
   ItemStack controllerStack;
   ISocketableController controller;
   ItemStack[] controlledStacks;
   int controlSlot;
   ItemStack socketableStack;
   ISocketable socketable;
   List<Integer> slots;
   List<ResourceLocation> signs;

   public GuiSocketSelect(ItemStack stack) {
      super(Component.empty());
      this.mc = Minecraft.getInstance();
      this.controllerStack = ItemStack.EMPTY;
      this.socketableStack = ItemStack.EMPTY;
      if (ISocketable.isSocketable(stack)) {
         this.setSocketable(stack);
      } else if (stack.getItem() instanceof ISocketableController) {
         this.controllerStack = stack;
         this.controller = (ISocketableController)stack.getItem();
         this.controlledStacks = this.controller.getControlledStacks(this.mc.player, stack);
         this.controlSlot = this.controller.getDefaultControlSlot(this.controllerStack);
         if (this.controlSlot >= this.controlledStacks.length) {
            this.controlSlot = 0;
         }

         this.setSocketable(this.controlledStacks.length == 0 ? ItemStack.EMPTY : this.controlledStacks[this.controlSlot]);
      }
   }

   private static double mouseAngle(int x, int y, int mx, int my) {
      return (Mth.atan2(my - y, mx - x) + 6.283185307179586) % 6.283185307179586;
   }

   public void setSocketable(ItemStack stack) {
      if (stack.isEmpty()) {
         this.slots = new ArrayList<>();
      } else {
         this.socketableStack = stack;
         this.socketable = ISocketable.socketable(stack);
         this.slots = this.socketable.getRadialMenuSlots();
         this.signs = this.socketable.getRadialMenuIcons();
      }
   }

   public void render(@NotNull GuiGraphics graphics, int mx, int my, float delta) {
      super.render(graphics, mx, my, delta);
      this.timeIn += delta;
      int x = this.width / 2;
      int y = this.height / 2;
      int maxRadius = 80;
      double angle = mouseAngle(x, y, mx, my);
      int segments = this.slots.size();
      float step = 0.017453292F;
      float degPer = 6.2831855F / segments;
      ItemStack cadStack = PsiAPI.getPlayerCAD(Minecraft.getInstance().player);
      this.slotSelected = -1;
      Tesselator tess = Tesselator.getInstance();
      RenderSystem.disableCull();
      RenderSystem.enableBlend();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      if (segments != 0) {
         BufferBuilder buf = tess.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

         for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0.0F, Math.min((this.timeIn - seg * 6.0F / segments) * 40.0F, (float)maxRadius));
            if (mouseInSector || seg == this.socketable.getSelectedSlot()) {
               radius *= 1.025F;
            }

            int gs = 64;
            if (seg % 2 == 0) {
               gs += 25;
            }

            int r = gs;
            int g = gs;
            int b = gs;
            int a = 102;
            if (seg == 0) {
               buf.addVertex(x, y, 0.0F).setColor(gs, gs, gs, a);
            }

            if (mouseInSector) {
               this.slotSelected = seg;
               if (!cadStack.isEmpty()) {
                  int color = Psi.proxy.getColorForCAD(cadStack);
                  r = PsiRenderHelper.r(color);
                  g = PsiRenderHelper.g(color);
                  b = PsiRenderHelper.b(color);
               } else {
                  b = 255;
                  g = 255;
                  r = 255;
               }
            } else if (seg == this.socketable.getSelectedSlot()) {
               if (!cadStack.isEmpty()) {
                  int color = Psi.proxy.getColorForCAD(cadStack);
                  r = 255 - PsiRenderHelper.r(color);
                  g = 255 - PsiRenderHelper.g(color);
                  b = 255 - PsiRenderHelper.b(color);
               } else {
                  r = 0;
                  g = 255;
                  b = 0;
               }
            }

            for (float i = 0.0F; i < degPer + step / 2.0F; i += step) {
               float rad = i + seg * degPer;
               float xp = x + Mth.cos(rad) * radius;
               float yp = y + Mth.sin(rad) * radius;
               if (i == 0.0F) {
                  buf.addVertex(xp, yp, 0.0F).setColor(r, g, b, a);
               }

               buf.addVertex(xp, yp, 0.0F).setColor(r, g, b, a);
            }
         }

         BufferUploader.drawWithShader(buf.buildOrThrow());
      }

      for (int seg = 0; seg < segments; seg++) {
         boolean mouseInSectorx = degPer * seg < angle && angle < degPer * (seg + 1);
         float radiusx = Math.max(0.0F, Math.min((this.timeIn - seg * 6.0F / segments) * 40.0F, (float)maxRadius));
         if (mouseInSectorx || seg == this.socketable.getSelectedSlot()) {
            radiusx *= 1.025F;
         }

         float rad = (seg + 0.5F) * degPer;
         float xp = x + Mth.cos(rad) * radiusx;
         float yp = y + Mth.sin(rad) * radiusx;
         ItemStack stack = this.socketable.getBulletInSocket(seg);
         if (!stack.isEmpty()) {
            float xsp = xp - 4.0F;
            float ysp = yp;
            String name = (mouseInSectorx ? ChatFormatting.UNDERLINE : ChatFormatting.RESET) + stack.getHoverName().getString();
            int width = this.font.width(name);
            double mod = 0.6;
            int xdp = (int)((xp - x) * mod + x);
            int ydp = (int)((yp - y) * mod + y);
            graphics.renderFakeItem(stack, xdp - 8, ydp - 8);
            if (xsp < x) {
               xsp -= width - 8;
            }

            if (yp < y) {
               ysp = yp - 9.0F;
            }

            graphics.drawString(this.font, name, xsp, ysp, 16777215, true);
            if (seg == this.socketable.getSelectedSlot()) {
               int color = 65280;
               if (!cadStack.isEmpty()) {
                  color = 16711680 - Psi.proxy.getColorForCAD(cadStack);
               }

               graphics.drawString(this.font, I18n.get("psimisc.selected", new Object[0]), xsp + width / 4.0F, ysp + 9.0F, color, true);
            }

            mod = 0.8;
            xdp = (int)((xp - x) * mod + x);
            ydp = (int)((yp - y) * mod + y);
            graphics.blit(this.signs.get(seg), xdp - 8, ydp - 8, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      float shift = Math.min(5.0F, this.timeIn) / 5.0F;
      float scale = 3.0F * shift;
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(770, 771, 1, 0);
      if (this.controlledStacks != null && this.controlledStacks.length > 0) {
         int xs = this.width / 2 - 18 * this.controlledStacks.length / 2 + 1;
         int ys = this.height / 2;

         for (int i = 0; i < this.controlledStacks.length; i++) {
            float yoff = 20.0F + maxRadius;
            if (i == this.controlSlot) {
               yoff += 5.0F;
            }

            ItemStack stack = this.controlledStacks[i];
            int rx = xs + i * 18;
            float ry = ys + -yoff * shift;
            graphics.renderFakeItem(stack, rx, (int)ry);
         }
      }

      if (!this.socketableStack.isEmpty()) {
         graphics.pose().pushPose();
         graphics.pose().scale(scale, scale, scale);
         graphics.renderFakeItem(this.socketableStack, (int)(x / scale) - 8, (int)(y / scale) - 8);
         graphics.pose().popPose();
      }

      RenderSystem.disableBlend();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (!this.controllerStack.isEmpty() && this.controlledStacks.length > 0) {
         if (mouseButton == 0) {
            this.controlSlot++;
            if (this.controlSlot >= this.controlledStacks.length) {
               this.controlSlot = 0;
            }
         } else if (mouseButton == 1) {
            this.controlSlot--;
            if (this.controlSlot < 0) {
               this.controlSlot = this.controlledStacks.length - 1;
            }
         }

         this.setSocketable(this.controlledStacks[this.controlSlot]);
         return true;
      } else {
         return false;
      }
   }

   public void tick() {
      super.tick();
      if (!this.isKeyDown(KeybindHandler.keybind)) {
         this.mc.setScreen(null);
         if (this.slotSelected != -1) {
            int slot = this.slots.get(this.slotSelected);
            PlayerDataHandler.get(this.mc.player).stopLoopcast();
            CustomPacketPayload message;
            if (!this.controllerStack.isEmpty()) {
               message = new MessageChangeControllerSlot(this.controlSlot, slot);
            } else {
               message = new MessageChangeSocketableSlot(slot);
            }

            MessageRegister.sendToServer(message);
         }
      }

      ImmutableSet<KeyMapping> set = ImmutableSet.of(
         this.mc.options.keyUp,
         this.mc.options.keyLeft,
         this.mc.options.keyDown,
         this.mc.options.keyRight,
         this.mc.options.keyShift,
         this.mc.options.keySprint,
         new KeyMapping[]{this.mc.options.keyJump}
      );
      UnmodifiableIterator var5 = set.iterator();

      while (var5.hasNext()) {
         KeyMapping k = (KeyMapping)var5.next();
         KeyMapping.set(k.getKey(), this.isKeyDown(k));
      }
   }

   public boolean isKeyDown(KeyMapping keybind) {
      Key key = keybind.getKey();
      return key.getType() == Type.MOUSE
         ? GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), key.getValue()) == 1
         : InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getValue());
   }

   public boolean isPauseScreen() {
      return false;
   }
}
