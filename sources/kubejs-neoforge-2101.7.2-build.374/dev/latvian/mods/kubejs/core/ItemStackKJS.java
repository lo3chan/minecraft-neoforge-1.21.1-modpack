package dev.latvian.mods.kubejs.core;

import com.google.errorprone.annotations.DoNotCall;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.component.ItemComponentFunctions;
import dev.latvian.mods.kubejs.component.MutableDataComponentHolderFunctions;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.kubejs.web.RelativeURL;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.rhino.util.ToStringJS;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemStackKJS
   extends SpecialEquality,
   WithCodec,
   IngredientSupplierKJS,
   ToStringJS,
   Replaceable,
   ItemComponentFunctions,
   MutableDataComponentHolderFunctions,
   ItemMatch,
   RegistryObjectKJS<Item> {
   default ItemStack kjs$self() {
      return (ItemStack)this;
   }

   @Override
   default boolean specialEquals(Context cx, Object o, boolean shallow) {
      return switch (o) {
         case CharSequence cs -> this.kjs$getId().equals(ID.string(cs.toString()));
         case ResourceLocation id -> this.kjs$getIdLocation().equals(id);
         case ItemStack s -> this.kjs$equalsIgnoringCount(s);
         case null, default -> KubeJSCodecs.filter(ItemWrapper.wrapResult(cx, o), this::kjs$equalsIgnoringCount);
      };
   }

   default boolean kjs$equalsIgnoringCount(ItemStack stack) {
      ItemStack self = this.kjs$self();
      if (self == stack) {
         return true;
      } else {
         return self.isEmpty() ? stack.isEmpty() : ItemStack.isSameItemSameComponents(self, stack);
      }
   }

   @Override
   default ResourceKey<Registry<Item>> kjs$getRegistryId() {
      return Registries.ITEM;
   }

   @Override
   default Registry<Item> kjs$getRegistry() {
      return BuiltInRegistries.ITEM;
   }

   @Override
   default ResourceLocation kjs$getIdLocation() {
      return this.kjs$self().getItem().kjs$getIdLocation();
   }

   @Override
   default Holder<Item> kjs$asHolder() {
      return this.kjs$self().getItem().kjs$asHolder();
   }

   @Override
   default ResourceKey<Item> kjs$getKey() {
      return this.kjs$self().getItem().kjs$getKey();
   }

   @Override
   default String kjs$getId() {
      return this.kjs$self().getItem().kjs$getId();
   }

   @Override
   default String kjs$getMod() {
      return this.kjs$self().getItem().kjs$getMod();
   }

   @Nullable
   default Block kjs$getBlock() {
      return this.kjs$self().getItem() instanceof BlockItem bi ? bi.getBlock() : null;
   }

   @ReturnsSelf(
      copy = true
   )
   default ItemStack kjs$withCount(int c) {
      if (c > 0 && !this.kjs$self().isEmpty()) {
         ItemStack is = this.kjs$self().copy();
         is.setCount(c);
         return is;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   default String kjs$getComponentString(Context cx) {
      return DataComponentWrapper.patchToString(new StringBuilder(), RegistryAccessContainer.of(cx).nbt(), this.kjs$self().getComponentsPatch()).toString();
   }

   @ReturnsSelf(
      copy = true
   )
   default ItemStack kjs$withCustomName(@Nullable Component name) {
      ItemStack is = this.kjs$self().copy();
      is.kjs$setCustomName(name);
      return is;
   }

   default ItemEnchantments kjs$getEnchantments() {
      return EnchantmentHelper.getEnchantmentsForCrafting(this.kjs$self());
   }

   default boolean kjs$hasEnchantment(Holder<Enchantment> enchantment, int level) {
      ItemEnchantments e = this.kjs$getEnchantments();
      return e != null && e.getLevel(enchantment) >= level;
   }

   @ReturnsSelf
   default ItemStack kjs$enchant(Holder<Enchantment> enchantment, int level) {
      ItemStack is = this.kjs$self();
      is.enchant(enchantment, level);
      return is;
   }

   @ReturnsSelf(
      copy = true
   )
   default ItemStack kjs$enchant(ItemEnchantments enchantments) {
      ItemStack is = this.kjs$self().copy();
      EnchantmentHelper.updateEnchantments(is, mutable -> {
         for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            mutable.upgrade((Holder)entry.getKey(), entry.getValue());
         }
      });
      return is;
   }

   default boolean kjs$areItemsEqual(ItemStack other) {
      return this.kjs$self().getItem() == other.getItem();
   }

   default boolean kjs$areComponentsEqual(ItemStack other) {
      return ItemStack.isSameItemSameComponents(this.kjs$self(), other);
   }

   default float kjs$getHarvestSpeed(@Nullable LevelBlock block) {
      return this.kjs$self().getDestroySpeed(block == null ? Blocks.AIR.defaultBlockState() : block.getBlockState());
   }

   default float kjs$getHarvestSpeed() {
      return this.kjs$getHarvestSpeed(null);
   }

   default Map<String, Object> kjs$getTypeData() {
      return this.kjs$self().getItem().kjs$getTypeData();
   }

   default String toStringJS(Context cx) {
      return this.kjs$toItemString0(RegistryAccessContainer.of(cx).nbt());
   }

   default String kjs$toItemString(Context cx) {
      return this.kjs$toItemString0(RegistryAccessContainer.of(cx).nbt());
   }

   @Deprecated
   @DoNotCall
   default ItemStack kjs$withChance(Context cx, float chance) {
      throw new KubeRuntimeException(
            ".withChance() is no longer supported on Minecraft 1.21! Please use the chance item implementation of the relevant mod addon (such as CreateItem.of(item, chance) for KubeJS Create) instead!"
         )
         .source(SourceLine.of(cx));
   }

   default String kjs$toItemString0(@Nullable DynamicOps<Tag> dynamicOps) {
      ItemStack is = this.kjs$self();
      int count = is.getCount();
      if (count <= 0) {
         return "minecraft:air";
      } else {
         StringBuilder builder = new StringBuilder();
         builder.append('\'');
         if (count > 1) {
            builder.append(count);
            builder.append("x ");
         }

         builder.append(this.kjs$getId());
         if (!is.isComponentsPatchEmpty()) {
            DataComponentWrapper.patchToString(builder, dynamicOps, is.getComponentsPatch());
         }

         builder.append('\'');
         return builder.toString();
      }
   }

   @Override
   default Ingredient kjs$asIngredient() {
      DataComponentPatch p = this.kjs$self().getComponentsPatch();
      if (p.isEmpty()) {
         return this.kjs$self().getItem().kjs$asIngredient();
      } else {
         Builder map = DataComponentMap.builder();

         for (Entry<DataComponentType<?>, Optional<?>> entry : p.entrySet()) {
            if (entry.getValue().isPresent()) {
               map.set(entry.getKey(), Cast.to(entry.getValue().get()));
            }
         }

         return IngredientWrapper.withData(HolderSet.direct(new Holder[]{this.kjs$asHolder()}), map.build());
      }
   }

   @Override
   default Codec<ItemStack> getCodec(Context cx) {
      return ItemStack.CODEC;
   }

   @ReturnsSelf(
      copy = true
   )
   default ItemStack kjs$withLore(Component[] lines) {
      ItemStack is = this.kjs$self().copy();
      is.set(DataComponents.LORE, new ItemLore(List.of(lines)));
      return is;
   }

   @ReturnsSelf(
      copy = true
   )
   default ItemStack kjs$withLore(Component[] lines, Component[] styledLines) {
      ItemStack is = this.kjs$self().copy();
      is.set(DataComponents.LORE, new ItemLore(List.of(lines), List.of(styledLines)));
      return is;
   }

   @Override
   default Object replaceThisWith(RecipeScriptContext cx, Object with) {
      ItemStack t = this.kjs$self();
      ItemStack r = ItemWrapper.wrap(cx.cx(), with);
      if (!ItemStack.isSameItemSameComponents(t, r)) {
         r.setCount(t.getCount());
         return r;
      } else {
         return this;
      }
   }

   @Override
   default boolean matches(RecipeMatchContext cx, ItemStack s, boolean exact) {
      return this.kjs$self().getItem() == s.getItem();
   }

   @Override
   default boolean matches(RecipeMatchContext cx, Ingredient in, boolean exact) {
      return in.test(this.kjs$self());
   }

   @Override
   default boolean matches(RecipeMatchContext cx, ItemLike itemLike, boolean exact) {
      return this.kjs$self().getItem() == itemLike.asItem();
   }

   default RelativeURL kjs$getWebIconURL(DynamicOps<Tag> ops, int size) {
      String url = "/img/" + size + "/item/" + ID.url(this.kjs$getIdLocation());
      String c = DataComponentWrapper.patchToString(new StringBuilder(), ops, DataComponentWrapper.visualPatch(this.kjs$self().getComponentsPatch()))
         .toString();
      return new RelativeURL(url, c.equals("[]") ? Map.of() : Map.of("components", c.substring(1, c.length() - 1)));
   }
}
