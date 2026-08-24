package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.renderer.v1.RenderTypeHelper;
import java.util.Objects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

public final class FluidRenderTypesContextImpl implements RenderTypesContext<Fluid> {
   public void registerRenderType(Fluid fluid, RenderType renderType) {
      Objects.requireNonNull(fluid, "fluid is null");
      Objects.requireNonNull(renderType, "render type is null");
      RenderTypeHelper.registerRenderType(fluid, renderType);
   }

   public RenderType getRenderType(Fluid object) {
      return ClientAbstractions.INSTANCE.getRenderType(object);
   }
}
