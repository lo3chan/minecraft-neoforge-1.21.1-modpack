package io.wispforest.owo.ops;

import io.wispforest.owo.mixin.SetComponentsLootFunctionAccessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class LootOps {
   private static final Map<ResourceLocation[], Supplier<Builder<?>>> ADDITIONS = new HashMap<>();

   private LootOps() {
   }

   public static void injectItem(ItemLike item, float chance, ResourceLocation... targetTables) {
      ADDITIONS.put(targetTables, () -> LootItem.lootTableItem(item).when(LootItemRandomChanceCondition.randomChance(chance)));
   }

   public static void injectItemWithCount(ItemLike item, float chance, int min, int max, ResourceLocation... targetTables) {
      ADDITIONS.put(
         targetTables,
         () -> ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(item)
               .when(LootItemRandomChanceCondition.randomChance(chance)))
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
      );
   }

   public static void injectItemStack(ItemStack stack, float chance, ResourceLocation... targetTables) {
      ADDITIONS.put(
         targetTables,
         () -> ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(stack.getItem())
               .when(LootItemRandomChanceCondition.randomChance(chance)))
            .apply(() -> SetComponentsLootFunctionAccessor.createSetComponentsLootFunction(List.of(), stack.getComponentsPatch()))
            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(stack.getCount())))
      );
   }

   public static boolean anyMatch(ResourceLocation target, ResourceLocation... predicates) {
      for (ResourceLocation predicate : predicates) {
         if (target.equals(predicate)) {
            return true;
         }
      }

      return false;
   }

   @Internal
   public static void registerListener() {
      NeoForge.EVENT_BUS.addListener(event -> ADDITIONS.forEach((identifiers, lootPoolEntrySupplier) -> {
         if (anyMatch(event.getName(), identifiers)) {
            event.getTable().addPool(LootPool.lootPool().add(lootPoolEntrySupplier.get()).build());
         }
      }));
   }
}
