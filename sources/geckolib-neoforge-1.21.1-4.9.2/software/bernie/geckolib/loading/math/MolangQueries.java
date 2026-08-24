package software.bernie.geckolib.loading.math;

import com.google.common.collect.Streams;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToDoubleFunction;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.util.ClientUtil;

public final class MolangQueries {
   public static final String ACTOR_COUNT = "query.actor_count";
   public static final String ANIM_TIME = "query.anim_time";
   public static final String BLOCKING = "query.blocking";
   public static final String BLOCK_STATE = "query.block_state";
   public static final String BODY_X_ROTATION = "query.body_x_rotation";
   public static final String BODY_Y_ROTATION = "query.body_y_rotation";
   public static final String CAN_CLIMB = "query.can_climb";
   public static final String CAN_FLY = "query.can_fly";
   public static final String CAN_SWIM = "query.can_swim";
   public static final String CAN_WALK = "query.can_walk";
   public static final String CARDINAL_FACING = "query.cardinal_facing";
   public static final String CARDINAL_FACING_2D = "query.cardinal_facing_2d";
   public static final String CARDINAL_PLAYER_FACING = "query.cardinal_player_facing";
   public static final String CONTROLLER_SPEED = "query.controller_speed";
   public static final String DAY = "query.day";
   public static final String DEATH_TICKS = "query.death_ticks";
   public static final String DISTANCE_FROM_CAMERA = "query.distance_from_camera";
   public static final String EQUIPMENT_COUNT = "query.equipment_count";
   public static final String FRAME_ALPHA = "query.frame_alpha";
   public static final String GET_ACTOR_INFO_ID = "query.get_actor_info_id";
   public static final String GROUND_SPEED = "query.ground_speed";
   public static final String HAS_CAPE = "query.has_cape";
   public static final String HAS_COLLISION = "query.has_collision";
   public static final String HAS_GRAVITY = "query.has_gravity";
   public static final String HAS_HEAD_GEAR = "query.has_head_gear";
   public static final String HAS_OWNER = "query.has_owner";
   public static final String HAS_PLAYER_RIDER = "query.has_player_rider";
   public static final String HAS_RIDER = "query.has_rider";
   public static final String HEAD_X_ROTATION = "query.head_x_rotation";
   public static final String HEAD_Y_ROTATION = "query.head_y_rotation";
   public static final String HEALTH = "query.health";
   public static final String HURT_TIME = "query.hurt_time";
   public static final String INVULNERABLE_TICKS = "query.invulnerable_ticks";
   public static final String IS_ALIVE = "query.is_alive";
   public static final String IS_ANGRY = "query.is_angry";
   public static final String IS_BABY = "query.is_baby";
   public static final String IS_BREATHING = "query.is_breathing";
   public static final String IS_ENCHANTED = "query.is_enchanted";
   public static final String IS_FIRE_IMMUNE = "query.is_fire_immune";
   public static final String IS_FIRST_PERSON = "query.is_first_person";
   public static final String IS_INVISIBLE = "query.is_invisible";
   public static final String IS_IN_CONTACT_WITH_WATER = "query.is_in_contact_with_water";
   public static final String IS_IN_LAVA = "query.is_in_lava";
   public static final String IS_IN_WATER = "query.is_in_water";
   public static final String IS_IN_WATER_OR_RAIN = "query.is_in_water_or_rain";
   public static final String IS_LEASHED = "query.is_leashed";
   public static final String IS_MOVING = "query.is_moving";
   public static final String IS_ON_FIRE = "query.is_on_fire";
   public static final String IS_ON_GROUND = "query.is_on_ground";
   public static final String IS_POWERED = "query.is_powered";
   public static final String IS_RIDING = "query.is_riding";
   public static final String IS_SADDLED = "query.is_saddled";
   public static final String IS_SILENT = "query.is_silent";
   public static final String IS_SLEEPING = "query.is_sleeping";
   public static final String IS_SNEAKING = "query.is_sneaking";
   public static final String IS_SPRINTING = "query.is_sprinting";
   public static final String IS_STACKABLE = "query.is_stackable";
   public static final String IS_SWIMMING = "query.is_swimming";
   public static final String IS_USING_ITEM = "query.is_using_item";
   public static final String IS_WALL_CLIMBING = "query.is_wall_climbing";
   public static final String ITEM_MAX_USE_DURATION = "query.item_max_use_duration";
   public static final String LIFE_TIME = "query.life_time";
   public static final String MAIN_HAND_ITEM_MAX_DURATION = "query.main_hand_item_max_duration";
   public static final String MAIN_HAND_ITEM_USE_DURATION = "query.main_hand_item_use_duration";
   public static final String MAX_DURABILITY = "query.max_durability";
   public static final String MAX_HEALTH = "query.max_health";
   public static final String MOON_BRIGHTNESS = "query.moon_brightness";
   public static final String MOON_PHASE = "query.moon_phase";
   public static final String MOVEMENT_DIRECTION = "query.movement_direction";
   public static final String PLAYER_LEVEL = "query.player_level";
   public static final String REMAINING_DURABILITY = "query.remaining_durability";
   public static final String RIDER_BODY_X_ROTATION = "query.rider_body_x_rotation";
   public static final String RIDER_BODY_Y_ROTATION = "query.rider_body_y_rotation";
   public static final String RIDER_HEAD_X_ROTATION = "query.rider_head_x_rotation";
   public static final String RIDER_HEAD_Y_ROTATION = "query.rider_head_y_rotation";
   public static final String SCALE = "query.scale";
   public static final String SLEEP_ROTATION = "query.sleep_rotation";
   public static final String TIME_OF_DAY = "query.time_of_day";
   public static final String TIME_STAMP = "query.time_stamp";
   public static final String VERTICAL_SPEED = "query.vertical_speed";
   public static final String YAW_SPEED = "query.yaw_speed";
   private static final Map<String, Variable> VARIABLES = new ConcurrentHashMap<>();
   private static MolangQueries.Actor<?> ACTOR = null;

   public static boolean isExistingVariable(String name) {
      return VARIABLES.containsKey(name);
   }

   static void registerVariable(Variable variable) {
      VARIABLES.put(variable.name(), variable);
   }

   static Variable getVariableFor(String name) {
      return VARIABLES.computeIfAbsent(applyPrefixAliases(name, "query.", "q."), key -> new Variable(key, 0.0));
   }

   private static String applyPrefixAliases(String text, String properName, String... aliases) {
      for (String alias : aliases) {
         if (text.startsWith(alias)) {
            return properName + text.substring(alias.length());
         }
      }

      return text;
   }

   public static void updateActor(AnimationState<? extends GeoAnimatable> animationState, double animTime) {
      ACTOR = new MolangQueries.Actor<>(animationState, animationState.getAnimatable(), animTime, Minecraft.getInstance(), Minecraft.getInstance().level);
   }

   public static void clearActor() {
      ACTOR = null;
   }

   public static <T> void setActorVariable(String name, ToDoubleFunction<MolangQueries.Actor<T>> value) {
      getVariableFor(name).set(() -> value.applyAsDouble((MolangQueries.Actor<T>)getActor()));
   }

   private static MolangQueries.Actor<?> getActor() {
      return ACTOR;
   }

   private static void setDefaultQueryValues() {
      getVariableFor("PI").set(3.141592653589793);
      getVariableFor("E").set(2.718281828459045);
      setActorVariable("query.controller_speed", actor -> actor.animationState.getController().getAnimationSpeed());
      setActorVariable("query.cardinal_player_facing", actor -> actor.mc.player.getDirection().ordinal());
      setActorVariable("query.day", actor -> actor.level.getGameTime() / 24000.0);
      setActorVariable("query.frame_alpha", actor -> actor.animationState().getPartialTick());
      setActorVariable("query.has_cape", actor -> actor.mc.player.getSkin().capeTexture() != null ? 1.0 : 0.0);
      setActorVariable("query.is_first_person", actor -> actor.mc.options.getCameraType() == CameraType.FIRST_PERSON ? 1.0 : 0.0);
      setActorVariable("query.life_time", actor -> actor.animTime / 20.0);
      setActorVariable("query.moon_brightness", actor -> actor.level.getMoonBrightness());
      setActorVariable("query.moon_phase", actor -> actor.level.getMoonPhase());
      setActorVariable("query.player_level", actor -> actor.mc.player.experienceLevel);
      setActorVariable("query.time_of_day", actor -> (float)actor.level.getDayTime() / 24000.0F);
      setActorVariable("query.time_stamp", actor -> actor.mc.level.getGameTime());
      setDefaultBlockEntityQueryValues();
      setDefaultEntityQueryValues();
      setDefaultLivingEntityQueryValues();
      setDefaultMobQueryValues();
      setDefaultItemQueryValues();
   }

   private static void setDefaultBlockEntityQueryValues() {
      setActorVariable(
         "query.block_state",
         actor -> ((BlockEntity)actor.animatable)
            .getBlockState()
            .getBlock()
            .getStateDefinition()
            .getPossibleStates()
            .indexOf(((BlockEntity)actor.animatable).getBlockState())
      );
   }

   private static void setDefaultEntityQueryValues() {
      setActorVariable(
         "query.body_x_rotation",
         actor -> actor.animatable instanceof LivingEntity ? 0.0 : ((Entity)actor.animatable).getViewXRot(actor.animationState.getPartialTick())
      );
      setActorVariable(
         "query.body_y_rotation",
         actor -> actor.animatable instanceof LivingEntity living
            ? Mth.lerp(actor.animationState.getPartialTick(), living.yBodyRotO, living.yBodyRot)
            : ((Entity)actor.animatable).getViewYRot(actor.animationState.getPartialTick())
      );
      setActorVariable("query.cardinal_facing", actor -> ((Entity)actor.animatable).getDirection().get3DDataValue());
      setActorVariable("query.cardinal_facing_2d", actor -> {
         int directionId = ((Entity)actor.animatable).getDirection().get3DDataValue();
         return directionId < 2 ? 6.0 : directionId;
      });
      setActorVariable(
         "query.distance_from_camera", actor -> actor.mc.gameRenderer.getMainCamera().getPosition().distanceTo(((Entity)actor.animatable).position())
      );
      setActorVariable("query.get_actor_info_id", actor -> ((Entity)actor.animatable).getId());
      setActorVariable("query.has_collision", actor -> !((Entity)actor.animatable).noPhysics ? 1.0 : 0.0);
      setActorVariable("query.has_gravity", actor -> !((Entity)actor.animatable).isNoGravity() ? 1.0 : 0.0);
      setActorVariable("query.has_owner", actor -> actor.animatable instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null ? 1 : 0);
      setActorVariable("query.has_player_rider", actor -> ((Entity)actor.animatable).hasPassenger(Player.class::isInstance) ? 1.0 : 0.0);
      setActorVariable("query.has_rider", actor -> ((Entity)actor.animatable).isVehicle() ? 1.0 : 0.0);
      setActorVariable("query.is_alive", actor -> ((Entity)actor.animatable).isAlive() ? 1.0 : 0.0);
      setActorVariable("query.is_angry", actor -> actor.animatable instanceof NeutralMob neutralMob && neutralMob.isAngry() ? 1 : 0);
      setActorVariable("query.is_breathing", actor -> ((Entity)actor.animatable).getAirSupply() >= ((Entity)actor.animatable).getMaxAirSupply() ? 1.0 : 0.0);
      setActorVariable("query.is_fire_immune", actor -> ((Entity)actor.animatable).getType().fireImmune() ? 1.0 : 0.0);
      setActorVariable("query.is_invisible", actor -> ((Entity)actor.animatable).isInvisible() ? 1.0 : 0.0);
      setActorVariable("query.is_in_contact_with_water", actor -> ((Entity)actor.animatable).isInWaterRainOrBubble() ? 1.0 : 0.0);
      setActorVariable("query.is_in_lava", actor -> ((Entity)actor.animatable).isInLava() ? 1.0 : 0.0);
      setActorVariable("query.is_in_water", actor -> ((Entity)actor.animatable).isInWater() ? 1.0 : 0.0);
      setActorVariable("query.is_in_water_or_rain", actor -> ((Entity)actor.animatable).isInWaterOrRain() ? 1.0 : 0.0);
      setActorVariable("query.is_leashed", actor -> actor.animatable instanceof Leashable leashable && leashable.isLeashed() ? 1 : 0);
      setActorVariable("query.is_moving", actor -> actor.animationState.isMoving() ? 1.0 : 0.0);
      setActorVariable("query.is_on_fire", actor -> ((Entity)actor.animatable).isOnFire() ? 1.0 : 0.0);
      setActorVariable("query.is_on_ground", actor -> ((Entity)actor.animatable).onGround() ? 1.0 : 0.0);
      setActorVariable("query.is_powered", actor -> actor.animatable instanceof PowerableMob powerable && powerable.isPowered() ? 1 : 0);
      setActorVariable("query.is_riding", actor -> ((Entity)actor.animatable).isPassenger() ? 1.0 : 0.0);
      setActorVariable("query.is_saddled", actor -> actor.animatable instanceof Saddleable saddleable && saddleable.isSaddled() ? 1 : 0);
      setActorVariable("query.is_silent", actor -> ((Entity)actor.animatable).isSilent() ? 1.0 : 0.0);
      setActorVariable("query.is_sneaking", actor -> ((Entity)actor.animatable).isCrouching() ? 1.0 : 0.0);
      setActorVariable("query.is_sprinting", actor -> ((Entity)actor.animatable).isSprinting() ? 1.0 : 0.0);
      setActorVariable("query.is_swimming", actor -> ((Entity)actor.animatable).isSwimming() ? 1.0 : 0.0);
      setActorVariable(
         "query.movement_direction",
         actor -> actor.animationState.isMoving() ? Direction.getNearest(((Entity)actor.animatable).getDeltaMovement()).get3DDataValue() : 6.0
      );
      setActorVariable(
         "query.rider_body_x_rotation",
         actor -> ((Entity)actor.animatable).isVehicle()
            ? (
               ((Entity)actor.animatable).getFirstPassenger() instanceof LivingEntity
                  ? 0.0
                  : ((Entity)actor.animatable).getFirstPassenger().getViewXRot(actor.animationState.getPartialTick())
            )
            : 0.0
      );
      setActorVariable(
         "query.rider_body_y_rotation",
         actor -> ((Entity)actor.animatable).isVehicle()
            ? (
               ((Entity)actor.animatable).getFirstPassenger() instanceof LivingEntity living
                  ? Mth.lerp(actor.animationState.getPartialTick(), living.yBodyRotO, living.yBodyRot)
                  : ((Entity)actor.animatable).getFirstPassenger().getViewYRot(actor.animationState.getPartialTick())
            )
            : 0.0F
      );
      setActorVariable(
         "query.rider_head_x_rotation",
         actor -> ((Entity)actor.animatable).getFirstPassenger() instanceof LivingEntity living
            ? living.getViewXRot(actor.animationState.getPartialTick())
            : 0.0
      );
      setActorVariable(
         "query.rider_head_y_rotation",
         actor -> ((Entity)actor.animatable).getFirstPassenger() instanceof LivingEntity living
            ? living.getViewYRot(actor.animationState.getPartialTick())
            : 0.0
      );
      setActorVariable("query.vertical_speed", actor -> ((Entity)actor.animatable).getDeltaMovement().y);
      setActorVariable("query.yaw_speed", actor -> ((Entity)actor.animatable).getYRot() - ((Entity)actor.animatable).yRotO);
   }

   private static void setDefaultLivingEntityQueryValues() {
      setActorVariable("query.blocking", actor -> ((LivingEntity)actor.animatable).isBlocking() ? 1.0 : 0.0);
      setActorVariable(
         "query.death_ticks",
         actor -> ((LivingEntity)actor.animatable).deathTime == 0 ? 0.0 : ((LivingEntity)actor.animatable).deathTime + actor.animationState.getPartialTick()
      );
      setActorVariable(
         "query.equipment_count", actor -> Streams.stream(((LivingEntity)actor.animatable).getArmorSlots()).filter(stack -> !stack.isEmpty()).count()
      );
      setActorVariable("query.ground_speed", actor -> ((LivingEntity)actor.animatable).getDeltaMovement().horizontalDistance());
      setActorVariable("query.has_head_gear", actor -> !((LivingEntity)actor.animatable).getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 1.0 : 0.0);
      setActorVariable("query.head_x_rotation", actor -> ((LivingEntity)actor.animatable).getViewXRot(actor.animationState.getPartialTick()));
      setActorVariable("query.head_y_rotation", actor -> ((LivingEntity)actor.animatable).getViewYRot(actor.animationState.getPartialTick()));
      setActorVariable("query.health", actor -> ((LivingEntity)actor.animatable).getHealth());
      setActorVariable(
         "query.hurt_time",
         actor -> ((LivingEntity)actor.animatable).hurtTime == 0 ? 0.0 : ((LivingEntity)actor.animatable).hurtTime - actor.animationState.getPartialTick()
      );
      setActorVariable(
         "query.invulnerable_ticks",
         actor -> ((LivingEntity)actor.animatable).invulnerableTime == 0
            ? 0.0
            : ((LivingEntity)actor.animatable).invulnerableTime - actor.animationState.getPartialTick()
      );
      setActorVariable("query.is_baby", actor -> ((LivingEntity)actor.animatable).isBaby() ? 1.0 : 0.0);
      setActorVariable("query.is_sleeping", actor -> ((LivingEntity)actor.animatable).isSleeping() ? 1.0 : 0.0);
      setActorVariable("query.is_using_item", actor -> ((LivingEntity)actor.animatable).isUsingItem() ? 1.0 : 0.0);
      setActorVariable("query.is_wall_climbing", actor -> ((LivingEntity)actor.animatable).onClimbable() ? 1.0 : 0.0);
      setActorVariable(
         "query.main_hand_item_max_duration", actor -> ((LivingEntity)actor.animatable).getMainHandItem().getUseDuration((LivingEntity)actor.animatable)
      );
      setActorVariable(
         "query.main_hand_item_use_duration",
         actor -> ((LivingEntity)actor.animatable).getUsedItemHand() == InteractionHand.MAIN_HAND
            ? ((LivingEntity)actor.animatable).getTicksUsingItem() / 20.0 + actor.animationState.getPartialTick()
            : 0.0
      );
      setActorVariable("query.max_health", actor -> ((LivingEntity)actor.animatable).getMaxHealth());
      setActorVariable("query.scale", actor -> ((LivingEntity)actor.animatable).getScale());
      setActorVariable(
         "query.sleep_rotation",
         actor -> Optional.ofNullable(((LivingEntity)actor.animatable).getBedOrientation()).<Float>map(Direction::toYRot).orElse(0.0F).floatValue()
      );
   }

   private static void setDefaultMobQueryValues() {
      setActorVariable(
         "query.can_climb", actor -> !((Mob)actor.animatable).isNoAi() && ((Mob)actor.animatable).getNavigation() instanceof WallClimberNavigation ? 1.0 : 0.0
      );
      setActorVariable(
         "query.can_fly", actor -> !((Mob)actor.animatable).isNoAi() && ((Mob)actor.animatable).getNavigation() instanceof FlyingPathNavigation ? 1.0 : 0.0
      );
      setActorVariable(
         "query.can_swim",
         actor -> (((Mob)actor.animatable).isNoAi() || !(((Mob)actor.animatable).getNavigation() instanceof WaterBoundPathNavigation))
               && !(((Mob)actor.animatable).getNavigation() instanceof AmphibiousPathNavigation)
            ? 0.0
            : 1.0
      );
      setActorVariable(
         "query.can_walk",
         actor -> (((Mob)actor.animatable).isNoAi() || !(((Mob)actor.animatable).getNavigation() instanceof GroundPathNavigation))
               && !(((Mob)actor.animatable).getNavigation() instanceof AmphibiousPathNavigation)
            ? 0.0
            : 1.0
      );
   }

   private static void setDefaultItemQueryValues() {
      setActorVariable("query.is_enchanted", actor -> actor.animationState.getData(DataTickets.ITEMSTACK).isEnchanted() ? 1.0 : 0.0);
      setActorVariable("query.is_stackable", actor -> actor.animationState.getData(DataTickets.ITEMSTACK).isStackable() ? 1.0 : 0.0);
      setActorVariable("query.item_max_use_duration", actor -> actor.animationState.getData(DataTickets.ITEMSTACK).getUseDuration(ClientUtil.getClientPlayer()));
      setActorVariable("query.max_durability", actor -> actor.animationState.getData(DataTickets.ITEMSTACK).getMaxDamage());
      setActorVariable("query.remaining_durability", actor -> {
         ItemStack stack = actor.animationState.getData(DataTickets.ITEMSTACK);
         return stack.isDamageableItem() ? stack.getMaxDamage() - stack.getDamageValue() : 1.0;
      });
   }

   static {
      setDefaultQueryValues();
   }

   public record Actor<T>(AnimationState<? extends GeoAnimatable> animationState, T animatable, double animTime, Minecraft mc, Level level) {
   }
}
