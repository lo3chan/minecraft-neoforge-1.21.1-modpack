@file:JvmName(name = "OperatorUtils")

@file:SourceDebugExtension(["SMAP\nActionUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,312:1\n1#2:313\n*E\n"])

package at.petrak.hexcasting.api.casting

import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.utils.HexUtils
import com.mojang.datafixers.util.Either
import java.util.function.DoubleUnaryOperator
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.phys.Vec3

public final val asActionResult: List<BooleanIota>
   public final inline get() {
      return CollectionsKt.listOf(new BooleanIota(`$this$asActionResult`));
   }


public final val asActionResult: List<DoubleIota>
   public final inline get() {
      return CollectionsKt.listOf(new DoubleIota(`$this$asActionResult`));
   }


public final val asActionResult: List<DoubleIota>
   public final inline get() {
      return CollectionsKt.listOf(new DoubleIota(`$this$asActionResult`.doubleValue()));
   }


public final val asActionResult: List<ListIota>
   public final inline get() {
      return CollectionsKt.listOf(new ListIota(`$this$asActionResult`));
   }


public final val asActionResult: List<ListIota>
   public final inline get() {
      return CollectionsKt.listOf(new ListIota(`$this$asActionResult`));
   }


public final val asActionResult: List<Vec3Iota>
   public final inline get() {
      return CollectionsKt.listOf(new Vec3Iota(Vec3.atCenterOf(`$this$asActionResult` as Vec3i)));
   }


public final val asActionResult: List<Vec3Iota>
   public final inline get() {
      return CollectionsKt.listOf(new Vec3Iota(new Vec3(`$this$asActionResult`)));
   }


public final val asActionResult: List<Vec3Iota>
   public final inline get() {
      return CollectionsKt.listOf(new Vec3Iota(`$this$asActionResult`));
   }


public final val asActionResult: List<Iota>
   public final inline get() {
      return CollectionsKt.listOf(if (`$this$asActionResult` == null) new NullIota() else new EntityIota(`$this$asActionResult`));
   }


public final val asActionResult: List<PatternIota>
   public final inline get() {
      return CollectionsKt.listOf(new PatternIota(`$this$asActionResult`));
   }


public fun List<Iota>.getDouble(idx: Int, argc: Int = 0): Double {
   if (0 <= idx && idx < `$this$getDouble`.size()) {
      val x: Iota = `$this$getDouble`.get(idx) as Iota;
      if (x is DoubleIota) {
         return (x as DoubleIota).getDouble();
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "double");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getDouble`.size());
   }
}

@JvmSynthetic
fun `getDouble$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Double {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getDouble(var0, var1, var2);
}

public fun List<Iota>.getEntity(idx: Int, argc: Int = 0): Entity {
   if (0 <= idx && idx < `$this$getEntity`.size()) {
      val x: Iota = `$this$getEntity`.get(idx) as Iota;
      if (x is EntityIota) {
         val var10000: Entity = (x as EntityIota).getEntity();
         return var10000;
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "entity");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getEntity`.size());
   }
}

@JvmSynthetic
fun `getEntity$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Entity {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getEntity(var0, var1, var2);
}

public fun List<Iota>.getList(idx: Int, argc: Int = 0): SpellList {
   if (0 <= idx && idx < `$this$getList`.size()) {
      val x: Iota = `$this$getList`.get(idx) as Iota;
      if (x is ListIota) {
         val var10000: SpellList = (x as ListIota).getList();
         return var10000;
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "list");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getList`.size());
   }
}

@JvmSynthetic
fun `getList$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): SpellList {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getList(var0, var1, var2);
}

public fun List<Iota>.getPattern(idx: Int, argc: Int = 0): HexPattern {
   if (0 <= idx && idx < `$this$getPattern`.size()) {
      val x: Iota = `$this$getPattern`.get(idx) as Iota;
      if (x is PatternIota) {
         val var10000: HexPattern = (x as PatternIota).getPattern();
         return var10000;
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "pattern");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPattern`.size());
   }
}

@JvmSynthetic
fun `getPattern$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): HexPattern {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getPattern(var0, var1, var2);
}

public fun List<Iota>.getVec3(idx: Int, argc: Int = 0): Vec3 {
   if (0 <= idx && idx < `$this$getVec3`.size()) {
      val x: Iota = `$this$getVec3`.get(idx) as Iota;
      if (x is Vec3Iota) {
         val var10000: Vec3 = (x as Vec3Iota).getVec3();
         return var10000;
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "vector");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getVec3`.size());
   }
}

@JvmSynthetic
fun `getVec3$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Vec3 {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getVec3(var0, var1, var2);
}

public fun List<Iota>.getBool(idx: Int, argc: Int = 0): Boolean {
   if (0 <= idx && idx < `$this$getBool`.size()) {
      val x: Iota = `$this$getBool`.get(idx) as Iota;
      if (x is BooleanIota) {
         return (x as BooleanIota).getBool();
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "boolean");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getBool`.size());
   }
}

@JvmSynthetic
fun `getBool$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Boolean {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getBool(var0, var1, var2);
}

public fun List<Iota>.getItemEntity(idx: Int, argc: Int = 0): ItemEntity {
   if (0 <= idx && idx < `$this$getItemEntity`.size()) {
      val x: Iota = `$this$getItemEntity`.get(idx) as Iota;
      if (x is EntityIota) {
         val e: Entity = (x as EntityIota).getEntity();
         if (e is ItemEntity) {
            return e as ItemEntity;
         }
      }

      throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "entity.item");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getItemEntity`.size());
   }
}

@JvmSynthetic
fun `getItemEntity$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): ItemEntity {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getItemEntity(var0, var1, var2);
}

public fun List<Iota>.getPlayer(idx: Int, argc: Int = 0): ServerPlayer {
   if (0 <= idx && idx < `$this$getPlayer`.size()) {
      val x: Iota = `$this$getPlayer`.get(idx) as Iota;
      if (x is EntityIota) {
         val e: Entity = (x as EntityIota).getEntity();
         if (e is ServerPlayer) {
            return e as ServerPlayer;
         }
      }

      throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "entity.player");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPlayer`.size());
   }
}

@JvmSynthetic
fun `getPlayer$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): ServerPlayer {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getPlayer(var0, var1, var2);
}

public fun List<Iota>.getMob(idx: Int, argc: Int = 0): Mob {
   if (0 <= idx && idx < `$this$getMob`.size()) {
      val x: Iota = `$this$getMob`.get(idx) as Iota;
      if (x is EntityIota) {
         val e: Entity = (x as EntityIota).getEntity();
         if (e is Mob) {
            return e as Mob;
         }
      }

      throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "entity.mob");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getMob`.size());
   }
}

@JvmSynthetic
fun `getMob$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Mob {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getMob(var0, var1, var2);
}

public fun List<Iota>.getLivingEntityButNotArmorStand(idx: Int, argc: Int = 0): LivingEntity {
   if (0 <= idx && idx < `$this$getLivingEntityButNotArmorStand`.size()) {
      val x: Iota = `$this$getLivingEntityButNotArmorStand`.get(idx) as Iota;
      if (x is EntityIota) {
         val e: Entity = (x as EntityIota).getEntity();
         if (e is LivingEntity && e !is ArmorStand) {
            return e as LivingEntity;
         }
      }

      throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "entity.living");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getLivingEntityButNotArmorStand`.size());
   }
}

@JvmSynthetic
fun `getLivingEntityButNotArmorStand$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): LivingEntity {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getLivingEntityButNotArmorStand(var0, var1, var2);
}

public fun List<Iota>.getPositiveDouble(idx: Int, argc: Int = 0): Double {
   if (0 <= idx && idx < `$this$getPositiveDouble`.size()) {
      val x: Iota = `$this$getPositiveDouble`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var4: Double = (x as DoubleIota).getDouble();
         if (0.0 <= var4) {
            return var4;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "double.positive");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPositiveDouble`.size());
   }
}

@JvmSynthetic
fun `getPositiveDouble$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Double {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getPositiveDouble(var0, var1, var2);
}

public fun List<Iota>.getPositiveDoubleUnder(idx: Int, max: Double, argc: Int = 0): Double {
   if (0 <= idx && idx < `$this$getPositiveDoubleUnder`.size()) {
      val x: Iota = `$this$getPositiveDoubleUnder`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var6: Double = (x as DoubleIota).getDouble();
         if (0.0 <= var6 && var6 < max) {
            return var6;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "double.positive.less", max);
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPositiveDoubleUnder`.size());
   }
}

@JvmSynthetic
fun `getPositiveDoubleUnder$default`(var0: java.util.List, var1: Int, var2: Double, var4: Int, var5: Int, var6: Any): Double {
   if ((var5 and 4) != 0) {
      var4 = 0;
   }

   return getPositiveDoubleUnder(var0, var1, var2, var4);
}

public fun List<Iota>.getPositiveDoubleUnderInclusive(idx: Int, max: Double, argc: Int = 0): Double {
   if (0 <= idx && idx < `$this$getPositiveDoubleUnderInclusive`.size()) {
      val x: Iota = `$this$getPositiveDoubleUnderInclusive`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var6: Double = (x as DoubleIota).getDouble();
         if (0.0 <= var6 && var6 <= max) {
            return var6;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "double.positive.less.equal", max);
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPositiveDoubleUnderInclusive`.size());
   }
}

@JvmSynthetic
fun `getPositiveDoubleUnderInclusive$default`(var0: java.util.List, var1: Int, var2: Double, var4: Int, var5: Int, var6: Any): Double {
   if ((var5 and 4) != 0) {
      var4 = 0;
   }

   return getPositiveDoubleUnderInclusive(var0, var1, var2, var4);
}

public fun List<Iota>.getDoubleBetween(idx: Int, min: Double, max: Double, argc: Int = 0): Double {
   if (0 <= idx && idx < `$this$getDoubleBetween`.size()) {
      val x: Iota = `$this$getDoubleBetween`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var8: Double = (x as DoubleIota).getDouble();
         if (min <= var8 && var8 <= max) {
            return var8;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "double.between", min, max);
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getDoubleBetween`.size());
   }
}

@JvmSynthetic
fun `getDoubleBetween$default`(var0: java.util.List, var1: Int, var2: Double, var4: Double, var6: Int, var7: Int, var8: Any): Double {
   if ((var7 and 8) != 0) {
      var6 = 0;
   }

   return getDoubleBetween(var0, var1, var2, var4, var6);
}

public fun List<Iota>.getInt(idx: Int, argc: Int = 0): Int {
   if (0 <= idx && idx < `$this$getInt`.size()) {
      val x: Iota = `$this$getInt`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var4: Double = (x as DoubleIota).getDouble();
         val var7: Int = MathKt.roundToInt(var4);
         if (Math.abs(var4 - (double)var7) <= 1.0E-4) {
            return var7;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getInt`.size());
   }
}

@JvmSynthetic
fun `getInt$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getInt(var0, var1, var2);
}

public fun List<Iota>.getLong(idx: Int, argc: Int = 0): Long {
   if (0 <= idx && idx < `$this$getLong`.size()) {
      val x: Iota = `$this$getLong`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var4: Double = (x as DoubleIota).getDouble();
         val var8: Long = MathKt.roundToLong(var4);
         if (Math.abs(var4 - (double)var8) <= 1.0E-4) {
            return var8;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getLong`.size());
   }
}

@JvmSynthetic
fun `getLong$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Long {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getLong(var0, var1, var2);
}

public fun List<Iota>.getPositiveInt(idx: Int, argc: Int = 0): Int {
   if (0 <= idx && idx < `$this$getPositiveInt`.size()) {
      val x: Iota = `$this$getPositiveInt`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var4: Double = (x as DoubleIota).getDouble();
         val var7: Int = MathKt.roundToInt(var4);
         if (Math.abs(var4 - (double)var7) <= 1.0E-4 && var7 >= 0) {
            return var7;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int.positive");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPositiveInt`.size());
   }
}

@JvmSynthetic
fun `getPositiveInt$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getPositiveInt(var0, var1, var2);
}

public fun List<Iota>.getPositiveIntUnder(idx: Int, max: Int, argc: Int = 0): Int {
   if (0 <= idx && idx < `$this$getPositiveIntUnder`.size()) {
      val x: Iota = `$this$getPositiveIntUnder`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var5: Double = (x as DoubleIota).getDouble();
         val var9: Int = MathKt.roundToInt(var5);
         if (Math.abs(var5 - (double)var9) <= 1.0E-4 && 0 <= var9 && var9 < max) {
            return var9;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int.positive.less", max);
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPositiveIntUnder`.size());
   }
}

@JvmSynthetic
fun `getPositiveIntUnder$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Int, var5: Any): Int {
   if ((var4 and 4) != 0) {
      var3 = 0;
   }

   return getPositiveIntUnder(var0, var1, var2, var3);
}

public fun List<Iota>.getPositiveIntUnderInclusive(idx: Int, max: Int, argc: Int = 0): Int {
   if (0 <= idx && idx < `$this$getPositiveIntUnderInclusive`.size()) {
      val x: Iota = `$this$getPositiveIntUnderInclusive`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var5: Double = (x as DoubleIota).getDouble();
         val var9: Int = MathKt.roundToInt(var5);
         if (Math.abs(var5 - (double)var9) <= 1.0E-4 && 0 <= var9 && var9 <= max) {
            return var9;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int.positive.less.equal", max);
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getPositiveIntUnderInclusive`.size());
   }
}

@JvmSynthetic
fun `getPositiveIntUnderInclusive$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Int, var5: Any): Int {
   if ((var4 and 4) != 0) {
      var3 = 0;
   }

   return getPositiveIntUnderInclusive(var0, var1, var2, var3);
}

public fun List<Iota>.getIntBetween(idx: Int, min: Int, max: Int, argc: Int = 0): Int {
   if (0 <= idx && idx < `$this$getIntBetween`.size()) {
      val x: Iota = `$this$getIntBetween`.get(idx) as Iota;
      if (x is DoubleIota) {
         val var6: Double = (x as DoubleIota).getDouble();
         val var10: Int = MathKt.roundToInt(var6);
         if (Math.abs(var6 - (double)var10) <= 1.0E-4 && min <= var10 && var10 <= max) {
            return var10;
         }
      }

      throw MishapInvalidIota.Companion.of(x, if (argc == 0) idx else argc - (idx + 1), "int.between", min, max);
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getIntBetween`.size());
   }
}

@JvmSynthetic
fun `getIntBetween$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Any): Int {
   if ((var5 and 8) != 0) {
      var4 = 0;
   }

   return getIntBetween(var0, var1, var2, var3, var4);
}

public fun List<Iota>.getBlockPos(idx: Int, argc: Int = 0): BlockPos {
   if (0 <= idx && idx < `$this$getBlockPos`.size()) {
      val x: Iota = `$this$getBlockPos`.get(idx) as Iota;
      if (x is Vec3Iota) {
         val var10000: BlockPos = BlockPos.containing((x as Vec3Iota).getVec3() as Position);
         return var10000;
      } else {
         throw MishapInvalidIota.Companion.ofType(x, if (argc == 0) idx else argc - (idx + 1), "vector");
      }
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getBlockPos`.size());
   }
}

@JvmSynthetic
fun `getBlockPos$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): BlockPos {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getBlockPos(var0, var1, var2);
}

public fun List<Iota>.getNumOrVec(idx: Int, argc: Int = 0): Either<Double, Vec3> {
   if (0 <= idx && idx < `$this$getNumOrVec`.size()) {
      val datum: Iota = `$this$getNumOrVec`.get(idx) as Iota;
      val var10000: Either;
      if (datum is DoubleIota) {
         var10000 = Either.left((datum as DoubleIota).getDouble());
      } else {
         if (datum !is Vec3Iota) {
            throw MishapInvalidIota.Companion.of(datum, if (argc == 0) idx else argc - (idx + 1), "numvec");
         }

         var10000 = Either.right((datum as Vec3Iota).getVec3());
      }

      return var10000;
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getNumOrVec`.size());
   }
}

@JvmSynthetic
fun `getNumOrVec$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Either {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getNumOrVec(var0, var1, var2);
}

public fun List<Iota>.getLongOrList(idx: Int, argc: Int = 0): Either<Long, SpellList> {
   if (0 <= idx && idx < `$this$getLongOrList`.size()) {
      val datum: Iota = `$this$getLongOrList`.get(idx) as Iota;
      if (datum is DoubleIota) {
         val var4: Double = (datum as DoubleIota).getDouble();
         val var8: Long = MathKt.roundToLong(var4);
         if (Math.abs(var4 - (double)var8) <= 1.0E-4) {
            val var10000: Either = Either.left(var8);
            return var10000;
         }
      } else if (datum is ListIota) {
         val var9: Either = Either.right((datum as ListIota).getList());
         return var9;
      }

      throw MishapInvalidIota.Companion.of(datum, if (argc == 0) idx else argc - (idx + 1), "numlist");
   } else {
      throw new MishapNotEnoughArgs(idx + 1, `$this$getLongOrList`.size());
   }
}

@JvmSynthetic
fun `getLongOrList$default`(var0: java.util.List, var1: Int, var2: Int, var3: Int, var4: Any): Either {
   if ((var3 and 2) != 0) {
      var2 = 0;
   }

   return getLongOrList(var0, var1, var2);
}

public fun evaluatable(datum: Iota, reverseIdx: Int): Either<Iota, SpellList> {
   val var10000: Either;
   if (datum is ListIota) {
      var10000 = Either.right((datum as ListIota).getList());
   } else {
      if (!datum.executable()) {
         throw new MishapInvalidIota(datum, reverseIdx, HexUtils.getAsTranslatedComponent("hexcasting.mishap.invalid_value.evaluatable") as Component);
      }

      val var2: Either = Either.left(datum);
      var10000 = var2;
   }

   return var10000;
}

public fun Iota?.orNull(): Iota {
   var var10000: Iota = `$this$orNull`;
   if (`$this$orNull` == null) {
      var10000 = new NullIota();
   }

   return var10000;
}

public fun aplKinnie(operatee: Either<Double, Vec3>, fn: DoubleUnaryOperator): Iota {
   val var2: Any = operatee.map(OperatorUtils::aplKinnie$lambda$24, OperatorUtils::aplKinnie$lambda$26);
   return var2 as Iota;
}

fun `aplKinnie$lambda$23`(`$fn`: DoubleUnaryOperator, num: java.lang.Double): Iota {
   return new DoubleIota(`$fn`.applyAsDouble(num));
}

fun `aplKinnie$lambda$24`(`$tmp0`: Function1, p0: Any): Iota {
   return `$tmp0`.invoke(p0) as Iota;
}

fun `aplKinnie$lambda$25`(`$fn`: DoubleUnaryOperator, vec: Vec3): Iota {
   return new Vec3Iota(new Vec3(`$fn`.applyAsDouble(vec.x), `$fn`.applyAsDouble(vec.y), `$fn`.applyAsDouble(vec.z)));
}

fun `aplKinnie$lambda$26`(`$tmp0`: Function1, p0: Any): Iota {
   return `$tmp0`.invoke(p0) as Iota;
}
