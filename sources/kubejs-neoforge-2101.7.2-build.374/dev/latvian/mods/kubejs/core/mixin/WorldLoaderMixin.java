package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldLoader.InitConfig;
import net.minecraft.server.WorldLoader.ResultFactory;
import net.minecraft.server.WorldLoader.WorldDataSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WorldLoader.class})
public class WorldLoaderMixin {
   @Inject(
      method = {"load"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
         shift = Shift.BEFORE
      )}
   )
   private static <D, R> void kjs$load(
      InitConfig initConfig,
      WorldDataSupplier<D> worldDataSupplier,
      ResultFactory<D, R> resultFactory,
      Executor backgroundExecutor,
      Executor gameExecutor,
      CallbackInfoReturnable<CompletableFuture<R>> cir,
      @Local Frozen registriesWithDimensions
   ) {
      RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithDimensions);
   }

   @Inject(
      method = {"load"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/datafixers/util/Pair;getFirst()Ljava/lang/Object;",
         shift = Shift.BEFORE
      )}
   )
   private static <D, R> void kjs$load2(
      InitConfig initConfig,
      WorldDataSupplier<D> worldDataSupplier,
      ResultFactory<D, R> resultFactory,
      Executor backgroundExecutor,
      Executor gameExecutor,
      CallbackInfoReturnable<CompletableFuture<R>> cir,
      @Local(ordinal = 1) Frozen registriesWithEverything
   ) {
      RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithEverything);
   }
}
