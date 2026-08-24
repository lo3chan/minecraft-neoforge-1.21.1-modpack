package dev.tr7zw.waveycapes.mixin;

import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class})
public abstract class PlayerMixin extends Entity implements CapeHolder {
   @Unique
   private BasicSimulation simulation;
   @Unique
   private Vector3 lastPlayerAnimatorPosition = new Vector3();
   @Unique
   private boolean dirty = false;

   public PlayerMixin(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public void setDirty() {
      this.dirty = true;
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("TAIL")}
   )
   private void moveCloakUpdate(CallbackInfo info) {
      if (this instanceof AbstractClientPlayer entity) {
         this.updateSimulation(16);
         PlayerDelegate playerDelegate = new PlayerDelegate(entity);
         if (this.dirty) {
            this.dirty = false;
            this.simulation.applyMovement(new Vector3(1.0F, 1.0F, 0.0F));

            for (int i = 0; i < 5; i++) {
               this.simulate(playerDelegate);
            }
         }

         this.simulate(playerDelegate);
      }
   }

   @Override
   public UUID getWCUUID() {
      return this.getUUID();
   }

   @Generated
   @Override
   public BasicSimulation getSimulation() {
      return this.simulation;
   }

   @Generated
   @Override
   public void setSimulation(BasicSimulation simulation) {
      this.simulation = simulation;
   }

   @Generated
   @Override
   public Vector3 getLastPlayerAnimatorPosition() {
      return this.lastPlayerAnimatorPosition;
   }

   @Generated
   @Override
   public void setLastPlayerAnimatorPosition(Vector3 lastPlayerAnimatorPosition) {
      this.lastPlayerAnimatorPosition = lastPlayerAnimatorPosition;
   }
}
