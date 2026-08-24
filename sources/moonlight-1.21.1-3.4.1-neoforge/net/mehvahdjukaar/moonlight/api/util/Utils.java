package net.mehvahdjukaar.moonlight.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.block.IOneUserInteractable;
import net.mehvahdjukaar.moonlight.api.client.IScreenProvider;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.api.misc.InvPlacer;
import net.mehvahdjukaar.moonlight.api.misc.TileOrEntityTarget;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.api.misc.Triplet;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.codec.CodecUtils;
import net.mehvahdjukaar.moonlight.api.util.codec.LenientListCodec;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {
   @Deprecated(
      forRemoval = true
   )
   public static final Codec<Boolean> MOD_LOADED_CODEC = Codec.STRING.xmap(PlatHelper::isModLoaded, b -> "");
   @Deprecated(
      forRemoval = true
   )
   public static final Codec<AABB> AABB_CODEC = RecordCodecBuilder.create(
      i -> i.group(Vec3.CODEC.fieldOf("from").forGetter(AABB::getMinPosition), Vec3.CODEC.fieldOf("to").forGetter(AABB::getMaxPosition)).apply(i, AABB::new)
   );

   @Deprecated(
      forRemoval = true
   )
   public static boolean openGuiIfPossible(BlockEntity be, ServerPlayer player, ItemStack stack, Direction hitFace) {
      return openGuiIfPossible(be, player, stack, hitFace, new Vec3(0.5, 0.5, 0.5));
   }

   public static boolean openGuiIfPossible(BlockEntity be, ServerPlayer player, ItemStack stack, Direction hitFace, Vec3 hitPos) {
      return openGuiIfPossible(TileOrEntityTarget.of(be), player, stack, hitFace, hitPos);
   }

   public static boolean openGuiIfPossible(TileOrEntityTarget target, ServerPlayer player, ItemStack stack, Direction hitFace, Vec3 hitPos) {
      Object targetObj = target.getTargetOrThrow(player.level());
      BlockPos targetPos = targetObj instanceof Entity e ? e.blockPosition() : ((BlockEntity)targetObj).getBlockPos();
      if (!mayPerformBlockAction(player, targetPos, stack)) {
         return false;
      } else {
         Objects.requireNonNull(targetObj);
         switch (targetObj) {
            case IOneUserInteractable ci when !ci.canBeUsedBy(targetPos, player):
               return false;
            case IScreenProvider sp:
               if (targetObj instanceof IOneUserInteractable cix) {
                  cix.setCurrentUser(player.getUUID());
               }

               sp.sendOpenGuiPacket(player, hitFace, hitPos);
               return false;
            case MenuProvider mp:
               if (targetObj instanceof IOneUserInteractable cix) {
                  cix.setCurrentUser(player.getUUID());
               }

               PlatHelper.openCustomMenu(player, mp, target::write);
               return true;
            default:
               return false;
         }
      }
   }

   public static void spawnItemWithTileData(Player player, RandomizableContainerBlockEntity tile) {
      Level level = player.level();
      if (!level.isClientSide && player.isCreative() && !tile.isEmpty()) {
         BlockPos pos = tile.getBlockPos();
         ItemStack itemstack = saveTileToItem(tile);
         ItemEntity itementity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemstack);
         itementity.setDefaultPickUpDelay();
         level.addFreshEntity(itementity);
      } else {
         tile.unpackLootTable(player);
      }
   }

   public static ItemStack saveTileToItem(BlockEntity tile) {
      Block block = tile.getBlockState().getBlock();
      ItemStack stack = new ItemStack(block.asItem());
      tile.saveToItem(stack, tile.getLevel().registryAccess());
      return stack;
   }

   public static void loadTileFromItem(BlockEntity tile, ItemStack stack) {
      CustomData comp = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
      if (comp != null) {
         tile.loadWithComponents(comp.copyTag(), tile.getLevel().registryAccess());
      }
   }

   public static void swapItem(Player player, InteractionHand hand, ItemStack oldItem, ItemStack newItem, boolean bothSides) {
      if (!player.level().isClientSide || bothSides) {
         player.setItemInHand(hand, ItemUtils.createFilledResult(oldItem.copy(), player, newItem, player.isCreative()));
      }
   }

   public static void swapItem(Player player, InteractionHand hand, ItemStack oldItem, ItemStack newItem) {
      swapItem(player, hand, oldItem, newItem, false);
   }

   public static void swapItemNBT(Player player, InteractionHand hand, ItemStack oldItem, ItemStack newItem) {
      if (!player.level().isClientSide) {
         player.setItemInHand(hand, ItemUtils.createFilledResult(oldItem.copy(), player, newItem, false));
      }
   }

   public static void swapItem(Player player, InteractionHand hand, ItemStack newItem) {
      swapItem(player, hand, player.getItemInHand(hand), newItem);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void addStackToExisting(Player player, ItemStack stack, boolean avoidEmptyHands) {
      addItemOrDrop(
         player,
         stack,
         avoidEmptyHands ? InvPlacer.handOrExistingOrAnyAvoidEmptyHand(InteractionHand.MAIN_HAND) : InvPlacer.handOrExistingOrAny(InteractionHand.MAIN_HAND)
      );
   }

   public static void addItemOrDrop(Player player, ItemStack stack, InvPlacer placer) {
      Inventory inv = player.getInventory();
      placer.or(InvPlacer.DROP).place(stack, inv, player);
   }

   public static void addItemOrDrop(Player player, ItemStack stack) {
      addItemOrDrop(player, stack, InvPlacer.existingOrAny());
   }

   public static int getXPinaBottle(int bottleCount, RandomSource rand) {
      int xp = 0;

      for (int i = 0; i < bottleCount; i++) {
         xp += 3 + rand.nextInt(5) + rand.nextInt(5);
      }

      return xp;
   }

   public static ResourceLocation getId(Holder<?> object) {
      return ((ResourceKey)object.unwrapKey().orElseThrow()).location();
   }

   public static ResourceLocation getID(@NotNull Block object) {
      return BuiltInRegistries.BLOCK.getKey(object);
   }

   public static ResourceLocation getID(@NotNull EntityType<?> object) {
      return BuiltInRegistries.ENTITY_TYPE.getKey(object);
   }

   public static ResourceLocation getID(@NotNull Biome object) {
      return hackyGetRegistry(Registries.BIOME).getKey(object);
   }

   public static ResourceLocation getID(@NotNull DamageType type) {
      return hackyGetRegistry(Registries.DAMAGE_TYPE).getKey(type);
   }

   public static ResourceLocation getID(@NotNull ConfiguredFeature<?, ?> object) {
      return hackyGetRegistry(Registries.CONFIGURED_FEATURE).getKey(object);
   }

   public static ResourceLocation getID(@NotNull Item object) {
      return BuiltInRegistries.ITEM.getKey(object);
   }

   public static ResourceLocation getID(@NotNull Fluid object) {
      return BuiltInRegistries.FLUID.getKey(object);
   }

   public static ResourceLocation getID(@NotNull BlockEntityType<?> object) {
      return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(object);
   }

   public static ResourceLocation getID(@NotNull RecipeSerializer<?> object) {
      return BuiltInRegistries.RECIPE_SERIALIZER.getKey(object);
   }

   @Deprecated(
      forRemoval = true
   )
   public static ResourceLocation getID(@NotNull SoftFluid object) {
      return SoftFluidRegistry.hackyGetRegistry().getKey(object);
   }

   @Deprecated(
      forRemoval = true
   )
   public static ResourceLocation getID(@NotNull MLMapDecorationType<?, ?> object) {
      return MapDataInternal.hackyGetRegistry().getKey(object);
   }

   public static ResourceLocation getID(@NotNull Potion object) {
      return BuiltInRegistries.POTION.getKey(object);
   }

   public static ResourceLocation getID(@NotNull MobEffect object) {
      return BuiltInRegistries.MOB_EFFECT.getKey(object);
   }

   public static ResourceLocation getID(@NotNull CreativeModeTab object) {
      return BuiltInRegistries.CREATIVE_MODE_TAB.getKey(object);
   }

   public static ResourceLocation getID(@NotNull StatType<?> object) {
      return BuiltInRegistries.STAT_TYPE.getKey(object);
   }

   public static ResourceLocation getID(@NotNull Object object) {
      return switch (object) {
         case Block b -> getID(b);
         case Item bx -> getID(bx);
         case EntityType<?> bxx -> getID(bxx);
         case BlockEntityType<?> bxxx -> getID(bxxx);
         case Biome bxxxx -> getID(bxxxx);
         case Fluid bxxxxx -> getID(bxxxxx);
         case RecipeSerializer<?> bxxxxxx -> getID(bxxxxxx);
         case ConfiguredFeature<?, ?> c -> getID(c);
         case Potion cx -> getID(cx);
         case MobEffect cxx -> getID(cxx);
         case Supplier<?> s -> getID(s.get());
         case SoftFluid sx -> getID(sx);
         case MLMapDecorationType<?, ?> sxx -> getID(sxx);
         case CreativeModeTab t -> getID(t);
         case DamageType tx -> getID(tx);
         case StatType<?> txx -> getID(txx);
         case Holder<?> h -> getId(h);
         default -> throw new UnsupportedOperationException(
            "Unsupported class type " + object.getClass() + ". Expected a registry entry for a call to Utils.getID()"
         );
      };
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T> boolean isTagged(T entry, Registry<T> registry, TagKey<T> tag) {
      return registry.wrapAsHolder(entry).is(tag);
   }

   public static <T> RegistryLookup<T> hackyFindRegistryOf(Holder<T> holder, ResourceKey<Registry<T>> registryKey) {
      if (holder instanceof Reference<T> ref && ref.owner instanceof RegistryLookup<T> ra) {
         return ra;
      } else {
         if (PlatHelper.getPhysicalSide().isClient()) {
            Level level = ClientHelper.getLocalLevel();
            if (level != null) {
               RegistryAccess clientRa = level.registryAccess();
               Registry<T> r = clientRa.registryOrThrow(registryKey);
               if (holder.canSerializeIn(r.holderOwner())) {
                  return clientRa.lookupOrThrow(registryKey);
               }
            }
         }

         MinecraftServer s = PlatHelper.getCurrentServer();
         if (s != null) {
            Frozen serverRa = s.registryAccess();
            Registry<T> r = serverRa.registryOrThrow(registryKey);
            if (holder.canSerializeIn(r.holderOwner())) {
               return serverRa.lookupOrThrow(registryKey);
            }
         }

         throw new UnsupportedOperationException("Failed to find registry access for " + holder);
      }
   }

   public static RegistryAccess hackyGetRegistryAccess() {
      MinecraftServer s = PlatHelper.getCurrentServer();
      if (!PlatHelper.getPhysicalSide().isClient()) {
         if (s != null) {
            return s.registryAccess();
         } else {
            WeakReference<RegistryAccess> hack2 = Moonlight.EARLY_REGISTRY_ACCESS.get();
            if (hack2 != null) {
               return hack2.get();
            } else {
               throw new UnsupportedOperationException("Failed to get registry access. This is a bug");
            }
         }
      } else if (s == null || !s.isSameThread() && MoonlightClient.isClientThread()) {
         Level level = ClientHelper.getLocalLevel();
         if (level != null) {
            return level.registryAccess();
         } else {
            WeakReference<RegistryAccess> hack2 = Moonlight.EARLY_REGISTRY_ACCESS.get();
            if (hack2 != null) {
               return hack2.get();
            } else {
               throw new UnsupportedOperationException("Failed to get registry access: level was null");
            }
         }
      } else {
         return s.registryAccess();
      }
   }

   public static <T> Registry<T> hackyGetRegistry(ResourceKey<Registry<T>> key) {
      return hackyGetRegistryAccess().registryOrThrow(key);
   }

   public static Properties copyPropertySafe(Block blockBehaviour) {
      Properties p = Properties.ofFullCopy(blockBehaviour);
      BlockState state = blockBehaviour.defaultBlockState();
      p.lightLevel(s -> state.getLightEmission());
      p.offsetType(OffsetType.NONE);
      p.isValidSpawn(
         (blockState, blockGetter, pos, entityType) -> blockState.isFaceSturdy(blockGetter, pos, Direction.UP) && blockState.getLightEmission() < 14
      );
      p.mapColor(blockBehaviour.defaultMapColor());
      p.emissiveRendering((blockState, blockGetter, blockPos) -> false);
      return p;
   }

   public static void awardAdvancement(ServerPlayer sp, ResourceLocation name) {
      awardAdvancement(sp, name, "unlock");
   }

   public static void awardAdvancement(ServerPlayer sp, ResourceLocation name, String unlockProp) {
      AdvancementHolder advancement = sp.getServer().getAdvancements().get(name);
      if (advancement != null) {
         PlayerAdvancements advancements = sp.getAdvancements();
         if (!advancements.getOrStartProgress(advancement).isDone()) {
            advancements.award(advancement, unlockProp);
         }
      }
   }

   @Nullable
   public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(
      BlockEntityType<A> type, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker
   ) {
      return targetType == type ? ticker : null;
   }

   @Nullable
   public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(
      BlockEntityType<A> type, BlockEntityType<E> targetType, Consumer<A> tickFunc
   ) {
      return targetType == type ? (level, blockPos, blockState, blockEntity) -> tickFunc.accept((A)blockEntity) : null;
   }

   public static BlockState readBlockState(CompoundTag compound, @Nullable Level level) {
      HolderGetter<Block> holderGetter = (HolderGetter<Block>)(level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup());
      return NbtUtils.readBlockState(holderGetter, compound);
   }

   public static <T extends Comparable<T>, A extends Property<T>> BlockState replaceProperty(BlockState from, BlockState to, A property) {
      return from.hasProperty(property) ? (BlockState)to.setValue(property, from.getValue(property)) : to;
   }

   public static boolean mayPerformBlockAction(Player player, BlockPos pos, ItemStack stack) {
      GameType gameMode;
      if (player instanceof ServerPlayer sp) {
         gameMode = sp.gameMode.getGameModeForPlayer();
      } else {
         gameMode = Minecraft.getInstance().gameMode.getPlayerMode();
      }

      boolean result = !player.blockActionRestricted(player.level(), pos, gameMode);
      return !result
            && gameMode == GameType.ADVENTURE
            && !stack.isEmpty()
            && stack.canPlaceOnBlockInAdventureMode(new BlockInWorld(player.level(), pos, false))
         ? true
         : result;
   }

   public static Optional<BlockState> getRotatedDirectionalBlock(BlockState state, Direction axis, boolean ccw) {
      Vec3 targetNormal = MthUtils.V3itoV3(((Direction)state.getValue(BlockStateProperties.FACING)).getNormal());
      Vec3 myNormal = MthUtils.V3itoV3(axis.getNormal());
      if (!ccw) {
         targetNormal = targetNormal.scale(-1.0);
      }

      Vec3 rotated = myNormal.cross(targetNormal);
      if (!rotated.equals(Vec3.ZERO)) {
         Direction newDir = Direction.getNearest(rotated.x(), rotated.y(), rotated.z());
         return Optional.of((BlockState)state.setValue(BlockStateProperties.FACING, newDir));
      } else {
         return Optional.empty();
      }
   }

   public static boolean isMethodImplemented(Class<?> original, Class<?> subclass, String name) {
      Method declaredMethod = findMethodWithMatchingName(subclass, name);
      Method modMethod = findMethodWithMatchingName(original, name);
      return declaredMethod != null
         && modMethod != null
         && Arrays.equals((Object[])declaredMethod.getParameterTypes(), (Object[])modMethod.getParameterTypes());
   }

   private static Method findMethodWithMatchingName(Class<?> clazz, String name) {
      for (Method method : clazz.getDeclaredMethods()) {
         if (method.getName().equals(name)) {
            return method;
         }
      }

      return null;
   }

   @Deprecated(
      forRemoval = true
   )
   public static <A> MapCodec<A> safeOptFieldOf(Codec<A> c, String name, Supplier<A> defaultValue) {
      return CodecUtils.safeOptFieldOf(c, name, defaultValue);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <K, V, C extends BaseMapCodec<K, V> & Codec<Map<K, V>>> C optionalMapCodec(Codec<K> keyCodec, Codec<V> elementCodec) {
      return CodecUtils.optionalMapCodec(keyCodec, elementCodec);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T> Codec<T> optionalRegistryCodec(Registry<T> reg, T defaultValue) {
      return CodecUtils.optionalRegistryCodec(reg, defaultValue);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T> Codec<List<T>> optionalRegistryListCodec(Registry<T> reg) {
      return CodecUtils.optionalRegistryListCodec(reg);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <A> Codec<List<A>> lenientListOrSingleCodec(Codec<A> elementCodec) {
      return CodecUtils.lenientListOrSingleCodec(elementCodec);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <A> LenientListCodec<A> lenientListCodec(Codec<A> elementCodec) {
      return CodecUtils.lenientListCodec(elementCodec);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <E> Codec<HolderSet<E>> lenientHomogeneousList(ResourceKey<? extends Registry<E>> registryKey) {
      return CodecUtils.lenientHomogeneousList(registryKey);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <T extends Enum<T>> StreamCodec<FriendlyByteBuf, T> enumStreamCodec(Class<T> enumClass) {
      return CodecUtils.enumStreamCodec(enumClass);
   }

   public static ResourceLocation idWithOptionalNamespace(String id, String namespace) {
      return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(namespace, id);
   }

   @Nullable
   public static <T> T findFirstInRegistry(Registry<T> registry, ResourceLocation... ids) {
      for (ResourceLocation r : ids) {
         Optional<T> optional = registry.getOptional(r);
         if (optional.isPresent()) {
            return optional.get();
         }
      }

      return null;
   }

   @Nullable
   public static <T> T findFirstInRegistry(Registry<T> registry, Iterable<ResourceLocation> ids) {
      for (ResourceLocation r : ids) {
         Optional<T> optional = registry.getOptional(r);
         if (optional.isPresent()) {
            return optional.get();
         }
      }

      return null;
   }

   public static <T, U, D, R> TriFunction<T, U, D, R> memoize(final TriFunction<T, U, D, R> memoBiFunction) {
      return new TriFunction<T, U, D, R>() {
         private final Map<Triplet<T, U, D>, R> cache = new ConcurrentHashMap<>();

         @Override
         public R apply(T object, U object2, D object3) {
            return this.cache.computeIfAbsent(Triplet.of(object, object2, object3), pair -> memoBiFunction.apply(pair.left(), pair.middle(), pair.right()));
         }

         @Override
         public String toString() {
            String var10000 = String.valueOf(memoBiFunction);
            return "memoize/3[function=" + var10000 + ", size=" + this.cache.size() + "]";
         }
      };
   }
}
