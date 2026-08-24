package me.shedaniel.clothconfig;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("cloth_config")
public class ClothConfigForge {
   public ClothConfigForge() {
      if (FMLEnvironment.dist.isClient()) {
         ClothConfigForgeDemo.registerModsPage();
      }
   }
}
