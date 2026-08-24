package jeresources.api.drop;

import net.minecraft.world.item.ItemStack;

public class PlantDrop {
   private ItemStack drop;
   private int itemWeight;
   private int minDrop;
   private int maxDrop;
   private float chance;
   private PlantDrop.DropKind dropKind;

   public PlantDrop(ItemStack drop, int itemWeight) {
      this.drop = drop;
      this.itemWeight = itemWeight;
      this.dropKind = PlantDrop.DropKind.weight;
   }

   public PlantDrop(ItemStack drop, float chance) {
      this.drop = drop;
      this.chance = chance;
      this.dropKind = PlantDrop.DropKind.chance;
   }

   public PlantDrop(ItemStack drop, int minDrop, int maxDrop) {
      this.drop = drop;
      this.minDrop = minDrop;
      this.maxDrop = maxDrop;
      this.dropKind = PlantDrop.DropKind.minMax;
   }

   public ItemStack getDrop() {
      return this.drop;
   }

   public int getWeight() {
      return this.itemWeight;
   }

   public int getMinDrop() {
      return this.minDrop;
   }

   public int getMaxDrop() {
      return this.maxDrop;
   }

   public float getChance() {
      return this.chance;
   }

   public PlantDrop.DropKind getDropKind() {
      return this.dropKind;
   }

   public static enum DropKind {
      chance,
      weight,
      minMax;
   }
}
