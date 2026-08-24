package fuzs.puzzleslib.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.event.GatherDataEvent.DataGeneratorConfig;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   value = {DatagenModLoader.class},
   remap = false
)
abstract class DatagenModLoaderNeoForgeMixin {
   @WrapOperation(
      method = {"begin"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/data/event/GatherDataEvent$DataGeneratorConfig;runAll()V"
      )}
   )
   private static void begin(DataGeneratorConfig dataGeneratorConfig, Operation<Void> operation) {
      if (!FMLEnvironment.production && isRunningDataGen()) {
         try {
            operation.call(new Object[]{dataGeneratorConfig});
         } catch (Throwable var6) {
            LogUtils.getLogger().error("Data generation failed", var6);
         } finally {
            System.exit(0);
         }
      } else {
         operation.call(new Object[]{dataGeneratorConfig});
      }
   }

   @Shadow
   public static boolean isRunningDataGen() {
      throw new RuntimeException();
   }
}
