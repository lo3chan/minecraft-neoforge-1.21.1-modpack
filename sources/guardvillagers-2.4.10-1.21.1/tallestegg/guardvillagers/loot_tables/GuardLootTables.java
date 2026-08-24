package tallestegg.guardvillagers.loot_tables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tallestegg.guardvillagers.loot_tables.functions.ArmorSlotFunction;

public class GuardLootTables {
   public static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY = HashBiMap.create();
   public static final LootContextParamSet SLOT = register("slot", table -> table.required(LootContextParams.THIS_ENTITY));
   public static final DeferredRegister<LootItemFunctionType<?>> LOOT_ITEM_FUNCTION_TYPES = DeferredRegister.create(
      Registries.LOOT_FUNCTION_TYPE, "guardvillagers"
   );
   public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister.create(
      Registries.LOOT_CONDITION_TYPE, "guardvillagers"
   );
   public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<ArmorSlotFunction>> ARMOR_SLOT = LOOT_ITEM_FUNCTION_TYPES.register(
      "slot", () -> new LootItemFunctionType(ArmorSlotFunction.CODEC)
   );

   public static LootContextParamSet register(String p_81429_, Consumer<Builder> p_81430_) {
      Builder lootcontextparamset$builder = new Builder();
      p_81430_.accept(lootcontextparamset$builder);
      LootContextParamSet lootcontextparamset = lootcontextparamset$builder.build();
      ResourceLocation resourcelocation = ResourceLocation.parse("guardvillagers" + p_81429_);
      LootContextParamSet lootcontextparamset1 = (LootContextParamSet)REGISTRY.put(resourcelocation, lootcontextparamset);
      if (lootcontextparamset1 != null) {
         throw new IllegalStateException("Loot table parameter set " + resourcelocation + " is already registered");
      } else {
         return lootcontextparamset;
      }
   }
}
