package dev.latvian.mods.kubejs.web.local.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.apps.tinyserver.http.response.error.client.BadRequestError;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UUIDWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.CachedComponentObject;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.NameProvider;
import dev.latvian.mods.kubejs.web.JsonContent;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.LocalWebServerAPIRegistry;
import dev.latvian.mods.kubejs.web.LocalWebServerRegistry;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;

public class KubeJSClientWeb {
   private static final Lazy<CreativeModeTab> SEARCH_TAB = Lazy.of(() -> (CreativeModeTab)BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH));
   private static final Lazy<Map<Item, NameProvider<ItemStack>>> ITEM_NAME_PROVIDERS = Lazy.identityMap(
      map -> KubeJSPlugins.forEachPlugin(map::put, KubeJSPlugin::registerItemNameProviders)
   );

   public static Map<UUID, CachedComponentObject<Item, ItemStack>> createItemSearch(boolean useSearchTab) {
      LinkedHashMap<UUID, CachedComponentObject<Item, ItemStack>> map = new LinkedHashMap<>();
      if (useSearchTab) {
         for (ItemStack stack : SEARCH_TAB.get().getSearchTabDisplayItems()) {
            CachedComponentObject<Item, ItemStack> obj = CachedComponentObject.ofItemStack(stack, false);
            map.put(obj.cacheKey(), obj);
         }
      } else {
         for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR) {
               CachedComponentObject<Item, ItemStack> obj = CachedComponentObject.ofItemStack(item.getDefaultInstance(), false);
               map.put(obj.cacheKey(), obj);
            }
         }
      }

      return map;
   }

   public static Map<CachedComponentObject<Item, ItemStack>, UUID> createReverseItemSearch(Map<UUID, CachedComponentObject<Item, ItemStack>> original) {
      HashMap<CachedComponentObject<Item, ItemStack>, UUID> map = new HashMap<>();
      original.forEach((uuid, obj) -> map.put((CachedComponentObject<Item, ItemStack>)obj, uuid));
      return map;
   }

   public static void registerAPIs(LocalWebServerAPIRegistry registry) {
      registry.register(KubeJS.id("translate"), 1);
      registry.register(KubeJS.id("translate"), 1);
      registry.register(KubeJS.id("translate"), 1);
      registry.register(KubeJS.id("translate"), 1);
      registry.register(KubeJS.id("translate"), 1);
      registry.register(KubeJS.id("translate"), 1);
   }

   public static void register(LocalWebServerRegistry registry) {
      registry.get("/api/client/translate/{key}", KubeJSClientWeb::getTranslate);
      registry.get("/api/client/component-string/{json}", KubeJSClientWeb::getComponentString);
      registry.get("/api/client/search/items", KubeJSClientWeb::getSearchItems);
      registry.get("/api/client/search/blocks", KubeJSClientWeb::getSearchBlocks);
      registry.get("/api/client/search/fluids", KubeJSClientWeb::getSearchFluids);
      registry.get("/api/client/assets/list/<prefix>", KubeJSClientWeb::getAssetList);
      registry.get("/api/client/assets/get/{namespace}/<path>", KubeJSClientWeb::getAssetContent);
      registry.get("/img/screenshot", KubeJSClientWeb::getScreenshot);
      registry.get("/img/{size}/item/{namespace}/{path}", ImageGenerator::item);
      registry.get("/img/{size}/block/{namespace}/{path}", ImageGenerator::block);
      registry.get("/img/{size}/fluid/{namespace}/{path}", ImageGenerator::fluid);
      registry.get("/img/{size}/item-tag/{namespace}/{path}", ImageGenerator::itemTag);
      registry.get("/img/{size}/block-tag/{namespace}/{path}", ImageGenerator::blockTag);
      registry.get("/img/{size}/fluid-tag/{namespace}/{path}", ImageGenerator::fluidTag);
   }

   public static void registerWithAuth(LocalWebServerRegistry registry) {
      KubeJSWeb.addScriptTypeEndpoints(registry, ScriptType.CLIENT, KubeJSClientWeb::reloadClientScripts);
   }

   private static void reloadClientScripts() {
      KubeJS.getClientScriptManager().reload();
   }

   private static HTTPResponse getScreenshot(KJSHTTPRequest req) {
      byte[] bytes = req.supplyInMainThread(() -> {
         Minecraft mc = Minecraft.getInstance();
         mc.getMainRenderTarget().bindRead();

         B var2;
         try {
            NativeImage image = new NativeImage(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);

            try {
               image.downloadTexture(0, true);
               image.flipY();
               var2 = image.asByteArray();
            } catch (Throwable var10) {
               try {
                  image.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            image.close();
            return (byte[])var2;
         } catch (Exception var11) {
            var11.printStackTrace();
            var2 = null;
         } finally {
            mc.getMainRenderTarget().unbindRead();
         }

         return (byte[])var2;
      });
      return HTTPResponse.ok().content(bytes, "image/png");
   }

   private static HTTPResponse getTranslate(KJSHTTPRequest req) {
      return HTTPResponse.ok().text(I18n.get(req.variable("key").asString(), new Object[0]));
   }

   private static HTTPResponse getComponentString(KJSHTTPRequest req) {
      return HTTPResponse.ok()
         .text(((Component)((Pair)ComponentSerialization.FLAT_CODEC.decode(req.registries().java(), req.variable("json")).getOrThrow()).getFirst()).getString());
   }

   private static HTTPResponse getSearchItems(KJSHTTPRequest req) {
      ClientLevel level = Minecraft.getInstance().level;
      RegistryOps<JsonElement> jsonOps = level == null ? req.registries().json() : level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
      RegistryOps<Tag> nbtOps = level == null ? req.registries().nbt() : level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      JsonArray results = new JsonArray();
      Map<UUID, CachedComponentObject<Item, ItemStack>> itemSearch = createItemSearch(level != null);
      String search = req.query("search").asString().toLowerCase(Locale.ROOT);
      boolean includeTags = req.query("tags").asBoolean(false);
      int renderIcons = req.query("render-icons").asInt(0);
      if (renderIcons > 1024) {
         throw new BadRequestError("render-icons value too large, max 1024!");
      } else {
         String localPath = "local/kubejs/cache/web/img/item/";
         String iconPathRoot = renderIcons > 0 ? KubeJSPaths.GAMEDIR.toUri() + localPath : LocalWebServer.instance().url() + "/img/64/item/";
         if (renderIcons > 0) {
            ArrayList<CompletableFuture<Void>> futures = new ArrayList<>(itemSearch.size());
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

            try {
               for (CachedComponentObject<Item, ItemStack> item : itemSearch.values()) {
                  futures.add(
                     CompletableFuture.runAsync(
                        () -> item.iconPath().setValue(ImageGenerator.renderItem(req, renderIcons, item.stack(), false).pathStr()), executor
                     )
                  );
               }

               CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            } catch (Throwable var16) {
               if (executor != null) {
                  try {
                     executor.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }
               }

               throw var16;
            }

            if (executor != null) {
               executor.close();
            }
         }

         RegistryAccess registries = (RegistryAccess)(level != null
            ? level.registryAccess()
            : RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
         return HTTPResponse.ok()
            .content(
               JsonContent.object(
                  json -> {
                     json.addProperty("world", level != null);

                     for (CachedComponentObject<Item, ItemStack> itemx : itemSearch.values()) {
                        JsonObject o = new JsonObject();
                        NameProvider<ItemStack> nameProvider = ITEM_NAME_PROVIDERS.get().get(itemx.value());
                        Component nameProviderName = nameProvider == null ? null : nameProvider.getName(registries, itemx.stack());
                        String name = (nameProviderName == null ? itemx.stack().getHoverName() : nameProviderName).getString();
                        if (search.isEmpty() || name.toLowerCase(Locale.ROOT).contains(search)) {
                           o.addProperty("cache_key", UUIDWrapper.toString(itemx.cacheKey()));
                           o.addProperty("id", ((Item)itemx.value()).kjs$getId());
                           o.addProperty("name", name);
                           o.addProperty(
                              "icon_path",
                              renderIcons > 0
                                 ? ((String)itemx.iconPath().getValue()).substring(localPath.length())
                                 : itemx.stack().kjs$getWebIconURL(nbtOps, 64).fullString().substring(iconPathRoot.length())
                           );
                           Block block = itemx.value() instanceof BlockItem b ? b.getBlock() : Blocks.AIR;
                           if (block != Blocks.AIR) {
                              o.addProperty("block", block.kjs$getId());
                           }

                           DataComponentPatch patch = itemx.components();
                           if (!patch.isEmpty()) {
                              JsonObject p = new JsonObject();

                              try {
                                 for (Entry<DataComponentType<?>, Optional<?>> entry : patch.entrySet()) {
                                    String key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey()).toString();
                                    if (entry.getValue().isEmpty()) {
                                       p.add(key, JsonNull.INSTANCE);
                                    } else if (entry.getKey().codec() != null) {
                                       p.add(key, (JsonElement)entry.getKey().codec().encodeStart(jsonOps, Cast.to(entry.getValue().get())).getOrThrow());
                                    }
                                 }

                                 o.add("components", p);
                              } catch (Exception var24) {
                                 var24.printStackTrace();
                              }
                           }

                           if (includeTags) {
                              JsonArray tags = new JsonArray();

                              for (TagKey<Item> t : ((Item)itemx.value()).builtInRegistryHolder().tags().toList()) {
                                 tags.add(t.location().toString());
                              }

                              if (!tags.isEmpty()) {
                                 o.add("tags", tags);
                              }
                           }

                           results.add(o);
                        }
                     }

                     json.addProperty("icon_path_root", iconPathRoot);
                     json.add("results", results);
                  }
               )
            );
      }
   }

   private static HTTPResponse getSearchBlocks(KJSHTTPRequest req) {
      boolean includeTags = req.query("tags").asBoolean(false);
      return HTTPResponse.ok().content(JsonContent.array(json -> {
         for (Block block : BuiltInRegistries.BLOCK) {
            JsonObject o = new JsonObject();
            o.addProperty("id", block.kjs$getId());
            o.addProperty("name", Component.translatable(block.getDescriptionId()).getString());
            if (includeTags) {
               JsonArray tags = new JsonArray();

               for (TagKey<Block> t : block.builtInRegistryHolder().tags().toList()) {
                  tags.add(t.location().toString());
               }

               if (!tags.isEmpty()) {
                  o.add("tags", tags);
               }
            }

            json.add(o);
         }
      }));
   }

   private static HTTPResponse getSearchFluids(KJSHTTPRequest req) {
      boolean includeTags = req.query("tags").asBoolean(false);
      return HTTPResponse.ok().content(JsonContent.array(json -> {
         for (Fluid fluid : BuiltInRegistries.FLUID) {
            JsonObject o = new JsonObject();
            o.addProperty("id", fluid.kjs$getId());
            o.addProperty("name", fluid.getFluidType().getDescription().getString());
            if (includeTags) {
               JsonArray tags = new JsonArray();

               for (TagKey<Fluid> t : fluid.builtInRegistryHolder().tags().toList()) {
                  tags.add(t.location().toString());
               }

               if (!tags.isEmpty()) {
                  o.add("tags", tags);
               }
            }

            json.add(o);
         }
      }));
   }

   private static HTTPResponse getAssetList(KJSHTTPRequest req) {
      String prefix = req.variable("prefix").asString();
      return (HTTPResponse)(prefix.isEmpty() ? HTTPStatus.BAD_REQUEST : HTTPResponse.ok().content(JsonContent.object(json -> {
         for (ResourceLocation id : Minecraft.getInstance().getResourceManager().listResources(prefix, idx -> true).keySet()) {
            JsonArray arr = (JsonArray)json.get(id.getNamespace());
            if (arr == null) {
               arr = new JsonArray();
               json.add(id.getNamespace(), arr);
            }

            arr.add(id.getPath().substring(prefix.length() + 1));
         }
      })));
   }

   private static HTTPResponse getAssetContent(KJSHTTPRequest req) throws Exception {
      ResourceLocation id = req.id();
      Optional<Resource> asset = Minecraft.getInstance().getResourceManager().getResource(id);
      if (asset.isEmpty()) {
         return HTTPStatus.NOT_FOUND;
      } else {
         HTTPResponse var4;
         try (InputStream in = asset.get().open()) {
            if (id.getPath().endsWith(".png")) {
               return HTTPResponse.ok().content(in.readAllBytes(), "image/png");
            }

            if (!id.getPath().endsWith(".json") && !id.getPath().endsWith(".mcmeta")) {
               if (id.getPath().endsWith(".ogg")) {
                  return HTTPResponse.ok().content(in.readAllBytes(), "audio/ogg");
               }

               return HTTPResponse.ok().content(in.readAllBytes(), "text/plain");
            }

            var4 = HTTPResponse.ok().content(in.readAllBytes(), "application/json; charset=utf-8");
         }

         return var4;
      }
   }
}
