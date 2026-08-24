package net.mehvahdjukaar.moonlight.api.map.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class MapDecorationClientManager {
   public static final ResourceLocation LOCATION_MAP_MARKERS = ResourceLocation.withDefaultNamespace("textures/atlas/map_decorations.png");
   public static final RenderType MAP_MARKERS_RENDER_TYPE = RenderType.text(LOCATION_MAP_MARKERS);
   private static final Map<ResourceLocation, Function<ResourceLocation, MapDecorationRenderer<?>>> CUSTOM_RENDERERS_FACTORIES = Maps.newHashMap();
   private static final Map<MLMapDecorationType<?, ?>, MapDecorationRenderer<?>> RENDERERS = Maps.newHashMap();

   public static <T extends MLMapDecoration> void registerCustomRenderer(
      ResourceLocation typeFactoryId, Function<ResourceLocation, MapDecorationRenderer<T>> renderer
   ) {
      CUSTOM_RENDERERS_FACTORIES.put(typeFactoryId, renderer);
   }

   private static MapDecorationRenderer<?> createRenderer(Holder<MLMapDecorationType<?, ?>> type) {
      ResourceLocation id = ((ResourceKey)type.unwrapKey().get()).location();
      Function<ResourceLocation, MapDecorationRenderer<?>> custom = CUSTOM_RENDERERS_FACTORIES.get(((MLMapDecorationType)type.value()).getCustomFactoryID());
      return custom != null ? custom.apply(id) : new MapDecorationRenderer(id);
   }

   public static <E extends MLMapDecoration> MapDecorationRenderer<E> getRenderer(E decoration) {
      return getRenderer(decoration.getType());
   }

   @Deprecated(
      forRemoval = true
   )
   public static <E extends MLMapDecoration, T extends MLMapDecorationType<E, ?>> MapDecorationRenderer<E> getRenderer(T type) {
      return getRenderer(MapDataInternal.hackyGetRegistry().wrapAsHolder(type));
   }

   public static <E extends MLMapDecoration> MapDecorationRenderer<E> getRenderer(Holder<MLMapDecorationType<?, ?>> type) {
      return (MapDecorationRenderer<E>)RENDERERS.computeIfAbsent((MLMapDecorationType<?, ?>)type.value(), t -> createRenderer(type));
   }

   public static <T extends MLMapDecoration> boolean render(
      T decoration,
      PoseStack matrixStack,
      VertexConsumer vertexBuilder,
      MultiBufferSource buffer,
      @Nullable MapItemSavedData mapData,
      boolean isOnFrame,
      int light,
      int index
   ) {
      MapDecorationRenderer<T> renderer = getRenderer(decoration);
      return renderer != null ? renderer.render(decoration, matrixStack, vertexBuilder, buffer, mapData, isOnFrame, light, index) : false;
   }
}
