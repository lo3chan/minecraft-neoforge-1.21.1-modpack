package net.diebuddies.mixins.item;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ThrownEnderpearl.class})
public abstract class MixinEnderpearl extends MixinEntity {
   @Override
   public void onClientRemoval(CallbackInfo info) {
      if (ConfigClient.enderpearlModel != 2) {
         Level level = ((ThrownEnderpearl)this).level();
         PhysicsMod.addEnderpearl(level, (ThrownEnderpearl)this);
      }
   }
}
