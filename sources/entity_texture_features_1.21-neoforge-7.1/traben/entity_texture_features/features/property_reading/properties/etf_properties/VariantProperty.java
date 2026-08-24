package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Optional;
import java.util.Properties;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class VariantProperty extends StringArrayOrRegexProperty {
   protected VariantProperty(String string) throws RandomProperty.RandomPropertyException {
      super(string);
   }

   public static VariantProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new VariantProperty(readPropertiesOrThrow(properties, propertyNum, new String[]{"variant", "variants"}));
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return false;
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState state) {
      if (state == null) {
         return null;
      } else {
         ETFEntity etfEntity = state.entity();
         if (etfEntity instanceof Entity) {
            if (etfEntity instanceof VariantHolder<?> variableEntity) {
               if (variableEntity.getVariant() instanceof StringRepresentable stringIdentifiable) {
                  return stringIdentifiable.getSerializedName();
               } else if (variableEntity.getVariant() instanceof CatVariant catVariant) {
                  return BuiltInRegistries.CAT_VARIANT
                     .getResourceKey(catVariant)
                     .map(catVariantRegistryKey -> catVariantRegistryKey.location().getPath())
                     .orElse(null);
               } else if (variableEntity.getVariant() instanceof FrogVariant frogVariant) {
                  return BuiltInRegistries.FROG_VARIANT
                     .getResourceKey(frogVariant)
                     .map(frogVariantRegistryKey -> frogVariantRegistryKey.location().getPath())
                     .orElse(null);
               } else if (variableEntity.getVariant() instanceof Holder<?> registryEntry) {
                  return registryEntry.unwrapKey().isPresent() ? ((ResourceKey)registryEntry.unwrapKey().get()).location().getPath() : null;
               } else if (variableEntity.getVariant() instanceof Optional<?> possibleStringIdentifiable) {
                  return possibleStringIdentifiable.isPresent() && possibleStringIdentifiable.get() instanceof StringRepresentable stringIdentifiable
                     ? stringIdentifiable.getSerializedName()
                     : null;
               } else {
                  return variableEntity.getVariant() instanceof VillagerType villagerType ? villagerType.toString() : variableEntity.getVariant().toString();
               }
            } else {
               return BuiltInRegistries.ENTITY_TYPE.getResourceKey(((Entity)etfEntity).getType()).map(key -> key.location().getPath()).orElse(null);
            }
         } else if (etfEntity instanceof BlockEntity) {
            if (etfEntity instanceof SignBlockEntity signBlockEntity && signBlockEntity.getBlockState().getBlock() instanceof SignBlock abstractSignBlock) {
               return abstractSignBlock.type().name();
            } else if (etfEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity
               && shulkerBoxBlockEntity.getBlockState().getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock) {
               return String.valueOf(shulkerBoxBlock.getColor());
            } else if (etfEntity instanceof BedBlockEntity bedBlockEntity && bedBlockEntity.getBlockState().getBlock() instanceof BedBlock bedBlock) {
               return String.valueOf(bedBlock.getColor());
            } else if (etfEntity instanceof DecoratedPotBlockEntity pot) {
               PotDecorations sherds = pot.getDecorations();
               return (sherds.back().isPresent() ? ((Item)sherds.back().get()).getDescriptionId() : "none")
                  + ","
                  + (sherds.left().isPresent() ? ((Item)sherds.left().get()).getDescriptionId() : "none")
                  + ","
                  + (sherds.right().isPresent() ? ((Item)sherds.right().get()).getDescriptionId() : "none")
                  + ","
                  + (sherds.front().isPresent() ? ((Item)sherds.front().get()).getDescriptionId() : "none");
            } else {
               String suffix = "";
               if (etfEntity instanceof SkullBlockEntity skull) {
                  suffix = "_direction_" + skull.getBlockState().getValue(SkullBlock.ROTATION);
               }

               return BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(((BlockEntity)etfEntity).getType()).map(key -> key.location().getPath()).orElse(null)
                  + suffix;
            }
         } else {
            return null;
         }
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"variant", "variants"};
   }
}
