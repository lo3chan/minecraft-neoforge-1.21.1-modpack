package vazkii.psi.client.render.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.client.model.ArmorModels;
import vazkii.psi.common.Psi;
import vazkii.psi.common.entity.EntitySpellCircle;

public class RenderSpellCircle extends EntityRenderer<EntitySpellCircle> {
   private static final RenderType[] LAYERS = new RenderType[3];
   private static final float BRIGHTNESS_FACTOR = 0.7F;

   public RenderSpellCircle(Context ctx) {
      super(ctx);
      ArmorModels.init(ctx);
   }

   public static void renderSpellCircle(
      float alive, float scale, float horizontalScale, float xDir, float yDir, float zDir, int color, PoseStack ms, MultiBufferSource buffers
   ) {
      ms.pushPose();
      double ratio = 0.0625 * horizontalScale;
      Vec3 direction = new Vec3(xDir, yDir, zDir);
      Vec3 normal = new Vec3(0.0, 0.0, 1.0);
      Vec3 axis = normal.cross(direction);
      double dot = normal.dot(direction);
      ms.mulPose(Axis.YP.rotationDegrees(90.0F));
      if (axis.length() < 1.0E-6) {
         if (dot < 0.0) {
            ms.mulPose(Axis.XP.rotationDegrees(180.0F));
         }
      } else {
         float angle = (float)Math.acos(dot);
         ms.mulPose(new Quaternionf().fromAxisAngleRad((float)axis.x, (float)axis.y, (float)axis.z, angle));
      }

      ms.translate(0.0, 0.0, 0.1);
      ms.scale((float)ratio * scale, (float)ratio * scale, (float)ratio);
      int r = PsiRenderHelper.r(color);
      int g = PsiRenderHelper.g(color);
      int b = PsiRenderHelper.b(color);

      for (int i = 0; i < LAYERS.length; i++) {
         int rValue = r;
         int gValue = g;
         int bValue = b;
         if (i == 1) {
            bValue = 255;
            gValue = 255;
            rValue = 255;
         } else if (i == 2) {
            int minBrightness = 3;
            if (r == 0 && g == 0 && b == 0) {
               bValue = minBrightness;
               gValue = minBrightness;
               rValue = minBrightness;
            }

            if (rValue > 0 && rValue < minBrightness) {
               rValue = minBrightness;
            }

            if (gValue > 0 && gValue < minBrightness) {
               gValue = minBrightness;
            }

            if (bValue > 0 && bValue < minBrightness) {
               bValue = minBrightness;
            }

            rValue = (int)Math.min(rValue / 0.7F, 255.0F);
            gValue = (int)Math.min(gValue / 0.7F, 255.0F);
            bValue = (int)Math.min(bValue / 0.7F, 255.0F);
         }

         ms.pushPose();
         ms.mulPose(Axis.ZP.rotationDegrees(i == 0 ? -alive : alive));
         VertexConsumer buffer = buffers.getBuffer(LAYERS[i]);
         Matrix4f mat = ms.last().pose();
         int fullbright = 15728880;
         buffer.addVertex(mat, -32.0F, 32.0F, 0.0F).setColor(rValue, gValue, bValue, 255).setUv(0.0F, 1.0F).setLight(fullbright);
         buffer.addVertex(mat, 32.0F, 32.0F, 0.0F).setColor(rValue, gValue, bValue, 255).setUv(1.0F, 1.0F).setLight(fullbright);
         buffer.addVertex(mat, 32.0F, -32.0F, 0.0F).setColor(rValue, gValue, bValue, 255).setUv(1.0F, 0.0F).setLight(fullbright);
         buffer.addVertex(mat, -32.0F, -32.0F, 0.0F).setColor(rValue, gValue, bValue, 255).setUv(0.0F, 0.0F).setLight(fullbright);
         ms.popPose();
         ms.translate(0.0, 0.0, -0.5);
      }

      ms.popPose();
   }

   public void render(EntitySpellCircle entity, float entityYaw, float partialTicks, PoseStack ms, @NotNull MultiBufferSource buffers, int light) {
      ms.pushPose();
      ItemStack colorizer = (ItemStack)entity.getEntityData().get(EntitySpellCircle.COLORIZER_DATA);
      int color = Psi.proxy.getColorForColorizer(colorizer);
      float alive = entity.getTimeAlive() + partialTicks;
      float scale = Math.min((Float)entity.getEntityData().get(EntitySpellCircle.SCALE), alive / 5.0F);
      int lifetime = (Integer)entity.getEntityData().get(EntitySpellCircle.LIFETIME);
      if (alive > lifetime - 5) {
         scale = 1.0F - Math.min(1.0F, Math.max(0.0F, alive - (lifetime - 5)) / 5.0F);
      }

      renderSpellCircle(
         alive,
         scale,
         1.0F,
         (Float)entity.getEntityData().get(EntitySpellCircle.DIRECTION_X),
         (Float)entity.getEntityData().get(EntitySpellCircle.DIRECTION_Y),
         (Float)entity.getEntityData().get(EntitySpellCircle.DIRECTION_Z),
         color,
         ms,
         buffers
      );
      ms.popPose();
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull EntitySpellCircle entitySpellCircle) {
      return PsiAPI.location("spell_circle");
   }

   static {
      for (int i = 0; i < LAYERS.length; i++) {
         ResourceLocation texture = ResourceLocation.parse(String.format("psi:textures/misc/spell_circle%d.png", i));
         CompositeState glState = CompositeState.builder()
            .setTextureState(new TextureStateShard(texture, false, false))
            .setCullState(new CullStateShard(false))
            .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
            .setLightmapState(new LightmapStateShard(true))
            .createCompositeState(true);
         LAYERS[i] = RenderType.create("psi:spell_circle_" + i, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 64, false, false, glState);
      }
   }
}
