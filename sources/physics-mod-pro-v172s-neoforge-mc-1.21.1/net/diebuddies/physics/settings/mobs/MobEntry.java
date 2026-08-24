package net.diebuddies.physics.settings.mobs;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import net.diebuddies.compat.Sodium;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.AxolotlRenderer;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.StriderRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer.DragonModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.axolotl.Axolotl.Variant;
import net.minecraft.world.entity.vehicle.Boat.Type;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public class MobEntry extends LabelEntry {
   private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.parse("physicsmod:textures/gui/white.png");
   private static final Map<EntityRenderer, Model> models = new Object2ObjectOpenHashMap();
   private String text;
   private EntityType<?> entityType;

   public MobEntry(LegacyObjectSelectionList objectSelectionList, String text) {
      super(objectSelectionList, text);
      this.text = text;
      this.entityType = (EntityType<?>)EntityType.byString(text).get();
   }

   public void setText(String text) {
      this.text = text;
   }

   @Override
   public void render(
      GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      Font font = Minecraft.getInstance().font;
      String text = this.text;
      if (font.width(Component.literal(text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55) {
         String newText = font.plainSubstrByWidth(text, this.objectSelectionList.getRowWidth() - 58);
         if (!text.equalsIgnoreCase(newText)) {
            text = newText + "...";
         }
      }

      MutableComponent label = Component.literal(text);
      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.drawCenteredString(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 16777215);
      } else {
         guiGraphics.drawCenteredString(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 12763842);
      }

      Matrix4fStack matrices = RenderSystem.getModelViewStack();
      float scale = entryHeight / 2.0F * 0.9F;
      double xPosition = this.objectSelectionList.getRowLeft() + 2 + (int)scale;
      double yPosition = y + entryHeight / 2;
      matrices.pushMatrix();
      matrices.translate((float)xPosition, (float)yPosition, 100.0F);
      matrices.scale(scale, scale, scale);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableDepthTest();
      Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
      Lighting.setupForEntityInInventory();
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      PhysicsMod.sodiumCatchBoundingBox = true;
      PhysicsMod.sodiumBoundingBox.start.set(1.7976931348623157E308);
      PhysicsMod.sodiumBoundingBox.end.set(-1.7976931348623157E308);

      try {
         EntityRenderer renderer = PhysicsMod.renderers.get(this.entityType);
         ResourceLocation textureLocation = getTextureLocation(renderer, this.entityType);
         Model model = getModel(renderer, this.entityType);
         if (model instanceof EntityModel entityModel) {
            entityModel.young = false;
         }

         RenderType renderType = RenderType.entityCutout(textureLocation);
         BoundingBoxGetter boundingBox = StarterClient.sodium ? Sodium.getNewBoundingBoxConsumer() : new BoundingBoxGetter();
         model.renderToBuffer(new PoseStack(), boundingBox, 15728880, OverlayTexture.NO_OVERLAY);
         if (StarterClient.sodium) {
            boundingBox.min = PhysicsMod.sodiumBoundingBox.getMin();
            boundingBox.max = PhysicsMod.sodiumBoundingBox.getMax();
         }

         double startX = boundingBox.min.x;
         double endX = boundingBox.max.x;
         double startY = boundingBox.min.y;
         double endY = boundingBox.max.y;
         double startZ = boundingBox.min.z;
         double endZ = boundingBox.max.z;
         double mobWidth = endX - startX;
         double mobHeight = endY - startY;
         double mobDepth = endZ - startZ;
         float mobScale = 1.0F / (float)Math.max(mobWidth, Math.max(mobHeight, mobDepth)) * 2.0F;
         matrices.scale(mobScale, mobScale, mobScale);
         matrices.translate((float)(-mobWidth * 0.5 - startX), (float)(-mobHeight * 0.5 - startY), (float)(-mobDepth * 0.5 - startZ));
         matrices.rotate(new Quaternionf().rotationXYZ((float)Math.toRadians(-25.0), (float)Math.toRadians(-130.0), 0.0F));
         RenderSystem.applyModelViewMatrix();
         BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
         VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
         model.renderToBuffer(new PoseStack(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
         bufferSource.endBatch();
      } catch (Exception var46) {
      }

      matrices.popMatrix();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
      PhysicsMod.sodiumCatchBoundingBox = false;
   }

   public static Model getModel(EntityRenderer renderer, EntityType<?> entityType, Entity entity) {
      if (entityType == EntityType.PLAYER) {
         if (entity != null && entity instanceof AbstractClientPlayer player) {
            return player.getSkin().model().name().equalsIgnoreCase("slim")
               ? models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true))
               : models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false));
         } else {
            return models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false));
         }
      } else if (renderer instanceof LivingEntityRenderer livingRenderer) {
         return livingRenderer.getModel();
      } else if (renderer instanceof EnderDragonRenderer) {
         return models.computeIfAbsent(renderer, key -> new DragonModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ENDER_DRAGON)));
      } else if (renderer instanceof BoatRenderer) {
         return models.computeIfAbsent(
            renderer, key -> new BoatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.createBoatModelName(Type.OAK)))
         );
      } else if (renderer instanceof MinecartRenderer) {
         if (entityType == EntityType.TNT_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TNT_MINECART)));
         } else if (entityType == EntityType.CHEST_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.CHEST_MINECART)));
         } else if (entityType == EntityType.COMMAND_BLOCK_MINECART) {
            return models.computeIfAbsent(
               renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.COMMAND_BLOCK_MINECART))
            );
         } else if (entityType == EntityType.FURNACE_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.FURNACE_MINECART)));
         } else if (entityType == EntityType.HOPPER_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.HOPPER_MINECART)));
         } else {
            return entityType == EntityType.SPAWNER_MINECART
               ? models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SPAWNER_MINECART)))
               : models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.MINECART)));
         }
      } else if (renderer instanceof ShulkerBulletRenderer) {
         return models.computeIfAbsent(renderer, key -> new ShulkerBulletModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER_BULLET)));
      } else if (renderer instanceof ThrownTridentRenderer) {
         return models.computeIfAbsent(renderer, key -> new TridentModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TRIDENT)));
      } else if (renderer instanceof WitherSkullRenderer) {
         return models.computeIfAbsent(renderer, key -> new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.WITHER_SKULL)));
      } else if (renderer instanceof LeashKnotRenderer) {
         return models.computeIfAbsent(renderer, key -> new LeashKnotModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.LEASH_KNOT)));
      } else {
         return renderer instanceof EndCrystalRenderer ? models.computeIfAbsent(renderer, key -> new HierarchicalModel() {
            ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.END_CRYSTAL);

            public ModelPart root() {
               return this.root;
            }

            public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
            }
         }) : null;
      }
   }

   public static Model getModel(EntityRenderer renderer, EntityType<?> entityType) {
      return getModel(renderer, entityType, null);
   }

   public static ResourceLocation getTextureLocation(EntityRenderer renderer, EntityType entityType, Entity entity) {
      if (entityType == EntityType.PLAYER) {
         return entity != null && entity instanceof AbstractClientPlayer player
            ? player.getSkin().texture()
            : ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");
      } else if (entityType == EntityType.PIGLIN) {
         return ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin.png");
      } else if (entityType == EntityType.ZOMBIFIED_PIGLIN) {
         return ResourceLocation.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");
      } else if (entityType == EntityType.PIGLIN_BRUTE) {
         return ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
      } else if (renderer instanceof RabbitRenderer) {
         return ResourceLocation.withDefaultNamespace("textures/entity/rabbit/brown.png");
      } else if (renderer instanceof AxolotlRenderer) {
         return ResourceLocation.withDefaultNamespace(String.format("textures/entity/axolotl/axolotl_%s.png", Variant.BLUE.getName()));
      } else if (renderer instanceof BeeRenderer) {
         return ResourceLocation.withDefaultNamespace("textures/entity/bee/bee.png");
      } else if (renderer instanceof BoatRenderer) {
         return ResourceLocation.withDefaultNamespace("textures/entity/boat/oak.png");
      } else if (renderer instanceof CatRenderer) {
         return ResourceLocation.withDefaultNamespace("textures/entity/cat/tabby.png");
      } else if (renderer instanceof HorseRenderer) {
         return ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_white.png");
      } else if (renderer instanceof UndeadHorseRenderer) {
         if (entityType == EntityType.ZOMBIE_HORSE) {
            return ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_zombie.png");
         } else {
            return entityType == EntityType.SKELETON_HORSE
               ? ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png")
               : ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png");
         }
      } else if (renderer instanceof LlamaRenderer) {
         return ResourceLocation.withDefaultNamespace("textures/entity/llama/creamy.png");
      } else {
         if (renderer instanceof ChestedHorseRenderer) {
            if (entityType == EntityType.DONKEY) {
               return ResourceLocation.withDefaultNamespace("textures/entity/horse/donkey.png");
            }

            if (entityType == EntityType.MULE) {
               return ResourceLocation.withDefaultNamespace("textures/entity/horse/mule.png");
            }
         } else {
            if (renderer instanceof FoxRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/fox/fox.png");
            }

            if (renderer instanceof GhastRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/ghast/ghast.png");
            }

            if (renderer instanceof MushroomCowRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/cow/red_mooshroom.png");
            }

            if (renderer instanceof PandaRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/panda/panda.png");
            }

            if (renderer instanceof ParrotRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_red_blue.png");
            }

            if (renderer instanceof ShulkerRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");
            }

            if (renderer instanceof StriderRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/strider/strider.png");
            }

            if (renderer instanceof TropicalFishRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a.png");
            }

            if (renderer instanceof VexRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");
            }

            if (renderer instanceof WitherBossRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
            }

            if (renderer instanceof WolfRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf.png");
            }

            if (renderer instanceof ItemFrameRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf.png");
            }

            if (renderer instanceof WitherSkullRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
            }

            if (renderer instanceof FrogRenderer) {
               return ResourceLocation.withDefaultNamespace("textures/entity/frog/cold_frog.png");
            }
         }

         ResourceLocation texture = null;

         try {
            texture = renderer.getTextureLocation(null);
         } catch (Exception var5) {
         }

         if (texture == null) {
            texture = getBackupTextureLocation(renderer, entityType);
         }

         return texture == null ? DEFAULT_TEXTURE : texture;
      }
   }

   public static ResourceLocation getTextureLocation(EntityRenderer renderer, EntityType entityType) {
      return getTextureLocation(renderer, entityType, null);
   }

   private static ResourceLocation getBackupTextureLocation(EntityRenderer renderer, EntityType entityType) {
      Field[] fields = renderer.getClass().getDeclaredFields();

      for (Field field : fields) {
         if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(ResourceLocation.class)) {
            try {
               field.setAccessible(true);
               ResourceLocation resource = (ResourceLocation)field.get(null);
               if (resource != null) {
                  String file = resource.getPath();
                  if (file.endsWith(".png") || file.endsWith(".jpg") || file.endsWith(".tga") || file.endsWith(".jpeg")) {
                     return resource;
                  }
               }
            } catch (IllegalAccessException | IllegalArgumentException var9) {
               var9.printStackTrace();
            }
         }
      }

      return null;
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.text);
   }
}
