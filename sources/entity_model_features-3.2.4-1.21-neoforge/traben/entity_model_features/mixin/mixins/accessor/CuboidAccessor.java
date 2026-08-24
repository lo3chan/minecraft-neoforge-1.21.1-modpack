package traben.entity_model_features.mixin.mixins.accessor;

import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Cube.class})
public interface CuboidAccessor {
   @Mutable
   @Accessor("polygons")
   void setPolygons(Polygon[] var1);

   @Mutable
   @Accessor("minX")
   void setMinX(float var1);

   @Mutable
   @Accessor("minY")
   void setMinY(float var1);

   @Mutable
   @Accessor("minZ")
   void setMinZ(float var1);

   @Mutable
   @Accessor("maxX")
   void setMaxX(float var1);

   @Mutable
   @Accessor("maxY")
   void setMaxY(float var1);

   @Mutable
   @Accessor("maxZ")
   void setMaxZ(float var1);
}
