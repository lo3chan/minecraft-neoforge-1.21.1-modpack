package net.bettercombat.particle;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BetterCombatParticles {
   private static final int default_age = 20;
   private static final int default_frames = 8;
   public static final ArrayList<BetterCombatParticles.Entry> ENTRIES = new ArrayList<>();
   public static final BetterCombatParticles.Entry botslash45 = add(new BetterCombatParticles.Entry("botslash45"));
   public static final BetterCombatParticles.Entry botslash90 = add(new BetterCombatParticles.Entry("botslash90"));
   public static final BetterCombatParticles.Entry botslash180 = add(new BetterCombatParticles.Entry("botslash180"));
   public static final BetterCombatParticles.Entry botslash270 = add(new BetterCombatParticles.Entry("botslash270"));
   public static final BetterCombatParticles.Entry botslash360 = add(new BetterCombatParticles.Entry("botslash360"));
   public static final BetterCombatParticles.Entry botstab = add(new BetterCombatParticles.Entry("botstab"));
   public static final BetterCombatParticles.Entry topslash45 = add(new BetterCombatParticles.Entry("topslash45"));
   public static final BetterCombatParticles.Entry topslash90 = add(new BetterCombatParticles.Entry("topslash90"));
   public static final BetterCombatParticles.Entry topslash180 = add(new BetterCombatParticles.Entry("topslash180"));
   public static final BetterCombatParticles.Entry topslash270 = add(new BetterCombatParticles.Entry("topslash270"));
   public static final BetterCombatParticles.Entry topslash360 = add(new BetterCombatParticles.Entry("topslash360"));
   public static final BetterCombatParticles.Entry topstab = add(new BetterCombatParticles.Entry("topstab"));

   private static ParticleType<SlashParticleEffect> createParticle() {
      return new ParticleType<SlashParticleEffect>(false) {
         public MapCodec<SlashParticleEffect> codec() {
            return SlashParticleEffect.createCodec(this);
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, SlashParticleEffect> streamCodec() {
            return SlashParticleEffect.createPacketCodec(this);
         }
      };
   }

   private static BetterCombatParticles.Entry add(BetterCombatParticles.Entry simpleEntry) {
      ENTRIES.add(simpleEntry);
      return simpleEntry;
   }

   public static void register() {
      for (BetterCombatParticles.Entry entry : ENTRIES) {
         Registry.register(BuiltInRegistries.PARTICLE_TYPE, entry.id, entry.particleType);
      }
   }

   public record DynamicParams(float red, float green, float blue, float alpha) {
   }

   public record Entry(
      ResourceLocation id, BetterCombatParticles.Texture texture, BetterCombatParticles.StaticParams params, ParticleType<SlashParticleEffect> particleType
   ) {
      public Entry(String name, BetterCombatParticles.Texture texture, BetterCombatParticles.StaticParams params) {
         this(ResourceLocation.fromNamespaceAndPath("bettercombat", name), texture, params);
      }

      public Entry(String name, int textureFrames, BetterCombatParticles.StaticParams params) {
         this(ResourceLocation.fromNamespaceAndPath("bettercombat", name), BetterCombatParticles.Texture.of(name, textureFrames), params);
      }

      public Entry(ResourceLocation id, BetterCombatParticles.Texture texture, BetterCombatParticles.StaticParams params) {
         this(id, texture, params, BetterCombatParticles.createParticle());
      }

      public Entry(String name) {
         this(
            ResourceLocation.fromNamespaceAndPath("bettercombat", name),
            BetterCombatParticles.Texture.of(name, 8),
            new BetterCombatParticles.StaticParams(true, 20, 1.0F)
         );
      }
   }

   public record StaticParams(boolean animated, int maxAge, float scale) {
   }

   public record Texture(ResourceLocation id, int frames) {
      public static BetterCombatParticles.Texture vanilla(String name) {
         return new BetterCombatParticles.Texture(ResourceLocation.withDefaultNamespace(name), 1);
      }

      public static BetterCombatParticles.Texture vanilla(String name, int frames) {
         return new BetterCombatParticles.Texture(ResourceLocation.withDefaultNamespace(name), frames);
      }

      public static BetterCombatParticles.Texture of(String name) {
         return new BetterCombatParticles.Texture(ResourceLocation.fromNamespaceAndPath("bettercombat", name), 1);
      }

      public static BetterCombatParticles.Texture of(String name, int frames) {
         return new BetterCombatParticles.Texture(ResourceLocation.fromNamespaceAndPath("bettercombat", name), frames);
      }
   }
}
