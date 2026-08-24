package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.client.particle.AnimFireParticle;
import net.mcreator.borninchaosv.client.particle.CandyOrangeParticle;
import net.mcreator.borninchaosv.client.particle.CandygrenParticle;
import net.mcreator.borninchaosv.client.particle.CandypurpleParticle;
import net.mcreator.borninchaosv.client.particle.ChaosenergyParticle;
import net.mcreator.borninchaosv.client.particle.ChitParticle;
import net.mcreator.borninchaosv.client.particle.CloudsofdustParticle;
import net.mcreator.borninchaosv.client.particle.DarkSmokeParticle;
import net.mcreator.borninchaosv.client.particle.DarkmatterParticle;
import net.mcreator.borninchaosv.client.particle.DarkspotsParticle;
import net.mcreator.borninchaosv.client.particle.DimLongParticle;
import net.mcreator.borninchaosv.client.particle.DimParticle;
import net.mcreator.borninchaosv.client.particle.DimlargParticle;
import net.mcreator.borninchaosv.client.particle.FleshsplashParticle;
import net.mcreator.borninchaosv.client.particle.FliParticle;
import net.mcreator.borninchaosv.client.particle.InfernalExplosionParticle;
import net.mcreator.borninchaosv.client.particle.InfernalSurgeParticle;
import net.mcreator.borninchaosv.client.particle.InfernaltrailParticle;
import net.mcreator.borninchaosv.client.particle.IntoxicatindBombPartParticle;
import net.mcreator.borninchaosv.client.particle.IntoxicatingsmokeParticle;
import net.mcreator.borninchaosv.client.particle.LittleCarrotParticle;
import net.mcreator.borninchaosv.client.particle.LittlesnowflakeParticle;
import net.mcreator.borninchaosv.client.particle.MagichitParticle;
import net.mcreator.borninchaosv.client.particle.MagictrailParticle;
import net.mcreator.borninchaosv.client.particle.ObsessionparParticle;
import net.mcreator.borninchaosv.client.particle.PumpkinStaffSParticle;
import net.mcreator.borninchaosv.client.particle.PumpkinTrailParticle;
import net.mcreator.borninchaosv.client.particle.RitualParticle;
import net.mcreator.borninchaosv.client.particle.RoarsplashParticle;
import net.mcreator.borninchaosv.client.particle.SnowcloudParticle;
import net.mcreator.borninchaosv.client.particle.SoulFireParticle;
import net.mcreator.borninchaosv.client.particle.SoulSlashParticle;
import net.mcreator.borninchaosv.client.particle.SpiderBlastParticle;
import net.mcreator.borninchaosv.client.particle.SpiderInfestationParticle;
import net.mcreator.borninchaosv.client.particle.SpikereleaseParticle;
import net.mcreator.borninchaosv.client.particle.SplashoffleshParticle;
import net.mcreator.borninchaosv.client.particle.SrirstPartParticle;
import net.mcreator.borninchaosv.client.particle.StimulatingbubblesParticle;
import net.mcreator.borninchaosv.client.particle.StimulatingsmokeParticle;
import net.mcreator.borninchaosv.client.particle.StunstarsParticle;
import net.mcreator.borninchaosv.client.particle.SwapParticle;
import net.mcreator.borninchaosv.client.particle.TLParticle;
import net.mcreator.borninchaosv.client.particle.WaningsnowflakeParticle;
import net.mcreator.borninchaosv.client.particle.WebSplashParticle;
import net.mcreator.borninchaosv.client.particle.XPParticleParticle;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class BornInChaosV1ModParticles {
   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SPLASHOFFLESH.get(), SplashoffleshParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.TL.get(), TLParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.DIM.get(), DimParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), RitualParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SRIRST_PART.get(), SrirstPartParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_STAFF_S.get(), PumpkinStaffSParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SOUL_FIRE.get(), SoulFireParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), AnimFireParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), IntoxicatindBombPartParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.MAGICTRAIL.get(), MagictrailParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SWAP.get(), SwapParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SOUL_SLASH.get(), SoulSlashParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), DarkSmokeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(), StunstarsParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), SpikereleaseParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), FleshsplashParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.FLI.get(), FliParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), InfernalSurgeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.INFERNALTRAIL.get(), InfernaltrailParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), DimlargParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.INTOXICATINGSMOKE.get(), IntoxicatingsmokeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.STIMULATINGSMOKE.get(), StimulatingsmokeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.STIMULATINGBUBBLES.get(), StimulatingbubblesParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), ObsessionparParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.CHAOSENERGY.get(), ChaosenergyParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.DIM_LONG.get(), DimLongParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.MAGICHIT.get(), MagichitParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_TRAIL.get(), PumpkinTrailParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_EXPLOSION.get(), InfernalExplosionParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.WEB_SPLASH.get(), WebSplashParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SPIDER_BLAST.get(), SpiderBlastParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SPIDER_INFESTATION.get(), SpiderInfestationParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.CLOUDSOFDUST.get(), CloudsofdustParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.ROARSPLASH.get(), RoarsplashParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.DARKMATTER.get(), DarkmatterParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.CANDY_ORANGE.get(), CandyOrangeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.CANDYGREN.get(), CandygrenParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.CANDYPURPLE.get(), CandypurpleParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.CHIT.get(), ChitParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(), LittlesnowflakeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(), WaningsnowflakeParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.SNOWCLOUD.get(), SnowcloudParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.DARKSPOTS.get(), DarkspotsParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.LITTLE_CARROT.get(), LittleCarrotParticle::provider);
      event.registerSpriteSet((ParticleType)BornInChaosV1ModParticleTypes.XP_PARTICLE.get(), XPParticleParticle::provider);
   }
}
