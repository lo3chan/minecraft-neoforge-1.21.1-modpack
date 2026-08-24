package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.potion.AciddecayMobEffect;
import net.mcreator.undeadrevamp.potion.AnimationtestMobEffect;
import net.mcreator.undeadrevamp.potion.AntiflyinhMobEffect;
import net.mcreator.undeadrevamp.potion.BomberexplodingMobEffect;
import net.mcreator.undeadrevamp.potion.BombtickingMobEffect;
import net.mcreator.undeadrevamp.potion.BriskMobEffect;
import net.mcreator.undeadrevamp.potion.BrokentankMobEffect;
import net.mcreator.undeadrevamp.potion.CurseofphamoreMobEffect;
import net.mcreator.undeadrevamp.potion.DebuffresistanceMobEffect;
import net.mcreator.undeadrevamp.potion.DungeonbuffMobEffect;
import net.mcreator.undeadrevamp.potion.ExplosivehandMobEffect;
import net.mcreator.undeadrevamp.potion.FlyingsppedupMobEffect;
import net.mcreator.undeadrevamp.potion.FulminationMobEffect;
import net.mcreator.undeadrevamp.potion.GooedMobEffect;
import net.mcreator.undeadrevamp.potion.HoardmaneMobEffect;
import net.mcreator.undeadrevamp.potion.HoneysplatMobEffect;
import net.mcreator.undeadrevamp.potion.MoonflowersscentMobEffect;
import net.mcreator.undeadrevamp.potion.ReekofmagicMobEffect;
import net.mcreator.undeadrevamp.potion.SleepwalkingMobEffect;
import net.mcreator.undeadrevamp.potion.TankleakMobEffect;
import net.mcreator.undeadrevamp.potion.ToxicfumesMobEffect;
import net.mcreator.undeadrevamp.potion.TrypanosomiasisMobEffect;
import net.mcreator.undeadrevamp.potion.UndeadstunsMobEffect;
import net.mcreator.undeadrevamp.potion.WitherflameMobEffect;
import net.mcreator.undeadrevamp.procedures.AnimationtestEffectStartedappliedProcedure;
import net.mcreator.undeadrevamp.procedures.AntiflyinhEffectExpiresProcedure;
import net.mcreator.undeadrevamp.procedures.BombtickingEffectExpiresProcedure;
import net.mcreator.undeadrevamp.procedures.FlyingsppedupEffectExpiresProcedure;
import net.mcreator.undeadrevamp.procedures.FulminationEffectExpiresProcedure;
import net.mcreator.undeadrevamp.procedures.SleepwalkingEffectExpiresProcedure;
import net.mcreator.undeadrevamp.procedures.UndeadstunsEffectExpiresProcedure;
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
public class UndeadRevamp2ModMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, "undead_revamp2");
   public static final DeferredHolder<MobEffect, MobEffect> BOMBEREXPLODING = REGISTRY.register("bomberexploding", () -> new BomberexplodingMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> GOOED = REGISTRY.register("gooed", () -> new GooedMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> ACIDDECAY = REGISTRY.register("aciddecay", () -> new AciddecayMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BROKENTANK = REGISTRY.register("brokentank", () -> new BrokentankMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> TANKLEAK = REGISTRY.register("tankleak", () -> new TankleakMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> ANIMATIONTEST = REGISTRY.register("animationtest", () -> new AnimationtestMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> TOXICFUMES = REGISTRY.register("toxicfumes", () -> new ToxicfumesMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> MOONFLOWERSSCENT = REGISTRY.register("moonflowersscent", () -> new MoonflowersscentMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> FLYINGSPPEDUP = REGISTRY.register("flyingsppedup", () -> new FlyingsppedupMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> UNDEADSTUNS = REGISTRY.register("undeadstuns", () -> new UndeadstunsMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> DEBUFFRESISTANCE = REGISTRY.register("debuffresistance", () -> new DebuffresistanceMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> REEKOFMAGIC = REGISTRY.register("reekofmagic", () -> new ReekofmagicMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BOMBTICKING = REGISTRY.register("bombticking", () -> new BombtickingMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> HONEYSPLAT = REGISTRY.register("honeysplat", () -> new HoneysplatMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> BRISK = REGISTRY.register("brisk", () -> new BriskMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> CURSEOFPHAMORE = REGISTRY.register("curseofphamore", () -> new CurseofphamoreMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> TRYPANOSOMIASIS = REGISTRY.register("trypanosomiasis", () -> new TrypanosomiasisMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> SLEEPWALKING = REGISTRY.register("sleepwalking", () -> new SleepwalkingMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> DUNGEONBUFF = REGISTRY.register("dungeonbuff", () -> new DungeonbuffMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> WITHERFLAME = REGISTRY.register("witherflame", () -> new WitherflameMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> HOARDMANE = REGISTRY.register("hoardmane", () -> new HoardmaneMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> ANTIFLYINH = REGISTRY.register("antiflyinh", () -> new AntiflyinhMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> FULMINATION = REGISTRY.register("fulmination", () -> new FulminationMobEffect());
   public static final DeferredHolder<MobEffect, MobEffect> EXPLOSIVEHAND = REGISTRY.register("explosivehand", () -> new ExplosivehandMobEffect());

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
      if (effectInstance.getEffect().is(ANIMATIONTEST)) {
         AnimationtestEffectStartedappliedProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      } else if (effectInstance.getEffect().is(FLYINGSPPEDUP)) {
         FlyingsppedupEffectExpiresProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      } else if (effectInstance.getEffect().is(UNDEADSTUNS)) {
         UndeadstunsEffectExpiresProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      } else if (effectInstance.getEffect().is(BOMBTICKING)) {
         BombtickingEffectExpiresProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      } else if (effectInstance.getEffect().is(SLEEPWALKING)) {
         SleepwalkingEffectExpiresProcedure.execute(entity);
      } else if (effectInstance.getEffect().is(ANTIFLYINH)) {
         AntiflyinhEffectExpiresProcedure.execute(entity);
      } else if (effectInstance.getEffect().is(FULMINATION)) {
         FulminationEffectExpiresProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      }
   }
}
