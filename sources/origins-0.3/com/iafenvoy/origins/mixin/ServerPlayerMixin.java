package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EndRespawningEntity;
import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyPlayerSpawnPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnPosAngle;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ServerPlayer.class})
public abstract class ServerPlayerMixin extends Player implements ContainerListener, EndRespawningEntity {
   @Shadow
   private ResourceKey<Level> respawnDimension;
   @Shadow
   @Nullable
   private BlockPos respawnPosition;
   @Shadow
   @Final
   public MinecraftServer server;
   @Shadow
   public ServerGamePacketListenerImpl connection;
   @Shadow
   private boolean respawnForced;
   @Shadow
   private float respawnAngle;
   @Unique
   private boolean origins$isEndRespawning;

   @Shadow
   private static Optional<RespawnPosAngle> findRespawnAndUseSpawnBlock(ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive) {
      throw new AssertionError();
   }

   private ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
      super(world, pos, yaw, gameProfile);
   }

   @ModifyReturnValue(
      method = {"getRespawnDimension"},
      at = {@At("RETURN")}
   )
   private ResourceKey<Level> origins$modifySpawnPointDimension(ResourceKey<Level> original) {
      return this.origins$isEndRespawning() || this.respawnPosition != null && !this.origins$hasObstructedOriginalSpawnPoint()
         ? original
         : PowerHelper.get(this).getFirst(ModifyPlayerSpawnPower.class).map(ModifyPlayerSpawnPower::getDimension).orElse(original);
   }

   @ModifyReturnValue(
      method = {"getRespawnPosition"},
      at = {@At("RETURN")}
   )
   private BlockPos origins$modifySpawnPointPosition(BlockPos original) {
      if (this.origins$isEndRespawning() || !PowerHelper.get(this).noneActive(ModifyPlayerSpawnPower.class)) {
         return original;
      } else if (original == null) {
         return this.origins$findPowerSpawnPoint();
      } else if (this.origins$hasObstructedOriginalSpawnPoint()) {
         this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
         return this.origins$findPowerSpawnPoint();
      } else {
         return original;
      }
   }

   @ModifyReturnValue(
      method = {"isRespawnForced"},
      at = {@At("RETURN")}
   )
   private boolean origins$modifySpawnForced(boolean original) {
      return original
         || !this.origins$isEndRespawning()
            && (this.respawnPosition == null || this.origins$hasObstructedOriginalSpawnPoint())
            && PowerHelper.get(this).anyActive(ModifyPlayerSpawnPower.class);
   }

   @WrapOperation(
      method = {"findRespawnPositionAndUseSpawnBlock"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"
      )}
   )
   private Optional<RespawnPosAngle> origins$retryObstructedSpawnPointIfFailed(
      ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive, Operation<Optional<RespawnPosAngle>> original
   ) {
      Optional<RespawnPosAngle> originalRespawnPos = (Optional<RespawnPosAngle>)original.call(new Object[]{world, pos, spawnAngle, spawnForced, alive});
      return originalRespawnPos.isEmpty() && PowerHelper.get(this).anyActive(ModifyPlayerSpawnPower.class)
         ? Optional.ofNullable(DismountHelper.findSafeDismountLocation(this.getType(), world, pos, spawnForced)).map(newPos -> RespawnPosAngle.of(newPos, pos))
         : originalRespawnPos;
   }

   @Unique
   private boolean origins$hasObstructedOriginalSpawnPoint() {
      ServerLevel spawnPointWorld = this.server.getLevel(this.respawnDimension);
      return this.respawnPosition != null && spawnPointWorld != null
         ? findRespawnAndUseSpawnBlock(spawnPointWorld, this.respawnPosition, this.respawnAngle, this.respawnForced, true).isEmpty()
         : false;
   }

   @Unique
   private BlockPos origins$findPowerSpawnPoint() {
      return PowerHelper.get(this).getFirst(ModifyPlayerSpawnPower.class).flatMap(x -> x.getSpawn(this)).<BlockPos>map(Tuple::getB).orElse(null);
   }

   @Override
   public void origins$setEndRespawning(boolean endSpawn) {
      this.origins$isEndRespawning = endSpawn;
   }

   @Override
   public boolean origins$isEndRespawning() {
      return this.origins$isEndRespawning;
   }

   @Override
   public boolean origins$hasRealRespawnPoint() {
      return this.respawnPosition != null && !this.origins$hasObstructedOriginalSpawnPoint();
   }

   @ModifyExpressionValue(
      method = {"<init>"},
      at = {@At(
         value = "NEW",
         target = "()Lnet/minecraft/stats/ServerRecipeBook;"
      )}
   )
   private ServerRecipeBook origins$cachePlayerToRecipeBook(ServerRecipeBook original) {
      if (original instanceof PowerCraftingObject pco) {
         pco.origins$setPlayer(this);
      }

      return original;
   }
}
