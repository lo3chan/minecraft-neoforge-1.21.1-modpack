package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SeagullAIStealFromPlayers extends Goal {
   private final EntitySeagull seagull;
   private Vec3 fleeVec = null;
   private Player target;
   private int fleeTime = 0;

   public SeagullAIStealFromPlayers(EntitySeagull entitySeagull) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
      this.seagull = entitySeagull;
   }

   public boolean canUse() {
      long worldTime = this.seagull.level().getGameTime() % 10L;
      if ((this.seagull.getNoActionTime() < 100 || worldTime == 0L) && !this.seagull.isSitting() && AMConfig.seagullStealing) {
         if ((this.seagull.getRandom().nextInt(12) == 0 || worldTime == 0L) && this.seagull.stealCooldown <= 0) {
            if (this.seagull.getMainHandItem().isEmpty()) {
               Player valid = this.getClosestValidPlayer();
               if (valid != null) {
                  this.target = valid;
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.seagull.aiItemFlag = true;
   }

   public void stop() {
      this.seagull.aiItemFlag = false;
      this.target = null;
      this.fleeVec = null;
      this.fleeTime = 0;
   }

   public boolean canContinueToUse() {
      return this.target != null && !this.target.isCreative() && (this.seagull.getMainHandItem().isEmpty() || this.fleeTime > 0);
   }

   public void tick() {
      this.seagull.setFlying(true);
      this.seagull.getMoveControl().setWantedPosition(this.target.getX(), this.target.getEyeY(), this.target.getZ(), 1.2000000476837158);
      if (this.seagull.distanceTo(this.target) < 2.0F && this.seagull.getMainHandItem().isEmpty()) {
         if (this.hasFoods(this.target)) {
            ItemStack foodStack = this.getFoodItemFrom(this.target);
            if (!foodStack.isEmpty()) {
               ItemStack copy = foodStack.copy();
               foodStack.shrink(1);
               copy.setCount(1);
               this.seagull.peck();
               this.seagull.setItemInHand(InteractionHand.MAIN_HAND, copy);
               this.fleeTime = 60;
               this.seagull.stealCooldown = 1500 + this.seagull.getRandom().nextInt(1500);
               if (this.target instanceof ServerPlayer) {
                  AMAdvancementTriggerRegistry.SEAGULL_STEAL.trigger((ServerPlayer)this.target);
               }
            } else {
               this.stop();
            }
         } else {
            this.stop();
         }
      }

      if (this.fleeTime > 0) {
         if (this.fleeVec == null) {
            this.fleeVec = this.seagull.getBlockInViewAway(this.target.position(), 4.0F);
         }

         if (this.fleeVec != null) {
            this.seagull.setFlying(true);
            this.seagull.getMoveControl().setWantedPosition(this.fleeVec.x, this.fleeVec.y, this.fleeVec.z, 1.2000000476837158);
            if (this.seagull.distanceToSqr(this.fleeVec) < 5.0) {
               this.fleeVec = this.seagull.getBlockInViewAway(this.fleeVec, 4.0F);
            }
         }

         this.fleeTime--;
      }
   }

   private Player getClosestValidPlayer() {
      List<Player> list = this.seagull
         .level()
         .getEntitiesOfClass(Player.class, this.seagull.getBoundingBox().inflate(10.0, 25.0, 10.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
      Player closest = null;
      if (!list.isEmpty()) {
         for (Player player : list) {
            if ((closest == null || closest.distanceTo(this.seagull) > player.distanceTo(this.seagull)) && this.hasFoods(player)) {
               closest = player;
            }
         }
      }

      return closest;
   }

   private boolean hasFoods(Player player) {
      for (int i = 0; i < 9; i++) {
         ItemStack stackIn = player.getInventory().getItem(i);
         if (AMCompat.isEdible(stackIn) && !this.isBlacklisted(stackIn)) {
            return true;
         }
      }

      return false;
   }

   private boolean isBlacklisted(ItemStack stack) {
      ResourceLocation loc = BuiltInRegistries.ITEM.getKey(stack.getItem());
      if (loc != null) {
         for (String str : AMConfig.seagullStealingBlacklist) {
            if (loc.toString().equals(str)) {
               return true;
            }
         }
      }

      return false;
   }

   private ItemStack getFoodItemFrom(Player player) {
      List<ItemStack> foods = new ArrayList<>();

      for (int i = 0; i < 9; i++) {
         ItemStack stackIn = player.getInventory().getItem(i);
         if (AMCompat.isEdible(stackIn) && !this.isBlacklisted(stackIn)) {
            foods.add(stackIn);
         }
      }

      return !foods.isEmpty() ? foods.get(foods.size() <= 1 ? 0 : this.seagull.getRandom().nextInt(foods.size() - 1)) : ItemStack.EMPTY;
   }
}
