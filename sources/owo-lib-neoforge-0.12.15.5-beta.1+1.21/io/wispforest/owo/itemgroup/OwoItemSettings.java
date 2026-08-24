package io.wispforest.owo.itemgroup;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;

@Deprecated(
   forRemoval = true
)
public class OwoItemSettings extends Properties {
   public OwoItemSettings group(ItemGroupReference ref) {
      return (OwoItemSettings)super.group(ref);
   }

   @Deprecated
   public OwoItemSettings group(OwoItemGroup group) {
      return (OwoItemSettings)super.group(group);
   }

   @Deprecated
   public OwoItemGroup group() {
      return this.groupSupplier().get();
   }

   public OwoItemSettings group(Supplier<OwoItemGroup> groupSupplier) {
      return (OwoItemSettings)super.group(groupSupplier);
   }

   public Supplier<OwoItemGroup> groupSupplier() {
      return super.groupSupplier();
   }

   public OwoItemSettings tab(int tab) {
      return (OwoItemSettings)super.tab(tab);
   }

   public int tab() {
      return super.tab();
   }

   public OwoItemSettings stackGenerator(BiConsumer<Item, Output> generator) {
      return (OwoItemSettings)super.stackGenerator(generator);
   }

   public BiConsumer<Item, Output> stackGenerator() {
      return super.stackGenerator();
   }

   public OwoItemSettings trackUsageStat() {
      return (OwoItemSettings)super.trackUsageStat();
   }

   public boolean shouldTrackUsageStat() {
      return super.shouldTrackUsageStat();
   }

   public OwoItemSettings maxCount(int maxCount) {
      return (OwoItemSettings)super.stacksTo(maxCount);
   }

   public OwoItemSettings maxDamage(int maxDamage) {
      return (OwoItemSettings)super.durability(maxDamage);
   }

   public OwoItemSettings recipeRemainder(Item recipeRemainder) {
      return (OwoItemSettings)super.craftRemainder(recipeRemainder);
   }

   public OwoItemSettings rarity(Rarity rarity) {
      return (OwoItemSettings)super.rarity(rarity);
   }

   public OwoItemSettings fireproof() {
      return (OwoItemSettings)super.fireResistant();
   }
}
