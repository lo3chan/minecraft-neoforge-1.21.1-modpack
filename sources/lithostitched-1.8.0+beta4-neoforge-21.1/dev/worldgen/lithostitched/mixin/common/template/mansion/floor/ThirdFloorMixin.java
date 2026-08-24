package dev.worldgen.lithostitched.mixin.common.template.mansion.floor;

import dev.worldgen.lithostitched.duck.MansionRoomDuck;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.ThirdFloorRoomCollection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ThirdFloorRoomCollection.class})
public abstract class ThirdFloorMixin implements MansionRoomDuck {
   @Override
   public int lithostitched$floorNumber() {
      return 3;
   }
}
