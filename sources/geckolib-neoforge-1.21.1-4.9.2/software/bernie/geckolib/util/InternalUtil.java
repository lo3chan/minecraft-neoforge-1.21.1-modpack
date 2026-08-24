package software.bernie.geckolib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Internal
public class InternalUtil {
   public static <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> boolean tryRenderGeoArmorPiece(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      T entity,
      ItemStack stack,
      EquipmentSlot equipmentSlot,
      M parentModel,
      A baseModel,
      float partialTick,
      int packedLight,
      float limbSwing,
      float limbSwingAmount,
      float lerpedTickCount,
      float netHeadYaw,
      float headPitch,
      BiConsumer<A, EquipmentSlot> partVisibilitySetter
   ) {
      Item item = stack.getItem();
      if (item instanceof Equipable equipable && equipable.getEquipmentSlot() == equipmentSlot) {
         HumanoidModel<?> geckolibModel = GeoRenderProvider.of(item).getGeoArmorRenderer(entity, stack, equipmentSlot, baseModel);
         if (geckolibModel == null) {
            return false;
         } else {
            parentModel.copyPropertiesTo(baseModel);
            partVisibilitySetter.accept(baseModel, equipmentSlot);
            if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer) {
               geoArmorRenderer.prepForRender(
                  entity, stack, equipmentSlot, baseModel, bufferSource, partialTick, limbSwing, limbSwingAmount, netHeadYaw, headPitch
               );
            }

            baseModel.copyPropertiesTo(geckolibModel);
            geckolibModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean areComponentsMatchingIgnoringGeckoLibId(PatchedDataComponentMap map1, PatchedDataComponentMap map2) {
      DataComponentType<Long> stackId = GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get();
      boolean patched = false;
      if (map1.has(stackId)) {
         boolean copyOnWrite = map1.copyOnWrite;
         (map1 = map1.copy()).remove(stackId);
         map1.copyOnWrite = copyOnWrite;
         patched = true;
      }

      if (map2.has(stackId)) {
         boolean copyOnWrite = map2.copyOnWrite;
         (map2 = map2.copy()).remove(stackId);
         map2.copyOnWrite = copyOnWrite;
         patched = true;
      }

      return patched && Objects.equals(map1, map2);
   }
}
