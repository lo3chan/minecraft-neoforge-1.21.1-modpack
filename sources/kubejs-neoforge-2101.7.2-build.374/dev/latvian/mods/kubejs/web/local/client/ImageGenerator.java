package dev.latvian.mods.kubejs.web.local.client;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.latvian.apps.tinyserver.content.ResponseContent;
import dev.latvian.apps.tinyserver.http.response.HTTPPayload;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UUIDWrapper;
import dev.latvian.mods.kubejs.util.CachedComponentObject;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public class ImageGenerator {
   public static final ItemTransform ROTATED_BLOCK_TRANSFORM = new ItemTransform(
      new Vector3f(30.0F, 225.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.625F, 0.625F, 0.625F)
   );
   public static final ResourceLocation WILDCARD_TEXTURE = KubeJS.id("textures/misc/wildcard.png");
   public static final Int2ObjectMap<TextureTarget> FB_CACHE = new Int2ObjectArrayMap();

   public static TextureTarget getCanvas(int size) {
      TextureTarget target = (TextureTarget)FB_CACHE.get(size);
      if (target == null) {
         target = new TextureTarget(size, size, true, Minecraft.ON_OSX);
         target.setClearColor(0.54F, 0.54F, 0.54F, 0.0F);
         FB_CACHE.put(size, target);
      }

      return target;
   }

   private static ImageGenerator.CachedImage renderCanvas(
      KJSHTTPRequest req, int canvasSize, int imageSize, String dir, @Nullable ByteBuf cacheBuf, boolean wildcard, Consumer<ImageGenerator.RenderImage> render
   ) {
      int size = imageSize > 0 ? imageSize : req.variable("size").asInt();
      if (size >= 1 && size <= 1024) {
         if (req.query().containsKey("uncached")) {
            cacheBuf = null;
         }

         if (cacheBuf != null) {
            cacheBuf.writeBoolean(wildcard);
         }

         String cacheUUIDStr = cacheBuf == null ? null : UUIDWrapper.toString(UUID.nameUUIDFromBytes(cacheBuf.array()));
         Path cachePath = cacheUUIDStr == null
            ? null
            : KubeJSPaths.dir(KubeJSPaths.LOCAL.resolve("cache/web/img/" + dir + "/" + cacheUUIDStr.substring(0, 2)))
               .resolve(cacheUUIDStr + "_" + size + ".png");
         if (cachePath != null && Files.exists(cachePath)) {
            String pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
            return new ImageGenerator.CachedImage(
               HTTPResponse.ok().content(cachePath).header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr
            );
         } else {
            byte[] bytes = req.supplyInMainThread(() -> {
               TextureTarget target = getCanvas(size);
               Minecraft mc = Minecraft.getInstance();
               BufferSource bufferSource = mc.renderBuffers().bufferSource();
               target.clear(Minecraft.ON_OSX);
               target.bindWrite(true);
               RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, canvasSize, canvasSize, 0.0F, -1000.0F, 1000.0F), VertexSorting.ORTHOGRAPHIC_Z);
               Matrix4fStack view = RenderSystem.getModelViewStack();
               view.pushMatrix();
               view.translation(0.0F, 0.0F, 0.0F);
               RenderSystem.applyModelViewMatrix();
               GuiGraphics graphics = new GuiGraphics(mc, bufferSource);
               render.accept(new ImageGenerator.RenderImage(mc, graphics, size));
               if (wildcard) {
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  graphics.blit(WILDCARD_TEXTURE, 0, 0, 300, 0.0F, 0.0F, 16, 16, 16, 16);
               }

               graphics.flush();
               target.bindRead();
               RenderSystem.bindTexture(target.getColorTextureId());

               B y;
               try {
                  NativeImage image = new NativeImage(size, size, false);

                  try {
                     image.downloadTexture(0, false);
                     image.flipY();

                     for (int yx = 0; yx < size; yx++) {
                        for (int x = 0; x < size; x++) {
                           int color = image.getPixelRGBA(x, yx);
                           int a = color >> 24 & 0xFF;
                           if (a == 0) {
                              image.setPixelRGBA(x, yx, 0);
                           } else if (a < 255) {
                              image.setPixelRGBA(x, yx, color & 16777215 | 0xFF000000);
                           }
                        }
                     }

                     y = image.asByteArray();
                  } catch (Throwable var20) {
                     try {
                        image.close();
                     } catch (Throwable var19) {
                        var20.addSuppressed(var19);
                     }

                     throw var20;
                  }

                  image.close();
                  return (byte[])y;
               } catch (Exception var21) {
                  var21.printStackTrace();
                  y = null;
               } finally {
                  target.unbindRead();
                  target.unbindWrite();
                  view.popMatrix();
                  RenderSystem.applyModelViewMatrix();
               }

               return (byte[])y;
            });
            if (cachePath != null) {
               try {
                  Files.write(cachePath, bytes);
               } catch (Exception var12) {
               }

               String pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
               return new ImageGenerator.CachedImage(
                  HTTPResponse.ok().content(bytes, "image/png").header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr
               );
            } else {
               return new ImageGenerator.CachedImage(HTTPResponse.ok().content(bytes, "image/png"), null);
            }
         }
      } else {
         return new ImageGenerator.CachedImage(HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]"), null);
      }
   }

   private static ImageGenerator.CachedImage renderAnimated(KJSHTTPRequest req, String dir, @Nullable ByteBuf cacheBuf, List<ImageGenerator.CachedImage> images) throws Exception {
      int size = req.variable("size").asInt();
      if (size >= 1 && size <= 1024) {
         if (req.query().containsKey("uncached")) {
            cacheBuf = null;
         }

         String cacheUUIDStr = cacheBuf == null ? null : UUIDWrapper.toString(UUID.nameUUIDFromBytes(cacheBuf.array()));
         Path cachePath = cacheUUIDStr == null
            ? null
            : KubeJSPaths.dir(KubeJSPaths.LOCAL.resolve("cache/web/img/" + dir + "/" + cacheUUIDStr.substring(0, 2)))
               .resolve(cacheUUIDStr + "_" + size + ".gif");
         if (cachePath != null && Files.exists(cachePath)) {
            String pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
            return new ImageGenerator.CachedImage(
               HTTPResponse.ok().content(cachePath).header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr
            );
         } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.start(outputStream);
            encoder.setSize(size, size);
            encoder.setBackground(Color.BLUE);
            encoder.setTransparent(Color.BLUE, false);
            encoder.setRepeat(0);
            encoder.setDelay(1000);
            HashSet<ImageGenerator.BodyKey> bodyKeys = new HashSet<>();
            req.runInMainThread(() -> {
               for (ImageGenerator.CachedImage image : images) {
                  try {
                     ImageGenerator.ContentGrabber content = new ImageGenerator.ContentGrabber(KubeJS.DISPLAY_NAME, req.startTime());
                     image.response().build(content);
                     if (content.body != null && bodyKeys.add(new ImageGenerator.BodyKey(content.body))) {
                        encoder.addFrame(ImageIO.read(new ByteArrayInputStream(content.body)));
                     }
                  } catch (Exception var7x) {
                  }
               }
            });
            encoder.finish();
            byte[] bytes = outputStream.toByteArray();
            if (cachePath != null) {
               try {
                  Files.write(cachePath, bytes);
               } catch (Exception var12) {
               }

               String pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
               return new ImageGenerator.CachedImage(
                  HTTPResponse.ok().content(bytes, "image/gif").header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr
               );
            } else {
               return new ImageGenerator.CachedImage(HTTPResponse.ok().content(bytes, "image/gif"), null);
            }
         }
      } else {
         return new ImageGenerator.CachedImage(HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]"), null);
      }
   }

   public static HTTPResponse renderAllItems(KJSHTTPRequest req) throws Exception {
      int size = req.variable("size").asInt();
      return size >= 1 && size <= 1024 ? HTTPResponse.noContent() : HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]");
   }

   public static HTTPResponse item(KJSHTTPRequest req) throws Exception {
      ItemStack stack = ((Item)BuiltInRegistries.ITEM.get(req.id())).getDefaultInstance();
      stack.applyComponents(req.components(req.registries().nbt()));
      return renderItem(req, 0, stack, req.query().containsKey("wildcard")).response();
   }

   public static ImageGenerator.CachedImage renderItem(KJSHTTPRequest req, int imageSize, ItemStack stack, boolean wildcard) {
      if (stack.isEmpty()) {
         return new ImageGenerator.CachedImage(HTTPStatus.NOT_FOUND, null);
      } else {
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
         CachedComponentObject.writeCacheKey(buf, stack.getItem(), DataComponentWrapper.visualPatch(stack.getComponentsPatch()));
         return renderCanvas(req, 16, imageSize, "item", buf, wildcard, render -> {
            render.graphics.renderFakeItem(stack, 0, 0, 0);
            render.graphics.renderItemDecorations(render.mc.font, stack, 0, 0);
         });
      }
   }

   public static HTTPResponse block(KJSHTTPRequest req) throws Exception {
      BlockState state = BlockWrapper.withProperties(((Block)BuiltInRegistries.BLOCK.get(req.id())).defaultBlockState(), req.query());
      return renderBlock(req, state, req.query().containsKey("wildcard")).response();
   }

   public static ImageGenerator.CachedImage renderBlock(KJSHTTPRequest req, BlockState state, boolean wildcard) {
      if (state.isEmpty()) {
         return new ImageGenerator.CachedImage(HTTPStatus.NOT_FOUND, null);
      } else {
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
         buf.writeUtf(state.kjs$getId());
         buf.writeVarInt(state.getBlock().getStateDefinition().getProperties().size());

         for (Property<?> p : state.getProperties()) {
            buf.writeUtf(p.getName());
            if (p instanceof BooleanProperty p1) {
               buf.writeBoolean((Boolean)state.getValue(p1));
            } else if (p instanceof IntegerProperty p1) {
               buf.writeVarInt(Cast.<Integer>to(state.getValue(p1)));
            } else {
               buf.writeUtf(p.getName(Cast.to(state.getValue(p))));
            }
         }

         return renderCanvas(
            req,
            16,
            0,
            "block",
            buf,
            wildcard,
            render -> {
               BakedModel model = render.mc.getBlockRenderer().getBlockModel(state);
               PoseStack pose = render.graphics.pose();
               pose.pushPose();
               pose.translate(8.0F, 8.0F, 150.0F);
               pose.scale(16.0F, -16.0F, 16.0F);
               boolean flag = !model.usesBlockLight();
               if (flag) {
                  Lighting.setupForFlatItems();
               }

               ROTATED_BLOCK_TRANSFORM.apply(false, pose);
               pose.translate(-0.5F, -0.5F, -0.5F);

               for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(0L), ModelData.EMPTY)) {
                  render.mc
                     .getBlockRenderer()
                     .renderSingleBlock(state, pose, render.graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
               }

               try {
                  FluidState fluidState = state.getFluidState();
                  if (!fluidState.is(Fluids.EMPTY)) {
                     FakeClientWorld world = new FakeClientWorld(render.mc.level, state, Biomes.THE_VOID);
                     render.mc
                        .getBlockRenderer()
                        .renderLiquid(
                           BlockPos.ZERO,
                           world,
                           new MovedVertexConsumer(render.graphics.bufferSource().getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState)), pose.last()),
                           state,
                           fluidState
                        );
                  }
               } catch (Exception var7x) {
               }

               render.graphics.flush();
               if (flag) {
                  Lighting.setupFor3DItems();
               }

               render.graphics.pose().popPose();
            }
         );
      }
   }

   public static HTTPResponse fluid(KJSHTTPRequest req) throws Exception {
      FluidStack stack = new FluidStack((Fluid)BuiltInRegistries.FLUID.get(req.id()), 1000);
      stack.applyComponents(req.components(req.registries().nbt()));
      return renderFluid(req, stack, req.query().containsKey("wildcard")).response();
   }

   public static ImageGenerator.CachedImage renderFluid(KJSHTTPRequest req, FluidStack stack, boolean wildcard) {
      if (stack.isEmpty()) {
         return new ImageGenerator.CachedImage(HTTPStatus.NOT_FOUND, null);
      } else {
         IClientFluidTypeExtensions fluidInfo = IClientFluidTypeExtensions.of(stack.getFluid());
         ResourceLocation still = fluidInfo.getStillTexture(stack);
         int tint = fluidInfo.getTintColor(stack);
         int a = 255;
         int r = tint >> 16 & 0xFF;
         int g = tint >> 8 & 0xFF;
         int b = tint & 0xFF;
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
         CachedComponentObject.writeCacheKey(buf, stack.getFluid(), DataComponentWrapper.visualPatch(stack.getComponentsPatch()));
         return renderCanvas(req, 16, 0, "fluid", buf, wildcard, render -> {
            TextureAtlasSprite s = (TextureAtlasSprite)render.mc.kjs$getBlockTextureAtlas().apply(still);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            BufferBuilder builder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            Matrix4f m = render.graphics.pose().last().pose();
            builder.addVertex(m, 0.0F, 0.0F, 0.0F).setUv(s.getU0(), s.getV1()).setColor(r, g, b, a);
            builder.addVertex(m, 0.0F, 16.0F, 0.0F).setUv(s.getU0(), s.getV0()).setColor(r, g, b, a);
            builder.addVertex(m, 16.0F, 16.0F, 0.0F).setUv(s.getU1(), s.getV0()).setColor(r, g, b, a);
            builder.addVertex(m, 16.0F, 0.0F, 0.0F).setUv(s.getU1(), s.getV1()).setColor(r, g, b, a);
            BufferUploader.drawWithShader(builder.buildOrThrow());
         });
      }
   }

   public static HTTPResponse itemTag(KJSHTTPRequest req) throws Exception {
      Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(ItemTags.create(req.id()));
      if (tag.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
         ArrayList<ImageGenerator.CachedImage> list = new ArrayList<>();

         for (Holder<Item> holder : tag.get()) {
            buf.writeUtf(((Item)holder.value()).kjs$getId());
            list.add(renderItem(req, 0, ((Item)holder.value()).getDefaultInstance(), true));
         }

         return renderAnimated(req, "item_tag", buf, list).response();
      }
   }

   public static HTTPResponse blockTag(KJSHTTPRequest req) throws Exception {
      Optional<Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(BlockTags.create(req.id()));
      if (tag.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
         ArrayList<ImageGenerator.CachedImage> list = new ArrayList<>();

         for (Holder<Block> holder : tag.get()) {
            buf.writeUtf(((Block)holder.value()).kjs$getId());
            Item item = ((Block)holder.value()).asItem();
            if (item != Items.AIR) {
               list.add(renderItem(req, 0, item.getDefaultInstance(), true));
            } else {
               list.add(renderBlock(req, ((Block)holder.value()).defaultBlockState(), true));
            }
         }

         return renderAnimated(req, "block_tag", buf, list).response();
      }
   }

   public static HTTPResponse fluidTag(KJSHTTPRequest req) throws Exception {
      Optional<Named<Fluid>> tag = BuiltInRegistries.FLUID.getTag(FluidTags.create(req.id()));
      if (tag.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
         ArrayList<ImageGenerator.CachedImage> list = new ArrayList<>();

         for (Holder<Fluid> holder : tag.get()) {
            buf.writeUtf(((Fluid)holder.value()).kjs$getId());
            list.add(renderFluid(req, new FluidStack(holder, 1000), true));
         }

         return renderAnimated(req, "fluid_tag", buf, list).response();
      }
   }

   private record BodyKey(byte[] bytes) {
      @Override
      public int hashCode() {
         return Arrays.hashCode(this.bytes);
      }

      @Override
      public boolean equals(Object o) {
         if (this != o) {
            if (o instanceof ImageGenerator.BodyKey(byte[] var6)) {
               byte[] var4 = var6;
               if (Arrays.equals(this.bytes, var4)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public record CachedImage(HTTPResponse response, @Nullable String pathStr) {
   }

   private static class ContentGrabber extends HTTPPayload {
      private byte[] body = null;

      public ContentGrabber(String serverName, Instant serverTime) {
         super(serverName, serverTime);
      }

      public void setBody(ResponseContent body) {
         try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            body.write(out);
            this.body = out.toByteArray();
         } catch (Exception var3) {
         }
      }
   }

   private record RenderImage(Minecraft mc, GuiGraphics graphics, int size) {
   }
}
