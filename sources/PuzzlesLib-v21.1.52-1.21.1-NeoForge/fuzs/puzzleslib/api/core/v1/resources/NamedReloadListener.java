package fuzs.puzzleslib.api.core.v1.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@Deprecated
public interface NamedReloadListener extends PreparableReloadListener {
   ResourceLocation identifier();

   default String getName() {
      return this.identifier().toString();
   }
}
