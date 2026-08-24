package cc.cosmetica.cosmetica;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.api.Cosmetic;
import cc.cosmetica.core.api.CosmeticManager;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.CosmeticaModel;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.api.NametagConfig;
import cc.cosmetica.core.api.NoneCosmetics;
import cc.cosmetica.core.api.PlayerCosmetics;
import cc.cosmetica.core.api.CosmeticManager.Either;
import cc.cosmetica.core.api.texture.CosmeticaTexture.Builder;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.BlockModelManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import gg.cloaks.javaclient.model.AnimatedTextureCosmetic;
import gg.cloaks.javaclient.model.CosmeticaUser;
import gg.cloaks.javaclient.model.ExternalCape;
import gg.cloaks.javaclient.model.Icon;
import gg.cloaks.javaclient.model.Lore;
import gg.cloaks.javaclient.model.Outfit;
import gg.cloaks.javaclient.model.OutfitAccessory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class CacheCosmeticManager implements CosmeticManager {
   private final Path directory;
   private final Path outfitCache;
   private final CacheCosmeticManager.UserIO userIO;
   private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r);
      t.setName("Cache Cosmetic Manager");
      return t;
   });
   private Cosmetics cosmetics;

   public CacheCosmeticManager(Path directory, CacheCosmeticManager.UserIO userIO) {
      this.directory = directory;
      this.outfitCache = directory.resolve("outfit.json");
      this.userIO = userIO;
      this.load();
   }

   public boolean canManage(Either entity) {
      return entity.entity instanceof LocalPlayer && this.cosmetics != null;
   }

   public Cosmetics getCosmetics(Either entity) {
      return this.cosmetics;
   }

   private void load() {
      this.executor.submit(() -> {
         try (InputStream is = new BufferedInputStream(Files.newInputStream(this.outfitCache))) {
            Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Reading offline cache outfit json", new Object[0]);
            CosmeticaUser user = this.userIO.read(is);
            this.loadUser(user);
         } catch (NoSuchFileException var6) {
            Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "No cached player cosmetics for self yet.", new Object[0]);
         } catch (IOException var7) {
            Logging.getInstance().error("Failed to read cached player cosmetics", var7);
         }
      });
   }

   private void loadUser(CosmeticaUser user) {
      Minecraft.getInstance()
         .execute(
            () -> {
               Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Transforming offline outfit json to outfit", new Object[0]);
               Outfit outfit = user.getOutfit();
               Icon icon = user.getIcon();
               Lore lore = user.getLore();
               ImageCosmetic iconImage = icon == null ? NametagConfig.NO_ICON : ImageCosmetic.fromIcon(icon);
               NametagConfig nametag = new NametagConfig(
                  user.getPrefix() == null ? "" : user.getPrefix(), user.getSuffix() == null ? "" : user.getSuffix(), iconImage, false
               );
               NametagConfig loreNametag = lore == null
                  ? null
                  : new NametagConfig(
                     lore.getFormatted().replaceAll("&", "§"),
                     "",
                     lore.getIconUrl() == null
                        ? NametagConfig.NO_ICON
                        : new ImageCosmetic(
                           CosmeticaModel.getOrCreateCosmeticaImage(new Builder(lore.getIconUrl(), BlockModelManager.FALLBACK_TEXTURE)),
                           lore.getService(),
                           lore.getService(),
                           null,
                           lore.getIconUrl(),
                           0
                        ),
                     false
                  );
               List<Accessory> accessories = new ArrayList<>();
               String outfitName = null;
               String outfitId = null;
               ImageCosmetic cloak = null;
               ImageCosmetic elytra = null;
               if (outfit != null) {
                  outfitName = outfit.getName();
                  outfitId = outfit.getId();
                  AnimatedTextureCosmetic apiCloak = outfit.getCloak();
                  AnimatedTextureCosmetic apiElytra = outfit.getElytra();
                  ExternalCape externalCape = user.getExternalCape();
                  if (apiCloak != null) {
                     cloak = ImageCosmetic.fromAPI(apiCloak);
                  } else if (externalCape != null) {
                     cloak = ImageCosmetic.fromExternalCape(externalCape);
                  }

                  if (apiElytra != null) {
                     elytra = ImageCosmetic.fromAPI(apiElytra);
                  } else if (externalCape != null && externalCape.isHasElytra()) {
                     elytra = ImageCosmetic.fromExternalCape(externalCape);
                  }

                  for (OutfitAccessory accessory : outfit.getAccessories()) {
                     CosmeticaModel model = CosmeticaModel.getOrCreateModel(
                        accessory.getAccessory().getId(),
                        "textures",
                        CosmeticaModel.textureId(accessory.getAccessory().getTexture()),
                        () -> Files.newInputStream(this.directory.resolve(accessory.getAccessory().getId() + ".json")),
                        accessory.getAccessory().getTexture(),
                        accessory.getAccessory().getTicksPerFrame().intValue(),
                        accessory.getAccessory().getFrames().intValue()
                     );
                     List<BigDecimal> offset = accessory.getOffset();
                     accessories.add(
                        new Accessory(
                           accessory.getAccessory(),
                           Cosmetic.gameProfileOf(accessory.getAccessory().getCreator()),
                           accessory.getFlags() == -1 ? OptionalInt.empty() : OptionalInt.of(accessory.getFlags()),
                           accessory.isMirrored(),
                           model,
                           Accessory.attachmentTransform(
                              accessory.getAccessory().getAttachment(), offset.get(0).doubleValue(), offset.get(1).doubleValue(), offset.get(2).doubleValue()
                           )
                        )
                     );
                  }
               } else {
                  ExternalCape externalCapex = user.getExternalCape();
                  if (externalCapex != null) {
                     cloak = ImageCosmetic.fromExternalCape(externalCapex);
                  }

                  if (externalCapex != null && externalCapex.isHasElytra()) {
                     elytra = ImageCosmetic.fromExternalCape(externalCapex);
                  }
               }

               Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Loaded offline cosmetics cache.", new Object[0]);
               this.cosmetics = new PlayerCosmetics(cloak, elytra, accessories, outfitName, outfitId, nametag, loreNametag, user.isUpsideDown());
            }
         );
   }

   public void save(CosmeticaUser response) {
      Cosmetics loaded = (Cosmetics)SelfCosmeticManager.getCosmetics().orElse(NoneCosmetics.NONE);
      this.executor.submit(() -> {
         Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Caching player cosmetics for offline use", new Object[0]);

         try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(this.outfitCache))) {
            CosmeticaUser user = new CosmeticaUser();
            user.setActiveSettings(response.getActiveSettings());
            user.setExternalCape(response.getExternalCape());
            user.setIcon(response.getIcon());
            user.setLore(response.getLore());
            user.setSkin(response.getSkin());
            user.setUuid(response.getUuid());
            user.setOutfit(response.getOutfit());
            user.setUpsideDown(response.isUpsideDown());
            user.setModpackId(response.getModpackId());
            user.setPrefix(response.getPrefix());
            user.setSuffix(response.getSuffix());
            this.userIO.write(user, os);
            Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Cached player cosmetics", new Object[0]);
            this.loadUser(user);
            Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Updated loaded cache cosmetics", new Object[0]);
         } catch (IOException var14) {
            Logging.getInstance().error("Failed to cache player cosmetics", var14);
         }

         List<ResourceLocation> cachedImages = new ArrayList<>();
         loaded.getCloak().ifPresent(ic -> {
            cachedImages.add(ic.getImage().location);
            if (ic.getThumbnail().isPresent()) {
               cachedImages.add(BlockModelManager.getLocation("textures/" + CosmeticaModel.textureId((String)ic.getThumbnail().get())));
            }
         });
         loaded.getElytra().ifPresent(ic -> {
            cachedImages.add(ic.getImage().location);
            if (ic.getThumbnail().isPresent()) {
               cachedImages.add(BlockModelManager.getLocation("textures/" + CosmeticaModel.textureId((String)ic.getThumbnail().get())));
            }
         });
         loaded.getLore().ifPresent(ic -> {
            if (ic.getIcon() != NametagConfig.NO_ICON) {
               cachedImages.add(ic.getIcon().getImage().location);
               if (ic.getIcon().getThumbnail().isPresent()) {
                  cachedImages.add(BlockModelManager.getLocation("textures/" + CosmeticaModel.textureId((String)ic.getIcon().getThumbnail().get())));
               }
            }
         });

         for (Accessory accessory : loaded.getAccessories()) {
            cachedImages.add(BlockModelManager.getLocation("textures/" + CosmeticaModel.textureId(accessory.getJsonObject().getTexture())));
            if (accessory.getThumbnail().isPresent()) {
               cachedImages.add(BlockModelManager.getLocation("textures/" + CosmeticaModel.textureId((String)accessory.getThumbnail().get())));
            }
         }

         BlockModelManager.preserveImages(cachedImages);
         Outfit outfit = response.getOutfit();
         if (outfit != null) {
            for (OutfitAccessory oa : outfit.getAccessories()) {
               String modelURL = oa.getAccessory().getModel();
               Path output = this.directory.resolve(oa.getAccessory().getId() + ".json");
               CosmeticaAPI.downloadAsync(modelURL).thenAcceptAsync(model -> {
                  try {
                     Files.write(output, model.getBytes(StandardCharsets.UTF_8));
                  } catch (IOException var4x) {
                     Logging.getInstance().error("Failed to cache model from {}", var4x, new Object[]{modelURL});
                  }
               }, this.executor);
            }
         }

         try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.directory, "*.json")) {
            Instant oneHourAgo = Instant.now().minus(1L, ChronoUnit.HOURS);
            int count = 0;

            for (Path entry : stream) {
               if (Files.isRegularFile(entry) && !Files.isSameFile(entry, this.outfitCache)) {
                  BasicFileAttributes attributes = Files.readAttributes(entry, BasicFileAttributes.class);
                  if (attributes.lastModifiedTime().toInstant().isBefore(oneHourAgo)) {
                     Files.delete(entry);
                     count++;
                  }
               }
            }

            Logging.getInstance().debug(CosmeticaLogCategory.CACHE, "Deleted " + count + " old cached models", new Object[0]);
         } catch (IOException var16) {
            Logging.getInstance().error("Error clearing old cached models", var16);
         }
      });
   }

   public interface UserIO {
      CosmeticaUser read(InputStream var1) throws IOException;

      void write(CosmeticaUser var1, OutputStream var2) throws IOException;
   }
}
