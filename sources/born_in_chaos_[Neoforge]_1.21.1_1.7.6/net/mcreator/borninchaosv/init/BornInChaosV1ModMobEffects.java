package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.potion.BadFeelingMobEffect;
import net.mcreator.borninchaosv.potion.BarbedattackMobEffect;
import net.mcreator.borninchaosv.potion.BlockBreakMobEffect;
import net.mcreator.borninchaosv.potion.BoneBarrierMobEffect;
import net.mcreator.borninchaosv.potion.BoneChillingMobEffect;
import net.mcreator.borninchaosv.potion.BoneFractureMobEffect;
import net.mcreator.borninchaosv.potion.CursedMarkMobEffect;
import net.mcreator.borninchaosv.potion.CurseoftheBoatMobEffect;
import net.mcreator.borninchaosv.potion.DarkSplashMobEffect;
import net.mcreator.borninchaosv.potion.DarkWardMobEffect;
import net.mcreator.borninchaosv.potion.DetonationMobEffect;
import net.mcreator.borninchaosv.potion.DogtruceMobEffect;
import net.mcreator.borninchaosv.potion.ExperienceContainerMobEffect;
import net.mcreator.borninchaosv.potion.FishBreathMobEffect;
import net.mcreator.borninchaosv.potion.FuriousRampageMobEffect;
import net.mcreator.borninchaosv.potion.GazeOfTerrorMobEffect;
import net.mcreator.borninchaosv.potion.IceBarrierMobEffect;
import net.mcreator.borninchaosv.potion.InfernalFlameMobEffect;
import net.mcreator.borninchaosv.potion.InfestationofFliesMobEffect;
import net.mcreator.borninchaosv.potion.InsectProtectionMobEffect;
import net.mcreator.borninchaosv.potion.InsipidityMobEffect;
import net.mcreator.borninchaosv.potion.IntoxicationMobEffect;
import net.mcreator.borninchaosv.potion.JawattackMobEffect;
import net.mcreator.borninchaosv.potion.LifestealMobEffect;
import net.mcreator.borninchaosv.potion.LightRampageMobEffect;
import net.mcreator.borninchaosv.potion.LivingBombMobEffect;
import net.mcreator.borninchaosv.potion.LivingCocoonMobEffect;
import net.mcreator.borninchaosv.potion.LivingCocoonPlayerSideMobEffect;
import net.mcreator.borninchaosv.potion.LostItemsMobEffect;
import net.mcreator.borninchaosv.potion.MagicDepletionMobEffect;
import net.mcreator.borninchaosv.potion.MediumRampageMobEffect;
import net.mcreator.borninchaosv.potion.MinionSummonMobEffect;
import net.mcreator.borninchaosv.potion.ObsessionMobEffect;
import net.mcreator.borninchaosv.potion.OverlyHeavyWeaponMobEffect;
import net.mcreator.borninchaosv.potion.PredatorsDesireMobEffect;
import net.mcreator.borninchaosv.potion.RabbitAgilityMobEffect;
import net.mcreator.borninchaosv.potion.RampantRampageMobEffect;
import net.mcreator.borninchaosv.potion.RottenSmellMobEffect;
import net.mcreator.borninchaosv.potion.SacrificeMobEffect;
import net.mcreator.borninchaosv.potion.SnowStormMobEffect;
import net.mcreator.borninchaosv.potion.SoulStratificationMobEffect;
import net.mcreator.borninchaosv.potion.StimulatingsurgeMobEffect;
import net.mcreator.borninchaosv.potion.StimulationMobEffect;
import net.mcreator.borninchaosv.potion.StonePersistenceMobEffect;
import net.mcreator.borninchaosv.potion.StrangleholdMobEffect;
import net.mcreator.borninchaosv.potion.StrongRampageMobEffect;
import net.mcreator.borninchaosv.potion.StunMobEffect;
import net.mcreator.borninchaosv.potion.StunningStrikeMobEffect;
import net.mcreator.borninchaosv.potion.SweetMadnessMobEffect;
import net.mcreator.borninchaosv.potion.TerrifyingPresenceMobEffect;
import net.mcreator.borninchaosv.potion.UndeadSummonunMobEffect;
import net.mcreator.borninchaosv.potion.UnityWithDarknessMobEffect;
import net.mcreator.borninchaosv.potion.VampiricTouchMobEffect;
import net.mcreator.borninchaosv.potion.WitherResistanceMobEffect;
import net.mcreator.borninchaosv.procedures.CursedMarkKoghdaEffiektZakanchivaietsiaProcedure;
import net.mcreator.borninchaosv.procedures.DarkSplashPriIstiechieniiEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.SweetMadnessPriIstiechieniiEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.UndeadSummonunKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class BornInChaosV1ModMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, "born_in_chaos_v1");
   public static final DeferredHolder<MobEffect, MobEffect> MAGIC_DEPLETION = REGISTRY.register("magic_depletion", () -> new MagicDepletionMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> STUN = REGISTRY.register("stun", () -> new StunMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> LIGHT_RAMPAGE = REGISTRY.register("light_rampage", () -> new LightRampageMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> MEDIUM_RAMPAGE = REGISTRY.register("medium_rampage", () -> new MediumRampageMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> STRONG_RAMPAGE = REGISTRY.register("strong_rampage", () -> new StrongRampageMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> FURIOUS_RAMPAGE = REGISTRY.register("furious_rampage", () -> new FuriousRampageMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> RAMPANT_RAMPAGE = REGISTRY.register("rampant_rampage", () -> new RampantRampageMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BLOCK_BREAK = REGISTRY.register("block_break", () -> new BlockBreakMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> CURSED_MARK = REGISTRY.register("cursed_mark", () -> new CursedMarkMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> SOUL_STRATIFICATION = REGISTRY.register(
      "soul_stratification", () -> new SoulStratificationMobEffect()
   );
   public static final DeferredHolder<MobEffect, MobEffect> INTOXICATION = REGISTRY.register("intoxication", () -> new IntoxicationMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> GAZE_OF_TERROR = REGISTRY.register("gaze_of_terror", () -> new GazeOfTerrorMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> DETONATION = REGISTRY.register("detonation", () -> new DetonationMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> SACRIFICE = REGISTRY.register("sacrifice", () -> new SacrificeMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> DOGTRUCE = REGISTRY.register("dogtruce", () -> new DogtruceMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BONE_FRACTURE = REGISTRY.register("bone_fracture", () -> new BoneFractureMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BARBEDATTACK = REGISTRY.register("barbedattack", () -> new BarbedattackMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> ROTTEN_SMELL = REGISTRY.register("rotten_smell", () -> new RottenSmellMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> INFESTATIONOF_FLIES = REGISTRY.register(
      "infestationof_flies", () -> new InfestationofFliesMobEffect()
   );
   public static final DeferredHolder<MobEffect, MobEffect> MYIASIS = REGISTRY.register("myiasis", () -> new InsipidityMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> FISH_BREATH = REGISTRY.register("fish_breath", () -> new FishBreathMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> INFERNAL_FLAME = REGISTRY.register("infernal_flame", () -> new InfernalFlameMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> LIVING_BOMB = REGISTRY.register("living_bomb", () -> new LivingBombMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> UNITY_WITH_DARKNESS = REGISTRY.register(
      "unity_with_darkness", () -> new UnityWithDarknessMobEffect()
   );
   public static final DeferredHolder<MobEffect, MobEffect> BONE_BARRIER = REGISTRY.register("bone_barrier", () -> new BoneBarrierMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> UNDEAD_SUMMONUN = REGISTRY.register("undead_summonun", () -> new UndeadSummonunMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> JAWATTACK = REGISTRY.register("jawattack", () -> new JawattackMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> STIMULATION = REGISTRY.register("stimulation", () -> new StimulationMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> STIMULATINGSURGE = REGISTRY.register("stimulatingsurge", () -> new StimulatingsurgeMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> OBSESSION = REGISTRY.register("obsession", () -> new ObsessionMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> LIFESTEAL = REGISTRY.register("lifesteal", () -> new LifestealMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> DARK_SPLASH = REGISTRY.register("dark_splash", () -> new DarkSplashMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> STRANGLEHOLD = REGISTRY.register("stranglehold", () -> new StrangleholdMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> VAMPIRIC_TOUCH = REGISTRY.register("vampiric_touch", () -> new VampiricTouchMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> LIVING_COCOON = REGISTRY.register("living_cocoon", () -> new LivingCocoonMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> LIVING_COCOON_PLAYER_SIDE = REGISTRY.register(
      "living_cocoon_player_side", () -> new LivingCocoonPlayerSideMobEffect()
   );
   public static final DeferredHolder<MobEffect, MobEffect> STUNNING_STRIKE = REGISTRY.register("stunning_strike", () -> new StunningStrikeMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> CURSEOFTHE_BOAT = REGISTRY.register("curseofthe_boat", () -> new CurseoftheBoatMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> TERRIFYING_PRESENCE = REGISTRY.register(
      "terrifying_presence", () -> new TerrifyingPresenceMobEffect()
   );
   public static final DeferredHolder<MobEffect, MobEffect> WITHER_RESISTANCE = REGISTRY.register("wither_resistance", () -> new WitherResistanceMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> INSECT_PROTECTION = REGISTRY.register("insect_protection", () -> new InsectProtectionMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> DARK_WARD = REGISTRY.register("dark_ward", () -> new DarkWardMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> SWEET_MADNESS = REGISTRY.register("sweet_madness", () -> new SweetMadnessMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BAD_FEELING = REGISTRY.register("bad_feeling", () -> new BadFeelingMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BONE_CHILLING = REGISTRY.register("bone_chilling", () -> new BoneChillingMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> ICE_BARRIER = REGISTRY.register("ice_barrier", () -> new IceBarrierMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> SNOW_STORM = REGISTRY.register("snow_storm", () -> new SnowStormMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> LOST_ITEMS = REGISTRY.register("lost_items", () -> new LostItemsMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> MINION_SUMMON = REGISTRY.register("minion_summon", () -> new MinionSummonMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> EXPERIENCE_CONTAINER = REGISTRY.register(
      "experience_container", () -> new ExperienceContainerMobEffect()
   );
   public static final DeferredHolder<MobEffect, MobEffect> RABBIT_AGILITY = REGISTRY.register("rabbit_agility", () -> new RabbitAgilityMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> STONE_PERSISTENCE = REGISTRY.register("stone_persistence", () -> new StonePersistenceMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> PREDATORS_DESIRE = REGISTRY.register("predators_desire", () -> new PredatorsDesireMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> OVERLY_HEAVY_WEAPON = REGISTRY.register(
      "overly_heavy_weapon", () -> new OverlyHeavyWeaponMobEffect()
   );

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      MobEffectInstance effectInstance = event.getEffectInstance();
      if (effectInstance != null) {
         expireEffects(event.getEntity(), effectInstance);
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      MobEffectInstance effectInstance = event.getEffectInstance();
      if (effectInstance != null) {
         expireEffects(event.getEntity(), effectInstance);
      }
   }

   private static void expireEffects(Entity entity, MobEffectInstance effectInstance) {
      if (effectInstance.getEffect().is(CURSED_MARK)) {
         CursedMarkKoghdaEffiektZakanchivaietsiaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
      } else if (effectInstance.getEffect().is(UNDEAD_SUMMONUN)) {
         UndeadSummonunKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      } else if (effectInstance.getEffect().is(DARK_SPLASH)) {
         DarkSplashPriIstiechieniiEffiektaProcedure.execute(entity);
      } else if (effectInstance.getEffect().is(SWEET_MADNESS)) {
         SweetMadnessPriIstiechieniiEffiektaProcedure.execute(entity);
      }
   }
}
