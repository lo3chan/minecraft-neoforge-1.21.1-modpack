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
import net.diebuddies.physics.liquid.LiquidController;
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
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
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
               long sectionIndex = SectionPos.blockToSection(blockIndex);
               int lx = SectionPos.sectionRelative(BlockPos.getX(blockIndex));
               int ly = SectionPos.sectionRelative(BlockPos.getY(blockIndex));
               int lz = SectionPos.sectionRelative(BlockPos.getZ(blockIndex));
               short localPos = (short)(lx << 8 | ly << 4 | lz);
               if (ConfigClient.areSnowPhysicsEnabled()) {
                  physics.getSnowWorld().getLightUpdates(sectionIndex).add(localPos);
               }

               if (ConfigClient.areOceanPhysicsEnabled()) {
                  physics.getOceanWorld().getLightUpdates(sectionIndex).add(localPos);
               }
            }
         }

         if (!StarterClient.disableLightingCache) {
            for (IRigidBody body : physics.getBodies()) {
               PhysicsEntity entity = body.getEntity();
               MutableBlockPos cached = entity.getCachedBrightnessPos();
               if (cached != null) {
                  long pos = cached.asLong();
                  if (mod.updatedLightBlocks.contains(pos)) {
                     entity.invalidateBrightness();
                  }
               }
            }
         }

         WeatherEffects.invalidateLight = true;
         mod.updatedLightBlocks.clear();
      }

      mod.removeUpdates.clear();

      for (int i = mod.updateQueue.size() - 1; i >= 0; i--) {
         BlockUpdate blockUpdate = mod.updateQueue.get(i);
         if (mod.fallingBlocks.isEmpty() || !mod.fallingBlocks.contains(blockUpdate.pos)) {
            mod.removeUpdates.add(blockUpdate);
         }
      }

      mod.fallingBlocks.clear();
      mod.updateQueue.clear();
      List<PhysicsEntity> newParts = new ObjectArrayList();
      List<PhysicsEntity> newPartsVoxel = new ObjectArrayList();
      double maxActivationDistanceSqr = ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange;
      BlockEntityRenderDispatcher berd = Minecraft.getInstance().getBlockEntityRenderDispatcher();

      for (BlockUpdate bu : mod.removeUpdates) {
         BlockEntityRenderer<BlockEntity> renderer;
         if (bu.blockEntity != null && (renderer = berd.getRenderer(bu.blockEntity)) != null) {
            BlockSetting blockSetting = ConfigBlocks.getBlockSetting(bu.state.getBlock());
            if (cameraPos.distanceToSqr(bu.pos.getX(), bu.pos.getY(), bu.pos.getZ()) < maxActivationDistanceSqr || ConfigClient.blockPhysicsRange > 319.999) {
               if (blockSetting.getType() == BlockPhysicsType.FRACTURED
                  || blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL
                  || blockSetting.getType() == BlockPhysicsType.BLOCKY) {
                  PhysicsEntity entity = mod.renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, renderer, bu.blockEntity, bu.state, bu.pos);
                  if (entity != null) {
                     physics.addBlockParticle(entity).applyRandomSpawnForces();
                  }

                  if (blockSetting.getType() == BlockPhysicsType.FRACTURED) {
                     newParts.addAll(this.getBlockData(physics, bu, bu.level));
                  } else if (blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL) {
                     newPartsVoxel.addAll(this.getBlockData(physics, bu, bu.level));
                  } else if (blockSetting.getType() == BlockPhysicsType.BLOCKY) {
                     PhysicsEntity blocky = mod.renderBlockIntoEntity(bu.level, PhysicsEntity.Type.BLOCK, bu.state, bu.pos, false);
                     if (blocky != null) {
                        physics.addBlockParticle(blocky).applyRandomSpawnForces();
                     }
                  }
               } else if (blockSetting.getType() == BlockPhysicsType.PARTICLES) {
                  double percent = this.calculateChance(physics.getBodies().size());
                  if (mod.removeUpdates.size() > 8) {
                     percent = Math.min(percent, 0.1);
                  } else if (mod.removeUpdates.size() == 1) {
                     percent = Math.max(0.1, percent);
                  }

                  this.spawnBlockBreakParticles(bu.level, bu.state, bu.pos, percent);
               }
            }

            if (ConfigClient.liquidPhysics && bu.state.getBlock() == Blocks.ENCHANTING_TABLE && !ConfigClient.cudaLiquids()) {
               physics.addLiquid(new Liquid(new WaterController(bu.pos, ConfigClient.waterDensity, true, 2)));
            }
         } else if (bu.state.getBlock() != Blocks.TNT && bu.state.getBlock() != Blocks.PISTON_HEAD && bu.state.getRenderShape() != RenderShape.INVISIBLE) {
            if (ConfigClient.areDynamicBlockPhysicsEnabled()) {
               DynamicSetting setting = VineHelper.getSetting(bu.state);
               if (setting != null && setting.linkedPhysics) {
                  continue;
               }
            }

            if (ConfigClient.liquidPhysics) {
               LiquidController controller = null;
               if (bu.state.getBlock() == Blocks.MELON) {
                  controller = new WaterController(bu.pos, 4, false, 1);
               } else if (bu.state.getBlock() == Blocks.WET_SPONGE) {
                  controller = new WaterController(bu.pos, 4, false, 3);
               } else if (bu.state.getBlock() == Blocks.PUMPKIN) {
                  controller = new WaterController(bu.pos, 4, false, 1);
               } else if (bu.state.getBlock() == Blocks.CACTUS) {
                  controller = new WaterController(bu.pos, 4, false, 1);
               } else if (bu.state.getBlock() == Blocks.WATER_CAULDRON) {
                  int fillLevel = (Integer)bu.state.getValue(BlockStateProperties.LEVEL_CAULDRON);
                  controller = new WaterController(bu.pos, 4, false, fillLevel);
               } else if (bu.state.getBlock() == Blocks.ENCHANTING_TABLE) {
                  controller = new WaterController(bu.pos, ConfigClient.waterDensity, true, 2);
               }

               if (controller != null && !ConfigClient.cudaLiquids()) {
                  physics.addLiquid(new Liquid(controller));
               }
            }

            BlockSetting blockSettingx = ConfigBlocks.getBlockSetting(bu.state.getBlock());
            if (cameraPos.distanceToSqr(bu.pos.getX(), bu.pos.getY(), bu.pos.getZ()) < maxActivationDistanceSqr || ConfigClient.blockPhysicsRange > 319.999) {
               if (blockSettingx.getType() == BlockPhysicsType.FRACTURED) {
                  newParts.addAll(this.getBlockData(physics, bu, bu.level));
               } else if (blockSettingx.getType() == BlockPhysicsType.FRACTURED_VOXEL) {
                  newPartsVoxel.addAll(this.getBlockData(physics, bu, bu.level));
               } else if (blockSettingx.getType() == BlockPhysicsType.BLOCKY) {
                  PhysicsEntity entityx = mod.renderBlockIntoEntity(bu.level, PhysicsEntity.Type.BLOCK, bu.state, bu.pos, false);
                  if (entityx != null) {
                     physics.addBlockParticle(entityx).applyRandomSpawnForces();
                  }
               } else if (blockSettingx.getType() == BlockPhysicsType.PARTICLES) {
                  double percent = this.calculateChance(physics.getBodies().size());
                  if (mod.removeUpdates.size() > 8) {
                     percent = Math.min(percent, 0.1);
                  } else if (mod.removeUpdates.size() == 1) {
                     percent = Math.max(0.1, percent);
                  }

                  this.spawnBlockBreakParticles(bu.level, bu.state, bu.pos, percent);
               }
            }
         }
      }

      double chance = this.calculateChance(physics.getBodies().size());
      int qsize = newParts.size() + newPartsVoxel.size();
      if (qsize == 1) {
         chance = 1.0;
      } else if (qsize > 10) {
         chance = Math.min(chance, 0.3);
      }

      this.addPhysicsBlocks(newParts, chance, physics, false);
      this.addPhysicsBlocks(newPartsVoxel, chance, physics, true);

      while (!mod.entityBlocks.isEmpty()) {
         PhysicsEntity particle = mod.entityBlocks.poll();
         if (!particle.noVolume) {
            physics.addBlockParticle(particle).applyRandomSpawnForces();
         }
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
         double volume = particle.getVolume();
         List<Mesh> mesh = PhysicsMod.brokenBlock;
         List<Mesh> physicsMesh = null;
         if (net.diebuddies.math.Math.random() < chance) {
            if (!(chance < 0.5) && !(physics.getBodies().size() > ConfigClient.maxPhysicsObjects * 0.4)) {
               int index = net.diebuddies.math.Math.randomInt(PhysicsMod.brokenBlocksLots.size());
               if (voxel) {
                  mesh = PhysicsMod.brokenBlocksLotsVoxel.get(index);
                  physicsMesh = PhysicsMod.brokenBlocksLots.get(index);
               } else {
                  mesh = PhysicsMod.brokenBlocksLots.get(index);
               }
            } else {
               int index = net.diebuddies.math.Math.randomInt(PhysicsMod.brokenBlocksLittle.size());
               if (voxel) {
                  mesh = PhysicsMod.brokenBlocksLittleVoxel.get(index);
                  physicsMesh = PhysicsMod.brokenBlocksLittle.get(index);
               } else {
                  mesh = PhysicsMod.brokenBlocksLittle.get(index);
               }
            }

            if (volume < 0.05) {
               mesh = PhysicsMod.brokenBlock;
            } else if (volume < 0.9) {
               int indexx = net.diebuddies.math.Math.randomInt(PhysicsMod.brokenBlocksLittle.size());
               if (voxel) {
                  mesh = PhysicsMod.brokenBlocksLittleVoxel.get(indexx);
                  physicsMesh = PhysicsMod.brokenBlocksLittle.get(indexx);
               } else {
                  mesh = PhysicsMod.brokenBlocksLittle.get(indexx);
               }
            }

            physics.addBlockParticle(mesh, physicsMesh, particle);
         }
      }
   }

   private List<PhysicsEntity> getBlockData(PhysicsWorld physics, BlockUpdate update, Level level) {
      List<PhysicsEntity> particles = new ObjectArrayList();
      BlockPos pos = update.pos;
      BlockState state = update.state;
      ModelResourceLocation id = BlockModelShaper.stateToModelLocation(state);
      BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(id);
      if (state.getBlock() == Blocks.BROWN_MUSHROOM_BLOCK || state.getBlock() == Blocks.RED_MUSHROOM_BLOCK || state.getBlock() == Blocks.MUSHROOM_STEM) {
         bakedModel = null;
      }

      if (bakedModel instanceof MultiPartBakedModel multi) {
         BitSet bitSet = (BitSet)multi.selectorCache.get(state);
         if (bitSet == null) {
            bitSet = new BitSet();

            for (int i = 0; i < multi.selectors.size(); i++) {
               Pair<Predicate<BlockState>, BakedModel> pair = (Pair<Predicate<BlockState>, BakedModel>)multi.selectors.get(i);
               if (((Predicate)pair.getLeft()).test(state)) {
                  bitSet.set(i);
               }
            }

            multi.selectorCache.put(state, bitSet);
         }

         for (int j = 0; j < bitSet.length(); j++) {
            if (bitSet.get(j)) {
               BakedModel model = (BakedModel)((Pair)multi.selectors.get(j)).getRight();
               JsonUnbakedModelHolder unbakedModel = PhysicsMod.loadedModels.get(model);
               if (unbakedModel != null) {
                  this.addParticles(particles, unbakedModel, level, update);
               } else {
                  PhysicsEntity entity = PhysicsMod.getInstance(level).renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, model, update.state, update.pos, false);
                  if (entity != null) {
                     physics.addBlockParticle(entity).applyRandomSpawnForces();
                  }
               }
            }
         }
      } else {
         JsonUnbakedModelHolder unbakedModel = bakedModel == null ? null : PhysicsMod.loadedModels.get(bakedModel);
         if (unbakedModel != null && unbakedModel.model instanceof BlockModel) {
            this.addParticles(particles, unbakedModel, level, update);
            if (particles.size() == 0 && bakedModel != null) {
               PhysicsEntity entity = PhysicsMod.getInstance(level)
                  .renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, bakedModel, update.state, update.pos, false);
               if (entity != null) {
                  physics.addBlockParticle(entity).applyRandomSpawnForces();
               }
            }
         } else {
            PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.BLOCK, update.state);
            Minecraft minecraft = Minecraft.getInstance();
            BlockRenderDispatcher ren = minecraft.getBlockRenderer();
            BakedModel model = ren.getBlockModel(state);
            Vec3 blockOffset = update.state.getOffset(update.level, update.pos);
            particle.getTransformation().translation(pos.getX() + 0.5 + blockOffset.x, pos.getY() + 0.5 + blockOffset.y, pos.getZ() + 0.5 + blockOffset.z);
            particle.getOldTransformation().set(particle.getTransformation());
            particle.models.get(0).texture = model.getParticleIcon();
            particle.models.get(0).textureID = Minecraft.getInstance().getTextureManager().getTexture(model.getParticleIcon().atlasLocation()).getId();
            int color = Minecraft.getInstance().getBlockColors().getColor(update.state, level, update.pos, 0);
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
      Vec3 blockOffset = state.getOffset(update.level, pos);

      for (BlockElement element : unbakedModel.model.getElements()) {
         PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.BLOCK, state);
         if (element.from.x != 0.0F
            || element.from.y != 0.0F
            || element.from.z != 0.0F
            || element.to.x != 16.0F
            || element.to.y != 16.0F
            || element.to.z != 16.0F) {
            particle.rescale = new AABBf(
               new Vector3f(element.from.x() / 16.0F, element.from.y() / 16.0F, element.from.z() / 16.0F),
               new Vector3f(element.to.x() / 16.0F, element.to.y() / 16.0F, element.to.z() / 16.0F)
            );
         }

         particle.shade = element.shade;
         Minecraft minecraft = Minecraft.getInstance();
         BlockRenderDispatcher ren = minecraft.getBlockRenderer();
         BakedModel model = ren.getBlockModel(state);
         Matrix4f m = unbakedModel.transformation;
         Matrix4d modelTransformation = new Matrix4d();
         modelTransformation.set(m);
         Matrix4d transformation = new Matrix4d();
         transformation.mul(modelTransformation);
         if (element.rotation != null) {
            transformation.translate(element.rotation.origin().x() - 0.5, element.rotation.origin().y() - 0.5, element.rotation.origin().z() - 0.5);
            transformation.mul(this.tmpMatrix.set(this.getElementRotation(element.rotation)));
            transformation.translate(-(element.rotation.origin().x() - 0.5), -(element.rotation.origin().y() - 0.5), -(element.rotation.origin().z() - 0.5));
         }

         transformation.m30(transformation.m30() + pos.getX() + 0.5 + blockOffset.x);
         transformation.m31(transformation.m31() + pos.getY() + 0.5 + blockOffset.y);
         transformation.m32(transformation.m32() + pos.getZ() + 0.5 + blockOffset.z);
         particle.getTransformation().set(transformation);
         particle.getOldTransformation().set(particle.getTransformation());
         particle.models.get(0).texture = model.getParticleIcon();
         if (element.faces.values().size() > 0 && !excludeBlockPhysicsTexture.contains(state.getBlock())) {
            Material material = unbakedModel.model.getMaterial(((BlockElementFace)element.faces.values().iterator().next()).texture());
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(material.atlasLocation()).getSprite(material.texture());
            particle.models.get(0).texture = sprite;
         }

         if (particle.models.get(0).texture != null) {
            particle.models.get(0).textureID = Minecraft.getInstance().getTextureManager().getTexture(particle.models.get(0).texture.atlasLocation()).getId();
         }

         int color = Minecraft.getInstance().getBlockColors().getColor(update.state, level, update.pos, 0);
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

   private Matrix4f getElementRotation(BlockElementRotation blockElementRotation) {
      Axis rotationAxis = Axis.YP;
      switch (blockElementRotation.axis()) {
         case X:
            rotationAxis = Axis.XP;
            break;
         case Y:
            rotationAxis = Axis.YP;
            break;
         case Z:
            rotationAxis = Axis.ZP;
      }

      return new Matrix4f().rotation(rotationAxis.rotationDegrees(blockElementRotation.angle()));
   }

   private double calculateChance(int count) {
      double chance = 1.0;
      if (count > ConfigClient.maxPhysicsObjects * 0.4) {
         chance = 0.3;
         if (count > ConfigClient.maxPhysicsObjects * 0.7) {
            chance = 0.05;
         }
      }

      if (count > ConfigClient.maxPhysicsObjects) {
         chance = 0.0;
      }

      return chance;
   }

   private void spawnBlockBreakParticles(Level level, BlockState state, BlockPos pos, double spawnRate) {
      VoxelShape voxelShape = state.getShape(level, pos);
      voxelShape.forAllBoxes(
         (minX, minY, minZ, maxX, maxY, maxZ) -> {
            double width = Math.min(1.0, maxX - minX);
            double height = Math.min(1.0, maxY - minY);
            double depth = Math.min(1.0, maxZ - minZ);
            int stepX = Math.max(2, Mth.ceil(width / 0.25));
            int stepY = Math.max(2, Mth.ceil(height / 0.25));
            int stepZ = Math.max(2, Mth.ceil(depth / 0.25));

            for (int xp = 0; xp < stepX; xp++) {
               for (int yp = 0; yp < stepY; yp++) {
                  for (int zp = 0; zp < stepZ; zp++) {
                     if (!(net.diebuddies.math.Math.random() > spawnRate)) {
                        double xSpeed = (xp + 0.5) / stepX;
                        double ySpeed = (yp + 0.5) / stepY;
                        double zSpeed = (zp + 0.5) / stepZ;
                        double xPos = xSpeed * width + minX;
                        double yPos = ySpeed * height + minY;
                        double zPos = zSpeed * depth + minZ;
                        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
                        PhysicsMod mod = PhysicsMod.getInstance(level);
                        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.BLOCK, state);
                        entity.getTransformation().translation(pos.getX() + xPos, pos.getY() + yPos, pos.getZ() + zPos);
                        entity.getOldTransformation().set(entity.getTransformation());
                        Model model = entity.models.get(0);
                        model.texture = sprite;
                        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
                        entity.scale = (float)(entity.scale * (net.diebuddies.math.Math.random() * 0.06 + 0.07));
                        entity.backfaceCulling = true;
                        model.mesh = PhysicsMod.brokenBlock.get(0);
                        int color = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
                        if (color == -1) {
                           color = -1;
                        }

                        entity.setColor(color);
                        if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
                           entity.setColor(-1);
                        }

                        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
                        mod.physicsWorld.queue(() -> {
                           if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                              float strength = 3.0F;
                              Vector3f speed = new Vector3f(0.0F, 0.2F, 0.0F);
                              speed.x = speed.x + (net.diebuddies.math.Math.random() - 0.5F) * 0.4F;
                              speed.y = speed.y + (net.diebuddies.math.Math.random() - 0.5F) * 0.4F;
                              speed.z = speed.z + (net.diebuddies.math.Math.random() - 0.5F) * 0.4F;
                              speed.normalize();
                              MemoryStack mem = MemoryStack.stackPush();

                              try {
                                 PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                                 rigidBody.setLinearVelocity(velocity);
                              } catch (Throwable var8) {
                                 if (mem != null) {
                                    try {
                                       mem.close();
                                    } catch (Throwable var7x) {
                                       var8.addSuppressed(var7x);
                                    }
                                 }

                                 throw var8;
                              }

                              if (mem != null) {
                                 mem.close();
                              }
                           }
                        });
                        float uo = net.diebuddies.math.Math.random() * 3.0F;
                        float vo = net.diebuddies.math.Math.random() * 3.0F;
                        Vector4f customUVs = new Vector4f(
                           sprite.getU(uo / 4.0F), sprite.getU((uo + 1.0F) / 4.0F), sprite.getV(vo / 4.0F), sprite.getV((vo + 1.0F) / 4.0F)
                        );
                        float xScale = customUVs.y - customUVs.x;
                        float yScale = customUVs.w - customUVs.z;
                        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0F).scale(xScale, yScale, 0.0F);
                     }
                  }
               }
            }
         }
      );
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
