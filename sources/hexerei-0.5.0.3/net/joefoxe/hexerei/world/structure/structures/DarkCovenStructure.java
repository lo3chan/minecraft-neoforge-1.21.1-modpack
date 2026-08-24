package net.joefoxe.hexerei.world.structure.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.ConnectedPillarBlock;
import net.joefoxe.hexerei.world.structure.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class DarkCovenStructure extends Structure {
   public static final MapCodec<DarkCovenStructure> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
         )
         .apply(instance, DarkCovenStructure::new)
   );
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int size;
   private final HeightProvider startHeight;
   private final Optional<Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;
   static final Logger LOGGER = LogUtils.getLogger();

   public DarkCovenStructure(
      StructureSettings config,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawName,
      int size,
      HeightProvider startHeight,
      Optional<Types> projectStartToHeightmap,
      int maxDistanceFromCenter
   ) {
      super(config);
      this.startPool = startPool;
      this.startJigsawName = startJigsawName;
      this.size = size;
      this.startHeight = startHeight;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.maxDistanceFromCenter = maxDistanceFromCenter;
   }

   private boolean canBeReplaced(BlockState currBlock) {
      return currBlock.canBeReplaced()
         || currBlock.isAir()
         || currBlock.is(BlockTags.LEAVES)
         || currBlock.is((Block)ModBlocks.WILLOW_FENCE.get())
         || currBlock.is((Block)ModBlocks.WILLOW_SLAB.get())
         || currBlock.is((Block)ModBlocks.WILLOW_STAIRS.get())
         || currBlock.is((Block)ModBlocks.MAHOGANY_SLAB.get())
         || currBlock.is((Block)ModBlocks.MAHOGANY_STAIRS.get())
         || currBlock.is(Blocks.DARK_OAK_SLAB)
         || currBlock.is(Blocks.DARK_OAK_STAIRS)
         || currBlock.is(Blocks.WATER)
         || currBlock.is(Blocks.LAVA);
   }

   public void afterPlace(
      WorldGenLevel pLevel,
      StructureManager pStructureManager,
      ChunkGenerator pChunkGenerator,
      RandomSource pRandom,
      BoundingBox pBoundingBox,
      ChunkPos pChunkPos,
      PiecesContainer pPieces
   ) {
      super.afterPlace(pLevel, pStructureManager, pChunkGenerator, pRandom, pBoundingBox, pChunkPos, pPieces);

      try {
         for (BlockPos blockPos : BlockPos.betweenClosed(
            pBoundingBox.minX(), pBoundingBox.minY(), pBoundingBox.minZ(), pBoundingBox.maxX(), pBoundingBox.maxY(), pBoundingBox.maxZ()
         )) {
            if (pPieces.isInsidePiece(blockPos) && pLevel.isAreaLoaded(blockPos, 1) && pLevel.getBlockState(blockPos).is(Blocks.YELLOW_STAINED_GLASS_PANE)) {
               if (pLevel instanceof ServerLevel serverLevel) {
                  serverLevel.setBlockAndUpdate(blockPos, ((ConnectedPillarBlock)ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get()).defaultBlockState());
                  MutableBlockPos mutable = blockPos.below().mutable();

                  for (BlockState currBlock = pLevel.getBlockState(mutable);
                     mutable.getY() > 0 && this.canBeReplaced(currBlock);
                     currBlock = serverLevel.getBlockState(mutable)
                  ) {
                     serverLevel.setBlockAndUpdate(mutable, Blocks.DARK_OAK_LOG.defaultBlockState());
                     mutable.move(Direction.DOWN);
                  }
               } else if (pLevel instanceof WorldGenRegion worldGenRegion) {
                  worldGenRegion.setBlock(blockPos, ((ConnectedPillarBlock)ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get()).defaultBlockState(), 3);
                  MutableBlockPos mutable = blockPos.below().mutable();

                  for (BlockState currBlock = pLevel.getBlockState(mutable);
                     mutable.getY() > 0
                        && (
                           currBlock.canBeReplaced()
                              || currBlock.isAir()
                              || currBlock.is(BlockTags.LEAVES)
                              || currBlock.is(Blocks.WATER)
                              || currBlock.is(Blocks.LAVA)
                        );
                     currBlock = worldGenRegion.getBlockState(mutable)
                  ) {
                     worldGenRegion.setBlock(mutable, Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
                     mutable.move(Direction.DOWN);
                  }
               }
            }
         }
      } catch (Exception var14) {
         var14.printStackTrace();
      }
   }

   public static Optional<GenerationStub> addPieces(
      GenerationContext pContext,
      Holder<StructureTemplatePool> pStartPool,
      Optional<ResourceLocation> pStartJigsawName,
      int pMaxDepth,
      BlockPos pPos,
      boolean pUseExpansionHack,
      Optional<Types> pProjectStartToHeightmap,
      int pMaxDistanceFromCenter
   ) {
      RegistryAccess registryaccess = pContext.registryAccess();
      ChunkGenerator chunkgenerator = pContext.chunkGenerator();
      StructureTemplateManager structuretemplatemanager = pContext.structureTemplateManager();
      LevelHeightAccessor levelheightaccessor = pContext.heightAccessor();
      WorldgenRandom worldgenrandom = pContext.random();
      Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registries.TEMPLATE_POOL);
      Rotation rotation = Rotation.getRandom(worldgenrandom);
      StructureTemplatePool structuretemplatepool = (StructureTemplatePool)pStartPool.value();
      StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
      if (structurepoolelement == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         BlockPos blockpos;
         if (pStartJigsawName.isPresent()) {
            ResourceLocation resourcelocation = pStartJigsawName.get();
            Optional<BlockPos> optional = getRandomNamedJigsaw(structurepoolelement, resourcelocation, pPos, rotation, structuretemplatemanager, worldgenrandom);
            if (optional.isEmpty()) {
               LOGGER.error(
                  "No starting jigsaw {} found in start pool {}",
                  resourcelocation,
                  pStartPool.unwrapKey().map(p_248484_ -> p_248484_.location().toString()).orElse("<unregistered>")
               );
               return Optional.empty();
            }

            blockpos = optional.get();
         } else {
            blockpos = pPos;
         }

         Vec3i vec3i = blockpos.subtract(pPos);
         BlockPos blockpos1 = pPos.subtract(vec3i);
         PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(
            structuretemplatemanager,
            structurepoolelement,
            blockpos1,
            structurepoolelement.getGroundLevelDelta(),
            rotation,
            structurepoolelement.getBoundingBox(structuretemplatemanager, blockpos1, rotation),
            LiquidSettings.APPLY_WATERLOGGING
         );
         BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
         int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
         int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
         int k;
         if (pProjectStartToHeightmap.isPresent()) {
            k = pPos.getY() + chunkgenerator.getFirstFreeHeight(i, j, pProjectStartToHeightmap.get(), levelheightaccessor, pContext.randomState());
         } else {
            k = blockpos1.getY();
         }

         int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
         poolelementstructurepiece.move(0, k - l, 0);
         int i1 = k + vec3i.getY();
         Consumer<StructurePiecesBuilder> pGenerator = structurePiecesBuilder -> {
            List<PoolElementStructurePiece> list = Lists.newArrayList();
            list.add(poolelementstructurepiece);
            if (pMaxDepth > 0) {
               AABB aabb = new AABB(
                  i - pMaxDistanceFromCenter,
                  i1 - pMaxDistanceFromCenter,
                  j - pMaxDistanceFromCenter,
                  i + pMaxDistanceFromCenter + 1,
                  i1 + pMaxDistanceFromCenter + 1,
                  j + pMaxDistanceFromCenter + 1
               );
               VoxelShape voxelshape = Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST);
               addPieces(
                  pContext.randomState(),
                  pMaxDepth,
                  pUseExpansionHack,
                  chunkgenerator,
                  structuretemplatemanager,
                  levelheightaccessor,
                  worldgenrandom,
                  registry,
                  poolelementstructurepiece,
                  list,
                  voxelshape,
                  pProjectStartToHeightmap.orElse(null)
               );
               list.forEach(structurePiecesBuilder::addPiece);
            }
         };
         return Optional.of(new GenerationStub(new BlockPos(i, i1, j), pGenerator));
      }
   }

   private static void addPieces(
      RandomState pRandomState,
      int pMaxDepth,
      boolean pUseExpansionHack,
      ChunkGenerator pChunkGenerator,
      StructureTemplateManager pStructureTemplateManager,
      LevelHeightAccessor pLevel,
      RandomSource pRandom,
      Registry<StructureTemplatePool> pPools,
      PoolElementStructurePiece p_227219_,
      List<PoolElementStructurePiece> pPieces,
      VoxelShape p_227221_,
      Types pType
   ) {
      DarkCovenStructure.Placer jigsawplacement$placer = new DarkCovenStructure.Placer(
         pPools, pMaxDepth, pChunkGenerator, pStructureTemplateManager, pPieces, pRandom
      );
      jigsawplacement$placer.placing.addLast(new DarkCovenStructure.PieceState(p_227219_, new MutableObject(p_227221_), 0));

      while (!jigsawplacement$placer.placing.isEmpty()) {
         DarkCovenStructure.PieceState jigsawplacement$piecestate = jigsawplacement$placer.placing.removeFirst();
         jigsawplacement$placer.tryPlacingChildren(
            jigsawplacement$piecestate.piece,
            jigsawplacement$piecestate.free,
            jigsawplacement$piecestate.depth,
            pUseExpansionHack,
            pLevel,
            pRandomState,
            pChunkGenerator,
            pType
         );
      }
   }

   private static Optional<BlockPos> getRandomNamedJigsaw(
      StructurePoolElement pElement,
      ResourceLocation pStartJigsawName,
      BlockPos pPos,
      Rotation pRotation,
      StructureTemplateManager pStructureTemplateManager,
      WorldgenRandom pRandom
   ) {
      List<StructureBlockInfo> list = pElement.getShuffledJigsawBlocks(pStructureTemplateManager, pPos, pRotation, pRandom);
      Optional<BlockPos> optional = Optional.empty();

      for (StructureBlockInfo usingStructureBlockInf : list) {
         ResourceLocation resourcelocation = ResourceLocation.tryParse(usingStructureBlockInf.nbt().getString("name"));
         if (pStartJigsawName.equals(resourcelocation)) {
            optional = Optional.of(usingStructureBlockInf.pos());
            break;
         }
      }

      return optional;
   }

   private static boolean extraSpawningChecks(GenerationContext context) {
      ChunkPos chunkpos = context.chunkPos();
      return context.chunkGenerator()
            .getFirstOccupiedHeight(
               chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState()
            )
         < 150;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      if (!extraSpawningChecks(context)) {
         return Optional.empty();
      } else {
         int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
         ChunkPos chunkPos = context.chunkPos();
         BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());
         return addPieces(context, this.startPool, this.startJigsawName, this.size, blockPos, false, this.projectStartToHeightmap, this.maxDistanceFromCenter);
      }
   }

   public StructureType<?> type() {
      return (StructureType<?>)ModStructures.DARK_COVEN.get();
   }

   static final class PieceState {
      final PoolElementStructurePiece piece;
      final MutableObject<VoxelShape> free;
      final int depth;

      PieceState(PoolElementStructurePiece pPiece, MutableObject<VoxelShape> pFree, int pDepth) {
         this.piece = pPiece;
         this.free = pFree;
         this.depth = pDepth;
      }
   }

   static final class Placer {
      private final Registry<StructureTemplatePool> pools;
      private final int maxDepth;
      private final ChunkGenerator chunkGenerator;
      private final StructureTemplateManager structureTemplateManager;
      private final List<? super PoolElementStructurePiece> pieces;
      private final RandomSource random;
      final Deque<DarkCovenStructure.PieceState> placing = Queues.newArrayDeque();

      Placer(
         Registry<StructureTemplatePool> pPools,
         int pMaxDepth,
         ChunkGenerator pChunkGenerator,
         StructureTemplateManager pStructureTemplateManager,
         List<? super PoolElementStructurePiece> pPieces,
         RandomSource pRandom
      ) {
         this.pools = pPools;
         this.maxDepth = pMaxDepth;
         this.chunkGenerator = pChunkGenerator;
         this.structureTemplateManager = pStructureTemplateManager;
         this.pieces = pPieces;
         this.random = pRandom;
      }

      void tryPlacingChildren(
         PoolElementStructurePiece pPiece,
         MutableObject<VoxelShape> pFree,
         int pDepth,
         boolean pUseExpansionHack,
         LevelHeightAccessor pLevel,
         RandomState pRandomState,
         ChunkGenerator chunkGenerator,
         Types pType
      ) {
         StructurePoolElement structurepoolelement = pPiece.getElement();
         BlockPos blockpos = pPiece.getPosition();
         Rotation rotation = pPiece.getRotation();
         Projection structuretemplatepool$projection = structurepoolelement.getProjection();
         boolean flag = structuretemplatepool$projection == Projection.RIGID;
         MutableObject<VoxelShape> mutableobject = new MutableObject();
         BoundingBox boundingbox = pPiece.getBoundingBox();
         int i = boundingbox.minY();

         label222:
         for (StructureBlockInfo structuretemplate$structureblockinfo : structurepoolelement.getShuffledJigsawBlocks(
            this.structureTemplateManager, blockpos, rotation, this.random
         )) {
            Direction direction = JigsawBlock.getFrontFacing(structuretemplate$structureblockinfo.state());
            BlockPos blockpos1 = structuretemplate$structureblockinfo.pos();
            BlockPos blockpos2 = blockpos1.relative(direction);
            int j = blockpos1.getY() - i;
            int k = -1;
            BlockPos forwards = blockpos1.relative(direction, 15);
            BlockPos left = blockpos1.relative(direction, 10).relative(direction.getCounterClockWise(), 6);
            BlockPos right = blockpos1.relative(direction, 10).relative(direction.getClockWise(), 6);
            int groundLevel = chunkGenerator.getFirstFreeHeight(blockpos2.getX(), blockpos2.getZ(), pType, pLevel, pRandomState);
            int distToGround = blockpos2.getY() - groundLevel;
            boolean stairsFlag = false;
            CompoundTag tag = structuretemplate$structureblockinfo.nbt() != null ? structuretemplate$structureblockinfo.nbt().copy() : new CompoundTag();
            if (distToGround < 3 && distToGround > -5 && tag.contains("name") && tag.getString("name").equals("minecraft:street")) {
               tag.putString("pool", "hexerei:coven/dark_coven/stairs");
               tag.putString("target", "minecraft:stairs_up");
               stairsFlag = true;
            } else if (distToGround > 8 && distToGround <= 15 && tag.contains("name") && tag.getString("name").equals("minecraft:street")) {
               tag.putString("pool", "hexerei:coven/dark_coven/stairs");
               tag.putString("target", "minecraft:stairs_down");
               stairsFlag = true;
            } else if (distToGround > 15 && tag.contains("name") && tag.getString("name").equals("minecraft:street")) {
               int groundLevelRight = chunkGenerator.getFirstFreeHeight(right.getX(), right.getZ(), pType, pLevel, pRandomState);
               int distToGroundRight = right.getY() - groundLevelRight;
               int groundLevelLeft = chunkGenerator.getFirstFreeHeight(left.getX(), left.getZ(), pType, pLevel, pRandomState);
               int distToGroundLeft = left.getY() - groundLevelLeft;
               int groundLevelForwards = chunkGenerator.getFirstFreeHeight(forwards.getX(), forwards.getZ(), pType, pLevel, pRandomState);
               int distToGroundForwards = forwards.getY() - groundLevelForwards;
               if (this.random.nextFloat() > 0.4F) {
                  if (distToGroundForwards < 8) {
                     tag.putString("pool", "hexerei:coven/dark_coven/streets");
                     tag.putString("target", "minecraft:street");
                     tag.putString("name", "minecraft:street");
                  } else if (distToGroundLeft >= distToGroundRight) {
                     tag.putString("pool", "hexerei:coven/dark_coven/stairs");
                     tag.putString("target", "minecraft:stairs_down_turn_right");
                  } else {
                     tag.putString("pool", "hexerei:coven/dark_coven/stairs");
                     tag.putString("target", "minecraft:stairs_down_turn_left");
                  }

                  stairsFlag = true;
               }
            } else if (tag.contains("name")
               && tag.getString("name").equals("minecraft:street")
               && tag.contains("target")
               && tag.getString("target").equals("minecraft:stairs_up")) {
               tag.putString("pool", "hexerei:coven/dark_coven/streets");
               tag.putString("target", "minecraft:street");
               tag.putString("name", "minecraft:street");
            } else if (tag.contains("name")
               && tag.getString("name").equals("minecraft:street")
               && tag.contains("target")
               && tag.getString("target").equals("minecraft:stairs_down")) {
               tag.putString("pool", "hexerei:coven/dark_coven/streets");
               tag.putString("target", "minecraft:street");
               tag.putString("name", "minecraft:street");
            } else if (tag.contains("name")
               && tag.getString("name").equals("minecraft:street")
               && tag.contains("target")
               && tag.getString("target").equals("minecraft:stairs_down_turn")) {
               tag.putString("pool", "hexerei:coven/dark_coven/streets");
               tag.putString("target", "minecraft:street");
               tag.putString("name", "minecraft:street");
            }

            StructureBlockInfo usingStructureBlockInf = new StructureBlockInfo(
               structuretemplate$structureblockinfo.pos(), structuretemplate$structureblockinfo.state(), tag
            );
            ResourceKey<StructureTemplatePool> resourcekey = readPoolName(usingStructureBlockInf);
            Optional<? extends Holder<StructureTemplatePool>> optional = this.pools.getHolder(resourcekey);
            if (optional.isEmpty()) {
               DarkCovenStructure.LOGGER.warn("Empty or non-existent pool: {}", resourcekey.location());
            } else {
               Holder<StructureTemplatePool> holder = (Holder<StructureTemplatePool>)optional.get();
               if (((StructureTemplatePool)holder.value()).size() == 0 && !holder.is(Pools.EMPTY)) {
                  DarkCovenStructure.LOGGER.warn("Empty or non-existent pool: {}", resourcekey.location());
               } else {
                  Holder<StructureTemplatePool> holder1 = ((StructureTemplatePool)holder.value()).getFallback();
                  if (((StructureTemplatePool)holder1.value()).size() == 0 && !holder1.is(Pools.EMPTY)) {
                     DarkCovenStructure.LOGGER
                        .warn(
                           "Empty or non-existent fallback pool: {}",
                           holder1.unwrapKey().map(p_255599_ -> p_255599_.location().toString()).orElse("<unregistered>")
                        );
                  } else {
                     boolean flag1 = boundingbox.isInside(blockpos2);
                     MutableObject<VoxelShape> mutableobject1;
                     if (flag1) {
                        mutableobject1 = mutableobject;
                        if (mutableobject.getValue() == null) {
                           mutableobject.setValue(Shapes.create(AABB.of(boundingbox)));
                        }
                     } else {
                        mutableobject1 = pFree;
                     }

                     List<StructurePoolElement> list = Lists.newArrayList();
                     if (pDepth != this.maxDepth) {
                        list.addAll(((StructureTemplatePool)holder.value()).getShuffledTemplates(this.random));
                     }

                     list.addAll(((StructureTemplatePool)holder1.value()).getShuffledTemplates(this.random));

                     for (StructurePoolElement structurepoolelement1 : list) {
                        if (structurepoolelement1 == EmptyPoolElement.INSTANCE) {
                           break;
                        }

                        for (Rotation rotation1 : Rotation.getShuffled(this.random)) {
                           List<StructureBlockInfo> list1 = structurepoolelement1.getShuffledJigsawBlocks(
                              this.structureTemplateManager, BlockPos.ZERO, rotation1, this.random
                           );
                           BoundingBox boundingbox1 = structurepoolelement1.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation1);
                           int l;
                           if (pUseExpansionHack && boundingbox1.getYSpan() <= 16) {
                              l = list1.stream()
                                 .mapToInt(
                                    p_255598_ -> {
                                       if (!boundingbox1.isInside(p_255598_.pos().relative(JigsawBlock.getFrontFacing(p_255598_.state())))) {
                                          return 0;
                                       } else {
                                          ResourceKey<StructureTemplatePool> resourcekey1 = readPoolName(p_255598_);
                                          Optional<? extends Holder<StructureTemplatePool>> optional1 = this.pools.getHolder(resourcekey1);
                                          Optional<Holder<StructureTemplatePool>> optional2 = optional1.map(
                                             p_255600_ -> ((StructureTemplatePool)p_255600_.value()).getFallback()
                                          );
                                          int j3 = optional1.<Integer>map(
                                                p_255596_ -> ((StructureTemplatePool)p_255596_.value()).getMaxSize(this.structureTemplateManager)
                                             )
                                             .orElse(0);
                                          int k3 = optional2.<Integer>map(
                                                p_255601_ -> ((StructureTemplatePool)p_255601_.value()).getMaxSize(this.structureTemplateManager)
                                             )
                                             .orElse(0);
                                          return Math.max(j3, k3);
                                       }
                                    }
                                 )
                                 .max()
                                 .orElse(0);
                           } else {
                              l = 0;
                           }

                           List<StructureBlockInfo> list2 = new ArrayList<>();

                           for (StructureBlockInfo usingStructureBlockInf1 : list1) {
                              CompoundTag tag2 = usingStructureBlockInf1.nbt();
                              if (stairsFlag && tag2 != null && tag2.contains("name") && tag2.getString("name").equals("minecraft:stairs_street")) {
                                 tag2.putString("pool", "hexerei:coven/dark_coven/streets");
                                 tag2.putString("target", "minecraft:street");
                                 tag2.putString("name", "minecraft:street");
                              }

                              list2.add(new StructureBlockInfo(usingStructureBlockInf1.pos(), usingStructureBlockInf1.state(), tag2));
                           }

                           for (StructureBlockInfo usingStructureBlockInf1 : list2) {
                              if (JigsawBlock.canAttach(usingStructureBlockInf, usingStructureBlockInf1)) {
                                 BlockPos blockpos3 = usingStructureBlockInf1.pos();
                                 BlockPos blockpos4 = blockpos2.subtract(blockpos3);
                                 BoundingBox boundingbox2 = structurepoolelement1.getBoundingBox(this.structureTemplateManager, blockpos4, rotation1);
                                 int i1 = boundingbox2.minY();
                                 Projection structuretemplatepool$projection1 = structurepoolelement1.getProjection();
                                 boolean flag2 = structuretemplatepool$projection1 == Projection.RIGID;
                                 int j1 = blockpos3.getY();
                                 int k1 = j - j1 + JigsawBlock.getFrontFacing(usingStructureBlockInf.state()).getStepY();
                                 int l1;
                                 if (flag && flag2) {
                                    l1 = i + k1;
                                 } else {
                                    if (k == -1) {
                                       k = this.chunkGenerator
                                          .getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Types.WORLD_SURFACE_WG, pLevel, pRandomState);
                                    }

                                    l1 = k - j1;
                                 }

                                 int i2 = l1 - i1;
                                 BoundingBox boundingbox3 = boundingbox2.moved(0, i2, 0);
                                 BlockPos blockpos5 = blockpos4.offset(0, i2, 0);
                                 if (l > 0) {
                                    int j2 = Math.max(l + 1, boundingbox3.maxY() - boundingbox3.minY());
                                    boundingbox3.encapsulate(new BlockPos(boundingbox3.minX(), boundingbox3.minY() + j2, boundingbox3.minZ()));
                                 }

                                 if (!Shapes.joinIsNotEmpty(
                                    (VoxelShape)mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3).deflate(0.25)), BooleanOp.ONLY_SECOND
                                 )) {
                                    mutableobject1.setValue(
                                       Shapes.joinUnoptimized((VoxelShape)mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3)), BooleanOp.ONLY_FIRST)
                                    );
                                    int i3 = pPiece.getGroundLevelDelta();
                                    int k2;
                                    if (flag2) {
                                       k2 = i3 - k1;
                                    } else {
                                       k2 = structurepoolelement1.getGroundLevelDelta();
                                    }

                                    PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(
                                       this.structureTemplateManager,
                                       structurepoolelement1,
                                       blockpos5,
                                       k2,
                                       rotation1,
                                       boundingbox3,
                                       LiquidSettings.APPLY_WATERLOGGING
                                    );
                                    int l2;
                                    if (flag) {
                                       l2 = i + j;
                                    } else if (flag2) {
                                       l2 = l1 + j1;
                                    } else {
                                       if (k == -1) {
                                          k = this.chunkGenerator
                                             .getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Types.WORLD_SURFACE_WG, pLevel, pRandomState);
                                       }

                                       l2 = k + k1 / 2;
                                    }

                                    pPiece.addJunction(
                                       new JigsawJunction(blockpos2.getX(), l2 - j + i3, blockpos2.getZ(), k1, structuretemplatepool$projection1)
                                    );
                                    poolelementstructurepiece.addJunction(
                                       new JigsawJunction(blockpos1.getX(), l2 - j1 + k2, blockpos1.getZ(), -k1, structuretemplatepool$projection)
                                    );
                                    this.pieces.add(poolelementstructurepiece);
                                    if (pDepth + 1 <= this.maxDepth) {
                                       this.placing.addLast(new DarkCovenStructure.PieceState(poolelementstructurepiece, mutableobject1, pDepth + 1));
                                    }
                                    continue label222;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      private static ResourceKey<StructureTemplatePool> readPoolName(StructureBlockInfo pStructureBlockInfo) {
         return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.parse(pStructureBlockInfo.nbt().getString("pool")));
      }
   }
}
