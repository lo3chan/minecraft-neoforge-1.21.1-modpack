package traben.entity_texture_features.mixin.mixins.entity.block;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BaseSpawner.class})
public abstract class MixinMobSpawnerLogic {
   @Inject(
      method = {"getOrCreateDisplayEntity"},
      at = {@At("RETURN")}
   )
   private void etf$stabiliseMobSpawnerUUID(Level world, BlockPos pos, CallbackInfoReturnable<Entity> cir) {
      Entity entity = (Entity)cir.getReturnValue();
      if (entity != null) {
         entity.setUUID(new UUID(pos.asLong(), 53021371281465L));
      }
   }
}
