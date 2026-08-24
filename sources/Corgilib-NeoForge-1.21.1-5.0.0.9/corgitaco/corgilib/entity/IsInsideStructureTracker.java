package corgitaco.corgilib.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.network.EntityIsInsideStructureTrackerUpdatePacket;
import corgitaco.corgilib.platform.PlatformNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class IsInsideStructureTracker {
   private IsInsideStructureTracker.IsInside tracker = new IsInsideStructureTracker.IsInside(false, false);

   public void setInside(Level world, Entity entity, IsInsideStructureTracker.IsInside isInside) {
      this.tracker = isInside;
      if (!world.isClientSide) {
         PlatformNetwork.NETWORK.sendToAllClients(((ServerLevel)world).players(), new EntityIsInsideStructureTrackerUpdatePacket(entity.getId(), isInside));
      }
   }

   public IsInsideStructureTracker.IsInside getTracker() {
      return this.tracker;
   }

   public interface Access {
      IsInsideStructureTracker getIsInsideStructureTracker();
   }

   public static class IsInside {
      public static final Codec<IsInsideStructureTracker.IsInside> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               Codec.BOOL.fieldOf("insideStructure").forGetter(isInside -> isInside.insideStructure),
               Codec.BOOL.fieldOf("insideStructurePiece").forGetter(isInside -> isInside.insideStructure)
            )
            .apply(builder, IsInsideStructureTracker.IsInside::new)
      );
      private boolean insideStructure;
      private boolean insideStructurePiece;

      public IsInside(boolean insideStructure, boolean insideStructurePiece) {
         this.insideStructure = insideStructure;
         this.insideStructurePiece = insideStructurePiece;
      }

      public boolean isInsideStructure() {
         return this.insideStructure;
      }

      public boolean isInsideStructurePiece() {
         return this.insideStructurePiece;
      }

      public IsInsideStructureTracker.IsInside setInsideStructure(boolean insideStructure) {
         this.insideStructure = insideStructure;
         return this;
      }

      public IsInsideStructureTracker.IsInside setInsideStructurePiece(boolean insideStructurePiece) {
         this.insideStructurePiece = insideStructurePiece;
         return this;
      }
   }
}
