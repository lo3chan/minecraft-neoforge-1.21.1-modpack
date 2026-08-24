package net.irisshaders.iris.mixin;

import java.util.function.Supplier;
import net.irisshaders.iris.Iris;
import net.minecraft.SystemReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SystemReport.class})
public abstract class MixinSystemReport {
   @Shadow
   public abstract void setDetail(String var1, Supplier<String> var2);

   @Inject(
      at = {@At("RETURN")},
      method = {"<init>"}
   )
   private void fillSystemDetails(CallbackInfo ci) {
      if (Iris.getCurrentPackName() != null) {
         this.setDetail("Loaded Shaderpack", () -> {
            StringBuilder sb = new StringBuilder(Iris.getCurrentPackName() + (Iris.isFallback() ? " (fallback)" : ""));
            Iris.getCurrentPack().ifPresent(pack -> {
               sb.append("\n\t\t");
               sb.append(pack.getProfileInfo());
            });
            return sb.toString();
         });
      }
   }
}
