package net.mehvahdjukaar.moonlight.api.client.gui.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;

public class ScreenParticle {
   private final List<ResourceLocation> sprites;
   private float x;
   private float y;
   private float velocityX;
   private float velocityY;
   private float gravity;
   private float drag;
   private float rotation;
   private float spin;
   private float startSize = 4.0F;
   private float endSize = 4.0F;
   private float startAlpha = 1.0F;
   private float endAlpha = 1.0F;
   private float fadeOutStart = 1.0F;
   private int tint = 16777215;
   private float lifetime = 1.0F;
   private float age;

   protected ScreenParticle(List<ResourceLocation> sprites, float x, float y) {
      this.sprites = sprites;
      this.x = x;
      this.y = y;
   }

   public static ScreenParticle sprite(ResourceLocation sprite, float x, float y) {
      return new ScreenParticle(List.of(sprite), x, y);
   }

   public static ScreenParticle animated(List<ResourceLocation> frames, float x, float y) {
      if (frames.isEmpty()) {
         throw new IllegalArgumentException("Animated screen particle needs at least one frame");
      } else {
         return new ScreenParticle(List.copyOf(frames), x, y);
      }
   }

   public static ScreenParticle randomSprite(List<ResourceLocation> choices, RandomSource random, float x, float y) {
      return sprite(choices.get(random.nextInt(choices.size())), x, y);
   }

   public static ScreenParticle square(float x, float y) {
      return new ScreenParticle(List.of(), x, y);
   }

   public ScreenParticle velocity(float x, float y) {
      this.velocityX = x;
      this.velocityY = y;
      return this;
   }

   public ScreenParticle gravity(float gravity) {
      this.gravity = gravity;
      return this;
   }

   public ScreenParticle drag(float drag) {
      this.drag = Math.max(0.0F, drag);
      return this;
   }

   public ScreenParticle rotation(float degrees) {
      this.rotation = degrees;
      return this;
   }

   public ScreenParticle spin(float degreesPerSecond) {
      this.spin = degreesPerSecond;
      return this;
   }

   public ScreenParticle size(float start, float end) {
      this.startSize = start;
      this.endSize = end;
      return this;
   }

   public ScreenParticle size(float size) {
      return this.size(size, size);
   }

   public ScreenParticle alpha(float start, float end) {
      this.startAlpha = start;
      this.endAlpha = end;
      return this;
   }

   public ScreenParticle alpha(float alpha) {
      return this.alpha(alpha, alpha);
   }

   public ScreenParticle fadeOut(float lifeFraction) {
      this.fadeOutStart = Mth.clamp(lifeFraction, 0.0F, 1.0F);
      return this;
   }

   public ScreenParticle tint(int rgb) {
      this.tint = rgb & 16777215;
      return this;
   }

   public ScreenParticle lifetime(float seconds) {
      this.lifetime = Math.max(0.01F, seconds);
      return this;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public boolean tick(float dt) {
      this.age += dt;
      if (this.age >= this.lifetime) {
         return false;
      } else {
         this.velocityY = this.velocityY + this.gravity * dt;
         if (this.drag > 0.0F) {
            float kept = (float)Math.exp(-this.drag * dt);
            this.velocityX *= kept;
            this.velocityY *= kept;
         }

         this.x = this.x + this.velocityX * dt;
         this.y = this.y + this.velocityY * dt;
         this.rotation = this.rotation + this.spin * dt;
         return true;
      }
   }

   public void render(GuiGraphics graphics) {
      float t = this.age / this.lifetime;
      float size = Mth.lerp(t, this.startSize, this.endSize);
      if (!(size <= 0.0F)) {
         float alpha = Mth.clamp(Mth.lerp(t, this.startAlpha, this.endAlpha), 0.0F, 1.0F);
         if (t > this.fadeOutStart) {
            alpha *= 1.0F - Mth.inverseLerp(t, this.fadeOutStart, 1.0F);
         }

         if (!(alpha <= 0.0F)) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(this.x, this.y, 0.0F);
            if (this.rotation != 0.0F) {
               pose.mulPose(Axis.ZP.rotationDegrees(this.rotation));
            }

            pose.scale(size, size, 1.0F);
            pose.translate(-0.5F, -0.5F, 0.0F);
            if (this.sprites.isEmpty()) {
               graphics.fill(0, 0, 1, 1, ARGB32.color(Mth.floor(alpha * 255.0F), this.tint));
            } else {
               graphics.setColor(ARGB32.red(this.tint) / 255.0F, ARGB32.green(this.tint) / 255.0F, ARGB32.blue(this.tint) / 255.0F, alpha);
               graphics.blitSprite(this.currentFrame(t), 0, 0, 1, 1);
               graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            pose.popPose();
         }
      }
   }

   private ResourceLocation currentFrame(float lifeFraction) {
      int frames = this.sprites.size();
      return frames == 1 ? (ResourceLocation)this.sprites.getFirst() : this.sprites.get(Mth.clamp(Mth.floor(lifeFraction * frames), 0, frames - 1));
   }
}
