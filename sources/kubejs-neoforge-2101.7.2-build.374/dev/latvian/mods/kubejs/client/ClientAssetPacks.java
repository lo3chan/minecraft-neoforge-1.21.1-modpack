package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.generator.KubeResourceGenerator;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ClientEvents;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import dev.latvian.mods.kubejs.script.data.KubeFileResourcePack;
import dev.latvian.mods.kubejs.script.data.VirtualAssetPack;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.loading.FMLLoader;

public class ClientAssetPacks {
   public static final ClientAssetPacks INSTANCE = new ClientAssetPacks();
   public final VirtualAssetPack internalAssetPack = new VirtualAssetPack(GeneratedDataStage.INTERNAL, () -> RegistryAccessContainer.BUILTIN);
   public final Map<GeneratedDataStage, VirtualAssetPack> virtualPacks = GeneratedDataStage.forScripts(
      stage -> new VirtualAssetPack(stage, () -> RegistryAccessContainer.BUILTIN)
   );

   public List<PackResources> inject(List<PackResources> original) {
      try {
         return this.inject0(original);
      } catch (Throwable var3) {
         ConsoleJS.CLIENT.error("Error while generating client assets", var3);
         return original;
      }
   }

   private List<PackResources> inject0(List<PackResources> original) {
      ArrayList<PackResources> packs = new ArrayList<>(original);
      ArrayList<PackResources> filePacks = new ArrayList<>();
      KubeFileResourcePack.scanAndLoad(KubeJSPaths.ASSETS, filePacks);
      filePacks.sort((p1, p2) -> p1.packId().compareToIgnoreCase(p2.packId()));
      filePacks.add(new KubeFileResourcePack(PackType.CLIENT_RESOURCES));
      int beforeModsIndex = KubeFileResourcePack.findBeforeModsIndex(packs);
      int afterModsIndex = KubeFileResourcePack.findAfterModsIndex(packs);
      packs.add(beforeModsIndex, this.virtualPacks.get(GeneratedDataStage.BEFORE_MODS));
      packs.add(afterModsIndex, this.internalAssetPack);
      packs.add(afterModsIndex + 1, this.virtualPacks.get(GeneratedDataStage.AFTER_MODS));
      packs.addAll(afterModsIndex + 2, filePacks);
      packs.add(this.virtualPacks.get(GeneratedDataStage.LAST));
      this.internalAssetPack.reset();

      for (BuilderBase<?> builder : RegistryObjectStorage.ALL_BUILDERS) {
         builder.generateAssets(this.internalAssetPack);
      }

      KubeJSPlugins.forEachPlugin(this.internalAssetPack, KubeJSPlugin::generateAssets);
      HashMap<LangKubeEvent.Key, String> langMap = new HashMap<>();
      HashMap<String, LangKubeEvent> langEvents = new HashMap<>();
      LangKubeEvent enUsLangEvent = langEvents.computeIfAbsent("en_us", s -> new LangKubeEvent(s, langMap));

      for (BuilderBase<?> builder : RegistryObjectStorage.ALL_BUILDERS) {
         builder.generateLang(enUsLangEvent);
      }

      KubeJSPlugins.forEachPlugin(enUsLangEvent, KubeJSPlugin::generateLang);
      ClientEvents.GENERATE_ASSETS.post(ScriptType.CLIENT, GeneratedDataStage.AFTER_MODS, this.virtualPacks.get(GeneratedDataStage.AFTER_MODS));

      for (String lang : ClientEvents.LANG.findUniqueExtraIds(ScriptType.CLIENT)) {
         String l = String.valueOf(lang);
         if (LangKubeEvent.PATTERN.matcher(l).matches()) {
            ClientEvents.LANG.post(ScriptType.CLIENT, l, langEvents.computeIfAbsent(l, k -> new LangKubeEvent(k, langMap)));
         } else {
            ConsoleJS.CLIENT.error("Invalid language key: " + l);
         }
      }

      try {
         for (Path dir : Files.list(KubeJSPaths.ASSETS).filter(x$0 -> Files.isDirectory(x$0)).toList()) {
            String ns = dir.getFileName().toString();
            Path langDir = dir.resolve("lang");
            if (Files.exists(langDir) && Files.isDirectory(langDir)) {
               for (Path path : Files.list(langDir).filter(x$0 -> Files.isRegularFile(x$0)).filter(Files::isReadable).toList()) {
                  String fileName = path.getFileName().toString();
                  if (fileName.endsWith(".json")) {
                     try (BufferedReader reader = Files.newBufferedReader(path)) {
                        JsonObject json = (JsonObject)JsonUtils.GSON.fromJson(reader, JsonObject.class);
                        String langx = fileName.substring(0, fileName.length() - 5);

                        for (Entry<String, JsonElement> entry : json.entrySet()) {
                           langMap.put(new LangKubeEvent.Key(ns, langx, entry.getKey()), entry.getValue().getAsString());
                        }
                     } catch (Exception var23) {
                        var23.printStackTrace();
                     }
                  }
               }
            }
         }
      } catch (Exception var24) {
         var24.printStackTrace();
      }

      HashMap<String, Map<String, JsonObject>> finalMap = new HashMap<>();

      for (Entry<LangKubeEvent.Key, String> entry : langMap.entrySet()) {
         Map<String, JsonObject> ns = finalMap.computeIfAbsent(entry.getKey().namespace(), s -> new HashMap<>());
         JsonObject langx = ns.computeIfAbsent(entry.getKey().lang(), s -> new JsonObject());
         langx.addProperty(entry.getKey().key(), entry.getValue());
      }

      for (Entry<String, Map<String, JsonObject>> e1 : finalMap.entrySet()) {
         for (Entry<String, JsonObject> e2 : e1.getValue().entrySet()) {
            this.internalAssetPack.json(ResourceLocation.parse(e1.getKey() + ":lang/" + e2.getKey()), (JsonElement)e2.getValue());
         }
      }

      for (VirtualAssetPack pack : this.virtualPacks.values()) {
         pack.reset();
         if (ClientEvents.GENERATE_ASSETS.hasListeners(pack.stage)) {
            ClientEvents.GENERATE_ASSETS.post(ScriptType.CLIENT, pack.stage, pack);
         }
      }

      for (PackResources packx : packs) {
         if (packx instanceof KubeResourceGenerator generator) {
            generator.flush();
         }
      }

      if (!FMLLoader.isProduction()) {
         KubeJS.LOGGER
            .info("Loaded {} asset packs: {}", packs.size(), packs.stream().<CharSequence>map(PackResources::packId).collect(Collectors.joining(", ")));
      }

      return packs;
   }
}
