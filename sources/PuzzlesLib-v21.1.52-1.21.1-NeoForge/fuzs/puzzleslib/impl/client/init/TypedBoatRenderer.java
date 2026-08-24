package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Type;

public class TypedBoatRenderer extends BoatRenderer {
   public TypedBoatRenderer(Context context, ModelLayerLocation modelId, Function<ModelPart, ListModel<Boat>> modelFactory) {
      super(context, false);
      this.boatResources = ImmutableMap.of(
         Type.OAK, Pair.of(modelId.getModel().withPath(path -> "textures/entity/" + path + ".png"), modelFactory.apply(context.bakeLayer(modelId)))
      );
   }
}
