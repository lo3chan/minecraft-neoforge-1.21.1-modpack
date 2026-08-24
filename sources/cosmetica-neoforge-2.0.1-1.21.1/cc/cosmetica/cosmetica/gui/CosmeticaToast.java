package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.impl.PoseCanvas;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class CosmeticaToast implements Toast {
   private final Text title;
   @Nullable
   private final Text description;
   private boolean changed;
   private long lastChanged;
   private static final ResourceKey TEXTURE = new ResourceKey("cosmetica", "textures/toast.png");

   public CosmeticaToast(Text text, @Nullable Text description) {
      this.title = text;
      this.description = description;
      this.changed = true;
   }

   public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long l) {
      if (this.changed) {
         this.changed = false;
         this.lastChanged = l;
      }

      PoseStack poseStack = graphics.pose();
      Canvas canvas = new PoseCanvas(graphics, toastComponent.getMinecraft(), null, 0.0F);
      int i = this.width();
      graphics.blit(TEXTURE.toResourceLocation(), 0, 0, i, this.height(), 0.0F, 0.0F, 360, 64, 360, 64);
      Matrix4f arg = poseStack.last().pose();
      Vector4f pos = new Vector4f(18.0F, 12.0F, 0.0F, 0.0F);
      pos.mul(arg);
      if (this.description == null) {
         canvas.drawText(this.title, (int)pos.x() + 17, (int)pos.y(), -256);
      } else {
         canvas.drawText(this.title, (int)pos.x() + 17, (int)pos.y() - 6, -256);
         canvas.drawText(this.description, (int)pos.x() + 17, (int)pos.y() + 6, -1);
      }

      return l - this.lastChanged < 5000L ? Visibility.SHOW : Visibility.HIDE;
   }

   public int width() {
      return 180;
   }
}
