package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Info("Invoked when a detector block registered in KubeJS receives a block update.\n\n`Powered`/`Unpowered` event will be fired when the detector block is powered/unpowered.\n")
public class DetectorBlockKubeEvent implements KubeLevelEvent {
   private final String detectorId;
   private final Level level;
   private final boolean powered;
   private final LevelBlock block;

   public DetectorBlockKubeEvent(String i, Level l, BlockPos p, boolean pow) {
      this.detectorId = i;
      this.level = l;
      this.powered = pow;
      this.block = this.level.kjs$getBlock(p);
   }

   @Info("The id of the detector block when it was registered.")
   public String getDetectorId() {
      return this.detectorId;
   }

   @Info("The level where the detector block is located.")
   @Override
   public Level getLevel() {
      return this.level;
   }

   @Info("If the detector block is powered.")
   public boolean isPowered() {
      return this.powered;
   }

   @Info("The detector block.")
   public LevelBlock getBlock() {
      return this.block;
   }
}
