package jeresources.reference;

import jeresources.jei.BackgroundDrawable;
import net.minecraft.resources.ResourceLocation;

public final class Resources {
   public static final class Gui {
      public static final class Jei {
         public static final BackgroundDrawable MOB = new BackgroundDrawable("textures/gui/mob.png", 163, 120);
         public static final BackgroundDrawable WORLD_GEN = new BackgroundDrawable("textures/gui/world_gen.png", 156, 80);
         public static final BackgroundDrawable DUNGEON = new BackgroundDrawable("textures/gui/dungeon.png", 163, 120);
         public static final BackgroundDrawable PLANT = new BackgroundDrawable("textures/gui/plant.png", 165, 120);
         public static final BackgroundDrawable ENCHANTMENT = new BackgroundDrawable("textures/gui/enchantment.png", 163, 120);
         public static final BackgroundDrawable VILLAGER = new BackgroundDrawable("textures/gui/villager.png", 163, 120);
         public static final ResourceLocation TABS = ResourceLocation.fromNamespaceAndPath("jeresources", "textures/gui/tabs.png");
      }
   }

   public static final class Vanilla {
      public static final ResourceLocation FONT = ResourceLocation.withDefaultNamespace("textures/font/ascii.png");
      public static final ResourceLocation CHEST = ResourceLocation.withDefaultNamespace("textures/entity/chest/normal.png");
   }
}
