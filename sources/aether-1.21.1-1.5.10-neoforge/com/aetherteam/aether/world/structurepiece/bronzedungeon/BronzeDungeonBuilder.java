package com.aetherteam.aether.world.structurepiece.bronzedungeon;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.world.BlockLogicUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.SimpleWeightedRandomList.Builder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class BronzeDungeonBuilder {
   public static final Map<String, Builder<BronzeDungeonBuilder.RoomProvider<?>>> ROOM_OPTIONS_BUILDER = Map.ofEntries(
      Map.entry("boss_room", new Builder()),
      Map.entry("chest_room", new Builder()),
      Map.entry("end_corridor", new Builder()),
      Map.entry("entrance", new Builder()),
      Map.entry("lobby", new Builder()),
      Map.entry("square_tunnel", new Builder())
   );
   private static Map<String, SimpleWeightedRandomList<BronzeDungeonBuilder.RoomProvider<?>>> ROOM_OPTIONS;
   private final GenerationContext context;
   private final StructureTemplateManager manager;
   private final RandomSource random;
   private final BronzeProcessorSettings processors;
   private final int nodeWidth;
   private final int edgeWidth;
   private final int edgeLength;
   private final int maxSize;
   private final List<StructurePiece> nodes = new ArrayList<>();
   private final Map<StructurePiece, Map<Direction, BronzeDungeonBuilder.Connection>> edges = new HashMap<>();

   public BronzeDungeonBuilder(GenerationContext context, int maxSize, BronzeProcessorSettings processors) {
      this.context = context;
      this.manager = context.structureTemplateManager();
      this.random = context.random();
      this.processors = processors;
      Vec3i nodeSize = context.structureTemplateManager().getOrCreate(ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/chest_room")).getSize();
      this.nodeWidth = nodeSize.getX();
      Vec3i edgeSize = context.structureTemplateManager()
         .getOrCreate(ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/square_tunnel"))
         .getSize();
      this.edgeWidth = edgeSize.getX();
      this.edgeLength = edgeSize.getZ();
      this.maxSize = Math.max(3, maxSize);
   }

   public void initializeDungeon(BlockPos startPos, GenerationContext genContext, StructurePiecesBuilder builder) {
      ROOM_OPTIONS = ROOM_OPTIONS_BUILDER.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().build()));
      StructureTemplate bossTemplate = this.context
         .structureTemplateManager()
         .getOrCreate(ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/boss_room"));
      Rotation rotation = this.getBossRoomRotation(startPos, startPos.offset(bossTemplate.getSize()));
      if (rotation != null) {
         BronzeDungeonPiece bossRoom = this.chooseRoom("boss_room", startPos, rotation, this.processors.bossSettings());
         Direction direction = bossRoom.getOrientation();
         if (direction != null) {
            BlockPos pos = BlockLogicUtil.tunnelFromEvenSquareRoom(bossRoom.getBoundingBox().moved(0, 2, 0), direction, this.edgeWidth);
            BronzeDungeonPiece hallway = this.chooseRoom("square_tunnel", pos, bossRoom.getRotation(), this.processors.roomSettings());
            pos = BlockLogicUtil.tunnelFromEvenSquareRoom(hallway.getBoundingBox(), direction, this.nodeWidth);
            BronzeDungeonPiece defaultRoom = this.chooseRoom("chest_room", pos, hallway.getRotation(), this.processors.roomSettings());
            this.nodes.add(bossRoom);
            this.nodes.add(defaultRoom);
            new BronzeDungeonBuilder.Connection(bossRoom, defaultRoom, hallway, direction);
            ChunkPos chunkPos = genContext.chunkPos();

            for (int i = 2; i < this.maxSize - 1; i++) {
               this.propagateRooms(defaultRoom, chunkPos, false);
            }

            this.propagateRooms(defaultRoom, chunkPos, true);
            StructurePiece lobby = (StructurePiece)this.nodes.getLast();
            this.buildEndTunnel(lobby, startPos);
            this.buildSurfaceTunnel(genContext.heightAccessor(), genContext.chunkGenerator(), genContext.randomState());
            this.populatePiecesBuilder(builder);
         }
      }
   }

   private boolean propagateRooms(StructurePiece currentNode, ChunkPos chunkPos, boolean placeLobby) {
      Rotation rotation = currentNode.getRotation();
      List<Rotation> rotations = new ArrayList<>(3);
      rotations.add(rotation.getRotated(Rotation.COUNTERCLOCKWISE_90));
      rotations.add(rotation);
      rotations.add(rotation.getRotated(Rotation.CLOCKWISE_90));
      String roomName = placeLobby ? "lobby" : "chest_room";

      for (int i = 3; i > 0; i--) {
         rotation = rotations.remove(this.random.nextInt(i));
         Direction direction = rotation.rotate(Direction.SOUTH);
         if (this.hasConnection(currentNode, direction)) {
            if (this.propagateRooms(this.edges.get(currentNode).get(direction).end, chunkPos, placeLobby)) {
               return true;
            }
         } else {
            BlockPos pos = BlockLogicUtil.tunnelFromEvenSquareRoom(currentNode.getBoundingBox(), direction, this.edgeWidth);
            BronzeDungeonPiece hallway = this.chooseRoom("square_tunnel", pos, rotation, this.processors.roomSettings());
            pos = BlockLogicUtil.tunnelFromEvenSquareRoom(hallway.getBoundingBox(), direction, this.nodeWidth);
            BronzeDungeonPiece room = this.chooseRoom(roomName, pos, rotation, this.processors.roomSettings());
            StructurePiece collisionPiece = StructurePiece.findCollisionPiece(this.nodes, room.getBoundingBox());
            if (this.isCloseToCenter(chunkPos, room.templatePosition()) && this.isCoveredAtPos(room.getBoundingBox())) {
               if (collisionPiece == null) {
                  new BronzeDungeonBuilder.Connection(currentNode, room, hallway, direction);
                  this.nodes.add(room);
                  return true;
               }

               if (!(collisionPiece instanceof BronzeBossRoom)) {
                  boolean flag = this.edges
                     .computeIfAbsent(collisionPiece, piece -> new HashMap<>())
                     .values()
                     .stream()
                     .map(BronzeDungeonBuilder.Connection::endPiece)
                     .anyMatch(piece -> piece == currentNode);
                  if (!flag) {
                     new BronzeDungeonBuilder.Connection(currentNode, room, hallway, direction);
                  }
               }
            }
         }
      }

      return false;
   }

   private void buildEndTunnel(StructurePiece lobby, BlockPos origin) {
      Rotation rotation = lobby.getRotation();
      List<Rotation> rotations = new ArrayList<>(3);
      rotations.add(rotation.getRotated(Rotation.COUNTERCLOCKWISE_90));
      rotations.add(rotation);
      rotations.add(rotation.getRotated(Rotation.CLOCKWISE_90));
      List<StructurePiece> longestTunnel = null;

      for (int i = 3; i > 0; i--) {
         List<StructurePiece> tunnel = new ArrayList<>();
         rotation = rotations.remove(this.random.nextInt(i));
         Direction direction = rotation.rotate(Direction.SOUTH);
         if (this.buildTunnelFromRoom(lobby, tunnel, rotation, direction, origin)) {
            longestTunnel = tunnel;
            break;
         }

         if (longestTunnel == null || tunnel.size() > longestTunnel.size()) {
            longestTunnel = tunnel;
         }
      }

      this.nodes.addAll(longestTunnel);
   }

   @Nullable
   private StructurePiece seekLastRoomNode(int minWidth) {
      for (int i = this.nodes.size() - 1; i >= 0; i--) {
         StructurePiece piece = this.nodes.get(i);
         BoundingBox box = piece.getBoundingBox();
         if (box.getXSpan() > minWidth && box.getZSpan() > minWidth) {
            return piece;
         }
      }

      return null;
   }

   private void buildSurfaceTunnel(LevelHeightAccessor level, ChunkGenerator chunkGenerator, RandomState randomState) {
      int shrink = 3;
      StructurePiece lobby = this.seekLastRoomNode(6);
      if (lobby != null) {
         BoundingBox lobbyBounds = lobby.getBoundingBox();
         BlockPos entranceRoomCenter = lobbyBounds.getCenter();
         int topSurfaceY = chunkGenerator.getFirstOccupiedHeight(entranceRoomCenter.getX(), entranceRoomCenter.getZ(), Types.OCEAN_FLOOR_WG, level, randomState);
         int roomCeiling = lobbyBounds.maxY() + 1;
         if (roomCeiling <= topSurfaceY) {
            int ruinsTopY = Math.max(roomCeiling, topSurfaceY + 4);
            int minX = lobbyBounds.minX() + 3;
            int minZ = lobbyBounds.minZ() + 3;
            int maxX = lobbyBounds.maxX() - 3;
            int maxZ = lobbyBounds.maxZ() - 3;
            BoundingBox upwardsTunnelBox = new BoundingBox(
               Math.min(minX, maxX), roomCeiling, Math.min(minZ, maxZ), Math.max(minX, maxX), ruinsTopY, Math.max(minZ, maxZ)
            );
            this.nodes.add(new BronzeDungeonSurfaceRuins(upwardsTunnelBox));
         }
      }
   }

   public boolean buildTunnelFromRoom(StructurePiece connectedRoom, List<StructurePiece> list, Rotation rotation, Direction direction, BlockPos origin) {
      StructureTemplate template = this.manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/entrance"));
      BlockPos startPos = BlockLogicUtil.tunnelFromEvenSquareRoom(connectedRoom.getBoundingBox(), direction, template.getSize().getX());
      BronzeDungeonPiece entrance = this.chooseRoom("entrance", startPos, rotation, this.processors.roomSettings());
      list.add(entrance);
      startPos = startPos.relative(direction);
      int length = template.getSize().getZ();
      boolean noOverlap = false;
      boolean reachedAir = false;
      int i = 0;

      BlockPos pos;
      do {
         pos = startPos.relative(direction, i);
         BronzeDungeonPiece tunnel = this.chooseRoom("end_corridor", pos, rotation, this.processors.tunnelSettings());
         StructurePiece col = null;

         for (StructurePiece piece : this.nodes) {
            if (piece != null && piece != connectedRoom && piece.getBoundingBox().intersects(tunnel.getBoundingBox())) {
               col = piece;
               break;
            }
         }

         if (col != null) {
            break;
         }

         noOverlap = true;
         list.add(tunnel);
         connectedRoom = tunnel;
         i += length;
         if (this.checkForAirAtPos(pos.getX(), pos.getY(), pos.getZ()) && this.checkForAirAtPos(pos.getX(), tunnel.getBoundingBox().maxY(), pos.getZ())) {
            reachedAir = true;
            break;
         }
      } while (Math.abs(origin.getX() - pos.getX()) < 100 && Math.abs(origin.getZ() - pos.getZ()) < 100);

      return noOverlap && reachedAir;
   }

   public BronzeDungeonPiece chooseRoom(String name, BlockPos pos, Rotation rotation, Holder<StructureProcessorList> processors) {
      SimpleWeightedRandomList<BronzeDungeonBuilder.RoomProvider<?>> list = ROOM_OPTIONS.get(name);
      if (list != null) {
         Optional<BronzeDungeonBuilder.RoomProvider<?>> option = list.getRandomValue(this.random);
         if (option.isPresent()) {
            return option.get().provide(this.manager, pos, rotation, processors);
         }
      }

      return new BronzeDungeonRoom(this.manager, name, pos, rotation, processors);
   }

   public void populatePiecesBuilder(StructurePiecesBuilder builder) {
      StructurePiece bossRoom = (StructurePiece)this.nodes.removeFirst();
      this.nodes.forEach(builder::addPiece);
      this.edges.values().forEach(map -> map.values().forEach(connection -> builder.addPiece(connection.hallway)));
      builder.addPiece(bossRoom);
   }

   private boolean hasConnection(StructurePiece node, Direction direction) {
      Map<Direction, BronzeDungeonBuilder.Connection> map = this.edges.get(node);
      return map != null && map.containsKey(direction);
   }

   private boolean checkForAirAtPos(int x, int y, int z) {
      NoiseColumn column = this.context.chunkGenerator().getBaseColumn(x, z, this.context.heightAccessor(), this.context.randomState());
      return column.getBlock(y).isAir();
   }

   private boolean isCloseToCenter(ChunkPos chunkPos, BlockPos pos) {
      ChunkPos currentChunk = new ChunkPos(pos);
      return chunkPos.getChessboardDistance(currentChunk) <= 3;
   }

   private boolean isCoveredAtPos(BoundingBox room) {
      ChunkGenerator chunkGenerator = this.context.chunkGenerator();
      LevelHeightAccessor heightAccessor = this.context.heightAccessor();
      RandomState randomState = this.context.randomState();
      int minX = room.minX() - 1;
      int minZ = room.minZ() - 1;
      int maxX = room.maxX() + 1;
      int maxZ = room.maxZ() + 1;
      NoiseColumn[] columns = new NoiseColumn[]{
         chunkGenerator.getBaseColumn(minX, minZ, heightAccessor, randomState),
         chunkGenerator.getBaseColumn(minX, maxZ, heightAccessor, randomState),
         chunkGenerator.getBaseColumn(maxX, minZ, heightAccessor, randomState),
         chunkGenerator.getBaseColumn(maxX, maxZ, heightAccessor, randomState)
      };
      return isSolidInColumns(columns, room.minY() - 1, room.maxY() + 1);
   }

   @Nullable
   private Rotation getBossRoomRotation(BlockPos minPos, BlockPos maxPos) {
      StructureTemplate template = this.context
         .structureTemplateManager()
         .getOrCreate(ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/chest_room"));
      RandomSource random = this.context.random();
      BoundingBox bossBox = new BoundingBox(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());

      for (Rotation rotation : Rotation.getShuffled(random)) {
         Direction direction = rotation.rotate(Direction.SOUTH);
         MutableBlockPos neighbor = BlockLogicUtil.tunnelFromEvenSquareRoom(bossBox, direction, this.nodeWidth).mutable();
         neighbor = neighbor.move(
            direction.getStepX() * (this.edgeLength + bossBox.getXSpan()), 0, direction.getStepZ() * (this.edgeLength + bossBox.getZSpan())
         );
         if (this.isCoveredAtPos(template.getBoundingBox(neighbor, rotation, BlockPos.ZERO, Mirror.NONE))) {
            return rotation;
         }
      }

      return null;
   }

   private static boolean isSolidInColumns(NoiseColumn[] columns, int minY, int maxY) {
      for (NoiseColumn column : columns) {
         for (int y = minY; y <= maxY; y++) {
            if (column.getBlock(y).isAir() || column.getBlock(y).is(AetherTags.Blocks.NON_BRONZE_DUNGEON_SPAWNABLE)) {
               return false;
            }
         }
      }

      return true;
   }

   static {
      ROOM_OPTIONS_BUILDER.get("boss_room")
         .add(
            (BronzeDungeonBuilder.RoomProvider<BronzeDungeonPiece>)(manager, pos, rotation, processorList) -> new BronzeBossRoom(
               manager, "boss_room", pos, rotation, processorList
            ),
            1
         );
      ROOM_OPTIONS_BUILDER.get("chest_room")
         .add(
            (BronzeDungeonBuilder.RoomProvider<BronzeDungeonPiece>)(manager, pos, rotation, processorList) -> new BronzeDungeonRoom(
               manager, "chest_room", pos, rotation, processorList
            ),
            1
         );
      ROOM_OPTIONS_BUILDER.get("end_corridor")
         .add(
            (BronzeDungeonBuilder.RoomProvider<BronzeDungeonPiece>)(manager, pos, rotation, processorList) -> new BronzeTunnel(
               manager, "end_corridor", pos, rotation, processorList
            ),
            1
         );
      ROOM_OPTIONS_BUILDER.get("entrance")
         .add(
            (BronzeDungeonBuilder.RoomProvider<BronzeDungeonPiece>)(manager, pos, rotation, processorList) -> new BronzeDungeonRoom(
               manager, "entrance", pos, rotation, processorList
            ),
            1
         );
      ROOM_OPTIONS_BUILDER.get("lobby")
         .add(
            (BronzeDungeonBuilder.RoomProvider<BronzeDungeonPiece>)(manager, pos, rotation, processorList) -> new BronzeDungeonRoom(
               manager, "lobby", pos, rotation, processorList
            ),
            1
         );
      ROOM_OPTIONS_BUILDER.get("square_tunnel")
         .add(
            (BronzeDungeonBuilder.RoomProvider<BronzeDungeonPiece>)(manager, pos, rotation, processorList) -> new BronzeDungeonRoom(
               manager, "square_tunnel", pos, rotation, processorList
            ),
            1
         );
   }

   private class Connection {
      public final StructurePiece start;
      public final StructurePiece end;
      public final StructurePiece hallway;

      public Connection(StructurePiece start, StructurePiece end, StructurePiece hallway, Direction direction) {
         this.start = start;
         this.end = end;
         this.hallway = hallway;
         BronzeDungeonBuilder.this.edges.computeIfAbsent(start, piece -> new HashMap<>()).put(direction, this);
      }

      public StructurePiece endPiece() {
         return this.end;
      }
   }

   @FunctionalInterface
   public interface RoomProvider<T extends BronzeDungeonPiece> {
      T provide(StructureTemplateManager var1, BlockPos var2, Rotation var3, Holder<StructureProcessorList> var4);
   }
}
