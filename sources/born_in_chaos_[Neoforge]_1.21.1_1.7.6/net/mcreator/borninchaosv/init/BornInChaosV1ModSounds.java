package net.mcreator.borninchaosv.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BornInChaosV1ModSounds {
   public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, "born_in_chaos_v1");
   public static final DeferredHolder<SoundEvent, SoundEvent> DFJN = REGISTRY.register(
      "dfjn", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "dfjn"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DOOR1 = REGISTRY.register(
      "door1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "door1"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DOOR2 = REGISTRY.register(
      "door2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "door2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SERPUMPKINHEADM = REGISTRY.register(
      "serpumpkinheadm", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "serpumpkinheadm"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> RESTLESS_SPIRIT_IDLE = REGISTRY.register(
      "restless_spirit_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "restless_spirit_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> RESTLESS_SPIRIT_HURT = REGISTRY.register(
      "restless_spirit_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "restless_spirit_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> RESTLESS_SPIRIT_DEATH = REGISTRY.register(
      "restless_spirit_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "restless_spirit_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SKELETON_TRASHER_STEP = REGISTRY.register(
      "skeleton_trasher_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "skeleton_trasher_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SKELETON_TRASHER_ATTACK = REGISTRY.register(
      "skeleton_trasher_attack",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "skeleton_trasher_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SKELETON_TRASHER_BLOCK1 = REGISTRY.register(
      "skeleton_trasher_block1",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "skeleton_trasher_block1"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SKELETON_TRASHER_BLOCK2 = REGISTRY.register(
      "skeleton_trasher_block2",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "skeleton_trasher_block2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SKELETON_TRASHER_BLOCK3 = REGISTRY.register(
      "skeleton_trasher_block3",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "skeleton_trasher_block3"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> NIGHTMARE_STALKER_ROAR = REGISTRY.register(
      "nightmare_stalker_roar", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "nightmare_stalker_roar"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPIRIT_IDLE = REGISTRY.register(
      "spirit_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "spirit_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_DEATH = REGISTRY.register(
      "stalker_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_HURT1 = REGISTRY.register(
      "vortex_hurt1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_hurt1"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_HURT2 = REGISTRY.register(
      "vortex_hurt2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_hurt2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_IDLE1 = REGISTRY.register(
      "vortex_idle1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_idle1"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_ATTACK = REGISTRY.register(
      "fallen_attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fallen_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_CURSE_MARK = REGISTRY.register(
      "fallen_curse_mark", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fallen_curse_mark"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_DEATH = REGISTRY.register(
      "fallen_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fallen_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_HURT = REGISTRY.register(
      "fallen_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fallen_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_IDLE = REGISTRY.register(
      "fallen_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fallen_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_STEP = REGISTRY.register(
      "fallen_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fallen_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PERSECUTOR_DEATH = REGISTRY.register(
      "persecutor_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "persecutor_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PERSECUTOR_HURT = REGISTRY.register(
      "persecutor_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "persecutor_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PERSECUTOR_IDLE = REGISTRY.register(
      "persecutor_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "persecutor_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PERSECUTOR_SCREAM = REGISTRY.register(
      "persecutor_scream", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "persecutor_scream"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_DEATH2 = REGISTRY.register(
      "stalker_death2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_death2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_DEATH1 = REGISTRY.register(
      "vortex_death1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_death1"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_IDLE2 = REGISTRY.register(
      "vortex_idle2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_idle2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_HURT11 = REGISTRY.register(
      "vortex_hurt11", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_hurt11"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX_HURT21 = REGISTRY.register(
      "vortex_hurt21", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "vortex_hurt21"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HAH = REGISTRY.register(
      "hah", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "hah"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HAHA = REGISTRY.register(
      "haha", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "haha"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DOORBLOKC = REGISTRY.register(
      "doorblokc", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "doorblokc"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DOORBLOKC2 = REGISTRY.register(
      "doorblokc2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "doorblokc2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HALLOWEEN = REGISTRY.register(
      "halloween", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "halloween"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DOORSDISC = REGISTRY.register(
      "doorsdisc", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "doorsdisc"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_ROAR = REGISTRY.register(
      "stalker_roar", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_roar"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_DEAD = REGISTRY.register(
      "stalker_dead", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_dead"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_STEP = REGISTRY.register(
      "stalker_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_HIT = REGISTRY.register(
      "stalker_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_hit"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_HURT = REGISTRY.register(
      "stalker_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKIN_SPIRIT_HURT = REGISTRY.register(
      "pumpkin_spirit_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkin_spirit_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKIN_SPIRIT_DEATH = REGISTRY.register(
      "pumpkin_spirit_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkin_spirit_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STALKER_ROAR_DISTANT = REGISTRY.register(
      "stalker_roar_distant", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stalker_roar_distant"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HOUND_DEATH = REGISTRY.register(
      "hound_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "hound_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HOUND_HIT = REGISTRY.register(
      "hound_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "hound_hit"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HOUND_AMBIENT = REGISTRY.register(
      "hound_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "hound_ambient"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HOUND_ATTACK = REGISTRY.register(
      "hound_attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "hound_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SEARED_SPIRIT_DEATH = REGISTRY.register(
      "seared_spirit_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "seared_spirit_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HOUNDTRAP = REGISTRY.register(
      "houndtrap", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "houndtrap"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DARK_WARLBLADE_ATAK = REGISTRY.register(
      "dark_warlblade_atak", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "dark_warlblade_atak"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STOMACH_OPEN = REGISTRY.register(
      "stomach_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "stomach_open"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOWN_STEP = REGISTRY.register(
      "clown_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "clown_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOWN_HURT = REGISTRY.register(
      "clown_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "clown_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CLOWN_DEATH = REGISTRY.register(
      "clown_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "clown_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ZOMBIE_CLOWN_ATTACK = REGISTRY.register(
      "zombie_clown_attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "zombie_clown_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CORPSE_FLY_AMBIENT = REGISTRY.register(
      "corpse_fly_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "corpse_fly_ambient"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CORPSE_FLY_DEATH = REGISTRY.register(
      "corpse_fly_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "corpse_fly_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CORPSE_FLY_HURT = REGISTRY.register(
      "corpse_fly_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "corpse_fly_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GADFLY_IDLE = REGISTRY.register(
      "gadfly_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "gadfly_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GADFLY_HURT = REGISTRY.register(
      "gadfly_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "gadfly_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GADFLY_DEATH = REGISTRY.register(
      "gadfly_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "gadfly_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CORPSE_FLY_AMBIENT2 = REGISTRY.register(
      "corpse_fly_ambient2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "corpse_fly_ambient2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CORPSE_FLY_HURT2 = REGISTRY.register(
      "corpse_fly_hurt2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "corpse_fly_hurt2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CORPSE_FLY_DEATH2 = REGISTRY.register(
      "corpse_fly_death2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "corpse_fly_death2"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FISH_SLAP = REGISTRY.register(
      "fish_slap", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fish_slap"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMER_IDLE = REGISTRY.register(
      "swarmer_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "swarmer_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMER_HURT = REGISTRY.register(
      "swarmer_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "swarmer_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SWARMER_DEATH = REGISTRY.register(
      "swarmer_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "swarmer_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CRAB_DEATH = REGISTRY.register(
      "crab_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "crab_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CRAB_IDLE = REGISTRY.register(
      "crab_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "crab_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CRAB_STEP = REGISTRY.register(
      "crab_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "crab_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CRAB_HURT = REGISTRY.register(
      "crab_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "crab_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GLUTTON_FISH_AMBIENT = REGISTRY.register(
      "glutton_fish_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "glutton_fish_ambient"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GLUTTON_FISH_ATTACK = REGISTRY.register(
      "glutton_fish_attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "glutton_fish_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GLUTTON_FISH_HURT = REGISTRY.register(
      "glutton_fish_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "glutton_fish_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> GLUTTON_FISH_DEATH = REGISTRY.register(
      "glutton_fish_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "glutton_fish_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> FLY_MOVE = REGISTRY.register(
      "fly_move", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "fly_move"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BRUTE_ZOMBIE_ATTACK = REGISTRY.register(
      "brute_zombie_attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "brute_zombie_attack"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BRUTE_ZOMBIE_STEP = REGISTRY.register(
      "brute_zombie_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "brute_zombie_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ZOMBIE_BRUTE_DEATH = REGISTRY.register(
      "zombie_brute_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "zombie_brute_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ZOMBIE_BRUTE_HURT = REGISTRY.register(
      "zombie_brute_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "zombie_brute_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ZOMBIE_BRUTE_IDLE = REGISTRY.register(
      "zombie_brute_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "zombie_brute_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKINHEAD_RELOAD = REGISTRY.register(
      "pumpkinhead_reload", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkinhead_reload"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKINHEAD_GUN_FIRE = REGISTRY.register(
      "pumpkinhead_gun_fire", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkinhead_gun_fire"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKINHEAD_BOMB_CURSE = REGISTRY.register(
      "pumpkinhead_bomb_curse", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkinhead_bomb_curse"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CHARM_OF_STEALTH_USE = REGISTRY.register(
      "charm_of_stealth_use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "charm_of_stealth_use"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CHARM_OF_ENDURANCE_USE = REGISTRY.register(
      "charm_of_endurance_use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "charm_of_endurance_use"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CHARM_OF_STRENGHT_USE = REGISTRY.register(
      "charm_of_strenght_use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "charm_of_strenght_use"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CHARM_OF_RAGE_USE = REGISTRY.register(
      "charm_of_rage_use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "charm_of_rage_use"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CHARM_OF_PROTECTION_USE = REGISTRY.register(
      "charm_of_protection_use",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "charm_of_protection_use"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_DEATH = REGISTRY.register(
      "missionary_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_HURT = REGISTRY.register(
      "missionary_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_IDLE = REGISTRY.register(
      "missionary_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_SPAWN_MOBS = REGISTRY.register(
      "missionary_spawn_mobs", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_spawn_mobs"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_STEP = REGISTRY.register(
      "missionary_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_TELEPORT = REGISTRY.register(
      "missionary_teleport", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_teleport"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_SHOOT = REGISTRY.register(
      "missionary_shoot", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_shoot"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> OBSESSION = REGISTRY.register(
      "obsession", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "obsession"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CHAOS_SPIRIT_HAUNT = REGISTRY.register(
      "chaos_spirit_haunt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "chaos_spirit_haunt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_ALERT = REGISTRY.register(
      "missionary_alert", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_alert"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> NSSTEP = REGISTRY.register(
      "nsstep", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "nsstep"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STEP5 = REGISTRY.register(
      "step5", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "step5"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> STEP_ST = REGISTRY.register(
      "step_st", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "step_st"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LIFESTEALER_HURT = REGISTRY.register(
      "lifestealer_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "lifestealer_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LIFESTEALER_IDLE = REGISTRY.register(
      "lifestealer_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "lifestealer_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LIFESTEALER_DEATH = REGISTRY.register(
      "lifestealer_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "lifestealer_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LIFESTEALER_SCREAM = REGISTRY.register(
      "lifestealer_scream", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "lifestealer_scream"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LIFESTEALER_SCREAM_AP = REGISTRY.register(
      "lifestealer_scream_ap", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "lifestealer_scream_ap"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_STAFF_SHOOT = REGISTRY.register(
      "magic_staff_shoot", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "magic_staff_shoot"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKIN_STAFF_SHOOT = REGISTRY.register(
      "pumpkin_staff_shoot", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkin_staff_shoot"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PUMPKIN_HIT = REGISTRY.register(
      "pumpkin_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "pumpkin_hit"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPIDER_SPLASH = REGISTRY.register(
      "spider_splash", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "spider_splash"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MOTHER_SPIDER_STEP = REGISTRY.register(
      "mother_spider_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "mother_spider_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MOTHER_SPIDER_IDLE = REGISTRY.register(
      "mother_spider_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "mother_spider_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MISSIONARY_STUN = REGISTRY.register(
      "missionary_stun", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "missionary_stun"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MOTHER_SPIDER_HURT = REGISTRY.register(
      "mother_spider_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "mother_spider_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SPIDER_MOTHER_DEATH = REGISTRY.register(
      "spider_mother_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "spider_mother_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HAHA_LORD = REGISTRY.register(
      "haha_lord", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "haha_lord"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BONK = REGISTRY.register(
      "bonk", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "bonk"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BONK_HIT = REGISTRY.register(
      "bonk_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "bonk_hit"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_DEATH = REGISTRY.register(
      "krampus_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_ROAR = REGISTRY.register(
      "krampus_roar", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_roar"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_IDLE = REGISTRY.register(
      "krampus_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_idle"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_BLOW = REGISTRY.register(
      "krampus_blow", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_blow"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_HORN_USE = REGISTRY.register(
      "krampus_horn_use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_horn_use"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_ALERT_FAR = REGISTRY.register(
      "krampus_alert_far", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_alert_far"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_ALERT = REGISTRY.register(
      "krampus_alert", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_alert"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_ALERT_CLOSE = REGISTRY.register(
      "krampus_alert_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_alert_close"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_HURT = REGISTRY.register(
      "krampus_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> KRAMPUS_STEP = REGISTRY.register(
      "krampus_step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "krampus_step"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MINION_BLOW = REGISTRY.register(
      "minion_blow", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "minion_blow"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MINION_DEATH = REGISTRY.register(
      "minion_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "minion_death"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MINION_HURT = REGISTRY.register(
      "minion_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "minion_hurt"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> MINION_IDLE = REGISTRY.register(
      "minion_idle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "minion_idle"))
   );
}
