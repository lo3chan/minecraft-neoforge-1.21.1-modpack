/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.block.BlockModelShaper
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.BlockElementFace
 *  net.minecraft.client.renderer.block.model.BlockElementRotation
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.client.resources.model.MultiPartBakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.apache.commons.lang3.tuple.Pair
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.render;

import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.AABBf;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.JsonUnbakedModelHolder;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.liquid.WaterController;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class PhysicsUpdater {
    private static final Set<Block> excludeBlockPhysicsTexture = new ObjectOpenHashSet();
    private Matrix4d tmpMatrix = new Matrix4d();

    public void updatePhysics(PhysicsMod mod, ClientLevel level, Vec3 cameraPos, PhysicsWorld physics) {
        if (mod.updatedLightBlocks.size() > 0) {
            LongIterator it = mod.updatedLightBlocks.iterator();
            if (ConfigClient.areSnowPhysicsEnabled() || ConfigClient.areOceanPhysicsEnabled()) {
                while (it.hasNext()) {
                    long blockIndex = it.nextLong();
                    long sectionIndex = SectionPos.blockToSection((long)blockIndex);
                    int lx = SectionPos.sectionRelative((int)BlockPos.getX((long)blockIndex));
                    int ly = SectionPos.sectionRelative((int)BlockPos.getY((long)blockIndex));
                    int lz = SectionPos.sectionRelative((int)BlockPos.getZ((long)blockIndex));
                    short localPos = (short)(lx << 8 | ly << 4 | lz);
                    if (ConfigClient.areSnowPhysicsEnabled()) {
                        physics.getSnowWorld().getLightUpdates(sectionIndex).add(localPos);
                    }
                    if (!ConfigClient.areOceanPhysicsEnabled()) continue;
                    physics.getOceanWorld().getLightUpdates(sectionIndex).add(localPos);
                }
            }
            if (!StarterClient.disableLightingCache) {
                for (IRigidBody body : physics.getBodies()) {
                    long pos;
                    PhysicsEntity entity = body.getEntity();
                    BlockPos.MutableBlockPos cached = entity.getCachedBrightnessPos();
                    if (cached == null || !mod.updatedLightBlocks.contains(pos = cached.asLong())) continue;
                    entity.invalidateBrightness();
                }
            }
            WeatherEffects.invalidateLight = true;
            mod.updatedLightBlocks.clear();
        }
        mod.removeUpdates.clear();
        for (int i = mod.updateQueue.size() - 1; i >= 0; --i) {
            BlockUpdate blockUpdate = mod.updateQueue.get(i);
            if (!mod.fallingBlocks.isEmpty() && mod.fallingBlocks.contains(blockUpdate.pos)) continue;
            mod.removeUpdates.add(blockUpdate);
        }
        mod.fallingBlocks.clear();
        mod.updateQueue.clear();
        ObjectArrayList newParts = new ObjectArrayList();
        ObjectArrayList newPartsVoxel = new ObjectArrayList();
        double maxActivationDistanceSqr = ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange;
        BlockEntityRenderDispatcher berd = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        for (BlockUpdate bu : mod.removeUpdates) {
            DynamicSetting setting;
            BlockSetting blockSetting;
            BlockEntityRenderer renderer;
            if (bu.blockEntity != null && (renderer = berd.getRenderer(bu.blockEntity)) != null) {
                blockSetting = ConfigBlocks.getBlockSetting(bu.state.getBlock());
                if (cameraPos.distanceToSqr((double)bu.pos.getX(), (double)bu.pos.getY(), (double)bu.pos.getZ()) < maxActivationDistanceSqr || ConfigClient.blockPhysicsRange > 319.999) {
                    if (blockSetting.getType() == BlockPhysicsType.FRACTURED || blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL || blockSetting.getType() == BlockPhysicsType.BLOCKY) {
                        PhysicsEntity blocky;
                        PhysicsEntity entity = mod.renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, (BlockEntityRenderer<BlockEntity>)renderer, bu.blockEntity, bu.state, bu.pos);
                        if (entity != null) {
                            physics.addBlockParticle(entity).applyRandomSpawnForces();
                        }
                        if (blockSetting.getType() == BlockPhysicsType.FRACTURED) {
                            newParts.addAll(this.getBlockData(physics, bu, bu.level));
                        } else if (blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL) {
                            newPartsVoxel.addAll(this.getBlockData(physics, bu, bu.level));
                        } else if (blockSetting.getType() == BlockPhysicsType.BLOCKY && (blocky = mod.renderBlockIntoEntity(bu.level, PhysicsEntity.Type.BLOCK, bu.state, bu.pos, false)) != null) {
                            physics.addBlockParticle(blocky).applyRandomSpawnForces();
                        }
                    } else if (blockSetting.getType() == BlockPhysicsType.PARTICLES) {
                        double percent = this.calculateChance(physics.getBodies().size());
                        if (mod.removeUpdates.size() > 8) {
                            percent = java.lang.Math.min(percent, 0.1);
                        } else if (mod.removeUpdates.size() == 1) {
                            percent = java.lang.Math.max(0.1, percent);
                        }
                        this.spawnBlockBreakParticles(bu.level, bu.state, bu.pos, percent);
                    }
                }
                if (!ConfigClient.liquidPhysics || bu.state.getBlock() != Blocks.ENCHANTING_TABLE || ConfigClient.cudaLiquids()) continue;
                physics.addLiquid(new Liquid(new WaterController(bu.pos, ConfigClient.waterDensity, true, 2)));
                continue;
            }
            if (bu.state.getBlock() == Blocks.TNT || bu.state.getBlock() == Blocks.PISTON_HEAD || bu.state.getRenderShape() == RenderShape.INVISIBLE || ConfigClient.areDynamicBlockPhysicsEnabled() && (setting = VineHelper.getSetting(bu.state)) != null && setting.linkedPhysics) continue;
            if (ConfigClient.liquidPhysics) {
                WaterController controller = null;
                if (bu.state.getBlock() == Blocks.MELON) {
                    controller = new WaterController(bu.pos, 4, false, 1);
                } else if (bu.state.getBlock() == Blocks.WET_SPONGE) {
                    controller = new WaterController(bu.pos, 4, false, 3);
                } else if (bu.state.getBlock() == Blocks.PUMPKIN) {
                    controller = new WaterController(bu.pos, 4, false, 1);
                } else if (bu.state.getBlock() == Blocks.CACTUS) {
                    controller = new WaterController(bu.pos, 4, false, 1);
                } else if (bu.state.getBlock() == Blocks.WATER_CAULDRON) {
                    int fillLevel = (Integer)bu.state.getValue((Property)BlockStateProperties.LEVEL_CAULDRON);
                    controller = new WaterController(bu.pos, 4, false, fillLevel);
                } else if (bu.state.getBlock() == Blocks.ENCHANTING_TABLE) {
                    controller = new WaterController(bu.pos, ConfigClient.waterDensity, true, 2);
                }
                if (controller != null && !ConfigClient.cudaLiquids()) {
                    physics.addLiquid(new Liquid(controller));
                }
            }
            blockSetting = ConfigBlocks.getBlockSetting(bu.state.getBlock());
            if (!(cameraPos.distanceToSqr((double)bu.pos.getX(), (double)bu.pos.getY(), (double)bu.pos.getZ()) < maxActivationDistanceSqr) && !(ConfigClient.blockPhysicsRange > 319.999)) continue;
            if (blockSetting.getType() == BlockPhysicsType.FRACTURED) {
                newParts.addAll(this.getBlockData(physics, bu, bu.level));
                continue;
            }
            if (blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL) {
                newPartsVoxel.addAll(this.getBlockData(physics, bu, bu.level));
                continue;
            }
            if (blockSetting.getType() == BlockPhysicsType.BLOCKY) {
                PhysicsEntity entity = mod.renderBlockIntoEntity(bu.level, PhysicsEntity.Type.BLOCK, bu.state, bu.pos, false);
                if (entity == null) continue;
                physics.addBlockParticle(entity).applyRandomSpawnForces();
                continue;
            }
            if (blockSetting.getType() != BlockPhysicsType.PARTICLES) continue;
            double percent = this.calculateChance(physics.getBodies().size());
            if (mod.removeUpdates.size() > 8) {
                percent = java.lang.Math.min(percent, 0.1);
            } else if (mod.removeUpdates.size() == 1) {
                percent = java.lang.Math.max(0.1, percent);
            }
            this.spawnBlockBreakParticles(bu.level, bu.state, bu.pos, percent);
        }
        double chance = this.calculateChance(physics.getBodies().size());
        int qsize = newParts.size() + newPartsVoxel.size();
        if (qsize == 1) {
            chance = 1.0;
        } else if (qsize > 10) {
            chance = java.lang.Math.min(chance, 0.3);
        }
        this.addPhysicsBlocks((List<PhysicsEntity>)newParts, chance, physics, false);
        this.addPhysicsBlocks((List<PhysicsEntity>)newPartsVoxel, chance, physics, true);
        while (!mod.entityBlocks.isEmpty()) {
            PhysicsEntity particle = mod.entityBlocks.poll();
            if (particle.noVolume) continue;
            physics.addBlockParticle(particle).applyRandomSpawnForces();
        }
        while (!mod.ragdolls.isEmpty()) {
            Ragdoll ragdoll = mod.ragdolls.poll();
            physics.addRagdoll(ragdoll);
        }
        while (!mod.blockUpdates.isEmpty()) {
            BlockPos pos = mod.blockUpdates.poll();
            physics.queue(() -> physics.blockUpdate(pos));
        }
        while (!mod.explosions.isEmpty()) {
            physics.applyExplosion(mod.explosions.poll());
        }
    }

    private void addPhysicsBlocks(List<PhysicsEntity> newParts, double chance, PhysicsWorld physics, boolean voxel) {
        for (PhysicsEntity particle : newParts) {
            int index;
            double volume = particle.getVolume();
            List<Mesh> mesh = PhysicsMod.brokenBlock;
            List<Mesh> physicsMesh = null;
            if (!((double)Math.random() < chance)) continue;
            if (chance < 0.5 || (double)physics.getBodies().size() > (double)ConfigClient.maxPhysicsObjects * 0.4) {
                index = Math.randomInt(PhysicsMod.brokenBlocksLittle.size());
                if (voxel) {
                    mesh = PhysicsMod.brokenBlocksLittleVoxel.get(index);
                    physicsMesh = PhysicsMod.brokenBlocksLittle.get(index);
                } else {
                    mesh = PhysicsMod.brokenBlocksLittle.get(index);
                }
            } else {
                index = Math.randomInt(PhysicsMod.brokenBlocksLots.size());
                if (voxel) {
                    mesh = PhysicsMod.brokenBlocksLotsVoxel.get(index);
                    physicsMesh = PhysicsMod.brokenBlocksLots.get(index);
                } else {
                    mesh = PhysicsMod.brokenBlocksLots.get(index);
                }
            }
            if (volume < 0.05) {
                mesh = PhysicsMod.brokenBlock;
            } else if (volume < 0.9) {
                index = Math.randomInt(PhysicsMod.brokenBlocksLittle.size());
                if (voxel) {
                    mesh = PhysicsMod.brokenBlocksLittleVoxel.get(index);
                    physicsMesh = PhysicsMod.brokenBlocksLittle.get(index);
                } else {
                    mesh = PhysicsMod.brokenBlocksLittle.get(index);
                }
            }
            physics.addBlockParticle(mesh, physicsMesh, particle);
        }
    }

    private List<PhysicsEntity> getBlockData(PhysicsWorld physics, BlockUpdate update, Level level) {
        ObjectArrayList particles = new ObjectArrayList();
        BlockPos pos = update.pos;
        BlockState state = update.state;
        ModelResourceLocation id = BlockModelShaper.stateToModelLocation((BlockState)state);
        BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(id);
        if (state.getBlock() == Blocks.BROWN_MUSHROOM_BLOCK || state.getBlock() == Blocks.RED_MUSHROOM_BLOCK || state.getBlock() == Blocks.MUSHROOM_STEM) {
            bakedModel = null;
        }
        if (bakedModel instanceof MultiPartBakedModel) {
            MultiPartBakedModel multi = (MultiPartBakedModel)bakedModel;
            BitSet bitSet = (BitSet)multi.selectorCache.get(state);
            if (bitSet == null) {
                bitSet = new BitSet();
                for (int i = 0; i < multi.selectors.size(); ++i) {
                    Pair pair = (Pair)multi.selectors.get(i);
                    if (!((Predicate)pair.getLeft()).test(state)) continue;
                    bitSet.set(i);
                }
                multi.selectorCache.put(state, bitSet);
            }
            for (int j = 0; j < bitSet.length(); ++j) {
                if (!bitSet.get(j)) continue;
                BakedModel model = (BakedModel)((Pair)multi.selectors.get(j)).getRight();
                JsonUnbakedModelHolder unbakedModel = PhysicsMod.loadedModels.get(model);
                if (unbakedModel != null) {
                    this.addParticles((List<PhysicsEntity>)particles, unbakedModel, level, update);
                    continue;
                }
                PhysicsEntity entity = PhysicsMod.getInstance(level).renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, model, update.state, update.pos, false);
                if (entity == null) continue;
                physics.addBlockParticle(entity).applyRandomSpawnForces();
            }
        } else {
            JsonUnbakedModelHolder unbakedModel;
            JsonUnbakedModelHolder jsonUnbakedModelHolder = unbakedModel = bakedModel == null ? null : PhysicsMod.loadedModels.get(bakedModel);
            if (unbakedModel != null && unbakedModel.model instanceof BlockModel) {
                PhysicsEntity entity;
                this.addParticles((List<PhysicsEntity>)particles, unbakedModel, level, update);
                if (particles.size() == 0 && bakedModel != null && (entity = PhysicsMod.getInstance(level).renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, bakedModel, update.state, update.pos, false)) != null) {
                    physics.addBlockParticle(entity).applyRandomSpawnForces();
                }
            } else {
                PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.BLOCK, update.state);
                Minecraft minecraft = Minecraft.getInstance();
                BlockRenderDispatcher ren = minecraft.getBlockRenderer();
                BakedModel model = ren.getBlockModel(state);
                Vec3 blockOffset = update.state.getOffset((BlockGetter)update.level, update.pos);
                particle.getTransformation().translation((double)pos.getX() + 0.5 + blockOffset.x, (double)pos.getY() + 0.5 + blockOffset.y, (double)pos.getZ() + 0.5 + blockOffset.z);
                particle.getOldTransformation().set((Matrix4dc)particle.getTransformation());
                particle.models.get((int)0).texture = model.getParticleIcon();
                particle.models.get((int)0).textureID = Minecraft.getInstance().getTextureManager().getTexture(model.getParticleIcon().atlasLocation()).getId();
                int color = Minecraft.getInstance().getBlockColors().getColor(update.state, (BlockAndTintGetter)level, update.pos, 0);
                if (color == -1) {
                    color = -1;
                }
                particle.setColor(color);
                if (update.state.getBlock() == Blocks.CAULDRON || update.state.getBlock() == Blocks.GRASS_BLOCK) {
                    particle.setColor(-1);
                }
                particles.add(particle);
            }
        }
        return particles;
    }

    private void addParticles(List<PhysicsEntity> particles, JsonUnbakedModelHolder unbakedModel, Level level, BlockUpdate update) {
        BlockState state = update.state;
        BlockPos pos = update.pos;
        Vec3 blockOffset = state.getOffset((BlockGetter)update.level, pos);
        for (BlockElement element : unbakedModel.model.getElements()) {
            int color;
            PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.BLOCK, state);
            if (element.from.x != 0.0f || element.from.y != 0.0f || element.from.z != 0.0f || element.to.x != 16.0f || element.to.y != 16.0f || element.to.z != 16.0f) {
                particle.rescale = new AABBf(new Vector3f(element.from.x() / 16.0f, element.from.y() / 16.0f, element.from.z() / 16.0f), new Vector3f(element.to.x() / 16.0f, element.to.y() / 16.0f, element.to.z() / 16.0f));
            }
            particle.shade = element.shade;
            Minecraft minecraft = Minecraft.getInstance();
            BlockRenderDispatcher ren = minecraft.getBlockRenderer();
            BakedModel model = ren.getBlockModel(state);
            Matrix4f m = unbakedModel.transformation;
            Matrix4d modelTransformation = new Matrix4d();
            modelTransformation.set((Matrix4fc)m);
            Matrix4d transformation = new Matrix4d();
            transformation.mul((Matrix4dc)modelTransformation);
            if (element.rotation != null) {
                transformation.translate((double)element.rotation.origin().x() - 0.5, (double)element.rotation.origin().y() - 0.5, (double)element.rotation.origin().z() - 0.5);
                transformation.mul((Matrix4dc)this.tmpMatrix.set((Matrix4fc)this.getElementRotation(element.rotation)));
                transformation.translate(-((double)element.rotation.origin().x() - 0.5), -((double)element.rotation.origin().y() - 0.5), -((double)element.rotation.origin().z() - 0.5));
            }
            transformation.m30(transformation.m30() + (double)pos.getX() + 0.5 + blockOffset.x);
            transformation.m31(transformation.m31() + (double)pos.getY() + 0.5 + blockOffset.y);
            transformation.m32(transformation.m32() + (double)pos.getZ() + 0.5 + blockOffset.z);
            particle.getTransformation().set((Matrix4dc)transformation);
            particle.getOldTransformation().set((Matrix4dc)particle.getTransformation());
            particle.models.get((int)0).texture = model.getParticleIcon();
            if (element.faces.values().size() > 0 && !excludeBlockPhysicsTexture.contains(state.getBlock())) {
                TextureAtlasSprite sprite;
                Material material = unbakedModel.model.getMaterial(((BlockElementFace)element.faces.values().iterator().next()).texture());
                particle.models.get((int)0).texture = sprite = Minecraft.getInstance().getModelManager().getAtlas(material.atlasLocation()).getSprite(material.texture());
            }
            if (particle.models.get((int)0).texture != null) {
                particle.models.get((int)0).textureID = Minecraft.getInstance().getTextureManager().getTexture(particle.models.get((int)0).texture.atlasLocation()).getId();
            }
            if ((color = Minecraft.getInstance().getBlockColors().getColor(update.state, (BlockAndTintGetter)level, update.pos, 0)) == -1) {
                color = -1;
            }
            particle.setColor(color);
            if (update.state.getBlock() == Blocks.CAULDRON || update.state.getBlock() == Blocks.GRASS_BLOCK) {
                particle.setColor(-1);
            }
            particles.add(particle);
        }
    }

    private Matrix4f getElementRotation(BlockElementRotation blockElementRotation) {
        Axis rotationAxis = Axis.YP;
        switch (blockElementRotation.axis()) {
            case X: {
                rotationAxis = Axis.XP;
                break;
            }
            case Y: {
                rotationAxis = Axis.YP;
                break;
            }
            case Z: {
                rotationAxis = Axis.ZP;
            }
        }
        return new Matrix4f().rotation((Quaternionfc)rotationAxis.rotationDegrees(blockElementRotation.angle()));
    }

    private double calculateChance(int count) {
        double chance = 1.0;
        if ((double)count > (double)ConfigClient.maxPhysicsObjects * 0.4) {
            chance = 0.3;
            if ((double)count > (double)ConfigClient.maxPhysicsObjects * 0.7) {
                chance = 0.05;
            }
        }
        if (count > ConfigClient.maxPhysicsObjects) {
            chance = 0.0;
        }
        return chance;
    }

    private void spawnBlockBreakParticles(Level level, BlockState state, BlockPos pos, double spawnRate) {
        VoxelShape voxelShape = state.getShape((BlockGetter)level, pos);
        voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double width = java.lang.Math.min(1.0, maxX - minX);
            double height = java.lang.Math.min(1.0, maxY - minY);
            double depth = java.lang.Math.min(1.0, maxZ - minZ);
            int stepX = java.lang.Math.max(2, Mth.ceil((double)(width / 0.25)));
            int stepY = java.lang.Math.max(2, Mth.ceil((double)(height / 0.25)));
            int stepZ = java.lang.Math.max(2, Mth.ceil((double)(depth / 0.25)));
            for (int xp = 0; xp < stepX; ++xp) {
                for (int yp = 0; yp < stepY; ++yp) {
                    for (int zp = 0; zp < stepZ; ++zp) {
                        if ((double)Math.random() > spawnRate) continue;
                        double xSpeed = ((double)xp + 0.5) / (double)stepX;
                        double ySpeed = ((double)yp + 0.5) / (double)stepY;
                        double zSpeed = ((double)zp + 0.5) / (double)stepZ;
                        double xPos = xSpeed * width + minX;
                        double yPos = ySpeed * height + minY;
                        double zPos = zSpeed * depth + minZ;
                        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
                        PhysicsMod mod = PhysicsMod.getInstance(level);
                        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.BLOCK, state);
                        entity.getTransformation().translation((double)pos.getX() + xPos, (double)pos.getY() + yPos, (double)pos.getZ() + zPos);
                        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
                        Model model = entity.models.get(0);
                        model.texture = sprite;
                        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
                        entity.scale = (float)((double)entity.scale * ((double)Math.random() * 0.06 + 0.07));
                        entity.backfaceCulling = true;
                        model.mesh = PhysicsMod.brokenBlock.get(0);
                        int color = Minecraft.getInstance().getBlockColors().getColor(state, (BlockAndTintGetter)level, pos, 0);
                        if (color == -1) {
                            color = -1;
                        }
                        entity.setColor(color);
                        if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
                            entity.setColor(-1);
                        }
                        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
                        mod.physicsWorld.queue(() -> {
                            PxRigidActor patt0$temp = body.getRigidBody();
                            if (patt0$temp instanceof PxRigidDynamic) {
                                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                                float strength = 3.0f;
                                Vector3f speed = new Vector3f(0.0f, 0.2f, 0.0f);
                                speed.x += (Math.random() - 0.5f) * 0.4f;
                                speed.y += (Math.random() - 0.5f) * 0.4f;
                                speed.z += (Math.random() - 0.5f) * 0.4f;
                                speed.normalize();
                                try (MemoryStack mem = MemoryStack.stackPush();){
                                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                                    rigidBody.setLinearVelocity(velocity);
                                }
                            }
                        });
                        float uo = Math.random() * 3.0f;
                        float vo = Math.random() * 3.0f;
                        Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0f), sprite.getU((uo + 1.0f) / 4.0f), sprite.getV(vo / 4.0f), sprite.getV((vo + 1.0f) / 4.0f));
                        float xScale = customUVs.y - customUVs.x;
                        float yScale = customUVs.w - customUVs.z;
                        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0f).scale(xScale, yScale, 0.0f);
                    }
                }
            }
        });
    }

    static {
        excludeBlockPhysicsTexture.add(Blocks.ACACIA_LOG);
        excludeBlockPhysicsTexture.add(Blocks.OAK_LOG);
        excludeBlockPhysicsTexture.add(Blocks.BIRCH_LOG);
        excludeBlockPhysicsTexture.add(Blocks.JUNGLE_LOG);
        excludeBlockPhysicsTexture.add(Blocks.DARK_OAK_LOG);
        excludeBlockPhysicsTexture.add(Blocks.SPRUCE_LOG);
    }
}

