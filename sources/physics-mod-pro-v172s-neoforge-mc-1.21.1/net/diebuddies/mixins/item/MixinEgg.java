package net.diebuddies.mixins.item;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ThrownEgg.class})
public abstract class MixinEgg extends MixinEntity {
   @Override
   public void onClientRemoval(CallbackInfo info) {
      if (ConfigClient.eggModel != 2) {
         Level level = ((ThrownEgg)this).level();
         PhysicsMod.addEgg(level, (ThrownEgg)this);
      }
   }
}
