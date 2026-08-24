package fuzs.puzzleslib.api.client.searchtree.v1;

import fuzs.puzzleslib.api.core.v1.Proxy;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.multiplayer.SessionSearchTrees.Key;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public final class SearchRegistryHelper {
   private static final Map<SearchTreeType<?>, SearchRegistryHelper.Entry<?>> SEARCH_TREES = new IdentityHashMap<>();

   private SearchRegistryHelper() {
   }

   public static <T> void register(SearchTreeType<T> type, Function<List<T>, SearchTree<T>> factory) {
      SEARCH_TREES.put(type, new SearchRegistryHelper.Entry<>(factory));
   }

   public static Stream<String> getTooltipLines(ItemStack itemStack) {
      return getTooltipLines(Stream.of(itemStack), TooltipFlag.NORMAL);
   }

   public static Stream<String> getTooltipLines(Stream<ItemStack> stream, TooltipFlag tooltipFlag) {
      Frozen registries = Proxy.INSTANCE.getClientPacketListener().registryAccess();
      return SessionSearchTrees.getTooltipLines(stream, TooltipContext.of(registries), tooltipFlag);
   }

   public static <T> void populateSearchTree(SearchTreeType<T> type, List<T> values) {
      SearchRegistryHelper.Entry<T> entry = lookupEntry(type);
      Proxy.INSTANCE.getClientPacketListener().searchTrees().register(entry.key, () -> {
         CompletableFuture<SearchTree<T>> searchTree = entry.searchTree;
         entry.searchTree = CompletableFuture.supplyAsync(() -> entry.factory.apply(values), Util.backgroundExecutor());
         searchTree.cancel(true);
      });
   }

   public static <T> SearchTree<T> getSearchTree(SearchTreeType<T> type) {
      return lookupEntry(type).searchTree.join();
   }

   private static <T> SearchRegistryHelper.Entry<T> lookupEntry(SearchTreeType<T> type) {
      SearchRegistryHelper.Entry<?> entry = SEARCH_TREES.get(type);
      Objects.requireNonNull(entry, () -> "Search tree type " + type.resourceLocation() + " is not registered");
      return (SearchRegistryHelper.Entry<T>)entry;
   }

   private static class Entry<T> {
      public final Key key = new Key();
      public final Function<List<T>, SearchTree<T>> factory;
      public CompletableFuture<SearchTree<T>> searchTree = CompletableFuture.completedFuture(SearchTree.empty());

      Entry(Function<List<T>, SearchTree<T>> factory) {
         this.factory = factory;
      }
   }
}
