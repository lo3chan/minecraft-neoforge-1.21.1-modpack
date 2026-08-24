package com.finndog.moogs_structures.neoforge.client;

import com.finndog.moogs_structures.client.ClothRequiredScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(
   value = "moogs_structures",
   dist = {Dist.CLIENT}
)
public class MoogsStructuresNeoforgeClient {
   public MoogsStructuresNeoforgeClient(ModContainer modContainer) {
      modContainer.registerExtensionPoint(
         IConfigScreenFactory.class,
         (IConfigScreenFactory)(minecraft, parent) -> (Screen)(ModList.get().isLoaded("cloth_config")
            ? MoogsStructuresConfigScreenNeoforge.create(parent)
            : new ClothRequiredScreen(parent))
      );
   }
}
