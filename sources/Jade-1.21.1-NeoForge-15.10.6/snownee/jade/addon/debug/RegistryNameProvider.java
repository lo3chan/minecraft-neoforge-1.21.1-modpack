package snownee.jade.addon.debug;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.material.FluidState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IToggleableProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.util.CommonProxy;

public abstract class RegistryNameProvider implements IToggleableProvider {
   public static RegistryNameProvider.ForBlock getBlock() {
      return RegistryNameProvider.ForBlock.INSTANCE;
   }

   public static RegistryNameProvider.ForEntity getEntity() {
      return RegistryNameProvider.ForEntity.INSTANCE;
   }

   public boolean append(ITooltip tooltip, String id, IPluginConfig config) {
      RegistryNameProvider.Mode mode = config.getEnum(JadeIds.DEBUG_REGISTRY_NAME);
      if (mode == RegistryNameProvider.Mode.OFF) {
         return false;
      } else if (mode == RegistryNameProvider.Mode.ADVANCED_TOOLTIPS && !Minecraft.getInstance().options.advancedItemTooltips) {
         return false;
      } else {
         tooltip.add(IWailaConfig.get().getFormatting().registryName(id));
         return true;
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.DEBUG_REGISTRY_NAME;
   }

   @Override
   public boolean isRequired() {
      return true;
   }

   @Override
   public int getDefaultPriority() {
      return -9900;
   }

   public static class ForBlock extends RegistryNameProvider implements IBlockComponentProvider {
      private static final RegistryNameProvider.ForBlock INSTANCE = new RegistryNameProvider.ForBlock();

      public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
         if (this.append(tooltip, CommonProxy.getId(accessor.getBlock()).toString(), config) && config.get(JadeIds.DEBUG_SPECIAL_REGISTRY_NAME)) {
            if (accessor.getBlockEntity() != null) {
               ResourceLocation id = CommonProxy.getId(accessor.getBlockEntity().getType());
               String s = I18n.get("config.jade.plugin_jade.registry_name.special.block_entity_type", new Object[]{id});
               tooltip.add(IWailaConfig.get().getFormatting().registryName(s), JadeIds.DEBUG_SPECIAL_REGISTRY_NAME);
            }

            FluidState fluidState = accessor.getBlockState().getFluidState();
            if (!fluidState.isEmpty()) {
               ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluidState.getType());
               String s = I18n.get("config.jade.plugin_jade.registry_name.special.fluid", new Object[]{id});
               tooltip.add(IWailaConfig.get().getFormatting().registryName(s), JadeIds.DEBUG_SPECIAL_REGISTRY_NAME);
            }

            Optional<Holder<PoiType>> poiTypeHolder = PoiTypes.forState(accessor.getBlockState());
            if (poiTypeHolder.isPresent()) {
               ResourceLocation id = ((ResourceKey)poiTypeHolder.get().unwrapKey().orElseThrow()).location();
               String s = I18n.get("config.jade.plugin_jade.registry_name.special.poi", new Object[]{id});
               tooltip.add(IWailaConfig.get().getFormatting().registryName(s), JadeIds.DEBUG_SPECIAL_REGISTRY_NAME);
            }
         }
      }
   }

   public static class ForEntity extends RegistryNameProvider implements IEntityComponentProvider {
      private static final RegistryNameProvider.ForEntity INSTANCE = new RegistryNameProvider.ForEntity();

      public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
         if (this.append(tooltip, CommonProxy.getId(accessor.getEntity().getType()).toString(), config)
            && config.get(JadeIds.DEBUG_SPECIAL_REGISTRY_NAME)
            && accessor.getEntity() instanceof Painting painting) {
            ResourceLocation id = ((ResourceKey)painting.getVariant().unwrapKey().orElseThrow()).location();
            String s = I18n.get("config.jade.plugin_jade.registry_name.special.painting", new Object[]{id});
            tooltip.add(IWailaConfig.get().getFormatting().registryName(s), JadeIds.DEBUG_SPECIAL_REGISTRY_NAME);
         }
      }
   }

   public static enum Mode {
      ON,
      OFF,
      ADVANCED_TOOLTIPS;
   }
}
