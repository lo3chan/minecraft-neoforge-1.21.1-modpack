package net.cibernet.alchemancy.events.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.StreamSupport;
import net.cibernet.alchemancy.blocks.GustBasketBlock;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.cibernet.alchemancy.network.S2CSetGlobalTimerPayload;
import net.cibernet.alchemancy.registries.AlchemancyPoiTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class GeneralEventHandler {
   private static final HashMap<Block, BiConsumer<ServerLevel, BlockPos>> TICKING_BLOCK_FUNCTIONS = new HashMap<>();

   public static void registerTickingBlockFunction(Block block, BiConsumer<ServerLevel, BlockPos> function) {
      TICKING_BLOCK_FUNCTIONS.put(block, function);
   }

   @SubscribeEvent
   private static void onLogIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PacketDistributor.sendToPlayer(player, new S2CSetGlobalTimerPayload(AlchemancyServerData.getGlobalTimer()), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public static void onServerTick(Pre event) {
      if (AlchemancyServerData.getGlobalTimer() == -1L) {
         long t = event.getServer().overworld().getDayTime();
         AlchemancyServerData.setGlobalTimer(t);
         PacketDistributor.sendToAllPlayers(new S2CSetGlobalTimerPayload(t), new CustomPacketPayload[0]);
      } else {
         AlchemancyServerData.tickGlobalTimer();
      }
   }

   @SubscribeEvent
   public static void onLevelTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Pre event) {
      Level level = event.getLevel();
      if (level.isClientSide()) {
         level.updateSkyBrightness();
      } else if (level instanceof ServerLevel serverLevel) {
         PoiManager poiManager = serverLevel.getPoiManager();
         List<LevelChunk> chunks = StreamSupport.stream(serverLevel.getChunkSource().chunkMap.getChunks().spliterator(), false)
            .<LevelChunk>map(ChunkHolder::getTickingChunk)
            .filter(Objects::nonNull)
            .toList();
         if (chunks.isEmpty()) {
            return;
         }

         RootedItemBlockEntity.cahceRootedItems(serverLevel, chunks);

         for (LevelChunk chunk : chunks) {
            poiManager.getInChunk(type -> type.equals(AlchemancyPoiTypes.TICKING_BLOCK), chunk.getPos(), Occupancy.ANY)
               .<BlockPos>map(PoiRecord::getPos)
               .forEach(pos -> {
                  Block block = serverLevel.getBlockState(pos).getBlock();
                  if (TICKING_BLOCK_FUNCTIONS.containsKey(block)) {
                     TICKING_BLOCK_FUNCTIONS.get(block).accept(serverLevel, pos);
                  }
               });
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre event) {
      if (event.getEntity().level().isClientSide()) {
         GustBasketBlock.clientPlayerTick(event.getEntity());
      }
   }
}
