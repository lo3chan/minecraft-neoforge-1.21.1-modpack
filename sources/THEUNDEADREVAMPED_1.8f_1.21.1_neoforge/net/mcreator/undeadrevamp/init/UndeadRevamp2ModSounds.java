package net.mcreator.undeadrevamp.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UndeadRevamp2ModSounds {
   public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, "undead_revamp2");
   public static final DeferredHolder<SoundEvent, SoundEvent> BOMBER_AMBIENT = REGISTRY.register(
      "bomber_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bomber_ambient"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BOMBER_READY = REGISTRY.register(
      "bomber_ready", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bomber_ready"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPECHURT = REGISTRY.register(
      "spechurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "spechurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPECDED = REGISTRY.register(
      "specded", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "specded"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPECAMB = REGISTRY.register(
      "specamb", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "specamb"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BOMBERHURTS = REGISTRY.register(
      "bomberhurts", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bomberhurts"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BOMBERAMBT = REGISTRY.register(
      "bomberambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bomberambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BOMBERDED = REGISTRY.register(
      "bomberded", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bomberded"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPITTERAM = REGISTRY.register(
      "spitteram", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "spitteram"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPITTERHURT = REGISTRY.register(
      "spitterhurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "spitterhurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ACIDDED = REGISTRY.register(
      "acidded", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "acidded"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HORRORSHURT = REGISTRY.register(
      "horrorshurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "horrorshurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HORRORSAMBT = REGISTRY.register(
      "horrorsambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "horrorsambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HORRORSDED = REGISTRY.register(
      "horrorsded", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "horrorsded"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WHEEZERDEAD = REGISTRY.register(
      "wheezerdead", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "wheezerdead"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WHEEZERAMBT = REGISTRY.register(
      "wheezerambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "wheezerambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WHEEZEHURT = REGISTRY.register(
      "wheezehurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "wheezehurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WHEEBOMB = REGISTRY.register(
      "wheebomb", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "wheebomb"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MOONSTEP = REGISTRY.register(
      "moonstep", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "moonstep"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SUCKAMBT = REGISTRY.register(
      "suckambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "suckambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SUCKERDIES = REGISTRY.register(
      "suckerdies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "suckerdies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SUCKERHURTS = REGISTRY.register(
      "suckerhurts", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "suckerhurts"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPRAY = REGISTRY.register(
      "spray", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "spray"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> POP = REGISTRY.register(
      "pop", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "pop"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HUNTERAMBT = REGISTRY.register(
      "hunterambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "hunterambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HUNTERHURTS = REGISTRY.register(
      "hunterhurts", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "hunterhurts"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HUNTERFOOTS = REGISTRY.register(
      "hunterfoots", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "hunterfoots"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLANK = REGISTRY.register(
      "clank", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "clank"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HUNTER_FLY = REGISTRY.register(
      "hunter_fly", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "hunter_fly"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WOLFORBRUIN_ATTACK = REGISTRY.register(
      "wolforbruin_attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "wolforbruin_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WOLFANRGY = REGISTRY.register(
      "wolfanrgy", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "wolfanrgy"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ODURESKING = REGISTRY.register(
      "oduresking", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "oduresking"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMERABMT = REGISTRY.register(
      "swarmerabmt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "swarmerabmt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMERHURT = REGISTRY.register(
      "swarmerhurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "swarmerhurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMERDIES = REGISTRY.register(
      "swarmerdies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "swarmerdies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMERSTEPS = REGISTRY.register(
      "swarmersteps", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "swarmersteps"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BIDY_AMBT = REGISTRY.register(
      "bidy_ambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bidy_ambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BIDYBOOM = REGISTRY.register(
      "bidyboom", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bidyboom"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PRENANTPUKES = REGISTRY.register(
      "prenantpukes", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "prenantpukes"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> IMPACT = REGISTRY.register(
      "impact", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "impact"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> THEPREGNANT_AMBT = REGISTRY.register(
      "thepregnant_ambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "thepregnant_ambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PREGNANTHURT = REGISTRY.register(
      "pregnanthurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "pregnanthurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PREGNANTDEATH = REGISTRY.register(
      "pregnantdeath", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "pregnantdeath"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WINDBLAST = REGISTRY.register(
      "windblast", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "windblast"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PARRY = REGISTRY.register(
      "parry", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "parry"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> THERODCHARG = REGISTRY.register(
      "therodcharg", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "therodcharg"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> RODSTEP = REGISTRY.register(
      "rodstep", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "rodstep"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> THERODHURTS = REGISTRY.register(
      "therodhurts", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "therodhurts"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> RODAMBIENCE = REGISTRY.register(
      "rodambience", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "rodambience"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> RODDIES = REGISTRY.register(
      "roddies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "roddies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SUGAREWW = REGISTRY.register(
      "sugareww", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "sugareww"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ROYALHURTS = REGISTRY.register(
      "royalhurts", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "royalhurts"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ROYALDIES = REGISTRY.register(
      "royaldies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "royaldies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HEAVYAMBT = REGISTRY.register(
      "heavyambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "heavyambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HEAVYATTACK = REGISTRY.register(
      "heavyattack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "heavyattack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HEAVYROAR = REGISTRY.register(
      "heavyroar", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "heavyroar"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HEAVYDIES = REGISTRY.register(
      "heavydies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "heavydies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STONECRASHES = REGISTRY.register(
      "stonecrashes", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "stonecrashes"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HEAVYHURT = REGISTRY.register(
      "heavyhurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "heavyhurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGEREXPLODES = REGISTRY.register(
      "cloggerexplodes", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloggerexplodes"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGEREXPLODING = REGISTRY.register(
      "cloggerexploding", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloggerexploding"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGERBLEED = REGISTRY.register(
      "cloggerbleed", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloggerbleed"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HUNTERDYING = REGISTRY.register(
      "hunterdying", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "hunterdying"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOOGEREATSU = REGISTRY.register(
      "cloogereatsu", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloogereatsu"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SKEEPERAMBIANCE = REGISTRY.register(
      "skeeperambiance", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "skeeperambiance"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SNORING = REGISTRY.register(
      "snoring", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "snoring"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SOMNOLENCEDIES = REGISTRY.register(
      "somnolencedies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "somnolencedies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SOMNOLENCEAMBT = REGISTRY.register(
      "somnolenceambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "somnolenceambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SOMNOLENCEHURTS = REGISTRY.register(
      "somnolencehurts", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "somnolencehurts"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LURKERRUNNING = REGISTRY.register(
      "lurkerrunning", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lurkerrunning"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LURKERAMB = REGISTRY.register(
      "lurkeramb", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lurkeramb"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LURKERDIES = REGISTRY.register(
      "lurkerdies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lurkerdies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> NECRORINESAMBT = REGISTRY.register(
      "necrorinesambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "necrorinesambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DETAHCHARGE = REGISTRY.register(
      "detahcharge", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "detahcharge"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DEATHDYING = REGISTRY.register(
      "deathdying", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "deathdying"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> THEDUNGEONDIES = REGISTRY.register(
      "thedungeondies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "thedungeondies"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> THEDUNGEONAMBT = REGISTRY.register(
      "thedungeonambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "thedungeonambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> THEDUNGEONHURT = REGISTRY.register(
      "thedungeonhurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "thedungeonhurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SLAM_COFFIN = REGISTRY.register(
      "slam_coffin", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "slam_coffin"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FLAP = REGISTRY.register(
      "flap", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "flap"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGER_ROARING = REGISTRY.register(
      "clogger_roaring", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "clogger_roaring"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGERPAIN = REGISTRY.register(
      "cloggerpain", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloggerpain"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGERAMBT = REGISTRY.register(
      "cloggerambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloggerambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOGGERDEATH = REGISTRY.register(
      "cloggerdeath", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "cloggerdeath"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LECHERYCRAWL = REGISTRY.register(
      "lecherycrawl", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lecherycrawl"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BONECRACK = REGISTRY.register(
      "bonecrack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "bonecrack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LECHERY_AMBT = REGISTRY.register(
      "lechery_ambt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lechery_ambt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LECHERYHURT = REGISTRY.register(
      "lecheryhurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lecheryhurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LECHERYDIES = REGISTRY.register(
      "lecherydies", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "lecherydies"))
   );
}
