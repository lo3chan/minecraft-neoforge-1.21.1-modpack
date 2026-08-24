package net.mehvahdjukaar.amendments.client;

import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class WallLanternTextureGen {
   private WallLanternTextureGen() {
   }

   public static TextureImage generate(ResourceManager manager, ResourceLocation lanternTexture) throws Exception {
      TextureImage template = TextureImage.open(manager, Amendments.res("block/wall_lanterns/wall_lantern"));

      TextureImage var6;
      label105: {
         TextureImage fullLantern;
         try {
            try {
               label106: {
                  fullLantern = TextureImage.open(manager, lanternTexture);

                  try {
                     TextureImage sourceLantern = firstFrame(fullLantern);

                     label108: {
                        try {
                           Palette palette = extractMetalPalette(sourceLantern);
                           if (palette != null) {
                              var6 = Respriter.of(template).recolor(palette);
                              break label108;
                           }

                           Amendments.LOGGER
                              .warn("Could not extract a usable palette from lantern texture {}. Using the default wall mount texture", lanternTexture);
                        } catch (Throwable var10) {
                           if (sourceLantern != null) {
                              try {
                                 sourceLantern.close();
                              } catch (Throwable var9) {
                                 var10.addSuppressed(var9);
                              }
                           }

                           throw var10;
                        }

                        if (sourceLantern != null) {
                           sourceLantern.close();
                        }
                        break label106;
                     }

                     if (sourceLantern != null) {
                        sourceLantern.close();
                     }
                  } catch (Throwable var11) {
                     if (fullLantern != null) {
                        try {
                           fullLantern.close();
                        } catch (Throwable var8) {
                           var11.addSuppressed(var8);
                        }
                     }

                     throw var11;
                  }

                  if (fullLantern != null) {
                     fullLantern.close();
                  }
                  break label105;
               }

               if (fullLantern != null) {
                  fullLantern.close();
               }
            } catch (Exception var12) {
               Amendments.LOGGER.warn("Failed to generate wall mount texture for lantern {}. Using the default wall mount texture", lanternTexture, var12);
            }

            fullLantern = template.makeCopy();
         } catch (Throwable var13) {
            if (template != null) {
               try {
                  template.close();
               } catch (Throwable var7) {
                  var13.addSuppressed(var7);
               }
            }

            throw var13;
         }

         if (template != null) {
            template.close();
         }

         return fullLantern;
      }

      if (template != null) {
         template.close();
      }

      return var6;
   }

   @Nullable
   private static Palette extractMetalPalette(TextureImage source) {
      TextureImage mask = createMetalMask(source);

      Palette var6;
      label43: {
         try {
            Palette base = trySample(source, mask);
            if (base != null) {
               var6 = base;
               break label43;
            }

            var6 = trySample(source, null);
         } catch (Throwable var5) {
            if (mask != null) {
               try {
                  mask.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (mask != null) {
            mask.close();
         }

         return var6;
      }

      if (mask != null) {
         mask.close();
      }

      return var6;
   }

   @Nullable
   private static Palette trySample(TextureImage source, @Nullable TextureImage mask) {
      try {
         Palette p = Palette.fromImage(source, mask);
         return p.isEmpty() ? null : p;
      } catch (Exception var3) {
         return null;
      }
   }

   private static TextureImage firstFrame(TextureImage image) {
      int fw = image.frameWidth();
      int fh = image.frameHeight();
      TextureImage frame = TextureImage.createNew(fw, fh);

      for (int x = 0; x < fw; x++) {
         for (int y = 0; y < fh; y++) {
            frame.setPixel(x, y, image.getFramePixel(0, x, y));
         }
      }

      return frame;
   }

   private static TextureImage createMetalMask(TextureImage image) {
      TextureImage mask = TextureImage.createNew(image.imageWidth(), image.imageHeight(), image.getMcMeta());
      int baseStart = Math.round(image.frameHeight() * 0.75F);
      image.forEachPixel(pixel -> {
         if (pixel.frameY() < baseStart) {
            mask.setPixel(pixel.x(), pixel.y(), -1);
         }
      });
      return mask;
   }

   public static ResourceLocation getSupportTextureLocation(LanternRegistry.LanternType type) {
      ResourceLocation reg = type.getId();
      if (type.isVanilla() && reg.getPath().equals("lantern")) {
         return Amendments.res("block/wall_lanterns/wall_lantern");
      } else if (reg.getNamespace().equals("skinnedlanterns")) {
         return Amendments.res("block/wall_lanterns/wall_lantern");
      } else {
         if (reg.getNamespace().equals("caverns_and_chasms")) {
            ResourceLocation copper = copperMountTexture(reg.getPath());
            if (copper != null) {
               return copper;
            }
         }

         if (reg.getNamespace().equals("supp_squared") && reg.getPath().equals("crimson_lantern")) {
            return Amendments.res("block/wall_lanterns/wall_lantern_gold");
         } else {
            String namespace = !reg.getNamespace().equals("minecraft") && !reg.getNamespace().equals("amendments") ? reg.getNamespace() + "/" : "";
            return Amendments.res("block/wall_lanterns/" + namespace + reg.getPath());
         }
      }
   }

   @Nullable
   private static ResourceLocation copperMountTexture(String path) {
      String p = path.startsWith("waxed_") ? path.substring("waxed_".length()) : path;

      return switch (p) {
         case "copper_lantern" -> Amendments.res("block/wall_lanterns/wall_lantern_copper");
         case "exposed_copper_lantern" -> Amendments.res("block/wall_lanterns/wall_lantern_copper_exposed");
         case "weathered_copper_lantern" -> Amendments.res("block/wall_lanterns/wall_lantern_copper_weathered");
         case "oxidized_copper_lantern" -> Amendments.res("block/wall_lanterns/wall_lantern_copper_oxidized");
         case "cupric_lantern" -> Amendments.res("block/wall_lanterns/wall_lantern");
         default -> null;
      };
   }

   public static ResourceLocation getLanternTextureLocation(ResourceManager manager, LanternRegistry.LanternType type) {
      try {
         return RPUtils.findFirstBlockTextureLocation(manager, type.lantern);
      } catch (Exception var3) {
         return ResourceLocation.withDefaultNamespace("block/lantern");
      }
   }
}
