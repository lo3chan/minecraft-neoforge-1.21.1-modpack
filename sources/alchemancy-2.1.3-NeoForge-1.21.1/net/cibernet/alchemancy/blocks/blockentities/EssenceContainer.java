package net.cibernet.alchemancy.blocks.blockentities;

import java.util.ArrayList;
import net.cibernet.alchemancy.essence.Essence;
import net.cibernet.alchemancy.registries.AlchemancyEssence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EssenceContainer {
   Essence essence = null;
   int amount = 0;
   final int limit;

   public EssenceContainer(int limit) {
      this.limit = limit;
   }

   public EssenceContainer(EssenceContainer other) {
      this(other.essence, other.limit, other.amount);
   }

   public EssenceContainer(Essence essence, int limit, int amount) {
      this(limit);
      this.replace(essence, amount);
   }

   public boolean isEmpty() {
      return this.essence == null || this.amount <= 0;
   }

   public void clear() {
      this.essence = null;
      this.amount = 0;
   }

   public void replace(Essence essence, int amount) {
      this.essence = essence;
      this.amount = Mth.clamp(amount, 0, this.limit);
   }

   public boolean canAdd(EssenceContainer container, boolean exact) {
      return this.canAdd(container.essence, container.amount, exact);
   }

   public boolean canAdd(Essence essence, int amount, boolean exact) {
      return this.isEmpty() || essence == this.essence && (!exact || this.amount + amount <= this.limit);
   }

   public boolean add(Essence essence, int amount, boolean exact) {
      if (this.isEmpty()) {
         this.replace(essence, amount);
         return true;
      } else if (this.canAdd(essence, amount, exact)) {
         this.amount = Math.min(this.limit, this.amount + amount);
         return true;
      } else {
         return false;
      }
   }

   public boolean add(Essence essence, int amount) {
      return this.add(essence, amount, true);
   }

   public boolean canRemove(Essence essence, int amount, boolean exact) {
      return !this.isEmpty() && essence == this.essence && (!exact || this.amount - amount >= 0);
   }

   public boolean remove(Essence essence, int amount, boolean exact) {
      if (this.canRemove(essence, amount, exact)) {
         this.amount = Math.max(0, this.amount - amount);
         return true;
      } else {
         return false;
      }
   }

   public boolean remove(Essence essence, int amount) {
      return this.remove(essence, amount, true);
   }

   public int transferTo(EssenceContainer targetContainer, int amount, boolean exact) {
      return this.transferTo(targetContainer, amount, exact, true);
   }

   public int transferTo(EssenceContainer targetContainer, int amount, boolean exact, boolean depleteThis) {
      amount = Mth.clamp(amount, 0, exact ? this.amount : Math.min(this.amount, targetContainer.spaceLeft()));
      if (targetContainer.add(this.essence, amount, exact)) {
         if (depleteThis) {
            this.remove(this.essence, amount, exact);
         }

         return amount;
      } else {
         return 0;
      }
   }

   private int spaceLeft() {
      return this.limit - this.amount;
   }

   public Essence getEssence() {
      return this.essence;
   }

   public int getAmount() {
      return this.amount;
   }

   public int getLimit() {
      return this.limit;
   }

   public void loadFromTag(CompoundTag tag) {
      this.clear();
      if (tag.contains("id", 8) && tag.contains("amount", 3)) {
         this.replace(AlchemancyEssence.getEssence((ResourceLocation)ResourceLocation.read(tag.getString("id")).getOrThrow()), tag.getInt("amount"));
      }
   }

   public CompoundTag saveToTag(CompoundTag tag) {
      if (!this.isEmpty()) {
         tag.putString("id", this.essence.getKey().toString());
         tag.putInt("amount", this.amount);
      }

      return tag;
   }

   public boolean isFull() {
      return this.amount >= this.limit;
   }

   public static ArrayList<EssenceContainer> collapseDuplicates(ArrayList<EssenceContainer> list) {
      ArrayList<EssenceContainer> collapsedList = new ArrayList<>();

      for (EssenceContainer essenceContainer : list) {
         boolean inserted = false;

         for (EssenceContainer collapsedEssenceContainer : collapsedList) {
            if (collapsedEssenceContainer.add(essenceContainer.essence, essenceContainer.amount)) {
               inserted = true;
               break;
            }
         }

         if (!inserted) {
            collapsedList.add(new EssenceContainer(essenceContainer.essence, 2147483647, essenceContainer.amount));
         }
      }

      return collapsedList;
   }
}
