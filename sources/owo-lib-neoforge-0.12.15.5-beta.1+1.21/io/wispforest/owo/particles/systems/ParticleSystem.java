package io.wispforest.owo.particles.systems;

import io.wispforest.endec.Endec;
import io.wispforest.owo.network.NetworkException;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owo.util.ServicesFrozenException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleSystem<T> {
   private final ParticleSystemController manager;
   final Class<T> dataClass;
   final int index;
   final Endec<T> endec;
   ParticleSystemExecutor<T> handler;
   private final boolean permitsContextlessExecution;

   ParticleSystem(ParticleSystemController manager, Class<T> dataClass, int index, Endec<T> endec, ParticleSystemExecutor<T> handler) {
      OwoFreezer.checkRegister("Particle systems");
      this.manager = manager;
      this.dataClass = dataClass;
      this.index = index;
      this.endec = endec;
      this.handler = handler;
      this.permitsContextlessExecution = dataClass == Void.class;
   }

   public void setHandler(ParticleSystemExecutor<T> handler) {
      if (OwoFreezer.isFrozen()) {
         throw new ServicesFrozenException("Particle systems can only be changed during mod init");
      } else if (this.handler != null) {
         throw new NetworkException("Particle system already has a handler");
      } else {
         this.handler = handler;
      }
   }

   public void spawn(Level world, Vec3 pos, @Nullable T data) {
      if (data == null && !this.permitsContextlessExecution) {
         throw new IllegalStateException("This particle system does not permit 'null' data");
      } else {
         if (world.isClientSide) {
            this.handler.executeParticleSystem(world, pos, data);
         } else {
            this.manager.sendPacket(this, (ServerLevel)world, pos, data);
         }
      }
   }

   public void spawn(Level world, Vec3 pos) {
      this.spawn(world, pos, null);
   }
}
