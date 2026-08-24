package net.bettercombat.api;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public interface MinecraftClient_BetterCombat {
   int getComboCount();

   boolean hasTargetsInReach();

   @Nullable
   default Entity getCursorTarget() {
      Minecraft client = (Minecraft)this;
      return client.hitResult != null && client.hitResult.getType() == Type.ENTITY ? ((EntityHitResult)client.hitResult).getEntity() : null;
   }

   int getUpswingTicks();

   float getSwingProgress();

   default boolean isWeaponSwingInProgress() {
      return this.getSwingProgress() < 1.0F;
   }

   @Nullable
   AttackHand getCurrentAttackHand();

   default WeaponAttributes.Attack getCurrentAttack() {
      AttackHand attackHand = this.getCurrentAttackHand();
      return attackHand == null ? null : attackHand.attack();
   }

   void cancelUpswing();
}
