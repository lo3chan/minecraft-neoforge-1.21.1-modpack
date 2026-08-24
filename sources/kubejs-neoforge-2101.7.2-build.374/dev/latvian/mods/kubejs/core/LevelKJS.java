package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.level.CachedLevelBlock;
import dev.latvian.mods.kubejs.level.ExplosionProperties;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface LevelKJS extends WithAttachedData<Level>, ScriptTypeHolder, EntityGetterKJS {
   default Level kjs$self() {
      return (Level)this;
   }

   @RemapForJS("getSide")
   @Override
   default ScriptType kjs$getScriptType() {
      return this.kjs$self().isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
   }

   @Override
   default Component kjs$getName() {
      return Component.literal(this.kjs$getDimension().toString());
   }

   @Override
   default void kjs$tell(Component message) {
      for (Player entity : this.kjs$self().players()) {
         entity.kjs$tell(message);
      }
   }

   @Override
   default void kjs$setStatusMessage(Component message) {
      for (Player entity : this.kjs$self().players()) {
         entity.kjs$setStatusMessage(message);
      }
   }

   @Info(
      value = "Each player in the level (world) runs the specified console command with their permission level.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommand(String command) {
      for (Player entity : this.kjs$self().players()) {
         entity.kjs$runCommand(command);
      }
   }

   @Info(
      value = "Each player in the level (world) runs the specified console command with their permission level. The command won't output any logs in chat nor console",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommandSilent(String command) {
      for (Player entity : this.kjs$self().players()) {
         entity.kjs$runCommandSilent(command);
      }
   }

   @Override
   default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
      for (Player entity : this.kjs$self().players()) {
         entity.kjs$setActivePostShader(id);
      }
   }

   default ResourceLocation kjs$getDimension() {
      return this.kjs$self().dimension().location();
   }

   default boolean kjs$isOverworld() {
      return this.kjs$self().dimension() == Level.OVERWORLD;
   }

   default void kjs$setTime(long time) {
      if (this.kjs$self().getLevelData() instanceof ServerLevelData d) {
         d.setGameTime(time);
      }
   }

   default LevelBlock kjs$getBlock(int x, int y, int z) {
      return this.kjs$getBlock(new BlockPos(x, y, z));
   }

   default LevelBlock kjs$getBlock(BlockPos pos) {
      return new CachedLevelBlock(this.kjs$self(), pos);
   }

   default LevelBlock kjs$getBlock(BlockEntity entity) {
      return new CachedLevelBlock(entity.getLevel(), entity.getBlockPos()).cache(entity).cache(entity.getBlockState());
   }

   default Explosion kjs$explode(double x, double y, double z, ExplosionProperties properties) {
      return properties.explode(this.kjs$self(), x, y, z);
   }

   @Nullable
   default Entity kjs$createEntity(EntityType<?> type) {
      return type.create(this.kjs$self());
   }

   default void spawnEntity(EntityType<?> type, Consumer<Entity> callback) {
      Entity entity = type.create(this.kjs$self());
      if (entity != null) {
         callback.accept(entity);
         this.kjs$self().addFreshEntity(entity);
      }
   }

   default void kjs$spawnFireworks(double x, double y, double z, Fireworks fireworks, int lifetime) {
      ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
      stack.set(DataComponents.FIREWORKS, fireworks);
      FireworkRocketEntity rocket = new FireworkRocketEntity(this.kjs$self(), x, y, z, stack);
      if (lifetime != -1) {
         ((FireworkRocketEntityKJS)rocket).setLifetimeKJS(lifetime);
      }

      rocket.setInvisible(true);
      this.kjs$self().addFreshEntity(rocket);
   }

   default void kjs$spawnParticles(
      ParticleOptions options, boolean overrideLimiter, double x, double y, double z, double vx, double vy, double vz, int count, double speed
   ) {
      Level level = this.kjs$self();
      if (count == 0) {
         double d0 = speed * vx;
         double d2 = speed * vy;
         double d4 = speed * vz;

         try {
            level.addParticle(options, overrideLimiter, x, y, z, d0, d2, d4);
         } catch (Throwable var35) {
         }
      } else {
         RandomSource random = level.random;

         for (int i = 0; i < count; i++) {
            double ox = random.nextGaussian() * vx;
            double oy = random.nextGaussian() * vy;
            double oz = random.nextGaussian() * vz;
            double d6 = random.nextGaussian() * speed;
            double d7 = random.nextGaussian() * speed;
            double d8 = random.nextGaussian() * speed;

            try {
               level.addParticle(options, overrideLimiter, x + ox, y + oy, z + oz, d6, d7, d8);
            } catch (Throwable var34) {
               return;
            }
         }
      }
   }

   default void kjs$spawnLightning(double x, double y, double z, boolean visualOnly, @Nullable ServerPlayer cause) {
      LightningBolt e = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.kjs$self());
      e.moveTo(x, y, z);
      e.setCause(cause);
      e.setVisualOnly(visualOnly);
      this.kjs$self().addFreshEntity(e);
   }

   default void kjs$spawnLightning(double x, double y, double z, boolean visualOnly) {
      this.kjs$spawnLightning(x, y, z, visualOnly, null);
   }
}
