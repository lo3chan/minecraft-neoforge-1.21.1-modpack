package net.mehvahdjukaar.moonlight.api.platform;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.misc.TileOrEntityTarget;
import net.mehvahdjukaar.moonlight.api.platform.platform.PlatHelperImpl;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.fake_player.FakeGenericPlayer;
import net.mehvahdjukaar.moonlight.core.fake_player.FakeLocalPlayer;
import net.mehvahdjukaar.moonlight.core.misc.LoaderCondition;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlatHelper {
   public static boolean isAFakePlayer(Player player) {
      if (player instanceof FakeGenericPlayer) {
         return true;
      } else {
         return getPhysicalSide().isClient() && player instanceof FakeLocalPlayer ? true : player instanceof ServerPlayer sp && isFakePlayer(sp);
      }
   }

   public static boolean isIntegratedServer() {
      return getPhysicalSide().isClient() && getCurrentServer() != null;
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static String urlHost(String url) {
      return TextHelper.urlHost(url);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void registerResourcePack(PackType packType, Supplier<Pack> packSupplier) {
      RegHelper.registerResourcePack(packType, packSupplier);
   }

   public static <T extends ParticleOptions> ParticleType<T> newParticle(
      MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, boolean overrideLimiter
   ) {
      return newParticle(
         (Function<ParticleType<T>, MapCodec<T>>)(c -> codec),
         (Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>>)(c -> streamCodec),
         overrideLimiter
      );
   }

   @Deprecated(
      forRemoval = true
   )
   public static void addServerReloadListener(PreparableReloadListener listener, ResourceLocation location) {
      addServerReloadListener((Function<Provider, PreparableReloadListener>)(provider -> listener), location);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void openCustomMenu(ServerPlayer player, MenuProvider menuProvider, BlockPos pos) {
      openCustomMenu(player, menuProvider, (Consumer<RegistryFriendlyByteBuf>)(buf -> buf.writeBlockPos(pos)));
   }

   public static <T extends Entity & MenuProvider> void openCustomMenu(ServerPlayer player, T menuProvider) {
      TileOrEntityTarget target = TileOrEntityTarget.of(menuProvider);
      openCustomMenu(player, menuProvider, target::write);
   }

   public static <T extends BlockEntity & MenuProvider> void openCustomMenu(ServerPlayer player, T menuProvider) {
      TileOrEntityTarget target = TileOrEntityTarget.of(menuProvider);
      openCustomMenu(player, menuProvider, target::write);
   }

   public static void addCommonSetup(Runnable var0) {
      PlatHelperImpl.addCommonSetup(var0);
   }

   public static void addCommonSetupAsync(Runnable var0) {
      PlatHelperImpl.addCommonSetupAsync(var0);
   }

   public static void addReloadableCommonSetup(BiConsumer<RegistryAccess, Boolean> var0) {
      PlatHelperImpl.addReloadableCommonSetup(var0);
   }

   public static boolean isDev() {
      return PlatHelperImpl.isDev();
   }

   public static boolean isModLoadingValid() {
      return PlatHelperImpl.isModLoadingValid();
   }

   public static boolean isInitializing() {
      return PlatHelperImpl.isInitializing();
   }

   public static boolean evaluateRecipeCondition(DynamicOps<JsonElement> var0, JsonElement var1) {
      return PlatHelperImpl.evaluateRecipeCondition(var0, var1);
   }

   public static <A> void setComponent(DataComponentHolder var0, DataComponentType<A> var1, A var2) {
      PlatHelperImpl.setComponent(var0, var1, var2);
   }

   public static void invokeLevelUnload(Level var0) {
      PlatHelperImpl.invokeLevelUnload(var0);
   }

   public static boolean isFakePlayer(ServerPlayer var0) {
      return PlatHelperImpl.isFakePlayer(var0);
   }

   public static MapCodec<LoaderCondition> getConditionCodec() {
      return PlatHelperImpl.getConditionCodec();
   }

   public static PlatHelper.Platform getPlatform() {
      return PlatHelperImpl.getPlatform();
   }

   public static PlatHelper.Side getPhysicalSide() {
      return PlatHelperImpl.getPhysicalSide();
   }

   public static Path getGamePath() {
      return PlatHelperImpl.getGamePath();
   }

   public static Path getModFilePath(String var0) {
      return PlatHelperImpl.getModFilePath(var0);
   }

   public static String getModPageUrl(String var0) {
      return PlatHelperImpl.getModPageUrl(var0);
   }

   public static String getModCurseforgeUrl(String var0) {
      return PlatHelperImpl.getModCurseforgeUrl(var0);
   }

   public static String getModModrinthUrl(String var0) {
      return PlatHelperImpl.getModModrinthUrl(var0);
   }

   public static String getModSourcesUrl(String var0) {
      return PlatHelperImpl.getModSourcesUrl(var0);
   }

   public static List<String> getModLinks(String var0) {
      return PlatHelperImpl.getModLinks(var0);
   }

   public static String getModName(String var0) {
      return PlatHelperImpl.getModName(var0);
   }

   public static Path getModIcon(String var0) {
      return PlatHelperImpl.getModIcon(var0);
   }

   public static Path findModResource(String var0, String var1) {
      return PlatHelperImpl.findModResource(var0, var1);
   }

   public static List<String> getModAuthors(String var0) {
      return PlatHelperImpl.getModAuthors(var0);
   }

   public static String getModLicense(String var0) {
      return PlatHelperImpl.getModLicense(var0);
   }

   public static <T> Field findField(Class<? super T> var0, String var1) {
      return PlatHelperImpl.findField(var0, var1);
   }

   public static Method findMethod(Class<?> var0, String var1, Class<?>... var2) {
      return PlatHelperImpl.findMethod(var0, var1, var2);
   }

   public static MinecraftServer getCurrentServer() {
      return PlatHelperImpl.getCurrentServer();
   }

   public static boolean isModLoaded(String var0) {
      return PlatHelperImpl.isModLoaded(var0);
   }

   public static String getModVersion(String var0) {
      return PlatHelperImpl.getModVersion(var0);
   }

   public static List<String> getInstalledMods() {
      return PlatHelperImpl.getInstalledMods();
   }

   public static boolean isMobGriefingOn(Level var0, Entity var1) {
      return PlatHelperImpl.isMobGriefingOn(var0, var1);
   }

   public static boolean isAreaLoaded(LevelReader var0, BlockPos var1, int var2) {
      return PlatHelperImpl.isAreaLoaded(var0, var1, var2);
   }

   public static FoodProperties getFoodProperties(ItemStack var0, Player var1) {
      return PlatHelperImpl.getFoodProperties(var0, var1);
   }

   public static int getBurnTime(ItemStack var0) {
      return PlatHelperImpl.getBurnTime(var0);
   }

   public static boolean canLightFire(ItemStack var0) {
      return PlatHelperImpl.canLightFire(var0);
   }

   public static boolean isFireSource(BlockState var0, Level var1, BlockPos var2, Direction var3) {
      return PlatHelperImpl.isFireSource(var0, var1, var2, var3);
   }

   public static int getFireSpreadSpeed(BlockState var0, BlockGetter var1, BlockPos var2, Direction var3) {
      return PlatHelperImpl.getFireSpreadSpeed(var0, var1, var2, var3);
   }

   public static int getFlammability(BlockState var0, BlockGetter var1, BlockPos var2, Direction var3) {
      return PlatHelperImpl.getFlammability(var0, var1, var2, var3);
   }

   public static boolean isFlammable(BlockState var0, BlockGetter var1, BlockPos var2, Direction var3) {
      return PlatHelperImpl.isFlammable(var0, var1, var2, var3);
   }

   public static void onCaughtFire(BlockState var0, Level var1, BlockPos var2, Direction var3, LivingEntity var4) {
      PlatHelperImpl.onCaughtFire(var0, var1, var2, var3, var4);
   }

   public static Packet<ClientGamePacketListener> getEntitySpawnPacket(Entity var0, ServerEntity var1) {
      return PlatHelperImpl.getEntitySpawnPacket(var0, var1);
   }

   public static SpawnEggItem newSpawnEgg(Supplier<? extends EntityType<? extends Mob>> var0, int var1, int var2, Properties var3) {
      return PlatHelperImpl.newSpawnEgg(var0, var1, var2, var3);
   }

   public static FlowerPotBlock newFlowerPot(
      Supplier<FlowerPotBlock> var0, Supplier<? extends Block> var1, net.minecraft.world.level.block.state.BlockBehaviour.Properties var2
   ) {
      return PlatHelperImpl.newFlowerPot(var0, var1, var2);
   }

   public static SimpleParticleType newSimpleParticle() {
      return PlatHelperImpl.newSimpleParticle();
   }

   public static <T extends ParticleOptions> ParticleType<T> newParticle(
      Function<ParticleType<T>, MapCodec<T>> var0, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> var1, boolean var2
   ) {
      return PlatHelperImpl.newParticle(var0, var1, var2);
   }

   public static <T extends BlockEntity> BlockEntityType<T> newBlockEntityType(PlatHelper.BlockEntitySupplier<T> var0, Block... var1) {
      return PlatHelperImpl.newBlockEntityType(var0, var1);
   }

   public static <E extends Entity> EntityType<E> newEntityType(
      String var0, EntityFactory<E> var1, MobCategory var2, float var3, float var4, int var5, boolean var6, int var7
   ) {
      return PlatHelperImpl.newEntityType(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void addServerReloadListener(Function<Provider, PreparableReloadListener> var0, ResourceLocation var1) {
      PlatHelperImpl.addServerReloadListener(var0, var1);
   }

   public static void openCustomMenu(ServerPlayer var0, MenuProvider var1, Consumer<RegistryFriendlyByteBuf> var2) {
      PlatHelperImpl.openCustomMenu(var0, var1, var2);
   }

   public static Player getFakeServerPlayer(GameProfile var0, ServerLevel var1) {
      return PlatHelperImpl.getFakeServerPlayer(var0, var1);
   }

   @FunctionalInterface
   public interface BlockEntitySupplier<T extends BlockEntity> {
      @NotNull
      T create(BlockPos var1, BlockState var2);
   }

   public static enum Platform {
      FORGE,
      FABRIC;

      private static boolean quilt = false;

      public boolean isForge() {
         return this == FORGE;
      }

      public boolean isFabric() {
         return this == FABRIC;
      }

      public boolean isQuilt() {
         return this.isFabric() && quilt;
      }

      public void ifForge(Runnable runnable) {
         if (this.isForge()) {
            runnable.run();
         }
      }

      public void ifFabric(Runnable runnable) {
         if (this.isFabric()) {
            runnable.run();
         }
      }

      static {
         try {
            Class.forName("org.quiltmc.loader.api.QuiltLoader");
            quilt = true;
         } catch (ClassNotFoundException var1) {
         }
      }
   }

   public static enum Side {
      CLIENT,
      SERVER;

      public boolean isClient() {
         return this == CLIENT;
      }

      public boolean isServer() {
         return this == SERVER;
      }

      public void ifClient(Runnable runnable) {
         if (this.isClient()) {
            runnable.run();
         }
      }

      public void ifServer(Runnable runnable) {
         if (this.isServer()) {
            runnable.run();
         }
      }
   }
}
