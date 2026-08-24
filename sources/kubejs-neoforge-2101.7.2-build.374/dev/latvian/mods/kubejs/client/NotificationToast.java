package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.latvian.mods.kubejs.client.icon.KubeIconRenderer;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextWrapper;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;

public class NotificationToast implements Toast {
   private final NotificationToastData notification;
   private final long duration;
   private final KubeIconRenderer icon;
   private final List<FormattedCharSequence> text;
   private int width;
   private int height;
   private long lastChanged;
   private boolean changed;

   public NotificationToast(Minecraft mc, NotificationToastData notification) {
      this.notification = notification;
      this.duration = notification.duration().toMillis();
      this.icon = notification.icon().map(KubeIconRenderer::from).orElse(null);
      this.text = new ArrayList<>(2);
      this.width = 0;
      this.height = 0;
      if (!TextWrapper.isEmpty(notification.text())) {
         this.text.addAll(mc.font.split(notification.text(), 240));
      }

      for (FormattedCharSequence l : this.text) {
         this.width = Math.max(this.width, mc.font.width(l));
      }

      this.width += 12;
      if (this.icon != null) {
         this.width += 24;
      }

      this.height = Math.max(this.text.size() * 10 + 12, 28);
      if (this.text.isEmpty() && this.icon != null) {
         this.width = 28;
         this.height = 28;
      }
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   private void drawRectangle(Matrix4f m, int x0, int y0, int x1, int y1, int r, int g, int b) {
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      buf.addVertex(m, x0, y1, 0.0F).setColor(r, g, b, 255);
      buf.addVertex(m, x1, y1, 0.0F).setColor(r, g, b, 255);
      buf.addVertex(m, x1, y0, 0.0F).setColor(r, g, b, 255);
      buf.addVertex(m, x0, y0, 0.0F).setColor(r, g, b, 255);
      BufferUploader.drawWithShader(buf.buildOrThrow());
   }

   public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long l) {
      if (this.changed) {
         this.lastChanged = l;
         this.changed = false;
      }

      Minecraft mc = toastComponent.getMinecraft();
      PoseStack poseStack = graphics.pose();
      poseStack.pushPose();
      poseStack.translate(-2.0, 2.0, 0.0);
      Matrix4f m = poseStack.last().pose();
      int w = this.width();
      int h = this.height();
      int oc = this.notification.outlineColor().orElse(NotificationToastData.DEFAULT_OUTLINE_COLOR).kjs$getRGB();
      int ocr = ARGB32.red(oc);
      int ocg = ARGB32.green(oc);
      int ocb = ARGB32.blue(oc);
      int bc = this.notification.borderColor().orElse(NotificationToastData.DEFAULT_BORDER_COLOR).kjs$getRGB();
      int bcr = ARGB32.red(bc);
      int bcg = ARGB32.green(bc);
      int bcb = ARGB32.blue(bc);
      int bgc = this.notification.backgroundColor().orElse(NotificationToastData.DEFAULT_BACKGROUND_COLOR).kjs$getRGB();
      int bgcr = ARGB32.red(bgc);
      int bgcg = ARGB32.green(bgc);
      int bgcb = ARGB32.blue(bgc);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      this.drawRectangle(m, 2, 0, w - 2, h, ocr, ocg, ocb);
      this.drawRectangle(m, 0, 2, w, h - 2, ocr, ocg, ocb);
      this.drawRectangle(m, 1, 1, w - 1, h - 1, ocr, ocg, ocb);
      this.drawRectangle(m, 2, 1, w - 2, h - 1, bcr, bcg, bcb);
      this.drawRectangle(m, 1, 2, w - 1, h - 2, bcr, bcg, bcb);
      this.drawRectangle(m, 2, 2, w - 2, h - 2, bgcr, bgcg, bgcb);
      if (this.icon != null) {
         this.icon.draw(mc, graphics, 14, h / 2, this.notification.iconSize());
      }

      int th = this.icon == null ? 6 : 26;
      int tv = (h - this.text.size() * 10) / 2 + 1;

      for (int i = 0; i < this.text.size(); i++) {
         graphics.drawString(mc.font, this.text.get(i), th, tv + i * 10, 16777215, this.notification.textShadow());
      }

      poseStack.popPose();
      return l - this.lastChanged < this.duration ? Visibility.SHOW : Visibility.HIDE;
   }
}
