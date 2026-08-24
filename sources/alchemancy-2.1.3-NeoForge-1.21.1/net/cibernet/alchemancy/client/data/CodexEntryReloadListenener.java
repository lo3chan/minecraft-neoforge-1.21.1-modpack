package net.cibernet.alchemancy.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CodexEntryReloadListenener implements ResourceManagerReloadListener {
   private static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final HashMap<Holder<Property>, CodexEntryReloadListenener.CodexEntry> ENTRIES = new HashMap<>();
   public static final CodexEntryReloadListenener INSTANCE = new CodexEntryReloadListenener();
   public static final String PATH = "alchemancy/codex_entries";

   public void onResourceManagerReload(ResourceManager resourceManager) {
      ENTRIES.clear();
      resourceManager.listResources("alchemancy/codex_entries", r -> r.getPath().endsWith(".json"))
         .forEach(
            (location, resource) -> {
               Optional<Reference<Property>> propertyHolder = ((Registry)AlchemancyProperties.REGISTRY.getRegistry().get())
                  .getHolder(
                     ResourceLocation.fromNamespaceAndPath(
                        location.getNamespace(), location.getPath().substring("alchemancy/codex_entries".length() + 1, location.getPath().lastIndexOf(".json"))
                     )
                  );
               if (!propertyHolder.isEmpty()) {
                  try {
                     InputStream inputStream = resource.open();
                     Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                     JsonObject json = ((JsonElement)GsonHelper.fromJson(GSON_INSTANCE, reader, JsonElement.class)).getAsJsonObject();
                     ENTRIES.put(
                        (Holder<Property>)propertyHolder.get(),
                        (CodexEntryReloadListenener.CodexEntry)((Pair)CodexEntryReloadListenener.CodexEntry.CODEC
                              .decode(JsonOps.INSTANCE, json)
                              .getPartialOrThrow())
                           .getFirst()
                     );
                  } catch (IndexOutOfBoundsException | IOException var6) {
                     throw new RuntimeException(var6);
                  }
               }
            }
         );
   }

   public static HashMap<Holder<Property>, CodexEntryReloadListenener.CodexEntry> getEntries() {
      return ENTRIES;
   }

   public record CodexEntry(Component flavor, List<PropertyFunction> functions, List<Holder<Item>> innates) {
      public static final Codec<CodexEntryReloadListenener.CodexEntry> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ComponentSerialization.CODEC.optionalFieldOf("flavor", Component.empty()).forGetter(CodexEntryReloadListenener.CodexEntry::flavor),
               PropertyFunction.CODEC.listOf().fieldOf("functions").forGetter(CodexEntryReloadListenener.CodexEntry::functions),
               ItemStack.ITEM_NON_AIR_CODEC.listOf().optionalFieldOf("innate", List.of()).forGetter(CodexEntryReloadListenener.CodexEntry::innates)
            )
            .apply(instance, CodexEntryReloadListenener.CodexEntry::new)
      );
   }
}
