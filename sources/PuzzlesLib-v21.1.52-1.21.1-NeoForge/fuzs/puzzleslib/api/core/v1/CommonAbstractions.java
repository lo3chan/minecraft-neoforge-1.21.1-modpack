package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface CommonAbstractions {
   CommonAbstractions INSTANCE = new CommonAbstractions() {};

   default MinecraftServer getMinecraftServer() {
      return ProxyImpl.get().getMinecraftServer();
   }

   default boolean hasChannel(ServerPlayer serverPlayer, Type<?> type) {
      return ProxyImpl.get().hasChannel(serverPlayer.connection, type);
   }

   default void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> dataWriter) {
      ProxyImpl.get().openMenu(serverPlayer, menuProvider, dataWriter);
   }

   default Entity getPartEntityParent(Entity entity) {
      return ProxyImpl.get().getPartEntityParent(entity);
   }

   default boolean isBossMob(EntityType<?> type) {
      return type.is(TagFactory.COMMON.registerEntityTypeTag("bosses"));
   }

   default float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos) {
      return ProxyImpl.get().getEnchantPowerBonus(state, level, pos);
   }

   default boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
      return ProxyImpl.get().canEquip(stack, slot, entity);
   }

   default int getMobLootingLevel(Entity target, @Nullable Entity attacker, @Nullable DamageSource damageSource) {
      if (attacker instanceof LivingEntity livingEntity) {
         Holder<Enchantment> enchantment = LookupHelper.lookupEnchantment(target, Enchantments.LOOTING);
         return EnchantmentHelper.getEnchantmentLevel(enchantment, livingEntity);
      } else {
         return 0;
      }
   }

   default boolean getMobGriefingRule(Level level, @Nullable Entity entity) {
      return level instanceof ServerLevel serverLevel && ProxyImpl.get().isMobGriefingAllowed(serverLevel, entity);
   }

   default void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand) {
      ProxyImpl.get().onPlayerDestroyItem(player, originalItemStack, interactionHand);
   }

   @Nullable
   default MobSpawnType getMobSpawnType(Mob mob) {
      return ProxyImpl.get().getMobSpawnReason(mob);
   }

   default Metadata createPackInfo(ResourceLocation id, Component description, PackCompatibility packCompatibility, FeatureFlagSet features, boolean hidden) {
      return ProxyImpl.get().createPackInfo(id, description, packCompatibility, features, hidden);
   }

   default boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
      return ProxyImpl.get().canApplyAtEnchantingTable(enchantment, itemStack);
   }

   default boolean isAllowedOnBooks(Holder<Enchantment> enchantment) {
      return true;
   }

   default boolean isBookEnchantable(ItemStack inputStack, ItemStack bookStack) {
      return true;
   }

   default boolean onExplosionStart(Level level, Explosion explosion) {
      return ProxyImpl.get().onExplosionStart(level, explosion);
   }
}
