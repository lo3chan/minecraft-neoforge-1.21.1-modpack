package at.petrak.hexcasting.forge.xplat;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.addldata.ADVariantItem;
import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.HexHolderItem;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.AltioraAbility;
import at.petrak.hexcasting.api.player.FlightAbility;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.common.lib.hex.HexContinuationTypes;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.forge.cap.CapSyncers;
import at.petrak.hexcasting.forge.cap.adimpl.CapEntityIotaHolder;
import at.petrak.hexcasting.forge.cap.adimpl.CapItemHexHolder;
import at.petrak.hexcasting.forge.cap.adimpl.CapItemIotaHolder;
import at.petrak.hexcasting.forge.cap.adimpl.CapItemMediaHolder;
import at.petrak.hexcasting.forge.cap.adimpl.CapItemVariantItem;
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticIotaHolder;
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticMediaHolder;
import at.petrak.hexcasting.forge.interop.curios.CuriosApiInterop;
import at.petrak.hexcasting.forge.mixin.ForgeAccessorBuiltInRegistries;
import at.petrak.hexcasting.forge.network.ForgePacketHandler;
import at.petrak.hexcasting.forge.network.MsgBrainsweepAck;
import at.petrak.hexcasting.interop.pehkui.PehkuiInterop;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import at.petrak.hexcasting.xplat.IXplatTags;
import at.petrak.hexcasting.xplat.Platform;
import com.google.common.base.Suppliers;
import com.illusivesoulworks.caelus.api.CaelusApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleTypes;

public class ForgeXplatImpl implements IXplatAbstractions {
   private static final IXplatTags TAGS = new IXplatTags() {
      @Override
      public TagKey<Item> amethystDust() {
         return HexTags.Items.create(ResourceLocation.fromNamespaceAndPath("forge", "dusts/amethyst"));
      }

      @Override
      public TagKey<Item> gems() {
         return HexTags.Items.create(ResourceLocation.fromNamespaceAndPath("forge", "gems"));
      }
   };
   private static final Supplier<Registry<ActionRegistryEntry>> ACTION_REGISTRY = Suppliers.memoize(
      () -> ForgeAccessorBuiltInRegistries.hex$registerSimple(HexRegistries.ACTION, null)
   );
   private static final Supplier<Registry<SpecialHandler.Factory<?>>> SPECIAL_HANDLER_REGISTRY = Suppliers.memoize(
      () -> ForgeAccessorBuiltInRegistries.hex$registerSimple(HexRegistries.SPECIAL_HANDLER, null)
   );
   private static final Supplier<Registry<IotaType<?>>> IOTA_TYPE_REGISTRY = Suppliers.memoize(
      () -> ForgeAccessorBuiltInRegistries.hex$registerDefaulted(HexRegistries.IOTA_TYPE, HexAPI.modLoc("null").toString(), registry -> HexIotaTypes.NULL)
   );
   private static final Supplier<Registry<Arithmetic>> ARITHMETIC_REGISTRY = Suppliers.memoize(
      () -> ForgeAccessorBuiltInRegistries.hex$registerSimple(HexRegistries.ARITHMETIC, null)
   );
   private static final Supplier<Registry<ContinuationFrame.Type<?>>> CONTINUATION_TYPE_REGISTRY = Suppliers.memoize(
      () -> ForgeAccessorBuiltInRegistries.hex$registerDefaulted(
         HexRegistries.CONTINUATION_TYPE, HexAPI.modLoc("end").toString(), registry -> HexContinuationTypes.END
      )
   );
   private static final Supplier<Registry<EvalSound>> EVAL_SOUND_REGISTRY = Suppliers.memoize(
      () -> ForgeAccessorBuiltInRegistries.hex$registerDefaulted(
         HexRegistries.EVAL_SOUND, HexAPI.modLoc("nothing").toString(), registry -> HexEvalSounds.NOTHING
      )
   );
   private static PehkuiInterop.ApiAbstraction PEHKUI_API = null;
   public static final String TAG_BRAINSWEPT = "hexcasting:brainswept";
   public static final String TAG_SENTINEL_EXISTS = "hexcasting:sentinel_exists";
   public static final String TAG_SENTINEL_GREATER = "hexcasting:sentinel_extends_range";
   public static final String TAG_SENTINEL_POSITION = "hexcasting:sentinel_position";
   public static final String TAG_SENTINEL_DIMENSION = "hexcasting:sentinel_dimension";
   public static final String TAG_PIGMENT = "hexcasting:pigment";
   public static final String TAG_FLIGHT_ALLOWED = "hexcasting:flight_allowed";
   public static final String TAG_FLIGHT_TIME = "hexcasting:flight_time";
   public static final String TAG_FLIGHT_ORIGIN = "hexcasting:flight_origin";
   public static final String TAG_FLIGHT_DIMENSION = "hexcasting:flight_dimension";
   public static final String TAG_FLIGHT_RADIUS = "hexcasting:flight_radius";
   public static final String TAG_ALTIORA_ALLOWED = "hexcasting:altiora_allowed";
   public static final String TAG_ALTIORA_GRACE = "hexcasting:altiora_grace_period";
   public static final ResourceLocation ALTIORA_ATTRIBUTE_ID = HexAPI.modLoc("altiora");
   public static final String TAG_HARNESS = "hexcasting:spell_harness";
   public static final String TAG_PATTERNS = "hexcasting:spell_patterns";

   @Override
   public Platform platform() {
      return Platform.FORGE;
   }

   @Override
   public boolean isPhysicalClient() {
      return FMLLoader.getDist() == Dist.CLIENT;
   }

   @Override
   public boolean isModPresent(String id) {
      return ModList.get().isLoaded(id);
   }

   @Override
   public void initPlatformSpecific() {
      if (this.isModPresent("curios")) {
         CuriosApiInterop.init();
      }
   }

   @Override
   public void setBrainsweepAddlData(Mob mob) {
      mob.getPersistentData().putBoolean("hexcasting:brainswept", true);
      if (mob.level() instanceof ServerLevel) {
         ForgePacketHandler.sendTracking(mob, MsgBrainsweepAck.of(mob));
      }
   }

   @Override
   public void setFlight(ServerPlayer player, FlightAbility flight) {
      CompoundTag tag = player.getPersistentData();
      tag.putBoolean("hexcasting:flight_allowed", flight != null);
      if (flight != null) {
         tag.putInt("hexcasting:flight_time", flight.timeLeft());
         tag.put("hexcasting:flight_origin", HexUtils.serializeToNBT(flight.origin()));
         tag.putString("hexcasting:flight_dimension", flight.dimension().location().toString());
         tag.putDouble("hexcasting:flight_radius", flight.radius());
      } else {
         tag.remove("hexcasting:flight_time");
         tag.remove("hexcasting:flight_origin");
         tag.remove("hexcasting:flight_dimension");
         tag.remove("hexcasting:flight_radius");
      }
   }

   @Override
   public void setAltiora(Player player, @Nullable AltioraAbility altiora) {
      CompoundTag tag = player.getPersistentData();
      tag.putBoolean("hexcasting:altiora_allowed", altiora != null);
      if (altiora != null) {
         tag.putInt("hexcasting:altiora_grace_period", altiora.gracePeriod());
      } else {
         tag.remove("hexcasting:altiora_allowed");
      }

      Holder<Attribute> elytraing = CaelusApi.getInstance().getFallFlyingAttribute();
      AttributeInstance inst = player.getAttributes().getInstance(elytraing);
      if (altiora != null) {
         if (inst.getModifier(ALTIORA_ATTRIBUTE_ID) == null) {
            inst.addTransientModifier(new AttributeModifier(ALTIORA_ATTRIBUTE_ID, 1.0, Operation.ADD_VALUE));
         }
      } else {
         inst.removeModifier(ALTIORA_ATTRIBUTE_ID);
      }

      if (player instanceof ServerPlayer serverPlayer) {
         CapSyncers.syncAltiora(serverPlayer);
      }
   }

   @Nullable
   @Override
   public FrozenPigment setPigment(Player player, @Nullable FrozenPigment pigment) {
      FrozenPigment old = this.getPigment(player);
      CompoundTag tag = player.getPersistentData();
      if (pigment != null) {
         tag.put("hexcasting:pigment", pigment.serializeToNBT());
      } else {
         tag.remove("hexcasting:pigment");
      }

      if (player instanceof ServerPlayer serverPlayer) {
         CapSyncers.syncPigment(serverPlayer);
      }

      return old;
   }

   @Override
   public void setSentinel(Player player, @Nullable Sentinel sentinel) {
      CompoundTag tag = player.getPersistentData();
      tag.putBoolean("hexcasting:sentinel_exists", sentinel != null);
      if (sentinel != null) {
         tag.putBoolean("hexcasting:sentinel_extends_range", sentinel.extendsRange());
         tag.put("hexcasting:sentinel_position", HexUtils.serializeToNBT(sentinel.position()));
         tag.putString("hexcasting:sentinel_dimension", sentinel.dimension().location().toString());
      } else {
         tag.remove("hexcasting:sentinel_extends_range");
         tag.remove("hexcasting:sentinel_position");
         tag.remove("hexcasting:sentinel_dimension");
      }

      if (player instanceof ServerPlayer serverPlayer) {
         CapSyncers.syncSentinel(serverPlayer);
      }
   }

   @Override
   public void setStaffcastImage(ServerPlayer player, @Nullable CastingImage image) {
      player.getPersistentData().put("hexcasting:spell_harness", image == null ? new CompoundTag() : image.serializeToNbt());
   }

   @Override
   public void setPatterns(ServerPlayer player, List<ResolvedPattern> patterns) {
      ListTag listTag = new ListTag();

      for (ResolvedPattern pattern : patterns) {
         listTag.add(pattern.serializeToNBT());
      }

      player.getPersistentData().put("hexcasting:spell_patterns", listTag);
   }

   @Override
   public boolean isBrainswept(Mob e) {
      return e.getPersistentData().getBoolean("hexcasting:brainswept");
   }

   @Override
   public FlightAbility getFlight(ServerPlayer player) {
      CompoundTag tag = player.getPersistentData();
      boolean allowed = tag.getBoolean("hexcasting:flight_allowed");
      if (allowed) {
         int timeLeft = tag.getInt("hexcasting:flight_time");
         Vec3 origin = HexUtils.vecFromNBT(tag.getLongArray("hexcasting:flight_origin"));
         double radius = tag.getDouble("hexcasting:flight_radius");
         ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("hexcasting:flight_dimension")));
         return new FlightAbility(timeLeft, dimension, origin, radius);
      } else {
         return null;
      }
   }

   @Override
   public AltioraAbility getAltiora(Player player) {
      CompoundTag tag = player.getPersistentData();
      boolean allowed = tag.getBoolean("hexcasting:altiora_allowed");
      if (allowed) {
         int grace = tag.getInt("hexcasting:altiora_grace_period");
         return new AltioraAbility(grace);
      } else {
         return null;
      }
   }

   @Override
   public FrozenPigment getPigment(Player player) {
      return FrozenPigment.fromNBT(player.getPersistentData().getCompound("hexcasting:pigment"));
   }

   @Override
   public Sentinel getSentinel(Player player) {
      CompoundTag tag = player.getPersistentData();
      boolean exists = tag.getBoolean("hexcasting:sentinel_exists");
      if (!exists) {
         return null;
      } else {
         boolean extendsRange = tag.getBoolean("hexcasting:sentinel_extends_range");
         Vec3 position = HexUtils.vecFromNBT(tag.getCompound("hexcasting:sentinel_position"));
         ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("hexcasting:sentinel_dimension")));
         return new Sentinel(extendsRange, position, dimension);
      }
   }

   @Override
   public CastingVM getStaffcastVM(ServerPlayer player, InteractionHand hand) {
      StaffCastEnv ctx = new StaffCastEnv(player, hand);
      return new CastingVM(CastingImage.loadFromNbt(player.getPersistentData().getCompound("hexcasting:spell_harness"), player.serverLevel()), ctx);
   }

   @Override
   public List<ResolvedPattern> getPatternsSavedInUi(ServerPlayer player) {
      ListTag patternsTag = player.getPersistentData().getList("hexcasting:spell_patterns", 10);
      List<ResolvedPattern> patterns = new ArrayList<>(patternsTag.size());

      for (int i = 0; i < patternsTag.size(); i++) {
         patterns.add(ResolvedPattern.fromNBT(patternsTag.getCompound(i)));
      }

      return patterns;
   }

   @Override
   public void clearCastingData(ServerPlayer player) {
      player.getPersistentData().remove("hexcasting:spell_harness");
      player.getPersistentData().remove("hexcasting:spell_patterns");
   }

   @Nullable
   @Override
   public ADMediaHolder findMediaHolder(ItemStack stack) {
      if (stack.getItem() instanceof MediaHolderItem holder) {
         return new CapItemMediaHolder(holder, stack);
      } else if (stack.is(HexItems.AMETHYST_DUST)) {
         return new CapStaticMediaHolder(HexConfig.common()::dustMediaAmount, 3000, stack);
      } else if (stack.is(Items.AMETHYST_SHARD)) {
         return new CapStaticMediaHolder(HexConfig.common()::shardMediaAmount, 2000, stack);
      } else if (stack.is(HexItems.CHARGED_AMETHYST)) {
         return new CapStaticMediaHolder(HexConfig.common()::chargedCrystalMediaAmount, 1000, stack);
      } else if (stack.is(HexItems.QUENCHED_SHARD)) {
         return new CapStaticMediaHolder(() -> 300000L, 900, stack);
      } else {
         return stack.is(HexBlocks.QUENCHED_ALLAY.asItem()) ? new CapStaticMediaHolder(() -> 1200000L, 800, stack) : null;
      }
   }

   @Nullable
   @Override
   public ADMediaHolder findMediaHolder(ServerPlayer player) {
      return null;
   }

   @Nullable
   @Override
   public ADIotaHolder findDataHolder(ItemStack stack) {
      if (stack.getItem() instanceof IotaHolderItem holder) {
         return new CapItemIotaHolder(holder, stack);
      } else {
         return stack.is(Items.PUMPKIN_PIE) ? new CapStaticIotaHolder(s -> new DoubleIota(3.141592653589793 * s.getCount()), stack) : null;
      }
   }

   @Nullable
   @Override
   public ADIotaHolder findDataHolder(Entity entity) {
      if (entity instanceof ItemEntity item) {
         return new CapEntityIotaHolder.Wrapper(new ItemDelegatingEntityIotaHolder.ToItemEntity(item));
      } else if (entity instanceof ItemFrame frame) {
         return new CapEntityIotaHolder.Wrapper(new ItemDelegatingEntityIotaHolder.ToItemFrame(frame));
      } else {
         return entity instanceof EntityWallScroll scroll ? new CapEntityIotaHolder.Wrapper(new ItemDelegatingEntityIotaHolder.ToWallScroll(scroll)) : null;
      }
   }

   @Nullable
   @Override
   public ADHexHolder findHexHolder(ItemStack stack) {
      return stack.getItem() instanceof HexHolderItem holder ? new CapItemHexHolder(holder, stack) : null;
   }

   @Nullable
   @Override
   public ADVariantItem findVariantHolder(ItemStack stack) {
      return stack.getItem() instanceof VariantItem holder ? new CapItemVariantItem(holder, stack) : null;
   }

   @Override
   public boolean isPigment(ItemStack stack) {
      return stack.getItem() instanceof PigmentItem;
   }

   @Override
   public ColorProvider getColorProvider(FrozenPigment pigment) {
      return pigment.item().getItem() instanceof PigmentItem item ? item.provideColor(pigment.item(), pigment.owner()) : ColorProvider.MISSING;
   }

   @Override
   public void sendPacketToPlayer(ServerPlayer target, IMessage packet) {
      ForgePacketHandler.sendToPlayer(target, packet);
   }

   @Override
   public void sendPacketNear(Vec3 pos, double radius, ServerLevel dimension, IMessage packet) {
      ForgePacketHandler.sendNear(null, pos.x, pos.y, pos.z, radius, dimension, packet);
   }

   @Override
   public void sendPacketTracking(Entity entity, IMessage packet) {
      ForgePacketHandler.sendTracking(entity, packet);
   }

   @Override
   public Packet<ClientGamePacketListener> toVanillaClientboundPacket(IMessage message) {
      return ForgePacketHandler.toVanillaClientboundPacket(message);
   }

   @Override
   public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
      return Builder.of(func::apply, blocks).build(null);
   }

   @Override
   public boolean tryPlaceFluid(Level level, InteractionHand hand, BlockPos pos, Fluid fluid) {
      Optional<IFluidHandler> handler = FluidUtil.getFluidHandler(level, pos, Direction.UP);
      return handler.isPresent() && handler.get().fill(new FluidStack(fluid, 1000), FluidAction.EXECUTE) > 0;
   }

   @Override
   public boolean drainAllFluid(Level level, BlockPos pos) {
      Optional<IFluidHandler> handler = FluidUtil.getFluidHandler(level, pos, Direction.UP);
      if (handler.isPresent()) {
         boolean any = false;
         IFluidHandler pool = handler.get();

         for (int i = 0; i < pool.getTanks(); i++) {
            if (!pool.drain(pool.getFluidInTank(i), FluidAction.EXECUTE).isEmpty()) {
               any = true;
            }
         }

         return any;
      } else {
         return false;
      }
   }

   @Override
   public Ingredient getUnsealedIngredient(ItemStack stack) {
      return Ingredient.of(new ItemLike[]{stack.getItem()});
   }

   @Override
   public boolean isCorrectTierForDrops(Tier tier, BlockState bs) {
      return !bs.requiresCorrectToolForDrops() || !bs.is(tier.getIncorrectBlocksForDrops());
   }

   @Override
   public IXplatTags tags() {
      return TAGS;
   }

   @Override
   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder isShearsCondition() {
      return LootItemRandomChanceCondition.randomChance(1.0F);
   }

   @Override
   public String getModName(String namespace) {
      if (namespace.equals("c")) {
         return "Common";
      } else {
         Optional<? extends ModContainer> container = ModList.get().getModContainerById(namespace);
         return container.isPresent() ? container.get().getModInfo().getDisplayName() : namespace;
      }
   }

   @Override
   public Registry<ActionRegistryEntry> getActionRegistry() {
      return ACTION_REGISTRY.get();
   }

   @Override
   public Registry<SpecialHandler.Factory<?>> getSpecialHandlerRegistry() {
      return SPECIAL_HANDLER_REGISTRY.get();
   }

   @Override
   public Registry<IotaType<?>> getIotaTypeRegistry() {
      return IOTA_TYPE_REGISTRY.get();
   }

   @Override
   public Registry<Arithmetic> getArithmeticRegistry() {
      return ARITHMETIC_REGISTRY.get();
   }

   @Override
   public Registry<ContinuationFrame.Type<?>> getContinuationTypeRegistry() {
      return CONTINUATION_TYPE_REGISTRY.get();
   }

   @Override
   public Registry<EvalSound> getEvalSoundRegistry() {
      return EVAL_SOUND_REGISTRY.get();
   }

   @Override
   public boolean isBreakingAllowed(ServerLevel world, BlockPos pos, BlockState state, @Nullable Player player) {
      if (player == null) {
         player = FakePlayerFactory.get(world, HEXCASTING);
      }

      return !((BreakEvent)NeoForge.EVENT_BUS.post(new BreakEvent(world, pos, state, player))).isCanceled();
   }

   @Override
   public boolean isPlacingAllowed(ServerLevel world, BlockPos pos, ItemStack blockStack, @Nullable Player player) {
      if (player == null) {
         player = FakePlayerFactory.get(world, HEXCASTING);
      }

      ItemStack cached = player.getMainHandItem();
      player.setItemInHand(InteractionHand.MAIN_HAND, blockStack.copy());
      RightClickBlock evt = CommonHooks.onRightClickBlock(
         player, InteractionHand.MAIN_HAND, pos, new BlockHitResult(Vec3.atCenterOf(pos), Direction.DOWN, pos, true)
      );
      player.setItemInHand(InteractionHand.MAIN_HAND, cached);
      return !evt.isCanceled();
   }

   @Override
   public PehkuiInterop.ApiAbstraction getPehkuiApi() {
      if (!this.isModPresent("pehkui")) {
         throw new IllegalArgumentException("cannot get the pehkui api without pehkui");
      } else {
         if (PEHKUI_API == null) {
            PEHKUI_API = new PehkuiInterop.ApiAbstraction() {
               @Override
               public float getScale(Entity e) {
                  return ScaleTypes.BASE.getScaleData(e).getScale();
               }

               @Override
               public void setScale(Entity e, float scale) {
                  ScaleTypes.BASE.getScaleData(e).setScale(scale);
               }
            };
         }

         return PEHKUI_API;
      }
   }
}
