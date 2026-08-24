package at.petrak.hexcasting.api.casting.circles;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.misc.Result;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class CircleExecutionState {
   public static final String TAG_IMPETUS_POS = "impetus_pos";
   public static final String TAG_IMPETUS_DIR = "impetus_dir";
   public static final String TAG_KNOWN_POSITIONS = "known_positions";
   public static final String TAG_REACHED_POSITIONS = "reached_positions";
   public static final String TAG_CURRENT_POS = "current_pos";
   public static final String TAG_ENTERED_FROM = "entered_from";
   public static final String TAG_IMAGE = "image";
   public static final String TAG_CASTER = "caster";
   public static final String TAG_PIGMENT = "pigment";
   public final BlockPos impetusPos;
   public final Direction impetusDir;
   public final Set<BlockPos> knownPositions;
   public final List<BlockPos> reachedPositions;
   public BlockPos currentPos;
   public Direction enteredFrom;
   public CastingImage currentImage;
   @Nullable
   public UUID caster;
   @Nullable
   public FrozenPigment casterPigment;
   public final AABB bounds;

   protected CircleExecutionState(
      BlockPos impetusPos,
      Direction impetusDir,
      Set<BlockPos> knownPositions,
      List<BlockPos> reachedPositions,
      BlockPos currentPos,
      Direction enteredFrom,
      CastingImage currentImage,
      @Nullable UUID caster,
      @Nullable FrozenPigment casterPigment
   ) {
      this.impetusPos = impetusPos;
      this.impetusDir = impetusDir;
      this.knownPositions = knownPositions;
      this.reachedPositions = reachedPositions;
      this.currentPos = currentPos;
      this.enteredFrom = enteredFrom;
      this.currentImage = currentImage;
      this.caster = caster;
      this.casterPigment = casterPigment;
      this.bounds = BlockEntityAbstractImpetus.getBounds(new ArrayList<>(this.knownPositions));
   }

   @Nullable
   public ServerPlayer getCaster(ServerLevel world) {
      if (this.caster == null) {
         return null;
      } else {
         return world.getEntity(this.caster) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
      }
   }

   public static Result<CircleExecutionState, BlockPos> createNew(BlockEntityAbstractImpetus impetus, @Nullable ServerPlayer caster) {
      ServerLevel level = (ServerLevel)impetus.getLevel();
      if (level == null) {
         return new Result.Err<>(null);
      } else {
         Stack<Pair<Direction, BlockPos>> todo = new Stack<>();
         todo.add(Pair.of(impetus.getStartDirection(), impetus.getBlockPos().relative(impetus.getStartDirection())));
         HashSet<BlockPos> seenGoodPosSet = new HashSet<>();
         ArrayList<BlockPos> seenGoodPositions = new ArrayList<>();

         while (!todo.isEmpty()) {
            Pair<Direction, BlockPos> pair = todo.pop();
            Direction enterDir = (Direction)pair.getFirst();
            BlockPos herePos = (BlockPos)pair.getSecond();
            BlockState hereBs = level.getBlockState(herePos);
            if (hereBs.getBlock() instanceof ICircleComponent cmp && cmp.canEnterFromDirection(enterDir, herePos, hereBs, level) && seenGoodPosSet.add(herePos)
               )
             {
               seenGoodPositions.add(herePos);

               for (Direction out : cmp.possibleExitDirections(herePos, hereBs, level)) {
                  todo.add(Pair.of(out, herePos.relative(out)));
               }
            }
         }

         if (seenGoodPositions.isEmpty()) {
            return new Result.Err<>(null);
         } else if (!seenGoodPosSet.contains(impetus.getBlockPos())) {
            return new Result.Err<>(seenGoodPositions.get(seenGoodPositions.size() - 1));
         } else {
            HashSet<BlockPos> knownPositions = new HashSet<>(seenGoodPositions);
            ArrayList<BlockPos> reachedPositions = new ArrayList<>();
            reachedPositions.add(impetus.getBlockPos());
            BlockPos start = seenGoodPositions.get(0);
            FrozenPigment colorizer = null;
            UUID casterUUID;
            if (caster == null) {
               casterUUID = null;
            } else {
               colorizer = HexAPI.instance().getColorizer(caster);
               casterUUID = caster.getUUID();
            }

            return new Result.Ok<>(
               new CircleExecutionState(
                  impetus.getBlockPos(),
                  impetus.getStartDirection(),
                  knownPositions,
                  reachedPositions,
                  start,
                  impetus.getStartDirection(),
                  new CastingImage(),
                  casterUUID,
                  colorizer
               )
            );
         }
      }
   }

   public CompoundTag save() {
      CompoundTag out = new CompoundTag();
      out.put("impetus_pos", NbtUtils.writeBlockPos(this.impetusPos));
      out.putByte("impetus_dir", (byte)this.impetusDir.ordinal());
      ListTag knownTag = new ListTag();

      for (BlockPos bp : this.knownPositions) {
         knownTag.add(NbtUtils.writeBlockPos(bp));
      }

      out.put("known_positions", knownTag);
      ListTag reachedTag = new ListTag();

      for (BlockPos bp : this.reachedPositions) {
         reachedTag.add(NbtUtils.writeBlockPos(bp));
      }

      out.put("reached_positions", reachedTag);
      out.put("current_pos", NbtUtils.writeBlockPos(this.currentPos));
      out.putByte("entered_from", (byte)this.enteredFrom.ordinal());
      out.put("image", this.currentImage.serializeToNbt());
      if (this.caster != null) {
         out.putUUID("caster", this.caster);
      }

      if (this.casterPigment != null) {
         out.put("pigment", this.casterPigment.serializeToNBT());
      }

      return out;
   }

   public static CircleExecutionState load(CompoundTag nbt, ServerLevel world) {
      BlockPos startPos = NbtUtils.readBlockPos(nbt, "impetus_pos").orElse(BlockPos.ZERO);
      Direction startDir = Direction.values()[nbt.getByte("impetus_dir")];
      HashSet<BlockPos> knownPositions = new HashSet<>();

      for (Tag tag : nbt.getList("known_positions", 11)) {
         int[] coords = tag instanceof IntArrayTag ints ? ints.getAsIntArray() : new int[0];
         knownPositions.add(coords.length == 3 ? new BlockPos(coords[0], coords[1], coords[2]) : BlockPos.ZERO);
      }

      ArrayList<BlockPos> reachedPositions = new ArrayList<>();

      for (Tag tag : nbt.getList("reached_positions", 11)) {
         int[] coords = tag instanceof IntArrayTag ints ? ints.getAsIntArray() : new int[0];
         reachedPositions.add(coords.length == 3 ? new BlockPos(coords[0], coords[1], coords[2]) : BlockPos.ZERO);
      }

      BlockPos currentPos = NbtUtils.readBlockPos(nbt, "current_pos").orElse(BlockPos.ZERO);
      Direction enteredFrom = Direction.values()[nbt.getByte("entered_from")];
      CastingImage image = CastingImage.loadFromNbt(nbt.getCompound("image"), world);
      UUID caster = null;
      if (nbt.hasUUID("caster")) {
         caster = nbt.getUUID("caster");
      }

      FrozenPigment pigment = null;
      if (nbt.contains("pigment", 10)) {
         pigment = FrozenPigment.fromNBT(nbt.getCompound("pigment"));
      }

      return new CircleExecutionState(startPos, startDir, knownPositions, reachedPositions, currentPos, enteredFrom, image, caster, pigment);
   }

   public boolean tick(BlockEntityAbstractImpetus impetus) {
      ServerLevel world = (ServerLevel)impetus.getLevel();
      if (world == null) {
         return true;
      } else {
         CircleCastEnv env = new CircleCastEnv(world, this);
         BlockState executorBlockState = world.getBlockState(this.currentPos);
         if (!(executorBlockState.getBlock() instanceof ICircleComponent executor)) {
            ICircleComponent.sfx(this.currentPos, executorBlockState, world, Objects.requireNonNull(env.getImpetus()), false);
            return false;
         } else {
            executorBlockState = executor.startEnergized(this.currentPos, executorBlockState, world);
            this.reachedPositions.add(this.currentPos);
            boolean var16 = false;
            ICircleComponent.ControlFlow ctrl = executor.acceptControlFlow(this.currentImage, env, this.enteredFrom, this.currentPos, executorBlockState, world);
            if (ctrl instanceof ICircleComponent.ControlFlow.Stop) {
               var16 = true;
            } else if (ctrl instanceof ICircleComponent.ControlFlow.Continue cont) {
               Pair<BlockPos, Direction> found = null;

               for (Pair<BlockPos, Direction> exit : cont.exits) {
                  BlockState there = world.getBlockState((BlockPos)exit.getFirst());
                  if (there.getBlock() instanceof ICircleComponent cc
                     && cc.canEnterFromDirection((Direction)exit.getSecond(), (BlockPos)exit.getFirst(), there, world)) {
                     if (found != null) {
                        impetus.postDisplay(
                           Component.translatable(
                              "hexcasting.tooltip.circle.many_exits",
                              new Object[]{Component.literal(this.currentPos.toShortString()).withStyle(ChatFormatting.RED)}
                           ),
                           new ItemStack(Items.COMPASS)
                        );
                        ICircleComponent.sfx(this.currentPos, executorBlockState, world, Objects.requireNonNull(env.getImpetus()), false);
                        var16 = true;
                        break;
                     }

                     found = exit;
                  }
               }

               if (found == null) {
                  ICircleComponent.sfx(this.currentPos, executorBlockState, world, Objects.requireNonNull(env.getImpetus()), false);
                  impetus.postNoExits(this.currentPos);
                  var16 = true;
               } else {
                  ICircleComponent.sfx(this.currentPos, executorBlockState, world, Objects.requireNonNull(env.getImpetus()), true);
                  this.currentPos = (BlockPos)found.getFirst();
                  this.enteredFrom = (Direction)found.getSecond();
                  this.currentImage = cont.update.withOverriddenUsedOps(0L);
               }
            }

            return !var16;
         }
      }
   }

   protected int getTickSpeed() {
      return Math.max(2, 10 - (this.reachedPositions.size() - 1) / 3);
   }

   public void endExecution(BlockEntityAbstractImpetus impetus) {
      ServerLevel world = (ServerLevel)impetus.getLevel();
      if (world != null) {
         for (BlockPos pos : this.reachedPositions) {
            BlockState there = world.getBlockState(pos);
            if (there.getBlock() instanceof ICircleComponent cc) {
               cc.endEnergized(pos, there, world);
            }
         }
      }
   }
}
