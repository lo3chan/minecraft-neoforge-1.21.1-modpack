package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFException;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFTexture {
   public static final String PATCH_NAMESPACE_PREFIX = "etf_patched_";
   public final ResourceLocation thisIdentifier;
   public ETFTexture.TextureReturnState currentTextureState = ETFTexture.TextureReturnState.NORMAL;
   public String eSuffix = null;
   private ResourceLocation thisIdentifier_Patched = null;
   private ResourceLocation emissiveIdentifier = null;
   private ResourceLocation emissiveBlinkIdentifier = null;
   private ResourceLocation emissiveBlink2Identifier = null;
   private ResourceLocation enchantIdentifier = null;
   private ResourceLocation enchantBlinkIdentifier = null;
   private ResourceLocation enchantBlink2Identifier = null;
   private ResourceLocation blinkIdentifier = null;
   private ResourceLocation blink2Identifier = null;
   private ResourceLocation blinkIdentifier_Patched = null;
   private ResourceLocation blink2Identifier_Patched = null;
   private Integer blinkLength = ETF.config().getConfig().blinkLength;
   private Integer blinkFrequency = ETF.config().getConfig().blinkFrequency;
   private boolean isBuilt = false;
   private ETFSprite atlasSprite = null;
   private boolean hasBeenReRegistered = false;
   private Boolean resourceExists = null;
   private boolean guiBlink = false;
   private boolean hasPatched = false;

   public ETFTexture(ResourceLocation variantIdentifier) {
      if (variantIdentifier == null) {
         ETFUtils2.logError("ETFTexture had a null identifier this should NOT happen");
         this.thisIdentifier = null;
      } else {
         this.thisIdentifier = variantIdentifier;
         this.setupBlinking();
         this.setupEmissives();
         this.setupEnchants();
      }
   }

   public static ETFTexture manual(
      @NotNull ResourceLocation modifiedSkinIdentifier,
      @Nullable ResourceLocation blinkIdentifier,
      @Nullable ResourceLocation blink2Identifier,
      @Nullable ResourceLocation emissiveIdentifier,
      @Nullable ResourceLocation blinkEmissiveIdentifier,
      @Nullable ResourceLocation blink2EmissiveIdentifier,
      @Nullable ResourceLocation enchantIdentifier,
      @Nullable ResourceLocation blinkenchantIdentifier,
      @Nullable ResourceLocation blink2enchantIdentifier,
      @Nullable ResourceLocation patchIdentifier,
      @Nullable ResourceLocation blinkpatchIdentifier,
      @Nullable ResourceLocation blink2patchIdentifier
   ) {
      return new ETFTexture(
         modifiedSkinIdentifier,
         blinkIdentifier,
         blink2Identifier,
         emissiveIdentifier,
         blinkEmissiveIdentifier,
         blink2EmissiveIdentifier,
         enchantIdentifier,
         blinkenchantIdentifier,
         blink2enchantIdentifier,
         patchIdentifier,
         blinkpatchIdentifier,
         blink2patchIdentifier
      );
   }

   public static ETFTexture manual(
      @NotNull ResourceLocation modifiedSkinIdentifier, @Nullable ResourceLocation emissiveIdentifier, @Nullable ResourceLocation enchantIdentifier
   ) {
      return new ETFTexture(modifiedSkinIdentifier, null, null, emissiveIdentifier, null, null, enchantIdentifier, null, null, null, null, null);
   }

   private ETFTexture(
      @NotNull ResourceLocation modifiedSkinIdentifier,
      @Nullable ResourceLocation blinkIdentifier,
      @Nullable ResourceLocation blink2Identifier,
      @Nullable ResourceLocation emissiveIdentifier,
      @Nullable ResourceLocation blinkEmissiveIdentifier,
      @Nullable ResourceLocation blink2EmissiveIdentifier,
      @Nullable ResourceLocation enchantIdentifier,
      @Nullable ResourceLocation blinkenchantIdentifier,
      @Nullable ResourceLocation blink2enchantIdentifier,
      @Nullable ResourceLocation patchIdentifier,
      @Nullable ResourceLocation blinkpatchIdentifier,
      @Nullable ResourceLocation blink2patchIdentifier
   ) {
      this.thisIdentifier = modifiedSkinIdentifier;
      this.blinkIdentifier = blinkIdentifier;
      this.blink2Identifier = blink2Identifier;
      this.emissiveIdentifier = emissiveIdentifier;
      this.emissiveBlinkIdentifier = blinkEmissiveIdentifier;
      this.emissiveBlink2Identifier = blink2EmissiveIdentifier;
      this.thisIdentifier_Patched = patchIdentifier;
      this.blinkIdentifier_Patched = blinkpatchIdentifier;
      this.blink2Identifier_Patched = blink2patchIdentifier;
      this.enchantIdentifier = enchantIdentifier;
      this.enchantBlinkIdentifier = blinkenchantIdentifier;
      this.enchantBlink2Identifier = blink2enchantIdentifier;
      this.hasPatched = this.thisIdentifier_Patched != null;
      if (this.hasPatched) {
         ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.thisIdentifier_Patched, this);
         if (this.blinkIdentifier_Patched != null) {
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blinkIdentifier_Patched, this);
         }

         if (this.blink2Identifier_Patched != null) {
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blink2Identifier_Patched, this);
         }
      }

      ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.thisIdentifier, this);
      if (blinkIdentifier != null) {
         ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blinkIdentifier, this);
      }

      if (blink2Identifier != null) {
         ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blink2Identifier, this);
      }
   }

   private ETFTexture(@NotNull ResourceLocation modifiedSkinIdentifier, @Nullable ResourceLocation emissiveIdentifier) {
      this.thisIdentifier = modifiedSkinIdentifier;
      this.emissiveIdentifier = emissiveIdentifier;
   }

   public static ETFTexture ofUnmodifiable(@NotNull ResourceLocation identifier, @Nullable ResourceLocation emissiveIdentifier) {
      return new ETFTexture(identifier, emissiveIdentifier);
   }

   public static void patchTextureToRemoveZFightingWithOtherTexture(NativeImage baseImage, NativeImage otherImage) throws IndexOutOfBoundsException {
      try {
         if (otherImage.getWidth() == baseImage.getWidth() && otherImage.getHeight() == baseImage.getHeight()) {
            for (int x = 0; x < baseImage.getWidth(); x++) {
               for (int y = 0; y < baseImage.getHeight(); y++) {
                  if (otherImage.getLuminanceOrAlpha(x, y) != 0) {
                     ETFUtils2.setPixel(baseImage, x, y, 0);
                  }
               }
            }
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         throw new ETFException("additional texture is not the correct size, ETF has crashed in the patching stage");
      }
   }

   private static boolean doesAnimaticaVersionExist(ResourceLocation identifier) {
      if (identifier == null) {
         return false;
      } else {
         String idString = identifier.toString();
         return idString.endsWith("-anim") ? true : Minecraft.getInstance().getTextureManager().getTexture(ETFUtils2.res(idString + "-anim"), null) != null;
      }
   }

   private void setupBlinking() {
      if (ETF.config().getConfig().enableBlinking) {
         ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
         Optional<Resource> vanillaR1 = resourceManager.getResource(this.thisIdentifier);
         if (!vanillaR1.isEmpty()) {
            ResourceLocation possibleBlinkIdentifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink.png");
            Optional<Resource> blinkR1 = resourceManager.getResource(possibleBlinkIdentifier);
            if (!blinkR1.isEmpty()) {
               String blink1PackName = blinkR1.get().sourcePackId();
               if (blink1PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(blink1PackName, vanillaR1.get().sourcePackId()))) {
                  this.blinkIdentifier = possibleBlinkIdentifier;
                  ResourceLocation possibleBlink2Identifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink2.png");
                  Optional<Resource> blinkR2 = resourceManager.getResource(possibleBlink2Identifier);
                  if (blinkR2.isPresent() && blink1PackName.equals(blinkR2.get().sourcePackId())) {
                     this.blink2Identifier = possibleBlink2Identifier;
                  }

                  ResourceLocation propertyIdentifier = ETFUtils2.replaceIdentifier(possibleBlinkIdentifier, ".png", ".properties");
                  Properties blinkingProps = ETFUtils2.readAndReturnPropertiesElseNull(propertyIdentifier);
                  if (blinkingProps != null) {
                     Optional<Resource> propertyResource = resourceManager.getResource(propertyIdentifier);
                     if (!propertyResource.isEmpty()
                        && propertyResource.get()
                           .sourcePackId()
                           .equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(propertyResource.get().sourcePackId(), blink1PackName))) {
                        this.blinkLength = blinkingProps.containsKey("blinkLength")
                           ? Integer.parseInt(blinkingProps.getProperty("blinkLength").replaceAll("\\D", ""))
                           : ETF.config().getConfig().blinkLength;
                        this.blinkFrequency = blinkingProps.containsKey("blinkFrequency")
                           ? Integer.parseInt(blinkingProps.getProperty("blinkFrequency").replaceAll("\\D", ""))
                           : ETF.config().getConfig().blinkFrequency;
                     }
                  }
               }
            }
         }
      }
   }

   public boolean exists() {
      if (this.resourceExists == null) {
         this.resourceExists = Minecraft.getInstance().getResourceManager().getResource(this.thisIdentifier).isPresent();
      }

      return this.isBuilt || this.resourceExists;
   }

   private void setupEmissives() {
      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

      for (String suffix : ETFManager.getInstance().EMISSIVE_SUFFIX_LIST) {
         Optional<Resource> baseResource = this.getResourceOrModifyForTrims(resourceManager);
         if (!baseResource.isEmpty()) {
            ResourceLocation emissiveId = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", suffix + ".png");
            Optional<Resource> emissiveResource = resourceManager.getResource(emissiveId);
            if (!emissiveResource.isEmpty()) {
               String emissivePack = emissiveResource.get().sourcePackId();
               if (emissivePack.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(emissivePack, baseResource.get().sourcePackId()))) {
                  this.emissiveIdentifier = emissiveId;
                  this.eSuffix = suffix;
                  ResourceLocation blinkId = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink" + suffix + ".png");
                  Optional<Resource> blinkResource = resourceManager.getResource(blinkId);
                  if (blinkResource.isPresent() && emissivePack.equals(blinkResource.get().sourcePackId())) {
                     this.emissiveBlinkIdentifier = blinkId;
                     ResourceLocation blink2Id = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink2" + suffix + ".png");
                     Optional<Resource> blink2Resource = resourceManager.getResource(blink2Id);
                     if (blink2Resource.isPresent() && emissivePack.equals(blink2Resource.get().sourcePackId())) {
                        this.emissiveBlink2Identifier = blink2Id;
                     }
                  }
                  break;
               }
            }
         }
      }
   }

   private void setupEnchants() {
      if (ETF.config().getConfig().enableEnchantedTextures) {
         ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
         String enchantSuffix = "_enchant";
         Optional<Resource> vanillaR1 = this.getResourceOrModifyForTrims(resourceManager);
         if (!vanillaR1.isEmpty()) {
            ResourceLocation possibleEnchantIdentifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", enchantSuffix + ".png");
            Optional<Resource> enchantR1 = resourceManager.getResource(possibleEnchantIdentifier);
            if (!enchantR1.isEmpty()) {
               String enchantPackName = enchantR1.get().sourcePackId();
               if (enchantPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantPackName, vanillaR1.get().sourcePackId()))) {
                  this.enchantIdentifier = possibleEnchantIdentifier;
                  ResourceLocation possibleEnchantBlinkIdentifier = ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_blink" + enchantSuffix + ".png");
                  Optional<Resource> enchantBlinkR1 = resourceManager.getResource(possibleEnchantBlinkIdentifier);
                  if (!enchantBlinkR1.isEmpty()) {
                     String enchantBlinkPackName = enchantBlinkR1.get().sourcePackId();
                     if (enchantBlinkPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantBlinkPackName, vanillaR1.get().sourcePackId()))) {
                        this.enchantBlinkIdentifier = possibleEnchantBlinkIdentifier;
                        ResourceLocation possibleEnchantBlink2Identifier = ETFUtils2.replaceIdentifier(
                           this.thisIdentifier, ".png", "_blink2" + enchantSuffix + ".png"
                        );
                        Optional<Resource> enchantBlink2R1 = resourceManager.getResource(possibleEnchantBlink2Identifier);
                        if (!enchantBlink2R1.isEmpty()) {
                           String enchantBlink2PackName = enchantBlink2R1.get().sourcePackId();
                           if (enchantBlink2PackName.equals(
                              ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantBlink2PackName, vanillaR1.get().sourcePackId())
                           )) {
                              this.enchantBlink2Identifier = possibleEnchantBlink2Identifier;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Optional<Resource> getResourceOrModifyForTrims(ResourceManager resourceManager) {
      Optional<Resource> vanillaR1 = resourceManager.getResource(this.thisIdentifier);
      if (vanillaR1.isEmpty()
         && (this.thisIdentifier.getPath().contains("textures/trims/models/armor/") || this.thisIdentifier.getPath().contains("textures/trims/entity/"))) {
         vanillaR1 = resourceManager.getResource(
            ETFUtils2.res(this.thisIdentifier.getNamespace(), this.thisIdentifier.getPath().replaceAll("_(.*?)(?=\\.png)", ""))
         );
         PackResources pack = vanillaR1.<PackResources>map(Resource::source).orElseGet(() -> Minecraft.getInstance().getVanillaPackResources());
         vanillaR1 = Optional.of(new Resource(pack, null));
      }

      return vanillaR1;
   }

   @NotNull
   public ResourceLocation getTextureIdentifier(@Nullable ETFEntityRenderState entity) {
      if (this.canPatch()) {
         this.currentTextureState = ETFTexture.TextureReturnState.NORMAL_PATCHED;
      } else {
         this.currentTextureState = ETFTexture.TextureReturnState.NORMAL;
      }

      return this.getBlinkingIdentifier(entity);
   }

   @NotNull
   private ResourceLocation getBlinkingIdentifier(@Nullable ETFEntityRenderState state) {
      if (this.doesBlink() && state != null && state.entity() instanceof LivingEntity) {
         ETFEntity entity = state.entity();
         if (this.guiBlink) {
            this.setBlink(Math.abs((int)System.currentTimeMillis() / 20 % 50000), 0);
         } else if (((LivingEntity)entity).hasPose(Pose.SLEEPING)) {
            this.modifyTextureState(ETFTexture.TextureReturnState.APPLY_BLINK);
         } else if (((LivingEntity)entity).hasEffect(MobEffects.BLINDNESS)) {
            this.modifyTextureState(this.doesBlink2() ? ETFTexture.TextureReturnState.APPLY_BLINK2 : ETFTexture.TextureReturnState.APPLY_BLINK);
         } else {
            this.setBlink(((LivingEntity)entity).tickCount, Math.abs(state.uuid().hashCode()));
         }

         return this.identifierOfCurrentState();
      } else {
         return this.identifierOfCurrentState();
      }
   }

   private void setBlink(int currentTime, int hash) {
      int uuidHash = hash % (this.blinkFrequency * 2) + 20 + this.blinkFrequency;
      int timeModulated = Math.abs(currentTime % uuidHash);
      if (timeModulated <= this.blinkLength + this.blinkLength) {
         if (this.doesBlink2()) {
            if (timeModulated >= this.blinkLength.intValue() / 1.5 && timeModulated <= this.blinkLength + 1 + this.blinkLength / 3) {
               this.modifyTextureState(ETFTexture.TextureReturnState.APPLY_BLINK);
            } else {
               this.modifyTextureState(ETFTexture.TextureReturnState.APPLY_BLINK2);
            }
         } else {
            this.modifyTextureState(ETFTexture.TextureReturnState.APPLY_BLINK);
         }
      }
   }

   public void setGUIBlink() {
      this.blinkFrequency = 100;
      this.blinkLength = 40;
      this.guiBlink = true;
   }

   public boolean isEmissive() {
      return this.emissiveIdentifier != null;
   }

   public boolean isEnchanted() {
      return this.enchantIdentifier != null;
   }

   public boolean canPatch() {
      return ETFRenderContext.isAllowedToPatch() && this.thisIdentifier_Patched != null;
   }

   public boolean doesBlink() {
      return this.blinkIdentifier != null;
   }

   @NotNull
   public ETFSprite getPaintingSprite(@NotNull TextureAtlasSprite originalSprite, @Nullable ResourceLocation originalID) {
      if (this.atlasSprite == null) {
         this.atlasSprite = new ETFSprite(originalSprite, this, this.thisIdentifier.equals(originalID));
      }

      return this.atlasSprite;
   }

   public boolean doesBlink2() {
      return this.blink2Identifier != null;
   }

   @Override
   public String toString() {
      return "[" + this.thisIdentifier.toString() + ", emissive=" + this.isEmissive() + ", blinks=" + this.doesBlink() + "]";
   }

   public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, ModelPart modelPart) {
      this.renderEmissive(matrixStack, vertexConsumerProvider, modelPart, ETFManager.getEmissiveMode());
   }

   public void renderEmissive(
      PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, ModelPart modelPart, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen
   ) {
      VertexConsumer vertexC = this.getEmissiveVertexConsumer(vertexConsumerProvider, null, modeToUsePossiblyManuallyChosen);
      if (vertexC != null) {
         ETFRenderContext.startSpecialRenderOverlayPhase();
         modelPart.render(matrixStack, vertexC, 15728882, OverlayTexture.NO_OVERLAY);
         ETFRenderContext.endSpecialRenderOverlayPhase();
      }
   }

   public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Model model) {
      this.renderEmissive(matrixStack, vertexConsumerProvider, model, ETFManager.getEmissiveMode());
   }

   public void renderEmissive(
      PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen
   ) {
      VertexConsumer vertexC = this.getEmissiveVertexConsumer(vertexConsumerProvider, model, modeToUsePossiblyManuallyChosen);
      if (vertexC != null) {
         ETFRenderContext.startSpecialRenderOverlayPhase();
         model.renderToBuffer(matrixStack, vertexC, 15728882, OverlayTexture.NO_OVERLAY);
         ETFRenderContext.endSpecialRenderOverlayPhase();
      }
   }

   @Nullable
   public VertexConsumer getEmissiveVertexConsumer(
      MultiBufferSource vertexConsumerProvider, @Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen
   ) {
      RenderType type = this.getEmissiveRenderLayer(model, modeToUsePossiblyManuallyChosen);
      return type == null ? null : vertexConsumerProvider.getBuffer(type);
   }

   @Nullable
   public RenderType getEmissiveRenderLayer(@Nullable Model model) {
      return this.getEmissiveRenderLayer(model, ETFManager.getEmissiveMode());
   }

   @Nullable
   public RenderType getEmissiveRenderLayer(@Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
      ETFRenderContext.preventRenderLayerTextureModify();
      RenderType type = this.getEmissiveVertexConsumerWrapped(model, modeToUsePossiblyManuallyChosen);
      ETFRenderContext.allowRenderLayerTextureModify();
      return type;
   }

   @Nullable
   private RenderType getEmissiveVertexConsumerWrapped(@Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
      if (this.isEmissive()) {
         ResourceLocation emissiveToUse = this.getEmissiveIdentifierOfCurrentState();
         if (emissiveToUse != null) {
            if (modeToUsePossiblyManuallyChosen == ETFConfig.EmissiveRenderModes.BRIGHT) {
               return RenderType.beaconBeam(emissiveToUse, true);
            }

            if (model == null) {
               return RenderType.entityCutoutNoCull(emissiveToUse);
            }

            return model.renderType(emissiveToUse);
         }
      }

      return null;
   }

   private void modifyTextureState(ETFTexture.TextureReturnState givenState) {
      switch (givenState) {
         case APPLY_BLINK:
            this.currentTextureState = this.currentTextureState == ETFTexture.TextureReturnState.NORMAL_PATCHED
               ? ETFTexture.TextureReturnState.BLINK_PATCHED
               : ETFTexture.TextureReturnState.BLINK;
            break;
         case APPLY_BLINK2:
            this.currentTextureState = switch (this.currentTextureState) {
               case NORMAL_PATCHED, BLINK_PATCHED -> ETFTexture.TextureReturnState.BLINK2_PATCHED;
               default -> ETFTexture.TextureReturnState.BLINK2;
            };
      }
   }

   @NotNull
   private ResourceLocation identifierOfCurrentState() {
      return switch (this.currentTextureState) {
         case NORMAL -> this.thisIdentifier;
         case NORMAL_PATCHED -> this.thisIdentifier_Patched;
         case BLINK -> this.blinkIdentifier;
         case BLINK_PATCHED -> this.blinkIdentifier_Patched;
         case BLINK2 -> this.blink2Identifier;
         case BLINK2_PATCHED -> this.blink2Identifier_Patched;
         default -> this.thisIdentifier;
      };
   }

   @Nullable
   public ResourceLocation getEmissiveIdentifierOfCurrentState() {
      return switch (this.currentTextureState) {
         case NORMAL, NORMAL_PATCHED -> this.emissiveIdentifier;
         case BLINK, BLINK_PATCHED -> this.emissiveBlinkIdentifier;
         case BLINK2, BLINK2_PATCHED -> this.emissiveBlink2Identifier;
         default -> this.emissiveIdentifier;
      };
   }

   @Nullable
   public ResourceLocation getEnchantIdentifierOfCurrentState() {
      return switch (this.currentTextureState) {
         case NORMAL, NORMAL_PATCHED -> this.enchantIdentifier;
         case BLINK, BLINK_PATCHED -> this.enchantBlinkIdentifier;
         case BLINK2, BLINK2_PATCHED -> this.enchantBlink2Identifier;
         default -> this.enchantIdentifier;
      };
   }

   public void assertPatchedTextures() {
      if (this.isEmissive() && !this.hasPatched) {
         this.hasPatched = true;
         ResourceManager files = Minecraft.getInstance().getResourceManager();
         if (!ETF.isThisModLoaded("iris") && !ETF.isThisModLoaded("oculus")
            || !files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_s.png")).isPresent()
               && !files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", "_n.png")).isPresent()) {
            if ((!ETF.isThisModLoaded("animatica") || !doesAnimaticaVersionExist(this.thisIdentifier) && !doesAnimaticaVersionExist(this.emissiveIdentifier))
               && (
                  !ETF.isThisModLoaded("moremcmeta")
                     || !files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", ".png.mcmeta")).isPresent()
                        && !files.getResource(ETFUtils2.replaceIdentifier(this.thisIdentifier, ".png", ".png.moremcmeta")).isPresent()
               )) {
               NativeImage newBaseTexture = ETFUtils2.getNativeImageElseNull(this.thisIdentifier);
               NativeImage newBlinkTexture = ETFUtils2.getNativeImageElseNull(this.blinkIdentifier);
               NativeImage newBlink2Texture = ETFUtils2.getNativeImageElseNull(this.blink2Identifier);
               boolean didPatch = false;
               if (this.emissiveIdentifier != null) {
                  NativeImage emissiveImage = ETFUtils2.getNativeImageElseNull(this.emissiveIdentifier);

                  try {
                     patchTextureToRemoveZFightingWithOtherTexture(newBaseTexture, emissiveImage);
                     didPatch = true;
                     if (this.doesBlink() && this.emissiveBlinkIdentifier != null) {
                        NativeImage emissiveBlinkImage = ETFUtils2.getNativeImageElseNull(this.emissiveBlinkIdentifier);
                        patchTextureToRemoveZFightingWithOtherTexture(newBlinkTexture, emissiveBlinkImage);
                        if (this.doesBlink2() && this.emissiveBlink2Identifier != null) {
                           NativeImage emissiveBlink2Image = ETFUtils2.getNativeImageElseNull(this.emissiveBlink2Identifier);
                           patchTextureToRemoveZFightingWithOtherTexture(newBlink2Texture, emissiveBlink2Image);
                        }
                     }
                  } catch (Exception var9) {
                     var9.printStackTrace();
                  }

                  if (didPatch) {
                     this.thisIdentifier_Patched = ETFUtils2.res("etf_patched_" + this.thisIdentifier.getNamespace(), this.thisIdentifier.getPath());
                     ETFUtils2.registerNativeImageToIdentifier(newBaseTexture, this.thisIdentifier_Patched);
                     ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.thisIdentifier_Patched, this);
                     if (this.doesBlink()) {
                        this.blinkIdentifier_Patched = ETFUtils2.res("etf_patched_" + this.blinkIdentifier.getNamespace(), this.blinkIdentifier.getPath());
                        ETFUtils2.registerNativeImageToIdentifier(newBlinkTexture, this.blinkIdentifier_Patched);
                        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blinkIdentifier_Patched, this);
                        if (this.doesBlink2()) {
                           this.blink2Identifier_Patched = ETFUtils2.res("etf_patched_" + this.blink2Identifier.getNamespace(), this.blink2Identifier.getPath());
                           ETFUtils2.registerNativeImageToIdentifier(newBlink2Texture, this.blink2Identifier_Patched);
                           ETFManager.getInstance().ETF_TEXTURE_CACHE.put(this.blink2Identifier_Patched, this);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static enum TextureReturnState {
      NORMAL,
      NORMAL_PATCHED,
      BLINK,
      BLINK_PATCHED,
      BLINK2,
      BLINK2_PATCHED,
      APPLY_PATCH,
      APPLY_BLINK,
      APPLY_BLINK2;

      @Override
      public String toString() {
         return switch (this) {
            case NORMAL -> "normal";
            case NORMAL_PATCHED -> "normal_patched";
            case BLINK -> "blink";
            case BLINK_PATCHED -> "blink_patched";
            case BLINK2 -> "blink2";
            case BLINK2_PATCHED -> "blink2_patched";
            case APPLY_PATCH -> "apply_patch";
            case APPLY_BLINK -> "apply_blink";
            case APPLY_BLINK2 -> "apply_blink2";
         };
      }
   }
}
