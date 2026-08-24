package net.mehvahdjukaar.amendments.client;

import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.AmendmentsClient;
import net.mehvahdjukaar.amendments.common.CakeRegistry;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.mixins.SignRendererAccessor;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicClientResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.PaletteColor;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureCollager;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureOps;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ClientResourceGenerator extends DynamicClientResourceProvider {
   public ClientResourceGenerator() {
      super(Amendments.res("generated_pack"), ClientConfigs.DYNAMIC_ASSETS_GEN_MODE.get().toStrategy());
   }

   public boolean canUseExternalResourcePacks() {
      return PlatHelper.isDev() || ClientConfigs.TEXTURE_PACK_SUPPORT.get();
   }

   protected Collection<String> gatherSupportedNamespaces() {
      List<String> namespaces = new ArrayList<>();
      namespaces.add("minecraft");
      if (ClientConfigs.PIXEL_CONSISTENT_SIGNS.get()) {
         PlatHelper.getInstalledMods().forEach(namespaces::add);
      }

      return namespaces;
   }

   public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
      if (ClientConfigs.JUKEBOX_MODEL.get()) {
         executor.accept(this::generateJukeboxAssets);
      }

      if (CommonConfigs.DOUBLE_CAKES.get()) {
         executor.accept(this::generateDoubleCakesAssets);
      }

      if (CommonConfigs.WALL_LANTERN.get()) {
         executor.accept(this::generateWallLanternAssets);
      }

      if (ClientConfigs.SIGN_ATTACHMENT.get()) {
         executor.accept(this::generateHangingSignAssets);
      }

      if (ClientConfigs.PIXEL_CONSISTENT_SIGNS.get()) {
         executor.accept(this::generateSignTextures);
         if (CompatHandler.FARMERS_DELIGHT) {
            executor.accept(this::generateFdSignTextures);
         }

         executor.accept(this::generateSignBlockModels);
      }

      executor.accept(
         (manager, sink) -> {
            if (ClientConfigs.COLORED_ARROWS.get()) {
               sink.addItemModel(
                  ResourceLocation.withDefaultNamespace("crossbow_arrow"),
                  JsonParser.parseString(
                     "{\n    \"parent\": \"item/crossbow\",\n    \"textures\": {\n        \"layer0\": \"item/crossbow_arrow_base\",\n        \"layer1\": \"item/crossbow_arrow_tip\"\n    }\n}\n"
                  )
               );
            }

            if (ClientConfigs.JUKEBOX_MODEL.get()) {
               sink.addItemModel(ResourceLocation.withDefaultNamespace("jukebox"), JsonParser.parseString("{\n  \"parent\": \"amendments:block/jukebox\"\n}\n"));
               sink.addBlockState(
                  ResourceLocation.withDefaultNamespace("jukebox"),
                  JsonParser.parseString(
                     "{\n  \"variants\": {\n    \"has_record=true\": {\n      \"model\": \"amendments:block/jukebox_on\"\n    },\n    \"has_record=false\": {\n      \"model\": \"amendments:block/jukebox\"\n    }\n  }\n}\n"
                  )
               );
            }
         }
      );
   }

   private void generateSignTextures(ResourceManager manager, ResourceSink sink) {
      TextureCollager transformer = TextureCollager.builder(64, 32, 64, 16).copyFrom(0, 16, 8, 16).to(56, 0).build();

      try {
         TextureImage template = TextureImage.open(manager, Amendments.res("block/signs/template"));

         try {
            TextureImage mask = TextureImage.open(manager, Amendments.res("block/signs/mask"));

            try {
               Respriter respriter = Respriter.masked(template, mask);

               for (WoodType w : WoodTypeRegistry.INSTANCE.getValues()) {
                  Block sing = w.getBlockOfThis("sign");
                  if (sing != null) {
                     ResourceLocation blockLocation = Amendments.res("block/signs/" + w.getVariantId("sign"));
                     ResourceLocation signTextureLocation = findSignTexture(manager, w, sing, false);
                     if (signTextureLocation != null) {
                        sink.addTextureIfNotPresent(manager, blockLocation, () -> {
                           try {
                              TextureImage signTexture = TextureImage.open(manager, signTextureLocation);

                              TextureImage var16x;
                              try {
                                 TextureImage modPlankTexture = TextureImage.open(manager, RPUtils.findFirstBlockTextureLocation(manager, w.planks));

                                 try {
                                    List<Palette> palette = Palette.fromAnimatedImage(modPlankTexture);

                                    for (Palette p : palette) {
                                       p.remove(p.getLightest());
                                    }

                                    TextureImage newImage = respriter.recolorWithAnimation(palette, modPlankTexture.getMcMeta());
                                    transformer.apply(signTexture, newImage);
                                    var16x = newImage;
                                 } catch (Throwable var12x) {
                                    if (modPlankTexture != null) {
                                       try {
                                          modPlankTexture.close();
                                       } catch (Throwable var11x) {
                                          var12x.addSuppressed(var11x);
                                       }
                                    }

                                    throw var12x;
                                 }

                                 if (modPlankTexture != null) {
                                    modPlankTexture.close();
                                 }
                              } catch (Throwable var13x) {
                                 if (signTexture != null) {
                                    try {
                                       signTexture.close();
                                    } catch (Throwable var10x) {
                                       var13x.addSuppressed(var10x);
                                    }
                                 }

                                 throw var13x;
                              }

                              if (signTexture != null) {
                                 signTexture.close();
                              }

                              return var16x;
                           } catch (Exception var14x) {
                              throw new RuntimeException(var14x);
                           }
                        });
                     }
                  }
               }
            } catch (Throwable var14) {
               if (mask != null) {
                  try {
                     mask.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (mask != null) {
               mask.close();
            }
         } catch (Throwable var15) {
            if (template != null) {
               try {
                  template.close();
               } catch (Throwable var12) {
                  var15.addSuppressed(var12);
               }
            }

            throw var15;
         }

         if (template != null) {
            template.close();
         }
      } catch (Exception var16) {
         Amendments.LOGGER.warn("Failed to generate sign extension textures, ", var16);
      }
   }

   @Nullable
   private static ResourceLocation findSignTexture(ResourceManager manager, WoodType w, Block sing, boolean hanging) {
      net.minecraft.world.level.block.state.properties.WoodType vanilla = w.toVanilla();
      if (vanilla == null) {
         Amendments.LOGGER.error("Vanilla wood type for wood {} was null. This is a bug", w);
         return null;
      } else {
         Material signMaterial = hanging ? Sheets.getHangingSignMaterial(vanilla) : Sheets.getSignMaterial(vanilla);
         if (signMaterial == null) {
            try {
               BlockEntity be = ((EntityBlock)sing).newBlockEntity(BlockPos.ZERO, sing.defaultBlockState());
               if (Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be) instanceof SignRendererAccessor sr) {
                  signMaterial = sr.invokeGetSignMaterial(vanilla);
               }
            } catch (Exception var9) {
               Amendments.LOGGER.error("Failed to get sign material for wood (from block entity renderer) {}, ", w, var9);
            }
         }

         if (signMaterial == null) {
            ResourceLocation relativeLocation = w.getId().withPrefix("entity/signs/" + (hanging ? "hanging/" : ""));
            ResourceLocation id = ResType.TEXTURES.getPath(relativeLocation);
            if (manager.getResource(id).isPresent()) {
               return relativeLocation;
            } else {
               Amendments.LOGGER
                  .error(
                     "Sign material for wood {} was null. This is likely due to some mod calling Sheets.getSignMaterial too early or by some wood mod not registering their wood type properly by not adding it to the vanilla texture map. Sheets.getSignMaterial is NOT Nullable, i shouldn't even have this check.",
                     w
                  );
               return null;
            }
         } else {
            return signMaterial.texture();
         }
      }
   }

   private static String joinNonEmpty(String first, String second) {
      if (first.isEmpty()) {
         return second;
      } else {
         return second.isEmpty() ? first : first + "_" + second;
      }
   }

   private void generateFdSignTextures(ResourceManager manager, ResourceSink sink) {
      TextureCollager transformer = TextureCollager.builder(64, 32, 64, 16)
         .copyFrom(0, 16, 8, 16)
         .to(56, 0)
         .copyFrom(0, 0, 32, 11)
         .to(0, 0)
         .copyFrom(0, 12, 28, 2)
         .to(0, 9)
         .copyFrom(26, 2, 2, 14)
         .to(18, 2)
         .copyFrom(24, 7, 2, 10)
         .to(16, 4)
         .copyFrom(23, 2, 3, 3)
         .to(15, 2)
         .copyFrom(28, 2, 24, 12)
         .to(20, 2)
         .copyFrom(28, 12, 24, 2)
         .to(20, 9)
         .copyFrom(50, 2, 2, 8)
         .to(34, 2)
         .build();
      List<String> names = new ArrayList<>();
      Arrays.stream(DyeColor.values()).forEach(dx -> names.add(dx.getName()));
      names.add("");

      for (String d : names) {
         ResourceLocation texturePath = ResourceLocation.fromNamespaceAndPath("farmersdelight", joinNonEmpty("entity/signs/canvas", d));
         ResourceLocation blockTexturePath = Amendments.res("block/signs/farmersdelight/" + joinNonEmpty(d, "canvas_sign"));
         sink.addTextureIfNotPresent(manager, blockTexturePath, () -> {
            try {
               TextureImage vanillaTexture = TextureImage.open(manager, texturePath);

               TextureImage var5;
               try {
                  TextureImage newImg = TextureImage.createNew(64, 16);
                  transformer.apply(vanillaTexture, newImg);
                  var5 = newImg;
               } catch (Throwable var7x) {
                  if (vanillaTexture != null) {
                     try {
                        vanillaTexture.close();
                     } catch (Throwable var6x) {
                        var7x.addSuppressed(var6x);
                     }
                  }

                  throw var7x;
               }

               if (vanillaTexture != null) {
                  vanillaTexture.close();
               }

               return var5;
            } catch (Exception var8x) {
               throw new RuntimeException(var8x);
            }
         });
      }
   }

   private void generateSignBlockModels(ResourceManager manager, ResourceSink sink) {
      AmendmentsClient.SIGN_THAT_WE_RENDER_AS_BLOCKS.clear();
      StaticResource sign0 = StaticResource.getOrThrow(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("signs/sign_oak_0")));
      StaticResource sign1 = StaticResource.getOrThrow(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("signs/sign_oak_1")));
      StaticResource sign2 = StaticResource.getOrThrow(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("signs/sign_oak_2")));
      StaticResource sign3 = StaticResource.getOrThrow(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("signs/sign_oak_3")));
      StaticResource signWall = StaticResource.getOrThrow(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("signs/sign_oak_wall")));
      StaticResource blockState = StaticResource.getOrThrow(manager, ResType.BLOCKSTATES.getPath(Amendments.res("sign_oak")));
      StaticResource blockStateWall = StaticResource.getOrThrow(manager, ResType.BLOCKSTATES.getPath(Amendments.res("sign_oak_wall")));
      String blockStateText = new String(blockState.data, StandardCharsets.UTF_8);
      String blockStateWallText = new String(blockStateWall.data, StandardCharsets.UTF_8);

      for (WoodType w : WoodTypeRegistry.INSTANCE.getValues()) {
         Block sign = w.getBlockOfThis("sign");
         Block wallSign = w.getBlockOfThis("wall_sign");
         if (sign != null && wallSign != null) {
            String variantId = w.getVariantId("sign");
            sink.addSimilarJsonResource(manager, sign0, "sign_oak", variantId);
            sink.addSimilarJsonResource(manager, sign1, "sign_oak", variantId);
            sink.addSimilarJsonResource(manager, sign2, "sign_oak", variantId);
            sink.addSimilarJsonResource(manager, sign3, "sign_oak", variantId);
            sink.addSimilarJsonResource(manager, signWall, "sign_oak", variantId);
            sink.addBytes(Utils.getID(sign), blockStateText.replace("sign_oak", variantId).getBytes(), ResType.BLOCKSTATES);
            sink.addBytes(Utils.getID(wallSign), blockStateWallText.replace("sign_oak", variantId).getBytes(), ResType.BLOCKSTATES);
            AmendmentsClient.SIGN_THAT_WE_RENDER_AS_BLOCKS.add(sign);
            AmendmentsClient.SIGN_THAT_WE_RENDER_AS_BLOCKS.add(wallSign);
         }
      }

      List<String> names = new ArrayList<>();
      Arrays.stream(DyeColor.values()).forEach(d -> names.add(d.getName() + "_"));
      names.add("");
      if (CompatHandler.FARMERS_DELIGHT) {
         for (Block canvas : BlockScanner.getInstance().getFdSigns()) {
            ResourceLocation id = Utils.getID(canvas);
            Block canvasWall = (Block)BuiltInRegistries.BLOCK.getOptional(id.withPath(p -> p.replace("sign", "wall_sign"))).orElse(null);
            if (canvasWall != null) {
               ResourceLocation canvasWallId = Utils.getID(canvasWall);
               String variantId = "farmersdelight/" + id.getPath();
               sink.addSimilarJsonResource(manager, sign0, "sign_oak", variantId);
               sink.addSimilarJsonResource(manager, sign1, "sign_oak", variantId);
               sink.addSimilarJsonResource(manager, sign2, "sign_oak", variantId);
               sink.addSimilarJsonResource(manager, sign3, "sign_oak", variantId);
               sink.addSimilarJsonResource(manager, signWall, "sign_oak", variantId);
               sink.addBytes(id, blockStateText.replace("sign_oak", variantId).getBytes(), ResType.BLOCKSTATES);
               sink.addBytes(canvasWallId, blockStateWallText.replace("sign_oak", variantId).getBytes(), ResType.BLOCKSTATES);
               AmendmentsClient.SIGN_THAT_WE_RENDER_AS_BLOCKS.add(canvas);
               AmendmentsClient.SIGN_THAT_WE_RENDER_AS_BLOCKS.add(canvasWall);
            }
         }
      }
   }

   private void generateHangingSignAssets(ResourceManager manager, ResourceSink sink) {
      TextureCollager transformer = TextureCollager.builder(32, 64, 16, 16)
         .copyFrom(26, 0, 2, 4)
         .to(4, 0)
         .copyFrom(26, 8, 6, 8)
         .to(4, 4)
         .copyFrom(28, 24, 4, 8)
         .to(0, 4)
         .copyFrom(26, 20, 2, 4)
         .to(6, 0)
         .copyFrom(26, 28, 2, 8)
         .to(10, 4)
         .flippedX()
         .build();

      for (WoodType w : WoodTypeRegistry.INSTANCE.getValues()) {
         Block hangingSign = w.getBlockOfThis("hanging_sign");
         if (hangingSign != null) {
            ResourceLocation signTexturePath = findSignTexture(manager, w, hangingSign, true);
            if (signTexturePath != null) {
               try {
                  TextureImage vanillaTexture = TextureImage.open(manager, signTexturePath);

                  try {
                     TextureImage rotated = TextureOps.createRotated(vanillaTexture, Rotation.CLOCKWISE_90);

                     try {
                        TextureImage newIm = TextureOps.createScaled(vanillaTexture, 0.25F, 0.5F);

                        try {
                           newIm.clear();
                           transformer.apply(rotated, newIm);
                           sink.addTexture(Amendments.res("entity/signs/hanging/" + w.getVariantId("extension")), newIm);
                        } catch (Throwable var23) {
                           if (newIm != null) {
                              try {
                                 newIm.close();
                              } catch (Throwable var18) {
                                 var23.addSuppressed(var18);
                              }
                           }

                           throw var23;
                        }

                        if (newIm != null) {
                           newIm.close();
                        }
                     } catch (Throwable var24) {
                        if (rotated != null) {
                           try {
                              rotated.close();
                           } catch (Throwable var17) {
                              var24.addSuppressed(var17);
                           }
                        }

                        throw var24;
                     }

                     if (rotated != null) {
                        rotated.close();
                     }
                  } catch (Throwable var25) {
                     if (vanillaTexture != null) {
                        try {
                           vanillaTexture.close();
                        } catch (Throwable var16) {
                           var25.addSuppressed(var16);
                        }
                     }

                     throw var25;
                  }

                  if (vanillaTexture != null) {
                     vanillaTexture.close();
                  }
               } catch (Exception var26) {
                  Amendments.LOGGER.warn("Failed to generate hanging sign extension texture for {}, ", w, var26);
               }
            }
         }
      }

      if (CompatHandler.FARMERS_DELIGHT) {
         try {
            TextureImage vanillaTexture = TextureImage.open(manager, ResourceLocation.fromNamespaceAndPath("farmersdelight", "entity/signs/hanging/canvas"));

            try {
               TextureImage rotated = TextureOps.createRotated(vanillaTexture, Rotation.CLOCKWISE_90);

               try {
                  TextureImage newIm = TextureOps.createScaled(rotated, 0.5F, 0.25F);

                  try {
                     newIm.clear();
                     transformer.apply(rotated, newIm);
                     sink.addTexture(Amendments.res("entity/signs/hanging/farmersdelight/extension_canvas"), newIm);
                  } catch (Throwable var19) {
                     if (newIm != null) {
                        try {
                           newIm.close();
                        } catch (Throwable var15) {
                           var19.addSuppressed(var15);
                        }
                     }

                     throw var19;
                  }

                  if (newIm != null) {
                     newIm.close();
                  }
               } catch (Throwable var20) {
                  if (rotated != null) {
                     try {
                        rotated.close();
                     } catch (Throwable var14) {
                        var20.addSuppressed(var14);
                     }
                  }

                  throw var20;
               }

               if (rotated != null) {
                  rotated.close();
               }
            } catch (Throwable var21) {
               if (vanillaTexture != null) {
                  try {
                     vanillaTexture.close();
                  } catch (Throwable var13) {
                     var21.addSuppressed(var13);
                  }
               }

               throw var21;
            }

            if (vanillaTexture != null) {
               vanillaTexture.close();
            }
         } catch (Exception var22) {
            Amendments.LOGGER.warn("Failed to generate hanging sign extension texture for {}, ", "canvas sign", var22);
         }
      }
   }

   private void generateJukeboxAssets(ResourceManager manager, ResourceSink sink) {
      TextureCollager transformer = TextureCollager.builder(16, 16, 16, 16)
         .copyFrom(5, 6, 3, 2)
         .to(6, 6)
         .copyFrom(8, 6, 1, 1)
         .to(9, 7)
         .copyFrom(7, 7, 3, 2)
         .to(7, 8)
         .copyFrom(6, 8, 1, 1)
         .to(6, 8)
         .copyFrom(9, 6, 1, 1)
         .to(9, 6)
         .copyFrom(5, 8, 1, 1)
         .to(6, 9)
         .build();

      try {
         TextureImage fallback = TextureImage.open(manager, Amendments.res("block/music_discs/music_disc_generic"));

         try {
            TextureImage template = TextureImage.open(manager, Amendments.res("block/music_discs/music_disc_template"));

            try {
               TextureImage mask = TextureImage.open(manager, Amendments.res("block/music_discs/music_disc_mask"));

               try {
                  Respriter respriter = Respriter.of(template);

                  for (Entry<Item, Material> e : AmendmentsClient.getAllRecords().entrySet()) {
                     ResourceLocation texturePath = Amendments.res(e.getValue().texture().getPath());
                     sink.addTextureIfNotPresent(manager, texturePath, () -> {
                        try {
                           TextureImage vanillaTexture = TextureImage.open(manager, RPUtils.findFirstItemTextureLocation(manager, e.getKey()));

                           TextureImage var9x;
                           try {
                              Palette p = Palette.fromImage(vanillaTexture, mask);
                              amendJukeboxPalette(p);
                              TextureImage newImage = respriter.recolor(p);
                              transformer.apply(vanillaTexture, newImage);
                              if (newImage.getPixel(6, 6) == p.get(p.size() - 2).rgb().toInt()) {
                                 newImage.setPixel(6, 6, p.getLightest().value());
                                 newImage.setPixel(9, 9, p.getLightest().value());
                              }

                              var9x = newImage;
                           } catch (Throwable var11x) {
                              if (vanillaTexture != null) {
                                 try {
                                    vanillaTexture.close();
                                 } catch (Throwable var10x) {
                                    var11x.addSuppressed(var10x);
                                 }
                              }

                              throw var11x;
                           }

                           if (vanillaTexture != null) {
                              vanillaTexture.close();
                           }

                           return var9x;
                        } catch (Exception var12x) {
                           Amendments.LOGGER.warn("Failed to generate record item texture for {}. Using default generic texture", e.getKey());
                           return fallback.makeCopy();
                        }
                     });
                  }
               } catch (Throwable var14) {
                  if (mask != null) {
                     try {
                        mask.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (mask != null) {
                  mask.close();
               }
            } catch (Throwable var15) {
               if (template != null) {
                  try {
                     template.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
                  }
               }

               throw var15;
            }

            if (template != null) {
               template.close();
            }
         } catch (Throwable var16) {
            if (fallback != null) {
               try {
                  fallback.close();
               } catch (Throwable var11) {
                  var16.addSuppressed(var11);
               }
            }

            throw var16;
         }

         if (fallback != null) {
            fallback.close();
         }
      } catch (Exception var17) {
      }
   }

   public static void amendJukeboxPalette(Palette p) {
      float averLum = p.getAverageLuminanceStep();
      if (averLum > 0.06) {
         p.increaseInner();
      }

      PaletteColor darkest = p.getDarkest();
      PaletteColor beforeDarkest = p.get(1);
      if (beforeDarkest.luminance() - darkest.luminance() > averLum - 0.005) {
         p.remove(darkest);
         p.increaseDown();
      }
   }

   private void generateWallLanternAssets(ResourceManager manager, ResourceSink sink) {
      StaticResource blockState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Amendments.res("wall_lantern")));
      StaticResource[] models = Stream.of(
            "", "_1", "_2", "_3", "_4", "_5", "_template", "_1_template", "_2_template", "_3_template", "_4_template", "_5_template"
         )
         .map(s -> StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("wall_lantern" + s))))
         .toArray(StaticResource[]::new);

      for (LanternRegistry.LanternType type : LanternRegistry.INSTANCE.getValues()) {
         if (!type.isVanilla() || !type.getId().getPath().equals("lantern")) {
            Block wallBlock = type.getBlockOfThis("wall_lantern");
            if (wallBlock != null) {
               try {
                  ResourceLocation wlId = Utils.getID(wallBlock);
                  if (!manager.getResource(ResType.BLOCKSTATES.getPath(wlId)).isPresent()) {
                     ResourceLocation supportTexture = WallLanternTextureGen.getSupportTextureLocation(type);
                     ResourceLocation lanternTexture = WallLanternTextureGen.getLanternTextureLocation(manager, type);
                     sink.addTextureUnlessPresent(manager, supportTexture, () -> WallLanternTextureGen.generate(manager, lanternTexture));
                     String textureRef = supportTexture.toString();
                     String modelPrefix = "amendments:block/" + wlId.getPath();
                     Function<String, String> pathTransform = s -> s.replace("wall_lantern", wlId.getPath());

                     for (StaticResource m : models) {
                        if (m != null) {
                           sink.addSimilarJsonResource(
                              manager,
                              m,
                              s -> s.replace("amendments:block/wall_lanterns/wall_lantern", textureRef)
                                 .replace("\"amendments:block/wall_lantern\"", "\"" + modelPrefix + "\"")
                                 .replace("\"amendments:block/wall_lantern_", "\"" + modelPrefix + "_"),
                              pathTransform
                           );
                        }
                     }

                     if (blockState != null) {
                        sink.addSimilarJsonResource(
                           manager,
                           blockState,
                           s -> s.replace("\"amendments:block/wall_lantern\"", "\"" + modelPrefix + "\"")
                              .replace("\"amendments:block/wall_lantern_", "\"" + modelPrefix + "_"),
                           pathTransform
                        );
                     }
                  }
               } catch (Exception var18) {
                  Amendments.LOGGER.error("Failed to generate assets for wall lantern {}", type.getId(), var18);
               }
            }
         }
      }
   }

   private void generateDoubleCakesAssets(ResourceManager manager, ResourceSink sink) {
      StaticResource[] cakeModels = Stream.of("full", "slice1", "slice2", "slice3", "slice4", "slice5", "slice6")
         .map(s -> StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Amendments.res("double_cake/vanilla_" + s))))
         .toArray(StaticResource[]::new);
      StaticResource doubleCakeModelState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Amendments.res("double_cake")));

      for (CakeRegistry.CakeType t : CakeRegistry.INSTANCE.getValues()) {
         if (!t.isVanilla()) {
            try {
               ResourceLocation dcId = Utils.getID(t.getBlockOfThis("double_cake"));
               ResourceLocation top = RPUtils.findFirstBlockTextureLocation(manager, t.cake, s -> s.contains("top"));
               ResourceLocation side = RPUtils.findFirstBlockTextureLocation(manager, t.cake, s -> s.contains("side"));
               ResourceLocation bottom = RPUtils.findFirstBlockTextureLocation(manager, t.cake, s -> s.contains("bottom"));

               ResourceLocation inner;
               try {
                  inner = RPUtils.findFirstBlockTextureLocation(manager, t.cake, s -> s.contains("inner") || s.contains("cut") || s.contains("inside"));
               } catch (Exception var17) {
                  inner = top;
               }

               for (StaticResource m : cakeModels) {
                  ResourceLocation finalInner = inner;
                  sink.addSimilarJsonResource(
                     manager,
                     m,
                     s -> s.replace("amendments:block/double_cake", "")
                        .replace("minecraft:block/cake", "")
                        .replace("\"/", "\"amendments:block/double_cake/")
                        .replace("_top", top.toString())
                        .replace("_side", side.toString())
                        .replace("_inner", finalInner.toString())
                        .replace("_bottom", bottom.toString()),
                     s -> s.replace("vanilla", dcId.getPath())
                  );
               }

               sink.addSimilarJsonResource(
                  manager, doubleCakeModelState, s -> s.replace("vanilla", dcId.getPath()), s -> s.replace("double_cake", dcId.getPath())
               );
            } catch (Exception var18) {
               Amendments.LOGGER.error("Failed to generate model for double cake {},", t, var18);
            }
         }
      }
   }

   public void addDynamicTranslations(AfterLanguageLoadEvent languageEvent) {
      if (languageEvent.isDefault()) {
         languageEvent.addEntry("item.minecraft.lingering_potion.effect.empty", "Lingering Mixed Potion");
         languageEvent.addEntry("item.minecraft.splash_potion.effect.empty", "Splash Mixed Potion");
         languageEvent.addEntry("item.minecraft.potion.effect.empty", "Mixed Potion");
      }
   }
}
