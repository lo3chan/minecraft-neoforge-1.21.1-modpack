package net.diebuddies.mixins.item;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Snowball.class})
public abstract class MixinSnowball extends MixinEntity {
   @Override
   public void onClientRemoval(CallbackInfo info) {
      if (ConfigClient.snowballModel != 2) {
         Level level = ((Snowball)this).level();
         PhysicsMod.addSnowball(level, (Snowball)this);
      }
   }
}
