package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.kubejs.core.BlockBehaviourKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@RemapPrefixForJS("kjs$")
@Mixin({BlockBehaviour.class})
public abstract class BlockBehaviourMixin implements BlockBehaviourKJS {
   @Unique
   private Consumer<RandomTickCallback> kjs$randomTickCallback;

   @Override
   public void kjs$setRandomTickCallback(Consumer<RandomTickCallback> callback) {
      this.kjs$setIsRandomlyTicking(true);
      this.kjs$randomTickCallback = callback;
   }

   @Inject(
      method = {"randomTick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRandomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
      if (this.kjs$randomTickCallback != null) {
         this.kjs$randomTickCallback.accept(new RandomTickCallback(serverLevel.kjs$getBlock(blockPos).cache(blockState), randomSource));
         ci.cancel();
      }
   }

   @Accessor("hasCollision")
   @Mutable
   @Override
   public abstract void kjs$setHasCollision(boolean v);

   @Accessor("explosionResistance")
   @Mutable
   @Override
   public abstract void kjs$setExplosionResistance(float v);

   @Accessor("isRandomlyTicking")
   @Mutable
   @Override
   public abstract void kjs$setIsRandomlyTicking(boolean v);

   @Accessor("soundType")
   @Mutable
   @Override
   public abstract void kjs$setSoundType(SoundType v);

   @Accessor("friction")
   @Mutable
   @Override
   public abstract void kjs$setFriction(float v);

   @Accessor("speedFactor")
   @Mutable
   @Override
   public abstract void kjs$setSpeedFactor(float v);

   @Accessor("jumpFactor")
   @Mutable
   @Override
   public abstract void kjs$setJumpFactor(float v);
}
