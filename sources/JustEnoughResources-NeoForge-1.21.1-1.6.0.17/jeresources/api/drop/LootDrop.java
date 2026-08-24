package jeresources.api.drop;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jeresources.api.conditionals.Conditional;
import jeresources.api.util.ItemHelper;
import jeresources.api.util.LootConditionHelper;
import jeresources.api.util.LootFunctionHelper;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public class LootDrop implements Comparable<LootDrop> {
   public int minDrop;
   public int maxDrop;
   public ItemStack item;
   public ItemStack smeltedItem;
   public float chance;
   private Set<Conditional> conditionals;
   public int fortuneLevel;
   public boolean enchanted;
   private float sortIndex;

   public LootDrop(ItemStack item) {
      this(item, item.getCount());
   }

   public LootDrop(ItemStack item, float chance) {
      this(item, chance, 0);
   }

   public LootDrop(ItemStack item, float chance, int fortuneLevel) {
      this(item, (int)Math.floor(chance), (int)Math.ceil(chance), chance, fortuneLevel);
   }

   public LootDrop(ItemStack item, int minDrop, int maxDrop, Conditional... conditionals) {
      this(item, minDrop, maxDrop, 1.0F, 0, conditionals);
   }

   public LootDrop(ItemStack item, int minDrop, int maxDrop, float chance, int fortuneLevel, Conditional... conditionals) {
      this.item = item;
      this.smeltedItem = null;
      this.minDrop = minDrop;
      this.maxDrop = maxDrop;
      this.chance = chance;
      this.sortIndex = Math.min(chance, 1.0F) * (minDrop + maxDrop);
      this.conditionals = new HashSet<>();
      Collections.addAll(this.conditionals, conditionals);
      this.fortuneLevel = fortuneLevel;
   }

   public LootDrop(Item item, int minDrop, int maxDrop, Conditional... conditionals) {
      this(new ItemStack(item), minDrop, maxDrop, 1.0F, 0, conditionals);
   }

   public LootDrop(Item item, DataComponentPatch dataComponentPatch, int minDrop, int maxDrop, Conditional... conditionals) {
      this(ItemHelper.itemStackWithDataComponents(item, 1, dataComponentPatch), minDrop, maxDrop, 1.0F, 0, conditionals);
   }

   public LootDrop(Item item, int minDrop, int maxDrop, float chance, Conditional... conditionals) {
      this(new ItemStack(item), minDrop, maxDrop, chance, 0, conditionals);
   }

   public LootDrop(Item item, DataComponentPatch dataComponentPatch, int minDrop, int maxDrop, float chance, Conditional... conditionals) {
      this(ItemHelper.itemStackWithDataComponents(item, 1, dataComponentPatch), minDrop, maxDrop, chance, 0, conditionals);
   }

   public LootDrop(ItemStack item, int minDrop, int maxDrop, float chance, Conditional... conditionals) {
      this(item, minDrop, maxDrop, chance, 0, conditionals);
   }

   public LootDrop(Item item, float chance, LootItemFunction... lootFunctions) {
      this(new ItemStack(item), chance);
      this.enchanted = false;
      this.addLootFunctions(lootFunctions);
   }

   public LootDrop(Item item, float chance, Collection<LootItemFunction> lootFunctions) {
      this(new ItemStack(item), chance);
      this.enchanted = false;
      this.addLootFunctions(lootFunctions);
   }

   public LootDrop(Item item, float chance, LootItemCondition[] lootConditions, LootItemFunction... lootFunctions) {
      this(item, chance, lootFunctions);
      this.addLootConditions(lootConditions);
   }

   public LootDrop(Item item, float chance, Collection<LootItemCondition> lootConditions, LootItemFunction... lootFunctions) {
      this(item, chance, lootFunctions);
      this.addLootConditions(lootConditions);
   }

   public LootDrop(Item item, float chance, Collection<LootItemCondition> lootConditions, Collection<LootItemFunction> lootFunctions) {
      this(item, chance, lootFunctions);
      this.addLootConditions(lootConditions);
   }

   public LootDrop addLootConditions(LootItemCondition[] lootConditions) {
      return this.addLootConditions(Arrays.asList(lootConditions));
   }

   public LootDrop addLootConditions(Collection<LootItemCondition> lootConditions) {
      lootConditions.forEach(this::addLootCondition);
      return this;
   }

   public LootDrop addLootCondition(LootItemCondition condition) {
      LootConditionHelper.applyCondition(condition, this);
      return this;
   }

   public LootDrop addLootFunctions(LootItemFunction[] lootFunctions) {
      return this.addLootFunctions(Arrays.asList(lootFunctions));
   }

   public LootDrop addLootFunctions(Collection<LootItemFunction> lootFunctions) {
      lootFunctions.forEach(this::addLootFunction);
      return this;
   }

   public LootDrop addLootFunction(LootItemFunction lootFunction) {
      LootFunctionHelper.applyFunction(lootFunction, this);
      return this;
   }

   public boolean canBeCooked() {
      return this.smeltedItem != null;
   }

   public List<ItemStack> getDrops() {
      List<ItemStack> list = new LinkedList<>();
      if (this.item != null) {
         list.add(this.item);
      }

      if (this.smeltedItem != null) {
         list.add(this.smeltedItem);
      }

      return list;
   }

   @Override
   public String toString() {
      return this.minDrop == this.maxDrop ? this.minDrop + this.getDropChance() : this.minDrop + "-" + this.maxDrop + this.getDropChance();
   }

   private String getDropChance() {
      return this.chance < 1.0F ? " (" + this.formatChance() + "%)" : "";
   }

   public String formatChance() {
      float chance = this.chance * 100.0F;
      return chance < 10.0F ? String.format("%.1f", chance) : String.format("%2d", (int)chance);
   }

   public boolean isAffectedBy(Conditional conditional) {
      return this.conditionals.contains(conditional);
   }

   public String chanceString() {
      return this.chance >= 0.995F ? String.format("%.2G", this.chance) : String.format("%.2G%%", this.chance * 100.0F);
   }

   public List<Component> getTooltipText() {
      return this.getTooltipText(false);
   }

   public List<Component> getTooltipText(boolean smelted) {
      List<Component> list = this.conditionals.stream().map(Conditional::toStringTextComponent).collect(Collectors.toList());
      if (smelted) {
         list.add(Conditional.burning.toStringTextComponent());
      }

      return list;
   }

   public void addConditional(Conditional conditional) {
      this.conditionals.add(conditional);
   }

   public void addConditionals(List<Conditional> conditionals) {
      this.conditionals.addAll(conditionals);
   }

   public float getSortIndex() {
      return this.sortIndex;
   }

   public Component toStringTextComponent() {
      return Component.literal(this.toString());
   }

   public int compareTo(@NotNull LootDrop o) {
      if (ItemStack.isSameItem(this.item, o.item)) {
         return Integer.compare(o.fortuneLevel, this.fortuneLevel);
      } else {
         int cmp = Float.compare(o.getSortIndex(), this.getSortIndex());
         return cmp != 0 ? cmp : this.item.getDisplayName().toString().compareTo(o.item.getDisplayName().toString());
      }
   }
}
