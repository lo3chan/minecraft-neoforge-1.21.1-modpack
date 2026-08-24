package com.aetherteam.aether.block.utility;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.blockentity.SunAltarBlockEntity;
import com.aetherteam.aether.command.SunAltarWhitelist;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.network.packet.clientbound.OpenSunAltarPacket;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public class SunAltarBlock extends BaseEntityBlock {
   public static final MapCodec<SunAltarBlock> CODEC = simpleCodec(SunAltarBlock::new);

   public SunAltarBlock(Properties properties) {
      super(properties);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SunAltarBlockEntity(pos, state);
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!level.isClientSide()) {
         if ((Boolean)AetherConfig.SERVER.sun_altar_whitelist.get()
            && !player.hasPermissions(4)
            && !SunAltarWhitelist.INSTANCE.isWhiteListed(player.getGameProfile())) {
            player.displayClientMessage(Component.translatable("aether.sun_altar.no_permission"), true);
         } else if (this.canControlDimension(level)) {
            if (level.hasData(AetherDataAttachments.AETHER_TIME)) {
               if (!((AetherTimeAttachment)level.getData(AetherDataAttachments.AETHER_TIME)).isEternalDay()) {
                  this.openScreen(level, pos, player, AetherTimeAttachment.getTicksPerDay());
               } else {
                  player.displayClientMessage(Component.translatable("aether.sun_altar.in_control"), true);
               }
            } else {
               this.openScreen(level, pos, player, AetherTimeAttachment.getTicksPerDay());
            }
         } else {
            player.displayClientMessage(Component.translatable("aether.sun_altar.no_power"), true);
         }
      }

      return InteractionResult.SUCCESS;
   }

   private boolean canControlDimension(Level level) {
      boolean defaultCheck = ((List)AetherConfig.SERVER.sun_altar_dimensions.get()).contains(level.dimension().location().toString());
      return !AetherConfig.SERVER.sync_aether_time.get()
         ? defaultCheck
         : level.dimension() == Level.OVERWORLD || level.dimension() == AetherDimensions.AETHER_LEVEL || defaultCheck;
   }

   protected void openScreen(Level level, BlockPos pos, Player player, int timeScale) {
      if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof SunAltarBlockEntity sunAltar) {
         PacketDistributor.sendToPlayer(serverPlayer, new OpenSunAltarPacket(sunAltar.getName(), timeScale), new CustomPacketPayload[0]);
      }
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}
