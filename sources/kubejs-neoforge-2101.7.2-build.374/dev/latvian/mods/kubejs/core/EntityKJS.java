package dev.latvian.mods.kubejs.core;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.entity.KubeRayTraceResult;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface EntityKJS extends WithPersistentData, MessageSenderKJS, ScriptTypeHolder {
   @HideFromJS
   default Entity kjs$self() {
      return (Entity)this;
   }

   default Level kjs$getLevel() {
      return this.kjs$self().level();
   }

   @Nullable
   default MinecraftServer kjs$getServer() {
      return this.kjs$getLevel().getServer();
   }

   default String kjs$getType() {
      return this.kjs$self().getType().kjs$getId();
   }

   @ThisIs(
      classNames = {"net.minecraft.client.player.LocalPlayer"}
   )
   @Info("Checks, whether the entity is a reference to yourself - that is - the client player you are controlling.")
   default boolean kjs$isSelf() {
      return false;
   }

   @Info("If the entity is a player, gets the player's profile, otherwise returns `null`.")
   @Nullable
   default GameProfile kjs$getProfile() {
      return null;
   }

   @Info("Gets the entity's custom name, or entity ID if entity has no custom name.")
   default String kjs$getUsername() {
      Component customName = this.kjs$self().getCustomName();
      return customName != null ? customName.getString() : this.kjs$getType();
   }

   @Override
   default Component kjs$getName() {
      return this.kjs$self().getName();
   }

   @Override
   default Component kjs$getDisplayName() {
      return this.kjs$self().getDisplayName();
   }

   @Info(
      value = "Sends a message in chat to the entity.",
      params = {@Param(
         name = "message",
         value = "A text component. It may be a string, which will be implicitly wrapped into a text component."
      )}
   )
   @Override
   default void kjs$tell(Component message) {
      this.kjs$self().sendSystemMessage(message);
   }

   @Info(
      value = "Runs the specified console command with permission level of the entity.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommand(String command) {
      if (this.kjs$getLevel() instanceof ServerLevel level) {
         level.getServer().getCommands().performPrefixedCommand(this.kjs$self().createCommandSourceStack(), command);
      }
   }

   @Info(
      value = "Runs the specified console command with permission level of the entity. The command won't output any logs in chat nor console.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommandSilent(String command) {
      if (this.kjs$getLevel() instanceof ServerLevel level) {
         level.getServer().getCommands().performPrefixedCommand(this.kjs$self().createCommandSourceStack().withSuppressedOutput(), command);
      }
   }

   @ThisIs({Player.class})
   @Info("Checks if the entity is a player entity.")
   default boolean kjs$isPlayer() {
      return false;
   }

   @ThisIs({ServerPlayer.class})
   @Info("Checks if the entity is a server-side player.")
   default boolean kjs$isServerPlayer() {
      return false;
   }

   @ThisIs(
      classNames = {"net.minecraft.client.player.AbstractClientPlayer"}
   )
   @Info("Checks if the entity is a client-side player.")
   default boolean kjs$isClientPlayer() {
      return false;
   }

   @Info("Gets the item stack corresponding to either:\n- the item contained in the item entity,\n- the item in the item frame.\nWill be `null` if the entity is neither an item entity nor an item frame.\n")
   @Nullable
   default ItemStack kjs$getItem() {
      return null;
   }

   @ThisIs({ItemFrame.class})
   @Info("Checks if the entity is an item frame entity.")
   default boolean kjs$isFrame() {
      return this instanceof ItemFrame;
   }

   @ThisIs({ItemEntity.class})
   @Info("Checks if the entity is an item entity.")
   default boolean kjs$isItem() {
      return this instanceof ItemEntity;
   }

   @ThisIs({LivingEntity.class})
   @Info("Checks if the entity is a `LivingEntity`.")
   default boolean kjs$isLiving() {
      return false;
   }

   @Info("Checks if the entity is a monster.")
   default boolean kjs$isMonster() {
      return !this.kjs$self().getType().getCategory().isFriendly();
   }

   @Info("Checks if the entity is an animal.")
   default boolean kjs$isAnimal() {
      return this.kjs$self().getType().getCategory().isPersistent();
   }

   @Info("Checks if the entity is an ambient creature.")
   default boolean kjs$isAmbientCreature() {
      return this.kjs$self().getType().getCategory() == MobCategory.AMBIENT;
   }

   @Info("Checks if the entity is a water creature.")
   default boolean kjs$isWaterCreature() {
      return this.kjs$self().getType().getCategory() == MobCategory.WATER_CREATURE;
   }

   @Info("Checks if the entity is a peaceful creature (not a monster).")
   default boolean kjs$isPeacefulCreature() {
      return this.kjs$self().getType().getCategory().isFriendly();
   }

   default void kjs$setX(double x) {
      this.kjs$setPosition(x, this.kjs$self().getY(), this.kjs$self().getZ());
   }

   default void kjs$setY(double y) {
      this.kjs$setPosition(this.kjs$self().getX(), y, this.kjs$self().getZ());
   }

   default void kjs$setZ(double z) {
      this.kjs$setPosition(this.kjs$self().getX(), this.kjs$self().getY(), z);
   }

   default double kjs$getMotionX() {
      return this.kjs$self().getDeltaMovement().x;
   }

   default void kjs$setMotionX(double x) {
      Vec3 m = this.kjs$self().getDeltaMovement();
      this.kjs$self().setDeltaMovement(x, m.y, m.z);
   }

   default double kjs$getMotionY() {
      return this.kjs$self().getDeltaMovement().y;
   }

   default void kjs$setMotionY(double y) {
      Vec3 m = this.kjs$self().getDeltaMovement();
      this.kjs$self().setDeltaMovement(m.x, y, m.z);
   }

   default double kjs$getMotionZ() {
      return this.kjs$self().getDeltaMovement().z;
   }

   default void kjs$setMotionZ(double z) {
      Vec3 m = this.kjs$self().getDeltaMovement();
      this.kjs$self().setDeltaMovement(m.x, m.y, z);
   }

   @Info(
      value = "Teleports an entity to specified coordinates.",
      params = {@Param(
         name = "x",
         value = "The `x` target coordinate."
      ), @Param(
         name = "y",
         value = "The `y` target coordinate."
      ), @Param(
         name = "z",
         value = "The `z` target coordinate."
      )}
   )
   default void kjs$teleportTo(double x, double y, double z) throws IllegalArgumentException {
      Entity self = this.kjs$self();
      if (!Level.isInSpawnableBounds(BlockPos.containing(x, y, z))) {
         throw new IllegalArgumentException("The provided coordinates are out of bounds.");
      } else {
         this.kjs$self().teleportTo(x, y, z);
      }
   }

   private void checkDestinationValidity(BlockPos blockPos, float yaw, float pitch) throws IllegalArgumentException {
      if (!Level.isInSpawnableBounds(blockPos)) {
         throw new IllegalArgumentException("The provided coordinates are out of bounds.");
      } else if (Float.isNaN(yaw)) {
         throw new IllegalArgumentException("Yaw is not a number.");
      } else if (Float.isNaN(pitch)) {
         throw new IllegalArgumentException("Pitch is not a number.");
      }
   }

   @Info(
      value = "Teleports an entity to a specified `ServerLevel`, to specified coordinates and rotation.",
      params = {@Param(
         name = "level",
         value = "A `ServerLevel` to teleport the entity to."
      ), @Param(
         name = "x",
         value = "The `x` target coordinate."
      ), @Param(
         name = "y",
         value = "The `y` target coordinate."
      ), @Param(
         name = "z",
         value = "The `z` target coordinate."
      ), @Param(
         name = "yaw",
         value = "The entity's target yaw."
      ), @Param(
         name = "pitch",
         value = "The entity's target pitch."
      )}
   )
   default boolean kjs$teleportToLevel(ServerLevel level, double x, double y, double z, float yaw, float pitch) throws IllegalArgumentException {
      Entity self = this.kjs$self();
      Level previousLevel = this.kjs$getLevel();
      this.checkDestinationValidity(BlockPos.containing(x, y, z), yaw, pitch);
      if (level == previousLevel) {
         this.kjs$setPositionAndRotation(x, y, z, yaw, pitch);
         return true;
      } else {
         float adjustedYaw = Mth.wrapDegrees(yaw);
         float adjustedPitch = Mth.wrapDegrees(pitch);
         boolean teleportSucceeded = self.teleportTo(level, x, y, z, Set.of(), adjustedYaw, adjustedPitch);
         if (!teleportSucceeded) {
            return false;
         } else {
            if (self instanceof LivingEntity livingEntity && !livingEntity.isFallFlying()) {
               livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
               livingEntity.setOnGround(true);
            }

            if (self instanceof PathfinderMob pathfinderMob) {
               pathfinderMob.getNavigation().stop();
            }

            return true;
         }
      }
   }

   @Info(
      value = "Teleports an entity to a dimension of specified ID, to specified coordinates and rotation.",
      params = {@Param(
         name = "dimension",
         value = "A `ResourceLocation` of the target dimension. It can be a string representing the dimension ID."
      ), @Param(
         name = "x",
         value = "The `x` target coordinate."
      ), @Param(
         name = "y",
         value = "The `y` target coordinate."
      ), @Param(
         name = "z",
         value = "The `z` target coordinate."
      ), @Param(
         name = "yaw",
         value = "The entity's target yaw."
      ), @Param(
         name = "pitch",
         value = "The entity's target pitch."
      )}
   )
   default boolean kjs$teleportTo(ResourceLocation dimension, double x, double y, double z, float yaw, float pitch) throws IllegalArgumentException {
      ServerLevel level = this.kjs$getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
      if (level == null) {
         throw new IllegalArgumentException("The provided dimension ID is invalid.");
      } else {
         return this.kjs$teleportToLevel(level, x, y, z, yaw, pitch);
      }
   }

   @Info(
      value = "Teleports an entity to a dimension of specified ID, to specified coordinates and rotation.",
      params = {@Param(
         name = "x",
         value = "The `x` target coordinate."
      ), @Param(
         name = "y",
         value = "The `y` target coordinate."
      ), @Param(
         name = "z",
         value = "The `z` target coordinate."
      ), @Param(
         name = "yaw",
         value = "The entity's target yaw."
      ), @Param(
         name = "pitch",
         value = "The entity's target pitch."
      )}
   )
   default void kjs$teleportTo(double x, double y, double z, float yaw, float pitch) throws IllegalArgumentException {
      this.checkDestinationValidity(BlockPos.containing(x, y, z), yaw, pitch);
      this.kjs$setPositionAndRotation(x, y, z, yaw, pitch);
   }

   default void kjs$setPosition(LevelBlock block) {
      this.kjs$teleportTo(block.getX(), block.getY(), block.getZ());
   }

   default void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      this.kjs$self().moveTo(x, y, z, yaw, pitch);
   }

   default void kjs$setPosition(double x, double y, double z) {
      this.kjs$setPositionAndRotation(x, y, z, this.kjs$self().getYRot(), this.kjs$self().getXRot());
   }

   default void kjs$setRotation(float yaw, float pitch) {
      this.kjs$setPositionAndRotation(this.kjs$self().getX(), this.kjs$self().getY(), this.kjs$self().getZ(), yaw, pitch);
   }

   @Info("Gets a list of all passengers of the entity.")
   default EntityArrayList kjs$getPassengers() {
      return new EntityArrayList(this.kjs$self().getPassengers());
   }

   @Deprecated
   @Info("Replaced by `entity.getTeamName()`")
   default String kjs$getTeamId() {
      return this.kjs$getTeamName();
   }

   @Info("Gets the name of the team entity is in, or `''` (empty string) if the entity is not part of any team")
   default String kjs$getTeamName() {
      PlayerTeam team = this.kjs$self().getTeam();
      return team == null ? "" : team.getName();
   }

   @Info("Checks, whether the entity is part of any team.")
   default boolean kjs$isOnScoreboardTeam() {
      return this.kjs$self().getTeam() != null;
   }

   @Info(
      value = "Checks, whether the entity is part of a team called `teamName`.",
      params = {@Param(
         name = "teamName",
         value = "The name of the team to check."
      )}
   )
   default boolean kjs$isOnScoreboardTeam(String teamName) {
      Team team = this.kjs$self().getCommandSenderWorld().getScoreboard().getPlayerTeam(teamName);
      return team != null && this.kjs$self().isAlliedTo(team);
   }

   @Info("Gets the entity's facing direction.\nIf the entity faces more than 45 degrees up or down, the resulting facing direction is respectively `up` or `down`.\nOtherwise, the resulting facing direction is determined by whichever cardinal direction is closer to entity's yaw.\n")
   default Direction kjs$getFacing() {
      if (this.kjs$self().getXRot() > 45.0F) {
         return Direction.DOWN;
      } else {
         return this.kjs$self().getXRot() < -45.0F ? Direction.UP : this.kjs$self().getDirection();
      }
   }

   @Info("Gets a block at the position of the entity.")
   default LevelBlock kjs$getBlock() {
      return this.kjs$getLevel().kjs$getBlock(this.kjs$self().blockPosition());
   }

   default CompoundTag kjs$getNbt() {
      CompoundTag nbt = new CompoundTag();
      this.kjs$self().saveWithoutId(nbt);
      return nbt;
   }

   default void kjs$setNbt(@Nullable CompoundTag nbt) {
      if (nbt != null) {
         this.kjs$self().load(nbt);
      }
   }

   default Entity kjs$mergeNbt(@Nullable CompoundTag tag) {
      if (tag != null && !tag.isEmpty()) {
         CompoundTag nbt = this.kjs$getNbt();

         for (String k : tag.getAllKeys()) {
            Tag t = tag.get(k);
            if (t != null && t != EndTag.INSTANCE) {
               nbt.put(k, tag.get(k));
            } else {
               nbt.remove(k);
            }
         }

         this.kjs$setNbt(nbt);
         return this.kjs$self();
      } else {
         return this.kjs$self();
      }
   }

   default void kjs$spawn() {
      this.kjs$getLevel().addFreshEntity(this.kjs$self());
   }

   @Info(
      value = "Damages an entity by a given amount of HP dealing generic damage.",
      params = {@Param(
         name = "hp",
         value = "The amount of damage to deal."
      )}
   )
   default boolean kjs$damage(float hp) {
      return this.kjs$self().hurt(this.kjs$self().damageSources().generic(), hp);
   }

   @Info(
      value = "Damages an entity by a given amount of HP dealing a specific type of damage.",
      params = {@Param(
         name = "hp",
         value = "The amount of damage to deal."
      ), @Param(
         name = "source",
         value = "The damage source. It may be a string specifying a damage source, like `'minecraft:cramming'`."
      )}
   )
   default boolean kjs$damage(float hp, DamageSource source) {
      return this.kjs$self().hurt(source, hp);
   }

   @Info("Replaced by `entity.damage(hp)`")
   @Deprecated
   default boolean kjs$attack(float hp) {
      return this.kjs$damage(hp);
   }

   @Info("Replaced by `entity.damage(hp, damageSource)`")
   @Deprecated
   default boolean kjs$attack(DamageSource source, float hp) {
      return this.kjs$damage(hp, source);
   }

   @Info("Measures the distance of entity to block at specified `BlockPos`.")
   default double kjs$distanceToBlock(BlockPos pos) {
      return Math.sqrt(this.kjs$distanceToBlockSqr(pos));
   }

   @Info("Measures the **square** of a distance of entity to the block at specified `BlockPos`.")
   default double kjs$distanceToBlockSqr(BlockPos pos) {
      return this.kjs$self().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
   }

   @Info("Measures the distance of entity to the point at specified `x`, `y` and `z`.")
   default double kjs$distanceTo(double x, double y, double z) {
      return Math.sqrt(this.kjs$self().distanceToSqr(x, y, z));
   }

   @Info("Measures the distance of entity to the point at specified 3D position vector.")
   default double kjs$distanceTo(Vec3 position) {
      return Math.sqrt(this.kjs$self().distanceToSqr(position));
   }

   @Deprecated
   @Info("Replaced by `entity.distanceToSqr(x, y, z)`.")
   default double kjs$getDistanceSq(double x, double y, double z) {
      return this.kjs$self().distanceToSqr(x, y, z);
   }

   @Deprecated
   @Info("Replaced by `entity.distanceTo(x, y, z)`.")
   default double kjs$getDistance(double x, double y, double z) {
      return this.kjs$distanceTo(x, y, z);
   }

   @Deprecated
   @Info("Replaced by `entity.distanceToBlockSqr(pos)`.")
   default double kjs$getDistanceSq(BlockPos pos) {
      return this.kjs$distanceToBlockSqr(pos);
   }

   default KubeRayTraceResult kjs$rayTrace(double distance, boolean fluids) {
      Entity entity = this.kjs$self();
      HitResult hitResult = entity.pick(distance, 0.0F, fluids);
      Vec3 eyePosition = entity.getEyePosition();
      Vec3 lookVector = entity.getViewVector(1.0F);
      Vec3 traceEnd = eyePosition.add(lookVector.x * distance, lookVector.y * distance, lookVector.z * distance);
      AABB bound = entity.getBoundingBox().expandTowards(lookVector.scale(distance)).inflate(1.0, 1.0, 1.0);
      double distanceSquared = hitResult.getType() != Type.MISS ? hitResult.getLocation().distanceToSqr(eyePosition) : distance * distance;
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         entity, eyePosition, traceEnd, bound, ent -> !ent.isSpectator() && ent.isPickable(), distanceSquared
      );
      if (entityHitResult != null) {
         double entityDistanceSquared = eyePosition.distanceToSqr(entityHitResult.getLocation());
         if (entityDistanceSquared < distanceSquared || hitResult.getType() == Type.MISS) {
            hitResult = entityHitResult;
         }
      }

      return new KubeRayTraceResult(entity, hitResult, distance);
   }

   default KubeRayTraceResult kjs$rayTrace(double distance) {
      return this.kjs$rayTrace(distance, true);
   }

   @Nullable
   default Entity kjs$rayTraceEntity(double distance, Predicate<Entity> filter) {
      double d0 = 1.7976931348623157E308;
      Entity entity = null;
      Vec3 start = this.kjs$self().getEyePosition();
      Vec3 end = start.add(this.kjs$self().getLookAngle().scale(distance));

      for (Entity entity1 : this.kjs$self().level().getEntities(this.kjs$self(), new AABB(start, end), filter == null ? UtilsJS.ALWAYS_TRUE : filter)) {
         AABB aabb = entity1.getBoundingBox();
         Optional<Vec3> optional = aabb.clip(start, end);
         double d1;
         if (!optional.isEmpty() && (d1 = start.distanceToSqr(optional.get())) < d0) {
            entity = entity1;
            d0 = d1;
         }
      }

      return entity;
   }

   @HideFromJS
   @Nullable
   default CompoundTag kjs$getRawPersistentData() {
      throw new NoMixinException();
   }

   @HideFromJS
   default void kjs$setRawPersistentData(@Nullable CompoundTag tag) {
      throw new NoMixinException();
   }

   @Override
   default ScriptType kjs$getScriptType() {
      return this.kjs$getLevel().kjs$getScriptType();
   }
}
