package net.nycto_team.overpacked.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.nycto_team.overpacked.util.ModLoc;

public class ModModelLayers {
   public static final ModelLayerLocation giant_backpack = reg("giant_backpack");
   public static final ModelLayerLocation giant_backpack_on_player = reg("giant_backpack_on_player");

   private static ModelLayerLocation reg(String path, String model) {
      return new ModelLayerLocation(ModLoc.get(path), model);
   }

   private static ModelLayerLocation reg(String path) {
      return reg(path, "main");
   }
}
