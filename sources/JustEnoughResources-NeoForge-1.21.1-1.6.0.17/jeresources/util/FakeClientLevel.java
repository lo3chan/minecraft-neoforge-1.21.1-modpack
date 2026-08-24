package jeresources.util;

import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

public class FakeClientLevel extends ClientLevel {
   public static final ClientLevelData clientLevelData = new ClientLevelData(Difficulty.NORMAL, false, false);

   public FakeClientLevel() {
      super(
         null,
         clientLevelData,
         Level.OVERWORLD,
         DimensionHelper.getType(BuiltinDimensionTypes.OVERWORLD),
         0,
         0,
         () -> InactiveProfiler.INSTANCE,
         Minecraft.getInstance().levelRenderer,
         false,
         1234567L
      );
   }

   public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
   }

   public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent soundIn, SoundSource source, float volume, float pitch) {
   }

   public void playSound(@Nullable Player p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundSource source, float p_184133_5_, float p_184133_6_) {
   }

   @Nullable
   public Entity getEntity(int p_73045_1_) {
      return null;
   }

   public ClientChunkCache getChunkSource() {
      return new ClientChunkCache(this, 0) {
         @Nullable
         public LevelChunk getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
            return super.getChunk(x, z, chunkStatus, requireChunk);
         }

         public void tick(BooleanSupplier booleanSupplier, boolean bool) {
         }

         public String gatherStats() {
            return "emptychunkprovider";
         }

         public LevelLightEngine getLightEngine() {
            return null;
         }

         public BlockGetter getLevel() {
            return FakeClientLevel.this;
         }
      };
   }

   @Nullable
   public MapItemSavedData getMapData(MapId mapId) {
      return super.getMapData(mapId);
   }

   public void setMapData(MapId mapId, MapItemSavedData mapData) {
      super.setMapData(mapId, mapData);
   }

   public MapId getFreeMapId() {
      return new MapId(0);
   }

   public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
   }

   public Scoreboard getScoreboard() {
      return null;
   }

   public RecipeManager getRecipeManager() {
      return null;
   }

   public boolean hasChunk(int p_217354_1_, int p_217354_2_) {
      return false;
   }

   public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
   }
}
