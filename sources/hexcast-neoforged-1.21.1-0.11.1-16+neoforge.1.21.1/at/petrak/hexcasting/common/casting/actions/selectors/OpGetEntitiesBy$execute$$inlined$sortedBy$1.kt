package at.petrak.hexcasting.common.casting.actions.selectors

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 OpGetEntitiesBy.kt\nat/petrak/hexcasting/common/casting/actions/selectors/OpGetEntitiesBy\n*L\n1#1,102:1\n33#2:103\n*E\n"])
internal class `OpGetEntitiesBy$execute$$inlined$sortedBy$1`<T> : Comparator {
   fun `OpGetEntitiesBy$execute$$inlined$sortedBy$1`(var1: Vec3) {
      this.$pos$inlined = var1;
   }

   override final fun compare(a: T, b: T): Int {
      return ComparisonsKt.compareValues((a as Entity).distanceToSqr(this.$pos$inlined), (b as Entity).distanceToSqr(this.$pos$inlined));
   }
}
