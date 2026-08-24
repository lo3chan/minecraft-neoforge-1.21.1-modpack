package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.component.MutableDataComponentHolderFunctions;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.kubejs.web.RelativeURL;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public interface FluidStackKJS
   extends Replaceable,
   SpecialEquality,
   WithCodec,
   FluidLike,
   FluidMatch,
   MutableDataComponentHolderFunctions,
   RegistryObjectKJS<Fluid> {
   default FluidStack kjs$self() {
      return (FluidStack)this;
   }

   @Override
   default boolean specialEquals(Context cx, Object o, boolean shallow) {
      return switch (o) {
         case CharSequence cs -> this.kjs$getId().equals(ID.string(cs.toString()));
         case ResourceLocation id -> this.kjs$getIdLocation().equals(id);
         case FluidStack s -> this.kjs$equalsIgnoringCount(s);
         case null, default -> this.kjs$equalsIgnoringCount(FluidWrapper.wrap(cx, o));
      };
   }

   default boolean kjs$equalsIgnoringCount(FluidStack stack) {
      FluidStack self = this.kjs$self();
      if (self == stack) {
         return true;
      } else {
         return self.isEmpty() ? stack.isEmpty() : FluidStack.isSameFluidSameComponents(self, stack);
      }
   }

   @Override
   default ResourceKey<Registry<Fluid>> kjs$getRegistryId() {
      return Registries.FLUID;
   }

   @Override
   default Registry<Fluid> kjs$getRegistry() {
      return BuiltInRegistries.FLUID;
   }

   @Override
   default ResourceLocation kjs$getIdLocation() {
      return this.kjs$self().getFluid().kjs$getIdLocation();
   }

   @Override
   default Holder<Fluid> kjs$asHolder() {
      return this.kjs$self().getFluid().kjs$asHolder();
   }

   @Override
   default ResourceKey<Fluid> kjs$getKey() {
      return this.kjs$self().getFluid().kjs$getKey();
   }

   @Override
   default String kjs$getId() {
      return this.kjs$self().getFluid().kjs$getId();
   }

   @Override
   default String kjs$getMod() {
      return this.kjs$self().getFluid().kjs$getMod();
   }

   @Override
   default int kjs$getAmount() {
      return this.kjs$self().getAmount();
   }

   @Override
   default boolean kjs$isEmpty() {
      return this.kjs$self().isEmpty();
   }

   @Override
   default Fluid kjs$getFluid() {
      return this.kjs$self().getFluid();
   }

   @Override
   default FluidLike kjs$copy(int amount) {
      return (FluidLike)this.kjs$self().copyWithAmount(amount);
   }

   @Override
   default Codec<?> getCodec(Context cx) {
      return FluidStack.CODEC;
   }

   @Override
   default Object replaceThisWith(RecipeScriptContext cx, Object with) {
      FluidStack t = this.kjs$self();
      FluidStack r = FluidWrapper.wrap(cx.cx(), with);
      if (!FluidStack.isSameFluidSameComponents(t, r)) {
         r.setAmount(t.getAmount());
         return r;
      } else {
         return this;
      }
   }

   @Override
   default boolean matches(RecipeMatchContext cx, FluidStack s, boolean exact) {
      return this.kjs$self().getFluid() == s.getFluid();
   }

   @Override
   default boolean matches(RecipeMatchContext cx, FluidIngredient ingredient, boolean exact) {
      return ingredient.test(this.kjs$self());
   }

   default RelativeURL kjs$getWebIconURL(DynamicOps<Tag> ops, int size) {
      String url = "/img/" + size + "/fluid/" + ID.url(this.kjs$getIdLocation());
      String c = DataComponentWrapper.patchToString(new StringBuilder(), ops, DataComponentWrapper.visualPatch(this.kjs$self().getComponentsPatch()))
         .toString();
      return new RelativeURL(url, c.equals("[]") ? Map.of() : Map.of("components", c.substring(1, c.length() - 1)));
   }
}
