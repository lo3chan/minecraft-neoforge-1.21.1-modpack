package traben.entity_texture_features;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.property_reading.PropertiesRandomProvider;
import traben.entity_texture_features.features.property_reading.TrueRandomProvider;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

public final class ETFApi {
   public static final int ETFApiVersion = 12;
   public static final UUID ETF_GENERIC_UUID = UUID.nameUUIDFromBytes("GENERIC".getBytes());
   public static final long ETF_SPAWNER_MARKER = 53021371281465L;
   @Deprecated
   public static ETFConfig getETFConfigObject = null;

   public static ETFConfig getETFConfigObject() {
      return ETF.config().getConfig();
   }

   public static void setETFConfigObject(ETFConfig newETFConfig) {
      if (newETFConfig != null) {
         ETF.config().setConfig(newETFConfig);
         saveETFConfigChangesAndResetETF();
      } else {
         ETFUtils2.logError("new config was null: ignoring.");
      }
   }

   public static ETFConfig getCopyOfETFConfigObject() {
      return ETF.config().copyOfConfig();
   }

   public static ETFConfig getDefaultETFConfigObject() {
      return new ETFConfig();
   }

   public static String getBlockEntityTypeToTranslationKey(BlockEntityType<?> type) {
      ResourceLocation id = BlockEntityType.getKey(type);
      return id == null ? null : "block." + id.getNamespace() + "." + id.getPath();
   }

   public static void saveETFConfigChangesAndResetETF() {
      ETF.config().saveToFile();
      ETFManager.resetInstance();
   }

   public static void resetETF() {
      ETFManager.resetInstance();
   }

   public static UUID getUUIDForBlockEntity(BlockEntity blockEntity) {
      BlockState blockEntityState = blockEntity.getBlockState();
      long most = blockEntityState == null ? 9223372036854775807L : blockEntityState.hashCode();
      BlockPos pos = blockEntity.getBlockPos();
      long least = pos == null ? 0L : pos.asLong();
      return new UUID(most, least);
   }

   public static ETFEntityRenderState stateOfEntityOrEntityState(Object entity) {
      if (entity instanceof ETFEntity etfEntity) {
         return ETFEntityRenderState.forEntity(etfEntity);
      } else if (entity instanceof HoldsETFRenderState holdsETFRenderState) {
         return holdsETFRenderState.etf$getState();
      } else {
         throw new IllegalArgumentException("Entity must be an instance of ETFEntity or HoldsETFRenderState");
      }
   }

   @NotNull
   public static ResourceLocation getCurrentETFVariantTextureOfEntity(@NotNull Entity entity, @NotNull ResourceLocation defaultTexture) {
      if (entity != null) {
         ETFEntity etfEntity = (ETFEntity)entity;
         ETFEntityRenderState state = stateOfEntityOrEntityState(etfEntity);
         ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTexture, state);
         if (etfTexture != null) {
            ResourceLocation etfIdentifier = etfTexture.getTextureIdentifier(state);
            if (etfIdentifier != null) {
               return etfIdentifier;
            }
         }
      }

      return defaultTexture;
   }

   public static ResourceLocation getCurrentETFVariantTextureOfBlockEntity(@NotNull BlockEntity entity, @NotNull ResourceLocation defaultTexture) {
      if (entity != null) {
         ETFEntity etfEntity = (ETFEntity)entity;
         return getCurrentETFVariantTextureOfBlockEntityInternal(etfEntity, defaultTexture);
      } else {
         return defaultTexture;
      }
   }

   public static ResourceLocation getCurrentETFVariantTextureOfBlockEntity(
      @NotNull BlockEntity entity, @NotNull ResourceLocation defaultTexture, @NotNull UUID specifiedUUID
   ) {
      if (entity != null) {
         ETFEntity etfEntity = (ETFEntity)entity;
         return getCurrentETFVariantTextureOfBlockEntityInternal(etfEntity, defaultTexture);
      } else {
         return defaultTexture;
      }
   }

   private static ResourceLocation getCurrentETFVariantTextureOfBlockEntityInternal(@NotNull ETFEntity etfEntity, @NotNull ResourceLocation defaultTexture) {
      ETFEntityRenderState state = stateOfEntityOrEntityState(etfEntity);
      ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTexture, state);
      if (etfTexture != null) {
         ResourceLocation etfIdentifier = etfTexture.getTextureIdentifier(state);
         if (etfIdentifier != null) {
            return etfIdentifier;
         }
      }

      return defaultTexture;
   }

   @Deprecated
   @NotNull
   public static ResourceLocation getCurrentETFVariantTextureOfEntity(@NotNull BlockEntity entity, @NotNull ResourceLocation defaultTexture, UUID ignore) {
      return getCurrentETFVariantTextureOfBlockEntity(entity, defaultTexture);
   }

   @Nullable
   public static ResourceLocation getCurrentETFEmissiveTextureOfEntityOrNull(@NotNull Entity entity, @NotNull ResourceLocation defaultTexture) {
      if (entity != null) {
         ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTexture, stateOfEntityOrEntityState(entity));
         if (etfTexture != null) {
            return etfTexture.getEmissiveIdentifierOfCurrentState();
         }
      }

      return null;
   }

   @Nullable
   public static ResourceLocation getCurrentETFEmissiveTextureOfBlockEntityOrNull(@NotNull BlockEntity entity, @NotNull ResourceLocation defaultTexture) {
      if (entity != null) {
         ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTexture, stateOfEntityOrEntityState(entity));
         if (etfTexture != null) {
            return etfTexture.getEmissiveIdentifierOfCurrentState();
         }
      }

      return null;
   }

   public static void renderETFEmissiveModel(
      @NotNull Entity entity,
      @NotNull ResourceLocation defaultTextureOfEntity,
      @NotNull PoseStack matrixStack,
      @NotNull MultiBufferSource vertexConsumerProvider,
      @NotNull Model model
   ) {
      ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTextureOfEntity, stateOfEntityOrEntityState(entity));
      if (etfTexture != null) {
         etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, model);
      }
   }

   public static void renderETFEmissiveModelPart(
      @NotNull Entity entity,
      @NotNull ResourceLocation defaultTextureOfEntity,
      @NotNull PoseStack matrixStack,
      @NotNull MultiBufferSource vertexConsumerProvider,
      @NotNull ModelPart modelPart
   ) {
      ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTextureOfEntity, stateOfEntityOrEntityState(entity));
      if (etfTexture != null) {
         etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, modelPart);
      }
   }

   public static void renderETFEmissiveModel(
      @NotNull BlockEntity entity,
      @NotNull ResourceLocation defaultTextureOfEntity,
      @NotNull PoseStack matrixStack,
      @NotNull MultiBufferSource vertexConsumerProvider,
      @NotNull Model model
   ) {
      ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTextureOfEntity, stateOfEntityOrEntityState(entity));
      if (etfTexture != null) {
         etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, model);
      }
   }

   public static void renderETFEmissiveModelPart(
      @NotNull BlockEntity entity,
      @NotNull ResourceLocation defaultTextureOfEntity,
      @NotNull PoseStack matrixStack,
      @NotNull MultiBufferSource vertexConsumerProvider,
      @NotNull ModelPart modelPart
   ) {
      ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(defaultTextureOfEntity, stateOfEntityOrEntityState(entity));
      if (etfTexture != null) {
         etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, modelPart);
      }
   }

   @Nullable
   public static ETFApi.ETFVariantSuffixProvider getVariantSupplierOrNull(
      ResourceLocation propertiesFileIdentifier, ResourceLocation vanillaIdentifier, String... suffixKeys
   ) {
      return ETFApi.ETFVariantSuffixProvider.getVariantProviderOrNull(propertiesFileIdentifier, vanillaIdentifier, suffixKeys);
   }

   @Deprecated
   public static int getLastMatchingRuleOfEntity(Entity entity) {
      return 0;
   }

   @Deprecated
   public static int getLastMatchingRuleOfBlockEntity(BlockEntity entity) {
      return 0;
   }

   public static void registerCustomRandomPropertyFactory(String yourModId, RandomProperties.RandomPropertyFactory... factories) {
      if (factories != null && factories.length != 0) {
         RandomProperties.register(factories);
         ETFUtils2.logMessage(factories.length + " new ETF Random Properties registered by " + yourModId);
      }
   }

   public static void registerCustomETFConfigWarning(String yourModId, ETFConfigWarning... warnings) {
      if (warnings != null && warnings.length != 0) {
         ETFConfigWarnings.registerConfigWarning(warnings);
         ETFUtils2.logMessage(warnings.length + " new ETF Config Warnings registered by " + yourModId);
      }
   }

   public interface ETFVariantSuffixProvider {
      @Nullable
      static ETFApi.ETFVariantSuffixProvider getVariantProviderOrNull(
         ResourceLocation propertiesFileIdentifier, ResourceLocation vanillaIdentifier, String... suffixKeyName
      ) {
         PropertiesRandomProvider optifine = PropertiesRandomProvider.of(propertiesFileIdentifier, vanillaIdentifier, suffixKeyName);
         TrueRandomProvider random = ETFRenderContext.isRandomLimitedToProperties() ? null : TrueRandomProvider.of(vanillaIdentifier);
         if (optifine == null
            && vanillaIdentifier.getPath().endsWith(".png")
            && "minecraft".equals(vanillaIdentifier.getNamespace())
            && vanillaIdentifier.getPath().contains("_")) {
            String vanId = vanillaIdentifier.getPath().replaceAll("_(tame|angry|nectar|shooting|cold)", "");
            optifine = PropertiesRandomProvider.of(ETFUtils2.res(vanId.replace(".png", ".properties")), ETFUtils2.res(vanId), suffixKeyName);
         }

         if (random == null && optifine == null) {
            return null;
         } else if (optifine == null) {
            return random;
         } else if (random == null) {
            return optifine;
         } else {
            return (ETFApi.ETFVariantSuffixProvider)(optifine.isHigherPackThan(random.getPackName()) ? optifine : random);
         }
      }

      boolean entityCanUpdate(UUID var1);

      Set<Integer> getAllSuffixes();

      int size();

      default int getSuffixForEntity(Entity entityToBeTested) {
         return this.getSuffixForETFEntity(ETFEntityRenderState.forEntity((ETFEntity)entityToBeTested));
      }

      default int getSuffixForBlockEntity(BlockEntity entityToBeTested) {
         return this.getSuffixForETFEntity(ETFEntityRenderState.forEntity((ETFEntity)entityToBeTested));
      }

      int getSuffixForETFEntity(ETFEntityRenderState var1);

      @Deprecated(
         forRemoval = true
      )
      default void setRandomSupplier(ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction entityRandomSeedFunction) {
      }

      @Deprecated(
         forRemoval = true
      )
      public interface EntityRandomSeedFunction {
         int toInt(ETFEntityRenderState var1);
      }
   }
}
