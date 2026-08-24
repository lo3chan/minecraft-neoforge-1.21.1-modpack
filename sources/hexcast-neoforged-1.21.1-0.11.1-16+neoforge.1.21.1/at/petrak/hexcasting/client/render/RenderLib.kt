@file:JvmName(name = "RenderLib")

package at.petrak.hexcasting.client.render

import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.client.ClientTickCounter
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat.Mode
import com.mojang.math.Axis
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.math.MathKt
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.MultiBufferSource.BufferSource
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.FastColor.ARGB32
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource
import net.minecraft.world.level.levelgen.synth.SimplexNoise
import net.minecraft.world.phys.Vec2
import org.joml.Matrix4f

public final val NOISE: SimplexNoise = new SimplexNoise((new SingleThreadedRandomSource(9001L)) as RandomSource)
public const val CAP_THETA: Float = 18.0F
public const val DEFAULT_READABILITY_OFFSET: Float = 0.2F
public const val DEFAULT_LAST_SEGMENT_LEN_PROP: Float = 0.8F

public fun getNoise(x: Double, y: Double, z: Double): Double {
   return NOISE.getValue(x * 0.6, y * 0.6, z * 0.6) / 2.0;
}

public fun drawLineSeq(mat: Matrix4f, points: List<Vec2>, width: Float, z: Float, tail: Int, head: Int) {
   if (points.size() > 1) {
      val r1: Float = ARGB32.red(tail);
      val g1: Float = ARGB32.green(tail);
      val b1: Float = ARGB32.blue(tail);
      val a: Int = ARGB32.alpha(tail);
      val headSource: Int = if (Screen.hasControlDown() != HexConfig.client().ctrlTogglesOffStrokeOrder()) head else tail;
      val r2: Float = ARGB32.red(headSource);
      val g2: Float = ARGB32.green(headSource);
      val b2: Float = ARGB32.blue(headSource);
      val tess: Tesselator = Tesselator.getInstance();
      val buf: ObjectRef = new ObjectRef();
      buf.element = tess.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
      val n: Int = points.size();
      val joinAngles: FloatArray = new float[n];
      val joinOffsets: FloatArray = new float[n];

      for (int i = 2; i < n; i++) {
         val p0: Vec2 = points.get(i - 2) as Vec2;
         val p1: Vec2 = points.get(i - 1) as Vec2;
         val p2: Vec2 = points.get(i) as Vec2;
         val tangent: Vec2 = p1.add(p0.negated());
         val normal: Vec2 = p2.add(p1.negated());
         val color1: Float = (float)Mth.atan2((double)(tangent.x * normal.y - tangent.y * normal.x), (double)(tangent.x * normal.x + tangent.y * normal.y));
         joinAngles[i - 1] = color1;
         val color2: Float = RangesKt.coerceAtMost(tangent.length(), normal.length()) / (width * 0.5F);
         joinOffsets[i - 1] = Mth.clamp(Mth.sin(color1) / ((float)1 + Mth.cos(color1)), -color2, color2);
      }

      var var42: Int = 0;

      for (int var43 = points.size() - 1; i < var43; i++) {
         val var44: Vec2 = points.get(var42) as Vec2;
         val var45: Vec2 = points.get(var42 + 1) as Vec2;
         val var46: Vec2 = var45.add(var44.negated()).normalized().scale(width * 0.5F);
         val var47: Vec2 = new Vec2(-var46.y, var46.x);
         val var48: BlockPos = drawLineSeq$color(r1, r2, g1, g2, b1, b2, (float)var42 / (float)n);
         val var49: BlockPos = drawLineSeq$color(r1, r2, g1, g2, b1, b2, ((float)var42 + 1.0F) / (float)n);
         val jlow: Float = joinOffsets[var42];
         val jhigh: Float = joinOffsets[var42 + 1];
         val p1Down: Vec2 = var44.add(var46.scale(Math.max(0.0F, jlow))).add(var47);
         val p1Up: Vec2 = var44.add(var46.scale(Math.max(0.0F, -jlow))).add(var47.negated());
         val p2Down: Vec2 = var45.add(var46.scale(Math.max(0.0F, jhigh)).negated()).add(var47);
         val p2Up: Vec2 = var45.add(var46.scale(Math.max(0.0F, -jhigh)).negated()).add(var47.negated());
         drawLineSeq$vertex(buf, mat, z, a, var48, p1Down);
         drawLineSeq$vertex(buf, mat, z, a, var48, var44);
         drawLineSeq$vertex(buf, mat, z, a, var48, p1Up);
         drawLineSeq$vertex(buf, mat, z, a, var48, p1Down);
         drawLineSeq$vertex(buf, mat, z, a, var48, p1Up);
         drawLineSeq$vertex(buf, mat, z, a, var49, p2Up);
         drawLineSeq$vertex(buf, mat, z, a, var48, p1Down);
         drawLineSeq$vertex(buf, mat, z, a, var49, p2Up);
         drawLineSeq$vertex(buf, mat, z, a, var49, var45);
         drawLineSeq$vertex(buf, mat, z, a, var48, p1Down);
         drawLineSeq$vertex(buf, mat, z, a, var49, var45);
         drawLineSeq$vertex(buf, mat, z, a, var49, p2Down);
         if (var42 > 0) {
            val sangle: Float = joinAngles[var42];
            val angle: Float = Math.abs(joinAngles[var42]);
            val rnormal: Vec2 = var47.negated();
            val joinSteps: Int = Mth.ceil(angle * (float)180 / 56.548668F);
            if (joinSteps >= 1) {
               if (sangle < 0.0F) {
                  var var50: Vec2 = new Vec2(var44.x - rnormal.x, var44.y - rnormal.y);
                  var var51: Int = 1;
                  if (1 <= joinSteps) {
                     while (true) {
                        val var52: Vec2 = rotate(rnormal, -sangle * ((float)var51 / (float)joinSteps));
                        val var53: Vec2 = new Vec2(var44.x - var52.x, var44.y - var52.y);
                        drawLineSeq$vertex(buf, mat, z, a, var48, var44);
                        drawLineSeq$vertex(buf, mat, z, a, var48, var50);
                        drawLineSeq$vertex(buf, mat, z, a, var48, var53);
                        var50 = var53;
                        if (var51 == joinSteps) {
                           break;
                        }

                        var51++;
                     }
                  }
               } else {
                  val startFan: Vec2 = rotate(var47, -sangle);
                  var prevVert: Vec2 = new Vec2(var44.x - startFan.x, var44.y - startFan.y);

                  for (int j = joinSteps - 1; -1 < j; j--) {
                     val fan: Vec2 = rotate(var47, -sangle * ((float)j / (float)joinSteps));
                     val fanShift: Vec2 = new Vec2(var44.x - fan.x, var44.y - fan.y);
                     drawLineSeq$vertex(buf, mat, z, a, var48, var44);
                     drawLineSeq$vertex(buf, mat, z, a, var48, prevVert);
                     drawLineSeq$vertex(buf, mat, z, a, var48, fanShift);
                     prevVert = fanShift;
                  }
               }
            }
         }
      }

      BufferUploader.drawWithShader((buf.element as BufferBuilder).buildOrThrow());
      drawLineSeq$drawCaps(width, buf, tess, mat, z, a, new BlockPos((int)r1, (int)g1, (int)b1), points.get(0) as Vec2, points.get(1) as Vec2);
      drawLineSeq$drawCaps(width, buf, tess, mat, z, a, new BlockPos((int)r2, (int)g2, (int)b2), points.get(n - 1) as Vec2, points.get(n - 2) as Vec2);
   }
}

public fun rotate(vec: Vec2, theta: Float): Vec2 {
   val cos: Float = Mth.cos(theta);
   val sin: Float = Mth.sin(theta);
   return new Vec2(vec.x * cos - vec.y * sin, vec.y * cos + vec.x * sin);
}

public fun drawPatternFromPoints(
   mat: Matrix4f,
   points: List<Vec2>,
   dupIndices: Set<Int>?,
   drawLast: Boolean,
   tail: Int,
   head: Int,
   flowIrregular: Float,
   readabilityOffset: Float,
   lastSegmentLenProportion: Float,
   seed: Double
) {
   val zappyPts: java.util.List = makeZappy(points, dupIndices, 10, 2.5F, 0.1F, flowIrregular, readabilityOffset, lastSegmentLenProportion, seed);
   val nodes: java.util.List = if (drawLast) points else CollectionsKt.dropLast(points, 1);
   drawLineSeq(mat, zappyPts, 5.0F, 0.0F, tail, head);
   drawLineSeq(mat, zappyPts, 2.0F, 1.0F, screenCol(tail), screenCol(head));

   for (Vec2 node : nodes) {
      drawSpot(
         mat,
         node,
         2.0F,
         dodge(ARGB32.red(head)) / 255.0F,
         dodge(ARGB32.green(head)) / 255.0F,
         dodge(ARGB32.blue(head)) / 255.0F,
         (float)ARGB32.alpha(head) / 255.0F
      );
   }
}

public fun makeZappy(
   barePoints: List<Vec2>,
   dupIndices: Set<Int>?,
   hops: Int,
   variance: Float,
   speed: Float,
   flowIrregular: Float,
   readabilityOffset: Float,
   lastSegmentLenProportion: Float,
   seed: Double
): List<Vec2> {
   if (barePoints.isEmpty()) {
      return CollectionsKt.emptyList();
   } else {
      val points: java.util.List = new ArrayList();
      val daisyChain: java.util.List = new ArrayList();
      val var10000: java.util.List;
      if (dupIndices != null) {
         val var12: java.util.Iterator = CollectionsKt.zipWithNext(barePoints).iterator();
         var var13: Int = 0;

         while (var12.hasNext()) {
            val i: Int = var13++;
            val pair: Pair = var12.next() as Pair;
            val head: Vec2 = pair.component1() as Vec2;
            val tail: Vec2 = pair.component2() as Vec2;
            val tangent: Vec2 = tail.add(head.negated()).scale(readabilityOffset);
            if (i != 0 && dupIndices.contains(i)) {
               val var10001: Vec2 = head.add(tangent);
               daisyChain.add(var10001);
            } else {
               daisyChain.add(head);
            }

            if (i == barePoints.size() - 2) {
               daisyChain.add(tail);
               points.addAll(makeZappy$zappify(speed, hops, variance, lastSegmentLenProportion, flowIrregular, seed, daisyChain, true));
            } else if (dupIndices.contains(i + 1)) {
               val var19: Vec2 = tail.add(tangent.negated());
               daisyChain.add(var19);
               points.addAll(makeZappy$zappify(speed, hops, variance, lastSegmentLenProportion, flowIrregular, seed, daisyChain, false));
               daisyChain.clear();
            }
         }

         var10000 = points;
      } else {
         var10000 = makeZappy$zappify(speed, hops, variance, lastSegmentLenProportion, flowIrregular, seed, barePoints, true);
      }

      return var10000;
   }
}

public fun <T> findDupIndices(pts: Iterable<T>): Set<Int> {
   val dedup: HashMap = new HashMap();
   val found: HashSet = new HashSet();
   val var3: java.util.Iterator = pts.iterator();
   var var4: Int = 0;

   while (var3.hasNext()) {
      val i: Int = var4++;
      val pt: Any = var3.next();
      val ix: Int = dedup.get(pt) as Int;
      if (ix != null) {
         found.add(i);
         found.add(ix);
      } else {
         dedup.put(pt, i);
      }
   }

   return found;
}

public fun drawSpot(mat: Matrix4f, point: Vec2, radius: Float, r: Float, g: Float, b: Float, a: Float) {
   val buf: BufferBuilder = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
   buf.addVertex(mat, point.x, point.y, 1.0F).setColor(r, g, b, a);
   val fracOfCircle: Int = 6;
   var i: Int = 0;

   while (true) {
      buf.addVertex(
            mat,
            Mth.cos((float)i / (float)fracOfCircle * 6.2831855F) * radius + point.x,
            Mth.sin((float)i / (float)fracOfCircle * 6.2831855F) * radius + point.y,
            1.0F
         )
         .setColor(r, g, b, a);
      if (i == fracOfCircle) {
         BufferUploader.drawWithShader(buf.buildOrThrow());
         return;
      }

      i++;
   }
}

public fun screenCol(n: Int): Int {
   return ARGB32.color(ARGB32.alpha(n), screen(ARGB32.red(n)), screen(ARGB32.green(n)), screen(ARGB32.blue(n)));
}

public fun screen(n: Int): Int {
   return (n + 255) / 2;
}

public fun dodge(n: Int): Float {
   return n * 0.9F;
}

public fun getCenteredPattern(pattern: HexPattern, width: Float, height: Float, minSize: Float): Pair<Float, List<Vec2>> {
   val com1: Vec2 = HexPattern.getCenter$default(pattern, 1.0F, null, 2, null);
   var var10002: Vec2 = Vec2.ZERO;
   val lines1: java.util.List = pattern.toLines(1.0F, var10002);
   var maxDx: Float = -1.0F;
   var maxDy: Float = -1.0F;

   for (Vec2 dot : lines1) {
      val lines2: Float = Mth.abs(com2.x - com1.x);
      if (lines2 > maxDx) {
         maxDx = lines2;
      }

      val dy: Float = Mth.abs(com2.y - com1.y);
      if (dy > maxDy) {
         maxDy = dy;
      }
   }

   val var12: Float = Math.min(minSize, Math.min(width / 3.0F / maxDx, height / 3.0F / maxDy));
   var10002 = HexPattern.getCenter$default(pattern, var12, null, 2, null).negated();
   return TuplesKt.to(var12, pattern.toLines(var12, var10002));
}

@JvmOverloads
public fun renderEntity(
   graphics: GuiGraphics,
   entity: Entity,
   world: Level,
   x: Float,
   y: Float,
   rotation: Float,
   renderScale: Float,
   offset: Float,
   bufferTransformer: (MultiBufferSource) -> MultiBufferSource = RenderLib::renderEntity$lambda$1
) {
   val rotationx: Float = if (Screen.hasShiftDown()) 0.0F else rotation;
   val ps: PoseStack = graphics.pose();
   ps.pushPose();
   ps.translate((double)x, (double)y, 50.0);
   ps.scale(renderScale, renderScale, renderScale);
   ps.translate(0.0, (double)offset, 0.0);
   ps.mulPose(Axis.ZP.rotationDegrees(180.0F));
   ps.mulPose(Axis.YP.rotationDegrees(rotationx));
   val erd: EntityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
   val immediate: BufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
   erd.setRenderShadow(false);
   erd.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, ps, bufferTransformer.invoke(immediate) as MultiBufferSource, 15728880);
   erd.setRenderShadow(true);
   immediate.endBatch();
   ps.popPose();
}

@JvmSynthetic
fun `renderEntity$default`(
   var0: GuiGraphics, var1: Entity, var2: Level, var3: Float, var4: Float, var5: Float, var6: Float, var7: Float, var8: Function1, var9: Int, var10: Any
) {
   if ((var9 and 256) != 0) {
      var8 = RenderLib::renderEntity$lambda$1;
   }

   renderEntity(var0, var1, var2, var3, var4, var5, var6, var7, var8);
}

public fun renderQuad(ps: PoseStack, x: Float, y: Float, w: Float, h: Float, color: Int) {
   val mat: Matrix4f = ps.last().pose();
   val buf: BufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
   buf.addVertex(mat, x, y, 0.0F).setColor(color);
   buf.addVertex(mat, x, y + h, 0.0F).setColor(color);
   buf.addVertex(mat, x + w, y + h, 0.0F).setColor(color);
   buf.addVertex(mat, x + w, y, 0.0F).setColor(color);
   BufferUploader.drawWithShader(buf.buildOrThrow());
}

@JvmOverloads
fun renderEntity(graphics: GuiGraphics, entity: Entity, world: Level, x: Float, y: Float, rotation: Float, renderScale: Float, offset: Float) {
   renderEntity$default(graphics, entity, world, x, y, rotation, renderScale, offset, null, 256, null);
}

fun `drawLineSeq$vertex`(buf: ObjectRef<BufferBuilder>, `$mat`: Matrix4f, `$z`: Float, a: Int, color: BlockPos, pos: Vec2): VertexConsumer {
   return (buf.element as BufferBuilder).addVertex(`$mat`, pos.x, pos.y, `$z`).setColor(color.getX(), color.getY(), color.getZ(), a);
}

fun `drawLineSeq$color`(r1: Float, r2: Float, g1: Float, g2: Float, b1: Float, b2: Float, time: Float): BlockPos {
   return new BlockPos((int)Mth.lerp(time, r1, r2), (int)Mth.lerp(time, g1, g2), (int)Mth.lerp(time, b1, b2));
}

fun `drawLineSeq$drawCaps`(
   `$width`: Float, buf: ObjectRef<BufferBuilder>, tess: Tesselator, `$mat`: Matrix4f, `$z`: Float, a: Int, color: BlockPos, point: Vec2, prev: Vec2
) {
   val tangent: Vec2 = point.add(prev.negated()).normalized().scale(0.5F * `$width`);
   val normal: Vec2 = new Vec2(-tangent.y, tangent.x);
   val joinSteps: Int = Mth.ceil(10.0F);
   buf.element = tess.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
   drawLineSeq$vertex(buf, `$mat`, `$z`, a, color, point);

   for (int j = joinSteps; -1 < j; j--) {
      val fan: Vec2 = rotate(normal, -3.1415927F * ((float)j / (float)joinSteps));
      (buf.element as BufferBuilder).addVertex(`$mat`, point.x + fan.x, point.y + fan.y, `$z`).setColor(color.getX(), color.getY(), color.getZ(), a);
   }

   BufferUploader.drawWithShader((buf.element as BufferBuilder).buildOrThrow());
}

fun `makeZappy$zappify$lambda$0`(it: Double): Double {
   return RangesKt.coerceAtMost(1.0, (double)8 * (0.5 - Math.abs(0.5 - it)));
}

fun `makeZappy$zappify`(
   `$speed`: Float,
   `$hops`: Int,
   `$variance`: Float,
   `$lastSegmentLenProportion`: Float,
   `$flowIrregular`: Float,
   `$seed`: Double,
   points: MutableList<Vec2>,
   truncateLast: Boolean
): MutableList<Vec2> {
   val scaleVariance: Function1 = RenderLib::makeZappy$zappify$lambda$0;
   val zSeed: Double = (double)ClientTickCounter.getTotal() * `$speed`;
   val zappyPts: ArrayList = new ArrayList(points.size() * `$hops`);
   zappyPts.add(points.get(0));
   val var13: java.util.Iterator = CollectionsKt.zipWithNext(points).iterator();
   var var14: Int = 0;

   while (var13.hasNext()) {
      val i: Int = var14++;
      val pair: Pair = var13.next() as Pair;
      val src: Vec2 = pair.component1() as Vec2;
      val target: Vec2 = pair.component2() as Vec2;
      val delta: Vec2 = target.add(src.negated());
      val maxVariance: Float = Mth.sqrt(src.distanceToSqr(target)) / `$hops` * `$variance`;
      val maxJ: Int = if (truncateLast && i == points.size() - 2) MathKt.roundToInt(`$lastSegmentLenProportion` * (float)`$hops`) else `$hops`;
      var j: Int = 1;
      if (1 <= maxJ) {
         while (true) {
            val progress: Double = (double)j / (`$hops` + 1);
            val pos: Vec2 = src.add(delta.scale((float)((double)j / (double)(`$hops` + 1))));
            val theta: Float = (float)(
               3
                  * getNoise(
                     (double)i + (double)j / (double)(`$hops` + 1) + getNoise((double)i, (double)j, Math.sin(zSeed)) * (double)`$flowIrregular` - zSeed,
                     1337.0,
                     `$seed`
                  )
                  * 6.283185307179586
            );
            val r: Float = (float)(
               getNoise((double)i + progress - zSeed, 69420.0, `$seed`) * maxVariance * (scaleVariance.invoke(progress) as java.lang.Number).doubleValue()
            );
            zappyPts.add(pos.add(new Vec2(r * Mth.cos(theta), r * Mth.sin(theta))));
            if (j == `$hops`) {
               zappyPts.add(target);
            }

            if (j == maxJ) {
               break;
            }

            j++;
         }
      }
   }

   return zappyPts;
}

fun `renderEntity$lambda$1`(it: MultiBufferSource): MultiBufferSource {
   return it;
}
