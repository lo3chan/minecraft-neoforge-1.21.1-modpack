package at.petrak.hexcasting.api.client

import at.petrak.hexcasting.api.casting.math.HexPattern
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nClientCastingStack.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClientCastingStack.kt\nat/petrak/hexcasting/api/client/ClientCastingStack\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n1869#2,2:62\n1869#2,2:64\n1869#2,2:66\n*S KotlinDebug\n*F\n+ 1 ClientCastingStack.kt\nat/petrak/hexcasting/api/client/ClientCastingStack\n*L\n25#1:62,2\n42#1:64,2\n51#1:66,2\n*E\n"])
public class ClientCastingStack {
   private final var patterns: ArrayList<HexPatternRenderHolder> = new ArrayList()
   private final var toRemove: MutableSet<HexPatternRenderHolder> = (new LinkedHashSet()) as java.util.Set
   private final var toAdd: ArrayList<HexPatternRenderHolder> = new ArrayList()

   public fun addPattern(pattern: HexPattern?, lifetime: Int) {
      if (pattern != null) {
         if (!this.patterns.stream().anyMatch(ClientCastingStack::addPattern$lambda$1)) {
            if (this.patterns.size() > 100) {
               this.patterns.remove(0);
            }

            this.patterns.add(new HexPatternRenderHolder(pattern, lifetime));
         }
      }
   }

   public fun slowClear() {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as HexPatternRenderHolder).setLifetime(Math.min((`element$iv` as HexPatternRenderHolder).getLifetime(), 140));
      }
   }

   public fun getPatterns(): List<HexPatternRenderHolder> {
      return this.patterns;
   }

   public fun getPattern(index: Int): HexPattern? {
      val var10000: HexPatternRenderHolder = CollectionsKt.getOrNull(this.patterns, index) as HexPatternRenderHolder;
      return if (var10000 != null) var10000.getPattern() else null;
   }

   public fun getPatternHolder(index: Int): HexPatternRenderHolder? {
      return CollectionsKt.getOrNull(this.patterns, index) as HexPatternRenderHolder;
   }

   public fun size(): Int {
      return this.patterns.size();
   }

   public fun tick() {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val pattern: HexPatternRenderHolder = `element$iv` as HexPatternRenderHolder;
         if (this.patterns.size() > 100) {
            this.patterns.remove(0);
         }

         this.patterns.add(pattern);
      }

      this.toAdd.clear();

      for (Object element$iv : $this$forEach$iv) {
         val var11: HexPatternRenderHolder = var10 as HexPatternRenderHolder;
         (var10 as HexPatternRenderHolder).tick();
         if (var11.getLifetime() <= 0) {
            this.toRemove.add(var11);
         }
      }

      this.patterns.removeAll(this.toRemove);
      this.toRemove.clear();
   }

   @JvmStatic
   fun `addPattern$lambda$0`(`$pattern`: HexPattern, patternRenderHolder: HexPatternRenderHolder): Boolean {
      return patternRenderHolder.getPattern().hashCode() == `$pattern`.hashCode();
   }

   @JvmStatic
   fun `addPattern$lambda$1`(`$tmp0`: Function1, p0: Any): Boolean {
      return `$tmp0`.invoke(p0) as java.lang.Boolean;
   }
}
