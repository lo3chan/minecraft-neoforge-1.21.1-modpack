package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.client.particle.AcidgooParticle;
import net.mcreator.undeadrevamp.client.particle.BluefumesParticle;
import net.mcreator.undeadrevamp.client.particle.BombergooParticle;
import net.mcreator.undeadrevamp.client.particle.BrightpinkdustParticle;
import net.mcreator.undeadrevamp.client.particle.CloggercarcassesParticle;
import net.mcreator.undeadrevamp.client.particle.DripsomnolenceParticle;
import net.mcreator.undeadrevamp.client.particle.PinkdustParticle;
import net.mcreator.undeadrevamp.client.particle.ToxicfumespinkParticle;
import net.mcreator.undeadrevamp.client.particle.ZeesleepParticle;
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
public class UndeadRevamp2ModParticles {
   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), BombergooParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.ACIDGOO.get(), AcidgooParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.TOXICFUMESPINK.get(), ToxicfumespinkParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.CLOGGERCARCASSES.get(), CloggercarcassesParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.ZEESLEEP.get(), ZeesleepParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.BLUEFUMES.get(), BluefumesParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.DRIPSOMNOLENCE.get(), DripsomnolenceParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.PINKDUST.get(), PinkdustParticle::provider);
      event.registerSpriteSet((ParticleType)UndeadRevamp2ModParticleTypes.BRIGHTPINKDUST.get(), BrightpinkdustParticle::provider);
   }
}
