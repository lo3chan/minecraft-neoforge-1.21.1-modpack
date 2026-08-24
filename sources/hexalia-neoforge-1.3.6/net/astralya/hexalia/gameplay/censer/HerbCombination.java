package net.astralya.hexalia.gameplay.censer;

import net.minecraft.world.item.Item;

public record HerbCombination(Item first, Item second) {
   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof HerbCombination other)
            ? false
            : this.first.equals(other.first) && this.second.equals(other.second) || this.first.equals(other.second) && this.second.equals(other.first);
      }
   }

   @Override
   public int hashCode() {
      return this.first.hashCode() + this.second.hashCode();
   }
}
