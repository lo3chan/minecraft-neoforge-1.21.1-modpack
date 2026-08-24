package net.mehvahdjukaar.amendments.common.item;

import net.mehvahdjukaar.amendments.common.ProjectileStats;
import net.mehvahdjukaar.amendments.common.entity.MediumDragonFireball;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ProjectileItem.DispenseConfig;
import net.minecraft.world.level.Level;

public class DragonChargeItem extends Item implements ProjectileItem {
   public DragonChargeItem(Properties properties) {
      super(properties);
   }

   public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
      MediumDragonFireball snowball = new MediumDragonFireball(level, pos.x(), pos.y(), pos.z());
      snowball.setItem(stack);
      return snowball;
   }

   public DispenseConfig createDispenseConfig() {
      return ProjectileStats.DISPENSER_CONFIG;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      ItemStack itemStack = player.getItemInHand(usedHand);
      level.playSound(
         null,
         player.getX(),
         player.getEyeY() - 0.1,
         player.getZ(),
         SoundEvents.SNOWBALL_THROW,
         SoundSource.NEUTRAL,
         0.5F,
         0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      player.getCooldowns().addCooldown(this, CommonConfigs.CHARGES_COOLDOWN.get());
      if (!level.isClientSide) {
         MediumDragonFireball snowball = new MediumDragonFireball(level, player);
         snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.1F, 1.0F);
         level.addFreshEntity(snowball);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
         itemStack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
   }

   private void playSound(Level level, BlockPos pos) {
      RandomSource randomSource = level.getRandom();
      level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2F + 1.0F);
   }
}
