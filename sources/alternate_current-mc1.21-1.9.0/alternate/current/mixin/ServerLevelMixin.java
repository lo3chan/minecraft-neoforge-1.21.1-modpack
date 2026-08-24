package alternate.current.mixin;

import alternate.current.interfaces.mixin.IServerLevel;
import alternate.current.wire.WireHandler;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public class ServerLevelMixin implements IServerLevel {
   private WireHandler wireHandler;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void alternate_current$parseConfig(
      MinecraftServer server,
      Executor executor,
      LevelStorageAccess storage,
      ServerLevelData data,
      ResourceKey<Level> key,
      LevelStem stem,
      ChunkProgressListener listener,
      boolean clientSide,
      long seed,
      List<CustomSpawner> customSpawners,
      boolean tickTime,
      RandomSequences randomSequences,
      CallbackInfo ci
   ) {
      this.wireHandler = new WireHandler((ServerLevel)this, storage);
   }

   @Override
   public WireHandler alternate_current$getWireHandler() {
      return this.wireHandler;
   }
}
