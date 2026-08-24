package io.wispforest.owo.ops;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class WorldOps {
   private WorldOps() {
   }

   public static void breakBlockWithItem(Level world, BlockPos pos, ItemStack breakItem) {
      breakBlockWithItem(world, pos, breakItem, null);
   }

   public static void breakBlockWithItem(Level world, BlockPos pos, ItemStack breakItem, @Nullable Entity breakingEntity) {
      BlockEntity breakEntity = world.getBlockState(pos).getBlock() instanceof EntityBlock ? world.getBlockEntity(pos) : null;
      Block.dropResources(world.getBlockState(pos), world, pos, breakEntity, breakingEntity, breakItem);
      world.destroyBlock(pos, false, breakingEntity);
   }

   public static void playSound(Level world, Vec3 pos, SoundEvent sound, SoundSource category) {
      playSound(world, BlockPos.containing(pos), sound, category, 1.0F, 1.0F);
   }

   public static void playSound(Level world, BlockPos pos, SoundEvent sound, SoundSource category) {
      playSound(world, pos, sound, category, 1.0F, 1.0F);
   }

   public static void playSound(Level world, Vec3 pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
      world.playSound(null, BlockPos.containing(pos), sound, category, volume, pitch);
   }

   public static void playSound(Level world, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
      world.playSound(null, pos, sound, category, volume, pitch);
   }

   public static void updateIfOnServer(Level world, BlockPos pos) {
      if (world instanceof ServerLevel serverWorld) {
         serverWorld.getChunkSource().blockChanged(pos);
      }
   }

   public static void teleportToWorld(ServerPlayer player, ServerLevel target, Vec3 pos) {
      teleportToWorld(player, target, pos, 0.0F, 0.0F);
   }

   public static void teleportToWorld(ServerPlayer player, ServerLevel target, Vec3 pos, float yaw, float pitch) {
      player.teleportTo(target, pos.x, pos.y, pos.z, yaw, pitch);
      player.giveExperiencePoints(0);
      player.getActiveEffects().forEach(effect -> player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect, false)));
   }
}
