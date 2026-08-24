package traben.entity_model_features.mixin.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFUtils;

@Mixin({BlockEntityRenderers.class})
public class MixinBlockEntityRendererFactories {
   @Unique
   private static final List<String> emf$renderers = new ArrayList<>();

   @Inject(
      method = {"createEntityRenderers"},
      at = {@At("RETURN")}
   )
   private static void emf$clearMarker(Context args, CallbackInfoReturnable<Map<BlockEntityType<?>, BlockEntityRenderer>> cir) {
      if (!EMF.testForForgeLoadingError()) {
         EMFManager.getInstance().currentSpecifiedModelLoading = "";
         EMFManager.getInstance().currentBlockEntityTypeLoading = null;
         if (((EMFConfig)EMF.config().getConfig()).logModelCreationData || ((EMFConfig)EMF.config().getConfig()).automaticModelExporting) {
            EMFUtils.log("Identified block entity renderers: " + emf$renderers);
         }

         emf$renderers.clear();
      }
   }

   @ModifyArg(
      method = {"createEntityRenderers"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"
      )
   )
   private static BiConsumer<BlockEntityType<?>, Object> setEmf$Model(BiConsumer<BlockEntityType<?>, Object> action) {
      return (type, idk) -> {
         if (!EMF.testForForgeLoadingError()) {
            EMFManager.getInstance().currentBlockEntityTypeLoading = type;
            if (BlockEntityType.ENCHANTING_TABLE.equals(type)) {
               EMFManager.getInstance().currentSpecifiedModelLoading = "enchanting_book";
            } else if (BlockEntityType.LECTERN.equals(type)) {
               EMFManager.getInstance().currentSpecifiedModelLoading = "lectern_book";
            } else if (BlockEntityType.CHEST.equals(type)) {
               EMFManager.getInstance().currentSpecifiedModelLoading = "chest";
            } else if (BlockEntityType.ENDER_CHEST.equals(type)) {
               EMFManager.getInstance().currentSpecifiedModelLoading = "ender_chest";
            } else if (BlockEntityType.TRAPPED_CHEST.equals(type)) {
               EMFManager.getInstance().currentSpecifiedModelLoading = "trapped_chest";
            } else if (type.builtInRegistryHolder() != null && type.builtInRegistryHolder().unwrapKey().isPresent()) {
               ResourceLocation id = ((ResourceKey)type.builtInRegistryHolder().unwrapKey().get()).location();
               if (id.getNamespace().equals("minecraft")) {
                  EMFManager.getInstance().currentSpecifiedModelLoading = id.getPath();
               } else {
                  EMFManager.getInstance().currentSpecifiedModelLoading = id.getNamespace() + ":" + id.getPath();
               }
            }

            emf$renderers.add(EMFManager.getInstance().currentSpecifiedModelLoading);
            if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
               EMFUtils.log("Seeing block entity renderer init for: " + EMFManager.getInstance().currentSpecifiedModelLoading);
            }

            action.accept(type, idk);
         }
      };
   }
}
