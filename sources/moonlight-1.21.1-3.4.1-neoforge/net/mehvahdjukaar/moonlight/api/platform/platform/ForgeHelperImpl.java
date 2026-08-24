package net.mehvahdjukaar.moonlight.api.platform.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.mixins.platform.ContextAwareReloadListenerAccessor;
import net.mehvahdjukaar.moonlight.platform.MoonlightForge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
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
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
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
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.GameData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeHelperImpl {
   public static boolean fireOnProjectileImpact(Projectile projectile, HitResult blockHitResult) {
      return EventHooks.onProjectileImpact(projectile, blockHitResult);
   }

   public static <T extends RecipeInput> Recipe<T> copyRecipeConditions(Recipe<T> originalRecipe, Recipe<?> otherRecipe) {
      boolean success = false;
      return originalRecipe;
   }

   public static <T> RegistryOps<T> conditionalOps(DynamicOps<T> ops, Provider provider, SimplePreparableReloadListener<?> reloader) {
      ContextAwareReloadListenerAccessor rel = (ContextAwareReloadListenerAccessor)reloader;
      return new ConditionalOps(RegistryOps.create(ops, provider), rel.invokeGetContext());
   }

   public static <T> Codec<Optional<T>> conditionalCodec(Codec<T> codec) {
      return ConditionalOps.createConditionalCodec(codec);
   }

   public static boolean isCurativeItem(ItemStack stack, MobEffectInstance effect) {
      EffectCure cure = null;
      if (stack.getItem() instanceof MilkBucketItem) {
         cure = EffectCures.MILK;
      }

      if (stack.getItem() instanceof HoneyBottleItem) {
         cure = EffectCures.HONEY;
      }

      return cure != null ? effect.getCures().contains(cure) : false;
   }

   public static boolean canHarvestBlock(BlockState state, ServerLevel level, BlockPos pos, ServerPlayer player) {
      return state.canHarvestBlock(level, pos, player);
   }

   public static float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
      return state.getFriction(level, pos, entity);
   }

   public static boolean canEquipItem(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
      return stack.canEquip(slot, entity);
   }

   public static boolean canEntityDestroy(Level level, BlockPos blockPos, Animal animal) {
      return CommonHooks.canEntityDestroy(level, blockPos, animal);
   }

   public static boolean fireOnExplosionStart(Level level, Explosion explosion) {
      return EventHooks.onExplosionStart(level, explosion);
   }

   public static void fireOnExplosionDetonate(Level level, Explosion explosion, List<Entity> entities, double diameter) {
      EventHooks.onExplosionDetonate(level, explosion, entities, diameter);
   }

   public static void fireOnLivingConvert(LivingEntity skellyHorseMixin, LivingEntity newHorse) {
      EventHooks.onLivingConvert(newHorse, newHorse);
   }

   public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> outcome, Consumer<Integer> timer) {
      return EventHooks.canLivingConvert(entity, outcome, timer);
   }

   public static float getExplosionResistance(BlockState state, Level level, BlockPos pos, Explosion explosion) {
      return state.getExplosionResistance(level, pos, explosion);
   }

   public static void fireOnBlockExploded(BlockState blockstate, Level level, BlockPos blockpos, Explosion explosion) {
      blockstate.onBlockExploded(level, blockpos, explosion);
   }

   public static boolean isFireSource(BlockState blockState, Level level, BlockPos pos, Direction up) {
      return blockState.isFireSource(level, pos, up);
   }

   public static boolean canDropFromExplosion(BlockState blockstate, Level level, BlockPos blockpos, Explosion explosion) {
      return blockstate.canDropFromExplosion(level, blockpos, explosion);
   }

   public static boolean isDye(ItemStack itemstack) {
      return itemstack.is(Items.DYES);
   }

   public static DyeColor getColor(ItemStack stack) {
      return DyeColor.getColor(stack);
   }

   public static BlockState rotateBlock(BlockState state, Level world, BlockPos targetPos, Rotation rot) {
      return state.rotate(world, targetPos, rot);
   }

   public static boolean isMultipartEntity(Entity e) {
      return e.isMultipartEntity();
   }

   public static RailShape getRailDirection(BaseRailBlock railBlock, BlockState blockstate, Level level, BlockPos blockpos, AbstractMinecart o) {
      return railBlock.getRailDirection(blockstate, level, blockpos, o);
   }

   public static Optional<ItemStack> getCraftingRemainingItem(ItemStack itemstack) {
      return itemstack.hasCraftingRemainingItem() ? Optional.of(itemstack.getCraftingRemainingItem()) : Optional.empty();
   }

   public static void reviveEntity(Entity entity) {
      entity.revive();
   }

   public static boolean fireOnCropsGrowPre(ServerLevel level, BlockPos pos, BlockState state, boolean b) {
      return CommonHooks.canCropGrow(level, pos, state, b);
   }

   public static void fireOnCropsGrowPost(ServerLevel level, BlockPos pos, BlockState state) {
      CommonHooks.fireCropGrowPost(level, pos, state);
   }

   public static void fireOnEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to) {
      NeoForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(entity, slot, from, to));
   }

   @Nullable
   public static InteractionResult fireOnRightClickBlock(Player player, InteractionHand hand, BlockPos below, BlockHitResult rayTraceResult) {
      RightClickBlock ev = CommonHooks.onRightClickBlock(player, hand, below, rayTraceResult);
      return ev.isCanceled() ? ev.getCancellationResult() : null;
   }

   public static int getLightEmission(BlockState state, Level level, BlockPos pos) {
      return state.getLightEmission(level, pos);
   }

   public static Map<Block, Item> getBlockItemMap() {
      return GameData.getBlockItemMap();
   }

   public static void registerDefaultContainerCap(BlockEntityType<? extends Container> type) {
      Moonlight.assertInitPhase();
      Consumer<RegisterCapabilitiesEvent> eventConsumer = event -> event.registerBlockEntity(
         ItemHandler.BLOCK, type, (x$0, x$1) -> makeDefaultInvHandler((Container)x$0, x$1)
      );
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   @NotNull
   public static IItemHandlerModifiable makeDefaultInvHandler(Container container, Direction side) {
      return (IItemHandlerModifiable)(container instanceof WorldlyContainer wc
         ? new SidedInvWrapper(wc, side == null ? Direction.UP : side)
         : new InvWrapper(container));
   }

   public static boolean isInFluidThatCanExtinguish(Entity entity) {
      return entity.isInFluidType((a, b) -> a.canExtinguish(entity));
   }

   public static ResourceLocation getQueriedLootTableId(LootContext lootContext) {
      return lootContext.getQueriedLootTableId();
   }
}
