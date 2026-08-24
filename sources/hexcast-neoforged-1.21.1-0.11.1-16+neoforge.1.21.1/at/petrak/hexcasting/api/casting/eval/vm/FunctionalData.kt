package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.casting.iota.Iota

public data class FunctionalData(stack: List<Iota>, parenCount: Int, parenthesized: List<Iota>, escapeNext: Boolean, ravenmind: Iota?) {
   public final val stack: List<Iota>
   public final val parenCount: Int
   public final val parenthesized: List<Iota>
   public final val escapeNext: Boolean
   public final val ravenmind: Iota?

   init {
      this.stack = stack;
      this.parenCount = parenCount;
      this.parenthesized = parenthesized;
      this.escapeNext = escapeNext;
      this.ravenmind = ravenmind;
   }

   public operator fun component1(): List<Iota> {
      return this.stack;
   }

   public operator fun component2(): Int {
      return this.parenCount;
   }

   public operator fun component3(): List<Iota> {
      return this.parenthesized;
   }

   public operator fun component4(): Boolean {
      return this.escapeNext;
   }

   public operator fun component5(): Iota? {
      return this.ravenmind;
   }

   public fun copy(
      stack: List<Iota> = this.stack,
      parenCount: Int = this.parenCount,
      parenthesized: List<Iota> = this.parenthesized,
      escapeNext: Boolean = this.escapeNext,
      ravenmind: Iota? = this.ravenmind
   ): FunctionalData {
      return new FunctionalData(stack, parenCount, parenthesized, escapeNext, ravenmind);
   }

   public override fun toString(): String {
      return "FunctionalData(stack=${this.stack}, parenCount=${this.parenCount}, parenthesized=${this.parenthesized}, escapeNext=${this.escapeNext}, ravenmind=${this.ravenmind})";
   }

   public override fun hashCode(): Int {
      return (
               ((this.stack.hashCode() * 31 + Integer.hashCode(this.parenCount)) * 31 + this.parenthesized.hashCode()) * 31
                  + java.lang.Boolean.hashCode(this.escapeNext)
            )
            * 31
         + (if (this.ravenmind == null) 0 else this.ravenmind.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is FunctionalData) {
         return false;
      } else {
         val var2: FunctionalData = other as FunctionalData;
         if (!(this.stack == (other as FunctionalData).stack)) {
            return false;
         } else if (this.parenCount != var2.parenCount) {
            return false;
         } else if (!(this.parenthesized == var2.parenthesized)) {
            return false;
         } else if (this.escapeNext != var2.escapeNext) {
            return false;
         } else {
            return this.ravenmind == var2.ravenmind;
         }
      }
   }
}
