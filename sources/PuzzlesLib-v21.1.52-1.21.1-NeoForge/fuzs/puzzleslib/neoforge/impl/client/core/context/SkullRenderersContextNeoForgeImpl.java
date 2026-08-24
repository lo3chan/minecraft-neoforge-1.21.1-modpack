package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;
import java.util.Objects;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.CreateSkullModels;

public record SkullRenderersContextNeoForgeImpl(CreateSkullModels event) implements SkullRenderersContext {
   @Override
   public void registerSkullRenderer(SkullRenderersFactory factory) {
      Objects.requireNonNull(factory, "factory is null");
      factory.createSkullRenderers(this.event.getEntityModelSet(), this.event::registerSkullModel);
   }
}
