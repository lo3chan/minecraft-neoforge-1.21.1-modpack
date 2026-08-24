package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.client.particle.ParticleProvider.Sprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface ParticleProvidersContext {
   <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> var1, ParticleProvider<T> var2);

   @Deprecated
   <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> var1, Sprite<T> var2);

   <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> var1, SpriteParticleRegistration<T> var2);
}
