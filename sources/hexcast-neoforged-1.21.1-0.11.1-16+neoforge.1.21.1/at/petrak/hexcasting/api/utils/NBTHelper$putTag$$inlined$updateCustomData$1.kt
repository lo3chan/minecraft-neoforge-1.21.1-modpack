package at.petrak.hexcasting.api.utils

import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nNBTHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper$updateCustomData$1\n+ 2 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper\n*L\n1#1,189:1\n239#2:190\n*E\n"])
internal class `NBTHelper$putTag$$inlined$updateCustomData$1`<T> : Consumer {
   fun `NBTHelper$putTag$$inlined$updateCustomData$1`(var1: java.lang.String, var2: Tag) {
      this.$key$inlined = var1;
      this.$value$inlined = var2;
   }

   fun accept(tag: CompoundTag) {
      tag.put(this.$key$inlined, this.$value$inlined);
   }
}
