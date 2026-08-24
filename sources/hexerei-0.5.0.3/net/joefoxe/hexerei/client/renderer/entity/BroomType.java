package net.joefoxe.hexerei.client.renderer.entity;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.item.Item;

public record BroomType(String name, Item item, float speedMultiplier) {
   private static final Set<BroomType> VALUES = new HashSet<>();

   public static BroomType create(String name, Item item, float speedMultiplier) {
      BroomType broomType = new BroomType(name, item, speedMultiplier);
      VALUES.add(broomType);
      return broomType;
   }

   public static Set<BroomType> getValues() {
      return VALUES;
   }

   public static BroomType byName(String name) {
      for (BroomType type : VALUES) {
         if (type.name.equalsIgnoreCase(name)) {
            return type;
         }
      }

      return VALUES.stream().toList().get(0);
   }

   public static BroomType byId(int id) {
      return id >= 0 && id < VALUES.size() ? VALUES.stream().toList().get(id) : VALUES.stream().toList().get(0);
   }
}
