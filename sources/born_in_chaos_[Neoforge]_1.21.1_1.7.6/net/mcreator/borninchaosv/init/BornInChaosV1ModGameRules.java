package net.mcreator.borninchaosv.init;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.Key;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class BornInChaosV1ModGameRules {
   public static Key<BooleanValue> GENERATIONOFINFECTEDDIAMONDS;
   public static Key<BooleanValue> THEAPPEARANCEOFTHENIGHTMARESTALKER;
   public static Key<BooleanValue> DECREPITSKELETONSPAWN;
   public static Key<BooleanValue> SKELETONBOMBSPAWN;
   public static Key<BooleanValue> DECAYINGZOMBIESPAWN;
   public static Key<BooleanValue> BABYSKELETONSPAWN;
   public static Key<BooleanValue> ZOMBIESINABARRELSPAWN;
   public static Key<BooleanValue> PHANTOMCREEPERSPAWN;
   public static Key<BooleanValue> DOORKNIGHTSPAWN;
   public static Key<BooleanValue> RESTLESSSPIRITSPAWN;
   public static Key<BooleanValue> SKELETONTHRASHERSPAWN;
   public static Key<BooleanValue> FIRELIGHTSPAWN;
   public static Key<BooleanValue> BONESCALLERSPAWN;
   public static Key<BooleanValue> BONEIMPSPAWN;
   public static Key<BooleanValue> DARKVORTEXSPAWN;
   public static Key<BooleanValue> DREADHOUNDSPAWN;
   public static Key<BooleanValue> FALLENCHAOSKNIGHTSPAWN;
   public static Key<BooleanValue> SERPUMPKINHEADSPAWN;
   public static Key<BooleanValue> ZOMBIECLOWNSPAWN;
   public static Key<BooleanValue> SPIRITGUIDESPAWN;
   public static Key<BooleanValue> DISAPPEARANCEOFSPIRITSUNDERTHESUN;
   public static Key<BooleanValue> SOULSTRATIFICATIONEFFECT;
   public static Key<BooleanValue> CORPSE_FLY_SPAWN;
   public static Key<BooleanValue> BLOODY_GADFLY_SPAWN;
   public static Key<BooleanValue> CORPSE_FISH_SPAWN;
   public static Key<BooleanValue> ZOMBIE_FISHERMAN_SPAWN;
   public static Key<BooleanValue> THORNSHELL_CRAB_SPAWN;
   public static Key<BooleanValue> GLUTTON_FISH_SPAWN;
   public static Key<BooleanValue> ZOMBIE_BRUISER_SPAWN;
   public static Key<BooleanValue> MAGGOTS_APPEARANCE;
   public static Key<BooleanValue> ZOMBIE_LUMBERJACK_SPAWN;
   public static Key<BooleanValue> MISSIONARY_SPAWN;
   public static Key<BooleanValue> SPIRIT_OF_CHAOS_SPAWN;
   public static Key<BooleanValue> LIFESTEALER_SPAWN;
   public static Key<BooleanValue> BABY_SPIDER_SPAWN;
   public static Key<BooleanValue> MOTHER_SPIDER_SPAWN;
   public static Key<BooleanValue> KRAMPUS_SPAWN;
   public static Key<BooleanValue> NAUGHTINESS_MECHANICS;
   public static Key<BooleanValue> SEASONAL_EVENTS;
   public static Key<BooleanValue> HALLOWEEN_EVENT;
   public static Key<BooleanValue> CHILLING_HORROR_EVENT;
   public static Key<BooleanValue> CHAOTIC_SPRING_EVENT;

   @SubscribeEvent
   public static void registerGameRules(FMLCommonSetupEvent event) {
      GENERATIONOFINFECTEDDIAMONDS = GameRules.register("generationofInfectedDiamonds", Category.SPAWNING, BooleanValue.create(true));
      THEAPPEARANCEOFTHENIGHTMARESTALKER = GameRules.register("theappearanceoftheNightmareStalker", Category.SPAWNING, BooleanValue.create(true));
      DECREPITSKELETONSPAWN = GameRules.register("decrepitSkeletonSpawn", Category.SPAWNING, BooleanValue.create(true));
      SKELETONBOMBSPAWN = GameRules.register("skeletonBombSpawn", Category.SPAWNING, BooleanValue.create(true));
      DECAYINGZOMBIESPAWN = GameRules.register("decayingZombieSpawn", Category.SPAWNING, BooleanValue.create(true));
      BABYSKELETONSPAWN = GameRules.register("babySkeletonSpawn", Category.SPAWNING, BooleanValue.create(true));
      ZOMBIESINABARRELSPAWN = GameRules.register("zombiesinaBarrelSpawn", Category.MOBS, BooleanValue.create(true));
      PHANTOMCREEPERSPAWN = GameRules.register("phantomCreeperSpawn", Category.SPAWNING, BooleanValue.create(true));
      DOORKNIGHTSPAWN = GameRules.register("doorKnightSpawn", Category.SPAWNING, BooleanValue.create(true));
      RESTLESSSPIRITSPAWN = GameRules.register("restlessSpiritSpawn", Category.SPAWNING, BooleanValue.create(true));
      SKELETONTHRASHERSPAWN = GameRules.register("skeletonThrasherSpawn", Category.SPAWNING, BooleanValue.create(true));
      FIRELIGHTSPAWN = GameRules.register("firelightSpawn", Category.SPAWNING, BooleanValue.create(true));
      BONESCALLERSPAWN = GameRules.register("bonescallerSpawn", Category.SPAWNING, BooleanValue.create(true));
      BONEIMPSPAWN = GameRules.register("boneImpSpawn", Category.SPAWNING, BooleanValue.create(true));
      DARKVORTEXSPAWN = GameRules.register("darkVortexSpawn", Category.SPAWNING, BooleanValue.create(true));
      DREADHOUNDSPAWN = GameRules.register("dreadHoundSpawn", Category.SPAWNING, BooleanValue.create(true));
      FALLENCHAOSKNIGHTSPAWN = GameRules.register("fallenChaosKnightSpawn", Category.SPAWNING, BooleanValue.create(true));
      SERPUMPKINHEADSPAWN = GameRules.register("serPumpkinheadSpawn", Category.SPAWNING, BooleanValue.create(true));
      ZOMBIECLOWNSPAWN = GameRules.register("zombieClownSpawn", Category.SPAWNING, BooleanValue.create(true));
      SPIRITGUIDESPAWN = GameRules.register("spiritGuideSpawn", Category.SPAWNING, BooleanValue.create(true));
      DISAPPEARANCEOFSPIRITSUNDERTHESUN = GameRules.register("disappearanceofSpiritsUndertheSun", Category.MOBS, BooleanValue.create(true));
      SOULSTRATIFICATIONEFFECT = GameRules.register("soulStratificationEffect", Category.PLAYER, BooleanValue.create(true));
      CORPSE_FLY_SPAWN = GameRules.register("corpseFlySpawn", Category.SPAWNING, BooleanValue.create(true));
      BLOODY_GADFLY_SPAWN = GameRules.register("bloodyGadflySpawn", Category.SPAWNING, BooleanValue.create(true));
      CORPSE_FISH_SPAWN = GameRules.register("corpseFishSpawn", Category.SPAWNING, BooleanValue.create(true));
      ZOMBIE_FISHERMAN_SPAWN = GameRules.register("zombieFishermanSpawn", Category.SPAWNING, BooleanValue.create(true));
      THORNSHELL_CRAB_SPAWN = GameRules.register("thornshellCrabSpawn", Category.SPAWNING, BooleanValue.create(true));
      GLUTTON_FISH_SPAWN = GameRules.register("gluttonFishSpawn", Category.SPAWNING, BooleanValue.create(true));
      ZOMBIE_BRUISER_SPAWN = GameRules.register("zombieBruiserSpawn", Category.SPAWNING, BooleanValue.create(true));
      MAGGOTS_APPEARANCE = GameRules.register("maggotsAppearance", Category.MOBS, BooleanValue.create(true));
      ZOMBIE_LUMBERJACK_SPAWN = GameRules.register("zombieLumberjackSpawn", Category.SPAWNING, BooleanValue.create(true));
      MISSIONARY_SPAWN = GameRules.register("missionarySpawn", Category.SPAWNING, BooleanValue.create(true));
      SPIRIT_OF_CHAOS_SPAWN = GameRules.register("spiritOfChaosSpawn", Category.SPAWNING, BooleanValue.create(true));
      LIFESTEALER_SPAWN = GameRules.register("lifestealerSpawn", Category.SPAWNING, BooleanValue.create(true));
      BABY_SPIDER_SPAWN = GameRules.register("babySpiderSpawn", Category.SPAWNING, BooleanValue.create(true));
      MOTHER_SPIDER_SPAWN = GameRules.register("motherSpiderSpawn", Category.SPAWNING, BooleanValue.create(true));
      KRAMPUS_SPAWN = GameRules.register("krampusSpawn", Category.SPAWNING, BooleanValue.create(true));
      NAUGHTINESS_MECHANICS = GameRules.register("naughtinessMechanics", Category.PLAYER, BooleanValue.create(true));
      SEASONAL_EVENTS = GameRules.register("seasonalEvents", Category.PLAYER, BooleanValue.create(true));
      HALLOWEEN_EVENT = GameRules.register("halloweenEvent", Category.PLAYER, BooleanValue.create(false));
      CHILLING_HORROR_EVENT = GameRules.register("chillingHorrorEvent", Category.PLAYER, BooleanValue.create(false));
      CHAOTIC_SPRING_EVENT = GameRules.register("chaoticSpringEvent", Category.PLAYER, BooleanValue.create(false));
   }
}
