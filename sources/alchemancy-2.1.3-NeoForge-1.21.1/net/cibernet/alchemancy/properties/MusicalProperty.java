package net.cibernet.alchemancy.properties;

import java.util.HashMap;
import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class MusicalProperty extends Property {
   public static final HashMap<TagKey<Item>, SoundEvent> ON_CLICK_SOUNDS = new HashMap<TagKey<Item>, SoundEvent>() {
      {
         for (NoteBlockInstrument instrument : NoteBlockInstrument.values()) {
            this.put(
               ItemTags.create(ResourceLocation.fromNamespaceAndPath("alchemancy", "musical_instruments/" + instrument.getSerializedName())),
               (SoundEvent)instrument.getSoundEvent().value()
            );
         }
      }
   };

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      Level level = user.level();
      if (slot == EquipmentSlot.FEET && user.onGround()) {
         double hSpeed = user.getKnownMovement().horizontalDistance();
         if (hSpeed > 0.05
            && user.tickCount % (int)(8.0 - Math.min(7.0, hSpeed * 10.0)) == 0
            && (!level.isClientSide() || user.isControlledByLocalInstance() || user instanceof Player player && player.isLocalPlayer())) {
            BlockPos standingOnPos = user.blockPosition();
            standingOnPos = user.level().getBlockState(standingOnPos).getCollisionShape(user.level(), standingOnPos).isEmpty()
               ? user.getBlockPosBelowThatAffectsMyMovement()
               : standingOnPos;
            this.playSound(
               user,
               level,
               user.position(),
               (SoundEvent)level.getBlockState(standingOnPos).instrument().getSoundEvent().value(),
               (float)((hSpeed * 80.0 - 12.0) / 12.0)
            );
         }
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      Level level = event.getLevel();
      if (!level.isClientSide()) {
         this.playSound(
            event.getEntity(), level, event.getEntity().getEyePosition(), getInstrumentFromItem(event.getItemStack()), event.getEntity().getXRot() / -90.0F
         );
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      Level level = target.level();
      if (!level.isClientSide()) {
         this.playSound(target, level, target.getEyePosition(), getInstrumentFromItem(stack), target.getRandom().nextFloat());
      }
   }

   private void playSound(@javax.annotation.Nullable Entity source, Level pLevel, Vec3 pPos, SoundEvent sound, float i) {
      float f = (float)Math.pow(2.0, i);
      pLevel.playSound(null, pPos.x, pPos.y, pPos.z, sound, SoundSource.RECORDS, 3.0F, f);
      if (!pLevel.isClientSide()) {
         ((ServerLevel)pLevel).sendParticles(ParticleTypes.NOTE, pPos.x(), pPos.y(), pPos.z(), 1, 0.0, 0.0, 0.0, i / 24.0);
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      if (root.getTickCount() % 20 == 0) {
         this.playSound(
            null,
            root.getLevel(),
            root.getBlockPos().getBottomCenter(),
            getInstrumentFromItem(root.getItem()),
            (root.getLevel().random.nextInt(24) - 12) / 12.0F
         );
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random) {
      if (root.getTickCount() % 20 == 0) {
         Vec3 pos = root.getBlockPos().getBottomCenter();
         Level level = root.getLevel();
         level.addParticle(SparklingProperty.getParticles(root.getItem()).orElse(ParticleTypes.NOTE), pos.x, pos.y + random.nextDouble(), pos.z, 0.0, 0.0, 0.0);
      }
   }

   public static SoundEvent getInstrumentFromItem(ItemStack stack) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CLUELESS)) {
         return (SoundEvent)AlchemancySoundEvents.CLUELESS.value();
      } else if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOME_RUN)) {
         return (SoundEvent)AlchemancySoundEvents.HOME_RUN_FAIL.value();
      } else {
         for (TagKey<Item> tag : ON_CLICK_SOUNDS.keySet()) {
            if (stack.is(tag)) {
               return ON_CLICK_SOUNDS.get(tag);
            }
         }

         return stack.getItem() instanceof BlockItem blockItem
            ? (SoundEvent)blockItem.getBlock().defaultBlockState().instrument().getSoundEvent().value()
            : (SoundEvent)NoteBlockInstrument.HARP.getSoundEvent().value();
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 11767539;
   }
}
