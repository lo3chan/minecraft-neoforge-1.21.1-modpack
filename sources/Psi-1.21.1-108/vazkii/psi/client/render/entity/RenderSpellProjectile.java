package vazkii.psi.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.entity.EntitySpellProjectile;

public class RenderSpellProjectile extends EntityRenderer<EntitySpellProjectile> {
   public RenderSpellProjectile(Context ctx) {
      super(ctx);
   }

   public boolean shouldRender(@NotNull EntitySpellProjectile livingEntityIn, @NotNull Frustum camera, double camX, double camY, double camZ) {
      return false;
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull EntitySpellProjectile entity) {
      return PsiAPI.location("spell_projectile");
   }
}
