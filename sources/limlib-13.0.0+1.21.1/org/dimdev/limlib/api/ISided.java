package org.dimdev.limlib.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ISided<T extends ISided<T>> extends IRegister, ICreativeTabHandler, INetworking {
   default T self() {
      return (T)this;
   }

   void onServerStarting(Consumer<MinecraftServer> var1);

   void onServerStarted(Consumer<MinecraftServer> var1);

   MinecraftServer getServer();

   void onPlayerQuit(Consumer<ServerPlayer> var1);

   void onServerLevelTick(Consumer<ServerLevel> var1);

   void onAttackBlock(ISided.AttackBlockCallback var1);

   void onUseItem(ISided.UseItemCallback var1);

   void onUseBlock(ISided.UseBlockCallback var1);

   void onBeforeBlockBreak(ISided.BlockBreakCallback var1);

   void onBeforeBlockPlace(ISided.BlockPlaceCallback var1);

   void registerEntityAttributes(EntityType<? extends LivingEntity> var1, Supplier<Builder> var2);

   void addPack(PackType var1, String var2, String var3, boolean var4);

   Path getConfigRoot();

   default <C extends Config> C loadConfig(Class<C> configClass) {
      return Config.load(this, configClass);
   }

   default <C extends Config> C createConfig(Class<C> configClass) {
      return Config.createInstance(configClass);
   }

   void registerServerLoader(String var1, BiConsumer<Provider, ResourceManager> var2, boolean var3);

   default void registerServerLoader(String pocketLoader, BiConsumer<Provider, ResourceManager> consumer) {
      this.registerServerLoader(pocketLoader, consumer, false);
   }

   boolean isModLoaded(String var1);

   boolean isClient();

   long bucketAmount();

   void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> var1);

   <S> void createDynamicRegistry(ResourceKey<Registry<S>> var1, Codec<S> var2, Codec<S> var3);

   default <S> void createDynamicRegistry(ResourceKey<Registry<S>> key, Codec<S> codec, boolean synced) {
      this.createDynamicRegistry(key, codec, synced ? codec : null);
   }

   default <S> void createDynamicRegistry(ResourceKey<Registry<S>> key, Codec<S> codec) {
      this.createDynamicRegistry(key, codec, null);
   }

   void registryFlammable(Block var1, int var2, int var3);

   void registerStrippable(Block var1, Block var2);

   void registerFuel(ItemLike var1, int var2);

   Path configPath();

   @FunctionalInterface
   public interface AttackBlockCallback {
      InteractionResult attack(Player var1, InteractionHand var2, BlockPos var3, Direction var4);
   }

   @FunctionalInterface
   public interface BlockBreakCallback {
      boolean shouldCancel(Level var1, BlockPos var2, BlockState var3, Player var4);
   }

   @FunctionalInterface
   public interface BlockPlaceCallback {
      boolean shouldCancel(Level var1, BlockPos var2, BlockState var3, Entity var4);
   }

   @FunctionalInterface
   public interface UseBlockCallback {
      InteractionResult use(Player var1, InteractionHand var2, BlockHitResult var3);
   }

   @FunctionalInterface
   public interface UseItemCallback {
      InteractionResult use(Player var1, InteractionHand var2);
   }
}
