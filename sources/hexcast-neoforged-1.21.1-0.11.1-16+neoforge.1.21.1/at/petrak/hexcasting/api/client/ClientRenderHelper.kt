@file:JvmName(name = "ClientRenderHelper")

package at.petrak.hexcasting.api.client

import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.ColorProvider
import at.petrak.hexcasting.client.ClientTickCounter
import at.petrak.hexcasting.client.render.RenderLib
import at.petrak.hexcasting.xplat.IClientXplatAbstractions
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec2
import org.joml.Matrix4f

public fun renderCastingStack(ps: PoseStack, player: Player, pticks: Float) {
   val stack: ClientCastingStack = IClientXplatAbstractions.INSTANCE.getClientCastingStack(player);
   var k: Int = 0;

   for (int var5 = stack.getPatterns().size(); k < var5; k++) {
      val var10000: HexPatternRenderHolder = stack.getPatternHolder(k);
      if (var10000 != null) {
         val pattern: HexPattern = var10000.getPattern();
         val lifetime: Int = var10000.getLifetime();
         val lifetimeOffset: Float = if (lifetime <= 5.0F) (5.0F - lifetime) / 5.0F else 0.0F;
         ps.pushPose();
         ps.mulPose(
            Axis.YP
               .rotationDegrees(
                  ((float)player.level().getGameTime() + pticks) * ((float)Math.sin((double)((float)k * 12.543565F)) * 3.4F) * ((float)k / 12.43F) % (float)360
                     + (float)(1 + k) * 45.0F
               )
         );
         ps.translate(
            0.0,
            (double)1 + Math.sin((double)k) * 0.75,
            0.75
               + Math.cos((double)k / 8.0) * 0.25
               + (double)((float)Math.cos((double)(((float)player.level().getGameTime() + pticks) / (float)(7 + k / 4)))) * 0.065
         );
         ps.scale(0.041666668F * ((float)1 - lifetimeOffset), 0.041666668F * ((float)1 - lifetimeOffset), 0.041666668F * ((float)1 - lifetimeOffset));
         ps.translate(0.0, Math.floor((double)k / 8.0), 0.0);
         ps.translate(0.0, Math.sin((double)((float)player.level().getGameTime() + pticks) / (7.0 + (double)k / 8.0)), 0.0);
         val oldShader: ShaderInstance = RenderSystem.getShader();
         RenderSystem.setShader(ClientRenderHelper::renderCastingStack$lambda$0);
         RenderSystem.enableDepthTest();
         RenderSystem.disableCull();
         val com1: Vec2 = HexPattern.getCenter$default(pattern, 1.0F, null, 2, null);
         var var10002: Vec2 = Vec2.ZERO;
         val lines1: java.util.List = pattern.toLines(1.0F, var10002);
         var maxDx: Float = -1.0F;
         var maxDy: Float = -1.0F;

         for (Vec2 line : lines1) {
            val lines2: Float = Math.abs(com2.x - com1.x);
            if (lines2 > maxDx) {
               maxDx = lines2;
            }

            val variance: Float = Math.abs(com2.y - com1.y);
            if (variance > maxDy) {
               maxDy = variance;
            }
         }

         val var28: Float = RangesKt.coerceAtMost(3.8F, RangesKt.coerceAtMost(6.4F / maxDx, 6.4F / maxDy));
         var10002 = HexPattern.getCenter$default(pattern, var28, null, 2, null).negated();
         val var30: java.util.List = CollectionsKt.toMutableList(pattern.toLines(var28, var10002));
         var var31: Int = 0;

         for (int speed = lines2.size(); i < speed; i++) {
            val stupidHash: Vec2 = var30.get(var31) as Vec2;
            var30.set(var31, new Vec2(stupidHash.x, -stupidHash.y));
         }

         val zappy: java.util.List = RenderLib.makeZappy(
            var30, RenderLib.findDupIndices(HexPattern.positions$default(pattern, null, 1, null)), 5, 0.65F, 0.1F, 0.2F, 0.0F, 1.0F, (double)player.hashCode()
         );
         val var35: ColorProvider = IXplatAbstractions.INSTANCE.getPigment(player).getColorProvider();
         val var10001: Float = ClientTickCounter.getTotal() / 2.0F;
         val var10003: RandomSource = player.getRandom();
         val outer: Int = var35.getColor(var10001, var10000.getColourPos(var10003));
         val rgbOnly: Int = outer and 16777215;
         var newAlpha: Int = outer ushr 24;
         if (lifetime <= 60) {
            newAlpha = (int)Math.floor((double)((float)lifetime / 60.0F * (float)255));
         }

         val newARGB: Int = newAlpha shl 24 or rgbOnly;
         val inner: Int = RenderLib.screenCol(newAlpha shl 24 or rgbOnly);
         val var36: Matrix4f = ps.last().pose();
         RenderLib.drawLineSeq(var36, zappy, 0.35F, 0.0F, newARGB, newARGB);
         val var37: Matrix4f = ps.last().pose();
         RenderLib.drawLineSeq(var37, zappy, 0.14F, 0.01F, inner, inner);
         ps.popPose();
         RenderSystem.setShader(ClientRenderHelper::renderCastingStack$lambda$1);
         RenderSystem.enableCull();
      }
   }
}

fun `renderCastingStack$lambda$0`(): ShaderInstance {
   return GameRenderer.getPositionColorShader();
}

fun `renderCastingStack$lambda$1`(`$oldShader`: ShaderInstance): ShaderInstance {
   return `$oldShader`;
}
