package vazkii.psi.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import vazkii.psi.common.Psi;

public class ModModelLayers {
   public static final ModelLayerLocation PSIMETAL_EXOSUIT_INNER_ARMOR = make("inner_armor");
   public static final ModelLayerLocation PSIMETAL_EXOSUIT_OUTER_ARMOR = make("outer_armor");

   private static ModelLayerLocation make(String layer) {
      return new ModelLayerLocation(Psi.location("psimetal_exosuit"), layer);
   }
}
