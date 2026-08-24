package com.yungnickyoung.minecraft.betteroceanmonuments.mixin;

import com.yungnickyoung.minecraft.betteroceanmonuments.mixin.accessor.ProjectileAccessor;
import com.yungnickyoung.minecraft.betteroceanmonuments.module.TagModule;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractArrow.class})
public abstract class PersistentTridentMixin extends Entity {
   public PersistentTridentMixin(EntityType<?> $$0, Level $$1) {
      super($$0, $$1);
   }

   @Inject(
      method = {"tickDespawn"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void betteroceanmonuments_preventTridentDespawning(CallbackInfo ci) {
      if (this.level() instanceof ServerLevel serverLevel && this.isTrident(this) && this.getOwner(this).equals("e624cdc1-c238-4dde-9f22-1f76b5123ce8")) {
         StructureStart structureStart = serverLevel.structureManager().getStructureWithPieceAt(this.blockPosition(), TagModule.BETTER_OCEAN_MONUMENT);
         if (structureStart.isValid()) {
            ci.cancel();
         }
      }
   }

   @Unique
   private boolean isTrident(Object object) {
      return object instanceof ThrownTrident;
   }

   @Unique
   private String getOwner(Object object) {
      return object instanceof Projectile projectile && ((ProjectileAccessor)projectile).getOwnerUUID() != null
         ? ((ProjectileAccessor)projectile).getOwnerUUID().toString()
         : "";
   }
}
