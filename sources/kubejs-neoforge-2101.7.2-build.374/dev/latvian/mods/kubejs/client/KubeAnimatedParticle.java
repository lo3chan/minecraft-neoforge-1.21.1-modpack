package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.typings.Info;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class KubeAnimatedParticle extends SimpleAnimatedParticle {
   private Float2IntFunction lightColorFunction;
   @Nullable
   private Consumer<KubeAnimatedParticle> onTick;

   public KubeAnimatedParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
      super(level, x, y, z, sprites, 0.0125F);
      this.setLifetime(20);
      this.setSpriteFromAge(sprites);
      this.lightColorFunction = x$0 -> super.getLightColor(x$0);
   }

   public void setGravity(float g) {
      this.gravity = g;
   }

   @Info("Sets teh friction of the particle, the particle's motion is multiplied by this value every tick")
   public void setFriction(float f) {
      this.friction = f;
   }

   public void setColor(KubeColor color, boolean alpha) {
      this.setColor(color.kjs$getRGB());
      if (alpha) {
         this.setAlpha((color.kjs$getARGB() >>> 24) / 255.0F);
      }
   }

   public void setColor(KubeColor color) {
      this.setColor(color, false);
   }

   public void setPhysicality(boolean hasPhysics) {
      this.hasPhysics = hasPhysics;
   }

   public void setFasterWhenYMotionBlocked(boolean b) {
      this.speedUpWhenYMotionIsBlocked = b;
   }

   public void setLightColor(Float2IntFunction function) {
      this.lightColorFunction = function;
   }

   public void onTick(@Nullable Consumer<KubeAnimatedParticle> tick) {
      this.onTick = tick;
   }

   public void setSpeed(Vec3 speed) {
      this.setParticleSpeed(speed.x(), speed.y(), speed.z());
   }

   public ClientLevel getLevel() {
      return this.level;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public double getXSpeed() {
      return this.xd;
   }

   public double getYSpeed() {
      return this.yd;
   }

   public double getZSpeed() {
      return this.zd;
   }

   public SpriteSet getSpriteSet() {
      return this.sprites;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public int getLightColor(float partialTick) {
      return this.lightColorFunction.get(partialTick);
   }

   public void tick() {
      super.tick();
      if (this.onTick != null) {
         this.onTick.accept(this);
      }
   }
}
