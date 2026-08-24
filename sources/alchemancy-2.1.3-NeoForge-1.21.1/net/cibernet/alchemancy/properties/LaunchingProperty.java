package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

public class LaunchingProperty extends Property implements IDataHolder<Long> {
   public static final DustColorTransitionOptions PARTICLES = new DustColorTransitionOptions(
      Vec3.fromRGB24(14837503).toVector3f(), Vec3.fromRGB24(6714111).toVector3f(), 1.0F
   );

   @Override
   public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) {
      this.launch(user, weapon, target, 1.0F);
      this.setData(weapon, target.level().getGameTime());
   }

   @Override
   public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack) {
      this.launch(null, stack, target, 1.2F);
   }

   @Override
   public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event) {
      if (this.getData(weapon) == target.level().getGameTime()) {
         event.setCanceled(true);
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random) {
      BlockPos pPos = root.getBlockPos();
      Level pLevel = root.getLevel();
      double d0 = pPos.getX();
      double d1 = pPos.getY();
      double d2 = pPos.getZ();
      double d5 = random.nextDouble();
      double d6 = random.nextDouble();
      double d7 = random.nextDouble();
      pLevel.addParticle(PARTICLES, d0 + d5, d1 + d6, d2 + d7, 0.0, 4.0, 0.0);
   }

   public void launch(@Nullable LivingEntity user, ItemStack weapon, Entity target, float strength) {
      target.setDeltaMovement(target.getDeltaMovement().multiply(1.0, 0.0, 1.0).add(0.0, strength, 0.0));
      target.hasImpulse = true;
      target.hurtMarked = true;

      for (int i = 0; i < 24; i++) {
         target.level().addParticle(PARTICLES, target.getRandomX(1.2), target.getRandomY(), target.getRandomZ(1.2), 0.0, 4.0, 0.0);
      }

      this.damageOrConsumeItem(target.level(), user, weapon, EquipmentSlot.MAINHAND, 20);
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 14837503;
   }

   public Long readData(CompoundTag tag) {
      return tag.getLong("crit_timestamp");
   }

   public CompoundTag writeData(final Long data) {
      return new CompoundTag() {
         {
            this.putLong("crit_timestamp", data);
         }
      };
   }

   public Long getDefaultData() {
      return 0L;
   }
}
