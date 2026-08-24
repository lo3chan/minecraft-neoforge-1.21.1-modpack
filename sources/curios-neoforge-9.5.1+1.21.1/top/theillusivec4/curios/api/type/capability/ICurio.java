package top.theillusivec4.curios.api.type.capability;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.platform.Services;

public interface ICurio {
   ItemStack getStack();

   default void curioTick(SlotContext slotContext) {
   }

   default void onEquip(SlotContext slotContext, ItemStack prevStack) {
   }

   default void onUnequip(SlotContext slotContext, ItemStack newStack) {
   }

   default boolean canEquip(SlotContext slotContext) {
      return true;
   }

   default boolean canUnequip(SlotContext slotContext) {
      return true;
   }

   default List<Component> getSlotsTooltip(List<Component> tooltips, TooltipContext context) {
      return this.getSlotsTooltip(tooltips);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.22"
   )
   default List<Component> getSlotsTooltip(List<Component> tooltips) {
      return tooltips;
   }

   @Deprecated(
      forRemoval = true,
      since = "1.21"
   )
   default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
      return LinkedHashMultimap.create();
   }

   default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id) {
      return LinkedHashMultimap.create();
   }

   default void onEquipFromUse(SlotContext slotContext) {
      LivingEntity livingEntity = slotContext.entity();
      ICurio.SoundInfo soundInfo = this.getEquipSound(new SlotContext("", livingEntity, 0, false, true));
      livingEntity.level()
         .playSound(null, livingEntity.blockPosition(), soundInfo.getSoundEvent(), livingEntity.getSoundSource(), soundInfo.getVolume(), soundInfo.getPitch());
   }

   @Nonnull
   default ICurio.SoundInfo getEquipSound(SlotContext slotContext) {
      return new ICurio.SoundInfo((SoundEvent)SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
   }

   default boolean canEquipFromUse(SlotContext slotContext) {
      return false;
   }

   default void curioBreak(SlotContext slotContext) {
      playBreakAnimation(this.getStack(), slotContext.entity());
   }

   default boolean canSync(SlotContext slotContext) {
      return false;
   }

   @Nonnull
   default CompoundTag writeSyncData(SlotContext slotContext) {
      return new CompoundTag();
   }

   default void readSyncData(SlotContext slotContext, CompoundTag compound) {
   }

   @Nonnull
   default ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit) {
      return this.getDropRule(slotContext, source, 0, recentlyHit);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.21.1"
   )
   @Nonnull
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   default ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
      return ICurio.DropRule.DEFAULT;
   }

   default List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context) {
      return this.getAttributesTooltip(tooltips);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.22"
   )
   default List<Component> getAttributesTooltip(List<Component> tooltips) {
      return tooltips;
   }

   default int getFortuneLevel(SlotContext slotContext, @Nullable LootContext lootContext) {
      return EnchantmentHelper.getItemEnchantmentLevel(
         slotContext.entity().level().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), this.getStack()
      );
   }

   default int getLootingLevel(SlotContext slotContext, @Nullable LootContext lootContext) {
      return EnchantmentHelper.getItemEnchantmentLevel(
         slotContext.entity().level().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), this.getStack()
      );
   }

   default boolean makesPiglinsNeutral(SlotContext slotContext) {
      return Services.CURIOS.makesPiglinsNeutral(this.getStack(), slotContext.entity());
   }

   default boolean canWalkOnPowderedSnow(SlotContext slotContext) {
      return Services.CURIOS.canWalkOnPowderedSnow(this.getStack(), slotContext.entity());
   }

   default boolean isEnderMask(SlotContext slotContext, EnderMan enderMan) {
      return slotContext.entity() instanceof Player player ? Services.CURIOS.isEnderMask(this.getStack(), player, enderMan) : false;
   }

   static void playBreakAnimation(ItemStack stack, LivingEntity livingEntity) {
      if (!stack.isEmpty()) {
         if (!livingEntity.isSilent()) {
            livingEntity.level()
               .playLocalSound(
                  livingEntity.getX(),
                  livingEntity.getY(),
                  livingEntity.getZ(),
                  SoundEvents.ITEM_BREAK,
                  livingEntity.getSoundSource(),
                  0.8F,
                  0.8F + livingEntity.level().random.nextFloat() * 0.4F,
                  false
               );
         }

         for (int i = 0; i < 5; i++) {
            Vec3 vec3d = new Vec3((livingEntity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.xRot(-livingEntity.getXRot() * 0.017453292F);
            vec3d = vec3d.yRot(-livingEntity.getYRot() * 0.017453292F);
            double d0 = -livingEntity.getRandom().nextFloat() * 0.6 - 0.3;
            Vec3 vec3d1 = new Vec3((livingEntity.getRandom().nextFloat() - 0.5) * 0.3, d0, 0.6);
            vec3d1 = vec3d1.xRot(-livingEntity.getXRot() * 0.017453292F);
            vec3d1 = vec3d1.yRot(-livingEntity.getYRot() * 0.017453292F);
            vec3d1 = vec3d1.add(livingEntity.getX(), livingEntity.getY() + livingEntity.getEyeHeight(), livingEntity.getZ());
            livingEntity.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
         }
      }
   }

   public static enum DropRule {
      DEFAULT,
      ALWAYS_DROP,
      ALWAYS_KEEP,
      DESTROY;
   }

   public record SoundInfo(SoundEvent soundEvent, float volume, float pitch) {
      @Deprecated(
         forRemoval = true,
         since = "1.20.1"
      )
      @ScheduledForRemoval(
         inVersion = "1.22"
      )
      public SoundEvent getSoundEvent() {
         return this.soundEvent;
      }

      @Deprecated(
         forRemoval = true,
         since = "1.20.1"
      )
      @ScheduledForRemoval(
         inVersion = "1.22"
      )
      public float getVolume() {
         return this.volume;
      }

      @Deprecated(
         forRemoval = true,
         since = "1.20.1"
      )
      @ScheduledForRemoval(
         inVersion = "1.22"
      )
      public float getPitch() {
         return this.pitch;
      }
   }
}
