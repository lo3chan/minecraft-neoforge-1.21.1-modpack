package net.astralya.hexalia.effect;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.hexalia.effect.custom.ArachnidGraceEffect;
import net.astralya.hexalia.effect.custom.BleedingEffect;
import net.astralya.hexalia.effect.custom.BloodlustEffect;
import net.astralya.hexalia.effect.custom.BrambleguardEffect;
import net.astralya.hexalia.effect.custom.DaybloomEffect;
import net.astralya.hexalia.effect.custom.HollowSilenceEffect;
import net.astralya.hexalia.effect.custom.OverfedEffect;
import net.astralya.hexalia.effect.custom.SiphonEffect;
import net.astralya.hexalia.effect.custom.SlimewalkerEffect;
import net.astralya.hexalia.effect.custom.SpikeskinEffect;
import net.astralya.hexalia.effect.custom.StunnedEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public final class ModMobEffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("hexalia", Registries.MOB_EFFECT);
   public static final RegistrySupplier<MobEffect> OVERFED = MOB_EFFECTS.register(
      "overfed",
      () -> new OverfedEffect(MobEffectCategory.NEUTRAL, 11545387)
         .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("overfed"), -0.10000000149011612, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static final RegistrySupplier<MobEffect> DAYBLOOM = MOB_EFFECTS.register("daybloom", () -> new DaybloomEffect(MobEffectCategory.NEUTRAL, 16767326));
   public static final RegistrySupplier<MobEffect> BLOODLUST = MOB_EFFECTS.register(
      "bloodlust",
      () -> new BloodlustEffect(MobEffectCategory.NEUTRAL, 9044739, 3.0)
         .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("bloodlust"), 0.0, Operation.ADD_VALUE)
   );
   public static final RegistrySupplier<MobEffect> SPIKESKIN = MOB_EFFECTS.register(
      "spikeskin",
      () -> new SpikeskinEffect(MobEffectCategory.NEUTRAL, 4090671, 3.0)
         .addAttributeModifier(Attributes.ARMOR, id("spikeskin_armor"), 0.0, Operation.ADD_VALUE)
         .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("spikeskin_slow"), -0.10000000149011612, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static final RegistrySupplier<MobEffect> SIPHON = MOB_EFFECTS.register(
      "siphon",
      () -> new SiphonEffect(MobEffectCategory.NEUTRAL, 7237362, 3.0)
         .addAttributeModifier(Attributes.ATTACK_SPEED, id("siphon"), 0.4000000059604645, Operation.ADD_VALUE)
   );
   public static final RegistrySupplier<MobEffect> HOLLOW_SILENCE = MOB_EFFECTS.register(
      "hollow_silence", () -> new HollowSilenceEffect(MobEffectCategory.NEUTRAL, 1315352)
   );
   public static final RegistrySupplier<MobEffect> SLIMEWALKER = MOB_EFFECTS.register(
      "slimewalker", () -> new SlimewalkerEffect(MobEffectCategory.NEUTRAL, 7072325)
   );
   public static final RegistrySupplier<MobEffect> ARACHNID_GRACE = MOB_EFFECTS.register(
      "arachnid_grace", () -> new ArachnidGraceEffect(MobEffectCategory.NEUTRAL, 3878474)
   );
   public static final RegistrySupplier<MobEffect> BRAMBLEGUARD = MOB_EFFECTS.register(
      "brambleguard", () -> new BrambleguardEffect(MobEffectCategory.NEUTRAL, 4281399)
   );
   public static final RegistrySupplier<MobEffect> STUNNED = MOB_EFFECTS.register("stunned", () -> new StunnedEffect(MobEffectCategory.HARMFUL, 16777181));
   public static final RegistrySupplier<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", () -> new BleedingEffect(MobEffectCategory.HARMFUL, 9109504));

   private ModMobEffects() {
   }

   private static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", path);
   }

   public static void init() {
      MOB_EFFECTS.register();
   }
}
