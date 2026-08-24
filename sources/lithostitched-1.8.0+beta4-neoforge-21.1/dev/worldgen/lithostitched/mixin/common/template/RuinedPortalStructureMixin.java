package dev.worldgen.lithostitched.mixin.common.template;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RuinedPortalStructure.class})
public class RuinedPortalStructureMixin {
   @Redirect(
      method = {"findGenerationPoint"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
         ordinal = 1
      )
   )
   private ResourceLocation useStandardTemplateList(String name, GenerationContext context) {
      return !Lithostitched.breaksSeedParity()
         ? ResourceLocation.withDefaultNamespace(name)
         : TemplateLists.getRandom(context.registryAccess(), TemplateLists.RUINED_PORTAL_STANDARD, context.random());
   }

   @Redirect(
      method = {"findGenerationPoint"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
         ordinal = 0
      )
   )
   private ResourceLocation useGiantTemplateList(String name, GenerationContext context) {
      return !Lithostitched.breaksSeedParity()
         ? ResourceLocation.withDefaultNamespace(name)
         : TemplateLists.getRandom(context.registryAccess(), TemplateLists.RUINED_PORTAL_GIANT, context.random());
   }
}
