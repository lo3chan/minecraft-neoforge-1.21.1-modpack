/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.model.BoatModel
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.LeashKnotModel
 *  net.minecraft.client.model.MinecartModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.ShulkerBulletModel
 *  net.minecraft.client.model.SkullModel
 *  net.minecraft.client.model.TridentModel
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.entity.AxolotlRenderer
 *  net.minecraft.client.renderer.entity.BeeRenderer
 *  net.minecraft.client.renderer.entity.BoatRenderer
 *  net.minecraft.client.renderer.entity.CatRenderer
 *  net.minecraft.client.renderer.entity.ChestedHorseRenderer
 *  net.minecraft.client.renderer.entity.EndCrystalRenderer
 *  net.minecraft.client.renderer.entity.EnderDragonRenderer
 *  net.minecraft.client.renderer.entity.EnderDragonRenderer$DragonModel
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.FoxRenderer
 *  net.minecraft.client.renderer.entity.FrogRenderer
 *  net.minecraft.client.renderer.entity.GhastRenderer
 *  net.minecraft.client.renderer.entity.HorseRenderer
 *  net.minecraft.client.renderer.entity.ItemFrameRenderer
 *  net.minecraft.client.renderer.entity.LeashKnotRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.LlamaRenderer
 *  net.minecraft.client.renderer.entity.MinecartRenderer
 *  net.minecraft.client.renderer.entity.MushroomCowRenderer
 *  net.minecraft.client.renderer.entity.PandaRenderer
 *  net.minecraft.client.renderer.entity.ParrotRenderer
 *  net.minecraft.client.renderer.entity.RabbitRenderer
 *  net.minecraft.client.renderer.entity.ShulkerBulletRenderer
 *  net.minecraft.client.renderer.entity.ShulkerRenderer
 *  net.minecraft.client.renderer.entity.StriderRenderer
 *  net.minecraft.client.renderer.entity.ThrownTridentRenderer
 *  net.minecraft.client.renderer.entity.TropicalFishRenderer
 *  net.minecraft.client.renderer.entity.UndeadHorseRenderer
 *  net.minecraft.client.renderer.entity.VexRenderer
 *  net.minecraft.client.renderer.entity.WitherBossRenderer
 *  net.minecraft.client.renderer.entity.WitherSkullRenderer
 *  net.minecraft.client.renderer.entity.WolfRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.animal.axolotl.Axolotl$Variant
 *  net.minecraft.world.entity.vehicle.Boat$Type
 *  org.joml.Matrix4fStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
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
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class MobEntry
extends LabelEntry {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.parse((String)"physicsmod:textures/gui/white.png");
    private static final Map<EntityRenderer, Model> models = new Object2ObjectOpenHashMap();
    private String text;
    private EntityType<?> entityType;

    public MobEntry(LegacyObjectSelectionList objectSelectionList, String text) {
        super(objectSelectionList, text);
        this.text = text;
        this.entityType = (EntityType)EntityType.byString((String)text).get();
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        String newText;
        Font font = Minecraft.getInstance().font;
        Object text = this.text;
        if (font.width((FormattedText)Component.literal((String)text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55 && !((String)text).equalsIgnoreCase(newText = font.plainSubstrByWidth((String)text, this.objectSelectionList.getRowWidth() - 58))) {
            text = newText + "...";
        }
        MutableComponent label = Component.literal((String)text);
        if (hovered) {
            label = label.withStyle(ChatFormatting.BOLD);
            guiGraphics.drawCenteredString(font, (Component)label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xFFFFFF);
        } else {
            guiGraphics.drawCenteredString(font, (Component)label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xC2C2C2);
        }
        Matrix4fStack matrices = RenderSystem.getModelViewStack();
        float scale = (float)entryHeight / 2.0f * 0.9f;
        double xPosition = this.objectSelectionList.getRowLeft() + 2 + (int)scale;
        double yPosition = y + entryHeight / 2;
        matrices.pushMatrix();
        matrices.translate((float)xPosition, (float)yPosition, 100.0f);
        matrices.scale(scale, scale, scale);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableDepthTest();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        Lighting.setupForEntityInInventory();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        PhysicsMod.sodiumCatchBoundingBox = true;
        PhysicsMod.sodiumBoundingBox.start.set(Double.MAX_VALUE);
        PhysicsMod.sodiumBoundingBox.end.set(-1.7976931348623157E308);
        try {
            EntityRenderer<?> renderer = PhysicsMod.renderers.get(this.entityType);
            ResourceLocation textureLocation = MobEntry.getTextureLocation(renderer, this.entityType);
            Model model = MobEntry.getModel(renderer, this.entityType);
            if (model instanceof EntityModel) {
                EntityModel entityModel = (EntityModel)model;
                entityModel.young = false;
            }
            RenderType renderType = RenderType.entityCutout((ResourceLocation)textureLocation);
            BoundingBoxGetter boundingBox = StarterClient.sodium ? Sodium.getNewBoundingBoxConsumer() : new BoundingBoxGetter();
            model.renderToBuffer(new PoseStack(), (VertexConsumer)boundingBox, 0xF000F0, OverlayTexture.NO_OVERLAY);
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
            float mobScale = 1.0f / (float)Math.max(mobWidth, Math.max(mobHeight, mobDepth)) * 2.0f;
            matrices.scale(mobScale, mobScale, mobScale);
            matrices.translate((float)(-mobWidth * 0.5 - startX), (float)(-mobHeight * 0.5 - startY), (float)(-mobDepth * 0.5 - startZ));
            matrices.rotate((Quaternionfc)new Quaternionf().rotationXYZ((float)Math.toRadians(-25.0), (float)Math.toRadians(-130.0), 0.0f));
            RenderSystem.applyModelViewMatrix();
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
            model.renderToBuffer(new PoseStack(), vertexConsumer, 0xF000F0, OverlayTexture.NO_OVERLAY);
            bufferSource.endBatch();
        }
        catch (Exception exception) {
            // empty catch block
        }
        matrices.popMatrix();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        PhysicsMod.sodiumCatchBoundingBox = false;
    }

    public static Model getModel(EntityRenderer renderer, EntityType<?> entityType, Entity entity) {
        if (entityType == EntityType.PLAYER) {
            if (entity != null && entity instanceof AbstractClientPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer)entity;
                if (player.getSkin().model().name().equalsIgnoreCase("slim")) {
                    return models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true));
                }
                return models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false));
            }
            return models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false));
        }
        if (renderer instanceof LivingEntityRenderer) {
            LivingEntityRenderer livingRenderer = (LivingEntityRenderer)renderer;
            return livingRenderer.getModel();
        }
        if (renderer instanceof EnderDragonRenderer) {
            return models.computeIfAbsent(renderer, key -> new EnderDragonRenderer.DragonModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ENDER_DRAGON)));
        }
        if (renderer instanceof BoatRenderer) {
            return models.computeIfAbsent(renderer, key -> new BoatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.createBoatModelName((Boat.Type)Boat.Type.OAK))));
        }
        if (renderer instanceof MinecartRenderer) {
            if (entityType == EntityType.TNT_MINECART) {
                return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TNT_MINECART)));
            }
            if (entityType == EntityType.CHEST_MINECART) {
                return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.CHEST_MINECART)));
            }
            if (entityType == EntityType.COMMAND_BLOCK_MINECART) {
                return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.COMMAND_BLOCK_MINECART)));
            }
            if (entityType == EntityType.FURNACE_MINECART) {
                return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.FURNACE_MINECART)));
            }
            if (entityType == EntityType.HOPPER_MINECART) {
                return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.HOPPER_MINECART)));
            }
            if (entityType == EntityType.SPAWNER_MINECART) {
                return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SPAWNER_MINECART)));
            }
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.MINECART)));
        }
        if (renderer instanceof ShulkerBulletRenderer) {
            return models.computeIfAbsent(renderer, key -> new ShulkerBulletModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER_BULLET)));
        }
        if (renderer instanceof ThrownTridentRenderer) {
            return models.computeIfAbsent(renderer, key -> new TridentModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TRIDENT)));
        }
        if (renderer instanceof WitherSkullRenderer) {
            return models.computeIfAbsent(renderer, key -> new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.WITHER_SKULL)));
        }
        if (renderer instanceof LeashKnotRenderer) {
            return models.computeIfAbsent(renderer, key -> new LeashKnotModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.LEASH_KNOT)));
        }
        if (renderer instanceof EndCrystalRenderer) {
            return models.computeIfAbsent(renderer, key -> new HierarchicalModel(){
                ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.END_CRYSTAL);

                public ModelPart root() {
                    return this.root;
                }

                public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
                }
            });
        }
        return null;
    }

    public static Model getModel(EntityRenderer renderer, EntityType<?> entityType) {
        return MobEntry.getModel(renderer, entityType, null);
    }

    public static ResourceLocation getTextureLocation(EntityRenderer renderer, EntityType entityType, Entity entity) {
        if (entityType == EntityType.PLAYER) {
            if (entity != null && entity instanceof AbstractClientPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer)entity;
                return player.getSkin().texture();
            }
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/player/wide/steve.png");
        }
        if (entityType == EntityType.PIGLIN) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/piglin/piglin.png");
        }
        if (entityType == EntityType.ZOMBIFIED_PIGLIN) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/piglin/zombified_piglin.png");
        }
        if (entityType == EntityType.PIGLIN_BRUTE) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/piglin/piglin_brute.png");
        }
        if (renderer instanceof RabbitRenderer) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/rabbit/brown.png");
        }
        if (renderer instanceof AxolotlRenderer) {
            return ResourceLocation.withDefaultNamespace((String)String.format("textures/entity/axolotl/axolotl_%s.png", Axolotl.Variant.BLUE.getName()));
        }
        if (renderer instanceof BeeRenderer) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/bee/bee.png");
        }
        if (renderer instanceof BoatRenderer) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/boat/oak.png");
        }
        if (renderer instanceof CatRenderer) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/cat/tabby.png");
        }
        if (renderer instanceof HorseRenderer) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/horse/horse_white.png");
        }
        if (renderer instanceof UndeadHorseRenderer) {
            if (entityType == EntityType.ZOMBIE_HORSE) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/horse/horse_zombie.png");
            }
            if (entityType == EntityType.SKELETON_HORSE) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/horse/horse_skeleton.png");
            }
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/horse/horse_skeleton.png");
        }
        if (renderer instanceof LlamaRenderer) {
            return ResourceLocation.withDefaultNamespace((String)"textures/entity/llama/creamy.png");
        }
        if (renderer instanceof ChestedHorseRenderer) {
            if (entityType == EntityType.DONKEY) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/horse/donkey.png");
            }
            if (entityType == EntityType.MULE) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/horse/mule.png");
            }
        } else {
            if (renderer instanceof FoxRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/fox/fox.png");
            }
            if (renderer instanceof GhastRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/ghast/ghast.png");
            }
            if (renderer instanceof MushroomCowRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/cow/red_mooshroom.png");
            }
            if (renderer instanceof PandaRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/panda/panda.png");
            }
            if (renderer instanceof ParrotRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/parrot/parrot_red_blue.png");
            }
            if (renderer instanceof ShulkerRenderer) {
                return ResourceLocation.withDefaultNamespace((String)("textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png"));
            }
            if (renderer instanceof StriderRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/strider/strider.png");
            }
            if (renderer instanceof TropicalFishRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/fish/tropical_a.png");
            }
            if (renderer instanceof VexRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/illager/vex.png");
            }
            if (renderer instanceof WitherBossRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/wither/wither.png");
            }
            if (renderer instanceof WolfRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/wolf/wolf.png");
            }
            if (renderer instanceof ItemFrameRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/wolf/wolf.png");
            }
            if (renderer instanceof WitherSkullRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/wither/wither_invulnerable.png");
            }
            if (renderer instanceof FrogRenderer) {
                return ResourceLocation.withDefaultNamespace((String)"textures/entity/frog/cold_frog.png");
            }
        }
        ResourceLocation texture = null;
        try {
            texture = renderer.getTextureLocation(null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (texture == null) {
            texture = MobEntry.getBackupTextureLocation(renderer, entityType);
        }
        if (texture == null) {
            return DEFAULT_TEXTURE;
        }
        return texture;
    }

    public static ResourceLocation getTextureLocation(EntityRenderer renderer, EntityType entityType) {
        return MobEntry.getTextureLocation(renderer, entityType, null);
    }

    private static ResourceLocation getBackupTextureLocation(EntityRenderer renderer, EntityType entityType) {
        Field[] fields;
        for (Field field : fields = renderer.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !field.getType().equals(ResourceLocation.class)) continue;
            try {
                String file;
                field.setAccessible(true);
                ResourceLocation resource = (ResourceLocation)field.get(null);
                if (resource == null || !(file = resource.getPath()).endsWith(".png") && !file.endsWith(".jpg") && !file.endsWith(".tga") && !file.endsWith(".jpeg")) continue;
                return resource;
            }
            catch (IllegalAccessException | IllegalArgumentException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Component getNarration() {
        return Component.literal((String)this.text);
    }
}

