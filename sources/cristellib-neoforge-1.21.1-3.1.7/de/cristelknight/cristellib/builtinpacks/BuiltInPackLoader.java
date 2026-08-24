package de.cristelknight.cristellib.builtinpacks;

import com.mojang.datafixers.util.Pair;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.config.simple.ConfigRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.CompositePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuiltInPackLoader {
   private static final List<BuiltInPack> PACK_LIST = new ArrayList<>();
   private static boolean frozen = false;

   public static void registerAlwaysOnPack(ResourceLocation path, Component displayName) {
      registerPack(path, displayName, () -> true);
   }

   public static void registerPack(ResourceLocation path, Component displayName, Supplier<Boolean> supplier) {
      Pair<PackResources, PackResources> packs = PlatformHelper.registerBuiltinResourcePack(path, displayName);
      if (packs != null) {
         PackResources server = (PackResources)packs.getFirst();
         PackResources client = (PackResources)packs.getSecond();
         if (server != null) {
            registerPack(server, displayName, supplier, PackType.SERVER_DATA);
         }

         if (client != null) {
            registerPack(client, displayName, supplier, PackType.CLIENT_RESOURCES);
         }
      }
   }

   public static void registerPack(PackResources packResource, Component displayName, Supplier<Boolean> supplier, PackType type) {
      if (frozen) {
         throw new RuntimeException(
            Constants.getWithPrefix(String.format("BuiltInPack Registry is already frozen. Cannot add Pack with id: %s", packResource.packId()))
         );
      } else {
         PACK_LIST.add(new BuiltInPack(packResource, displayName, supplier, type));
      }
   }

   public static List<String> getCustomIDs() {
      return PACK_LIST.stream().map(pack -> pack.packResource().packId()).filter(id -> !id.equals(Constants.CRISTEL_LIB_PACK_ID.toString())).toList();
   }

   public static void getPacks(Consumer<Pack> consumer, PackType type) {
      if (!frozen) {
         throw new RuntimeException(Constants.getWithPrefix("Tried to load Packs before the Registry phase is over!"));
      } else if (!PACK_LIST.isEmpty()) {
         BuiltInPackConfig config = ConfigRegistry.get(BuiltInPackConfig.class);

         for (BuiltInPack entry : PACK_LIST) {
            PackResources pack = entry.packResource();
            if (entry.type().equals(type) && !pack.getNamespaces(type).isEmpty() && entry.supplier().get() && !config.disabledPacks().contains(pack.packId())) {
               Pack profile = buildPack(entry, type);
               if (profile != null) {
                  consumer.accept(profile);
               }
            }
         }
      }
   }

   public static void registerEachPackAsSource(PackType type, Consumer<RepositorySource> sourceRegistrar) {
      if (!frozen) {
         throw new RuntimeException(Constants.getWithPrefix("Tried to load Packs before the Registry phase is over!"));
      } else if (!PACK_LIST.isEmpty()) {
         for (BuiltInPack entry : PACK_LIST) {
            PackResources pack = entry.packResource();
            if (entry.type().equals(type) && !pack.getNamespaces(type).isEmpty()) {
               sourceRegistrar.accept(consumer -> {
                  BuiltInPackConfig config = ConfigRegistry.get(BuiltInPackConfig.class);
                  if (entry.supplier().get() && !config.disabledPacks().contains(pack.packId())) {
                     Pack profile = buildPack(entry, type);
                     if (profile != null) {
                        consumer.accept(profile);
                     }
                  }
               });
            }
         }
      }
   }

   @Nullable
   private static Pack buildPack(BuiltInPack entry, PackType type) {
      final PackResources pack = entry.packResource();
      Component displayName = entry.displayName();
      PackLocationInfo metadata = new PackLocationInfo(pack.packId(), displayName, new BuiltinResourcePackSource(), pack.knownPackInfo());
      PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Position.TOP, false);
      Pack profile = Pack.readMetaAndCreate(
         metadata,
         new ResourcesSupplier() {
            @NotNull
            public PackResources openPrimary(PackLocationInfo var1) {
               return pack;
            }

            public PackResources openFull(PackLocationInfo packLocationInfo, Metadata metadatax) {
               if (metadatax.overlays().isEmpty()) {
                  return pack;
               } else {
                  List<PackResources> overlays = new ArrayList<>(metadatax.overlays().size());

                  for (String overlay : metadatax.overlays()) {
                     PackResources overlayPack = pack instanceof OverlayPack packWithOverlays
                        ? packWithOverlays.createOverlay(overlay)
                        : PlatformHelper.createOverlay(pack, overlay);
                     if (overlayPack != null) {
                        overlays.add(overlayPack);
                     }
                  }

                  return new CompositePackResources(pack, overlays);
               }
            }
         },
         type,
         selectionConfig
      );
      if (profile == null) {
         Constants.LOG.error("Pack Profile with display name: {} is null", displayName);
      }

      return profile;
   }

   public static void freeze() {
      frozen = true;
   }
}
