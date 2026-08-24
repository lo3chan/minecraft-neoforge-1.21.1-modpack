package at.petrak.hexcasting.api.casting.math

import at.petrak.hexcasting.api.HexAPI
import java.util.ArrayList
import java.util.EnumSet
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.Stack
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import kotlin.random.RandomKt

@SourceDebugExtension(["SMAP\nEulerPathFinder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EulerPathFinder.kt\nat/petrak/hexcasting/api/casting/math/EulerPathFinder\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,89:1\n538#2:90\n523#2,6:91\n384#2,7:117\n384#2,7:124\n384#2,7:131\n384#2,7:138\n3581#3,10:97\n3581#3,10:107\n*S KotlinDebug\n*F\n+ 1 EulerPathFinder.kt\nat/petrak/hexcasting/api/casting/math/EulerPathFinder\n*L\n38#1:90\n38#1:91,6\n77#1:117,7\n78#1:124,7\n83#1:131,7\n84#1:138,7\n65#1:97,10\n66#1:107,10\n*E\n"])
public object EulerPathFinder {
   @JvmOverloads
   @JvmStatic
   public fun findAltDrawing(original: HexPattern, seed: Long, rule: (HexPattern) -> Boolean = EulerPathFinder::findAltDrawing$lambda$0): HexPattern {
      val rand: Random = RandomKt.Random(seed);
      var iterationsLeft: Int = 100;

      while (iterationsLeft > 0) {
         iterationsLeft--;
         val var7: HexPattern = INSTANCE.walkPath(original, rand);
         if (rule.invoke(var7) as java.lang.Boolean) {
            return var7;
         }
      }

      HexAPI.LOGGER.warn("Didn't find alternate path for {} in time", original);
      return original;
   }

   private fun walkPath(original: HexPattern, rand: Random): HexPattern {
      val graph: HashMap = this.toGraph(original);
      val current: java.util.Map = graph;
      val dirs: java.util.Map = new LinkedHashMap();

      for (Entry element$iv$iv : $this$filter$iv.entrySet()) {
         if ((`$i$f$zipWithNext`.getValue() as EnumSet).size() % 2 == 1) {
            dirs.put(`$i$f$zipWithNext`.getKey(), `$i$f$zipWithNext`.getValue());
         }
      }

      var var10000: HexCoord;
      switch (destination$iv$iv.size()) {
         case 0:
            val var39: java.util.Set = graph.keySet();
            var10000 = CollectionsKt.random(var39, rand) as HexCoord;
            break;
         case 1:
         default:
            throw new IllegalStateException();
         case 2:
            var10000 = CollectionsKt.random(dirs.keySet(), rand) as HexCoord;
      }

      var var20: HexCoord = var10000;
      val var21: Stack = new Stack();
      val out: java.util.List = new ArrayList();

      do {
         var10000 = (HexCoord)graph.get(var20);
         val var22: EnumSet = var10000 as EnumSet;
         if ((var10000 as EnumSet).isEmpty()) {
            out.add(var20);
            var20 = var21.pop() as HexCoord;
         } else {
            var21.push(var20);
            val var24: HexDir = CollectionsKt.random(var22, rand) as HexDir;
            var22.remove(var24);
            val var41: EnumSet = graph.get(var20.plus(var24)) as EnumSet;
            if (var41 != null) {
               var41.remove(var24.times(HexAngle.BACK));
            }

            var20 = var20.plus(var24);
         }

         var42 = graph.get(var20) as EnumSet;
      } while (var42 != null && !var42.isEmpty() || !stack.isEmpty());

      out.add(var20);
      val var29: java.util.Iterator = out.iterator();
      val var43: java.util.List;
      if (!var29.hasNext()) {
         var43 = CollectionsKt.emptyList();
      } else {
         val `iterator$iv`: java.util.List = new ArrayList();
         var var32: Any = var29.next();

         while (iterator$iv.hasNext()) {
            val var34: Any = var29.next();
            val var44: HexDir = (var32 as HexCoord).immediateDelta(var34 as HexCoord);
            `iterator$iv`.add(var44);
            var32 = var34;
         }

         var43 = `iterator$iv`;
      }

      val `iterator$ivx`: java.util.Iterator = var43.iterator();
      val var45: java.util.List;
      if (!`iterator$ivx`.hasNext()) {
         var45 = CollectionsKt.emptyList();
      } else {
         val var33: java.util.List = new ArrayList();
         var var35: Any = `iterator$ivx`.next();

         while (iterator$ivx.hasNext()) {
            val var36: Any = `iterator$ivx`.next();
            var33.add((var36 as HexDir).angleFrom(var35 as HexDir));
            var35 = var36;
         }

         var45 = var33;
      }

      return new HexPattern(var43.get(0) as HexDir, CollectionsKt.toMutableList(var45));
   }

   private fun toGraph(pat: HexPattern): HashMap<HexCoord, EnumSet<HexDir>> {
      val graph: HashMap = new HashMap();
      var compass: HexDir = pat.getStartDir();
      var cursor: HexCoord = HexCoord.Companion.getOrigin();

      for (HexAngle a : pat.getAngles()) {
         var `$i$f$getOrPut`: java.util.Map = graph;
         val `answer$iv`: Any = graph.get(cursor);
         var var29: Any;
         if (`answer$iv` == null) {
            var29 = EnumSet.noneOf(HexDir.class);
            `$i$f$getOrPut`.put(cursor, var29);
            var29 = var29;
         } else {
            var29 = `answer$iv`;
         }

         (var29 as EnumSet).add(compass);
         `$i$f$getOrPut` = graph;
         val var19: Any = cursor.plus(compass);
         val `value$ivx`: Any = `$i$f$getOrPut`.get(var19);
         if (`value$ivx` == null) {
            var29 = EnumSet.noneOf(HexDir.class);
            `$i$f$getOrPut`.put(var19, var29);
            var29 = var29;
         } else {
            var29 = `value$ivx`;
         }

         (var29 as EnumSet).add(compass.times(HexAngle.BACK));
         cursor = cursor.plus(compass);
         compass = compass.times(`key$iv`);
      }

      var `$this$getOrPut$ivx`: java.util.Map = graph;
      val `value$ivx`: Any = graph.get(cursor);
      var var33: Any;
      if (`value$ivx` == null) {
         var33 = EnumSet.noneOf(HexDir.class);
         `$this$getOrPut$ivx`.put(cursor, var33);
         var33 = var33;
      } else {
         var33 = `value$ivx`;
      }

      (var33 as EnumSet).add(compass);
      `$this$getOrPut$ivx` = graph;
      val var15: Any = cursor.plus(compass);
      val `value$ivxx`: Any = `$this$getOrPut$ivx`.get(var15);
      if (`value$ivxx` == null) {
         var33 = EnumSet.noneOf(HexDir.class);
         `$this$getOrPut$ivx`.put(var15, var33);
         var33 = var33;
      } else {
         var33 = `value$ivxx`;
      }

      (var33 as EnumSet).add(compass.times(HexAngle.BACK));
      return graph;
   }

   @JvmOverloads
   @JvmStatic
   fun findAltDrawing(original: HexPattern, seed: Long): HexPattern {
      return findAltDrawing$default(original, seed, null, 4, null);
   }

   @JvmStatic
   fun `findAltDrawing$lambda$0`(it: HexPattern): Boolean {
      return true;
   }
}
