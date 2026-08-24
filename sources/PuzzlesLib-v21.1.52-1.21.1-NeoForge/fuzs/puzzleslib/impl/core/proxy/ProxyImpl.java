package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

public interface ProxyImpl extends SidedProxy, FactoriesProxy, NetworkingProxy, EntityProxy {
   static ProxyImpl get() {
      return (ProxyImpl)Proxy.INSTANCE;
   }

   @MustBeInvokedByOverriders
   default void registerEventHandlers() {
      LoadCompleteCallback.EVENT.register(() -> {
         ModContext.forEach(ModContext::runAfterConstruction);
         EventInvokerImpl.initialize();
      });
   }

   MinecraftServer getMinecraftServer();

   <T> void openMenu(Player var1, MenuProvider var2, T var3);

   @Deprecated
   void openMenu(ServerPlayer var1, MenuProvider var2, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> var3);

   Metadata createPackInfo(ResourceLocation var1, Component var2, PackCompatibility var3, FeatureFlagSet var4, boolean var5);

   Style getRarityStyle(Rarity var1);

   void onPlayerDestroyItem(Player var1, ItemStack var2, @Nullable InteractionHand var3);

   void forEachPool(Builder var1, Consumer<? super net.minecraft.world.level.storage.loot.LootPool.Builder> var2);

   float getEnchantPowerBonus(BlockState var1, Level var2, BlockPos var3);

   boolean canApplyAtEnchantingTable(Holder<Enchantment> var1, ItemStack var2);

   void setTagBuilderReplace(TagBuilder var1, boolean var2);

   @Deprecated
   boolean onExplosionStart(Level var1, Explosion var2);
}
