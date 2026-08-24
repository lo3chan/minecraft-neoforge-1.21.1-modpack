package net.joefoxe.hexerei.data.candle;

import java.util.List;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class AbstractCandleEffect implements CandleEffect {
   public int checkCooldown;
   public ParticleOptions particle;
   public List<ResourceLocation> particleLocation;

   public AbstractCandleEffect(ParticleOptions particleOptions) {
      this.particle = particleOptions;
   }

   public AbstractCandleEffect() {
   }

   @Override
   public void tick(Level level, CandleTile blockEntity, CandleData candleData) {
   }

   @Override
   public ParticleOptions getParticleType() {
      return this.particle;
   }

   public boolean isEmpty() {
      return this.getLocationName().equals("hexerei:no_effect");
   }
}
