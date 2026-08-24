package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.core.FluidKJS;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public class RecipeViewerEntryType {
   public static final RecipeViewerEntryType ITEM = new RecipeViewerEntryType(
      "item",
      new RecipeViewerEntryType.Component(ItemWrapper.TYPE_INFO, ItemStack.STREAM_CODEC, ItemStack::isEmpty),
      new RecipeViewerEntryType.Component(ItemPredicate.TYPE_INFO, Ingredient.CONTENTS_STREAM_CODEC, Ingredient::isEmpty),
      new RecipeViewerEntryType.Component<>(ItemWrapper.ITEM_TYPE_INFO, ByteBufCodecs.registry(Registries.ITEM), i -> i == Items.AIR)
   ) {
      @Override
      public Object wrapEntry(Context cx, Object from) {
         return ItemWrapper.wrap(cx, from);
      }

      @Override
      public Object wrapPredicate(Context cx, Object from) {
         return ItemPredicate.wrap(cx, from);
      }

      @Override
      public Object getBase(Object from) {
         return ((ItemStack)from).getItem();
      }
   };
   public static final RecipeViewerEntryType FLUID = new RecipeViewerEntryType(
      "fluid",
      new RecipeViewerEntryType.Component(FluidWrapper.TYPE_INFO, FluidStack.STREAM_CODEC, FluidStack::isEmpty),
      new RecipeViewerEntryType.Component(FluidWrapper.INGREDIENT_TYPE_INFO, FluidIngredient.STREAM_CODEC, FluidIngredient::isEmpty),
      new RecipeViewerEntryType.Component<>(FluidWrapper.FLUID_TYPE_INFO, ByteBufCodecs.registry(Registries.FLUID), FluidKJS::kjs$isEmpty)
   ) {
      @Override
      public Object wrapEntry(Context cx, Object from) {
         return FluidWrapper.wrap(cx, from);
      }

      @Override
      public Object wrapPredicate(Context cx, Object from) {
         return FluidWrapper.wrapIngredient(cx, from);
      }

      @Override
      public Object getBase(Object from) {
         return ((FluidStack)from).getFluid();
      }
   };
   public static Lazy<Map<String, RecipeViewerEntryType>> CUSTOM_TYPES = Lazy.map(
      map -> KubeJSPlugins.forEachPlugin(t -> map.put(t.id, t), KubeJSPlugin::registerRecipeViewerEntryTypes)
   );
   public static final Lazy<List<RecipeViewerEntryType>> ALL_TYPES = Lazy.of(() -> {
      ArrayList<RecipeViewerEntryType> list = new ArrayList<>();
      list.add(ITEM);
      list.add(FLUID);
      list.addAll(CUSTOM_TYPES.get().values());
      return List.copyOf(list);
   });
   public final String id;
   public final RecipeViewerEntryType.Component<?> entryType;
   public final RecipeViewerEntryType.Component<?> predicateType;
   public final RecipeViewerEntryType.Component<?> baseClass;

   public static RecipeViewerEntryType fromString(@Nullable Object id) {
      return switch (id == null ? "" : id.toString()) {
         case null -> null;
         case "" -> null;
         case "item" -> ITEM;
         case "fluid" -> FLUID;
         default -> (RecipeViewerEntryType)CUSTOM_TYPES.get().get(String.valueOf(id));
      };
   }

   public RecipeViewerEntryType(
      String id,
      RecipeViewerEntryType.Component<?> entryType,
      RecipeViewerEntryType.Component<?> predicateType,
      @Nullable RecipeViewerEntryType.Component<?> baseClass
   ) {
      this.id = id;
      this.entryType = entryType;
      this.predicateType = predicateType;
      this.baseClass = baseClass;
   }

   public Object wrapEntry(Context cx, Object from) {
      return cx.jsToJava(from, this.entryType.type);
   }

   public Object wrapPredicate(Context cx, Object from) {
      return cx.jsToJava(from, this.predicateType.type);
   }

   public Object getBase(Object from) {
      return from;
   }

   public record Component<T>(TypeInfo type, StreamCodec<?, T> streamCodec, Predicate<T> empty) {
   }
}
