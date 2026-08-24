package net.bettercombat.client.particle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.bettercombat.api.fx.Color;
import net.bettercombat.api.fx.ConditionalTrailAppearance;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.config.TrailConfig;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.core.particles.ParticleType;

public class TrailParticles {
   private static final String NAMESPACE = "bettercombat";
   public static Map<String, List<TrailParticles.Entry>> ENTRIES = Map.of(
      "stab",
      List.of(
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topstab.particleType(), BetterCombatParticles.botstab.particleType())),
            -45.0F,
            true
         ),
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topstab.particleType(), BetterCombatParticles.botstab.particleType())),
            45.0F,
            true
         )
      ),
      "slash45",
      List.of(
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topslash45.particleType(), BetterCombatParticles.botslash45.particleType()))
         )
      ),
      "slash90",
      List.of(
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topslash90.particleType(), BetterCombatParticles.botslash90.particleType()))
         )
      ),
      "slash180",
      List.of(
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topslash180.particleType(), BetterCombatParticles.botslash180.particleType()))
         )
      ),
      "slash270",
      List.of(
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topslash270.particleType(), BetterCombatParticles.botslash270.particleType()))
         )
      ),
      "slash360",
      List.of(
         new TrailParticles.Entry(
            List.of(new TrailParticles.LayeredParticle(BetterCombatParticles.topslash360.particleType(), BetterCombatParticles.botslash360.particleType()))
         )
      )
   );

   public static TrailConfig defaults() {
      LinkedHashMap<String, List<ParticlePlacement>> map = new LinkedHashMap<>();
      map.put("bettercombat:one_handed_slash_horizontal_right", List.of(new ParticlePlacement("slash90", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.0F)));
      map.put("bettercombat:one_handed_slash_horizontal_left", List.of(new ParticlePlacement("slash90", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 180.0F)));
      map.put("bettercombat:one_handed_uppercut_right", List.of(new ParticlePlacement("slash90", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 75.0F)));
      map.put("bettercombat:one_handed_swipe_horizontal_right", List.of(new ParticlePlacement("slash90", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, -0.1F)));
      map.put("bettercombat:one_handed_slam", List.of(new ParticlePlacement("slash90", 0.05F, -0.1F, 0.0F, 45.0F, 0.0F, -85.0F)));
      map.put("bettercombat:one_handed_stab", List.of(new ParticlePlacement("stab", 0.0F, -0.13F, 0.2F, 0.0F, 0.0F, 0.0F)));
      map.put("bettercombat:one_handed_stab_mounted", List.of(new ParticlePlacement("stab", 0.0F, 0.15F, 0.0F, 0.0F, 0.0F, 0.0F)));
      map.put("bettercombat:one_handed_punch", List.of(new ParticlePlacement("stab", 0.0F, -0.1F, 0.15F, 0.0F, 0.0F, 0.0F)));
      map.put(
         "bettercombat:dual_handed_slash_cross",
         List.of(
            new ParticlePlacement("slash180", 0.2F, -0.15F, 0.0F, 0.0F, 0.0F, -120.0F),
            new ParticlePlacement("slash180", -0.2F, -0.15F, 0.0F, 0.0F, 0.0F, -60.0F)
         )
      );
      map.put(
         "bettercombat:dual_handed_slash_uncross",
         List.of(
            new ParticlePlacement("slash180", 0.2F, -0.15F, 0.0F, 0.0F, 0.0F, 240.0F),
            new ParticlePlacement("slash180", -0.2F, -0.15F, 0.0F, 0.0F, 0.0F, 300.0F)
         )
      );
      map.put(
         "bettercombat:dual_handed_stab",
         List.of(new ParticlePlacement("stab", 0.4F, -0.3F, 0.0F, 0.0F, 0.0F, 0.0F), new ParticlePlacement("stab", -0.4F, -0.3F, 0.0F, 0.0F, 0.0F, 0.0F))
      );
      map.put("bettercombat:two_handed_stab_left", List.of(new ParticlePlacement("stab", 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 0.0F)));
      map.put("bettercombat:two_handed_stab_right", List.of(new ParticlePlacement("stab", 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 0.0F)));
      map.put("bettercombat:two_handed_slash_horizontal_right", List.of(new ParticlePlacement("slash180", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, -5.0F)));
      map.put("bettercombat:two_handed_slash_horizontal_left", List.of(new ParticlePlacement("slash180", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 180.0F)));
      map.put("bettercombat:one_handed_slash_switch_blade_right", List.of(new ParticlePlacement("slash180", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.0F)));
      map.put("bettercombat:one_handed_slash_switch_blade_left", List.of(new ParticlePlacement("slash180", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 180.0F)));
      map.put("bettercombat:two_handed_spin", List.of(new ParticlePlacement("slash360", 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 180.0F)));
      map.put("bettercombat:two_handed_slash_vertical_right", List.of(new ParticlePlacement("slash90", 0.0F, -0.1F, 0.0F, 45.0F, 0.0F, -80.0F)));
      map.put("bettercombat:two_handed_slash_vertical_left", List.of(new ParticlePlacement("slash90", 0.0F, -0.1F, 0.0F, 45.0F, 0.0F, -100.0F)));
      map.put("bettercombat:two_handed_slam", List.of(new ParticlePlacement("slash180", 0.1F, -0.1F, 0.0F, 45.0F, 0.0F, -86.0F)));
      map.put("bettercombat:two_handed_slam_heavy", List.of(new ParticlePlacement("slash180", 0.1F, -0.1F, 0.0F, 45.0F, 0.0F, -86.0F)));
      TrailAppearance defaultTrail = new TrailAppearance(
         new TrailAppearance.Part(Color.WHITE.alpha(0.6F).toRGBA(), false), new TrailAppearance.Part(Color.from(10066329).alpha(0.4F).toRGBA(), false)
      );
      TrailAppearance enchantedTrail = new TrailAppearance(
         new TrailAppearance.Part(Color.from(6740479).alpha(0.6F).toRGBA(), true), new TrailAppearance.Part(Color.from(10086143).alpha(0.3F).toRGBA(), true)
      );
      LinkedHashMap<String, TrailAppearance> conditionalAppearances = new LinkedHashMap<>();
      conditionalAppearances.put("is_enchanted", enchantedTrail);
      ConditionalTrailAppearance trailAppearance = new ConditionalTrailAppearance(defaultTrail, conditionalAppearances);
      return new TrailConfig(trailAppearance, map);
   }

   public record Entry(List<TrailParticles.LayeredParticle> particles, float rollOffset, boolean stabPosition) {
      public Entry(List<TrailParticles.LayeredParticle> particles, float rollOffset) {
         this(particles, rollOffset, false);
      }

      public Entry(List<TrailParticles.LayeredParticle> particles) {
         this(particles, 0.0F, false);
      }
   }

   public record LayeredParticle(ParticleType<SlashParticleEffect> top, ParticleType<SlashParticleEffect> bottom) {
      public TrailParticles.LayeredParticle of(ParticleType<SlashParticleEffect> top, ParticleType<SlashParticleEffect> bottom) {
         return new TrailParticles.LayeredParticle(top, bottom);
      }
   }
}
