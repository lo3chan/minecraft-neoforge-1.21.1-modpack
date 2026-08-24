package com.github.alexthe666.alexsmobs.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public final class AMLootModifiers {
   private static final LootItemCondition[] NO_CONDITIONS = new LootItemCondition[0];
   private static final Map<ResourceLocation, AMLootModifiers.Appender> BY_TABLE = byTable();

   private static Map<ResourceLocation, AMLootModifiers.Appender> byTable() {
      Map<ResourceLocation, AMLootModifiers.Appender> map = new LinkedHashMap<>(4);
      map.put(AMCompat.rl("minecraft", "blocks/jungle_leaves"), new BananaLootModifier(NO_CONDITIONS)::doApply);
      map.put(AMCompat.rl("minecraft", "blocks/acacia_leaves"), new BlossomLootModifier(NO_CONDITIONS)::doApply);
      map.put(AMCompat.rl("minecraft", "gameplay/piglin_bartering"), new PigshoesLootModifier(NO_CONDITIONS)::doApply);
      AncientDartLootModifier dart = new AncientDartLootModifier(NO_CONDITIONS);
      map.put(AMCompat.rl("minecraft", "chests/jungle_temple"), dart::doApply);
      map.put(AMCompat.rl("minecraft", "chests/jungle_temple_dispenser"), dart::doApply);
      return map;
   }

   private AMLootModifiers() {
   }

   public static AMLootModifiers.Appender resolve(LootTable table, LootContext context) {
      ServerLevel level = context.getLevel();
      if (level != null && level.getServer() != null) {
         for (Entry<ResourceLocation, AMLootModifiers.Appender> entry : BY_TABLE.entrySet()) {
            LootTable found = AMPlatform.lootTableById(level.getServer(), entry.getKey());
            if (found != null && found != LootTable.EMPTY && found == table) {
               return entry.getValue();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @FunctionalInterface
   public interface Appender {
      ObjectArrayList<ItemStack> append(ObjectArrayList<ItemStack> var1, LootContext var2);
   }
}
