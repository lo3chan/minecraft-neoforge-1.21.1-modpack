package com.aetherteam.aether.item.miscellaneous;

import com.aetherteam.aether.entity.miscellaneous.Parachute;
import java.util.function.Supplier;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ParachuteItem extends Item {
   protected final Supplier<? extends EntityType<? extends Parachute>> parachuteEntity;

   public ParachuteItem(Supplier<? extends EntityType<? extends Parachute>> parachuteEntity, Properties properties) {
      super(properties);
      this.parachuteEntity = parachuteEntity;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (!player.onGround()
         && !player.isInFluidType()
         && !player.isShiftKeyDown()
         && this.getParachuteEntity().get().create(level) instanceof Parachute parachute) {
         parachute.setPos(player.getX(), player.getY() - 1.0, player.getZ());
         parachute.setDeltaMovement(player.getDeltaMovement());
         if (player.isPassenger()) {
            if (!(player.getVehicle() instanceof Parachute)) {
               return InteractionResultHolder.pass(heldStack);
            }

            player.getVehicle().ejectPassengers();
         }

         if (!level.isClientSide()) {
            level.addFreshEntity(parachute);
            player.startRiding(parachute);
            heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
         }

         parachute.spawnExplosionParticle();
         player.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(heldStack, level.isClientSide());
      } else {
         return InteractionResultHolder.pass(heldStack);
      }
   }

   public Supplier<? extends EntityType<? extends Parachute>> getParachuteEntity() {
      return this.parachuteEntity;
   }
}
