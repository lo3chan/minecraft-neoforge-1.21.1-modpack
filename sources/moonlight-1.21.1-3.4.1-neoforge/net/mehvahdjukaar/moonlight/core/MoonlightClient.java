package net.mehvahdjukaar.moonlight.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidColors;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicClientResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcePack;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.SimplePackProvider;
import net.mehvahdjukaar.moonlight.core.client.MLRenderTypes;
import net.mehvahdjukaar.moonlight.core.client.MoonlightHubInfo;
import net.mehvahdjukaar.moonlight.core.client.SimpleSpecialModelsLoader;
import net.mehvahdjukaar.moonlight.core.client.SpawnBoxBlockEntityRenderer;
import net.mehvahdjukaar.moonlight.core.pack.DynamicResourcesInternals;
import net.mehvahdjukaar.moonlight.core.pack.MergedDynamicClientResourcesProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Vector3f;

@Internal
public class MoonlightClient {
   private static final ThreadLocal<Boolean> MAP_MIPMAP = ThreadLocal.withInitial(() -> false);
   private static final MergedDynamicClientResourcesProvider INSTANCE = new MergedDynamicClientResourcesProvider(
      new PackLocationInfo("moonlight:merged_pack", Component.translatable("message.moonlight.merged_pack.title"), PackSource.BUILT_IN, Optional.empty())
   );
   public static ClientConfigs.ShadeFix fixShade = ClientConfigs.ShadeFix.FALSE;
   public static final Vector3f NEW_L_0 = new Vector3f(0.2F, 0.7777778F, -0.6F).normalize();
   public static final Vector3f NEW_L_1 = new Vector3f(-0.2F, 0.7777778F, 0.6F).normalize();

   public static void initClient() {
      ClientConfigs.init();
      MoonlightHubInfo.fetchFromServer();
      ClientHelper.addShaderRegistration(MoonlightClient::registerShaders);
      ClientHelper.addClientReloadListener(SoftFluidColors::new, Moonlight.res("soft_fluid"));
      ClientHelper.addBlockEntityRenderersRegistration(
         event -> event.register(MoonlightRegistry.SPAWN_BOX_BLOCK_ENTITY.get(), SpawnBoxBlockEntityRenderer::new)
      );
      SimpleSpecialModelsLoader specialModels = new SimpleSpecialModelsLoader();
      ClientHelper.addClientReloadListener(() -> specialModels, Moonlight.res("special_models_loader"));
      ClientHelper.addSpecialModelRegistration(specialModelEvent -> specialModels.getSpecialModels().forEach(specialModelEvent::register));
      RegHelper.registerDynamicResourceProvider(new MoonlightClient.MLDynamicClientResources());
   }

   @Nullable
   public static SimplePackProvider mergePackSupplier(DynamicResourcesProvider provider) {
      if (!ClientConfigs.MERGE_PACKS.get()) {
         return provider;
      } else {
         INSTANCE.add(provider);
         return INSTANCE.size() == 1 ? INSTANCE : null;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean maybeMergeLegacyPack(DynamicResourcePack pack) {
      if (!ClientConfigs.MERGE_PACKS.get()) {
         return false;
      } else {
         INSTANCE.addLegacy(pack);
         if (INSTANCE.size() == 1) {
            RegHelper.registerResourcePack(PackType.CLIENT_RESOURCES, INSTANCE::createPack);
         }

         return true;
      }
   }

   public static boolean isClientThread() {
      return Minecraft.getInstance().isSameThread();
   }

   public static void setupClient() {
      ExtraModelData e = ExtraModelData.EMPTY;
   }

   public static void onItemTooltip(ItemStack stack, TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> list) {
      if (ClientConfigs.TAGS_TOOLTIP.get().isOn(tooltipFlag) && tooltipFlag.isAdvanced()) {
         if (stack.getItem() instanceof BlockItem bi) {
            Block b = bi.getBlock();
            BlockState state = b.defaultBlockState();
            List<TagKey<Block>> tags = state.getTags().toList();
            if (!tags.isEmpty()) {
               list.add(Component.translatable("tooltip.moonlight.block_tags").withStyle(ChatFormatting.GREEN));
               tags.forEach(k -> list.add(Component.literal("-" + k.location()).withStyle(Style.EMPTY.withColor(13172680))));
            }
         }

         List<TagKey<Item>> tags = stack.getTags().toList();
         if (!tags.isEmpty()) {
            list.add(Component.translatable("tooltip.moonlight.item_tags").withStyle(ChatFormatting.LIGHT_PURPLE));
            tags.forEach(k -> list.add(Component.literal("-" + k.location()).withStyle(Style.EMPTY.withColor(16763135))));
         }
      }
   }

   public static void registerShaders(ClientHelper.ShaderEvent event) {
      event.register(Moonlight.res("particle_translucent"), DefaultVertexFormat.POSITION_TEX, MLRenderTypes.PARTICLE_TRANSLUCENT_SHADER::assign);
      event.register(Moonlight.res("text_alpha_color"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, MLRenderTypes.TEXT_COLOR_SHADER::assign);
   }

   public static void afterTextureReload() {
      DynamicResourcesInternals.clearAfterReload(PackType.CLIENT_RESOURCES);
   }

   public static void setMipMap(boolean b) {
      if (ClientConfigs.MAPS_MIPMAP.get() == 0) {
         b = false;
      }

      MAP_MIPMAP.set(b);
   }

   public static boolean isMapMipMap() {
      return MAP_MIPMAP.get();
   }

   private static class MLDynamicClientResources extends DynamicClientResourceProvider {
      protected MLDynamicClientResources() {
         super(Moonlight.res("dynamic_resources"), PackGenerationStrategy.REGEN_ON_EVERY_RELOAD);
      }

      @Override
      protected void addDynamicTranslations(AfterLanguageLoadEvent afterLanguageLoadEvent) {
      }

      @Override
      protected Collection<String> gatherSupportedNamespaces() {
         return List.of("minecraft");
      }

      @Override
      protected void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
         MoonlightClient.fixShade = ClientConfigs.FIX_SHADE.get();
         if (MoonlightClient.fixShade != ClientConfigs.ShadeFix.FALSE) {
            executor.accept(
               (manager, sink) -> sink.addBytes(
                  ResourceLocation.parse("shaders/include/light.glsl"),
                  "#version 150\n\n#define MINECRAFT_LIGHT_POWER   (0.6)\n#define MINECRAFT_LIGHT_POWER_FIXED   (0.5)\n#define MINECRAFT_AMBIENT_LIGHT (0.4)\n#define MINECRAFT_AMBIENT_LIGHT_FIXED (0.5)\n\nvec4 minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color) {\n    lightDir0 = normalize(lightDir0);\n    lightDir1 = normalize(lightDir1);\n    float light0 = max(0.0, dot(lightDir0, normal));\n    float light1 = max(0.0, dot(lightDir1, normal));\n\n    float dotP = dot(lightDir0, lightDir1);\n    bool isFixed = dotP > 0.20 && dotP < 0.205;\n    float lightPow = isFixed ? MINECRAFT_LIGHT_POWER_FIXED : MINECRAFT_LIGHT_POWER;\n    float ambientLight = isFixed ? MINECRAFT_AMBIENT_LIGHT_FIXED : MINECRAFT_AMBIENT_LIGHT;\n\n    float lightAccum = min(1.0, (light0 + light1) * lightPow + ambientLight);\n    return vec4(color.rgb * lightAccum, color.a);\n}\n\nvec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {\n    return texture(lightMap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));\n}"
                     .getBytes(),
                  ResType.GENERIC
               )
            );
         }
      }
   }
}
