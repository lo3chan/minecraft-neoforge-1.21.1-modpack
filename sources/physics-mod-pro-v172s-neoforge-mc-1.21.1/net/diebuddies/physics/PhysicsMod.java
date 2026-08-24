package net.diebuddies.physics;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import net.diebuddies.bridge.ModLoaderFunctions;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Replay;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.RayIntersection;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.model.ColladaParser;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollMapper;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.ClothRules;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.vines.DynamicLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;

public class PhysicsMod {
   public static final Path CLOTH_DIRECTORY = ModLoaderFunctions.getGameDir().resolve("cloth_local");
   public static final Path CLOTH_SYNCHRONIZED_DIRECTORY = ModLoaderFunctions.getGameDir().resolve(".physics_mod_cache/cloth");
   public static final ResourceLocation SNOWBALL_TEXTURE = ResourceLocation.parse("physicsmod:textures/items/snowball.png");
   public static final ResourceLocation ENDERPEARL_TEXTURE = ResourceLocation.parse("physicsmod:textures/items/enderpearl.png");
   public static final ResourceLocation EGG_TEXTURE = ResourceLocation.parse("physicsmod:textures/items/egg.png");
   public static final ResourceLocation SMOKE_TEXTURE = ResourceLocation.parse("physicsmod:textures/smoke/smoke.png");
   public static final ResourceLocation PUDDLE_TEXTURE = ResourceLocation.parse("physicsmod:textures/ocean/puddle.png");
   private static Vector3f shaderLight0 = new Vector3f();
   private static Vector3f shaderLight1 = new Vector3f();
   public static Object2ObjectMap<Level, PhysicsMod> instances = new Object2ObjectOpenHashMap();
   private static PhysicsMod currentInstance;
   public static final Map<EntityType<?>, EntityRenderer<?>> renderers = new Object2ObjectOpenHashMap();
   public static final Map<Block, String> registeredBlocks = new Object2ObjectOpenHashMap();
   public static final Map<String, Block> invRegisteredBlocks = new Object2ObjectOpenHashMap();
   public static final Map<String, ParticleOptions> registeredParticles = new Object2ObjectAVLTreeMap();
   public static final Map<ParticleOptions, String> invRegisteredParticles = new Object2ObjectOpenHashMap();
   public static final Map<String, SoundEvent> registeredSounds = new Object2ObjectAVLTreeMap();
   public static final Map<SoundEvent, String> invRegisteredSounds = new Object2ObjectOpenHashMap();
   public static boolean hudRendering;
   public static boolean sodiumCatch;
   public static boolean sodiumCatchBoundingBox;
   public static AABB3D sodiumBoundingBox = new AABB3D(new Vector3d(), new Vector3d());
   public static boolean clothSmootShadingIrisFix;
   public static Map<BakedModel, JsonUnbakedModelHolder> loadedModels = new ConcurrentHashMap<>();
   public static Matrix4f projectionMatrix = new Matrix4f();
   public static Matrix4f viewMatrix = new Matrix4f();
   public static List<VerletSimulation> optifineClothCompat = new ObjectArrayList();
   public static List<ClothRenderCommand> clothRenderFast = new ObjectArrayList();
   public static boolean reloadCloth;
   public static Matrix4f itemBreakTransformation;
   public PhysicsWorld physicsWorld;
   public boolean init = false;
   public ConcurrentLinkedQueue<PhysicsEntity> entityBlocks = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<BlockPos> blockUpdates = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<Explosion> explosions = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<Ragdoll> ragdolls = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<Ragdoll> sodiumRemoveRagdolls = new ConcurrentLinkedQueue<>();
   public Set<BlockPos> fallingBlocks = new ObjectOpenHashSet();
   public List<BlockUpdate> updateQueue = new ObjectArrayList();
   public List<PhysicsEntity> blockifiedEntity = new ObjectArrayList();
   public PhysicsEntity itemStackEntity;
   public Set<BlockUpdate> removeUpdates = new ObjectOpenHashSet();
   public Set<Integer> alreadyBlockified = new ObjectOpenHashSet();
   public LongSet updatedLightBlocks = new LongOpenHashSet();
   public PoseStack localPivotMatrix = new PoseStack();
   public EntityRenderer cubifyEntityRenderer;
   public Entity cubifyEntity;
   public long time;
   public boolean blockify;
   public Entity blockifyEntity;
   public int blockifyFeatureIndex;
   public RenderLayer blockifyFeature;
   public static final List<List<Mesh>> brokenBlocksLittle = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLots = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLittleVoxel = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLotsVoxel = new ObjectArrayList();
   public static final List<Mesh> brokenBlock;
   public static final List<Mesh> snowballMesh = new ObjectArrayList();
   public static final List<List<Mesh>> snowballMeshFractured = new ObjectArrayList();
   public static final List<Mesh> enderpearlMesh = new ObjectArrayList();
   public static final List<List<Mesh>> enderpearlMeshFractured = new ObjectArrayList();
   public static final List<Mesh> eggMesh = new ObjectArrayList();
   public static final List<List<Mesh>> eggMeshFractured = new ObjectArrayList();
   public static final Mesh smoke;
   public static final Mesh liquid;
   public static Cloth defaultCape;
   public static Texture whiteTexture;
   public static Texture blackTexture;
   public static Texture foamTexture;
   public static Map<String, Cloth> cloth;
   private static final Direction[] DIRECTIONS;

   private static List<Mesh> readBlock(String asset) {
      List<Mesh> meshes = new ObjectArrayList();
      List<Vector3i> indices = new ObjectArrayList();
      List<Vector3i> indicesQuads = new ObjectArrayList();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(asset)))) {
         String line = "";
         Mesh mesh = null;
         int ov = 0;
         int ot = 0;
         int on = 0;

         while ((line = reader.readLine()) != null) {
            if (line.startsWith("o")) {
               if (mesh != null) {
                  ov += mesh.positions.size();
                  ot += mesh.uvs.size();
                  on += mesh.normals.size();
                  mesh = unrollMeshIndices(mesh, indices, indicesQuads);
                  indices.clear();
                  indicesQuads.clear();
                  meshes.add(mesh);
               }

               mesh = new Mesh(false);
            } else if (line.startsWith("vt")) {
               String[] data = line.split(" ");
               mesh.uvs.add(new Vector2f(Float.parseFloat(data[1]), Float.parseFloat(data[2])));
            } else if (line.startsWith("vn")) {
               String[] data = line.split(" ");
               mesh.normals.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
            } else if (line.startsWith("v")) {
               String[] data = line.split(" ");
               mesh.positions.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
            } else if (line.startsWith("f")) {
               String[] data = line.split(" ");
               boolean quads = data.length == 5;

               for (int i = 1; i < data.length; i++) {
                  String[] idata = data[i].split("/");
                  if (quads) {
                     indicesQuads.add(new Vector3i(Integer.parseInt(idata[0]) - ov, Integer.parseInt(idata[1]) - ot, Integer.parseInt(idata[2]) - on));
                  } else {
                     indices.add(new Vector3i(Integer.parseInt(idata[0]) - ov, Integer.parseInt(idata[1]) - ot, Integer.parseInt(idata[2]) - on));
                  }
               }

               if (quads) {
                  int index = indicesQuads.size() - 4;
                  indices.add(indicesQuads.get(index));
                  indices.add(indicesQuads.get(index + 1));
                  indices.add(indicesQuads.get(index + 2));
                  indices.add(indicesQuads.get(index));
                  indices.add(indicesQuads.get(index + 2));
                  indices.add(indicesQuads.get(index + 3));
               }
            }
         }

         if (mesh != null) {
            mesh = unrollMeshIndices(mesh, indices, indicesQuads);
            meshes.add(mesh);
         }

         for (Mesh m : meshes) {
            m.calculateOffset(true);
            m.calculatePBRData(true);
         }
      } catch (IOException var16) {
         var16.printStackTrace();
      }

      return meshes;
   }

   private static Mesh unrollMeshIndices(Mesh mesh, List<Vector3i> indices, List<Vector3i> indicesQuads) {
      Mesh unrolledMesh = new Mesh(false);
      if (indicesQuads.size() > 0) {
         indices.clear();

         for (int i = 0; i < indicesQuads.size() / 4; i++) {
            int indexQuads = i * 6;
            unrolledMesh.indicesQuads.add(indexQuads);
            unrolledMesh.indicesQuads.add(indexQuads + 1);
            unrolledMesh.indicesQuads.add(indexQuads + 2);
            unrolledMesh.indicesQuads.add(indexQuads + 5);
            int indexTriangles = i * 4;
            indices.add(indicesQuads.get(indexTriangles));
            indices.add(indicesQuads.get(indexTriangles + 1));
            indices.add(indicesQuads.get(indexTriangles + 2));
            indices.add(indicesQuads.get(indexTriangles));
            indices.add(indicesQuads.get(indexTriangles + 2));
            indices.add(indicesQuads.get(indexTriangles + 3));
         }
      }

      for (int i = 0; i < indices.size(); i++) {
         Vector3i index = indices.get(i);
         int ipos = index.x - 1;
         int iuv = index.y - 1;
         int inormal = index.z - 1;
         unrolledMesh.positions.add(new Vector3f((Vector3fc)mesh.positions.get(ipos)));
         unrolledMesh.uvs.add(new Vector2f((Vector2fc)mesh.uvs.get(iuv)));
         unrolledMesh.normals.add(new Vector3f((Vector3fc)mesh.normals.get(inormal)));
         unrolledMesh.indices.add(i);
      }

      return unrolledMesh;
   }

   public static void createClothDirectory() {
      try {
         Files.createDirectories(CLOTH_DIRECTORY);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      try {
         copyResource("assets/physicsmod/cloth/Cloth.blend", CLOTH_DIRECTORY);
         copyResource("assets/physicsmod/cloth/Vanilla Cape.dae", CLOTH_DIRECTORY);
         copyResource("assets/physicsmod/cloth/TUTORIAL.txt", CLOTH_DIRECTORY);
      } catch (IOException var1) {
         var1.printStackTrace();
      }
   }

   public static void loadCloth() {
      if (whiteTexture == null) {
         whiteTexture = Texture.createColoredTexture((byte)-1, (byte)-1, (byte)-1, (byte)-1, Texture.FILTER_MINECRAFT_TEXTURE);
         blackTexture = Texture.createColoredTexture((byte)0, (byte)0, (byte)0, (byte)-1, Texture.FILTER_MINECRAFT_TEXTURE);
         foamTexture = Texture.load3DTexture("assets/physicsmod/textures/ocean/foam.dat", Texture.FILTER_LOAD_3D_TEXTURE, 256, 256, 16);
      }

      VAO.storePreviouslyBoundState();

      try {
         if (defaultCape == null) {
            ColladaMesh mesh = ColladaParser.loadStaticModel(new File(CLOTH_DIRECTORY + "/Vanilla Cape.dae"));
            mesh.flipUVs();
            defaultCape = new Cloth("Vanilla Cape", mesh, null, null, new ClothRules());
         }
      } catch (Exception var2) {
         System.err.println("Couldn't load default cape model");
         var2.printStackTrace();
      }

      if (cloth != null) {
         for (Cloth cape : cloth.values()) {
            cape.destroy();
         }
      }

      cloth = new Object2ObjectLinkedOpenHashMap();
      loadClothFromDirectory(CLOTH_DIRECTORY, true);
      loadClothFromDirectory(CLOTH_SYNCHRONIZED_DIRECTORY, false);
      VAO.restorePreviouslyBoundState();
   }

   private static void loadClothFromDirectory(Path clothDirectory, boolean local) {
      try {
         Collection<Path> files = Files.list(clothDirectory).filter(PhysicsMod::isValidCloth).collect(Collectors.toList());
         List<Cloth> sortedCloth = new ObjectArrayList();

         for (Path path : files) {
            File model = path.toFile();
            String name = path.getFileName().toString();
            if (name.contains(".")) {
               name = name.substring(0, name.lastIndexOf(46));
            }

            String pathNoExtension = path.toFile().getAbsolutePath().substring(0, path.toFile().getAbsolutePath().length() - 3);
            String texture = pathNoExtension.concat("png");
            File rules = new File(pathNoExtension.concat("rules"));

            try {
               Map<String, ColladaMesh> meshes = ColladaParser.loadMultipleStaticModel(model);
               ColladaMesh playerMesh = meshes.remove("Player");
               ColladaMesh clothMesh = meshes.values().iterator().next();
               sortedCloth.add(new Cloth(name, clothMesh, playerMesh, Texture.load(texture, Texture.FILTER_MINECRAFT_TEXTURE), ClothRules.load(rules, local)));
            } catch (Exception var14) {
               System.err.println("Couldn't load " + model.toString());
               var14.printStackTrace();
            }
         }

         Collections.sort(sortedCloth);

         for (Cloth cloth : sortedCloth) {
            PhysicsMod.cloth.put(cloth.name, cloth);
         }
      } catch (IOException var15) {
         var15.printStackTrace();
      }
   }

   private static boolean isValidCloth(Path file) {
      try {
         if (file.toFile().getName().endsWith("dae")
            && new File(file.toFile().getAbsolutePath().substring(0, file.toFile().getAbsolutePath().length() - 3).concat("png")).exists()) {
            return true;
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      return false;
   }

   public static void copyResource(String res, Path dest) throws IOException {
      String[] split = res.split("/");

      try (InputStream src = PhysicsMod.class.getClassLoader().getResourceAsStream(res)) {
         Files.copy(src, dest.resolve(split[split.length - 1]), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception var8) {
      }
   }

   public static void copyClothResource(String res, Path dest) throws IOException {
      copyResource(res + ".dae", dest);
      copyResource(res + ".png", dest);
      copyResource(res + ".rules", dest);
   }

   public static void resetClothSimulations() {
      ObjectIterator var0 = instances.values().iterator();

      while (var0.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var0.next();

         for (VerletSimulation simulation : mod.getPhysicsWorld().getVerletSimulations()) {
            simulation.destroyed = true;
         }
      }
   }

   public static double getPlaybackSpeed() {
      if (Minecraft.getInstance().isPaused()) {
         return 0.0;
      } else {
         return StarterClient.replay ? ConfigClient.playbackSpeed * Replay.getPlaybackSpeed() : ConfigClient.playbackSpeed;
      }
   }

   public static PhysicsMod getInstance(Level level) {
      if (!(level instanceof ClientLevel)) {
         Thread.dumpStack();
      }

      PhysicsMod mod = (PhysicsMod)instances.get(level);
      if (mod == null) {
         mod = new PhysicsMod(level);
         instances.put(level, mod);
      }

      return mod;
   }

   public static PhysicsMod getCurrentInstance() {
      return currentInstance;
   }

   public static void setCurrentInstance(PhysicsMod currentInstance) {
      PhysicsMod.currentInstance = currentInstance;
   }

   public static Object2ObjectMap<Level, PhysicsMod> getInstances() {
      return instances;
   }

   public PhysicsMod(Level level) {
      this.physicsWorld = new PhysicsWorld(level);
      if (level instanceof ClientLevel) {
         ((DynamicLoader)((ClientLevel)level).getChunkSource()).setPhysicsMod(this);
      }
   }

   public PhysicsWorld getPhysicsWorld() {
      return this.physicsWorld;
   }

   public static void addSnowball(Level level, Snowball snowball) {
      if (ConfigClient.snowballImpact != 2) {
         addThrowableProjectile(
            level,
            snowball,
            snowballMesh.get(ConfigClient.snowballModel),
            Minecraft.getInstance().getTextureManager().getTexture(SNOWBALL_TEXTURE).getId(),
            ConfigClient.snowballShade,
            ConfigClient.snowballImpact == 0 ? snowballMeshFractured.get(ConfigClient.snowballModel) : null,
            ConfigClient.snowballModel == 0
         );
      }
   }

   public static void addEnderpearl(Level level, ThrownEnderpearl enderpearl) {
      if (ConfigClient.enderpearlImpact != 2) {
         addThrowableProjectile(
            level,
            enderpearl,
            enderpearlMesh.get(ConfigClient.enderpearlModel),
            Minecraft.getInstance().getTextureManager().getTexture(ENDERPEARL_TEXTURE).getId(),
            ConfigClient.enderpearlShade,
            ConfigClient.enderpearlImpact == 0 ? enderpearlMeshFractured.get(ConfigClient.enderpearlModel) : null,
            ConfigClient.enderpearlModel == 0
         );
      }
   }

   public static void addEgg(Level level, ThrownEgg egg) {
      if (ConfigClient.eggImpact != 2) {
         addThrowableProjectile(
            level,
            egg,
            eggMesh.get(ConfigClient.eggModel),
            Minecraft.getInstance().getTextureManager().getTexture(EGG_TEXTURE).getId(),
            ConfigClient.eggShade,
            ConfigClient.eggImpact == 0 ? eggMeshFractured.get(ConfigClient.eggModel) : null,
            ConfigClient.eggModel == 0
         );
      }
   }

   public static void addThrowableProjectile(
      Level level, ThrowableItemProjectile projectile, Mesh mesh, int textureID, boolean shade, List<Mesh> fractures, boolean fastBoxes
   ) {
      PhysicsMod mod = getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.ITEM, null);
      Vector3d snowballPos = new Vector3d(projectile.getX(), projectile.getY(), projectile.getZ());
      float snowballRadius = 0.14F;
      MutableBlockPos originPos = new MutableBlockPos();
      MutableBlockPos tmpPos = new MutableBlockPos();
      Vector3d rayDir = new Vector3d(projectile.getX() - projectile.xOld, projectile.getY() - projectile.yOld, projectile.getZ() - projectile.zOld)
         .normalize()
         .negate();
      Vector3d aabbMin = new Vector3d();
      Vector3d aabbMax = new Vector3d();
      Set<BlockPos> hitPositions = new ObjectOpenHashSet();

      for (int count = 0; count < 5; count++) {
         float threshold = snowballRadius;
         boolean hasHit = false;
         originPos.set(snowballPos.x, snowballPos.y, snowballPos.z);

         for (int x = -1; x <= 1 && !hasHit; x++) {
            for (int y = -1; y <= 1 && !hasHit; y++) {
               for (int z = -1; z <= 1 && !hasHit; z++) {
                  tmpPos.set(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                  BlockState state = level.getBlockState(tmpPos);
                  if (!state.isAir() && !hitPositions.contains(tmpPos)) {
                     VoxelShape voxelShape = state.getCollisionShape(level, projectile.blockPosition());
                     if (!voxelShape.isEmpty()) {
                        for (AABB aabb : voxelShape.toAabbs()) {
                           if (AABB3D.isInside(
                              aabb.minX - threshold,
                              aabb.minY - threshold,
                              aabb.minZ - threshold,
                              aabb.maxX + threshold,
                              aabb.maxY + threshold,
                              aabb.maxZ + threshold,
                              snowballPos.x,
                              snowballPos.y,
                              snowballPos.z
                           )) {
                              aabbMin.set(aabb.minX - threshold, aabb.minY - threshold, aabb.minZ - threshold);
                              aabbMax.set(aabb.maxX + threshold, aabb.maxY + threshold, aabb.maxZ + threshold);
                              RayIntersection.IntersectionResult result = RayIntersection.intersectAABB(snowballPos, rayDir, aabbMin, aabbMax);
                              if (result.hit) {
                                 hasHit = true;
                                 snowballPos.x = snowballPos.x + rayDir.x * result.fraction;
                                 snowballPos.y = snowballPos.y + rayDir.y * result.fraction;
                                 snowballPos.z = snowballPos.z + rayDir.z * result.fraction;
                                 hitPositions.add(new BlockPos(tmpPos));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (hasHit) {
            break;
         }
      }

      entity.getTransformation().translation(snowballPos);
      Random random = new Random(projectile.getId());
      float progress = projectile.tickCount;
      entity.getTransformation().rotateX(random.nextDouble() * 3.141592653589793);
      entity.getTransformation().rotateY(random.nextDouble() * 3.141592653589793);
      entity.getTransformation().rotateZ(random.nextDouble() * 3.141592653589793 + progress * 0.5);
      entity.getOldTransformation().set(entity.getTransformation());
      entity.models.get(0).textureID = textureID;
      entity.backfaceCulling = true;
      entity.shade = shade;
      entity.models.get(0).mesh = mesh;
      List<IRigidBody> bodies = new ObjectArrayList();
      if (fractures != null) {
         mod.physicsWorld.addBlockParticle(fractures, entity, null, bodies, true);
      } else {
         IRigidBody body = mod.physicsWorld.addPhysicsSphere(entity, snowballRadius);
         ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity((float)Math.toRadians(360.0));
         ((PxRigidDynamic)body.getRigidBody()).setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_SPECULATIVE_CCD, true);
         ((PxRigidDynamic)body.getRigidBody()).setLinearDamping(0.9F);
         ((PxRigidDynamic)body.getRigidBody()).setAngularDamping(0.9F);
         bodies.add(body);
      }

      double speedX = projectile.getX() - projectile.xOld;
      double speedY = projectile.getY() - projectile.yOld;
      double speedZ = projectile.getZ() - projectile.zOld;
      float speedMultiplier = 10.0F;

      for (IRigidBody body : bodies) {
         MemoryStack mem = MemoryStack.stackPush();

         try {
            ((PxRigidDynamic)body.getRigidBody())
               .setLinearVelocity(
                  PxVec3.createAt(mem, MemoryStack::nmalloc, (float)speedX * speedMultiplier, (float)speedY * speedMultiplier, (float)speedZ * speedMultiplier)
               );
         } catch (Throwable var33) {
            if (mem != null) {
               try {
                  mem.close();
               } catch (Throwable var32) {
                  var33.addSuppressed(var32);
               }
            }

            throw var33;
         }

         if (mem != null) {
            mem.close();
         }
      }
   }

   public static void blockifyEntity(Level level, LivingEntity entity) {
      PhysicsMod mod = getInstance(level);
      if (ConfigMobs.getMobSetting(entity).getType() != MobPhysicsType.OFF) {
         if (!mod.alreadyBlockified.contains(entity.getId()) || entity instanceof Player) {
            if (!entity.isInvisible()) {
               mod.alreadyBlockified.add(entity.getId());
               EntityRenderer entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
               EntityRenderer renderer = entityRenderer;
               EntityModel model = null;
               if (entityRenderer instanceof LivingEntityRenderer) {
                  model = ((LivingEntityRenderer)entityRenderer).getModel();
               }

               PoseStack stack = new PoseStack();
               stack.pushPose();
               mod.blockify = true;
               mod.localPivotMatrix = new PoseStack();
               mod.cubifyEntityRenderer = entityRenderer;
               mod.cubifyEntity = entity;
               setCurrentInstance(mod);
               mod.blockifyEntity = entity;
               mod.blockifyFeature = null;
               mod.blockifyFeatureIndex = 0;
               DummyMultiBufferSource source = new DummyMultiBufferSource();

               try {
                  renderer.render(entity, 0.0F, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), stack, source, 0);
               } catch (Exception var18) {
                  System.err.println("error rendering " + entity.getClass());
                  var18.printStackTrace();
               } finally {
                  if (source.lastLayer != null) {
                     source.lastLayer.clearRenderState();
                  }
               }

               setCurrentInstance(null);
               mod.blockify = false;

               try {
                  RagdollMapper.filterCuboidsFromEntities(entity, model);
               } catch (Exception var17) {
                  System.err.println("error filtering " + entity.getClass());
                  var17.printStackTrace();
               }

               stack.popPose();

               for (PhysicsEntity physicsEntity : mod.blockifiedEntity) {
                  physicsEntity.backfaceCulling = false;
               }

               MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
               if (type != MobPhysicsType.RAGDOLL && type != MobPhysicsType.RAGDOLL_BREAK && type != MobPhysicsType.RAGDOLL_BREAK_BLOOD) {
                  mod.entityBlocks.addAll(mod.blockifiedEntity);
                  if (mod.entityBlocks.size() > 0) {
                     entity.remove(RemovalReason.DISCARDED);
                     entity.deathTime = 20;
                     entity.hurtTime = 0;
                  }
               } else {
                  Ragdoll ragdoll = null;

                  try {
                     ragdoll = RagdollMapper.map(type, entity, model);
                  } catch (Exception var16) {
                     System.err.println("error creating ragdoll for " + entity.getClass());
                     var16.printStackTrace();
                  }

                  if (ragdoll == null) {
                     mod.entityBlocks.addAll(mod.blockifiedEntity);
                     if (mod.entityBlocks.size() > 0) {
                        entity.remove(RemovalReason.DISCARDED);
                        entity.deathTime = 20;
                        entity.hurtTime = 0;
                     }
                  } else {
                     mod.ragdolls.add(ragdoll);
                     if (level instanceof ClientLevel clientLevel) {
                        Player closest = clientLevel.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 8.0, false);
                        if (closest != null) {
                           ragdoll.velocity.set(entity.getX() - closest.getX(), 2.0, entity.getZ() - closest.getZ()).normalize().mul(5.0);
                        }
                     }

                     ragdoll.velocity.add(entity.getDeltaMovement().x * 10.0, entity.getDeltaMovement().y * 10.0, entity.getDeltaMovement().z * 10.0);
                     entity.remove(RemovalReason.DISCARDED);
                     entity.deathTime = 20;
                     entity.hurtTime = 0;
                  }
               }

               mod.blockifiedEntity.clear();
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
            }
         }
      }
   }

   public static void createParticlesFromCuboids(
      Pose stack,
      PoseStack local,
      List<Cube> cuboids,
      Entity entity,
      EntityRenderer renderer,
      RenderLayer feature,
      int overlay,
      float red,
      float green,
      float blue
   ) {
      Matrix4f m = stack.pose();
      Matrix4f localM = local.last().pose();
      Matrix3f localNM = local.last().normal();
      Matrix4d transformation = new Matrix4d();
      Matrix4d transformationLocal = new Matrix4d();
      transformation.set(m);
      transformationLocal.set(localM);
      transformation.mul(transformationLocal.invert(new Matrix4d()));
      if (transformation.isFinite()) {
         PhysicsMod mod = getInstance(entity.getCommandSenderWorld());
         int textureID = TextureHelper.getLoadedTextures();
         float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
         double px = Mth.lerp(partialTicks, entity.xo, entity.getX());
         double py = Mth.lerp(partialTicks, entity.yo, entity.getY());
         double pz = Mth.lerp(partialTicks, entity.zo, entity.getZ());
         transformation.setTranslation(px + transformation.m30(), py + transformation.m31(), pz + transformation.m32());
         Vector4f[] minMax = new Vector4f[6];
         Vector3f tmpNormal = new Vector3f();
         Vector4f tmpPos = new Vector4f();

         for (int i = 0; i < minMax.length; i++) {
            minMax[i] = new Vector4f();
         }

         MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
         Iterator var28 = cuboids.iterator();

         while (true) {
            Cube box;
            float minX;
            float minY;
            float minZ;
            float maxX;
            float maxY;
            float maxZ;
            int[] remap;
            float volume;
            boolean isBlocky;
            boolean noVolume;
            while (true) {
               if (!var28.hasNext()) {
                  return;
               }

               box = (Cube)var28.next();
               if (box.polygons.length >= 6) {
                  minX = box.polygons[0].vertices[2].pos.x();
                  minY = box.polygons[0].vertices[2].pos.y();
                  minZ = box.polygons[0].vertices[2].pos.z();
                  maxX = box.polygons[1].vertices[3].pos.x();
                  maxY = box.polygons[1].vertices[3].pos.y();
                  maxZ = box.polygons[1].vertices[3].pos.z();
                  remap = new int[]{5, 4, 3, 2, 1, 0};
                  volume = Math.abs(maxX - minX) / 16.0F * (Math.abs(maxY - minY) / 16.0F) * (Math.abs(maxZ - minZ) / 16.0F);
                  isBlocky = type == MobPhysicsType.BLOCKY
                     || type == MobPhysicsType.RAGDOLL
                     || type == MobPhysicsType.RAGDOLL_BREAK
                     || type == MobPhysicsType.RAGDOLL_BREAK_BLOOD;
                  noVolume = false;
                  if (!(volume <= 1.0E-4)) {
                     break;
                  }

                  if (isBlocky) {
                     noVolume = true;
                     break;
                  }
               }
            }

            boolean mirror = ((CubeExtension)box).isMirrored();
            List<Mesh> meshes = brokenBlocksLittle.get((int)(net.diebuddies.math.Math.random() * brokenBlocksLittle.size()));
            if (volume <= 0.04 || isBlocky) {
               meshes = brokenBlock;
            }

            for (int i = 0; i < box.polygons.length; i++) {
               float minU = 1.0F;
               float maxU = 0.0F;
               float minV = 1.0F;
               float maxV = 0.0F;

               for (Vertex vertex : box.polygons[i].vertices) {
                  if (vertex.u < minU) {
                     minU = vertex.u;
                  }

                  if (vertex.v < minV) {
                     minV = vertex.v;
                  }

                  if (vertex.u > maxU) {
                     maxU = vertex.u;
                  }

                  if (vertex.v > maxV) {
                     maxV = vertex.v;
                  }
               }

               minMax[i].set(minU, maxU, minV, maxV);
            }

            PhysicsEntity parent = null;

            for (Mesh mesh : meshes) {
               PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.MOB, entity.getType());
               particle.feature = feature;
               particle.noVolume = noVolume;
               particle.models.get(0).textureID = textureID;
               Mesh clone = new Mesh();
               particle.models.get(0).mesh = clone;
               particle.getTransformation().set(transformation);
               particle.getOldTransformation().set(particle.getTransformation());
               int count = 0;
               Vector3f offset = new Vector3f();

               for (int i = 0; i < mesh.indices.size(); i++) {
                  int index = mesh.indices.getInt(i);
                  byte sideIndex = mesh.sides.getByte(index);
                  Vector3f position = mesh.positions.get(index);
                  Vector2f uv = mesh.uvs.get(index);
                  Vector3f normal = mesh.normals.get(index);
                  float r = red;
                  float g = green;
                  float b = blue;
                  if (sideIndex == -1) {
                     if (type == MobPhysicsType.FRACTURED_BLOOD) {
                        r = 0.6F;
                        g = 0.0F;
                        b = 0.0F;
                     }

                     sideIndex = 0;
                  }

                  tmpNormal.set(mirror ? -normal.x : normal.x, normal.y, normal.z);
                  localNM.transform(tmpNormal);
                  Vector4f minMaxUVs = minMax[remap[sideIndex]];
                  tmpPos.set(
                     (float)net.diebuddies.math.Math.remap((double)(position.x + mesh.offset.x), -0.5, 0.5, (double)minX, (double)maxX) / 16.0F,
                     (float)net.diebuddies.math.Math.remap((double)(position.y + mesh.offset.y), -0.5, 0.5, (double)minY, (double)maxY) / 16.0F,
                     (float)net.diebuddies.math.Math.remap(
                           (double)(position.z + mesh.offset.z), mirror ? 0.5 : -0.5, mirror ? -0.5 : 0.5, (double)minZ, (double)maxZ
                        )
                        / 16.0F,
                     1.0F
                  );
                  localM.transform(tmpPos);
                  clone.indices.add(count);
                  offset.add(tmpPos.x(), tmpPos.y(), tmpPos.z());
                  count++;
                  Vector3f posR = new Vector3f(tmpPos.x(), tmpPos.y(), tmpPos.z());
                  clone.positions.add(posR);
                  clone.uvs
                     .add(
                        new Vector2f(
                           net.diebuddies.math.Math.remap(uv.x, 1.0F, 0.0F, minMaxUVs.x, minMaxUVs.y),
                           net.diebuddies.math.Math.remap(uv.y, 0.0F, 1.0F, minMaxUVs.z, minMaxUVs.w)
                        )
                     );
                  clone.normals.add(new Vector3f(tmpNormal.x(), tmpNormal.y(), tmpNormal.z()));
                  clone.addColor(r, g, b);
               }

               if (StarterClient.iris || StarterClient.optifabric) {
                  clone.calculatePBRData(false);
               }

               offset.div(clone.positions.size());

               for (Vector3f position : clone.positions) {
                  position.sub(offset);
               }

               clone.offset = offset;
               Vector3d ps = transformationLocal.getTranslation(new Vector3d());
               particle.pivot.set(ps);
               if (clone.positions.size() > 0) {
                  if (parent == null) {
                     parent = particle;
                     mod.blockifiedEntity.add(particle);
                  } else {
                     parent.children.add(particle);
                  }
               }
            }
         }
      }
   }

   public PhysicsEntity renderBlockIntoEntity(PhysicsEntity.Type type, BakedModel model, BlockState state, BlockPos pos, boolean bakeAO) {
      this.itemStackEntity = new PhysicsEntity(type, state);
      this.itemStackEntity.models.get(0).mesh = new Mesh();
      setCurrentInstance(this);
      Vec3 blockOffset = state.getOffset(this.physicsWorld.getWorld(), pos);

      label50: {
         Object var8;
         try {
            LegacyRandomSource random = new LegacyRandomSource(0L);
            this.renderFlat(this.itemStackEntity, this.physicsWorld.getWorld(), model, state, pos, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
            break label50;
         } catch (Exception var12) {
            var8 = null;
         } finally {
            setCurrentInstance(null);
         }

         return (PhysicsEntity)var8;
      }

      if (this.itemStackEntity.models.get(0).mesh.indices.size() < 9) {
         setCurrentInstance(null);
         return null;
      } else {
         this.itemStackEntity.models.get(0).mesh.calculateOffset();
         if (StarterClient.iris || StarterClient.optifabric) {
            this.itemStackEntity.models.get(0).mesh.calculatePBRData(false);
         }

         this.itemStackEntity.models.get(0).textureID = Minecraft.getInstance().getTextureManager().getTexture(model.getParticleIcon().atlasLocation()).getId();
         this.itemStackEntity
            .getTransformation()
            .set(new Matrix4d().translate(pos.getX() + blockOffset.x, pos.getY() + blockOffset.y, pos.getZ() + blockOffset.z));
         this.itemStackEntity.getOldTransformation().set(this.itemStackEntity.getTransformation());
         this.itemStackEntity.models.get(0).animationSprite = model.getParticleIcon();
         return this.itemStackEntity;
      }
   }

   public PhysicsEntity renderBlockIntoEntity(
      PhysicsEntity.Type type, BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, BlockState state, BlockPos pos, boolean destruction
   ) {
      PhysicsEntity physicsBlockEntity = new PhysicsEntity(type, state);
      physicsBlockEntity.backfaceCulling = false;
      physicsBlockEntity.models.clear();
      setCurrentInstance(this);
      sodiumCatch = true;
      float tickDelta = 0.0F;
      BlockEntityVertexConsumerProvider source = new BlockEntityVertexConsumerProvider(destruction);

      label86: {
         Object meshes;
         try {
            renderer.render(blockEntity, tickDelta, new PoseStack(), source, OverlayTexture.NO_OVERLAY, 0);
            break label86;
         } catch (Exception var17) {
            meshes = null;
         } finally {
            setCurrentInstance(null);
            if (source.getLastLayer() != null) {
               source.getLastLayer().clearRenderState();
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            sodiumCatch = false;
         }

         return (PhysicsEntity)meshes;
      }

      Vec3 blockOffset = state.getOffset(this.physicsWorld.getWorld(), pos);
      ObjectArrayList var19 = new ObjectArrayList();

      for (BlockEntityVertexConsumer consumer : source.getBakedRenderTypeModels().values()) {
         consumer.validateModel();
         Model model = consumer.getModel();
         if (model.mesh.indices.size() >= 6) {
            var19.add(model.mesh);
            if (StarterClient.iris || StarterClient.optifabric) {
               model.mesh.calculatePBRData(false);
            }

            physicsBlockEntity.models.add(model);
         }
      }

      if (physicsBlockEntity.models.size() == 0) {
         setCurrentInstance(null);
         return null;
      } else {
         Mesh.calculateMeshOffsets(var19, false);
         physicsBlockEntity.getTransformation()
            .set(new Matrix4d().translate(pos.getX() + blockOffset.x, pos.getY() + blockOffset.y, pos.getZ() + blockOffset.z));
         physicsBlockEntity.getOldTransformation().set(physicsBlockEntity.getTransformation());
         return physicsBlockEntity;
      }
   }

   public PhysicsEntity renderBlockIntoEntity(
      PhysicsEntity.Type type, BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, BlockState state, BlockPos pos
   ) {
      return this.renderBlockIntoEntity(type, renderer, blockEntity, state, pos, true);
   }

   public PhysicsEntity renderBlockIntoEntity(Level level, PhysicsEntity.Type type, BlockState state, BlockPos pos, boolean bakeAO) {
      if (state.hasBlockEntity()) {
         BlockEntityRenderDispatcher berd = Minecraft.getInstance().getBlockEntityRenderDispatcher();
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity != null) {
            BlockEntityRenderer<BlockEntity> renderer = berd.getRenderer(blockEntity);
            if (renderer != null) {
               return this.renderBlockIntoEntity(type, renderer, blockEntity, state, pos, false);
            }
         }
      }

      BlockRenderDispatcher manager = Minecraft.getInstance().getBlockRenderer();
      BakedModel model = manager.getBlockModel(state);
      return this.renderBlockIntoEntity(type, model, state, pos, bakeAO);
   }

   private int renderFlat(
      PhysicsEntity entity, BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, RandomSource random, long seed, int overlay
   ) {
      int hashCode = 0;
      Mesh mesh = entity.models.get(0).mesh;

      for (int i = 0; i < DIRECTIONS.length; i++) {
         Direction direction = DIRECTIONS[i];
         random.setSeed(seed);
         List<BakedQuad> list = model.getQuads(state, direction, random);
         if (!list.isEmpty()) {
            hashCode = hashCode * 31 + this.renderQuadsFlat(entity, mesh, world, state, pos, overlay, list);
         }
      }

      random.setSeed(seed);
      List<BakedQuad> quads = model.getQuads(state, (Direction)null, random);
      if (!quads.isEmpty()) {
         hashCode = hashCode * 31 + this.renderQuadsFlat(entity, mesh, world, state, pos, overlay, quads);
      }

      return hashCode;
   }

   private int renderQuadsFlat(PhysicsEntity entity, Mesh mesh, BlockAndTintGetter world, BlockState state, BlockPos pos, int overlay, List<BakedQuad> quads) {
      int hashCode = 0;

      for (int i = 0; i < quads.size(); i++) {
         hashCode = hashCode * 31 + this.renderQuadFlat(entity, mesh, world, state, pos, quads.get(i), overlay);
      }

      return hashCode;
   }

   private int renderQuadFlat(PhysicsEntity entity, Mesh mesh, BlockAndTintGetter world, BlockState state, BlockPos pos, BakedQuad quad, int overlay) {
      int hashCode = 0;
      float red = 1.0F;
      float green = 1.0F;
      float blue = 1.0F;
      if (quad.isTinted()) {
         int blockColor = Minecraft.getInstance().getBlockColors().getColor(state, world, pos, quad.getTintIndex());
         hashCode = hashCode * 31 + blockColor;
         red = (blockColor >> 16 & 0xFF) / 255.0F;
         green = (blockColor >> 8 & 0xFF) / 255.0F;
         blue = (blockColor & 0xFF) / 255.0F;
      }

      entity.shade = quad.isShade();
      int[] vertexData = quad.getVertices();
      Vec3i normal = quad.getDirection().getNormal();
      int integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
      int vertexSize = integerSize;
      int vertices = vertexData.length / integerSize;

      for (int i = 0; i < vertices; i++) {
         int offset = i * vertexSize;
         float x = Float.intBitsToFloat(vertexData[offset]);
         float y = Float.intBitsToFloat(vertexData[offset + 1]);
         float z = Float.intBitsToFloat(vertexData[offset + 2]);
         int rgb = vertexData[offset + 3];
         float r = (rgb >> 24 & 0xFF) / 255.0F * red;
         float g = (rgb >> 16 & 0xFF) / 255.0F * green;
         float b = (rgb >> 8 & 0xFF) / 255.0F * blue;
         mesh.positions.add(new Vector3f(x, y, z));
         mesh.addColor(r, g, b);
         mesh.normals.add(new Vector3f(normal.getX(), normal.getY(), normal.getZ()));
         mesh.uvs.add(new Vector2f(Float.intBitsToFloat(vertexData[offset + 4]), Float.intBitsToFloat(vertexData[offset + 5])));
      }

      int index = mesh.positions.size() - 4;
      mesh.indices.add(index);
      mesh.indices.add(index + 1);
      mesh.indices.add(index + 2);
      mesh.indices.add(index);
      mesh.indices.add(index + 2);
      mesh.indices.add(index + 3);
      return hashCode * 31 + quad.hashCode();
   }

   private void calculateShape(
      BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet
   ) {
      float f = 32.0F;
      float g = 32.0F;
      float h = 32.0F;
      float i = -32.0F;
      float j = -32.0F;
      float k = -32.0F;

      for (int l = 0; l < 4; l++) {
         float m = Float.intBitsToFloat(is[l * 8]);
         float n = Float.intBitsToFloat(is[l * 8 + 1]);
         float o = Float.intBitsToFloat(is[l * 8 + 2]);
         f = Math.min(f, m);
         g = Math.min(g, n);
         h = Math.min(h, o);
         i = Math.max(i, m);
         j = Math.max(j, n);
         k = Math.max(k, o);
      }

      if (fs != null) {
         fs[Direction.WEST.get3DDataValue()] = f;
         fs[Direction.EAST.get3DDataValue()] = i;
         fs[Direction.DOWN.get3DDataValue()] = g;
         fs[Direction.UP.get3DDataValue()] = j;
         fs[Direction.NORTH.get3DDataValue()] = h;
         fs[Direction.SOUTH.get3DDataValue()] = k;
         int var19 = DIRECTIONS.length;
         fs[Direction.WEST.get3DDataValue() + var19] = 1.0F - f;
         fs[Direction.EAST.get3DDataValue() + var19] = 1.0F - i;
         fs[Direction.DOWN.get3DDataValue() + var19] = 1.0F - g;
         fs[Direction.UP.get3DDataValue() + var19] = 1.0F - j;
         fs[Direction.NORTH.get3DDataValue() + var19] = 1.0F - h;
         fs[Direction.SOUTH.get3DDataValue() + var19] = 1.0F - k;
      }

      float p = 1.0E-4F;
      float m = 0.9999F;
      switch (direction) {
         case DOWN:
            bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, g == j && (g < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case UP:
            bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, g == j && (j > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case NORTH:
            bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
            bitSet.set(0, h == k && (h < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case SOUTH:
            bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
            bitSet.set(0, h == k && (k > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case WEST:
            bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, f == i && (f < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case EAST:
            bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, f == i && (i > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
      }
   }

   public static void blockifyItemStack(Level level, ItemStack item, boolean mainHand) {
      try {
         if (StarterClient.iris) {
            Iris.enableHandRendering();
         }

         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         renderHand(level, item, camera, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), mainHand);
         if (StarterClient.iris) {
            Iris.disableHandRendering();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   private static void renderHand(Level level, ItemStack item, Camera camera, float tickDelta, boolean mainHand) {
      PoseStack matrices = new PoseStack();
      matrices.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
      matrices.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
      matrices.last().pose().invert();
      matrices.pushPose();
      bobViewWhenHurt(matrices, tickDelta);
      if ((Boolean)Minecraft.getInstance().options.bobView().get()) {
         bobView(matrices, tickDelta);
      }

      renderItem(
         level,
         item,
         camera,
         mainHand,
         Minecraft.getInstance().gameRenderer.itemInHandRenderer,
         tickDelta,
         matrices,
         Minecraft.getInstance().player,
         Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(Minecraft.getInstance().player, tickDelta)
      );
      matrices.popPose();
   }

   private static void renderItem(
      Level level,
      ItemStack item,
      Camera camera,
      boolean mainHand,
      ItemInHandRenderer firstPersonRenderer,
      float tickDelta,
      PoseStack matrices,
      LocalPlayer player,
      int light
   ) {
      float f = player.getAttackAnim(tickDelta);
      InteractionHand hand = (InteractionHand)MoreObjects.firstNonNull(player.swingingArm, InteractionHand.MAIN_HAND);
      float xRot = Mth.lerp(tickDelta, player.xRotO, player.getXRot());
      float h = Mth.lerp(tickDelta, player.xBobO, player.xBob);
      float i = Mth.lerp(tickDelta, player.yBobO, player.yBob);
      matrices.mulPose(Axis.XP.rotationDegrees((player.getViewXRot(tickDelta) - h) * 0.1F));
      matrices.mulPose(Axis.YP.rotationDegrees((player.getViewYRot(tickDelta) - i) * 0.1F));
      ItemVertexConsumerProvider dummy = new ItemVertexConsumerProvider();
      float anim = hand == InteractionHand.MAIN_HAND ? f : 0.0F;
      float height = 1.0F - Mth.lerp(tickDelta, firstPersonRenderer.oMainHandHeight, firstPersonRenderer.mainHandHeight);

      try {
         itemBreakTransformation = new Matrix4f();
         firstPersonRenderer.renderArmWithItem(
            player, tickDelta, xRot, mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, anim, item, height, matrices, dummy, light
         );
         BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(item, null, null, 1);
         TextureAtlasSprite sprite = model.getParticleIcon();
         SpriteContents contents = sprite.contents();
         float size = 1.0F / contents.width() * 0.6666666F;
         float texelSizeX = 1.0F / contents.width();
         float texelSizeY = 1.0F / contents.height();
         float depthScale = 0.041666664F / size;
         itemBreakTransformation.translateLocal((float)camera.getPosition().x, (float)camera.getPosition().y, (float)camera.getPosition().z);
         boolean highRes = contents.width() > 32 || contents.height() > 32;
         if (!highRes) {
            for (int x = 0; x < contents.width(); x++) {
               for (int y = 0; y < contents.height(); y++) {
                  if (!contents.isTransparent(0, x, y)) {
                     float uvx = (float)x / contents.width() + texelSizeX * 0.5F;
                     float uvy = (float)y / contents.height() + texelSizeY * 0.5F;
                     ParticleSpawner.spawnItemPhysicsParticle(sprite, level, uvx, 1.0F - uvy, 0.5, size, depthScale, uvx, uvy, itemBreakTransformation);
                  }
               }
            }
         }
      } finally {
         itemBreakTransformation = null;
      }
   }

   private static void bobView(PoseStack matrices, float f) {
      if (Minecraft.getInstance().getCameraEntity() instanceof Player) {
         Player playerEntity = (Player)Minecraft.getInstance().getCameraEntity();
         float g = playerEntity.walkDist - playerEntity.walkDistO;
         float h = -(playerEntity.walkDist + g * f);
         float i = Mth.lerp(f, playerEntity.oBob, playerEntity.bob);
         matrices.translate(Mth.sin(h * 3.1415927F) * i * 0.5F, -Math.abs(Mth.cos(h * 3.1415927F) * i), 0.0);
         matrices.mulPose(Axis.ZP.rotationDegrees(Mth.sin(h * 3.1415927F) * i * 3.0F));
         matrices.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(h * 3.1415927F - 0.2F) * i) * 5.0F));
      }
   }

   private static void bobViewWhenHurt(PoseStack matrices, float f) {
      if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity) {
         LivingEntity livingEntity = (LivingEntity)Minecraft.getInstance().getCameraEntity();
         float g = livingEntity.hurtTime - f;
         if (livingEntity.isDeadOrDying()) {
            float i = Math.min(livingEntity.deathTime + f, 20.0F);
            matrices.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (i + 200.0F)));
         }

         if (g < 0.0F) {
            return;
         }

         g /= livingEntity.hurtDuration;
         g = Mth.sin(g * g * g * g * 3.1415927F);
         float i = livingEntity.getHurtDir();
         matrices.mulPose(Axis.YP.rotationDegrees(-i));
         matrices.mulPose(Axis.ZP.rotationDegrees(-g * 14.0F));
         matrices.mulPose(Axis.YP.rotationDegrees(i));
      }
   }

   public static void storeShaderLightDirections() {
      shaderLight0 = RenderSystem.shaderLightDirections[0];
      shaderLight1 = RenderSystem.shaderLightDirections[1];
   }

   public static void restoreShaderLightDirections() {
      RenderSystem.shaderLightDirections[0] = shaderLight0;
      RenderSystem.shaderLightDirections[1] = shaderLight1;
   }

   static {
      brokenBlocksLittle.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_1.obj"));
      brokenBlocksLittle.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_2.obj"));
      brokenBlocksLittle.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_3.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_2.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_3.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_4.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_5.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_6.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_7.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_8.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_9.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_10.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_11.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_12.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_13.obj"));
      brokenBlocksLittleVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_1_voxel.obj"));
      brokenBlocksLittleVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_2_voxel.obj"));
      brokenBlocksLittleVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_3_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_2_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_3_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_4_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_5_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_6_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_7_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_8_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_9_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_10_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_11_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_12_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_13_voxel.obj"));

      for (int i = 0; i < brokenBlocksLittle.size(); i++) {
         List<Mesh> meshes = brokenBlocksLittle.get(i);
         List<Mesh> meshesVoxel = brokenBlocksLittleVoxel.get(i);

         for (int j = 0; j < meshes.size(); j++) {
            meshesVoxel.get(j).offset = new Vector3f(meshes.get(j).offset);
         }
      }

      for (int i = 0; i < brokenBlocksLots.size(); i++) {
         List<Mesh> meshes = brokenBlocksLots.get(i);
         List<Mesh> meshesVoxel = brokenBlocksLotsVoxel.get(i);

         for (int j = 0; j < meshes.size(); j++) {
            meshesVoxel.get(j).offset = new Vector3f(meshes.get(j).offset);
         }
      }

      brokenBlock = readBlock("assets/physicsmod/models/fractures/physics_simple.obj");
      snowballMesh.add(readBlock("assets/physicsmod/models/snowball/snowball_voxel.obj").get(0));
      snowballMesh.add(readBlock("assets/physicsmod/models/snowball/snowball_round.obj").get(0));
      snowballMeshFractured.add(readBlock("assets/physicsmod/models/snowball/snowball_voxel_fractured.obj"));
      snowballMeshFractured.add(readBlock("assets/physicsmod/models/snowball/snowball_round_fractured.obj"));
      enderpearlMesh.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_voxel.obj").get(0));
      enderpearlMesh.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_round.obj").get(0));
      enderpearlMeshFractured.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_voxel_fractured.obj"));
      enderpearlMeshFractured.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_round_fractured.obj"));
      eggMesh.add(readBlock("assets/physicsmod/models/egg/egg_voxel.obj").get(0));
      eggMesh.add(readBlock("assets/physicsmod/models/egg/egg_round.obj").get(0));
      eggMeshFractured.add(readBlock("assets/physicsmod/models/egg/egg_voxel_fractured.obj"));
      eggMeshFractured.add(readBlock("assets/physicsmod/models/egg/egg_round_fractured.obj"));
      smoke = readBlock("assets/physicsmod/models/smoke/smoke.obj").get(0);
      liquid = readBlock("assets/physicsmod/models/liquid/liquid.obj").get(0);
      DIRECTIONS = Direction.values();
   }
}
