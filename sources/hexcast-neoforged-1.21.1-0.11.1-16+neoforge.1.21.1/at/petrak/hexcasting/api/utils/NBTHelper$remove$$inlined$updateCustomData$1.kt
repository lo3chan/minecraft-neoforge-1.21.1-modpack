package at.petrak.hexcasting.api.utils

import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nNBTHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper$updateCustomData$1\n+ 2 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper\n*L\n1#1,189:1\n243#2:190\n*E\n"])
internal class `NBTHelper$remove$$inlined$updateCustomData$1`<T> : Consumer {
   fun `NBTHelper$remove$$inlined$updateCustomData$1`(var1: java.lang.String) {
      this.$key$inlined = var1;
   }

   fun accept(tag: CompoundTag) {
      tag.remove(this.$key$inlined);
   }
}
