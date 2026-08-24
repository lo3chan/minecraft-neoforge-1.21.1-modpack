package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool.Builder;

@FunctionalInterface
public interface LootTableLoadCallback {
   EventInvoker<LootTableLoadCallback> EVENT = EventInvoker.lookup(LootTableLoadCallback.class);

   void onLootTableLoad(ResourceLocation var1, net.minecraft.world.level.storage.loot.LootTable.Builder var2, Provider var3);

   static void forEachPool(net.minecraft.world.level.storage.loot.LootTable.Builder lootTable, Consumer<? super Builder> lootPoolConsumer) {
      Objects.requireNonNull(lootTable, "loot table is null");
      Objects.requireNonNull(lootPoolConsumer, "loot pool consumer is null");
      ProxyImpl.get().forEachPool(lootTable, lootPoolConsumer);
   }
}
