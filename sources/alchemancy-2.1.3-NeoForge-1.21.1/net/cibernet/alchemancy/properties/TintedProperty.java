package net.cibernet.alchemancy.properties;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.AbstractCauldronAccessor;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public class TintedProperty extends Property implements IDataHolder<Integer[]>, ITintModifier {
   public static final int DEFAULT_COLOR = ARGB32.opaque(16777215);
   public static final Integer[] DEFAULT_COLORS = new Integer[0];
   private static final Int2ObjectOpenHashMap<DyeColor> TINT_TO_DYE_MAP = new Int2ObjectOpenHashMap(
      Arrays.stream(DyeColor.values()).collect(Collectors.toMap(DyeColor::getTextureDiffuseColor, dye -> (DyeColor)dye))
   );
   private static final int[] DYE_COLORS = Arrays.stream(DyeColor.values()).mapToInt(DyeColor::getTextureDiffuseColor).toArray();

   private static Component getColorName(int color) {
      DyeColor dyecolor = (DyeColor)TINT_TO_DYE_MAP.get(color);
      return dyecolor == null
         ? Component.translatable("property.detail.color", new Object[]{ColorUtils.colorToHexString(color)}).withColor(color)
         : Component.translatable("color.minecraft." + dyecolor.getName()).withColor(color);
   }

   @Override
   public boolean cluelessCanReset() {
      return false;
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      if (!stack.is(AlchemancyTags.Items.CANNOT_TINT) && !propertySource.is(AlchemancyTags.Items.APPLIES_CHROMA_TINT)) {
         Integer[] base = this.getData(stack);
         Integer[] colors = this.getDyeColor(propertySource);
         if (colors.length == 0) {
            return false;
         } else {
            if (consumeItem) {
               this.setData(stack, mixColors(base, colors));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public Integer[] getDyeColor(ItemStack stack) {
      if (stack.getItem() instanceof DyeItem dyeItem) {
         return new Integer[]{ARGB32.color(255, dyeItem.getDyeColor().getTextureDiffuseColor())};
      } else {
         return ((Integer[])this.getData(stack)).length > 0 ? this.getData(stack) : DEFAULT_COLORS;
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (InfusedPropertiesHelper.hasInfusedProperty(event.getItemStack(), this.asHolder())) {
         BlockState state = event.getLevel().getBlockState(event.getPos());
         if (state.getBlock() instanceof AbstractCauldronBlock cauldron
            && ((AbstractCauldronAccessor)cauldron).getInteractions().equals(CauldronInteraction.WATER)) {
            if (state.hasProperty(LayeredCauldronBlock.LEVEL)) {
               LayeredCauldronBlock.lowerFillLevel(state, event.getLevel(), event.getPos());
            }

            this.setData(event.getItemStack(), this.getDefaultData());
            InfusedPropertiesHelper.removeProperty(event.getItemStack(), this.asHolder());
            event.setCancellationResult(ItemInteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   @Override
   public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint) {
      boolean tintBase = stack.is(AlchemancyTags.Items.TINT_BASE_LAYER);
      boolean dontTintBase = stack.is(AlchemancyTags.Items.DONT_TINT_BASE_LAYER);
      return (!tintBase || !dontTintBase) && (!tintBase || tintIndex <= 0) && (!dontTintBase || tintIndex != 0)
         ? ARGB32.color(ARGB32.alpha(currentTint), this.getColor(stack))
         : currentTint;
   }

   public Integer[] readData(CompoundTag tag) {
      return tag.contains("colors", 11) ? this.toIntegerArray(tag.getIntArray("colors")) : new Integer[]{tag.getInt("color")};
   }

   public CompoundTag writeData(final Integer[] data) {
      return new CompoundTag() {
         {
            this.putIntArray("colors", Arrays.stream(data).mapToInt(Integer::valueOf).toArray());
         }
      };
   }

   public Integer[] combineData(@Nullable Integer[] currentData, Integer[] newData) {
      return mixColors(currentData, newData);
   }

   public void setData(ItemStack stack, int value) {
      IDataHolder.super.setData(stack, new Integer[]{value});
   }

   public Integer[] getDefaultData() {
      return DEFAULT_COLORS;
   }

   @Override
   public int getColor(ItemStack stack) {
      Integer[] colors = this.getData(stack);
      if (colors.length == 0 && InfusedPropertiesHelper.hasDormantProperty(stack, this.asHolder())) {
         return -1;
      } else {
         return colors.length == 0
            ? ColorUtils.interpolateColorsAndWait(1.0F, 1.0F, DYE_COLORS)
            : ColorUtils.interpolateColorsOverTime(1.0F, Arrays.stream(colors).mapToInt(Integer::valueOf).toArray());
      }
   }

   private Integer[] toIntegerArray(int... numbers) {
      return Arrays.stream(numbers).boxed().toArray(Integer[]::new);
   }

   @Override
   public Component getName(ItemStack stack) {
      int[] colors = Arrays.stream(this.getDyeColor(stack)).mapToInt(Integer::valueOf).toArray();
      return (Component)(colors.length == 0 ? super.getName(stack) : super.getName(stack).copy().withColor(ColorUtils.interpolateColorsOverTime(1.0F, colors)));
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      ArrayList<ItemStack> result = new ArrayList<>();

      for (DyeColor dye : DyeColor.values()) {
         ItemStack stack = capsuleItem.toStack();
         stack.set(AlchemancyItems.Components.STORED_PROPERTIES, new InfusedPropertiesComponent(List.of(holder)));
         this.setData(stack, dye.getTextureDiffuseColor());
         result.add(stack);
      }

      return result;
   }

   public static Integer[] mixColors(Integer[] base, Integer[] colors) {
      if (colors.length == 0) {
         return base;
      } else if (base.length == 0) {
         return colors;
      } else {
         for (int i = 0; i < base.length; i++) {
            base[i] = base[i] == DEFAULT_COLOR
               ? ARGB32.color(255, colors[Math.min(i, colors.length - 1)])
               : mixColors(base[i].intValue(), List.of(colors[Math.min(i, colors.length - 1)]));
         }

         return base;
      }
   }

   public static int mixColors(int base, List<Integer> dyes) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      int i1 = 0;
      int j1 = ARGB32.red(base);
      int k1 = ARGB32.green(base);
      int l1 = ARGB32.blue(base);
      l += Math.max(j1, Math.max(k1, l1));
      i += j1;
      j += k1;
      k += l1;
      i1++;

      for (int dyeitem : dyes) {
         l1 = ARGB32.red(dyeitem);
         int j2 = ARGB32.green(dyeitem);
         int k2 = ARGB32.blue(dyeitem);
         l += Math.max(l1, Math.max(j2, k2));
         i += l1;
         j += j2;
         k += k2;
         i1++;
      }

      j1 = i / i1;
      k1 = j / i1;
      l1 = k / i1;
      float f = (float)l / i1;
      float f1 = Math.max(j1, Math.max(k1, l1));
      j1 = (int)(j1 * f / f1);
      k1 = (int)(k1 * f / f1);
      l1 = (int)(l1 * f / f1);
      return ARGB32.color(0, j1, k1, l1);
   }
}
