package me.shedaniel.clothconfig;

import me.shedaniel.clothconfig2.ClothConfigDemo;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClothConfigForgeDemo {
   public static void registerModsPage() {
      ModLoadingContext.get()
         .registerExtensionPoint(
            IConfigScreenFactory.class, () -> (container, parent) -> ClothConfigDemo.getConfigBuilderWithDemo().setParentScreen(parent).build()
         );
   }
}
