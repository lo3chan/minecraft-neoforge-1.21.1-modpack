package dev.latvian.mods.kubejs.client;

import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class ParticleProviderRegistryKubeEvent implements ClientKubeEvent {
   private final RegisterParticleProvidersEvent parent;

   public ParticleProviderRegistryKubeEvent(RegisterParticleProvidersEvent event) {
      this.parent = event;
   }

   public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProviderRegistryKubeEvent.SpriteSetParticleProvider<T> spriteProvider) {
      this.parent.registerSpriteSet(type, spriteProvider);
   }

   public <T extends ParticleOptions> void register(ParticleType<T> type, Consumer<KubeAnimatedParticle> particle) {
      this.parent.registerSpriteSet(type, set -> (type1, level, x, y, z, xSpeed, ySpeed, zSpeed) -> {
         KubeAnimatedParticle kube = new KubeAnimatedParticle(level, x, y, z, set);
         kube.setParticleSpeed(xSpeed, ySpeed, zSpeed);
         particle.accept(kube);
         return kube;
      });
   }

   public <T extends ParticleOptions> void register(ParticleType<T> type) {
      this.register(type, p -> {});
   }

   public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
      this.parent.registerSpecial(type, provider);
   }

   @FunctionalInterface
   public interface SpriteSetParticleProvider<T extends ParticleOptions> extends SpriteParticleRegistration<T> {
      Particle create(T type, ClientLevel clientLevel, double x, double y, double z, SpriteSet sprites, double xSpeed, double ySpeed, double zSpeed);

      default ParticleProvider<T> create(SpriteSet sprites) {
         return (type, level, x, y, z, xSpeed, ySpeed, zSpeed) -> this.create((T)type, level, x, y, z, sprites, xSpeed, ySpeed, zSpeed);
      }
   }
}
