package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

@Deprecated
public final class LootTableLoadEvents {
   public static final EventInvoker<LootTableLoadEvents.Replace> REPLACE = EventInvoker.lookup(LootTableLoadEvents.Replace.class);
   public static final EventInvoker<LootTableLoadEvents.Modify> MODIFY = EventInvoker.lookup(LootTableLoadEvents.Modify.class);

   private LootTableLoadEvents() {
   }

   @FunctionalInterface
   public interface Modify {
      void onModifyLootTable(ResourceLocation var1, Consumer<LootPool> var2, IntPredicate var3);
   }

   @FunctionalInterface
   public interface Replace {
      void onReplaceLootTable(ResourceLocation var1, MutableValue<LootTable> var2);
   }
}
