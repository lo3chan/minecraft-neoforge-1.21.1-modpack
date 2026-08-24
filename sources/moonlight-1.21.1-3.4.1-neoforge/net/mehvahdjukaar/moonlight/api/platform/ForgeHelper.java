package net.mehvahdjukaar.moonlight.api.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.platform.platform.ForgeHelperImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ForgeHelper {
   @Deprecated(
      forRemoval = true
   )
   public static <T> DynamicOps<T> addConditionOps(DynamicOps<T> ops) {
      return ops;
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean onProjectileImpact(Projectile projectile, HitResult blockHitResult) {
      return fireOnProjectileImpact(projectile, blockHitResult);
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean onExplosionStart(Level level, Explosion explosion) {
      return fireOnExplosionStart(level, explosion);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void onLivingConvert(LivingEntity skellyHorseMixin, LivingEntity newHorse) {
      fireOnLivingConvert(skellyHorseMixin, newHorse);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> entities, double diameter) {
      fireOnExplosionDetonate(level, explosion, entities, diameter);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void onBlockExploded(BlockState blockstate, Level level, BlockPos blockpos, Explosion explosion) {
      fireOnBlockExploded(blockstate, level, blockpos, explosion);
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean onCropsGrowPre(ServerLevel level, BlockPos pos, BlockState state, boolean b) {
      return fireOnCropsGrowPre(level, pos, state, b);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void onCropsGrowPost(ServerLevel level, BlockPos pos, BlockState state) {
      fireOnCropsGrowPost(level, pos, state);
   }

   public static void onEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to) {
      fireOnEquipmentChange(entity, slot, from, to);
   }

   @Deprecated(
      forRemoval = true
   )
   public static InteractionResult onRightClickBlock(Player player, InteractionHand hand, BlockPos below, BlockHitResult rayTraceResult) {
      return fireOnRightClickBlock(player, hand, below, rayTraceResult);
   }

   /** @deprecated */
   public static <T extends RecipeInput> Recipe<T> copyRecipeConditions(Recipe<T> var0, Recipe<?> var1) {
      return ForgeHelperImpl.copyRecipeConditions(var0, var1);
   }

   public static <T> RegistryOps<T> conditionalOps(DynamicOps<T> var0, Provider var1, SimplePreparableReloadListener<?> var2) {
      return ForgeHelperImpl.conditionalOps(var0, var1, var2);
   }

   public static <T> Codec<Optional<T>> conditionalCodec(Codec<T> var0) {
      return ForgeHelperImpl.conditionalCodec(var0);
   }

   public static boolean fireOnProjectileImpact(Projectile var0, HitResult var1) {
      return ForgeHelperImpl.fireOnProjectileImpact(var0, var1);
   }

   public static boolean isCurativeItem(ItemStack var0, MobEffectInstance var1) {
      return ForgeHelperImpl.isCurativeItem(var0, var1);
   }

   public static boolean canHarvestBlock(BlockState var0, ServerLevel var1, BlockPos var2, ServerPlayer var3) {
      return ForgeHelperImpl.canHarvestBlock(var0, var1, var2, var3);
   }

   public static float getFriction(BlockState var0, LevelReader var1, BlockPos var2, Entity var3) {
      return ForgeHelperImpl.getFriction(var0, var1, var2, var3);
   }

   public static boolean canEquipItem(LivingEntity var0, ItemStack var1, EquipmentSlot var2) {
      return ForgeHelperImpl.canEquipItem(var0, var1, var2);
   }

   public static boolean canEntityDestroy(Level var0, BlockPos var1, Animal var2) {
      return ForgeHelperImpl.canEntityDestroy(var0, var1, var2);
   }

   public static boolean fireOnExplosionStart(Level var0, Explosion var1) {
      return ForgeHelperImpl.fireOnExplosionStart(var0, var1);
   }

   public static void fireOnLivingConvert(LivingEntity var0, LivingEntity var1) {
      ForgeHelperImpl.fireOnLivingConvert(var0, var1);
   }

   public static boolean canLivingConvert(LivingEntity var0, EntityType<? extends LivingEntity> var1, Consumer<Integer> var2) {
      return ForgeHelperImpl.canLivingConvert(var0, var1, var2);
   }

   public static void fireOnExplosionDetonate(Level var0, Explosion var1, List<Entity> var2, double var3) {
      ForgeHelperImpl.fireOnExplosionDetonate(var0, var1, var2, var3);
   }

   public static float getExplosionResistance(BlockState var0, Level var1, BlockPos var2, Explosion var3) {
      return ForgeHelperImpl.getExplosionResistance(var0, var1, var2, var3);
   }

   public static void fireOnBlockExploded(BlockState var0, Level var1, BlockPos var2, Explosion var3) {
      ForgeHelperImpl.fireOnBlockExploded(var0, var1, var2, var3);
   }

   public static boolean canDropFromExplosion(BlockState var0, Level var1, BlockPos var2, Explosion var3) {
      return ForgeHelperImpl.canDropFromExplosion(var0, var1, var2, var3);
   }

   /** @deprecated */
   public static boolean isDye(ItemStack var0) {
      return ForgeHelperImpl.isDye(var0);
   }

   public static DyeColor getColor(ItemStack var0) {
      return ForgeHelperImpl.getColor(var0);
   }

   public static BlockState rotateBlock(BlockState var0, Level var1, BlockPos var2, Rotation var3) {
      return ForgeHelperImpl.rotateBlock(var0, var1, var2, var3);
   }

   public static boolean isMultipartEntity(Entity var0) {
      return ForgeHelperImpl.isMultipartEntity(var0);
   }

   public static RailShape getRailDirection(BaseRailBlock var0, BlockState var1, Level var2, BlockPos var3, AbstractMinecart var4) {
      return ForgeHelperImpl.getRailDirection(var0, var1, var2, var3, var4);
   }

   public static Optional<ItemStack> getCraftingRemainingItem(ItemStack var0) {
      return ForgeHelperImpl.getCraftingRemainingItem(var0);
   }

   public static void reviveEntity(Entity var0) {
      ForgeHelperImpl.reviveEntity(var0);
   }

   public static boolean fireOnCropsGrowPre(ServerLevel var0, BlockPos var1, BlockState var2, boolean var3) {
      return ForgeHelperImpl.fireOnCropsGrowPre(var0, var1, var2, var3);
   }

   public static void fireOnCropsGrowPost(ServerLevel var0, BlockPos var1, BlockState var2) {
      ForgeHelperImpl.fireOnCropsGrowPost(var0, var1, var2);
   }

   public static void fireOnEquipmentChange(LivingEntity var0, EquipmentSlot var1, ItemStack var2, ItemStack var3) {
      ForgeHelperImpl.fireOnEquipmentChange(var0, var1, var2, var3);
   }

   public static InteractionResult fireOnRightClickBlock(Player var0, InteractionHand var1, BlockPos var2, BlockHitResult var3) {
      return ForgeHelperImpl.fireOnRightClickBlock(var0, var1, var2, var3);
   }

   public static int getLightEmission(BlockState var0, Level var1, BlockPos var2) {
      return ForgeHelperImpl.getLightEmission(var0, var1, var2);
   }

   public static Map<Block, Item> getBlockItemMap() {
      return ForgeHelperImpl.getBlockItemMap();
   }

   public static boolean isInFluidThatCanExtinguish(Entity var0) {
      return ForgeHelperImpl.isInFluidThatCanExtinguish(var0);
   }

   public static void registerDefaultContainerCap(BlockEntityType<? extends Container> var0) {
      ForgeHelperImpl.registerDefaultContainerCap(var0);
   }

   public static ResourceLocation getQueriedLootTableId(LootContext var0) {
      return ForgeHelperImpl.getQueriedLootTableId(var0);
   }
}
