package com.seibel.distanthorizons.core.pos.blockPos;

import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.network.INetworkObject;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DhBlockPos implements INetworkObject {
   public static final DhBlockPos ZERO = new DhBlockPos(0, 0, 0);
   protected int x;
   protected int y;
   protected int z;

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public DhBlockPos(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public DhBlockPos() {
      this(0, 0, 0);
   }

   public DhBlockPos(DhBlockPos pos) {
      this(pos.x, pos.y, pos.z);
   }

   public DhBlockPos(DhBlockPos2D pos, int y) {
      this(pos.x, y, pos.z);
   }

   public DhBlockPos createOffset(EDhDirection direction) {
      return this.mutateOrCreateOffset(direction.normal.x, direction.normal.y, direction.normal.z, null);
   }

   public void mutateOffset(EDhDirection direction, @NotNull DhBlockPosMutable mutablePos) {
      this.mutateOrCreateOffset(direction.normal.x, direction.normal.y, direction.normal.z, mutablePos);
   }

   public DhBlockPos createOffset(int x, int y, int z) {
      return this.mutateOrCreateOffset(x, y, z, null);
   }

   public void mutateOffset(int x, int y, int z, @NotNull DhBlockPosMutable mutablePos) {
      this.mutateOrCreateOffset(x, y, z, mutablePos);
   }

   protected DhBlockPos mutateOrCreateOffset(int x, int y, int z, @Nullable DhBlockPosMutable mutablePos) {
      int newX = this.x + x;
      int newY = this.y + y;
      int newZ = this.z + z;
      if (mutablePos != null) {
         mutablePos.x = newX;
         mutablePos.y = newY;
         mutablePos.z = newZ;
         return mutablePos;
      } else {
         return new DhBlockPos(newX, newY, newZ);
      }
   }

   public DhBlockPos createChunkRelativePos() {
      return this.mutateOrCreateChunkRelativePos(null);
   }

   public void mutateToChunkRelativePos(DhBlockPosMutable mutableBlockPos) {
      this.mutateOrCreateChunkRelativePos(mutableBlockPos);
   }

   protected DhBlockPos mutateOrCreateChunkRelativePos(@Nullable DhBlockPosMutable mutableBlockPos) {
      int relX = convertWorldPosToChunkRelative(this.x);
      int relZ = convertWorldPosToChunkRelative(this.z);
      if (mutableBlockPos != null) {
         mutableBlockPos.x = relX;
         mutableBlockPos.y = this.y;
         mutableBlockPos.z = relZ;
         return mutableBlockPos;
      } else {
         return new DhBlockPos(relX, this.y, relZ);
      }
   }

   protected static int convertWorldPosToChunkRelative(int xOrZ) {
      int relPos = xOrZ % 16;
      return relPos < 0 ? relPos + 16 : relPos;
   }

   public int getManhattanDistance(DhBlockPos otherPos) {
      return Math.abs(this.x - otherPos.x) + Math.abs(this.y - otherPos.y) + Math.abs(this.z - otherPos.z);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         DhBlockPos that = (DhBlockPos)obj;
         return this.x == that.x && this.y == that.y && this.z == that.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.z);
   }

   @Override
   public String toString() {
      return "DHBlockPos[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   @Override
   public void encode(ByteBuf out) {
      out.writeInt(this.x);
      out.writeInt(this.y);
      out.writeInt(this.z);
   }

   @Override
   public void decode(ByteBuf in) {
      this.x = in.readInt();
      this.y = in.readInt();
      this.z = in.readInt();
   }
}
