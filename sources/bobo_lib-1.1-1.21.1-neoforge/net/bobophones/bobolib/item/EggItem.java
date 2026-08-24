package net.bobophones.bobolib.item;

import java.util.function.Supplier;
import net.bobophones.bobolib.entity.projectile.ThrownEggProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class EggItem extends Item {
   private final Supplier<? extends EntityType<? extends ThrownEggProjectile>> projectile;

   public EggItem(Supplier<? extends EntityType<? extends ThrownEggProjectile>> projectile) {
      super(new Properties().stacksTo(16));
      this.projectile = projectile;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      level.playSound(
         null,
         player.getX(),
         player.getY(),
         player.getZ(),
         SoundEvents.EGG_THROW,
         SoundSource.PLAYERS,
         0.5F,
         0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      if (!level.isClientSide()) {
         ThrownEggProjectile thrown_egg = (ThrownEggProjectile)this.projectile.get().create(level);
         thrown_egg.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
         thrown_egg.setOwner(player);
         thrown_egg.setItem(stack);
         thrown_egg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
         level.addFreshEntity(thrown_egg);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
         stack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
   }
}
