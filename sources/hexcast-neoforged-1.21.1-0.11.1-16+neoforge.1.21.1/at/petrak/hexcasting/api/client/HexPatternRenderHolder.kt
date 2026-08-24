package at.petrak.hexcasting.api.client

import at.petrak.hexcasting.api.casting.math.HexPattern
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nHexPatternRenderHolder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HexPatternRenderHolder.kt\nat/petrak/hexcasting/api/client/HexPatternRenderHolder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,19:1\n1#2:20\n*E\n"])
public data class HexPatternRenderHolder(pattern: HexPattern, lifetime: Int) {
   public final val pattern: HexPattern

   public final var lifetime: Int
      internal set

   private final var colourPos: Vec3?

   init {
      this.pattern = pattern;
      this.lifetime = lifetime;
   }

   public fun getColourPos(random: RandomSource): Vec3 {
      var var10000: Vec3 = this.colourPos;
      if (this.colourPos == null) {
         val it: HexPatternRenderHolder = this;
         val var4: Vec3 = new Vec3(random.nextDouble(), random.nextDouble(), random.nextDouble()).normalize().scale(3.0);
         this.colourPos = var4;
         var10000 = var4;
      }

      return var10000;
   }

   public fun tick() {
      this.lifetime--;
   }

   public operator fun component1(): HexPattern {
      return this.pattern;
   }

   public operator fun component2(): Int {
      return this.lifetime;
   }

   public fun copy(pattern: HexPattern = this.pattern, lifetime: Int = this.lifetime): HexPatternRenderHolder {
      return new HexPatternRenderHolder(pattern, lifetime);
   }

   public override fun toString(): String {
      return "HexPatternRenderHolder(pattern=${this.pattern}, lifetime=${this.lifetime})";
   }

   public override fun hashCode(): Int {
      return this.pattern.hashCode() * 31 + Integer.hashCode(this.lifetime);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is HexPatternRenderHolder) {
         return false;
      } else {
         val var2: HexPatternRenderHolder = other as HexPatternRenderHolder;
         if (!(this.pattern == (other as HexPatternRenderHolder).pattern)) {
            return false;
         } else {
            return this.lifetime == var2.lifetime;
         }
      }
   }
}
