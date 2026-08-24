package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.NoOpAction;
import com.iafenvoy.origins.data.action.SimpleActions;
import com.iafenvoy.origins.data.action.builtin.entity.ActionOnSetAction;
import com.iafenvoy.origins.data.action.builtin.entity.AddVelocityAction;
import com.iafenvoy.origins.data.action.builtin.entity.AddXPAction;
import com.iafenvoy.origins.data.action.builtin.entity.ApplyEffectAction;
import com.iafenvoy.origins.data.action.builtin.entity.AwardStatAction;
import com.iafenvoy.origins.data.action.builtin.entity.BlockActionAction;
import com.iafenvoy.origins.data.action.builtin.entity.BlockActionAtAction;
import com.iafenvoy.origins.data.action.builtin.entity.ChangeResourceAction;
import com.iafenvoy.origins.data.action.builtin.entity.ClearSetAction;
import com.iafenvoy.origins.data.action.builtin.entity.CraftingTableAction;
import com.iafenvoy.origins.data.action.builtin.entity.DamageAction;
import com.iafenvoy.origins.data.action.builtin.entity.DropInventoryAction;
import com.iafenvoy.origins.data.action.builtin.entity.EmitGameEventAction;
import com.iafenvoy.origins.data.action.builtin.entity.EnderChestAction;
import com.iafenvoy.origins.data.action.builtin.entity.EquippedItemActionAction;
import com.iafenvoy.origins.data.action.builtin.entity.ExecuteCommandAction;
import com.iafenvoy.origins.data.action.builtin.entity.ExhaustAction;
import com.iafenvoy.origins.data.action.builtin.entity.ExplodeAction;
import com.iafenvoy.origins.data.action.builtin.entity.FeedAction;
import com.iafenvoy.origins.data.action.builtin.entity.FireProjectileAction;
import com.iafenvoy.origins.data.action.builtin.entity.GainAirAction;
import com.iafenvoy.origins.data.action.builtin.entity.GiveItemAction;
import com.iafenvoy.origins.data.action.builtin.entity.GrantAdvancementAction;
import com.iafenvoy.origins.data.action.builtin.entity.GrantPowerAction;
import com.iafenvoy.origins.data.action.builtin.entity.HealAction;
import com.iafenvoy.origins.data.action.builtin.entity.ModifyDeathTicksAction;
import com.iafenvoy.origins.data.action.builtin.entity.ModifyInventoryAction;
import com.iafenvoy.origins.data.action.builtin.entity.ModifyResourceAction;
import com.iafenvoy.origins.data.action.builtin.entity.PassengerActionAction;
import com.iafenvoy.origins.data.action.builtin.entity.PlaySoundAction;
import com.iafenvoy.origins.data.action.builtin.entity.RandomTeleportAction;
import com.iafenvoy.origins.data.action.builtin.entity.RaycastAction;
import com.iafenvoy.origins.data.action.builtin.entity.RemoveEffectAction;
import com.iafenvoy.origins.data.action.builtin.entity.ReplaceInventoryAction;
import com.iafenvoy.origins.data.action.builtin.entity.ResetStatAction;
import com.iafenvoy.origins.data.action.builtin.entity.RevokeAdvancementAction;
import com.iafenvoy.origins.data.action.builtin.entity.RevokePowerAction;
import com.iafenvoy.origins.data.action.builtin.entity.RidingActionAction;
import com.iafenvoy.origins.data.action.builtin.entity.SelectorActionAction;
import com.iafenvoy.origins.data.action.builtin.entity.SetFallDistanceAction;
import com.iafenvoy.origins.data.action.builtin.entity.SetNoGravityAction;
import com.iafenvoy.origins.data.action.builtin.entity.SetOnFireAction;
import com.iafenvoy.origins.data.action.builtin.entity.SpawnEffectCloudAction;
import com.iafenvoy.origins.data.action.builtin.entity.SpawnEntityAction;
import com.iafenvoy.origins.data.action.builtin.entity.SpawnParticlesAction;
import com.iafenvoy.origins.data.action.builtin.entity.SwingHandAction;
import com.iafenvoy.origins.data.action.builtin.entity.ToggleAction;
import com.iafenvoy.origins.data.action.builtin.entity.TriggerCooldownAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.AndAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.ChanceAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.ChoiceAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.DelayAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.IfElseAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.IfElseListAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.RegionApplyAction;
import com.iafenvoy.origins.data.action.builtin.entity.meta.SideAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityActions {
   public static final DeferredRegister<MapCodec<? extends EntityAction>> REGISTRY = DeferredRegister.create(ActionRegistries.ENTITY_ACTION, "origins");
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<NoOpAction>> NO_OP = REGISTRY.register("no_op", () -> NoOpAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ActionOnSetAction>> ACTION_ON_SET = REGISTRY.register(
      "action_on_set", () -> ActionOnSetAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AddVelocityAction>> ADD_VELOCITY = REGISTRY.register(
      "add_velocity", () -> AddVelocityAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AddXPAction>> ADD_XP = REGISTRY.register("add_xp", () -> AddXPAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ApplyEffectAction>> APPLY_EFFECT = REGISTRY.register(
      "apply_effect", () -> ApplyEffectAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AwardStatAction>> AWARD_STAT = REGISTRY.register(
      "award_stat", () -> AwardStatAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<BlockActionAction>> BLOCK_ACTION = REGISTRY.register(
      "block_action", () -> BlockActionAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<BlockActionAtAction>> BLOCK_ACTION_AT = REGISTRY.register(
      "block_action_at", () -> BlockActionAtAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ChangeResourceAction>> CHANGE_RESOURCE = REGISTRY.register(
      "change_resource", () -> ChangeResourceAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ClearSetAction>> CLEAR_SET = REGISTRY.register(
      "clear_set", () -> ClearSetAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<CraftingTableAction>> CRAFTING_TABLE = REGISTRY.register(
      "crafting_table", () -> CraftingTableAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DamageAction>> DAMAGE = REGISTRY.register("damage", () -> DamageAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<? extends EntityAction>> DISMOUNT = REGISTRY.register(
      "dismount", () -> SimpleActions.createEntity(Entity::stopRiding)
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DropInventoryAction>> DROP_INVENTORY = REGISTRY.register(
      "drop_inventory", () -> DropInventoryAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EmitGameEventAction>> EMIT_GAME_EVENT = REGISTRY.register(
      "emit_game_event", () -> EmitGameEventAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EnderChestAction>> ENDER_CHEST = REGISTRY.register(
      "ender_chest", () -> EnderChestAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EquippedItemActionAction>> EQUIPPED_ITEM_ACTION = REGISTRY.register(
      "equipped_item_action", () -> EquippedItemActionAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExecuteCommandAction>> EXECUTE_COMMAND = REGISTRY.register(
      "execute_command", () -> ExecuteCommandAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExhaustAction>> EXHAUST = REGISTRY.register(
      "exhaust", () -> ExhaustAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExplodeAction>> EXPLODE = REGISTRY.register(
      "explode", () -> ExplodeAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<? extends EntityAction>> EXTINGUISH = REGISTRY.register(
      "extinguish", () -> SimpleActions.createEntity(Entity::extinguishFire)
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<FeedAction>> FEED = REGISTRY.register("feed", () -> FeedAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<FireProjectileAction>> FIRE_PROJECTILE = REGISTRY.register(
      "fire_projectile", () -> FireProjectileAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<GainAirAction>> GAIN_AIR = REGISTRY.register(
      "gain_air", () -> GainAirAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<GiveItemAction>> GIVE_ITEM = REGISTRY.register(
      "give_item", () -> GiveItemAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<GrantAdvancementAction>> GRANT_ADVANCEMENT = REGISTRY.register(
      "grant_advancement", () -> GrantAdvancementAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<GrantPowerAction>> GRANT_POWER = REGISTRY.register(
      "grant_power", () -> GrantPowerAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<HealAction>> HEAL = REGISTRY.register("heal", () -> HealAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ModifyDeathTicksAction>> MODIFY_DEATH_TICKS = REGISTRY.register(
      "modify_death_ticks", () -> ModifyDeathTicksAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ModifyInventoryAction>> MODIFY_INVENTORY = REGISTRY.register(
      "modify_inventory", () -> ModifyInventoryAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ModifyResourceAction>> MODIFY_RESOURCE = REGISTRY.register(
      "modify_resource", () -> ModifyResourceAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<PassengerActionAction>> PASSENGER_ACTION = REGISTRY.register(
      "passenger_action", () -> PassengerActionAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<PlaySoundAction>> PLAY_SOUND = REGISTRY.register(
      "play_sound", () -> PlaySoundAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RandomTeleportAction>> RANDOM_TELEPORT = REGISTRY.register(
      "random_teleport", () -> RandomTeleportAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RaycastAction>> RAYCAST = REGISTRY.register(
      "raycast", () -> RaycastAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RemoveEffectAction>> REMOVE_EFFECT = REGISTRY.register(
      "remove_effect", () -> RemoveEffectAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ReplaceInventoryAction>> REPLACE_INVENTORY = REGISTRY.register(
      "replace_inventory", () -> ReplaceInventoryAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ResetStatAction>> RESET_STAT = REGISTRY.register(
      "reset_stat", () -> ResetStatAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RevokeAdvancementAction>> REVOKE_ADVANCEMENT = REGISTRY.register(
      "revoke_advancement", () -> RevokeAdvancementAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RevokePowerAction>> REVOKE_POWER = REGISTRY.register(
      "revoke_power", () -> RevokePowerAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RidingActionAction>> RIDING_ACTION = REGISTRY.register(
      "riding_action", () -> RidingActionAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SelectorActionAction>> SELECTOR_ACTION = REGISTRY.register(
      "selector_action", () -> SelectorActionAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SetFallDistanceAction>> SET_FALL_DISTANCE = REGISTRY.register(
      "set_fall_distance", () -> SetFallDistanceAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SetOnFireAction>> SET_ON_FIRE = REGISTRY.register(
      "set_on_fire", () -> SetOnFireAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SetNoGravityAction>> SET_NO_GRAVITY = REGISTRY.register(
      "set_no_gravity", () -> SetNoGravityAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SpawnEffectCloudAction>> SPAWN_EFFECT_CLOUD = REGISTRY.register(
      "spawn_effect_cloud", () -> SpawnEffectCloudAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SpawnEntityAction>> SPAWN_ENTITY = REGISTRY.register(
      "spawn_entity", () -> SpawnEntityAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SpawnParticlesAction>> SPAWN_PARTICLES = REGISTRY.register(
      "spawn_particles", () -> SpawnParticlesAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SwingHandAction>> SWING_HAND = REGISTRY.register(
      "swing_hand", () -> SwingHandAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ToggleAction>> TOGGLE = REGISTRY.register("toggle", () -> ToggleAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<TriggerCooldownAction>> TRIGGER_COOLDOWN = REGISTRY.register(
      "trigger_cooldown", () -> TriggerCooldownAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AndAction>> AND = REGISTRY.register("and", () -> AndAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ChanceAction>> CHANCE = REGISTRY.register("chance", () -> ChanceAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ChoiceAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DelayAction>> DELAY = REGISTRY.register("delay", () -> DelayAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<IfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> IfElseAction.CODEC);
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<IfElseListAction>> IF_ELSE_LIST = REGISTRY.register(
      "if_else_list", () -> IfElseListAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RegionApplyAction>> REGION_APPLY = REGISTRY.register(
      "region_apply", () -> RegionApplyAction.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SideAction>> SIDE = REGISTRY.register("side", () -> SideAction.CODEC);
}
