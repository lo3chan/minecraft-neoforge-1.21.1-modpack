package net.diebuddies.util;

import net.minecraft.world.level.Level;

public class PlayerLevelPacked {
   public String e1;
   public Level e2;

   public PlayerLevelPacked(String e1, Level e2) {
      this.e1 = e1;
      this.e2 = e2;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.e1 == null ? 0 : this.e1.hashCode());
      return 31 * result + (this.e2 == null ? 0 : this.e2.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         PlayerLevelPacked other = (PlayerLevelPacked)obj;
         if (this.e1 == null) {
            if (other.e1 != null) {
               return false;
            }
         } else if (!this.e1.equals(other.e1)) {
            return false;
         }

         if (this.e2 == null) {
            if (other.e2 != null) {
               return false;
            }
         } else if (!this.e2.equals(other.e2)) {
            return false;
         }

         return true;
      }
   }
}
