package dev.latvian.mods.kubejs.entity;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.level.WrappedSpawner;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Info("Invoked before an entity is spawned into the world.\n\nOnly entities from a `BaseSpawner` or world generation will trigger this event.\n")
public class CheckLivingEntitySpawnKubeEvent implements KubeLivingEntityEvent {
   private final LivingEntity entity;
   private final Level level;
   public final double x;
   public final double y;
   public final double z;
   public final transient MobSpawnType type;
   private final Either<BlockEntity, Entity> spawnerEither;
   @Nullable
   public transient WrappedSpawner spawner;

   public CheckLivingEntitySpawnKubeEvent(
      LivingEntity entity, Level level, double x, double y, double z, MobSpawnType type, Either<BlockEntity, Entity> spawnerEither
   ) {
      this.entity = entity;
      this.level = level;
      this.x = x;
      this.y = y;
      this.z = z;
      this.type = type;
      this.spawnerEither = spawnerEither;
   }

   @Info("The level the entity is being spawned into.")
   @Override
   public Level getLevel() {
      return this.level;
   }

   @Info("The entity being spawned.")
   @Override
   public LivingEntity getEntity() {
      return this.entity;
   }

   @Info("The block the entity is being spawned on.")
   public LevelBlock getBlock() {
      return this.level.kjs$getBlock(BlockPos.containing(this.x, this.y, this.z));
   }

   @Info("The type of spawn.")
   public MobSpawnType getType() {
      return this.type;
   }

   @Info("The spawner that spawned the entity.")
   @NotNull
   public WrappedSpawner getSpawner() {
      if (this.spawner == null) {
         this.spawner = WrappedSpawner.of(this.spawnerEither);
      }

      return this.spawner;
   }
}
