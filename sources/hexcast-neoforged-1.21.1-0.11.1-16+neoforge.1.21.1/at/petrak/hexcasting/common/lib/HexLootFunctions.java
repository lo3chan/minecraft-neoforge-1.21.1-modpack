package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.loot.AddPerWorldPatternToScrollFunc;
import at.petrak.hexcasting.common.loot.AmethystReducerFunc;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class HexLootFunctions {
   private static final Map<ResourceLocation, LootItemFunctionType> LOOT_FUNCS = new LinkedHashMap<>();
   public static final LootItemFunctionType<AddPerWorldPatternToScrollFunc> PATTERN_SCROLL = register(
      "pattern_scroll", new LootItemFunctionType(AddPerWorldPatternToScrollFunc.CODEC)
   );
   public static final LootItemFunctionType<AmethystReducerFunc> AMETHYST_SHARD_REDUCER = register(
      "amethyst_shard_reducer", new LootItemFunctionType(AmethystReducerFunc.CODEC)
   );

   public static void registerSerializers(BiConsumer<LootItemFunctionType, ResourceLocation> r) {
      for (Entry<ResourceLocation, LootItemFunctionType> e : LOOT_FUNCS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static LootItemFunctionType register(String id, LootItemFunctionType lift) {
      LootItemFunctionType old = LOOT_FUNCS.put(HexAPI.modLoc(id), lift);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         return lift;
      }
   }
}
