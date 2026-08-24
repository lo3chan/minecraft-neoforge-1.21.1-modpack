package org.dimdev.limlib.api.world;

import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;

public class NbtPlacerUtil {
   public final CompoundTag storedNbt;
   public final HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions;
   public final ListTag entities;
   public final BlockPos lowestPos;
   public final int sizeX;
   public final int sizeY;
   public final int sizeZ;
   public final Vec3i sizeVector;

   public NbtPlacerUtil(
      CompoundTag storedNbt,
      HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions,
      ListTag entities,
      BlockPos lowestPos,
      int sizeX,
      int sizeY,
      int sizeZ
   ) {
      this.storedNbt = storedNbt;
      this.positions = positions;
      this.entities = entities;
      this.lowestPos = lowestPos;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.sizeZ = sizeZ;
      this.sizeVector = new Vec3i(sizeX, sizeY, sizeZ);
   }

   public NbtPlacerUtil(
      CompoundTag storedNbt, HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions, ListTag entities, BlockPos lowestPos, BlockPos sizePos
   ) {
      this(storedNbt, positions, entities, lowestPos, sizePos.getX(), sizePos.getY(), sizePos.getZ());
   }

   public NbtPlacerUtil manipulate(Manipulation manipulation) {
      ListTag paletteList = this.storedNbt.getList("palette", 10);
      HashMap<Integer, BlockState> palette = new HashMap<>(paletteList.size());
      List<CompoundTag> paletteCompoundList = paletteList.stream()
         .filter(nbtElement -> nbtElement instanceof CompoundTag)
         .map(element -> (CompoundTag)element)
         .toList();

      for (int i = 0; i < paletteCompoundList.size(); i++) {
         palette.put(
            i,
            NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteCompoundList.get(i))
               .mirror(manipulation.getMirror())
               .rotate(manipulation.getRotation())
         );
      }

      ListTag sizeList = this.storedNbt.getList("size", 3);
      BlockPos sizeVector = transformSizeVector(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2), manipulation.getRotation());
      BlockPos zeroOffset = StructureTemplate.getZeroPositionWithTransform(
         BlockPos.ZERO, manipulation.getMirror(), manipulation.getRotation(), sizeList.getInt(0), sizeList.getInt(2)
      );
      ListTag positionsList = this.storedNbt.getList("blocks", 10);
      HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions = new HashMap<>(positionsList.size());
      List<Pair<BlockPos, Pair<BlockState, Optional<CompoundTag>>>> positionsPairList = positionsList.stream()
         .filter(nbtElement -> nbtElement instanceof CompoundTag)
         .map(element -> (CompoundTag)element)
         .map(
            nbtCompound -> Pair.of(
               StructureTemplate.transform(
                  new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1), nbtCompound.getList("pos", 3).getInt(2)),
                  manipulation.getMirror(),
                  manipulation.getRotation(),
                  BlockPos.ZERO
               ),
               Pair.of(palette.get(nbtCompound.getInt("state")), nbtCompound.contains("nbt", 10) ? Optional.of(nbtCompound.getCompound("nbt")) : emptyNbt())
            )
         )
         .sorted(Comparator.comparing(pair -> ((BlockPos)pair.getFirst()).getX()))
         .sorted(Comparator.comparing(pair -> ((BlockPos)pair.getFirst()).getY()))
         .sorted(Comparator.comparing(pair -> ((BlockPos)pair.getFirst()).getZ()))
         .toList();
      BlockPos lowestPos = BlockPos.ZERO.subtract(zeroOffset);
      positionsPairList.forEach(pair -> positions.put(((BlockPos)pair.getFirst()).offset(zeroOffset), (Pair)pair.getSecond()));
      return new NbtPlacerUtil(this.storedNbt, positions, this.storedNbt.getList("entities", 10), lowestPos, sizeVector);
   }

   public static NbtPlacerUtil load(ResourceLocation id, ResourceManager manager) {
      return loadSafe(id, manager).get();
   }

   public static Optional<NbtPlacerUtil> loadSafe(ResourceLocation id, ResourceManager manager) {
      try {
         Optional<CompoundTag> nbtOptional = loadNbtSafe(id, manager);
         if (!nbtOptional.isPresent()) {
            throw new NullPointerException();
         } else {
            CompoundTag nbt = nbtOptional.get();
            ListTag paletteList = nbt.getList("palette", 10);
            HashMap<Integer, BlockState> palette = new HashMap<>(paletteList.size());
            List<CompoundTag> paletteCompoundList = paletteList.stream()
               .filter(nbtElement -> nbtElement instanceof CompoundTag)
               .map(element -> (CompoundTag)element)
               .toList();

            for (int i = 0; i < paletteCompoundList.size(); i++) {
               palette.put(i, NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteCompoundList.get(i)));
            }

            ListTag sizeList = nbt.getList("size", 3);
            BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2));
            BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()), Math.abs(sizeVectorRotated.getZ()));
            ListTag positionsList = nbt.getList("blocks", 10);
            HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions = new HashMap<>(positionsList.size());
            List<Pair<BlockPos, Pair<BlockState, Optional<CompoundTag>>>> positionsPairList = positionsList.stream()
               .filter(nbtElement -> nbtElement instanceof CompoundTag)
               .map(element -> (CompoundTag)element)
               .map(
                  nbtCompound -> Pair.of(
                     new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1), nbtCompound.getList("pos", 3).getInt(2)),
                     Pair.of(
                        palette.get(nbtCompound.getInt("state")), nbtCompound.contains("nbt", 10) ? Optional.of(nbtCompound.getCompound("nbt")) : emptyNbt()
                     )
                  )
               )
               .sorted(Comparator.comparing(pair -> ((BlockPos)pair.getFirst()).getX()))
               .sorted(Comparator.comparing(pair -> ((BlockPos)pair.getFirst()).getY()))
               .sorted(Comparator.comparing(pair -> ((BlockPos)pair.getFirst()).getZ()))
               .toList();
            positionsPairList.forEach(
               pair -> positions.put(((BlockPos)pair.getFirst()).subtract((Vec3i)positionsPairList.get(0).getFirst()), (Pair)pair.getSecond())
            );
            return Optional.of(new NbtPlacerUtil(nbt, positions, nbt.getList("entities", 10), (BlockPos)positionsPairList.get(0).getFirst(), sizeVector));
         }
      } catch (Exception var13) {
         var13.printStackTrace();
         return Optional.empty();
      }
   }

   private static Optional<CompoundTag> emptyNbt() {
      return Optional.empty();
   }

   public static Optional<CompoundTag> loadNbtSafe(ResourceLocation id, ResourceManager manager) {
      try {
         return Optional.ofNullable(readStructure((Resource)manager.getResource(id).get()));
      } catch (Exception var3) {
         var3.printStackTrace();
         return Optional.empty();
      }
   }

   public static CompoundTag readStructure(Resource resource) throws IOException {
      InputStream stream = resource.open();
      CompoundTag nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
      stream.close();
      return nbt;
   }

   public NbtPlacerUtil generateNbt(WorldGenRegion region, BlockPos at, TriConsumer<BlockPos, BlockState, Optional<CompoundTag>> consumer) {
      return this.generateNbt(region, BlockPos.ZERO, at, at.offset(this.sizeVector), consumer);
   }

   public NbtPlacerUtil generateNbt(
      WorldGenRegion region, Vec3i offset, BlockPos from, BlockPos to, TriConsumer<BlockPos, BlockState, Optional<CompoundTag>> consumer
   ) {
      for (int xi = 0; xi < Math.min(to.subtract(from).getX(), this.sizeX); xi++) {
         for (int yi = 0; yi < Math.min(to.subtract(from).getY(), this.sizeY); yi++) {
            for (int zi = 0; zi < Math.min(to.subtract(from).getZ(), this.sizeZ); zi++) {
               BlockPos pos = new BlockPos(xi, yi, zi);
               Pair<BlockState, Optional<CompoundTag>> pair = this.positions.get(pos.offset(offset));
               if (pair != null) {
                  BlockState state = (BlockState)pair.getFirst();
                  Optional<CompoundTag> nbt = (Optional<CompoundTag>)pair.getSecond();
                  if (state != null) {
                     consumer.accept(from.offset(pos), state, nbt);
                  }
               }
            }
         }
      }

      return this;
   }

   public NbtPlacerUtil spawnEntities(WorldGenRegion region, BlockPos pos, Manipulation manipulation) {
      return this.spawnEntities(region, BlockPos.ZERO, pos, pos.offset(this.sizeX, this.sizeY, this.sizeZ), manipulation);
   }

   public NbtPlacerUtil spawnEntities(WorldGenRegion region, BlockPos offset, BlockPos from, BlockPos to, Manipulation manipulation) {
      this.entities.forEach(nbtElement -> this.spawnEntity(nbtElement, region, offset, from, to, manipulation));
      return this;
   }

   public NbtPlacerUtil spawnEntity(Tag nbtElement, WorldGenRegion region, BlockPos offset, BlockPos from, BlockPos to, Manipulation manipulation) {
      CompoundTag entityCompound = (CompoundTag)nbtElement;
      ListTag nbtPos = entityCompound.getList("pos", 6);
      Vec3 relativeLocation = StructureTemplate.transform(
            new Vec3(nbtPos.getDouble(0), nbtPos.getDouble(1), nbtPos.getDouble(2)), manipulation.getMirror(), manipulation.getRotation(), BlockPos.ZERO
         )
         .subtract(Vec3.atLowerCornerOf(this.lowestPos));
      Vec3 realPosition = relativeLocation.add(Vec3.atLowerCornerOf(from.subtract(offset)));
      BlockPos max = to.subtract(from).offset(offset);
      if (relativeLocation.x() < max.getX()
         && relativeLocation.x() >= offset.getX()
         && relativeLocation.y() < max.getY()
         && relativeLocation.y() >= offset.getY()
         && relativeLocation.z() < max.getZ()
         && relativeLocation.z() >= offset.getZ()) {
         BlockPos relativeBlockPos = null;
         if (entityCompound.contains("blockPos", 9)) {
            ListTag blockPosTag = entityCompound.getList("blockPos", 3);
            relativeBlockPos = StructureTemplate.transform(
                  new BlockPos(blockPosTag.getInt(0), blockPosTag.getInt(1), blockPosTag.getInt(2)),
                  manipulation.getMirror(),
                  manipulation.getRotation(),
                  BlockPos.ZERO
               )
               .subtract(this.lowestPos);
         }

         BlockPos realBlockPos = relativeBlockPos == null ? BlockPos.containing(realPosition) : from.subtract(offset).offset(relativeBlockPos);
         CompoundTag nbt = entityCompound.getCompound("nbt").copy();
         nbt.remove("Pos");
         nbt.remove("UUID");
         ListTag posList = new ListTag();
         posList.add(DoubleTag.valueOf(realPosition.x));
         posList.add(DoubleTag.valueOf(realPosition.y));
         posList.add(DoubleTag.valueOf(realPosition.z));
         nbt.put("Pos", posList);
         boolean hasTilePos = nbt.contains("TileX", 3) && nbt.contains("TileY", 3) && nbt.contains("TileZ", 3);
         if (hasTilePos) {
            nbt.remove("TileX");
            nbt.remove("TileY");
            nbt.remove("TileZ");
            nbt.putInt("TileX", realBlockPos.getX());
            nbt.putInt("TileY", realBlockPos.getY());
            nbt.putInt("TileZ", realBlockPos.getZ());
         }

         if (hasTilePos && nbt.contains("facing", 1)) {
            Direction direction = Direction.from2DDataValue(nbt.getByte("facing"));
            direction = manipulation.getRotation().rotate(manipulation.getMirror().mirror(direction));
            nbt.putByte("facing", (byte)direction.get2DDataValue());
         }

         if (hasTilePos && nbt.contains("Facing", 1)) {
            Direction direction = Direction.from3DDataValue(nbt.getByte("Facing"));
            direction = manipulation.getRotation().rotate(manipulation.getMirror().mirror(direction));
            nbt.putByte("Facing", (byte)direction.get3DDataValue());
         }

         Optional<Entity> optionalEntity = getEntity(region, nbt);
         if (optionalEntity.isPresent()) {
            Entity entity = optionalEntity.get();
            if (entity instanceof HangingEntity deco) {
               if (entity instanceof Painting painting) {
                  realBlockPos = getPaintingAnchor(painting, realPosition);
               }

               deco.setPos(realBlockPos.getX(), realBlockPos.getY(), realBlockPos.getZ());
            } else {
               float yawRotation = entity.rotate(manipulation.getRotation());
               yawRotation += entity.mirror(manipulation.getMirror()) - entity.getYRot();
               entity.moveTo(realPosition.x, realPosition.y, realPosition.z, yawRotation, entity.getXRot());
            }

            region.addFreshEntity(entity);
         }

         return this;
      } else {
         return this;
      }
   }

   private static BlockPos getPaintingAnchor(Painting painting, Vec3 center) {
      Direction direction = painting.getDirection();
      Direction counterClockWise = direction.getCounterClockWise();
      double widthOffset = ((PaintingVariant)painting.getVariant().value()).width() % 2 == 0 ? 0.5 : 0.0;
      double heightOffset = ((PaintingVariant)painting.getVariant().value()).height() % 2 == 0 ? 0.5 : 0.0;
      return BlockPos.containing(
         center.x() + direction.getStepX() * 0.46875 - counterClockWise.getStepX() * widthOffset - 0.5,
         center.y() - heightOffset - 0.5,
         center.z() + direction.getStepZ() * 0.46875 - counterClockWise.getStepZ() * widthOffset - 0.5
      );
   }

   public static Optional<Entity> getEntity(WorldGenRegion region, CompoundTag nbt) {
      try {
         return EntityType.create(nbt, region.getLevel());
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   public static Vec3 rotate(Vec3 in, Rotation rotation) {
      switch (rotation) {
         case NONE:
         default:
            return in;
         case CLOCKWISE_90:
            return new Vec3(-in.z(), in.y(), in.x());
         case CLOCKWISE_180:
            return new Vec3(-in.x(), in.y(), -in.z());
         case COUNTERCLOCKWISE_90:
            return new Vec3(in.z(), in.y(), -in.x());
      }
   }

   public static Vec3 mirror(Vec3 in, Mirror mirror) {
      switch (mirror) {
         case NONE:
         default:
            return in;
         case LEFT_RIGHT:
            return new Vec3(in.x(), in.y(), -in.z());
         case FRONT_BACK:
            return new Vec3(-in.x(), in.y(), in.z());
      }
   }

   public static BlockPos rotate(BlockPos in, Rotation rotation) {
      switch (rotation) {
         case NONE:
         default:
            return in;
         case CLOCKWISE_90:
            return new BlockPos(-in.getZ(), in.getY(), in.getX());
         case CLOCKWISE_180:
            return new BlockPos(-in.getX(), in.getY(), -in.getZ());
         case COUNTERCLOCKWISE_90:
            return new BlockPos(in.getZ(), in.getY(), -in.getX());
      }
   }

   public static BlockPos mirror(BlockPos in, Mirror mirror) {
      switch (mirror) {
         case NONE:
         default:
            return in;
         case LEFT_RIGHT:
            return new BlockPos(in.getX(), in.getY(), -in.getZ());
         case FRONT_BACK:
            return new BlockPos(-in.getX(), in.getY(), in.getZ());
      }
   }

   private static BlockPos transformSizeVector(int x, int y, int z, Rotation rotation) {
      switch (rotation) {
         case CLOCKWISE_90:
         case COUNTERCLOCKWISE_90:
            return new BlockPos(z, y, x);
         default:
            return new BlockPos(x, y, z);
      }
   }

   public static BlockPos transformSize(BlockPos in, Rotation rotation, Mirror mirror) {
      BlockPos origin = BlockPos.ZERO;
      BlockPos xPin = mirror(rotate(new BlockPos(in.getX(), 0, 0), rotation), mirror);
      BlockPos zPin = mirror(rotate(new BlockPos(0, 0, in.getZ()), rotation), mirror);
      BlockPos pin = mirror(rotate(new BlockPos(in.getX(), 0, in.getZ()), rotation), mirror);
      return findBottomLeftVertex(origin, xPin, zPin, pin);
   }

   public static BlockPos findBottomLeftVertex(BlockPos v1, BlockPos v2, BlockPos v3, BlockPos v4) {
      BlockPos[] vertices = new BlockPos[]{v1, v2, v3, v4};
      Arrays.sort(vertices, Comparator.comparingInt(Vec3i::getX));
      Arrays.sort(vertices, Comparator.comparingInt(Vec3i::getZ));
      return vertices[0];
   }

   public Direction mirror(Direction in, Mirror mirror) {
      switch (mirror) {
         case NONE:
         default:
            break;
         case LEFT_RIGHT:
            if (in.getAxis().equals(Axis.Z)) {
               return in.getOpposite();
            }
            break;
         case FRONT_BACK:
            if (in.getAxis().equals(Axis.X)) {
               return in.getOpposite();
            }
      }

      return in;
   }

   public float applyRotation(float in, Rotation rotation) {
      float f = Mth.wrapDegrees(in);
      switch (rotation) {
         case CLOCKWISE_90:
            return f + 90.0F;
         case CLOCKWISE_180:
            return f + 180.0F;
         case COUNTERCLOCKWISE_90:
            return f + 270.0F;
         default:
            return f;
      }
   }

   public float applyMirror(float in, Mirror mirror) {
      float f = Mth.wrapDegrees(in);
      switch (mirror) {
         case LEFT_RIGHT:
            return 180.0F - f;
         case FRONT_BACK:
            return -f;
         default:
            return f;
      }
   }

   public static Vec3 abs(Vec3 in) {
      return new Vec3(Math.abs(in.x()), Math.abs(in.y()), Math.abs(in.z()));
   }

   public static ListTag createNbtIntList(int... ints) {
      ListTag nbtList = new ListTag();

      for (int i : ints) {
         nbtList.add(IntTag.valueOf(i));
      }

      return nbtList;
   }
}
