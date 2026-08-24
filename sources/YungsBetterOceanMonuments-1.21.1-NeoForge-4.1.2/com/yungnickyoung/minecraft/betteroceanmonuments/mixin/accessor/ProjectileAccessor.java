package com.yungnickyoung.minecraft.betteroceanmonuments.mixin.accessor;

import java.util.UUID;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Projectile.class})
public interface ProjectileAccessor {
   @Accessor
   UUID getOwnerUUID();
}
