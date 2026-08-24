package at.petrak.hexcasting.forge.mixin;

import at.petrak.hexcasting.common.recipe.RecipeSerializerBase;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {RecipeSerializerBase.class},
   remap = false
)
public class ForgeMixinCursedRecipeSerializerBase {
   @Shadow
   @Nullable
   private ResourceLocation registryName;

   public Object setRegistryName(ResourceLocation name) {
      this.registryName = name;
      return this;
   }
}
