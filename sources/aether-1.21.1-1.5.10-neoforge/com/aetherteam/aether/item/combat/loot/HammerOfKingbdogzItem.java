package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.projectile.weapon.HammerProjectile;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ProjectileItem.DispenseConfig;
import net.minecraft.world.level.Level;

public class HammerOfKingbdogzItem extends SwordItem implements ProjectileItem {
   public HammerOfKingbdogzItem() {
      super(
         AetherItemTiers.HAMMER_OF_KINGBDOGZ,
         new Properties().rarity(AetherItems.AETHER_LOOT).attributes(SwordItem.createAttributes(AetherItemTiers.HAMMER_OF_KINGBDOGZ, 3.0F, -2.4F))
      );
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (!level.isClientSide()) {
         if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(this, (Integer)AetherConfig.SERVER.hammer_of_kingbdogz_cooldown.get());
            heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
         }

         HammerProjectile hammerProjectile = new HammerProjectile(player, level);
         hammerProjectile.shoot(player.getXRot(), player.getYRot(), 3.0F, 1.0F);
         if (heldStack.getHoverName().getString().equalsIgnoreCase("hammer of jeb")) {
            hammerProjectile.setIsJeb(true);
         }

         level.addFreshEntity(hammerProjectile);
      }

      level.playLocalSound(
         player.getX(),
         player.getY(),
         player.getZ(),
         (SoundEvent)AetherSoundEvents.ITEM_HAMMER_OF_KINGBDOGZ_SHOOT.get(),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F),
         false
      );
      player.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.success(heldStack);
   }

   public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
      HammerProjectile hammerProjectile = new HammerProjectile(level);
      hammerProjectile.setPos(pos.x(), pos.y(), pos.z());
      if (stack.getHoverName().getString().equalsIgnoreCase("hammer of jeb")) {
         hammerProjectile.setIsJeb(true);
      }

      return hammerProjectile;
   }

   public DispenseConfig createDispenseConfig() {
      return DispenseConfig.builder().uncertainty(1.0F).build();
   }
}
