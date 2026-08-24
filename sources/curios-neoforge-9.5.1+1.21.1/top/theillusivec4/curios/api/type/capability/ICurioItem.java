package top.theillusivec4.curios.api.type.capability;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.platform.Services;

public interface ICurioItem {
   ICurio defaultInstance = () -> ItemStack.EMPTY;

   default boolean hasCurioCapability(ItemStack stack) {
      return true;
   }

   default void curioTick(SlotContext slotContext, ItemStack stack) {
      defaultInstance.curioTick(slotContext);
   }

   default void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
      defaultInstance.onEquip(slotContext, prevStack);
   }

   default void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
      defaultInstance.onUnequip(slotContext, newStack);
   }

   default boolean canEquip(SlotContext slotContext, ItemStack stack) {
      return defaultInstance.canEquip(slotContext);
   }

   default boolean canUnequip(SlotContext slotContext, ItemStack stack) {
      return defaultInstance.canUnequip(slotContext);
   }

   default List<Component> getSlotsTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
      return defaultInstance.getSlotsTooltip(tooltips, context);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.22"
   )
   default List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
      return defaultInstance.getSlotsTooltip(tooltips, TooltipContext.EMPTY);
   }

   default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      return defaultInstance.getAttributeModifiers(slotContext, id);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.21"
   )
   default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
      return defaultInstance.getAttributeModifiers(slotContext, ResourceLocation.withDefaultNamespace("empty"));
   }

   default void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
      defaultInstance.onEquipFromUse(slotContext);
   }

   @Nonnull
   default ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
      return defaultInstance.getEquipSound(slotContext);
   }

   default boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
      return defaultInstance.canEquipFromUse(slotContext);
   }

   default void curioBreak(SlotContext slotContext, ItemStack stack) {
      defaultInstance.curioBreak(slotContext);
   }

   default boolean canSync(SlotContext slotContext, ItemStack stack) {
      return defaultInstance.canSync(slotContext);
   }

   @Nonnull
   default CompoundTag writeSyncData(SlotContext slotContext, ItemStack stack) {
      return defaultInstance.writeSyncData(slotContext);
   }

   default void readSyncData(SlotContext slotContext, CompoundTag compound, ItemStack stack) {
      defaultInstance.readSyncData(slotContext, compound);
   }

   @Nonnull
   default ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
      return this.getDropRule(slotContext, source, 0, recentlyHit, stack);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.21.1"
   )
   @Nonnull
   @ScheduledForRemoval(
      inVersion = "1.22"
   )
   default ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
      return defaultInstance.getDropRule(slotContext, source, recentlyHit);
   }

   default List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
      return defaultInstance.getAttributesTooltip(tooltips, context);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.22"
   )
   default List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
      return defaultInstance.getAttributesTooltip(tooltips, TooltipContext.EMPTY);
   }

   default int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
      return defaultInstance.getFortuneLevel(slotContext, lootContext);
   }

   default int getLootingLevel(SlotContext slotContext, @Nullable LootContext lootContext, ItemStack stack) {
      return defaultInstance.getLootingLevel(slotContext, lootContext);
   }

   default boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
      return Services.CURIOS.makesPiglinsNeutral(stack, slotContext.entity());
   }

   default boolean canWalkOnPowderedSnow(SlotContext slotContext, ItemStack stack) {
      return Services.CURIOS.canWalkOnPowderedSnow(stack, slotContext.entity());
   }

   default boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
      return slotContext.entity() instanceof Player player ? Services.CURIOS.isEnderMask(stack, player, enderMan) : false;
   }
}
