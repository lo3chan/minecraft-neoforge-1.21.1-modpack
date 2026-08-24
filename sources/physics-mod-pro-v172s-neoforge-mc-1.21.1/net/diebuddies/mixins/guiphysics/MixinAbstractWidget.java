package net.diebuddies.mixins.guiphysics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.BodyType;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.math.Math;
import net.diebuddies.physics.Box2DUtil;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.diebuddies.physics.settings.gui.ScreenExtension;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractWidget.class})
public class MixinAbstractWidget implements Animatable {
   @Shadow
   private int width;
   @Shadow
   private int height;
   @Shadow
   private int x;
   @Shadow
   private int y;
   @Unique
   private double positionX;
   @Unique
   private double positionY;
   @Unique
   private float rotation;
   @Unique
   private Body buttonBody;
   @Unique
   private float totalDelta;
   @Unique
   private boolean isSimActive;
   @Unique
   private float animX;
   @Unique
   private float animY;
   @Unique
   private float animWidth;
   @Unique
   private float animHeight;
   @Unique
   private List<Animator> animations;
   @Unique
   private float animRed = 1.0F;
   @Unique
   private float animGreen = 1.0F;
   @Unique
   private float animBlue = 1.0F;
   @Unique
   private float animAlpha = 1.0F;
   @Unique
   private float animDepth = -100.0F;
   @Unique
   private float renderPercent = 0.0F;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   public void constructor(int x, int y, int width, int height, Component component, CallbackInfo info) {
      this.animX = x;
      this.animY = y;
      this.animWidth = width;
      this.animHeight = height;
      this.animations = new ObjectArrayList();
   }

   @Unique
   private void physicsSetup() {
      Screen screen = Minecraft.getInstance().screen;
      if (screen != null && !(screen instanceof PhysicsSettingsScreen)) {
         World world = ((ScreenExtension)screen).getPhysicsWorld();
         AbstractWidget widget = (AbstractWidget)this;
         this.buttonBody = Box2DUtil.createBox(world, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), BodyType.DYNAMIC);
         this.buttonBody.setAngularVelocity(Math.random() * 10.0F - 5.0F);
         this.buttonBody.setLinearVelocity(new Vec2(Math.random() * 100.0F - 50.0F, Math.random() * 100.0F - 50.0F));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"},
      cancellable = true
   )
   public void renderHead(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      this.renderPercent += delta;

      while (this.renderPercent >= 1.0F) {
         this.renderPercent--;

         for (Animator animator : this.animations) {
            animator.tick(this);
         }
      }

      Screen screen = Minecraft.getInstance().screen;
      if (screen != null) {
         World world = ((ScreenExtension)screen).getPhysicsWorld();
         if (world != null && (this.buttonBody == null || world != this.buttonBody.getWorld())) {
            this.physicsSetup();
         }

         if (this.buttonBody != null) {
            Vec2 pos = this.buttonBody.getPosition();
            this.rotation = this.buttonBody.getAngle();
            this.positionX = pos.x;
            this.positionY = pos.y;
            ((MixinAbstractWidgetAccessor)this).setIsHovered(this.isInside(mouseX, mouseY));
            this.isSimActive = true;
         }

         if (world == null && this.isSimActive) {
            this.buttonBody = null;
            this.totalDelta += 0.05F * delta;
            AbstractWidget widget = (AbstractWidget)this;
            float posX = (float)(this.getAnimX() + this.getAnimWidth() / 2.0);

            for (float posY = (float)(this.getAnimY() + this.getAnimHeight() / 2.0); this.totalDelta > 0.004166667F; this.totalDelta -= 0.004166667F) {
               this.rotation = org.joml.Math.lerp(this.rotation, 0.0F, 0.05F);
               this.positionX = org.joml.Math.lerp(this.positionX, posX, 0.05000000074505806);
               this.positionY = org.joml.Math.lerp(this.positionY, posY, 0.05000000074505806);
            }

            if (java.lang.Math.abs(this.positionX - posX) < 0.10000000149011612
               && java.lang.Math.abs(this.positionX - posX) < 0.10000000149011612
               && java.lang.Math.abs(this.rotation) < 0.005F) {
               this.isSimActive = false;
            }
         } else {
            this.totalDelta = 0.0F;
         }

         PoseStack poseStack = guiGraphics.pose();
         if (this.isAnimationActive()) {
            poseStack.pushPose();
            this.applyTransformation(poseStack);
         }

         boolean cancelled = false;
         float tickAdjustedDelta = delta / 20.0F;

         for (Animator animator : this.animations) {
            cancelled |= animator.render(this, guiGraphics, mouseX, mouseY, this.renderPercent, tickAdjustedDelta);
         }

         if (cancelled) {
            if (this.isAnimationActive()) {
               poseStack.popPose();
               ((MixinAbstractWidgetAccessor)this).setIsHovered(this.isInside(mouseX, mouseY));
            }

            info.cancel();
         }
      }
   }

   @Unique
   private boolean isAnimationActive() {
      Screen screen = Minecraft.getInstance().screen;
      return screen != null && !(screen instanceof PhysicsSettingsScreen) ? this.buttonBody != null || this.isSimActive : false;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"render"}
   )
   public void renderTail(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      if (this.isAnimationActive()) {
         guiGraphics.pose().popPose();
         ((MixinAbstractWidgetAccessor)this).setIsHovered(this.isInside(mouseX, mouseY));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"clicked"},
      cancellable = true
   )
   protected void clicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> info) {
      if (this.isAnimationActive()) {
         AbstractWidget widget = (AbstractWidget)this;
         info.setReturnValue(widget.active && widget.visible && this.isInside(mouseX, mouseY));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"isMouseOver"},
      cancellable = true
   )
   public void isMouseOver(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> info) {
      if (this.isAnimationActive()) {
         AbstractWidget widget = (AbstractWidget)this;
         info.setReturnValue(widget.active && widget.visible && this.isInside(mouseX, mouseY));
      }
   }

   private void applyTransformation(PoseStack poseStack) {
      AbstractWidget widget = (AbstractWidget)this;
      poseStack.translate(this.positionX - (this.getAnimX() + this.getAnimWidth() / 2.0), this.positionY - (this.getAnimY() + this.getAnimHeight() / 2.0), 0.0);
      poseStack.translate(this.getAnimX() + this.getAnimWidth() / 2.0, this.getAnimY() + this.getAnimHeight() / 2.0, 0.0);
      poseStack.mulPose(Axis.ZP.rotation(this.rotation));
      poseStack.translate(-(this.getAnimX() + this.getAnimWidth() / 2.0), -(this.getAnimY() + this.getAnimHeight() / 2.0), 0.0);
   }

   @Override
   public boolean isInside(double mouseX, double mouseY) {
      if (this.isAnimationActive()) {
         PoseStack poseStack = new PoseStack();
         this.applyTransformation(poseStack);
         Vector4f trMouse = new Vector4f((float)mouseX, (float)mouseY, 0.0F, 1.0F);
         poseStack.last().pose().invert();
         poseStack.last().pose().transform(trMouse);
         return trMouse.x() >= this.getAnimX()
            && trMouse.y() >= this.getAnimY()
            && trMouse.x() < this.getAnimX() + this.getAnimWidth()
            && trMouse.y() < this.getAnimY() + this.getAnimHeight();
      } else {
         return mouseX >= this.getAnimX()
            && mouseY >= this.getAnimY()
            && mouseX < this.getAnimX() + this.getAnimWidth()
            && mouseY < this.getAnimY() + this.getAnimHeight();
      }
   }

   @Override
   public Animatable addAnimator(Animator animator) {
      this.animations.add(animator);
      animator.init(this);
      return this;
   }

   @Override
   public Animatable addAnimator(int offset, Animator animator) {
      this.animations.add(offset, animator);
      animator.init(this);
      return this;
   }

   @Override
   public Animatable addAnimator(List<Animator> animators) {
      for (Animator animator : animators) {
         this.animations.add(animator);
         animator.init(this);
      }

      return this;
   }

   @Override
   public Animatable addAnimator(Animator... animators) {
      for (Animator animator : animators) {
         this.animations.add(animator);
         animator.init(this);
      }

      return this;
   }

   @Override
   public List<Animator> getAnimators() {
      return this.animations;
   }

   @Override
   public <T extends Animator> T getAnimator(Class<T> clzz) {
      for (Animator animation : this.animations) {
         if (clzz == animation.getClass()) {
            return (T)animation;
         }
      }

      return null;
   }

   @Override
   public Animatable removeAnimator(Animator animator) {
      this.animations.remove(animator);
      return this;
   }

   @Override
   public float getAnimX() {
      return this.animX;
   }

   @Override
   public Animatable setAnimX(float x) {
      this.animX = x;
      this.x = (int)x;
      return this;
   }

   @Override
   public float getAnimY() {
      return this.animY;
   }

   @Override
   public Animatable setAnimY(float y) {
      this.animY = y;
      this.y = (int)y;
      return this;
   }

   @Override
   public float getAnimWidth() {
      return this.animWidth;
   }

   @Override
   public Animatable setAnimWidth(float width) {
      this.animWidth = width;
      this.width = (int)width;
      return this;
   }

   @Override
   public float getAnimHeight() {
      return this.animHeight;
   }

   @Override
   public Animatable setAnimHeight(float height) {
      this.animHeight = height;
      this.height = (int)height;
      return this;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setX"}
   )
   public void setX(int x, CallbackInfo info) {
      this.animX = x;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setY"}
   )
   public void setY(int y, CallbackInfo info) {
      this.animY = y;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setWidth"}
   )
   public void setWidth(int width, CallbackInfo info) {
      this.animWidth = width;
   }

   @Override
   public float getAnimRed() {
      return this.animRed;
   }

   @Override
   public Animatable setAnimRed(float red) {
      this.animRed = red;
      return this;
   }

   @Override
   public float getAnimGreen() {
      return this.animGreen;
   }

   @Override
   public Animatable setAnimGreen(float green) {
      this.animGreen = green;
      return this;
   }

   @Override
   public float getAnimBlue() {
      return this.animBlue;
   }

   @Override
   public Animatable setAnimBlue(float blue) {
      this.animBlue = blue;
      return this;
   }

   @Override
   public float getAnimAlpha() {
      return this.animAlpha;
   }

   @Override
   public Animatable setAnimAlpha(float alpha) {
      this.animAlpha = alpha;
      return this;
   }

   @Override
   public Animatable setAnimColor(float red, float green, float blue, float alpha) {
      this.animRed = red;
      this.animGreen = green;
      this.animBlue = blue;
      this.animAlpha = alpha;
      return this;
   }

   @Override
   public float getAnimDepth() {
      return this.animDepth;
   }

   @Override
   public Animatable setAnimDepth(float depth) {
      this.animDepth = depth;
      return this;
   }
}
