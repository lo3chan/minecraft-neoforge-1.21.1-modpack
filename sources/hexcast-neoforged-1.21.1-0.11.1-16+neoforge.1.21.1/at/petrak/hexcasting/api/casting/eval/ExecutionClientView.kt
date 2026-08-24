package at.petrak.hexcasting.api.casting.eval

import net.minecraft.nbt.CompoundTag

public data class ExecutionClientView(isStackClear: Boolean, resolutionType: ResolvedPatternType, stackDescs: List<CompoundTag>, ravenmind: CompoundTag?) {
   public final val isStackClear: Boolean
   public final val resolutionType: ResolvedPatternType
   public final val stackDescs: List<CompoundTag>
   public final val ravenmind: CompoundTag?

   init {
      this.isStackClear = isStackClear;
      this.resolutionType = resolutionType;
      this.stackDescs = stackDescs;
      this.ravenmind = ravenmind;
   }

   public operator fun component1(): Boolean {
      return this.isStackClear;
   }

   public operator fun component2(): ResolvedPatternType {
      return this.resolutionType;
   }

   public operator fun component3(): List<CompoundTag> {
      return this.stackDescs;
   }

   public operator fun component4(): CompoundTag? {
      return this.ravenmind;
   }

   public fun copy(
      isStackClear: Boolean = this.isStackClear,
      resolutionType: ResolvedPatternType = this.resolutionType,
      stackDescs: List<CompoundTag> = this.stackDescs,
      ravenmind: CompoundTag? = this.ravenmind
   ): ExecutionClientView {
      return new ExecutionClientView(isStackClear, resolutionType, stackDescs, ravenmind);
   }

   public override fun toString(): String {
      return "ExecutionClientView(isStackClear=${this.isStackClear}, resolutionType=${this.resolutionType}, stackDescs=${this.stackDescs}, ravenmind=${this.ravenmind})";
   }

   public override fun hashCode(): Int {
      return ((java.lang.Boolean.hashCode(this.isStackClear) * 31 + this.resolutionType.hashCode()) * 31 + this.stackDescs.hashCode()) * 31
         + (if (this.ravenmind == null) 0 else this.ravenmind.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ExecutionClientView) {
         return false;
      } else {
         val var2: ExecutionClientView = other as ExecutionClientView;
         if (this.isStackClear != (other as ExecutionClientView).isStackClear) {
            return false;
         } else if (this.resolutionType != var2.resolutionType) {
            return false;
         } else if (!(this.stackDescs == var2.stackDescs)) {
            return false;
         } else {
            return this.ravenmind == var2.ravenmind;
         }
      }
   }
}
