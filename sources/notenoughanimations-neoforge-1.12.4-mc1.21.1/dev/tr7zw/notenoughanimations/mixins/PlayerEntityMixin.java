package dev.tr7zw.notenoughanimations.mixins;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.renderlayer.SwordRenderLayer;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.DataHolder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public class PlayerEntityMixin implements PlayerData {
   private int armsUpdated = 0;
   private float[] lastRotations = new float[45];
   private ItemStack sideSword = ItemStack.EMPTY;
   private ItemStack[] lastHeldItems = new ItemStack[2];
   private boolean disableBodyRotation = false;
   private boolean rotateBodyToHead = false;
   private int itemSwapAnimationTimer = 0;
   private int lastAnimationSwapTick = -1;
   private Pose poseOverwrite = null;
   private Map<DataHolder<?>, Object> animationData = new HashMap<>();

   @Inject(
      method = {"tick()V"},
      at = {@At("RETURN")}
   )
   public void tick(CallbackInfo info) {
      this.updateRenderLayerItems();
      this.setRotateBodyToHead(false);
   }

   @Inject(
      method = {"getMaxHeadRotationRelativeToBody()F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void overrideMaxHeadRoationRelativeToBody(CallbackInfoReturnable<Float> ci) {
      if (NEABaseMod.config.maxBlockingAngle < 0.0F || NEABaseMod.config.maxBlockingAngle > 15.0F) {
         NEABaseMod.config.maxBlockingAngle = 15.0F;
      }

      if (NEABaseMod.config.maxNormalAngle < 0.0F || NEABaseMod.config.maxNormalAngle > 50.0F) {
         NEABaseMod.config.maxNormalAngle = 50.0F;
      }

      Player player = (Player)this;
      ci.setReturnValue(player.isBlocking() ? NEABaseMod.config.maxBlockingAngle : NEABaseMod.config.maxNormalAngle);
   }

   @Override
   public int isUpdated(int frameId) {
      return Math.abs(frameId - this.armsUpdated);
   }

   @Override
   public void setUpdated(int frameId) {
      this.armsUpdated = frameId;
   }

   private void updateRenderLayerItems() {
      SwordRenderLayer.update((Player)this);
   }

   @Override
   public ItemStack[] getLastHeldItems() {
      return this.lastHeldItems;
   }

   @Override
   public int getItemSwapAnimationTimer() {
      return this.itemSwapAnimationTimer;
   }

   @Override
   public void setItemSwapAnimationTimer(int count) {
      this.itemSwapAnimationTimer = count;
   }

   @Override
   public int getLastAnimationSwapTick() {
      return this.lastAnimationSwapTick;
   }

   @Override
   public void setLastAnimationSwapTick(int count) {
      this.lastAnimationSwapTick = count;
   }

   @Override
   public void setPoseOverwrite(Pose state) {
      this.poseOverwrite = state;
   }

   @Override
   public Pose getPoseOverwrite() {
      return this.poseOverwrite;
   }

   @Override
   public <T> T getData(DataHolder<T> holder, Supplier<T> builder) {
      return (T)this.animationData.computeIfAbsent(holder, h -> builder.get());
   }

   @Generated
   @Override
   public float[] getLastRotations() {
      return this.lastRotations;
   }

   @Generated
   public void setLastRotations(float[] lastRotations) {
      this.lastRotations = lastRotations;
   }

   @Generated
   @Override
   public ItemStack getSideSword() {
      return this.sideSword;
   }

   @Generated
   @Override
   public void setSideSword(ItemStack sideSword) {
      this.sideSword = sideSword;
   }

   @Generated
   @Override
   public boolean isDisableBodyRotation() {
      return this.disableBodyRotation;
   }

   @Generated
   @Override
   public void setDisableBodyRotation(boolean disableBodyRotation) {
      this.disableBodyRotation = disableBodyRotation;
   }

   @Generated
   @Override
   public boolean isRotateBodyToHead() {
      return this.rotateBodyToHead;
   }

   @Generated
   @Override
   public void setRotateBodyToHead(boolean rotateBodyToHead) {
      this.rotateBodyToHead = rotateBodyToHead;
   }
}
