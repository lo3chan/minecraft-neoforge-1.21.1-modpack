package at.petrak.hexcasting.api.casting.circles;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.misc.Result;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.lib.HexItems;
import com.mojang.datafixers.util.Pair;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntityAbstractImpetus extends HexBlockEntity implements WorldlyContainer {
   private static final DecimalFormat DUST_AMOUNT = new DecimalFormat("###,###.##");
   private static final long MAX_CAPACITY = 9000000000000000000L;
   public static final String TAG_EXECUTION_STATE = "executor";
   public static final String TAG_MEDIA = "media";
   public static final String TAG_ERROR_MSG = "errorMsg";
   public static final String TAG_ERROR_DISPLAY = "errorDisplay";
   public static final String TAG_PIGMENT = "pigment";
   @Nullable
   CompoundTag lazyExecutionState;
   @Nullable
   protected CircleExecutionState executionState;
   protected long media = 0L;
   @Nullable
   protected Component displayMsg = null;
   @Nullable
   protected ItemStack displayItem = null;
   @Nullable
   protected FrozenPigment pigment = null;
   private static final int[] MAJOR_SCALE = new int[]{0, 2, 4, 5, 7, 9, 11, 12};
   private static final int[] MINOR_SCALE = new int[]{0, 2, 3, 5, 7, 8, 11, 12};
   private static final int[] DORIAN_SCALE = new int[]{0, 2, 3, 5, 7, 9, 10, 12};
   private static final int[] MIXOLYDIAN_SCALE = new int[]{0, 2, 4, 5, 7, 9, 10, 12};
   private static final int[] BLUES_SCALE = new int[]{0, 3, 5, 6, 7, 10, 12};
   private static final int[] BAD_TIME = new int[]{0, 0, 12, 7, 6, 5, 3, 0, 3, 5};
   private static final int[] SUSSY_BAKA = new int[]{5, 8, 10, 11, 10, 8, 5, 3, 7, 5};
   private static final int[] SLOTS = new int[]{0};

   public BlockEntityAbstractImpetus(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
      super(pType, pWorldPosition, pBlockState);
   }

   public Direction getStartDirection() {
      return (Direction)this.getBlockState().getValue(BlockStateProperties.FACING);
   }

   @Nullable
   public Component getDisplayMsg() {
      return this.displayMsg;
   }

   public void clearDisplay() {
      this.displayMsg = null;
      this.displayItem = null;
      this.sync();
   }

   public void postDisplay(Component error, ItemStack display) {
      this.displayMsg = error;
      this.displayItem = display;
      this.sync();
   }

   public void postMishap(Component mishapDisplay) {
      this.postDisplay(mishapDisplay, new ItemStack(Items.MUSIC_DISC_11));
   }

   public void postPrint(Component printDisplay) {
      this.postDisplay(printDisplay, new ItemStack(Items.BOOK));
   }

   public void postNoExits(BlockPos pos) {
      this.postDisplay(
         Component.translatable("hexcasting.tooltip.circle.no_exit", new Object[]{Component.literal(pos.toShortString()).withStyle(ChatFormatting.RED)}),
         new ItemStack(Items.OAK_SIGN)
      );
   }

   public void tickExecution() {
      if (this.level != null) {
         this.setChanged();
         CircleExecutionState state = this.getExecutionState();
         if (state != null) {
            boolean shouldContinue = state.tick(this);
            if (!shouldContinue) {
               this.endExecution();
               this.executionState = null;
            } else {
               this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), state.getTickSpeed());
            }
         }
      }
   }

   public void endExecution() {
      if (this.executionState != null) {
         this.executionState.endExecution(this);
      }
   }

   @Nullable
   public CircleExecutionState getExecutionState() {
      if (this.level == null) {
         throw new IllegalStateException("didn't you read the doc comment, don't call this if the level is null");
      } else if (this.executionState != null) {
         return this.executionState;
      } else {
         if (this.lazyExecutionState != null) {
            this.executionState = CircleExecutionState.load(this.lazyExecutionState, (ServerLevel)this.level);
         }

         return this.executionState;
      }
   }

   public void startExecution(@Nullable ServerPlayer player) {
      if (this.level != null) {
         if (!this.level.isClientSide) {
            if (this.executionState == null) {
               Result<CircleExecutionState, BlockPos> result = CircleExecutionState.createNew(this, player);
               if (result.isErr()) {
                  BlockPos errPos = result.unwrapErr();
                  if (errPos == null) {
                     ICircleComponent.sfx(this.getBlockPos(), this.getBlockState(), this.level, null, false);
                     this.postNoExits(this.getBlockPos());
                  } else {
                     ICircleComponent.sfx(errPos, this.level.getBlockState(errPos), this.level, null, false);
                     this.postDisplay(
                        Component.translatable(
                           "hexcasting.tooltip.circle.no_closure", new Object[]{Component.literal(errPos.toShortString()).withStyle(ChatFormatting.RED)}
                        ),
                        new ItemStack(Items.LEAD)
                     );
                  }
               } else {
                  this.executionState = result.unwrap();
                  this.clearDisplay();
                  ServerLevel serverLevel = (ServerLevel)this.level;
                  serverLevel.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), this.executionState.getTickSpeed());
                  serverLevel.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(BlockCircleComponent.ENERGIZED, true));
               }
            }
         }
      }
   }

   @Contract(
      pure = true
   )
   protected static AABB getBounds(List<BlockPos> poses) {
      int minX = 2147483647;
      int minY = 2147483647;
      int minZ = 2147483647;
      int maxX = -2147483648;
      int maxY = -2147483648;
      int maxZ = -2147483648;

      for (BlockPos pos : poses) {
         if (pos.getX() < minX) {
            minX = pos.getX();
         }

         if (pos.getY() < minY) {
            minY = pos.getY();
         }

         if (pos.getZ() < minZ) {
            minZ = pos.getZ();
         }

         if (pos.getX() > maxX) {
            maxX = pos.getX();
         }

         if (pos.getY() > maxY) {
            maxY = pos.getY();
         }

         if (pos.getZ() > maxZ) {
            maxZ = pos.getZ();
         }
      }

      return new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
   }

   public long getMedia() {
      return this.media;
   }

   public void setMedia(long media) {
      this.media = media;
      this.sync();
   }

   public long extractMediaFromInsertedItem(ItemStack stack, boolean simulate) {
      return this.media < 0L ? 0L : MediaHelper.extractMedia(stack, this.remainingMediaCapacity(), true, simulate);
   }

   public void insertMedia(ItemStack stack) {
      if (this.getMedia() >= 0L && !stack.isEmpty() && stack.getItem() == HexItems.CREATIVE_UNLOCKER) {
         this.setInfiniteMedia();
         stack.shrink(1);
      } else {
         long mediamount = this.extractMediaFromInsertedItem(stack, false);
         if (mediamount > 0L) {
            this.media = Math.min(mediamount + this.media, 9000000000000000000L);
            this.sync();
         }
      }
   }

   public void setInfiniteMedia() {
      this.media = -1L;
      this.sync();
   }

   public long remainingMediaCapacity() {
      return this.media < 0L ? 0L : Math.max(0L, 9000000000000000000L - this.media);
   }

   public FrozenPigment getPigment() {
      if (this.pigment != null) {
         return this.pigment;
      } else {
         return this.executionState != null && this.executionState.casterPigment != null ? this.executionState.casterPigment : FrozenPigment.DEFAULT.get();
      }
   }

   @Nullable
   public FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
      this.pigment = pigment;
      return this.pigment;
   }

   @Override
   protected void saveModData(CompoundTag tag) {
      if (this.executionState != null) {
         tag.put("executor", this.executionState.save());
      }

      tag.putLong("media", this.media);
      if (this.displayMsg != null && this.displayItem != null) {
         tag.putString("errorMsg", Serializer.toJson(this.displayMsg, this.registryProvider()));
         tag.put("errorDisplay", this.displayItem.save(this.registryProvider()));
      }

      if (this.pigment != null) {
         tag.put("pigment", this.pigment.serializeToNBT());
      }
   }

   @Override
   protected void loadModData(CompoundTag tag) {
      this.executionState = null;
      if (tag.contains("executor", 10)) {
         this.lazyExecutionState = tag.getCompound("executor");
      } else {
         this.lazyExecutionState = null;
      }

      if (tag.contains("media", 4)) {
         this.media = tag.getLong("media");
      }

      if (tag.contains("errorMsg", 8) && tag.contains("errorDisplay", 10)) {
         MutableComponent msg = Serializer.fromJson(tag.getString("errorMsg"), this.registryProvider());
         ItemStack display = ItemStack.parseOptional(this.registryProvider(), tag.getCompound("errorDisplay"));
         this.displayMsg = msg;
         this.displayItem = display;
      } else {
         this.displayMsg = null;
         this.displayItem = null;
      }

      if (tag.contains("pigment", 10)) {
         this.pigment = FrozenPigment.fromNBT(tag.getCompound("pigment"));
      }
   }

   private Provider registryProvider() {
      return (Provider)(this.level != null ? this.level.registryAccess() : RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
   }

   public void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines, BlockState state, BlockPos pos, Player observer, Level world, Direction hitFace) {
      if (world.getBlockEntity(pos) instanceof BlockEntityAbstractImpetus beai) {
         if (beai.getMedia() < 0L) {
            lines.add(new Pair(new ItemStack(HexItems.AMETHYST_DUST), ItemCreativeUnlocker.infiniteMedia(world)));
         } else {
            float dustCount = (float)beai.getMedia() / 10000.0F;
            MutableComponent dustCmp = Component.translatable("hexcasting.tooltip.media", new Object[]{DUST_AMOUNT.format(dustCount)});
            lines.add(new Pair(new ItemStack(HexItems.AMETHYST_DUST), dustCmp));
         }

         if (this.displayMsg != null && this.displayItem != null) {
            lines.add(new Pair(this.displayItem, this.displayMsg));
         }
      }
   }

   protected int semitoneFromScale(int note) {
      BlockState blockBelow = this.level.getBlockState(this.getBlockPos().below());
      int[] scale = MAJOR_SCALE;
      if (blockBelow.is(Blocks.CRYING_OBSIDIAN)) {
         scale = MINOR_SCALE;
      } else if (blockBelow.is(BlockTags.DOORS) || blockBelow.is(BlockTags.TRAPDOORS)) {
         scale = DORIAN_SCALE;
      } else if (blockBelow.is(Blocks.PISTON) || blockBelow.is(Blocks.STICKY_PISTON)) {
         scale = MIXOLYDIAN_SCALE;
      } else if (blockBelow.is(Blocks.BLUE_WOOL)
         || blockBelow.is(Blocks.BLUE_CONCRETE)
         || blockBelow.is(Blocks.BLUE_CONCRETE_POWDER)
         || blockBelow.is(Blocks.BLUE_TERRACOTTA)
         || blockBelow.is(Blocks.BLUE_GLAZED_TERRACOTTA)
         || blockBelow.is(Blocks.BLUE_STAINED_GLASS)
         || blockBelow.is(Blocks.BLUE_STAINED_GLASS_PANE)) {
         scale = BLUES_SCALE;
      } else if (blockBelow.is(Blocks.BONE_BLOCK)) {
         scale = BAD_TIME;
      } else if (blockBelow.is(Blocks.COMPOSTER)) {
         scale = SUSSY_BAKA;
      }

      note = Mth.clamp(note, 0, scale.length - 1);
      return scale[note];
   }

   public int[] getSlotsForFace(Direction var1) {
      return SLOTS;
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
      return this.canPlaceItem(index, stack);
   }

   public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
      return false;
   }

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      return true;
   }

   public ItemStack getItem(int index) {
      return ItemStack.EMPTY.copy();
   }

   public ItemStack removeItem(int index, int count) {
      return ItemStack.EMPTY.copy();
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ItemStack.EMPTY.copy();
   }

   public void setItem(int index, ItemStack stack) {
      this.insertMedia(stack);
   }

   public boolean stillValid(Player player) {
      return false;
   }

   public void clearContent() {
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      if (this.remainingMediaCapacity() == 0L) {
         return false;
      } else if (stack.is(HexItems.CREATIVE_UNLOCKER)) {
         return true;
      } else {
         long mediamount = this.extractMediaFromInsertedItem(stack, true);
         return mediamount > 0L;
      }
   }
}
