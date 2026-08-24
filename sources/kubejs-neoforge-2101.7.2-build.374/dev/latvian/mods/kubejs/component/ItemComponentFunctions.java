package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.item.FoodBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Unbreakable;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface ItemComponentFunctions extends ComponentFunctions, AttributeModifierFunctions {
   default void kjs$setMaxStackSize(int size) {
      this.kjs$override(DataComponents.MAX_STACK_SIZE, size);
   }

   default void kjs$setMaxDamage(int maxDamage) {
      this.kjs$override(DataComponents.MAX_DAMAGE, maxDamage);
   }

   default void kjs$setDamage(int damage) {
      this.kjs$override(DataComponents.DAMAGE, damage);
   }

   default void kjs$setUnbreakable() {
      this.kjs$override(DataComponents.UNBREAKABLE, new Unbreakable(false));
   }

   default void kjs$setUnbreakableWithTooltip() {
      this.kjs$override(DataComponents.UNBREAKABLE, new Unbreakable(true));
   }

   default void kjs$setItemName(Component component) {
      this.kjs$override(DataComponents.ITEM_NAME, component);
   }

   default void kjs$setRepairCost(int repairCost) {
      this.kjs$override(DataComponents.REPAIR_COST, repairCost);
   }

   default void kjs$setFood(FoodProperties foodProperties) {
      this.kjs$override(DataComponents.FOOD, foodProperties);
   }

   default void kjs$modifyFood(Consumer<FoodBuilder> foodBuilder) {
      FoodProperties food = this.kjs$get(DataComponents.FOOD);
      FoodBuilder builder = food == null ? new FoodBuilder() : new FoodBuilder(food);
      foodBuilder.accept(builder);
      this.kjs$setFood(builder.build());
   }

   default void kjs$setFood(int nutrition, float saturation) {
      this.kjs$modifyFood(builder -> builder.nutrition(nutrition).saturation(saturation));
   }

   default void kjs$setFireResistant() {
      this.kjs$setUnit(DataComponents.FIRE_RESISTANT);
   }

   default void kjs$setTool(Tool tool) {
      this.kjs$override(DataComponents.TOOL, tool);
   }

   default void kjs$setMapItemColor(KubeColor color) {
      this.kjs$override(DataComponents.MAP_COLOR, new MapItemColor(color.kjs$getRGB()));
   }

   default void kjs$setChargedProjectiles(List<ItemStack> items) {
      this.kjs$override(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(items));
   }

   default void kjs$setBundleContents(List<ItemStack> items) {
      this.kjs$override(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
   }

   default void kjs$setBucketEntityData(CompoundTag tag) {
      this.kjs$override(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
   }

   default void kjs$setBlockEntityData(CompoundTag tag) {
      this.kjs$override(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
   }

   default void kjs$setInstrument(Holder<Instrument> instrument) {
      this.kjs$override(DataComponents.INSTRUMENT, instrument);
   }

   default void kjs$setFireworkExplosion(FireworkExplosion explosion) {
      this.kjs$override(DataComponents.FIREWORK_EXPLOSION, explosion);
   }

   default void kjs$setFireworks(Fireworks fireworks) {
      this.kjs$override(DataComponents.FIREWORKS, fireworks);
   }

   default void kjs$setNoteBlockSound(ResourceLocation id) {
      this.kjs$override(DataComponents.NOTE_BLOCK_SOUND, id);
   }

   @Override
   default ItemAttributeModifiers kjs$getAttributeModifiers() {
      ItemAttributeModifiers mods = this.kjs$get(DataComponents.ATTRIBUTE_MODIFIERS);
      return mods == null ? new ItemAttributeModifiers(List.of(), true) : mods;
   }

   @Override
   default void kjs$setAttributeModifiers(ItemAttributeModifiers modifiers) {
      this.kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
   }
}
