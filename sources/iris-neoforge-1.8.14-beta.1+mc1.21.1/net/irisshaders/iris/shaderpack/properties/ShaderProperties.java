package net.irisshaders.iris.shaderpack.properties;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.AlphaTestFunction;
import net.irisshaders.iris.gl.blending.BlendMode;
import net.irisshaders.iris.gl.blending.BlendModeFunction;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendInformation;
import net.irisshaders.iris.gl.buffer.ShaderStorageInfo;
import net.irisshaders.iris.gl.framebuffer.ViewportData;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.PixelFormat;
import net.irisshaders.iris.gl.texture.PixelType;
import net.irisshaders.iris.gl.texture.TextureDefinition;
import net.irisshaders.iris.gl.texture.TextureScaleOverride;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shaderpack.ImageInformation;
import net.irisshaders.iris.shaderpack.option.OrderBackedProperties;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.preprocessor.PropertiesPreprocessor;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;

public class ShaderProperties {
   final CustomUniforms.Builder customUniforms = new CustomUniforms.Builder();
   private final Map<String, List<String>> profiles = new LinkedHashMap<>();
   private final Map<String, List<String>> subScreenOptions = new HashMap<>();
   private final Map<String, Integer> subScreenColumnCount = new HashMap<>();
   private final Object2ObjectMap<String, AlphaTest> alphaTestOverrides = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, ViewportData> viewportScaleOverrides = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, TextureScaleOverride> textureScaleOverrides = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, BlendModeOverride> blendModeOverrides = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, IndirectPointer> indirectPointers = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, ArrayList<BufferBlendInformation>> bufferBlendOverrides = new Object2ObjectOpenHashMap();
   private final EnumMap<TextureStage, Object2ObjectMap<String, TextureDefinition>> customTextures = new EnumMap<>(TextureStage.class);
   private final Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> customTexturePatching = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, TextureDefinition> irisCustomTextures = new Object2ObjectOpenHashMap();
   private final List<ImageInformation> irisCustomImages = new ArrayList<>();
   private final Int2ObjectArrayMap<ShaderStorageInfo> bufferObjects = new Int2ObjectArrayMap();
   private final Object2ObjectMap<String, Object2BooleanMap<String>> explicitFlips = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<String, String> conditionallyEnabledPrograms = new Object2ObjectOpenHashMap();
   private int customTexAmount;
   private CloudSetting cloudSetting = CloudSetting.DEFAULT;
   private CloudSetting dhCloudSetting = CloudSetting.DEFAULT;
   private OptionalBoolean weather = OptionalBoolean.DEFAULT;
   private OptionalBoolean weatherParticles = OptionalBoolean.DEFAULT;
   private OptionalBoolean oldHandLight = OptionalBoolean.DEFAULT;
   private OptionalBoolean dynamicHandLight = OptionalBoolean.DEFAULT;
   private OptionalBoolean supportsColorCorrection = OptionalBoolean.DEFAULT;
   private OptionalBoolean oldLighting = OptionalBoolean.DEFAULT;
   private OptionalBoolean shadowTerrain = OptionalBoolean.DEFAULT;
   private OptionalBoolean shadowTranslucent = OptionalBoolean.DEFAULT;
   private OptionalBoolean shadowEntities = OptionalBoolean.DEFAULT;
   private OptionalBoolean shadowPlayer = OptionalBoolean.DEFAULT;
   private OptionalBoolean shadowBlockEntities = OptionalBoolean.DEFAULT;
   private OptionalBoolean shadowLightBlockEntities = OptionalBoolean.DEFAULT;
   private OptionalBoolean underwaterOverlay = OptionalBoolean.DEFAULT;
   private OptionalBoolean sun = OptionalBoolean.DEFAULT;
   private OptionalBoolean moon = OptionalBoolean.DEFAULT;
   private OptionalBoolean stars = OptionalBoolean.DEFAULT;
   private OptionalBoolean sky = OptionalBoolean.DEFAULT;
   private OptionalBoolean vignette = OptionalBoolean.DEFAULT;
   private OptionalBoolean backFaceSolid = OptionalBoolean.DEFAULT;
   private OptionalBoolean backFaceCutout = OptionalBoolean.DEFAULT;
   private OptionalBoolean backFaceCutoutMipped = OptionalBoolean.DEFAULT;
   private OptionalBoolean backFaceTranslucent = OptionalBoolean.DEFAULT;
   private OptionalBoolean rainDepth = OptionalBoolean.DEFAULT;
   private OptionalBoolean concurrentCompute = OptionalBoolean.DEFAULT;
   private OptionalBoolean beaconBeamDepth = OptionalBoolean.DEFAULT;
   private OptionalBoolean separateAo = OptionalBoolean.DEFAULT;
   private OptionalBoolean voxelizeLightBlocks = OptionalBoolean.DEFAULT;
   private OptionalBoolean separateEntityDraws = OptionalBoolean.DEFAULT;
   private OptionalBoolean skipAllRendering = OptionalBoolean.DEFAULT;
   private OptionalBoolean frustumCulling = OptionalBoolean.DEFAULT;
   private OptionalBoolean occlusionCulling = OptionalBoolean.DEFAULT;
   private ShadowCullState shadowCulling = ShadowCullState.DEFAULT;
   private OptionalBoolean shadowEnabled = OptionalBoolean.DEFAULT;
   private OptionalBoolean dhShadowEnabled = OptionalBoolean.DEFAULT;
   private ParticleRenderingSettings particleRenderingSettings = ParticleRenderingSettings.UNSET;
   private OptionalBoolean prepareBeforeShadow = OptionalBoolean.DEFAULT;
   private List<String> sliderOptions = new ArrayList<>();
   private List<String> mainScreenOptions = null;
   private Integer mainScreenColumnCount = null;
   private int fallbackTex = 0;
   private String noiseTexturePath = null;
   private List<String> requiredFeatureFlags = new ArrayList<>();
   private List<String> optionalFeatureFlags = new ArrayList<>();

   private ShaderProperties() {
   }

   public ShaderProperties(String contents, ShaderPackOptions shaderPackOptions, Iterable<StringPair> environmentDefines) {
      String preprocessedContents = PropertiesPreprocessor.preprocessSource(contents, shaderPackOptions, environmentDefines);
      if (Iris.getIrisConfig().areDebugOptionsEnabled()) {
         try {
            Files.writeString(IrisPlatformHelpers.getInstance().getGameDir().resolve("preprocessed.properties"), preprocessedContents);
            Files.writeString(IrisPlatformHelpers.getInstance().getGameDir().resolve("original.properties"), contents);
         } catch (IOException var9) {
            throw new RuntimeException(var9);
         }
      }

      Properties preprocessed = new OrderBackedProperties();
      Properties original = new OrderBackedProperties();

      try {
         preprocessed.load(new StringReader(preprocessedContents));
         original.load(new StringReader(contents));
      } catch (IOException var8) {
         Iris.logger.error("Error loading shaders.properties!", var8);
      }

      preprocessed.forEach(
         (keyObject, valueObject) -> {
            String key = (String)keyObject;
            String value = (String)valueObject;
            if ("texture.noise".equals(key)) {
               this.noiseTexturePath = value;
            } else {
               if ("clouds".equals(key)) {
                  switch (value) {
                     case "off":
                        this.cloudSetting = CloudSetting.OFF;
                        break;
                     case "fast":
                        this.cloudSetting = CloudSetting.FAST;
                        break;
                     case "fancy":
                        this.cloudSetting = CloudSetting.FANCY;
                        break;
                     case null:
                     default:
                        Iris.logger.error("Unrecognized clouds setting: " + value);
                  }
               }

               if ("dhClouds".equals(key)) {
                  if ("off".equals(value)) {
                     this.dhCloudSetting = CloudSetting.OFF;
                  } else if (!"on".equals(value) && !"fancy".equals(value)) {
                     Iris.logger.error("Unrecognized DH clouds setting (need off, on): " + value);
                  } else {
                     this.dhCloudSetting = CloudSetting.FANCY;
                  }
               }

               if ("shadow.culling".equals(key)) {
                  switch (value) {
                     case "false":
                        this.shadowCulling = ShadowCullState.DISTANCE;
                        break;
                     case "true":
                        this.shadowCulling = ShadowCullState.ADVANCED;
                        break;
                     case "reversed":
                        this.shadowCulling = ShadowCullState.REVERSED;
                        break;
                     case null:
                     default:
                        Iris.logger.error("Unrecognized shadow culling setting: " + value);
                  }
               }

               handleBooleanDirective(key, value, "oldHandLight", bool -> this.oldHandLight = bool);
               handleBooleanDirective(key, value, "dynamicHandLight", bool -> this.dynamicHandLight = bool);
               handleBooleanDirective(key, value, "oldLighting", bool -> this.oldLighting = bool);
               handleBooleanDirective(key, value, "shadowTerrain", bool -> this.shadowTerrain = bool);
               handleBooleanDirective(key, value, "shadowTranslucent", bool -> this.shadowTranslucent = bool);
               handleBooleanDirective(key, value, "shadowEntities", bool -> this.shadowEntities = bool);
               handleBooleanDirective(key, value, "shadowPlayer", bool -> this.shadowPlayer = bool);
               handleBooleanDirective(key, value, "shadowBlockEntities", bool -> this.shadowBlockEntities = bool);
               handleBooleanDirective(key, value, "shadowLightBlockEntities", bool -> this.shadowLightBlockEntities = bool);
               handleBooleanDirective(key, value, "underwaterOverlay", bool -> this.underwaterOverlay = bool);
               handleBooleanDirective(key, value, "sun", bool -> this.sun = bool);
               handleBooleanDirective(key, value, "moon", bool -> this.moon = bool);
               handleBooleanDirective(key, value, "stars", bool -> this.stars = bool);
               handleBooleanDirective(key, value, "sky", bool -> this.sky = bool);
               handleBooleanDirective(key, value, "vignette", bool -> this.vignette = bool);
               handleBooleanDirective(key, value, "backFace.solid", bool -> this.backFaceSolid = bool);
               handleBooleanDirective(key, value, "backFace.cutout", bool -> this.backFaceCutout = bool);
               handleBooleanDirective(key, value, "backFace.cutoutMipped", bool -> this.backFaceCutoutMipped = bool);
               handleBooleanDirective(key, value, "backFace.translucent", bool -> this.backFaceTranslucent = bool);
               handleBooleanDirective(key, value, "rain.depth", bool -> this.rainDepth = bool);
               handleBooleanDirective(key, value, "allowConcurrentCompute", bool -> this.concurrentCompute = bool);
               handleBooleanDirective(key, value, "beacon.beam.depth", bool -> this.beaconBeamDepth = bool);
               handleBooleanDirective(key, value, "separateAo", bool -> this.separateAo = bool);
               handleBooleanDirective(key, value, "voxelizeLightBlocks", bool -> this.voxelizeLightBlocks = bool);
               handleBooleanDirective(key, value, "separateEntityDraws", bool -> {
                  this.separateEntityDraws = bool;
                  this.particleRenderingSettings = ParticleRenderingSettings.MIXED;
               });
               handleBooleanDirective(key, value, "frustum.culling", bool -> this.frustumCulling = bool);
               handleBooleanDirective(key, value, "occlusion.culling", bool -> this.occlusionCulling = bool);
               handleBooleanDirective(key, value, "shadow.enabled", bool -> this.shadowEnabled = bool);
               handleBooleanDirective(key, value, "skipAllRendering", bool -> this.skipAllRendering = bool);
               handleBooleanDirective(key, value, "dhShadow.enabled", bool -> this.dhShadowEnabled = bool);
               handleBooleanDirective(key, value, "particles.before.deferred", bool -> {
                  if (bool.orElse(false) && this.particleRenderingSettings == ParticleRenderingSettings.UNSET) {
                     this.particleRenderingSettings = ParticleRenderingSettings.BEFORE;
                  }
               });
               handleBooleanDirective(key, value, "prepareBeforeShadow", bool -> this.prepareBeforeShadow = bool);
               handleBooleanDirective(key, value, "supportsColorCorrection", bool -> this.supportsColorCorrection = bool);
               handleIntDirective(key, value, "fallbackTex", bool -> this.fallbackTex = bool);
               if (key.startsWith("particles.ordering")) {
                  this.particleRenderingSettings = ParticleRenderingSettings.fromString(value.trim().toUpperCase(Locale.US));
               }

               handlePassDirective("scale.", key, value, pass -> {
                  float offsetX = 0.0F;
                  float offsetY = 0.0F;
                  String[] partsx = value.split(" ");

                  float scale;
                  try {
                     scale = Float.parseFloat(partsx[0]);
                     if (partsx.length > 1) {
                        offsetX = Float.parseFloat(partsx[1]);
                        offsetY = Float.parseFloat(partsx[2]);
                     }
                  } catch (ArrayIndexOutOfBoundsException | NumberFormatException var8x) {
                     Iris.logger.error("Unable to parse scale directive for " + pass + ": " + value, var8x);
                     return;
                  }

                  this.viewportScaleOverrides.put(pass, new ViewportData(scale, offsetX, offsetY));
               });
               if ("weather".equals(key)) {
                  String[] parts = value.split(" ");
                  this.weather = parts[0].equals("true") ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
                  if (parts.length > 1) {
                     this.weatherParticles = parts[1].equals("true") ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
                  }
               }

               handlePassDirective("size.buffer.", key, value, pass -> {
                  String[] partsx = value.split(" ");
                  if (partsx.length != 2) {
                     Iris.logger.error("Unable to parse size.buffer directive for " + pass + ": " + value);
                  } else {
                     this.textureScaleOverrides.put(pass, new TextureScaleOverride(partsx[0], partsx[1]));
                  }
               });
               handlePassDirective("alphaTest.", key, value, pass -> {
                  if (!"off".equals(value) && !"false".equals(value)) {
                     String[] partsx = value.split(" ");
                     if (partsx.length > 2) {
                        Iris.logger.warn("Weird alpha test directive for " + pass + " contains more parts than we expected: " + value);
                     } else if (partsx.length < 2) {
                        Iris.logger.error("Invalid alpha test directive for " + pass + ": " + value);
                        return;
                     }

                     Optional<AlphaTestFunction> function = AlphaTestFunction.fromString(partsx[0]);
                     if (function.isEmpty()) {
                        Iris.logger.error("Unable to parse alpha test directive for " + pass + ", unknown alpha test function " + partsx[0] + ": " + value);
                     } else {
                        float reference;
                        try {
                           reference = Float.parseFloat(partsx[1]);
                        } catch (NumberFormatException var7) {
                           Iris.logger.error("Unable to parse alpha test directive for " + pass + ": " + value, var7);
                           return;
                        }

                        this.alphaTestOverrides.put(pass, new AlphaTest(function.get(), reference));
                     }
                  } else {
                     this.alphaTestOverrides.put(pass, AlphaTest.ALWAYS);
                  }
               });
               handlePassDirective(
                  "blend.",
                  key,
                  value,
                  pass -> {
                     if (pass.contains(".")) {
                        if (!IrisRenderSystem.supportsBufferBlending()) {
                           throw new RuntimeException("Buffer blending is not supported on this platform, however it was attempted to be used!");
                        } else {
                           String[] partsx = pass.split("\\.");
                           int index = PackRenderTargetDirectives.LEGACY_RENDER_TARGETS.indexOf(partsx[1]);
                           if (index == -1 && partsx[1].startsWith("colortex")) {
                              String id = partsx[1].substring("colortex".length());

                              try {
                                 index = Integer.parseInt(id);
                              } catch (NumberFormatException var12) {
                                 throw new RuntimeException("Failed to parse buffer blend!", var12);
                              }
                           }

                           if (index == -1) {
                              throw new RuntimeException("Failed to parse buffer blend! index = " + index);
                           } else if ("off".equals(value)) {
                              ((ArrayList)this.bufferBlendOverrides.computeIfAbsent(partsx[0], list -> new ArrayList()))
                                 .add(new BufferBlendInformation(index, null));
                           } else {
                              String[] modeArray = value.split(" ");
                              int[] modes = new int[modeArray.length];
                              int i = 0;

                              for (String modeName : modeArray) {
                                 modes[i] = BlendModeFunction.fromString(modeName).get().getGlId();
                                 i++;
                              }

                              ((ArrayList)this.bufferBlendOverrides.computeIfAbsent(partsx[0], list -> new ArrayList()))
                                 .add(new BufferBlendInformation(index, new BlendMode(modes[0], modes[1], modes[2], modes[3])));
                           }
                        }
                     } else if ("off".equals(value)) {
                        this.blendModeOverrides.put(pass, BlendModeOverride.OFF);
                     } else {
                        String[] modeArray = value.split(" ");
                        int[] modes = new int[modeArray.length];
                        int i = 0;

                        for (String modeName : modeArray) {
                           modes[i] = BlendModeFunction.fromString(modeName).get().getGlId();
                           i++;
                        }

                        this.blendModeOverrides.put(pass, new BlendModeOverride(new BlendMode(modes[0], modes[1], modes[2], modes[3])));
                     }
                  }
               );
               handlePassDirective("indirect.", key, value, pass -> {
                  try {
                     String[] locations = value.split(" ");
                     this.indirectPointers.put(pass, new IndirectPointer(Integer.parseInt(locations[0]), Long.parseLong(locations[1])));
                  } catch (ArrayIndexOutOfBoundsException | NumberFormatException var4x) {
                     Iris.logger.fatal("Failed to parse indirect command for " + pass + "! " + value);
                  }
               });
               handleProgramEnabledDirective("program.", key, value, program -> this.conditionallyEnabledPrograms.put(program, value));
               handlePassDirective("bufferObject.", key, value, index -> {
                  String[] partsx = value.split(" ");
                  if (partsx.length <= 2) {
                     int trueIndex;
                     long trueSize;
                     try {
                        trueIndex = Integer.parseInt(index);
                        trueSize = Long.parseLong(partsx[0]);
                     } catch (NumberFormatException var12) {
                        Iris.logger.error("Number format exception parsing SSBO index/size!", var12);
                        return;
                     }

                     String name = null;
                     if (partsx.length > 1) {
                        name = partsx[1];
                     }

                     if (trueIndex > 8) {
                        Iris.logger.fatal("SSBO's cannot use buffer numbers higher than 8, they're reserved!");
                        return;
                     }

                     if (trueSize < 1L) {
                        return;
                     }

                     this.bufferObjects.put(trueIndex, new ShaderStorageInfo(trueSize, false, 0.0F, 0.0F, name));
                  } else {
                     boolean isRelative;
                     float scaleX;
                     float scaleY;
                     int trueIndexx;
                     long trueSizex;
                     try {
                        trueIndexx = Integer.parseInt(index);
                        trueSizex = Long.parseLong(partsx[0]);
                        isRelative = Boolean.parseBoolean(partsx[1]);
                        scaleX = Float.parseFloat(partsx[2]);
                        scaleY = Float.parseFloat(partsx[3]);
                     } catch (ArrayIndexOutOfBoundsException | NumberFormatException var11) {
                        Iris.logger.error("Number format exception parsing SSBO index/size, or not correct format!", var11);
                        return;
                     }

                     if (trueIndexx > 8) {
                        Iris.logger.fatal("SSBO's cannot use buffer numbers higher than 8, they're reserved!");
                        return;
                     }

                     if (trueSizex < 1L) {
                        return;
                     }

                     this.bufferObjects.put(trueIndexx, new ShaderStorageInfo(trueSizex, isRelative, scaleX, scaleY, null));
                  }
               });
               handleTwoArgDirective(
                  "texture.",
                  key,
                  value,
                  (stageName, samplerName) -> {
                     String[] partsx = value.split(" ");
                     samplerName = samplerName.split("\\.")[0];
                     Optional<TextureStage> optionalTextureStage = TextureStage.parse(stageName);
                     if (optionalTextureStage.isEmpty()) {
                        Iris.logger.warn("Unknown texture stage \"" + stageName + "\", ignoring custom texture directive for " + key);
                     } else {
                        TextureStage stage = optionalTextureStage.get();
                        if (partsx.length > 1) {
                           String newSamplerName = "customtex" + this.customTexAmount;
                           this.customTexAmount++;
                           TextureType type = null;
                           if (partsx.length == 6) {
                              type = TextureType.TEXTURE_1D;
                              this.irisCustomTextures
                                 .put(
                                    newSamplerName,
                                    new TextureDefinition.RawDefinition(
                                       partsx[0],
                                       TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT)),
                                       InternalTextureFormat.fromString(partsx[2]).orElseThrow(IllegalArgumentException::new),
                                       Integer.parseInt(partsx[3]),
                                       0,
                                       0,
                                       PixelFormat.fromString(partsx[4]).orElseThrow(IllegalArgumentException::new),
                                       PixelType.fromString(partsx[5]).orElseThrow(IllegalArgumentException::new)
                                    )
                                 );
                           } else if (partsx.length == 7) {
                              type = TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT));
                              this.irisCustomTextures
                                 .put(
                                    newSamplerName,
                                    new TextureDefinition.RawDefinition(
                                       partsx[0],
                                       TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT)),
                                       InternalTextureFormat.fromString(partsx[2]).orElseThrow(IllegalArgumentException::new),
                                       Integer.parseInt(partsx[3]),
                                       Integer.parseInt(partsx[4]),
                                       0,
                                       PixelFormat.fromString(partsx[5]).orElseThrow(IllegalArgumentException::new),
                                       PixelType.fromString(partsx[6]).orElseThrow(IllegalArgumentException::new)
                                    )
                                 );
                           } else if (partsx.length == 8) {
                              type = TextureType.TEXTURE_3D;
                              this.irisCustomTextures
                                 .put(
                                    newSamplerName,
                                    new TextureDefinition.RawDefinition(
                                       partsx[0],
                                       TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT)),
                                       InternalTextureFormat.fromString(partsx[2]).orElseThrow(IllegalArgumentException::new),
                                       Integer.parseInt(partsx[3]),
                                       Integer.parseInt(partsx[4]),
                                       Integer.parseInt(partsx[5]),
                                       PixelFormat.fromString(partsx[6]).orElseThrow(IllegalArgumentException::new),
                                       PixelType.fromString(partsx[7]).orElseThrow(IllegalArgumentException::new)
                                    )
                                 );
                           } else {
                              Iris.logger.warn("Unknown texture directive for " + key + ": " + value);
                           }

                           this.customTexturePatching.put(new Tri<>(samplerName, type, stage), newSamplerName);
                        } else {
                           this.customTextures
                              .computeIfAbsent(stage, _stage -> new Object2ObjectOpenHashMap())
                              .put(samplerName, new TextureDefinition.PNGDefinition(value));
                        }
                     }
                  }
               );
               handlePassDirective(
                  "customTexture.",
                  key,
                  value,
                  samplerName -> {
                     String[] partsx = value.split(" ");
                     if (partsx.length > 1) {
                        if (partsx.length == 6) {
                           this.irisCustomTextures
                              .put(
                                 samplerName,
                                 new TextureDefinition.RawDefinition(
                                    partsx[0],
                                    TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT)),
                                    InternalTextureFormat.fromString(partsx[2]).orElseThrow(IllegalArgumentException::new),
                                    Integer.parseInt(partsx[3]),
                                    0,
                                    0,
                                    PixelFormat.fromString(partsx[4]).orElseThrow(IllegalArgumentException::new),
                                    PixelType.fromString(partsx[5]).orElseThrow(IllegalArgumentException::new)
                                 )
                              );
                        } else if (partsx.length == 7) {
                           this.irisCustomTextures
                              .put(
                                 samplerName,
                                 new TextureDefinition.RawDefinition(
                                    partsx[0],
                                    TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT)),
                                    InternalTextureFormat.fromString(partsx[2]).orElseThrow(IllegalArgumentException::new),
                                    Integer.parseInt(partsx[3]),
                                    Integer.parseInt(partsx[4]),
                                    0,
                                    PixelFormat.fromString(partsx[5]).orElseThrow(IllegalArgumentException::new),
                                    PixelType.fromString(partsx[6]).orElseThrow(IllegalArgumentException::new)
                                 )
                              );
                        } else if (partsx.length == 8) {
                           this.irisCustomTextures
                              .put(
                                 samplerName,
                                 new TextureDefinition.RawDefinition(
                                    partsx[0],
                                    TextureType.valueOf(partsx[1].toUpperCase(Locale.ROOT)),
                                    InternalTextureFormat.fromString(partsx[2]).orElseThrow(IllegalArgumentException::new),
                                    Integer.parseInt(partsx[3]),
                                    Integer.parseInt(partsx[4]),
                                    Integer.parseInt(partsx[5]),
                                    PixelFormat.fromString(partsx[6]).orElseThrow(IllegalArgumentException::new),
                                    PixelType.fromString(partsx[7]).orElseThrow(IllegalArgumentException::new)
                                 )
                              );
                        } else {
                           Iris.logger.warn("Unknown texture directive for " + key + ": " + value);
                        }
                     } else {
                        this.irisCustomTextures.put(samplerName, new TextureDefinition.PNGDefinition(value));
                     }
                  }
               );
               handlePassDirective(
                  "image.",
                  key,
                  value,
                  imageName -> {
                     String[] partsx = value.split(" ");
                     String key2 = key.substring(6);
                     if (this.irisCustomImages.size() > 15) {
                        Iris.logger.error("Only up to 16 images are allowed, but tried to add another image! " + key);
                     } else {
                        String samplerName = partsx[0];
                        if (samplerName.equals("none")) {
                           samplerName = null;
                        }

                        PixelFormat format = PixelFormat.fromString(partsx[1]).orElse(null);
                        InternalTextureFormat internalFormat = InternalTextureFormat.fromString(partsx[2]).orElse(null);
                        PixelType pixelType = PixelType.fromString(partsx[3]).orElse(null);
                        if (format == null || internalFormat == null || pixelType == null) {
                           Iris.logger
                              .error("Image " + key2 + " is invalid! Format: " + format + " Internal format: " + internalFormat + " Pixel type: " + pixelType);
                        }

                        boolean clear = Boolean.parseBoolean(partsx[4]);
                        boolean relative = Boolean.parseBoolean(partsx[5]);
                        ImageInformation image;
                        if (relative) {
                           float relativeWidth = Float.parseFloat(partsx[6]);
                           float relativeHeight = Float.parseFloat(partsx[7]);
                           image = new ImageInformation(
                              key2, samplerName, TextureType.TEXTURE_2D, format, internalFormat, pixelType, 0, 0, 0, clear, true, relativeWidth, relativeHeight
                           );
                        } else {
                           int height;
                           int depth;
                           TextureType type;
                           int width;
                           if (partsx.length == 7) {
                              type = TextureType.TEXTURE_1D;
                              width = Integer.parseInt(partsx[6]);
                              height = 0;
                              depth = 0;
                           } else if (partsx.length == 8) {
                              type = TextureType.TEXTURE_2D;
                              width = Integer.parseInt(partsx[6]);
                              height = Integer.parseInt(partsx[7]);
                              depth = 0;
                           } else {
                              if (partsx.length != 9) {
                                 Iris.logger.error("Unknown image type! " + key2 + " = " + value);
                                 return;
                              }

                              type = TextureType.TEXTURE_3D;
                              width = Integer.parseInt(partsx[6]);
                              height = Integer.parseInt(partsx[7]);
                              depth = Integer.parseInt(partsx[8]);
                           }

                           image = new ImageInformation(
                              key2, samplerName, type, format, internalFormat, pixelType, width, height, depth, clear, false, 0.0F, 0.0F
                           );
                        }

                        this.irisCustomImages.add(image);
                     }
                  }
               );
               handleTwoArgDirective(
                  "flip.",
                  key,
                  value,
                  (pass, buffer) -> handleBooleanValue(
                     key,
                     value,
                     shouldFlip -> ((Object2BooleanMap)this.explicitFlips.computeIfAbsent(pass, _pass -> new Object2BooleanOpenHashMap()))
                        .put(buffer, shouldFlip)
                  )
               );
               handlePassDirective("variable.", key, value, pass -> {
                  String[] partsx = pass.split("\\.");
                  if (partsx.length != 2) {
                     Iris.logger.warn("Custom variables should take the form of `variable.<type>.<name> = <expression>. Ignoring " + key);
                  } else {
                     this.customUniforms.addVariable(partsx[0], partsx[1], value, false);
                  }
               });
               handlePassDirective("uniform.", key, value, pass -> {
                  String[] partsx = pass.split("\\.");
                  if (partsx.length != 2) {
                     Iris.logger.warn("Custom uniforms should take the form of `uniform.<type>.<name> = <expression>. Ignoring " + key);
                  } else {
                     this.customUniforms.addVariable(partsx[0], partsx[1], value, true);
                  }
               });
            }
         }
      );
      original.forEach((keyObject, valueObject) -> {
         String key = (String)keyObject;
         String value = (String)valueObject;
         handleWhitespacedListDirective(key, value, "iris.features.required", options -> this.requiredFeatureFlags = options);
         handleWhitespacedListDirective(key, value, "iris.features.optional", options -> this.optionalFeatureFlags = options);
         handleWhitespacedListDirective(key, value, "sliders", sliders -> this.sliderOptions = sliders);
         handlePrefixedWhitespacedListDirective("profile.", key, value, this.profiles::put);
         if (!handleIntDirective(key, value, "screen.columns", columns -> this.mainScreenColumnCount = columns)) {
            if (!handleAffixedIntDirective("screen.", ".columns", key, value, this.subScreenColumnCount::put)) {
               handleWhitespacedListDirective(key, value, "screen", options -> this.mainScreenOptions = options);
               handlePrefixedWhitespacedListDirective("screen.", key, value, this.subScreenOptions::put);
            }
         }
      });
   }

   private static void handleBooleanValue(String key, String value, BooleanConsumer handler) {
      if ("true".equals(value) || "1".equals(value)) {
         handler.accept(true);
      } else if (!"false".equals(value) && !"0".equals(value)) {
         Iris.logger.warn("Unexpected value for boolean key " + key + " in shaders.properties: got " + value + ", but expected either true or false");
      } else {
         handler.accept(false);
      }
   }

   private static void handleBooleanDirective(String key, String value, String expectedKey, Consumer<OptionalBoolean> handler) {
      if (expectedKey.equals(key)) {
         if ("true".equals(value) || "1".equals(value)) {
            handler.accept(OptionalBoolean.TRUE);
         } else if (!"false".equals(value) && !"0".equals(value)) {
            Iris.logger.warn("Unexpected value for boolean key " + key + " in shaders.properties: got " + value + ", but expected either true or false");
         } else {
            handler.accept(OptionalBoolean.FALSE);
         }
      }
   }

   private static boolean handleIntDirective(String key, String value, String expectedKey, Consumer<Integer> handler) {
      if (!expectedKey.equals(key)) {
         return false;
      } else {
         try {
            int result = Integer.parseInt(value);
            handler.accept(result);
         } catch (NumberFormatException var5) {
            Iris.logger.warn("Unexpected value for integer key " + key + " in shaders.properties: got " + value + ", but expected an integer");
         }

         return true;
      }
   }

   private static boolean handleAffixedIntDirective(String prefix, String suffix, String key, String value, BiConsumer<String, Integer> handler) {
      if (key.startsWith(prefix) && key.endsWith(suffix)) {
         int substrBegin = prefix.length();
         int substrEnd = key.length() - suffix.length();
         if (substrEnd <= substrBegin) {
            return false;
         } else {
            String affixStrippedKey = key.substring(substrBegin, substrEnd);

            try {
               int result = Integer.parseInt(value);
               handler.accept(affixStrippedKey, result);
            } catch (NumberFormatException var9) {
               Iris.logger.warn("Unexpected value for integer key " + key + " in shaders.properties: got " + value + ", but expected an integer");
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static void handlePassDirective(String prefix, String key, String value, Consumer<String> handler) {
      if (key.startsWith(prefix)) {
         String pass = key.substring(prefix.length());
         handler.accept(pass);
      }
   }

   private static void handleProgramEnabledDirective(String prefix, String key, String value, Consumer<String> handler) {
      if (key.startsWith(prefix)) {
         String program = key.substring(prefix.length(), key.indexOf(".", prefix.length()));
         handler.accept(program);
      }
   }

   private static void handleWhitespacedListDirective(String key, String value, String expectedKey, Consumer<List<String>> handler) {
      if (expectedKey.equals(key)) {
         String[] elements = value.split(" +");
         handler.accept(Arrays.asList(elements));
      }
   }

   private static void handlePrefixedWhitespacedListDirective(String prefix, String key, String value, BiConsumer<String, List<String>> handler) {
      if (key.startsWith(prefix)) {
         String prefixStrippedKey = key.substring(prefix.length());
         String[] elements = value.split(" +");
         handler.accept(prefixStrippedKey, Arrays.asList(elements));
      }
   }

   private static void handleTwoArgDirective(String prefix, String key, String value, BiConsumer<String, String> handler) {
      if (key.startsWith(prefix)) {
         int endOfPassIndex = key.indexOf(".", prefix.length());
         String stage = key.substring(prefix.length(), endOfPassIndex);
         String sampler = key.substring(endOfPassIndex + 1);
         handler.accept(stage, sampler);
      }
   }

   public static ShaderProperties empty() {
      return new ShaderProperties();
   }

   public OptionalBoolean getDhShadowEnabled() {
      return this.dhShadowEnabled;
   }

   public CloudSetting getCloudSetting() {
      return this.cloudSetting;
   }

   public CloudSetting getDHCloudSetting() {
      return this.dhCloudSetting;
   }

   public OptionalBoolean getOldHandLight() {
      return this.oldHandLight;
   }

   public OptionalBoolean getDynamicHandLight() {
      return this.dynamicHandLight;
   }

   public OptionalBoolean getOldLighting() {
      return this.oldLighting;
   }

   public OptionalBoolean getShadowTerrain() {
      return this.shadowTerrain;
   }

   public OptionalBoolean getShadowTranslucent() {
      return this.shadowTranslucent;
   }

   public OptionalBoolean getShadowEntities() {
      return this.shadowEntities;
   }

   public OptionalBoolean getShadowPlayer() {
      return this.shadowPlayer;
   }

   public OptionalBoolean getShadowBlockEntities() {
      return this.shadowBlockEntities;
   }

   public OptionalBoolean getShadowLightBlockEntities() {
      return this.shadowLightBlockEntities;
   }

   public OptionalBoolean getUnderwaterOverlay() {
      return this.underwaterOverlay;
   }

   public OptionalBoolean getSun() {
      return this.sun;
   }

   public OptionalBoolean getWeather() {
      return this.weather;
   }

   public OptionalBoolean getWeatherParticles() {
      return this.weatherParticles;
   }

   public OptionalBoolean getMoon() {
      return this.moon;
   }

   public OptionalBoolean getStars() {
      return this.stars;
   }

   public OptionalBoolean getSky() {
      return this.sky;
   }

   public OptionalBoolean getVignette() {
      return this.vignette;
   }

   public OptionalBoolean getBackFaceSolid() {
      return this.backFaceSolid;
   }

   public OptionalBoolean getBackFaceCutout() {
      return this.backFaceCutout;
   }

   public OptionalBoolean getBackFaceCutoutMipped() {
      return this.backFaceCutoutMipped;
   }

   public OptionalBoolean getBackFaceTranslucent() {
      return this.backFaceTranslucent;
   }

   public OptionalBoolean getRainDepth() {
      return this.rainDepth;
   }

   public OptionalBoolean getBeaconBeamDepth() {
      return this.beaconBeamDepth;
   }

   public OptionalBoolean getSeparateAo() {
      return this.separateAo;
   }

   public OptionalBoolean getVoxelizeLightBlocks() {
      return this.voxelizeLightBlocks;
   }

   public OptionalBoolean getSeparateEntityDraws() {
      return this.separateEntityDraws;
   }

   public OptionalBoolean skipAllRendering() {
      return this.skipAllRendering;
   }

   public OptionalBoolean getFrustumCulling() {
      return this.frustumCulling;
   }

   public OptionalBoolean getOcclusionCulling() {
      return this.occlusionCulling;
   }

   public ShadowCullState getShadowCulling() {
      return this.shadowCulling;
   }

   public int getFallbackTex() {
      return this.fallbackTex;
   }

   public Object2ObjectMap<String, AlphaTest> getAlphaTestOverrides() {
      return this.alphaTestOverrides;
   }

   public OptionalBoolean getShadowEnabled() {
      return this.shadowEnabled;
   }

   public ParticleRenderingSettings getParticleRenderingSettings() {
      return this.particleRenderingSettings;
   }

   public OptionalBoolean getConcurrentCompute() {
      return this.concurrentCompute;
   }

   public OptionalBoolean getPrepareBeforeShadow() {
      return this.prepareBeforeShadow;
   }

   public Object2ObjectMap<String, ViewportData> getViewportScaleOverrides() {
      return this.viewportScaleOverrides;
   }

   public Object2ObjectMap<String, TextureScaleOverride> getTextureScaleOverrides() {
      return this.textureScaleOverrides;
   }

   public Object2ObjectMap<String, BlendModeOverride> getBlendModeOverrides() {
      return this.blendModeOverrides;
   }

   public Object2ObjectMap<String, IndirectPointer> getIndirectPointers() {
      return this.indirectPointers;
   }

   public Object2ObjectMap<String, ArrayList<BufferBlendInformation>> getBufferBlendOverrides() {
      return this.bufferBlendOverrides;
   }

   public Int2ObjectArrayMap<ShaderStorageInfo> getBufferObjects() {
      return this.bufferObjects;
   }

   public EnumMap<TextureStage, Object2ObjectMap<String, TextureDefinition>> getCustomTextures() {
      return this.customTextures;
   }

   public Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> getCustomTexturePatching() {
      return this.customTexturePatching;
   }

   public Object2ObjectMap<String, TextureDefinition> getIrisCustomTextures() {
      return this.irisCustomTextures;
   }

   public List<ImageInformation> getIrisCustomImages() {
      return this.irisCustomImages;
   }

   public Optional<String> getNoiseTexturePath() {
      return Optional.ofNullable(this.noiseTexturePath);
   }

   public Object2ObjectMap<String, String> getConditionallyEnabledPrograms() {
      return this.conditionallyEnabledPrograms;
   }

   public List<String> getSliderOptions() {
      return this.sliderOptions;
   }

   public Map<String, List<String>> getProfiles() {
      return this.profiles;
   }

   public Optional<List<String>> getMainScreenOptions() {
      return Optional.ofNullable(this.mainScreenOptions);
   }

   public Map<String, List<String>> getSubScreenOptions() {
      return this.subScreenOptions;
   }

   public Optional<Integer> getMainScreenColumnCount() {
      return Optional.ofNullable(this.mainScreenColumnCount);
   }

   public Map<String, Integer> getSubScreenColumnCount() {
      return this.subScreenColumnCount;
   }

   public Object2ObjectMap<String, Object2BooleanMap<String>> getExplicitFlips() {
      return this.explicitFlips;
   }

   public List<String> getRequiredFeatureFlags() {
      return this.requiredFeatureFlags;
   }

   public List<String> getOptionalFeatureFlags() {
      return this.optionalFeatureFlags;
   }

   public OptionalBoolean supportsColorCorrection() {
      return this.supportsColorCorrection;
   }

   public CustomUniforms.Builder getCustomUniforms() {
      return this.customUniforms;
   }
}
