package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.condition.SimpleConditions;
import com.iafenvoy.origins.data.condition.builtin.item.AmountCondition;
import com.iafenvoy.origins.data.condition.builtin.item.ArmorValueCondition;
import com.iafenvoy.origins.data.condition.builtin.item.BaseEnchantmentCondition;
import com.iafenvoy.origins.data.condition.builtin.item.ComponentCondition;
import com.iafenvoy.origins.data.condition.builtin.item.DurabilityCondition;
import com.iafenvoy.origins.data.condition.builtin.item.FuelCondition;
import com.iafenvoy.origins.data.condition.builtin.item.HasComponentCondition;
import com.iafenvoy.origins.data.condition.builtin.item.HasPowerCondition;
import com.iafenvoy.origins.data.condition.builtin.item.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.item.IngredientCondition;
import com.iafenvoy.origins.data.condition.builtin.item.IsEquipableCondition;
import com.iafenvoy.origins.data.condition.builtin.item.OnCooldownCondition;
import com.iafenvoy.origins.data.condition.builtin.item.PowerCountCondition;
import com.iafenvoy.origins.data.condition.builtin.item.RelativeDurabilityCondition;
import com.iafenvoy.origins.data.condition.builtin.item.ToolAbilityCondition;
import com.iafenvoy.origins.data.condition.builtin.item.meta.AndCondition;
import com.iafenvoy.origins.data.condition.builtin.item.meta.ChanceCondition;
import com.iafenvoy.origins.data.condition.builtin.item.meta.ConstantCondition;
import com.iafenvoy.origins.data.condition.builtin.item.meta.NotCondition;
import com.iafenvoy.origins.data.condition.builtin.item.meta.OrCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemConditions {
   public static final DeferredRegister<MapCodec<? extends ItemCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.ITEM_CONDITION, "origins");
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(
      "always_true", () -> AlwaysTrueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<AmountCondition>> AMOUNT = REGISTRY.register(
      "amount", () -> AmountCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ArmorValueCondition>> ARMOR_VALUE = REGISTRY.register(
      "armor_value", () -> ArmorValueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<BaseEnchantmentCondition>> BASE_ENCHANTMENT = REGISTRY.register(
      "base_enchantment", () -> BaseEnchantmentCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ComponentCondition>> COMPONENT = REGISTRY.register(
      "component", () -> ComponentCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<DurabilityCondition>> DURABILITY = REGISTRY.register(
      "durability", () -> DurabilityCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<? extends ItemCondition>> EMPTY = REGISTRY.register(
      "empty", () -> SimpleConditions.createItem((level, stack) -> stack.isEmpty())
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<? extends ItemCondition>> ENCHANTABLE = REGISTRY.register(
      "enchantable", () -> SimpleConditions.createItem((level, stack) -> stack.isEnchantable())
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<? extends ItemCondition>> FOOD = REGISTRY.register(
      "food", () -> SimpleConditions.createItem((level, stack) -> stack.getFoodProperties(null) != null)
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<FuelCondition>> FUEL = REGISTRY.register("fuel", () -> FuelCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<HasComponentCondition>> HAS_COMPONENT = REGISTRY.register(
      "has_component", () -> HasComponentCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<HasPowerCondition>> HAS_POWER = REGISTRY.register(
      "has_power", () -> HasPowerCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<IngredientCondition>> INGREDIENT = REGISTRY.register(
      "ingredient", () -> IngredientCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register(
      "in_tag", () -> InTagCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<OnCooldownCondition>> ON_COOLDOWN = REGISTRY.register(
      "on_cooldown", () -> OnCooldownCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<? extends ItemCondition>> IS_DAMAGEABLE = REGISTRY.register(
      "is_damageable", () -> SimpleConditions.createItem((level, stack) -> stack.isDamageableItem())
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<IsEquipableCondition>> IS_EQUIPABLE = REGISTRY.register(
      "is_equipable", () -> IsEquipableCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<PowerCountCondition>> POWER_COUNT = REGISTRY.register(
      "power_count", () -> PowerCountCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<RelativeDurabilityCondition>> RELATIVE_DURABILITY = REGISTRY.register(
      "relative_durability", () -> RelativeDurabilityCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<? extends ItemCondition>> SMELTABLE = REGISTRY.register(
      "smeltable",
      () -> SimpleConditions.createItem(
         (level, stack) -> level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).isPresent()
      )
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ToolAbilityCondition>> TOOL_ABILITY = REGISTRY.register(
      "tool_ability", () -> ToolAbilityCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register(
      "chance", () -> ChanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register(
      "constant", () -> ConstantCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
