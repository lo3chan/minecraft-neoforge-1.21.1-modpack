@file:JvmName(name = "MediaHelper")

@file:SourceDebugExtension(["SMAP\nMediaHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaHelper.kt\nat/petrak/hexcasting/api/utils/MediaHelper\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,112:1\n1869#2,2:113\n*S KotlinDebug\n*F\n+ 1 MediaHelper.kt\nat/petrak/hexcasting/api/utils/MediaHelper\n*L\n67#1:113,2\n*E\n"])

package at.petrak.hexcasting.api.utils

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.xplat.IXplatAbstractions
import java.util.ArrayList
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack

public fun isMediaItem(stack: ItemStack): Boolean {
   val var10000: ADMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack);
   if (var10000 == null) {
      return false;
   } else if (!var10000.canProvide()) {
      return false;
   } else {
      return var10000.withdrawMedia(-1L, true) > 0L;
   }
}

@JvmOverloads
public fun extractMedia(stack: ItemStack, cost: Long = -1L, drainForBatteries: Boolean = false, simulate: Boolean = false): Long {
   val var10000: ADMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack);
   return if (var10000 == null) 0L else extractMedia(var10000, cost, drainForBatteries, simulate);
}

@JvmSynthetic
fun `extractMedia$default`(var0: ItemStack, var1: Long, var3: Boolean, var4: Boolean, var5: Int, var6: Any): Long {
   if ((var5 and 2) != 0) {
      var1 = -1L;
   }

   if ((var5 and 4) != 0) {
      var3 = false;
   }

   if ((var5 and 8) != 0) {
      var4 = false;
   }

   return extractMedia(var0, var1, var3, var4);
}

public fun extractMedia(holder: ADMediaHolder, cost: Long = -1L, drainForBatteries: Boolean = false, simulate: Boolean = false): Long {
   return if (drainForBatteries && !holder.canConstructBattery()) 0L else holder.withdrawMedia(cost, simulate);
}

@JvmSynthetic
fun `extractMedia$default`(var0: ADMediaHolder, var1: Long, var3: Boolean, var4: Boolean, var5: Int, var6: Any): Long {
   if ((var5 and 2) != 0) {
      var1 = -1L;
   }

   if ((var5 and 4) != 0) {
      var3 = false;
   }

   if ((var5 and 8) != 0) {
      var4 = false;
   }

   return extractMedia(var0, var1, var3, var4);
}

public fun scanPlayerForMediaStuff(player: ServerPlayer): List<ADMediaHolder> {
   val sources: java.util.List = new ArrayList();
   val var10000: NonNullList = player.getInventory().items;
   val var9: java.util.Collection = var10000 as java.util.Collection;
   val var10001: NonNullList = player.getInventory().armor;
   val var10: java.util.Collection = CollectionsKt.plus(var9, var10001 as java.lang.Iterable);

   val `$this$forEach$iv`: java.lang.Iterable;
   for (Object element$iv : $this$forEach$iv) {
      val holder: ADMediaHolder = HexAPI.instance().findMediaHolder(`element$iv` as ItemStack);
      if (holder != null) {
         sources.add(holder);
      }
   }

   CollectionsKt.sortWith(sources, MediaHelper::scanPlayerForMediaStuff$lambda$1);
   CollectionsKt.reverse(sources);
   return sources;
}

public fun compareMediaItem(aMedia: ADMediaHolder, bMedia: ADMediaHolder): Int {
   val priority: Int = aMedia.getConsumptionPriority() - bMedia.getConsumptionPriority();
   return if (priority != 0) priority else (int)RangesKt.coerceIn(aMedia.withdrawMedia(-1L, true) - bMedia.withdrawMedia(-1L, true), -2147483648L, 2147483647L);
}

public fun mediaBarColor(media: Long, maxMedia: Long): Int {
   return Mth.color(
      Mth.lerp(if (maxMedia == 0L) 0.0F else (float)media / (float)maxMedia, 84.0F, 254.0F) / 255.0F,
      Mth.lerp(if (maxMedia == 0L) 0.0F else (float)media / (float)maxMedia, 57.0F, 203.0F) / 255.0F,
      Mth.lerp(if (maxMedia == 0L) 0.0F else (float)media / (float)maxMedia, 138.0F, 230.0F) / 255.0F
   );
}

public fun mediaBarWidth(media: Long, maxMedia: Long): Int {
   return MathKt.roundToInt(13.0F * (if (maxMedia == 0L) 0.0F else (float)media / (float)maxMedia));
}

@JvmOverloads
fun extractMedia(stack: ItemStack, cost: Long, drainForBatteries: Boolean): Long {
   return extractMedia$default(stack, cost, drainForBatteries, false, 8, null);
}

@JvmOverloads
fun extractMedia(stack: ItemStack, cost: Long): Long {
   return extractMedia$default(stack, cost, false, false, 12, null);
}

@JvmOverloads
fun extractMedia(stack: ItemStack): Long {
   return extractMedia$default(stack, 0L, false, false, 14, null);
}

fun `scanPlayerForMediaStuff$lambda$1`(`$tmp0`: Function2, p0: Any, p1: Any): Int {
   return (`$tmp0`.invoke(p0, p1) as java.lang.Number).intValue();
}
