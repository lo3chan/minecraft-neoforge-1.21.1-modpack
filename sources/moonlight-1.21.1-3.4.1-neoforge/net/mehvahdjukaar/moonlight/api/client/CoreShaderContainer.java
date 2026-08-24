package net.mehvahdjukaar.moonlight.api.client;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.integration.IrisCompat;
import net.minecraft.client.renderer.ShaderInstance;

public class CoreShaderContainer implements Supplier<ShaderInstance> {
   private final Supplier<ShaderInstance> vanillaFallback;
   private ShaderInstance instance;

   public CoreShaderContainer(Supplier<ShaderInstance> vanillaFallback) {
      this.vanillaFallback = vanillaFallback;
   }

   public void assign(ShaderInstance instance) {
      this.instance = instance;
   }

   public ShaderInstance get() {
      return CompatHandler.IRIS && !CompatHandler.MONOCLE && IrisCompat.isIrisShaderStuffActive()
         ? this.vanillaFallback.get()
         : (ShaderInstance)Preconditions.checkNotNull(this.instance, "Shader {} was not assigned! How?!" + this.instance);
   }
}
