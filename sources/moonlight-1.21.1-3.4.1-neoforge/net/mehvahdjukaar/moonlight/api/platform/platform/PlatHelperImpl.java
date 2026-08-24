package net.mehvahdjukaar.moonlight.api.platform.platform;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.recipe.platform.ResourceConditionsBridge;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.LoaderCondition;
import net.mehvahdjukaar.moonlight.platform.MoonlightForge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

public class PlatHelperImpl {
   private static final ItemAbility TINKERS_LIGHT_FIRE = ItemAbility.get("light_fire");
   private static final List<String> LINK_KEYS = List.of("displayURL", "curseforge", "modrinth", "sources", "discord");
   private static final MapCodec<PlatHelperImpl.ForgeCondition> CONDITION_CODEC = ResourceConditionsBridge.SINGLE_OR_LIST
      .xmap(PlatHelperImpl.ForgeCondition::new, PlatHelperImpl.ForgeCondition::condition)
      .fieldOf("neoforge:conditions");

   public static boolean isDev() {
      return !FMLLoader.isProduction();
   }

   public static PlatHelper.Side getPhysicalSide() {
      return FMLEnvironment.dist == Dist.CLIENT ? PlatHelper.Side.CLIENT : PlatHelper.Side.SERVER;
   }

   public static PlatHelper.Platform getPlatform() {
      return PlatHelper.Platform.FORGE;
   }

   public static boolean isModLoaded(String name) {
      return ModList.get().isLoaded(name);
   }

   @Nullable
   public static <T> Field findField(Class<? super T> clazz, String fieldName) {
      try {
         return ObfuscationReflectionHelper.findField(clazz, fieldName);
      } catch (Exception var3) {
         return null;
      }
   }

   @Nullable
   public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
      try {
         return ObfuscationReflectionHelper.findMethod(clazz, methodName, parameterTypes);
      } catch (Exception var4) {
         return null;
      }
   }

   public static boolean isMobGriefingOn(Level level, Entity entity) {
      return EventHooks.canEntityGrief(level, entity);
   }

   public static boolean isAreaLoaded(LevelReader level, BlockPos pos, int maxRange) {
      return level.isAreaLoaded(pos, maxRange);
   }

   @Nullable
   public static FoodProperties getFoodProperties(ItemStack stack, Player player) {
      return stack.getFoodProperties(player);
   }

   public static int getBurnTime(ItemStack stack) {
      return stack.getBurnTime(RecipeType.SMELTING);
   }

   public static boolean canLightFire(ItemStack stack) {
      return stack.canPerformAction(ItemAbilities.FIRESTARTER_LIGHT) || stack.canPerformAction(TINKERS_LIGHT_FIRE);
   }

   public static int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return state.getFireSpreadSpeed(level, pos, direction);
   }

   public static int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return state.getFlammability(level, pos, direction);
   }

   public static boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return state.isFlammable(level, pos, direction);
   }

   public static void onCaughtFire(BlockState state, Level level, BlockPos pos, Direction direction, @Nullable LivingEntity igniter) {
      state.onCaughtFire(level, pos, direction, igniter);
   }

   public static boolean isFireSource(BlockState blockState, Level level, BlockPos pos, Direction up) {
      return blockState.isFireSource(level, pos, up);
   }

   @Nullable
   public static MinecraftServer getCurrentServer() {
      return ServerLifecycleHooks.getCurrentServer();
   }

   public static Path getGamePath() {
      return FMLPaths.GAMEDIR.get();
   }

   public static String getModPageUrl(String modId) {
      return ((ModContainer)ModList.get().getModContainerById(modId).get()).getModInfo().getModURL().map(URL::toString).orElse(null);
   }

   public static String getModCurseforgeUrl(String modId) {
      String custom = readModString(modId, "curseforge");
      return custom != null ? custom : getModPageUrl(modId);
   }

   public static String getModModrinthUrl(String modId) {
      return readModString(modId, "modrinth");
   }

   public static String getModSourcesUrl(String modId) {
      String custom = readModString(modId, "sources");
      if (custom != null) {
         return custom;
      } else {
         String issues = ModList.get()
            .getModContainerById(modId)
            .map(c -> c.getModInfo().getOwningFile().getConfig())
            .flatMap(cfg -> cfg.getConfigElement(new String[]{"issueTrackerURL"}))
            .orElse(null);
         return issues != null && issues.endsWith("/issues") ? issues.substring(0, issues.length() - "/issues".length()) : issues;
      }
   }

   public static List<String> getModLinks(String modId) {
      ModContainer container = (ModContainer)ModList.get().getModContainerById(modId).orElse(null);
      if (container == null) {
         return List.of();
      } else {
         IModInfo info = container.getModInfo();
         List<String> out = new ArrayList<>();
         info.getModURL().map(URL::toString).ifPresent(out::add);

         for (String key : LINK_KEYS) {
            String url = readModString(modId, key);
            if (url != null) {
               out.add(url);
            }
         }

         info.getOwningFile().getConfig().getConfigElement(new String[]{"issueTrackerURL"}).ifPresent(out::add);
         return out.stream().map(String::trim).filter(u -> u.startsWith("http")).distinct().toList();
      }
   }

   @Nullable
   private static String readModString(String modId, String key) {
      return ModList.get().getModContainerById(modId).flatMap(c -> c.getModInfo().getConfig().getConfigElement(new String[]{key})).orElse(null);
   }

   public static String getModName(String modId) {
      return ModList.get().getModContainerById(modId).map(c -> c.getModInfo().getDisplayName()).orElseGet(() -> TextHelper.getReadableName(modId));
   }

   @Nullable
   public static Path getModIcon(String modId) {
      ModContainer container = (ModContainer)ModList.get().getModContainerById(modId).orElse(null);
      if (container == null) {
         return null;
      } else {
         IModInfo info = container.getModInfo();
         String logo = (String)info.getLogoFile().orElse(null);
         if (logo != null && !logo.isBlank()) {
            Path path = info.getOwningFile().getFile().findResource(new String[]{logo});
            return path != null && Files.exists(path) ? path : null;
         } else {
            return null;
         }
      }
   }

   @Nullable
   public static Path findModResource(String modId, String path) {
      IModFileInfo file = ModList.get().getModFileById(modId);
      if (file == null) {
         return null;
      } else {
         Path found = file.getFile().findResource(new String[]{path});
         return found != null && Files.exists(found) ? found : null;
      }
   }

   public static List<String> getModAuthors(String modId) {
      String authors = readModString(modId, "authors");
      return authors != null && !authors.isBlank() ? Arrays.stream(authors.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList() : List.of();
   }

   @Nullable
   public static String getModLicense(String modId) {
      return ModList.get().getModContainerById(modId).map(c -> c.getModInfo().getOwningFile().getLicense()).filter(l -> !l.isBlank()).orElse(null);
   }

   public static SpawnEggItem newSpawnEgg(Supplier<? extends EntityType<? extends Mob>> entityType, int color, int outerColor, Properties properties) {
      return new DeferredSpawnEggItem(entityType, color, outerColor, properties);
   }

   public static Path getModFilePath(String modId) {
      return ModList.get().getModFileById(modId).getFile().getFilePath();
   }

   public static FlowerPotBlock newFlowerPot(
      @Nullable Supplier<FlowerPotBlock> emptyPot,
      Supplier<? extends Block> supplier,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties properties
   ) {
      return new FlowerPotBlock(emptyPot, supplier, properties);
   }

   public static SimpleParticleType newSimpleParticle() {
      return new SimpleParticleType(true);
   }

   public static <T extends ParticleOptions> ParticleType<T> newParticle(
      final Function<ParticleType<T>, MapCodec<T>> codec,
      final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec,
      boolean overrideLimiter
   ) {
      return new ParticleType<T>(overrideLimiter) {
         public MapCodec<T> codec() {
            return codec.apply(this);
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec.apply(this);
         }
      };
   }

   public static <T extends BlockEntity> BlockEntityType<T> newBlockEntityType(PlatHelper.BlockEntitySupplier<T> blockEntitySupplier, Block... validBlocks) {
      return Builder.of(blockEntitySupplier::create, validBlocks).build(null);
   }

   public static <E extends Entity> EntityType<E> newEntityType(
      String name,
      EntityFactory<E> factory,
      MobCategory category,
      float width,
      float height,
      int clientTrackingRange,
      boolean velocityUpdates,
      int updateInterval
   ) {
      return net.minecraft.world.entity.EntityType.Builder.of(factory, category)
         .sized(width, height)
         .clientTrackingRange(clientTrackingRange)
         .setShouldReceiveVelocityUpdates(velocityUpdates)
         .updateInterval(updateInterval)
         .build(name);
   }

   public static boolean isModLoadingValid() {
      return !ModLoader.hasErrors();
   }

   public static void openCustomMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataProvider) {
      player.openMenu(menuProvider, extraDataProvider);
   }

   public static boolean evaluateRecipeCondition(DynamicOps<JsonElement> ops, JsonElement jo) {
      return ICondition.conditionsMatched(ops, jo);
   }

   public static List<String> getInstalledMods() {
      return ModList.get().getMods().stream().<String>map(IModInfo::getModId).filter(s -> !s.startsWith("generated_")).toList();
   }

   public static Player getFakeServerPlayer(GameProfile id, ServerLevel level) {
      return FakePlayerFactory.get(level, id);
   }

   public static boolean isInitializing() {
      return !ModLoadingContext.get().getActiveNamespace().equals("minecraft");
   }

   public static void addCommonSetup(Runnable commonSetup) {
      Moonlight.assertInitPhase();
      Consumer<FMLCommonSetupEvent> eventConsumer = event -> event.enqueueWork(commonSetup);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addCommonSetupAsync(Runnable commonSetup) {
      Moonlight.assertInitPhase();
      Consumer<FMLCommonSetupEvent> eventConsumer = event -> commonSetup.run();
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addReloadableCommonSetup(BiConsumer<RegistryAccess, Boolean> listener) {
      Moonlight.assertInitPhase();
      Consumer<TagsUpdatedEvent> eventConsumer = event -> listener.accept(
         event.getRegistryAccess(), event.getUpdateCause() == UpdateCause.CLIENT_PACKET_RECEIVED
      );
      NeoForge.EVENT_BUS.addListener(eventConsumer);
   }

   public static void addServerReloadListener(Function<Provider, PreparableReloadListener> listener, ResourceLocation location) {
      Moonlight.assertInitPhase();
      Consumer<AddReloadListenerEvent> eventConsumer = event -> event.addListener(listener.apply(event.getServerResources().getRegistryLookup()));
      NeoForge.EVENT_BUS.addListener(eventConsumer);
   }

   public static String getModVersion(String modId) {
      return ModList.get().getModContainerById(modId).map(v -> v.getModInfo().getVersion().toString()).orElse(null);
   }

   public static Packet<ClientGamePacketListener> getEntitySpawnPacket(Entity entity, ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(entity, serverEntity);
   }

   public static <A> void setComponent(DataComponentHolder to, DataComponentType<A> type, A componentValue) {
      if (to instanceof MutableDataComponentHolder mc) {
         mc.set(type, componentValue);
      }
   }

   public static void invokeLevelUnload(Level l) {
      NeoForge.EVENT_BUS.post(new Unload(l));
   }

   public static boolean isFakePlayer(ServerPlayer instance) {
      return instance instanceof FakePlayer;
   }

   public static MapCodec<LoaderCondition> getConditionCodec() {
      return CONDITION_CODEC;
   }

   private record ForgeCondition(ICondition condition) implements LoaderCondition {
      @Override
      public boolean test(Provider ra) {
         return this.condition.test(IContext.EMPTY);
      }
   }
}
