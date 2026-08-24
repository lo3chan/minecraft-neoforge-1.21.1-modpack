package dev.latvian.mods.kubejs.ingredient;

import java.util.function.Supplier;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public interface KubeJSIngredients {
   DeferredRegister<IngredientType<?>> REGISTRY = DeferredRegister.create(Keys.INGREDIENT_TYPES, "kubejs");
   Supplier<IngredientType<WildcardIngredient>> WILDCARD = REGISTRY.register(
      "wildcard", () -> new IngredientType(WildcardIngredient.CODEC, WildcardIngredient.STREAM_CODEC)
   );
   Supplier<IngredientType<NamespaceIngredient>> NAMESPACE = REGISTRY.register(
      "namespace", () -> new IngredientType(NamespaceIngredient.CODEC, NamespaceIngredient.STREAM_CODEC)
   );
   Supplier<IngredientType<RegExIngredient>> REGEX = REGISTRY.register("regex", () -> new IngredientType(RegExIngredient.CODEC, RegExIngredient.STREAM_CODEC));
   Supplier<IngredientType<CreativeTabIngredient>> CREATIVE_TAB = REGISTRY.register(
      "creative_tab", () -> new IngredientType(CreativeTabIngredient.CODEC, CreativeTabIngredient.STREAM_CODEC)
   );
}
