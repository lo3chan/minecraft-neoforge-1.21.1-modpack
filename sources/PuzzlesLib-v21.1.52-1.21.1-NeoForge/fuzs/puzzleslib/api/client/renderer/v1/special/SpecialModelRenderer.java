package fuzs.puzzleslib.api.client.renderer.v1.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface SpecialModelRenderer<T> {
   void render(@Nullable T var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6);

   @Nullable
   T extractArgument(ItemStack var1);

   public interface Unbaked<T> {
      @Nullable
      SpecialModelRenderer<T> bake(EntityModelSet var1);

      MapCodec<? extends SpecialModelRenderer.Unbaked<T>> type();
   }
}
