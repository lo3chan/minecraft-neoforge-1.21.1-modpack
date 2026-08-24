package dev.tr7zw.waveycapes.delegate;

import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import lombok.Generated;
import net.minecraft.client.player.AbstractClientPlayer;

public class PlayerDelegate implements MinecraftPlayer {
   private AbstractClientPlayer player;

   @Override
   public double getXCloak() {
      return this.player.xCloak;
   }

   @Override
   public double getZCloak() {
      return this.player.zCloak;
   }

   @Override
   public float getYBodyRotO() {
      return this.player.yBodyRotO;
   }

   @Override
   public float getYBodyRot() {
      return this.player.yBodyRot;
   }

   @Override
   public double getYo() {
      return this.player.yo;
   }

   @Override
   public double getXo() {
      return this.player.xo;
   }

   @Override
   public double getZo() {
      return this.player.zo;
   }

   @Generated
   public PlayerDelegate(AbstractClientPlayer player) {
      this.player = player;
   }

   @Generated
   public AbstractClientPlayer getPlayer() {
      return this.player;
   }

   @Generated
   @Override
   public boolean isVisuallySwimming() {
      return this.getPlayer().isVisuallySwimming();
   }

   @Generated
   @Override
   public float getXRot() {
      return this.getPlayer().getXRot();
   }

   @Generated
   @Override
   public boolean isCrouching() {
      return this.getPlayer().isCrouching();
   }

   @Generated
   @Override
   public double getY() {
      return this.getPlayer().getY();
   }

   @Generated
   @Override
   public float getYRot() {
      return this.getPlayer().getYRot();
   }

   @Generated
   @Override
   public double getZ() {
      return this.getPlayer().getZ();
   }

   @Generated
   @Override
   public double getX() {
      return this.getPlayer().getX();
   }

   @Generated
   @Override
   public boolean isUnderWater() {
      return this.getPlayer().isUnderWater();
   }
}
