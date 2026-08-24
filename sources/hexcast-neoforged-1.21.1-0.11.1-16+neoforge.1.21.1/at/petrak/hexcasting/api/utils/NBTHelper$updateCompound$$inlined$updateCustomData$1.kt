package at.petrak.hexcasting.api.utils

import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nNBTHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper$updateCustomData$1\n+ 2 NBTHelper.kt\nat/petrak/hexcasting/api/utils/NBTHelper\n*L\n1#1,189:1\n286#2,4:190\n*E\n"])
internal class `NBTHelper$updateCompound$$inlined$updateCustomData$1`<T> : Consumer {
   fun `NBTHelper$updateCompound$$inlined$updateCustomData$1`(var1: java.lang.String, var2: Consumer) {
      this.$key$inlined = var1;
      this.$updater$inlined = var2;
   }

   fun accept(tag: CompoundTag) {
      val child: CompoundTag = NBTHelper.getOrCreateCompound(tag, this.$key$inlined);
      this.$updater$inlined.accept(child);
      tag.put(this.$key$inlined, child as Tag);
   }
}
