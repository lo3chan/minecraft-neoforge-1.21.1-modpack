package net.cibernet.alchemancy.properties.special;

import java.util.Collection;
import java.util.List;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public class SoundEffectProperty extends Property {
   private final int color;
   private final SoundEvent sound;
   private final boolean hidden;

   public SoundEffectProperty(int color, SoundEvent sound, boolean hidden) {
      this.color = color;
      this.sound = sound;
      this.hidden = hidden;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      Level level = event.getLevel();
      if (!level.isClientSide()) {
         this.playSound(event.getEntity(), level, event.getEntity().getEyePosition());
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      Level level = target.level();
      if (!level.isClientSide()) {
         this.playSound(target, level, target.getEyePosition());
      }
   }

   private void playSound(Entity user, Level level, Vec3 eyePosition) {
      level.playSound(null, eyePosition.x, eyePosition.y, eyePosition.z, this.sound, SoundSource.RECORDS, 3.0F, 1.0F);
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.NOTE, eyePosition.x(), eyePosition.y(), eyePosition.z(), 1, 0.0, 0.0, 0.0, 1.0);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.color;
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      return (Collection<ItemStack>)(this.hidden ? List.of() : super.populateCreativeTab(capsuleItem, holder));
   }
}
