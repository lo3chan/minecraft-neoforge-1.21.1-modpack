package net.joefoxe.hexerei.block.connected;

public class CTSpriteShiftEntry extends SpriteShiftEntry {
   protected final CTType type;

   public CTSpriteShiftEntry(CTType type) {
      this.type = type;
   }

   public CTType getType() {
      return this.type;
   }

   public float getTargetU(float localU, int index) {
      float uOffset = index % this.type.getSheetSize();
      return this.getTarget().getU((getUnInterpolatedU(this.getOriginal(), localU) + uOffset) / this.type.getSheetSize());
   }

   public float getTargetV(float localV, int index) {
      float vOffset = index / this.type.getSheetSize();
      return this.getTarget().getV((getUnInterpolatedV(this.getOriginal(), localV) + vOffset) / this.type.getSheetSize());
   }
}
