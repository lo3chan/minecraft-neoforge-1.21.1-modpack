package net.mehvahdjukaar.moonlight.api.client.model;

import java.util.Iterator;
import net.mehvahdjukaar.moonlight.core.mixins.accessor.AgeableListModelAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

public interface IModelPartExtension {
   void moonlight$setDimensions(int var1, int var2);

   int moonlight$getTextWidth();

   int moonlight$getTextHeight();

   @Nullable
   static ModelPart getRootPart(EntityModel<?> model) {
      if (model instanceof AgeableListModelAccessor al) {
         Iterator var3 = al.moonlight$invokeBodyParts().iterator();
         if (var3.hasNext()) {
            return (ModelPart)var3.next();
         }
      } else if (model instanceof HierarchicalModel<?> m) {
         return m.root();
      }

      return null;
   }

   static int[] getTextureSize(EntityModel<?> model) {
      ModelPart part = getRootPart(model);
      if (part != null) {
         IModelPartExtension ext = (IModelPartExtension)part;
         return new int[]{ext.moonlight$getTextWidth(), ext.moonlight$getTextHeight()};
      } else {
         return new int[]{64, 64};
      }
   }
}
