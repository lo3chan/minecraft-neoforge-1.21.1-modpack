package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.BlockStateKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@RemapPrefixForJS("kjs$")
@Mixin({BlockStateBase.class})
public abstract class BlockStateBaseMixin implements BlockStateKJS {
   @Shadow
   protected abstract BlockState asState();

   @Accessor("destroySpeed")
   @Mutable
   @Override
   public abstract void kjs$setDestroySpeed(float v);

   @Accessor("requiresCorrectToolForDrops")
   @Mutable
   @Override
   public abstract void kjs$setRequiresTool(boolean v);

   @Accessor("lightEmission")
   @Mutable
   @Override
   public abstract void kjs$setLightEmission(int v);

   @Inject(
      method = {"randomTick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void kjs$onRandomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
      if (this.kjs$randomTickOverride(this.asState(), level, pos, random)) {
         ci.cancel();
      }
   }
}
