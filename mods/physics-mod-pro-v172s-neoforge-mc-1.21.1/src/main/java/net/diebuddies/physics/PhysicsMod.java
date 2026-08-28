/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.math.Axis
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  net.minecraft.client.model.geom.ModelPart$Vertex
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.ItemInHandRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Snowball
 *  net.minecraft.world.entity.projectile.ThrowableItemProjectile
 *  net.minecraft.world.entity.projectile.ThrownEgg
 *  net.minecraft.world.entity.projectile.ThrownEnderpearl
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.LegacyRandomSource
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
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
import net.diebuddies.math.Math;
import net.diebuddies.math.RayIntersection;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.model.ColladaParser;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.BlockEntityVertexConsumer;
import net.diebuddies.physics.BlockEntityVertexConsumerProvider;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.CubeExtension;
import net.diebuddies.physics.DummyMultiBufferSource;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.ItemVertexConsumerProvider;
import net.diebuddies.physics.JsonUnbakedModelHolder;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
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
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;

public class PhysicsMod {
    public static final Path CLOTH_DIRECTORY;
    public static final Path CLOTH_SYNCHRONIZED_DIRECTORY;
    public static final ResourceLocation SNOWBALL_TEXTURE;
    public static final ResourceLocation ENDERPEARL_TEXTURE;
    public static final ResourceLocation EGG_TEXTURE;
    public static final ResourceLocation SMOKE_TEXTURE;
    public static final ResourceLocation PUDDLE_TEXTURE;
    private static Vector3f shaderLight0;
    private static Vector3f shaderLight1;
    public static Object2ObjectMap<Level, PhysicsMod> instances;
    private static PhysicsMod currentInstance;
    public static final Map<EntityType<?>, EntityRenderer<?>> renderers;
    public static final Map<Block, String> registeredBlocks;
    public static final Map<String, Block> invRegisteredBlocks;
    public static final Map<String, ParticleOptions> registeredParticles;
    public static final Map<ParticleOptions, String> invRegisteredParticles;
    public static final Map<String, SoundEvent> registeredSounds;
    public static final Map<SoundEvent, String> invRegisteredSounds;
    public static boolean hudRendering;
    public static boolean sodiumCatch;
    public static boolean sodiumCatchBoundingBox;
    public static AABB3D sodiumBoundingBox;
    public static boolean clothSmootShadingIrisFix;
    public static Map<BakedModel, JsonUnbakedModelHolder> loadedModels;
    public static Matrix4f projectionMatrix;
    public static Matrix4f viewMatrix;
    public static List<VerletSimulation> optifineClothCompat;
    public static List<ClothRenderCommand> clothRenderFast;
    public static boolean reloadCloth;
    public static Matrix4f itemBreakTransformation;
    public PhysicsWorld physicsWorld;
    public boolean init = false;
    public ConcurrentLinkedQueue<PhysicsEntity> entityBlocks = new ConcurrentLinkedQueue();
    public ConcurrentLinkedQueue<BlockPos> blockUpdates = new ConcurrentLinkedQueue();
    public ConcurrentLinkedQueue<Explosion> explosions = new ConcurrentLinkedQueue();
    public ConcurrentLinkedQueue<Ragdoll> ragdolls = new ConcurrentLinkedQueue();
    public ConcurrentLinkedQueue<Ragdoll> sodiumRemoveRagdolls = new ConcurrentLinkedQueue();
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
    public static final List<List<Mesh>> brokenBlocksLittle;
    public static final List<List<Mesh>> brokenBlocksLots;
    public static final List<List<Mesh>> brokenBlocksLittleVoxel;
    public static final List<List<Mesh>> brokenBlocksLotsVoxel;
    public static final List<Mesh> brokenBlock;
    public static final List<Mesh> snowballMesh;
    public static final List<List<Mesh>> snowballMeshFractured;
    public static final List<Mesh> enderpearlMesh;
    public static final List<List<Mesh>> enderpearlMeshFractured;
    public static final List<Mesh> eggMesh;
    public static final List<List<Mesh>> eggMeshFractured;
    public static final Mesh smoke;
    public static final Mesh liquid;
    public static Cloth defaultCape;
    public static Texture whiteTexture;
    public static Texture blackTexture;
    public static Texture foamTexture;
    public static Map<String, Cloth> cloth;
    private static final Direction[] DIRECTIONS;

    private static List<Mesh> readBlock(String asset) {
        ObjectArrayList meshes = new ObjectArrayList();
        ObjectArrayList indices = new ObjectArrayList();
        ObjectArrayList indicesQuads = new ObjectArrayList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(asset)));){
            String line = "";
            Mesh mesh = null;
            int ov = 0;
            int ot = 0;
            int on = 0;
            while ((line = reader.readLine()) != null) {
                String[] data;
                if (line.startsWith("o")) {
                    if (mesh != null) {
                        ov += mesh.positions.size();
                        ot += mesh.uvs.size();
                        on += mesh.normals.size();
                        mesh = PhysicsMod.unrollMeshIndices(mesh, (List<Vector3i>)indices, (List<Vector3i>)indicesQuads);
                        indices.clear();
                        indicesQuads.clear();
                        meshes.add(mesh);
                    }
                    mesh = new Mesh(false);
                    continue;
                }
                if (line.startsWith("vt")) {
                    data = line.split(" ");
                    mesh.uvs.add(new Vector2f(Float.parseFloat(data[1]), Float.parseFloat(data[2])));
                    continue;
                }
                if (line.startsWith("vn")) {
                    data = line.split(" ");
                    mesh.normals.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
                    continue;
                }
                if (line.startsWith("v")) {
                    data = line.split(" ");
                    mesh.positions.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
                    continue;
                }
                if (!line.startsWith("f")) continue;
                data = line.split(" ");
                boolean quads = data.length == 5;
                for (int i = 1; i < data.length; ++i) {
                    String[] idata = data[i].split("/");
                    if (quads) {
                        indicesQuads.add(new Vector3i(Integer.parseInt(idata[0]) - ov, Integer.parseInt(idata[1]) - ot, Integer.parseInt(idata[2]) - on));
                        continue;
                    }
                    indices.add(new Vector3i(Integer.parseInt(idata[0]) - ov, Integer.parseInt(idata[1]) - ot, Integer.parseInt(idata[2]) - on));
                }
                if (!quads) continue;
                int index = indicesQuads.size() - 4;
                indices.add((Vector3i)indicesQuads.get(index));
                indices.add((Vector3i)indicesQuads.get(index + 1));
                indices.add((Vector3i)indicesQuads.get(index + 2));
                indices.add((Vector3i)indicesQuads.get(index));
                indices.add((Vector3i)indicesQuads.get(index + 2));
                indices.add((Vector3i)indicesQuads.get(index + 3));
            }
            if (mesh != null) {
                mesh = PhysicsMod.unrollMeshIndices(mesh, (List<Vector3i>)indices, (List<Vector3i>)indicesQuads);
                meshes.add(mesh);
            }
            for (Mesh m : meshes) {
                m.calculateOffset(true);
                m.calculatePBRData(true);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return meshes;
    }

    private static Mesh unrollMeshIndices(Mesh mesh, List<Vector3i> indices, List<Vector3i> indicesQuads) {
        int i;
        Mesh unrolledMesh = new Mesh(false);
        if (indicesQuads.size() > 0) {
            indices.clear();
            for (i = 0; i < indicesQuads.size() / 4; ++i) {
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
        for (i = 0; i < indices.size(); ++i) {
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
            Files.createDirectories(CLOTH_DIRECTORY, new FileAttribute[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PhysicsMod.copyResource("assets/physicsmod/cloth/Cloth.blend", CLOTH_DIRECTORY);
            PhysicsMod.copyResource("assets/physicsmod/cloth/Vanilla Cape.dae", CLOTH_DIRECTORY);
            PhysicsMod.copyResource("assets/physicsmod/cloth/TUTORIAL.txt", CLOTH_DIRECTORY);
        }
        catch (IOException e) {
            e.printStackTrace();
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
                ColladaMesh mesh = ColladaParser.loadStaticModel(new File(String.valueOf(CLOTH_DIRECTORY) + "/Vanilla Cape.dae"));
                mesh.flipUVs();
                defaultCape = new Cloth("Vanilla Cape", mesh, null, null, new ClothRules());
            }
        }
        catch (Exception e) {
            System.err.println("Couldn't load default cape model");
            e.printStackTrace();
        }
        if (cloth != null) {
            for (Cloth cape : cloth.values()) {
                cape.destroy();
            }
        }
        cloth = new Object2ObjectLinkedOpenHashMap();
        PhysicsMod.loadClothFromDirectory(CLOTH_DIRECTORY, true);
        PhysicsMod.loadClothFromDirectory(CLOTH_SYNCHRONIZED_DIRECTORY, false);
        VAO.restorePreviouslyBoundState();
    }

    private static void loadClothFromDirectory(Path clothDirectory, boolean local) {
        try {
            Collection files = Files.list(clothDirectory).filter(PhysicsMod::isValidCloth).collect(Collectors.toList());
            ObjectArrayList sortedCloth = new ObjectArrayList();
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
                }
                catch (Exception e) {
                    System.err.println("Couldn't load " + model.toString());
                    e.printStackTrace();
                }
            }
            Collections.sort(sortedCloth);
            for (Cloth cloth : sortedCloth) {
                PhysicsMod.cloth.put(cloth.name, cloth);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidCloth(Path file) {
        try {
            if (file.toFile().getName().endsWith("dae") && new File(file.toFile().getAbsolutePath().substring(0, file.toFile().getAbsolutePath().length() - 3).concat("png")).exists()) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copyResource(String res, Path dest) throws IOException {
        String[] split = res.split("/");
        try (InputStream src = PhysicsMod.class.getClassLoader().getResourceAsStream(res);){
            Files.copy(src, dest.resolve(split[split.length - 1]), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void copyClothResource(String res, Path dest) throws IOException {
        PhysicsMod.copyResource(res + ".dae", dest);
        PhysicsMod.copyResource(res + ".png", dest);
        PhysicsMod.copyResource(res + ".rules", dest);
    }

    public static void resetClothSimulations() {
        for (PhysicsMod mod : instances.values()) {
            for (VerletSimulation simulation : mod.getPhysicsWorld().getVerletSimulations()) {
                simulation.destroyed = true;
            }
        }
    }

    public static double getPlaybackSpeed() {
        if (Minecraft.getInstance().isPaused()) {
            return 0.0;
        }
        if (StarterClient.replay) {
            return (double)ConfigClient.playbackSpeed * Replay.getPlaybackSpeed();
        }
        return ConfigClient.playbackSpeed;
    }

    public static PhysicsMod getInstance(Level level) {
        PhysicsMod mod;
        if (!(level instanceof ClientLevel)) {
            Thread.dumpStack();
        }
        if ((mod = (PhysicsMod)instances.get((Object)level)) == null) {
            mod = new PhysicsMod(level);
            instances.put((Object)level, (Object)mod);
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
            PhysicsMod.addThrowableProjectile(level, (ThrowableItemProjectile)snowball, snowballMesh.get(ConfigClient.snowballModel), Minecraft.getInstance().getTextureManager().getTexture(SNOWBALL_TEXTURE).getId(), ConfigClient.snowballShade, ConfigClient.snowballImpact == 0 ? snowballMeshFractured.get(ConfigClient.snowballModel) : null, ConfigClient.snowballModel == 0);
        }
    }

    public static void addEnderpearl(Level level, ThrownEnderpearl enderpearl) {
        if (ConfigClient.enderpearlImpact != 2) {
            PhysicsMod.addThrowableProjectile(level, (ThrowableItemProjectile)enderpearl, enderpearlMesh.get(ConfigClient.enderpearlModel), Minecraft.getInstance().getTextureManager().getTexture(ENDERPEARL_TEXTURE).getId(), ConfigClient.enderpearlShade, ConfigClient.enderpearlImpact == 0 ? enderpearlMeshFractured.get(ConfigClient.enderpearlModel) : null, ConfigClient.enderpearlModel == 0);
        }
    }

    public static void addEgg(Level level, ThrownEgg egg) {
        if (ConfigClient.eggImpact != 2) {
            PhysicsMod.addThrowableProjectile(level, (ThrowableItemProjectile)egg, eggMesh.get(ConfigClient.eggModel), Minecraft.getInstance().getTextureManager().getTexture(EGG_TEXTURE).getId(), ConfigClient.eggShade, ConfigClient.eggImpact == 0 ? eggMeshFractured.get(ConfigClient.eggModel) : null, ConfigClient.eggModel == 0);
        }
    }

    public static void addThrowableProjectile(Level level, ThrowableItemProjectile projectile, Mesh mesh, int textureID, boolean shade, List<Mesh> fractures, boolean fastBoxes) {
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.ITEM, null);
        Vector3d snowballPos = new Vector3d(projectile.getX(), projectile.getY(), projectile.getZ());
        float snowballRadius = 0.14f;
        BlockPos.MutableBlockPos originPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos tmpPos = new BlockPos.MutableBlockPos();
        Vector3d rayDir = new Vector3d(projectile.getX() - projectile.xOld, projectile.getY() - projectile.yOld, projectile.getZ() - projectile.zOld).normalize().negate();
        Vector3d aabbMin = new Vector3d();
        Vector3d aabbMax = new Vector3d();
        ObjectOpenHashSet hitPositions = new ObjectOpenHashSet();
        for (int count = 0; count < 5; ++count) {
            float threshold = snowballRadius;
            boolean hasHit = false;
            originPos.set(snowballPos.x, snowballPos.y, snowballPos.z);
            for (int x = -1; x <= 1 && !hasHit; ++x) {
                for (int y = -1; y <= 1 && !hasHit; ++y) {
                    for (int z = -1; z <= 1 && !hasHit; ++z) {
                        VoxelShape voxelShape;
                        tmpPos.set(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                        BlockState state = level.getBlockState((BlockPos)tmpPos);
                        if (state.isAir() || hitPositions.contains(tmpPos) || (voxelShape = state.getCollisionShape((BlockGetter)level, projectile.blockPosition())).isEmpty()) continue;
                        for (AABB aabb : voxelShape.toAabbs()) {
                            if (!AABB3D.isInside(aabb.minX - (double)threshold, aabb.minY - (double)threshold, aabb.minZ - (double)threshold, aabb.maxX + (double)threshold, aabb.maxY + (double)threshold, aabb.maxZ + (double)threshold, snowballPos.x, snowballPos.y, snowballPos.z)) continue;
                            aabbMin.set(aabb.minX - (double)threshold, aabb.minY - (double)threshold, aabb.minZ - (double)threshold);
                            aabbMax.set(aabb.maxX + (double)threshold, aabb.maxY + (double)threshold, aabb.maxZ + (double)threshold);
                            RayIntersection.IntersectionResult result = RayIntersection.intersectAABB(snowballPos, rayDir, aabbMin, aabbMax);
                            if (!result.hit) continue;
                            hasHit = true;
                            snowballPos.x += rayDir.x * result.fraction;
                            snowballPos.y += rayDir.y * result.fraction;
                            snowballPos.z += rayDir.z * result.fraction;
                            hitPositions.add(new BlockPos((Vec3i)tmpPos));
                        }
                    }
                }
            }
            if (hasHit) break;
        }
        entity.getTransformation().translation((Vector3dc)snowballPos);
        Random random = new Random(projectile.getId());
        float progress = projectile.tickCount;
        entity.getTransformation().rotateX(random.nextDouble() * java.lang.Math.PI);
        entity.getTransformation().rotateY(random.nextDouble() * java.lang.Math.PI);
        entity.getTransformation().rotateZ(random.nextDouble() * java.lang.Math.PI + (double)progress * 0.5);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        entity.models.get((int)0).textureID = textureID;
        entity.backfaceCulling = true;
        entity.shade = shade;
        entity.models.get((int)0).mesh = mesh;
        ObjectArrayList bodies = new ObjectArrayList();
        if (fractures != null) {
            mod.physicsWorld.addBlockParticle(fractures, entity, null, (List<IRigidBody>)bodies, true);
        } else {
            IRigidBody body = mod.physicsWorld.addPhysicsSphere(entity, snowballRadius);
            ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity((float)java.lang.Math.toRadians(360.0));
            ((PxRigidDynamic)body.getRigidBody()).setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_SPECULATIVE_CCD, true);
            ((PxRigidDynamic)body.getRigidBody()).setLinearDamping(0.9f);
            ((PxRigidDynamic)body.getRigidBody()).setAngularDamping(0.9f);
            bodies.add(body);
        }
        double speedX = projectile.getX() - projectile.xOld;
        double speedY = projectile.getY() - projectile.yOld;
        double speedZ = projectile.getZ() - projectile.zOld;
        float speedMultiplier = 10.0f;
        for (IRigidBody body : bodies) {
            MemoryStack mem = MemoryStack.stackPush();
            try {
                ((PxRigidDynamic)body.getRigidBody()).setLinearVelocity(PxVec3.createAt(mem, MemoryStack::nmalloc, (float)speedX * speedMultiplier, (float)speedY * speedMultiplier, (float)speedZ * speedMultiplier));
            }
            finally {
                if (mem == null) continue;
                mem.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void blockifyEntity(Level level, LivingEntity entity) {
        EntityRenderer entityRenderer;
        PhysicsMod mod = PhysicsMod.getInstance(level);
        if (ConfigMobs.getMobSetting((Entity)entity).getType() == MobPhysicsType.OFF) {
            return;
        }
        if (mod.alreadyBlockified.contains(entity.getId()) && !(entity instanceof Player)) {
            return;
        }
        if (entity.isInvisible()) {
            return;
        }
        mod.alreadyBlockified.add(entity.getId());
        EntityRenderer renderer = entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)entity);
        EntityModel model = null;
        if (entityRenderer instanceof LivingEntityRenderer) {
            model = ((LivingEntityRenderer)entityRenderer).getModel();
        }
        PoseStack stack = new PoseStack();
        stack.pushPose();
        mod.blockify = true;
        mod.localPivotMatrix = new PoseStack();
        mod.cubifyEntityRenderer = renderer;
        mod.cubifyEntity = entity;
        PhysicsMod.setCurrentInstance(mod);
        mod.blockifyEntity = entity;
        mod.blockifyFeature = null;
        mod.blockifyFeatureIndex = 0;
        DummyMultiBufferSource source = new DummyMultiBufferSource();
        try {
            renderer.render((Entity)entity, 0.0f, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), stack, (MultiBufferSource)source, 0);
        }
        catch (Exception e) {
            System.err.println("error rendering " + String.valueOf(entity.getClass()));
            e.printStackTrace();
        }
        finally {
            if (source.lastLayer != null) {
                source.lastLayer.clearRenderState();
            }
        }
        PhysicsMod.setCurrentInstance(null);
        mod.blockify = false;
        try {
            RagdollMapper.filterCuboidsFromEntities((Entity)entity, model);
        }
        catch (Exception e) {
            System.err.println("error filtering " + String.valueOf(entity.getClass()));
            e.printStackTrace();
        }
        stack.popPose();
        for (PhysicsEntity physicsEntity : mod.blockifiedEntity) {
            physicsEntity.backfaceCulling = false;
        }
        MobPhysicsType type = ConfigMobs.getMobSetting((Entity)entity).getType();
        if (type == MobPhysicsType.RAGDOLL || type == MobPhysicsType.RAGDOLL_BREAK || type == MobPhysicsType.RAGDOLL_BREAK_BLOOD) {
            Ragdoll ragdoll = null;
            try {
                ragdoll = RagdollMapper.map(type, (Entity)entity, model);
            }
            catch (Exception e) {
                System.err.println("error creating ragdoll for " + String.valueOf(entity.getClass()));
                e.printStackTrace();
            }
            if (ragdoll == null) {
                mod.entityBlocks.addAll(mod.blockifiedEntity);
                if (mod.entityBlocks.size() > 0) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                    entity.deathTime = 20;
                    entity.hurtTime = 0;
                }
            } else {
                ClientLevel clientLevel;
                Player closest;
                mod.ragdolls.add(ragdoll);
                if (level instanceof ClientLevel && (closest = (clientLevel = (ClientLevel)level).getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 8.0, false)) != null) {
                    ragdoll.velocity.set(entity.getX() - closest.getX(), 2.0, entity.getZ() - closest.getZ()).normalize().mul(5.0);
                }
                ragdoll.velocity.add(entity.getDeltaMovement().x * 10.0, entity.getDeltaMovement().y * 10.0, entity.getDeltaMovement().z * 10.0);
                entity.remove(Entity.RemovalReason.DISCARDED);
                entity.deathTime = 20;
                entity.hurtTime = 0;
            }
        } else {
            mod.entityBlocks.addAll(mod.blockifiedEntity);
            if (mod.entityBlocks.size() > 0) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                entity.deathTime = 20;
                entity.hurtTime = 0;
            }
        }
        mod.blockifiedEntity.clear();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void createParticlesFromCuboids(PoseStack.Pose stack, PoseStack local, List<ModelPart.Cube> cuboids, Entity entity, EntityRenderer renderer, RenderLayer feature, int overlay, float red, float green, float blue) {
        Matrix4f m = stack.pose();
        Matrix4f localM = local.last().pose();
        Matrix3f localNM = local.last().normal();
        Matrix4d transformation = new Matrix4d();
        Matrix4d transformationLocal = new Matrix4d();
        transformation.set((Matrix4fc)m);
        transformationLocal.set((Matrix4fc)localM);
        transformation.mul((Matrix4dc)transformationLocal.invert(new Matrix4d()));
        if (!transformation.isFinite()) {
            return;
        }
        PhysicsMod mod = PhysicsMod.getInstance(entity.getCommandSenderWorld());
        int textureID = TextureHelper.getLoadedTextures();
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        double px = Mth.lerp((double)partialTicks, (double)entity.xo, (double)entity.getX());
        double py = Mth.lerp((double)partialTicks, (double)entity.yo, (double)entity.getY());
        double pz = Mth.lerp((double)partialTicks, (double)entity.zo, (double)entity.getZ());
        transformation.setTranslation(px + transformation.m30(), py + transformation.m31(), pz + transformation.m32());
        Vector4f[] minMax = new Vector4f[6];
        Vector3f tmpNormal = new Vector3f();
        Vector4f tmpPos = new Vector4f();
        for (int i = 0; i < minMax.length; ++i) {
            minMax[i] = new Vector4f();
        }
        MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
        for (ModelPart.Cube box : cuboids) {
            if (box.polygons.length < 6) continue;
            float minX = box.polygons[0].vertices[2].pos.x();
            float minY = box.polygons[0].vertices[2].pos.y();
            float minZ = box.polygons[0].vertices[2].pos.z();
            float maxX = box.polygons[1].vertices[3].pos.x();
            float maxY = box.polygons[1].vertices[3].pos.y();
            float maxZ = box.polygons[1].vertices[3].pos.z();
            int[] remap = new int[]{5, 4, 3, 2, 1, 0};
            float volume = java.lang.Math.abs(maxX - minX) / 16.0f * (java.lang.Math.abs(maxY - minY) / 16.0f) * (java.lang.Math.abs(maxZ - minZ) / 16.0f);
            boolean isBlocky = type == MobPhysicsType.BLOCKY || type == MobPhysicsType.RAGDOLL || type == MobPhysicsType.RAGDOLL_BREAK || type == MobPhysicsType.RAGDOLL_BREAK_BLOOD;
            boolean noVolume = false;
            if ((double)volume <= 1.0E-4) {
                if (!isBlocky) continue;
                noVolume = true;
            }
            boolean mirror = ((CubeExtension)box).isMirrored();
            List<Mesh> meshes = brokenBlocksLittle.get((int)(Math.random() * (float)brokenBlocksLittle.size()));
            if ((double)volume <= 0.04 || isBlocky) {
                meshes = brokenBlock;
            }
            for (int i = 0; i < box.polygons.length; ++i) {
                float minU = 1.0f;
                float maxU = 0.0f;
                float minV = 1.0f;
                float maxV = 0.0f;
                for (ModelPart.Vertex vertex : box.polygons[i].vertices) {
                    if (vertex.u < minU) {
                        minU = vertex.u;
                    }
                    if (vertex.v < minV) {
                        minV = vertex.v;
                    }
                    if (vertex.u > maxU) {
                        maxU = vertex.u;
                    }
                    if (!(vertex.v > maxV)) continue;
                    maxV = vertex.v;
                }
                minMax[i].set(minU, maxU, minV, maxV);
            }
            PhysicsEntity parent = null;
            for (Mesh mesh : meshes) {
                Mesh clone;
                PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.MOB, entity.getType());
                particle.feature = feature;
                particle.noVolume = noVolume;
                particle.models.get((int)0).textureID = textureID;
                particle.models.get((int)0).mesh = clone = new Mesh();
                particle.getTransformation().set((Matrix4dc)transformation);
                particle.getOldTransformation().set((Matrix4dc)particle.getTransformation());
                int count = 0;
                Vector3f offset = new Vector3f();
                for (int i = 0; i < mesh.indices.size(); ++i) {
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
                            r = 0.6f;
                            g = 0.0f;
                            b = 0.0f;
                        }
                        sideIndex = 0;
                    }
                    tmpNormal.set(mirror ? -normal.x : normal.x, normal.y, normal.z);
                    localNM.transform(tmpNormal);
                    Vector4f minMaxUVs = minMax[remap[sideIndex]];
                    tmpPos.set((float)Math.remap((double)(position.x + mesh.offset.x), -0.5, 0.5, (double)minX, (double)maxX) / 16.0f, (float)Math.remap((double)(position.y + mesh.offset.y), -0.5, 0.5, (double)minY, (double)maxY) / 16.0f, (float)Math.remap((double)(position.z + mesh.offset.z), mirror ? 0.5 : -0.5, mirror ? -0.5 : 0.5, (double)minZ, (double)maxZ) / 16.0f, 1.0f);
                    localM.transform(tmpPos);
                    clone.indices.add(count);
                    offset.add(tmpPos.x(), tmpPos.y(), tmpPos.z());
                    ++count;
                    Vector3f posR = new Vector3f(tmpPos.x(), tmpPos.y(), tmpPos.z());
                    clone.positions.add(posR);
                    clone.uvs.add(new Vector2f(Math.remap(uv.x, 1.0f, 0.0f, minMaxUVs.x, minMaxUVs.y), Math.remap(uv.y, 0.0f, 1.0f, minMaxUVs.z, minMaxUVs.w)));
                    clone.normals.add(new Vector3f(tmpNormal.x(), tmpNormal.y(), tmpNormal.z()));
                    clone.addColor(r, g, b);
                }
                if (StarterClient.iris || StarterClient.optifabric) {
                    clone.calculatePBRData(false);
                }
                offset.div((float)clone.positions.size());
                for (Vector3f position : clone.positions) {
                    position.sub((Vector3fc)offset);
                }
                clone.offset = offset;
                Vector3d ps = transformationLocal.getTranslation(new Vector3d());
                particle.pivot.set((Vector3dc)ps);
                if (clone.positions.size() <= 0) continue;
                if (parent == null) {
                    parent = particle;
                    mod.blockifiedEntity.add(particle);
                    continue;
                }
                parent.children.add(particle);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PhysicsEntity renderBlockIntoEntity(PhysicsEntity.Type type, BakedModel model, BlockState state, BlockPos pos, boolean bakeAO) {
        this.itemStackEntity = new PhysicsEntity(type, state);
        this.itemStackEntity.models.get((int)0).mesh = new Mesh();
        PhysicsMod.setCurrentInstance(this);
        Vec3 blockOffset = state.getOffset((BlockGetter)this.physicsWorld.getWorld(), pos);
        try {
            LegacyRandomSource random = new LegacyRandomSource(0L);
            this.renderFlat(this.itemStackEntity, (BlockAndTintGetter)this.physicsWorld.getWorld(), model, state, pos, (RandomSource)random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
        }
        catch (Exception e) {
            PhysicsEntity physicsEntity = null;
            return physicsEntity;
        }
        finally {
            PhysicsMod.setCurrentInstance(null);
        }
        if (this.itemStackEntity.models.get((int)0).mesh.indices.size() < 9) {
            PhysicsMod.setCurrentInstance(null);
            return null;
        }
        this.itemStackEntity.models.get((int)0).mesh.calculateOffset();
        if (StarterClient.iris || StarterClient.optifabric) {
            this.itemStackEntity.models.get((int)0).mesh.calculatePBRData(false);
        }
        this.itemStackEntity.models.get((int)0).textureID = Minecraft.getInstance().getTextureManager().getTexture(model.getParticleIcon().atlasLocation()).getId();
        this.itemStackEntity.getTransformation().set((Matrix4dc)new Matrix4d().translate((double)pos.getX() + blockOffset.x, (double)pos.getY() + blockOffset.y, (double)pos.getZ() + blockOffset.z));
        this.itemStackEntity.getOldTransformation().set((Matrix4dc)this.itemStackEntity.getTransformation());
        this.itemStackEntity.models.get((int)0).animationSprite = model.getParticleIcon();
        return this.itemStackEntity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PhysicsEntity renderBlockIntoEntity(PhysicsEntity.Type type, BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, BlockState state, BlockPos pos, boolean destruction) {
        PhysicsEntity physicsBlockEntity = new PhysicsEntity(type, state);
        physicsBlockEntity.backfaceCulling = false;
        physicsBlockEntity.models.clear();
        PhysicsMod.setCurrentInstance(this);
        sodiumCatch = true;
        float tickDelta = 0.0f;
        BlockEntityVertexConsumerProvider source = new BlockEntityVertexConsumerProvider(destruction);
        try {
            renderer.render(blockEntity, tickDelta, new PoseStack(), (MultiBufferSource)source, OverlayTexture.NO_OVERLAY, 0);
        }
        catch (Exception e) {
            PhysicsEntity physicsEntity = null;
            return physicsEntity;
        }
        finally {
            PhysicsMod.setCurrentInstance(null);
            if (source.getLastLayer() != null) {
                source.getLastLayer().clearRenderState();
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            sodiumCatch = false;
        }
        Vec3 blockOffset = state.getOffset((BlockGetter)this.physicsWorld.getWorld(), pos);
        ObjectArrayList meshes = new ObjectArrayList();
        for (BlockEntityVertexConsumer consumer : source.getBakedRenderTypeModels().values()) {
            consumer.validateModel();
            Model model = consumer.getModel();
            if (model.mesh.indices.size() < 6) continue;
            meshes.add(model.mesh);
            if (StarterClient.iris || StarterClient.optifabric) {
                model.mesh.calculatePBRData(false);
            }
            physicsBlockEntity.models.add(model);
        }
        if (physicsBlockEntity.models.size() == 0) {
            PhysicsMod.setCurrentInstance(null);
            return null;
        }
        Mesh.calculateMeshOffsets((List<Mesh>)meshes, false);
        physicsBlockEntity.getTransformation().set((Matrix4dc)new Matrix4d().translate((double)pos.getX() + blockOffset.x, (double)pos.getY() + blockOffset.y, (double)pos.getZ() + blockOffset.z));
        physicsBlockEntity.getOldTransformation().set((Matrix4dc)physicsBlockEntity.getTransformation());
        return physicsBlockEntity;
    }

    public PhysicsEntity renderBlockIntoEntity(PhysicsEntity.Type type, BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, BlockState state, BlockPos pos) {
        return this.renderBlockIntoEntity(type, renderer, blockEntity, state, pos, true);
    }

    public PhysicsEntity renderBlockIntoEntity(Level level, PhysicsEntity.Type type, BlockState state, BlockPos pos, boolean bakeAO) {
        if (state.hasBlockEntity()) {
            BlockEntityRenderer renderer;
            BlockEntityRenderDispatcher berd = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && (renderer = berd.getRenderer(blockEntity)) != null) {
                return this.renderBlockIntoEntity(type, (BlockEntityRenderer<BlockEntity>)renderer, blockEntity, state, pos, false);
            }
        }
        BlockRenderDispatcher manager = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = manager.getBlockModel(state);
        return this.renderBlockIntoEntity(type, model, state, pos, bakeAO);
    }

    private int renderFlat(PhysicsEntity entity, BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, RandomSource random, long seed, int overlay) {
        int hashCode = 0;
        Mesh mesh = entity.models.get((int)0).mesh;
        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            random.setSeed(seed);
            List list = model.getQuads(state, direction, random);
            if (list.isEmpty()) continue;
            hashCode = hashCode * 31 + this.renderQuadsFlat(entity, mesh, world, state, pos, overlay, list);
        }
        random.setSeed(seed);
        List quads = model.getQuads(state, (Direction)null, random);
        if (!quads.isEmpty()) {
            hashCode = hashCode * 31 + this.renderQuadsFlat(entity, mesh, world, state, pos, overlay, quads);
        }
        return hashCode;
    }

    private int renderQuadsFlat(PhysicsEntity entity, Mesh mesh, BlockAndTintGetter world, BlockState state, BlockPos pos, int overlay, List<BakedQuad> quads) {
        int hashCode = 0;
        for (int i = 0; i < quads.size(); ++i) {
            hashCode = hashCode * 31 + this.renderQuadFlat(entity, mesh, world, state, pos, quads.get(i), overlay);
        }
        return hashCode;
    }

    private int renderQuadFlat(PhysicsEntity entity, Mesh mesh, BlockAndTintGetter world, BlockState state, BlockPos pos, BakedQuad quad, int overlay) {
        int integerSize;
        int hashCode = 0;
        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;
        if (quad.isTinted()) {
            int blockColor = Minecraft.getInstance().getBlockColors().getColor(state, world, pos, quad.getTintIndex());
            hashCode = hashCode * 31 + blockColor;
            red = (float)(blockColor >> 16 & 0xFF) / 255.0f;
            green = (float)(blockColor >> 8 & 0xFF) / 255.0f;
            blue = (float)(blockColor & 0xFF) / 255.0f;
        }
        entity.shade = quad.isShade();
        int[] vertexData = quad.getVertices();
        Vec3i normal = quad.getDirection().getNormal();
        int vertexSize = integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
        int vertices = vertexData.length / vertexSize;
        for (int i = 0; i < vertices; ++i) {
            int offset = i * vertexSize;
            float x = Float.intBitsToFloat(vertexData[offset]);
            float y = Float.intBitsToFloat(vertexData[offset + 1]);
            float z = Float.intBitsToFloat(vertexData[offset + 2]);
            int rgb = vertexData[offset + 3];
            float r = (float)(rgb >> 24 & 0xFF) / 255.0f * red;
            float g = (float)(rgb >> 16 & 0xFF) / 255.0f * green;
            float b = (float)(rgb >> 8 & 0xFF) / 255.0f * blue;
            mesh.positions.add(new Vector3f(x, y, z));
            mesh.addColor(r, g, b);
            mesh.normals.add(new Vector3f((float)normal.getX(), (float)normal.getY(), (float)normal.getZ()));
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

    private void calculateShape(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet) {
        float m;
        int l;
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (l = 0; l < 4; ++l) {
            m = Float.intBitsToFloat(is[l * 8]);
            float n = Float.intBitsToFloat(is[l * 8 + 1]);
            float o = Float.intBitsToFloat(is[l * 8 + 2]);
            f = java.lang.Math.min(f, m);
            g = java.lang.Math.min(g, n);
            h = java.lang.Math.min(h, o);
            i = java.lang.Math.max(i, m);
            j = java.lang.Math.max(j, n);
            k = java.lang.Math.max(k, o);
        }
        if (fs != null) {
            fs[Direction.WEST.get3DDataValue()] = f;
            fs[Direction.EAST.get3DDataValue()] = i;
            fs[Direction.DOWN.get3DDataValue()] = g;
            fs[Direction.UP.get3DDataValue()] = j;
            fs[Direction.NORTH.get3DDataValue()] = h;
            fs[Direction.SOUTH.get3DDataValue()] = k;
            l = DIRECTIONS.length;
            fs[Direction.WEST.get3DDataValue() + l] = 1.0f - f;
            fs[Direction.EAST.get3DDataValue() + l] = 1.0f - i;
            fs[Direction.DOWN.get3DDataValue() + l] = 1.0f - g;
            fs[Direction.UP.get3DDataValue() + l] = 1.0f - j;
            fs[Direction.NORTH.get3DDataValue() + l] = 1.0f - h;
            fs[Direction.SOUTH.get3DDataValue() + l] = 1.0f - k;
        }
        float p = 1.0E-4f;
        m = 0.9999f;
        switch (direction) {
            case DOWN: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, g == j && (g < 1.0E-4f || blockState.isCollisionShapeFullBlock((BlockGetter)blockAndTintGetter, blockPos)));
                break;
            }
            case UP: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, g == j && (j > 0.9999f || blockState.isCollisionShapeFullBlock((BlockGetter)blockAndTintGetter, blockPos)));
                break;
            }
            case NORTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, h == k && (h < 1.0E-4f || blockState.isCollisionShapeFullBlock((BlockGetter)blockAndTintGetter, blockPos)));
                break;
            }
            case SOUTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, h == k && (k > 0.9999f || blockState.isCollisionShapeFullBlock((BlockGetter)blockAndTintGetter, blockPos)));
                break;
            }
            case WEST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, f == i && (f < 1.0E-4f || blockState.isCollisionShapeFullBlock((BlockGetter)blockAndTintGetter, blockPos)));
                break;
            }
            case EAST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, f == i && (i > 0.9999f || blockState.isCollisionShapeFullBlock((BlockGetter)blockAndTintGetter, blockPos)));
            }
        }
    }

    public static void blockifyItemStack(Level level, ItemStack item, boolean mainHand) {
        try {
            if (StarterClient.iris) {
                Iris.enableHandRendering();
            }
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            PhysicsMod.renderHand(level, item, camera, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), mainHand);
            if (StarterClient.iris) {
                Iris.disableHandRendering();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderHand(Level level, ItemStack item, Camera camera, float tickDelta, boolean mainHand) {
        PoseStack matrices = new PoseStack();
        matrices.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0f));
        matrices.last().pose().invert();
        matrices.pushPose();
        PhysicsMod.bobViewWhenHurt(matrices, tickDelta);
        if (((Boolean)Minecraft.getInstance().options.bobView().get()).booleanValue()) {
            PhysicsMod.bobView(matrices, tickDelta);
        }
        PhysicsMod.renderItem(level, item, camera, mainHand, Minecraft.getInstance().gameRenderer.itemInHandRenderer, tickDelta, matrices, Minecraft.getInstance().player, Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords((Entity)Minecraft.getInstance().player, tickDelta));
        matrices.popPose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void renderItem(Level level, ItemStack item, Camera camera, boolean mainHand, ItemInHandRenderer firstPersonRenderer, float tickDelta, PoseStack matrices, LocalPlayer player, int light) {
        float f = player.getAttackAnim(tickDelta);
        InteractionHand hand = (InteractionHand)MoreObjects.firstNonNull((Object)player.swingingArm, (Object)InteractionHand.MAIN_HAND);
        float xRot = Mth.lerp((float)tickDelta, (float)player.xRotO, (float)player.getXRot());
        float h = Mth.lerp((float)tickDelta, (float)player.xBobO, (float)player.xBob);
        float i = Mth.lerp((float)tickDelta, (float)player.yBobO, (float)player.yBob);
        matrices.mulPose(Axis.XP.rotationDegrees((player.getViewXRot(tickDelta) - h) * 0.1f));
        matrices.mulPose(Axis.YP.rotationDegrees((player.getViewYRot(tickDelta) - i) * 0.1f));
        ItemVertexConsumerProvider dummy = new ItemVertexConsumerProvider();
        float anim = hand == InteractionHand.MAIN_HAND ? f : 0.0f;
        float height = 1.0f - Mth.lerp((float)tickDelta, (float)firstPersonRenderer.oMainHandHeight, (float)firstPersonRenderer.mainHandHeight);
        try {
            boolean highRes;
            itemBreakTransformation = new Matrix4f();
            firstPersonRenderer.renderArmWithItem((AbstractClientPlayer)player, tickDelta, xRot, mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, anim, item, height, matrices, (MultiBufferSource)dummy, light);
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(item, null, null, 1);
            TextureAtlasSprite sprite = model.getParticleIcon();
            SpriteContents contents = sprite.contents();
            float size = 1.0f / (float)contents.width() * 0.6666666f;
            float texelSizeX = 1.0f / (float)contents.width();
            float texelSizeY = 1.0f / (float)contents.height();
            float depthScale = 0.041666664f / size;
            itemBreakTransformation.translateLocal((float)camera.getPosition().x, (float)camera.getPosition().y, (float)camera.getPosition().z);
            boolean bl = highRes = contents.width() > 32 || contents.height() > 32;
            if (!highRes) {
                for (int x = 0; x < contents.width(); ++x) {
                    for (int y = 0; y < contents.height(); ++y) {
                        if (contents.isTransparent(0, x, y)) continue;
                        float uvx = (float)x / (float)contents.width() + texelSizeX * 0.5f;
                        float uvy = (float)y / (float)contents.height() + texelSizeY * 0.5f;
                        ParticleSpawner.spawnItemPhysicsParticle(sprite, level, uvx, 1.0f - uvy, 0.5, size, depthScale, uvx, uvy, itemBreakTransformation);
                    }
                }
            }
        }
        finally {
            itemBreakTransformation = null;
        }
    }

    private static void bobView(PoseStack matrices, float f) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player) {
            Player playerEntity = (Player)Minecraft.getInstance().getCameraEntity();
            float g = playerEntity.walkDist - playerEntity.walkDistO;
            float h = -(playerEntity.walkDist + g * f);
            float i = Mth.lerp((float)f, (float)playerEntity.oBob, (float)playerEntity.bob);
            matrices.translate((double)(Mth.sin((float)(h * (float)java.lang.Math.PI)) * i * 0.5f), (double)(-java.lang.Math.abs(Mth.cos((float)(h * (float)java.lang.Math.PI)) * i)), 0.0);
            matrices.mulPose(Axis.ZP.rotationDegrees(Mth.sin((float)(h * (float)java.lang.Math.PI)) * i * 3.0f));
            matrices.mulPose(Axis.XP.rotationDegrees(java.lang.Math.abs(Mth.cos((float)(h * (float)java.lang.Math.PI - 0.2f)) * i) * 5.0f));
        }
    }

    private static void bobViewWhenHurt(PoseStack matrices, float f) {
        if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity) {
            float i;
            LivingEntity livingEntity = (LivingEntity)Minecraft.getInstance().getCameraEntity();
            float g = (float)livingEntity.hurtTime - f;
            if (livingEntity.isDeadOrDying()) {
                i = java.lang.Math.min((float)livingEntity.deathTime + f, 20.0f);
                matrices.mulPose(Axis.ZP.rotationDegrees(40.0f - 8000.0f / (i + 200.0f)));
            }
            if (g < 0.0f) {
                return;
            }
            g /= (float)livingEntity.hurtDuration;
            g = Mth.sin((float)(g * g * g * g * (float)java.lang.Math.PI));
            i = livingEntity.getHurtDir();
            matrices.mulPose(Axis.YP.rotationDegrees(-i));
            matrices.mulPose(Axis.ZP.rotationDegrees(-g * 14.0f));
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
        int j;
        List<Mesh> meshesVoxel;
        List<Mesh> meshes;
        int i;
        CLOTH_DIRECTORY = ModLoaderFunctions.getGameDir().resolve("cloth_local");
        CLOTH_SYNCHRONIZED_DIRECTORY = ModLoaderFunctions.getGameDir().resolve(".physics_mod_cache/cloth");
        SNOWBALL_TEXTURE = ResourceLocation.parse((String)"physicsmod:textures/items/snowball.png");
        ENDERPEARL_TEXTURE = ResourceLocation.parse((String)"physicsmod:textures/items/enderpearl.png");
        EGG_TEXTURE = ResourceLocation.parse((String)"physicsmod:textures/items/egg.png");
        SMOKE_TEXTURE = ResourceLocation.parse((String)"physicsmod:textures/smoke/smoke.png");
        PUDDLE_TEXTURE = ResourceLocation.parse((String)"physicsmod:textures/ocean/puddle.png");
        shaderLight0 = new Vector3f();
        shaderLight1 = new Vector3f();
        instances = new Object2ObjectOpenHashMap();
        renderers = new Object2ObjectOpenHashMap();
        registeredBlocks = new Object2ObjectOpenHashMap();
        invRegisteredBlocks = new Object2ObjectOpenHashMap();
        registeredParticles = new Object2ObjectAVLTreeMap();
        invRegisteredParticles = new Object2ObjectOpenHashMap();
        registeredSounds = new Object2ObjectAVLTreeMap();
        invRegisteredSounds = new Object2ObjectOpenHashMap();
        sodiumBoundingBox = new AABB3D(new Vector3d(), new Vector3d());
        loadedModels = new ConcurrentHashMap<BakedModel, JsonUnbakedModelHolder>();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        optifineClothCompat = new ObjectArrayList();
        clothRenderFast = new ObjectArrayList();
        brokenBlocksLittle = new ObjectArrayList();
        brokenBlocksLots = new ObjectArrayList();
        brokenBlocksLittleVoxel = new ObjectArrayList();
        brokenBlocksLotsVoxel = new ObjectArrayList();
        snowballMesh = new ObjectArrayList();
        snowballMeshFractured = new ObjectArrayList();
        enderpearlMesh = new ObjectArrayList();
        enderpearlMeshFractured = new ObjectArrayList();
        eggMesh = new ObjectArrayList();
        eggMeshFractured = new ObjectArrayList();
        brokenBlocksLittle.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_1.obj"));
        brokenBlocksLittle.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_2.obj"));
        brokenBlocksLittle.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_3.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_2.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_3.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_4.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_5.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_6.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_7.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_8.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_9.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_10.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_11.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_12.obj"));
        brokenBlocksLots.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_13.obj"));
        brokenBlocksLittleVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_1_voxel.obj"));
        brokenBlocksLittleVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_2_voxel.obj"));
        brokenBlocksLittleVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_3_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_2_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_3_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_4_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_5_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_6_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_7_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_8_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_9_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_10_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_11_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_12_voxel.obj"));
        brokenBlocksLotsVoxel.add(PhysicsMod.readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_13_voxel.obj"));
        for (i = 0; i < brokenBlocksLittle.size(); ++i) {
            meshes = brokenBlocksLittle.get(i);
            meshesVoxel = brokenBlocksLittleVoxel.get(i);
            for (j = 0; j < meshes.size(); ++j) {
                meshesVoxel.get((int)j).offset = new Vector3f((Vector3fc)meshes.get((int)j).offset);
            }
        }
        for (i = 0; i < brokenBlocksLots.size(); ++i) {
            meshes = brokenBlocksLots.get(i);
            meshesVoxel = brokenBlocksLotsVoxel.get(i);
            for (j = 0; j < meshes.size(); ++j) {
                meshesVoxel.get((int)j).offset = new Vector3f((Vector3fc)meshes.get((int)j).offset);
            }
        }
        brokenBlock = PhysicsMod.readBlock("assets/physicsmod/models/fractures/physics_simple.obj");
        snowballMesh.add(PhysicsMod.readBlock("assets/physicsmod/models/snowball/snowball_voxel.obj").get(0));
        snowballMesh.add(PhysicsMod.readBlock("assets/physicsmod/models/snowball/snowball_round.obj").get(0));
        snowballMeshFractured.add(PhysicsMod.readBlock("assets/physicsmod/models/snowball/snowball_voxel_fractured.obj"));
        snowballMeshFractured.add(PhysicsMod.readBlock("assets/physicsmod/models/snowball/snowball_round_fractured.obj"));
        enderpearlMesh.add(PhysicsMod.readBlock("assets/physicsmod/models/enderpearl/enderpearl_voxel.obj").get(0));
        enderpearlMesh.add(PhysicsMod.readBlock("assets/physicsmod/models/enderpearl/enderpearl_round.obj").get(0));
        enderpearlMeshFractured.add(PhysicsMod.readBlock("assets/physicsmod/models/enderpearl/enderpearl_voxel_fractured.obj"));
        enderpearlMeshFractured.add(PhysicsMod.readBlock("assets/physicsmod/models/enderpearl/enderpearl_round_fractured.obj"));
        eggMesh.add(PhysicsMod.readBlock("assets/physicsmod/models/egg/egg_voxel.obj").get(0));
        eggMesh.add(PhysicsMod.readBlock("assets/physicsmod/models/egg/egg_round.obj").get(0));
        eggMeshFractured.add(PhysicsMod.readBlock("assets/physicsmod/models/egg/egg_voxel_fractured.obj"));
        eggMeshFractured.add(PhysicsMod.readBlock("assets/physicsmod/models/egg/egg_round_fractured.obj"));
        smoke = PhysicsMod.readBlock("assets/physicsmod/models/smoke/smoke.obj").get(0);
        liquid = PhysicsMod.readBlock("assets/physicsmod/models/liquid/liquid.obj").get(0);
        DIRECTIONS = Direction.values();
    }
}

