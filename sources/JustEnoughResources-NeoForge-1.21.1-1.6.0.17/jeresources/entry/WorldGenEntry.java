package jeresources.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import jeresources.api.distributions.DistributionBase;
import jeresources.api.distributions.DistributionHelpers;
import jeresources.api.drop.LootDrop;
import jeresources.api.restrictions.Restriction;
import jeresources.util.MapKeys;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WorldGenEntry {
   private float[] chances;
   private boolean silktouch;
   private ItemStack block;
   private ItemStack deepSlateBlock;
   private int minY;
   private int maxY;
   private int colour;
   private Restriction restriction;
   private DistributionBase distribution;
   private Map<String, Set<LootDrop>> drops;
   private Map<Item, Set<LootDrop>> wildcardDrops;
   private Map<String, ItemStack> dropsDisplay;

   public WorldGenEntry(ItemStack block, ItemStack deepSlateBlock, DistributionBase distribution, Restriction restriction, boolean silktouch, LootDrop... drops) {
      this.block = block;
      this.deepSlateBlock = deepSlateBlock;
      this.distribution = distribution;
      this.restriction = restriction;
      this.colour = -16777216;
      this.silktouch = silktouch;
      this.drops = new HashMap<>();
      this.wildcardDrops = new HashMap<>();
      this.dropsDisplay = new HashMap<>();
      this.addDrops(drops);
      this.calcChances();
   }

   public WorldGenEntry(ItemStack block, DistributionBase distribution, Restriction restriction, boolean silktouch, LootDrop... drops) {
      this(block, null, distribution, restriction, silktouch, drops);
   }

   public WorldGenEntry(ItemStack block, DistributionBase distribution, LootDrop... drops) {
      this(block, distribution, Restriction.OVERWORLD, false, drops);
   }

   public WorldGenEntry(ItemStack block, ItemStack deepSlateBlock, DistributionBase distribution, LootDrop... drops) {
      this(block, deepSlateBlock, distribution, Restriction.OVERWORLD, false, drops);
   }

   public WorldGenEntry(ItemStack block, DistributionBase distribution, boolean silktouch, LootDrop... drops) {
      this(block, distribution, Restriction.OVERWORLD, silktouch, drops);
   }

   public WorldGenEntry(ItemStack block, ItemStack deepSlateBlock, DistributionBase distribution, boolean silktouch, LootDrop... drops) {
      this(block, deepSlateBlock, distribution, Restriction.OVERWORLD, silktouch, drops);
   }

   public WorldGenEntry(ItemStack block, DistributionBase distribution, Restriction restriction, LootDrop... drops) {
      this(block, distribution, restriction, false, drops);
   }

   public WorldGenEntry(ItemStack block, ItemStack deepSlateBlock, DistributionBase distribution, Restriction restriction, LootDrop... drops) {
      this(block, deepSlateBlock, distribution, restriction, false, drops);
   }

   public void addDrops(LootDrop... drops) {
      for (LootDrop drop : drops) {
         String mapKey = MapKeys.getKey(drop.item);
         if (mapKey != null) {
            Set<LootDrop> dropSet = this.drops.get(mapKey);
            if (dropSet == null) {
               dropSet = new TreeSet<>();
            }

            dropSet.add(drop);
            this.drops.put(mapKey, dropSet);
            if (!this.dropsDisplay.containsKey(mapKey)) {
               ItemStack itemStack = drop.item.copy();
               itemStack.setCount(Math.max(1, drop.minDrop));
               this.dropsDisplay.put(mapKey, itemStack);
            }
         }
      }
   }

   public void addDrops(Collection<LootDrop> drops) {
      this.addDrops(drops.toArray(new LootDrop[drops.size()]));
   }

   private void calcChances() {
      this.chances = new float[320];
      this.minY = 320;
      this.maxY = 0;
      int i = -1;

      for (float chance : this.distribution.getDistribution()) {
         if (++i == this.chances.length) {
            break;
         }

         this.chances[i] = this.chances[i] + chance;
         if (this.chances[i] > 0.0F) {
            if (this.minY > i) {
               this.minY = i;
            }

            if (i > this.maxY) {
               this.maxY = i;
            }
         }
      }

      if (this.minY == 320) {
         this.minY = 0;
      }

      if (this.maxY == 0) {
         this.maxY = 319;
      }

      if (this.minY < 160) {
         this.minY = 0;
      } else {
         this.minY = 160;
      }

      if (this.maxY <= 159) {
         this.maxY = 159;
      } else {
         this.maxY = 287;
      }

      this.minY -= 64;
   }

   public float[] getChances() {
      return Arrays.copyOfRange(this.chances, this.minY + 64, this.maxY + 1 + 64);
   }

   public int getMinY() {
      return this.minY;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public boolean isSilkTouchNeeded() {
      return this.silktouch;
   }

   public int getColour() {
      return this.colour;
   }

   public List<ItemStack> getDrops() {
      return new ArrayList<>(this.dropsDisplay.values());
   }

   public List<ItemStack> getBlockAndDrops() {
      List<ItemStack> list = new LinkedList<>();
      list.add(this.block);
      list.addAll(this.getDrops());
      return list;
   }

   public ItemStack getBlock() {
      return this.block;
   }

   public ItemStack getDeepSlateBlock() {
      return this.deepSlateBlock;
   }

   public List<String> getBiomeRestrictions() {
      return this.restriction.getBiomeRestrictions();
   }

   public String getDimension() {
      return this.restriction.getDimensionRestriction();
   }

   public List<LootDrop> getLootDrops(ItemStack itemStack) {
      String key = MapKeys.getKey(itemStack);
      List<LootDrop> list = new ArrayList<>(this.drops.containsKey(key) ? this.drops.get(key) : this.wildcardDrops.get(itemStack.getItem()));
      Collections.reverse(list);
      return list;
   }

   public float getAverageBlockCountPerChunk() {
      float sum = 0.0F;

      for (float chance : this.chances) {
         sum += chance;
      }

      return Math.round(sum * this.chances.length * 100.0F) / 100.0F;
   }

   @Override
   public String toString() {
      return "WorldGenEntry: " + this.block.getDisplayName() + " - " + this.restriction.toString();
   }

   public Restriction getRestriction() {
      return this.restriction;
   }

   public void merge(WorldGenEntry entry) {
      entry.drops.values().forEach(this::addDrops);
      this.distribution = DistributionHelpers.addDistribution(this.distribution, entry.distribution);
      this.calcChances();
   }

   public boolean hasDeepSlateVariant() {
      return this.deepSlateBlock != null && !this.deepSlateBlock.isEmpty();
   }

   public List<ItemStack> getBlocks() {
      List<ItemStack> blocks = new LinkedList<>();
      blocks.add(this.getBlock());
      if (this.hasDeepSlateVariant()) {
         blocks.add(this.getDeepSlateBlock());
      }

      return blocks;
   }
}
