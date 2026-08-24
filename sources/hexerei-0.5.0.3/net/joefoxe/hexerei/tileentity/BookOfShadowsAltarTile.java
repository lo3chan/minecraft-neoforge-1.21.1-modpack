package net.joefoxe.hexerei.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.books.BookEntries;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.BookBookmarkDeleteToServer;
import net.joefoxe.hexerei.util.message.BookBookmarkPageToServer;
import net.joefoxe.hexerei.util.message.BookBookmarkSwapToServer;
import net.joefoxe.hexerei.util.message.BookSyncDataPacket;
import net.joefoxe.hexerei.util.message.BookTurnPageToServer;
import net.joefoxe.hexerei.util.message.ClientboundBookDataUpdate;
import net.joefoxe.hexerei.util.message.ClientboundBookTurnPage;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BookOfShadowsAltarTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {
   public final ItemStackHandler itemHandler = this.createHandler();
   private final Optional<IItemHandler> handler = Optional.of(this.itemHandler);
   public BookData currentBook;
   public PageDrawing drawing;
   public float bookYaw;
   public float bookYawO;
   public float bookYawIncrement;
   public float degreesSpun;
   public float degreesSpunOld;
   public float degreesSpunTo;
   public float degreesSpunRender;
   public float degreesSpunSpeed;
   public float degreesOpened;
   public float openedPercent;
   public float openedPercentOld;
   public float degreesOpenedTo;
   public float degreesOpenedRender;
   public float degreesOpenedSpeed;
   public float floppedPercent;
   public float floppedPercentOld;
   public float degreesFlopped;
   public float degreesFloppedTo;
   public float degreesFloppedRender;
   public float degreesFloppedSpeed;
   public boolean drawTooltip;
   public float tooltipScale;
   public float tooltipScaleOld;
   public int turnPage;
   public int turnToPage;
   public int turnToChapter;
   public float buttonScale;
   public float buttonScaleOld;
   public float buttonScaleTo;
   public float buttonScaleRender;
   public float buttonScaleSpeed;
   public float bookmarkSelectorScale;
   public float pageOneRotation;
   public float pageTwoRotation;
   public float pageOneRotationLast;
   public float pageTwoRotationLast;
   public float pageOneRotationTo;
   public float pageTwoRotationTo;
   public float pageOneRotationRender;
   public float pageTwoRotationRender;
   public float pageOneRotationSpeed;
   public float pageTwoRotationSpeed;
   public float numberOfCandles;
   public float maxCandles = 3.0F;
   public BlockPos candlePos1;
   public BlockPos candlePos2;
   public BlockPos candlePos3;
   public int candlePos1Slot;
   public int candlePos2Slot;
   public int candlePos3Slot;
   public float degreesSpunCandles;
   public float tickCount;
   public Vec3 closestPlayerPos;
   public Player closestPlayer;
   public double closestDist;
   public final double maxDist = 5.0;
   public int slotClicked = -1;
   public int slotClickedTick = 0;
   public boolean fromItem = false;

   public BookOfShadowsAltarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
      this.bookYaw = 0.0F;
      this.bookYawO = 0.0F;
      this.bookYawIncrement = 0.0F;
      this.drawTooltip = false;
      this.tooltipScale = 0.0F;
      this.tooltipScaleOld = 0.0F;
      this.turnPage = 0;
      this.buttonScale = 1.0F;
      this.buttonScaleTo = 1.0F;
      this.buttonScaleRender = 1.0F;
      this.buttonScaleSpeed = 0.0F;
      this.bookmarkSelectorScale = 0.0F;
      this.pageOneRotation = 0.0F;
      this.pageOneRotationLast = 0.0F;
      this.pageOneRotationRender = 0.0F;
      this.pageOneRotationTo = 0.0F;
      this.pageOneRotationSpeed = 0.0F;
      this.pageTwoRotation = 0.0F;
      this.pageTwoRotationLast = 0.0F;
      this.pageTwoRotationRender = 0.0F;
      this.pageTwoRotationTo = 0.0F;
      this.pageTwoRotationSpeed = 0.0F;
      this.floppedPercent = 1.0F;
      this.floppedPercentOld = 1.0F;
      this.degreesFlopped = 90.0F;
      this.degreesFloppedTo = 1.0F;
      this.degreesFloppedSpeed = 0.0F;
      this.degreesFloppedRender = 90.0F;
      this.openedPercent = 1.0F;
      this.openedPercentOld = 1.0F;
      this.degreesOpened = 90.0F;
      this.degreesOpenedTo = 1.0F;
      this.degreesOpenedSpeed = 0.0F;
      this.degreesOpenedRender = 90.0F;
      this.degreesSpun = 0.0F;
      this.degreesSpunTo = 0.0F;
      this.degreesSpunSpeed = 0.0F;
      this.degreesSpunRender = 0.0F;
      this.candlePos1Slot = 0;
      this.candlePos2Slot = 0;
      this.candlePos3Slot = 0;
      this.drawing = new PageDrawing(this);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            BookOfShadowsAltarTile.this.setChanged();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return true;
         }

         public int getSlotLimit(int slot) {
            return 64;
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !this.isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
         }
      };
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      super.onDataPacket(net, pkt, lookupProvider);
   }

   public void setChanged() {
      super.setChanged();
      this.sync();
   }

   public void sync() {
      if (this.level != null) {
         if (!this.level.isClientSide) {
            CompoundTag tag = new CompoundTag();
            this.saveAdditional(tag, this.level.registryAccess());
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new TESyncPacket(this.worldPosition, tag));
         }

         if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
         }
      }
   }

   public CompoundTag save(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      return tag;
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      tag.put("inv", this.itemHandler.serializeNBT(registries));
      tag.putFloat("degreesSpun", this.degreesSpun);
      tag.putFloat("floppedPercent", this.floppedPercent);
      tag.putFloat("openedPercent", this.openedPercent);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
      if (this.currentBook == null) {
         this.currentBook = (BookData)this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
      }

      this.degreesSpun = tag.getFloat("degreesSpun");
      this.degreesSpunRender = this.degreesSpun;
      this.bookYaw = this.degreesSpun;
      this.floppedPercent = tag.getFloat("floppedPercent");
      this.floppedPercentOld = this.floppedPercent;
      this.openedPercent = tag.getFloat("openedPercent");
      this.openedPercentOld = this.openedPercent;
   }

   public boolean interact(Player player, InteractionHand handIn, ItemStack stackIn) {
      ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
      if (!player.isShiftKeyDown()) {
         if (stack.isEmpty()) {
            Random rand = new Random();
            if (!stackIn.isEmpty()) {
               this.itemHandler.setStackInSlot(0, stackIn);
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
               player.setItemInHand(handIn, ItemStack.EMPTY);
               BookData bookData = (BookData)stackIn.get(ModDataComponents.BOOK);
               if (stackIn.getItem() instanceof HexereiBookItem) {
                  if (bookData != null) {
                     this.turnToChapter = bookData.getChapter();
                     this.turnToPage = bookData.getPage();
                     this.closestDist = getDistanceToEntity(player, this.worldPosition);
                     this.closestPlayerPos = player.position();
                     this.closestPlayer = player;
                     this.degreesSpun = 270.0F - this.getAngle(this.closestPlayerPos);
                     this.degreesSpunTo = 270.0F - this.getAngle(this.closestPlayerPos);
                     this.degreesSpunRender = 270.0F - this.getAngle(this.closestPlayerPos);
                  }

                  this.setChanged();
               }

               return true;
            }
         } else if (stack.getItem() instanceof HexereiBookItem) {
            BookData bookData = (BookData)stack.get(ModDataComponents.BOOK);
            if (bookData != null && !bookData.isOpened() && this.openedPercent == 1.0F) {
               this.level
                  .playSound(
                     null,
                     this.worldPosition.above(),
                     (SoundEvent)ModSounds.BOOK_OPENING.get(),
                     SoundSource.BLOCKS,
                     1.0F,
                     this.level.random.nextFloat() * 0.25F + 0.75F
                  );
               bookData = bookData.setOpened(true);
               stack.set(ModDataComponents.BOOK, bookData);
               this.itemHandler.setStackInSlot(0, stack);
               HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));
               this.setChanged();
               return true;
            }
         }
      }

      if (!stack.isEmpty()) {
         if (stack.getItem() instanceof HexereiBookItem && !player.isShiftKeyDown()) {
            return false;
         } else {
            this.setChanged();
            if (player.getItemInHand(handIn).isEmpty()) {
               player.setItemInHand(handIn, this.itemHandler.getStackInSlot(0).copy());
            } else {
               player.getInventory().placeItemBackInInventory(this.itemHandler.getStackInSlot(0).copy());
            }

            this.resetBookRotations();
            this.level
               .playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F);
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            this.setChanged();
            return true;
         }
      } else {
         return false;
      }
   }

   public void resetBookRotations() {
      this.floppedPercent = 1.0F;
      this.degreesFlopped = 90.0F;
      this.degreesFloppedRender = 90.0F;
      this.openedPercent = 1.0F;
      this.degreesOpened = 90.0F;
      this.degreesOpenedRender = 90.0F;
      this.degreesSpun = 0.0F;
      this.degreesSpunRender = 0.0F;
      this.degreesSpunTo = 0.0F;
      this.pageOneRotation = 0.0F;
      this.pageOneRotationTo = 0.0F;
      this.pageOneRotationRender = 0.0F;
      this.pageTwoRotation = 0.0F;
      this.pageTwoRotationTo = 0.0F;
      this.pageTwoRotationRender = 0.0F;
      this.turnPage = 0;
      this.turnToPage = 0;
      this.turnToChapter = 0;
   }

   public void requestModelDataUpdate() {
      super.requestModelDataUpdate();
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      super.handleUpdateTag(tag, lookupProvider);
   }

   public void onLoad() {
      super.onLoad();
   }

   protected Component getDefaultName() {
      return null;
   }

   protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
      return null;
   }

   protected NonNullList<ItemStack> getItems() {
      NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);

      for (int i = 0; i < this.itemHandler.getSlots(); i++) {
         items.set(i, this.itemHandler.getStackInSlot(i));
      }

      return items;
   }

   public ItemStack removeItem(int p_59613_, int p_59614_) {
      this.unpackLootTable(null);
      ItemStack itemstack = p_59613_ >= 0 && p_59613_ < this.itemHandler.getSlots() && !this.itemHandler.getStackInSlot(p_59613_).isEmpty() && p_59614_ > 0
         ? ((ItemStack)this.getItems().get(p_59613_)).split(p_59614_)
         : ItemStack.EMPTY;
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int p_59630_) {
      this.unpackLootTable(null);
      if (p_59630_ >= 0 && p_59630_ < this.itemHandler.getSlots()) {
         this.itemHandler.setStackInSlot(p_59630_, ItemStack.EMPTY);
         return this.itemHandler.getStackInSlot(p_59630_);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getItem(int p_59611_) {
      this.unpackLootTable(null);
      return this.itemHandler.getStackInSlot(p_59611_);
   }

   public void setItem(int p_59616_, ItemStack p_59617_) {
      this.unpackLootTable(null);
      this.itemHandler.setStackInSlot(p_59616_, p_59617_);
      if (p_59617_.getCount() > this.getMaxStackSize()) {
         p_59617_.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      for (int i = 0; i < Math.min(itemsIn.size(), this.itemHandler.getSlots()); i++) {
         this.itemHandler.setStackInSlot(i, (ItemStack)itemsIn.get(i));
      }
   }

   public void clearContent() {
      super.clearContent();

      for (int i = 0; i < this.itemHandler.getSlots(); i++) {
         this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
      }
   }

   public BookOfShadowsAltarTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), blockPos, blockState);
   }

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.position().x() - pos.getX() - 0.5;
      double deltaY = entity.position().y() - pos.getY() - 0.5;
      double deltaZ = entity.position().z() - pos.getZ() - 0.5;
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public static double getDistance(float x1, float y1, float x2, float y2) {
      double deltaX = x2 - x1;
      double deltaY = y2 - y1;
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
   }

   public float getAngle(Vec3 pos) {
      float angle = (float)Math.toDegrees(Math.atan2(pos.z() - this.worldPosition.getZ() - 0.5, pos.x() - this.worldPosition.getX() - 0.5));
      if (angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   private boolean getCandle(Level world, BlockPos pos) {
      return world.getBlockEntity(pos) instanceof CandleTile;
   }

   public static float easeFlop(float x) {
      return x < 0.5F ? (float)Math.pow(2.0, 20.0F * x - 10.0F) / 2.0F : (float)(2.0 - Math.pow(2.0, -20.0F * x + 10.0F)) / 2.0F;
   }

   public static float easeOpened(float x) {
      float c1 = 1.0F;
      float c2 = c1 * 1.525F;
      return x < 0.5F ? (float)(Math.pow(2.0F * x, 2.0) * ((c2 + 1.0F) * 2.0F * x - c2)) / 2.0F : (float)(2.0 - Math.pow(2.0, -20.0F * x + 10.0F)) / 2.0F;
   }

   public static float easeButtons(float x) {
      float c1 = 1.70158F;
      float c2 = c1 * 1.525F;
      return x < 0.5
         ? (float)(Math.pow(2.0F * x, 2.0) * ((c2 + 1.0F) * 2.0F * x - c2)) / 2.0F
         : (float)(Math.pow(2.0F * x - 2.0F, 2.0) * ((c2 + 1.0F) * (x * 2.0F - 2.0F) + c2) + 2.0) / 2.0F;
   }

   public void tickCandles() {
      this.tickCount++;
      this.numberOfCandles = 0.0F;
      this.candlePos1 = new BlockPos(0, 0, 0);
      this.candlePos2 = new BlockPos(0, 0, 0);
      this.candlePos3 = new BlockPos(0, 0, 0);

      for (int k = -1; k <= 1; k++) {
         for (int l = -1; l <= 1; l++) {
            if (k != 0 || l != 0) {
               if (this.level.getBlockEntity(this.worldPosition.offset(l * 2, 0, k * 2)) instanceof CandleTile candleTile
                  && this.numberOfCandles < this.maxCandles) {
                  for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {
                     if (i == 0 && ((CandleData)candleTile.candles.get(0)).lit
                        || i == 1 && ((CandleData)candleTile.candles.get(1)).lit
                        || i == 2 && ((CandleData)candleTile.candles.get(2)).lit
                        || i == 3 && ((CandleData)candleTile.candles.get(3)).lit) {
                        if (this.numberOfCandles == 0.0F) {
                           this.candlePos1 = this.worldPosition.offset(l * 2, 0, k * 2);
                           this.candlePos1Slot = i;
                        }

                        if (this.numberOfCandles == 1.0F) {
                           this.candlePos2 = this.worldPosition.offset(l * 2, 0, k * 2);
                           this.candlePos2Slot = i;
                        }

                        if (this.numberOfCandles == 2.0F) {
                           this.candlePos3 = this.worldPosition.offset(l * 2, 0, k * 2);
                           this.candlePos3Slot = i;
                        }

                        this.numberOfCandles++;
                     }
                  }
               }

               if (this.level.getBlockEntity(this.worldPosition.offset(l * 2, 1, k * 2)) instanceof CandleTile candleTile
                  && this.numberOfCandles < this.maxCandles) {
                  for (int ix = 0; ix < candleTile.getNumberOfCandles(); ix++) {
                     if (ix == 0 && ((CandleData)candleTile.candles.get(0)).lit
                        || ix == 1 && ((CandleData)candleTile.candles.get(1)).lit
                        || ix == 2 && ((CandleData)candleTile.candles.get(2)).lit
                        || ix == 3 && ((CandleData)candleTile.candles.get(3)).lit) {
                        if (this.numberOfCandles == 0.0F) {
                           this.candlePos1 = this.worldPosition.offset(l * 2, 1, k * 2);
                           this.candlePos1Slot = ix;
                        }

                        if (this.numberOfCandles == 1.0F) {
                           this.candlePos2 = this.worldPosition.offset(l * 2, 1, k * 2);
                           this.candlePos2Slot = ix;
                        }

                        if (this.numberOfCandles == 2.0F) {
                           this.candlePos3 = this.worldPosition.offset(l * 2, 1, k * 2);
                           this.candlePos3Slot = ix;
                        }

                        this.numberOfCandles++;
                     }
                  }
               }

               if (l != 0 && k != 0) {
                  if (this.level.getBlockEntity(this.worldPosition.offset(l * 2, 0, k)) instanceof CandleTile candleTile
                     && this.numberOfCandles < this.maxCandles) {
                     for (int ixx = 0; ixx < candleTile.getNumberOfCandles(); ixx++) {
                        if (ixx == 0 && ((CandleData)candleTile.candles.get(0)).lit
                           || ixx == 1 && ((CandleData)candleTile.candles.get(1)).lit
                           || ixx == 2 && ((CandleData)candleTile.candles.get(2)).lit
                           || ixx == 3 && ((CandleData)candleTile.candles.get(3)).lit) {
                           if (this.numberOfCandles == 0.0F) {
                              this.candlePos1 = this.worldPosition.offset(l * 2, 0, k);
                              this.candlePos1Slot = ixx;
                           }

                           if (this.numberOfCandles == 1.0F) {
                              this.candlePos2 = this.worldPosition.offset(l * 2, 0, k);
                              this.candlePos2Slot = ixx;
                           }

                           if (this.numberOfCandles == 2.0F) {
                              this.candlePos3 = this.worldPosition.offset(l * 2, 0, k);
                              this.candlePos3Slot = ixx;
                           }

                           this.numberOfCandles++;
                        }
                     }
                  }

                  if (this.level.getBlockEntity(this.worldPosition.offset(l * 2, 1, k)) instanceof CandleTile candleTile
                     && this.numberOfCandles < this.maxCandles) {
                     for (int ixxx = 0; ixxx < candleTile.getNumberOfCandles(); ixxx++) {
                        if (ixxx == 0 && ((CandleData)candleTile.candles.get(0)).lit
                           || ixxx == 1 && ((CandleData)candleTile.candles.get(1)).lit
                           || ixxx == 2 && ((CandleData)candleTile.candles.get(2)).lit
                           || ixxx == 3 && ((CandleData)candleTile.candles.get(3)).lit) {
                           if (this.numberOfCandles == 0.0F) {
                              this.candlePos1 = this.worldPosition.offset(l * 2, 1, k);
                              this.candlePos1Slot = ixxx;
                           }

                           if (this.numberOfCandles == 1.0F) {
                              this.candlePos2 = this.worldPosition.offset(l * 2, 1, k);
                              this.candlePos2Slot = ixxx;
                           }

                           if (this.numberOfCandles == 2.0F) {
                              this.candlePos3 = this.worldPosition.offset(l * 2, 1, k);
                              this.candlePos3Slot = ixxx;
                           }

                           this.numberOfCandles++;
                        }
                     }
                  }

                  if (this.level.getBlockEntity(this.worldPosition.offset(l, 0, k * 2)) instanceof CandleTile candleTile
                     && this.numberOfCandles < this.maxCandles) {
                     for (int ixxxx = 0; ixxxx < candleTile.getNumberOfCandles(); ixxxx++) {
                        if (ixxxx == 0 && ((CandleData)candleTile.candles.get(0)).lit
                           || ixxxx == 1 && ((CandleData)candleTile.candles.get(1)).lit
                           || ixxxx == 2 && ((CandleData)candleTile.candles.get(2)).lit
                           || ixxxx == 3 && ((CandleData)candleTile.candles.get(3)).lit) {
                           if (this.numberOfCandles == 0.0F) {
                              this.candlePos1 = this.worldPosition.offset(l, 0, k * 2);
                              this.candlePos1Slot = ixxxx;
                           }

                           if (this.numberOfCandles == 1.0F) {
                              this.candlePos2 = this.worldPosition.offset(l, 0, k * 2);
                              this.candlePos2Slot = ixxxx;
                           }

                           if (this.numberOfCandles == 2.0F) {
                              this.candlePos3 = this.worldPosition.offset(l, 0, k * 2);
                              this.candlePos3Slot = ixxxx;
                           }

                           this.numberOfCandles++;
                        }
                     }
                  }

                  if (this.level.getBlockEntity(this.worldPosition.offset(l, 1, k * 2)) instanceof CandleTile candleTile
                     && this.numberOfCandles < this.maxCandles) {
                     for (int ixxxxx = 0; ixxxxx < candleTile.getNumberOfCandles(); ixxxxx++) {
                        if (ixxxxx == 0 && ((CandleData)candleTile.candles.get(0)).lit
                           || ixxxxx == 1 && ((CandleData)candleTile.candles.get(1)).lit
                           || ixxxxx == 2 && ((CandleData)candleTile.candles.get(2)).lit
                           || ixxxxx == 3 && ((CandleData)candleTile.candles.get(3)).lit) {
                           if (this.numberOfCandles == 0.0F) {
                              this.candlePos1 = this.worldPosition.offset(l, 1, k * 2);
                              this.candlePos1Slot = ixxxxx;
                           }

                           if (this.numberOfCandles == 1.0F) {
                              this.candlePos2 = this.worldPosition.offset(l, 1, k * 2);
                              this.candlePos2Slot = ixxxxx;
                           }

                           if (this.numberOfCandles == 2.0F) {
                              this.candlePos3 = this.worldPosition.offset(l, 1, k * 2);
                              this.candlePos3Slot = ixxxxx;
                           }

                           this.numberOfCandles++;
                        }
                     }
                  }
               }
            }
         }
      }

      this.degreesSpunCandles = HexereiUtil.moveToAngle(this.degreesSpunCandles, this.degreesSpunCandles + 1.0F, 0.025F);
      if (this.numberOfCandles >= 1.0F && this.level.getBlockEntity(this.candlePos1) instanceof CandleTile candle) {
         CandleData candleData = (CandleData)candle.candles.get(this.candlePos1Slot);
         candleData.setNotReturn((int)this.tickCount);
         candleData.xTarget = this.worldPosition.getX() - this.candlePos1.getX() + (float)Math.sin(this.degreesSpunCandles) * 1.25F;
         candleData.yTarget = this.worldPosition.getY() - this.candlePos1.getY() + 1.0F + (float)Math.sin(this.tickCount / 20.0F) / 10.0F;
         candleData.zTarget = this.worldPosition.getZ() - this.candlePos1.getZ() + (float)Math.cos(this.degreesSpunCandles) * 1.25F;
      }

      if (this.numberOfCandles >= 2.0F && this.level.getBlockEntity(this.candlePos2) instanceof CandleTile candle) {
         CandleData candleData = (CandleData)candle.candles.get(this.candlePos2Slot);
         candleData.setNotReturn((int)this.tickCount);
         candleData.xTarget = this.worldPosition.getX() - this.candlePos2.getX()
            + (float)Math.sin(this.degreesSpunCandles + (this.numberOfCandles == 2.0F ? 3.141592653589793 : 2.0943951023931953)) * 1.25F;
         candleData.yTarget = this.worldPosition.getY() - this.candlePos2.getY() + 1.0F + (float)Math.sin((this.tickCount + 10.0F) / 20.0F) / 10.0F;
         candleData.zTarget = this.worldPosition.getZ() - this.candlePos2.getZ()
            + (float)Math.cos(this.degreesSpunCandles + (this.numberOfCandles == 2.0F ? 3.141592653589793 : 2.0943951023931953)) * 1.25F;
      }

      if (this.numberOfCandles >= 3.0F && this.level.getBlockEntity(this.candlePos3) instanceof CandleTile candle) {
         CandleData candleData = (CandleData)candle.candles.get(this.candlePos3Slot);
         candleData.setNotReturn((int)this.tickCount);
         candleData.xTarget = this.worldPosition.getX() - this.candlePos3.getX() + (float)Math.sin(this.degreesSpunCandles + 4.1887902047863905) * 1.25F;
         candleData.yTarget = this.worldPosition.getY() - this.candlePos3.getY() + 1.0F + (float)Math.sin((this.tickCount + 20.0F) / 20.0F) / 10.0F;
         candleData.zTarget = this.worldPosition.getZ() - this.candlePos3.getZ() + (float)Math.cos(this.degreesSpunCandles + 4.1887902047863905) * 1.25F;
      }
   }

   public void tickBook(ItemStack stack) {
      this.tickBook(stack, false);
   }

   public static int snapToCardinalDirection(float angle) {
      angle %= 360.0F;
      if (angle < 0.0F) {
         angle += 360.0F;
      }

      int[] directions = new int[]{0, 45, 90, 135, 180, 225, 270, 315, 360};
      int closestDirection = 0;
      float smallestDifference = 360.0F;

      for (int direction : directions) {
         float difference = Math.abs(angle - direction);
         if (difference < smallestDifference) {
            smallestDifference = difference;
            closestDirection = direction;
         }
      }

      if (closestDirection == 360) {
         closestDirection = 0;
      }

      return closestDirection;
   }

   public void tickBook(ItemStack stack, boolean fromItem) {
      if (stack.getItem() instanceof HexereiBookItem) {
         if (this.slotClicked != -1) {
            this.slotClickedTick++;
         }

         BookData bookData = !fromItem && !this.level.isClientSide ? (BookData)stack.get(ModDataComponents.BOOK) : this.currentBook;
         this.pageOneRotationLast = this.pageOneRotation;
         this.pageTwoRotationLast = this.pageTwoRotation;
         boolean opened = bookData != null && bookData.isOpened();
         if (!opened) {
            this.currentBook = (BookData)this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
            this.degreesOpenedTo = 1.0F;
            this.degreesOpenedSpeed = (2.0F + 6.0F * Math.abs(0.5F - this.openedPercent)) / 90.0F / 2.0F;
            this.openedPercent = HexereiUtil.moveTo(this.openedPercent, this.degreesOpenedTo, this.degreesOpenedSpeed);
            this.degreesOpened = easeOpened(this.openedPercent) * 90.0F;
            if (this.openedPercent > 0.2F) {
               this.degreesFloppedTo = 1.0F;
               this.degreesFloppedSpeed = (2.0F + 7.0F * (0.5F - Math.abs(0.5F - this.floppedPercent))) / 90.0F / 2.0F;
            }

            this.floppedPercent = HexereiUtil.moveTo(this.floppedPercent, this.degreesFloppedTo, this.degreesFloppedSpeed);
            this.degreesFlopped = this.floppedPercent * 90.0F;
         } else {
            this.buttonScale = HexereiUtil.moveTo(this.buttonScale, this.buttonScaleTo, this.buttonScaleSpeed);
            this.buttonScaleRender = this.buttonScale;
            if (this.slotClicked != -1 && this.slotClickedTick > 5) {
               this.bookmarkSelectorScale = HexereiUtil.moveTo(this.bookmarkSelectorScale, 1.0F, 0.15F * (this.bookmarkSelectorScale + 0.25F));
            } else {
               this.bookmarkSelectorScale = 0.0F;
            }

            if (this.closestPlayerPos == null && !fromItem) {
               this.degreesOpenedTo = 1.0F;
               this.degreesOpenedSpeed = (2.0F + 6.0F * (0.5F - Math.abs(0.5F - this.openedPercent))) / 90.0F / 2.0F;
               this.degreesFloppedTo = 1.0F;
               this.degreesFloppedSpeed = (2.0F + 4.0F * (0.5F - Math.abs(0.5F - this.floppedPercent))) / 90.0F / 2.0F;
            } else {
               if (!fromItem && (this.level.isClientSide ? this.degreesFloppedRender < 81.0F : this.floppedPercent < 0.9F)) {
                  Vec3 playerPos = this.closestPlayerPos;
                  double dx = playerPos.x - this.getBlockPos().getX() - 0.5;
                  double dz = playerPos.z - this.getBlockPos().getZ() - 0.5;
                  float yaw = 270.0F - (float)(Math.atan2(dz, dx) * 57.29577951308232);
                  yaw = snapToCardinalDirection(yaw);
                  this.bookYawIncrement = this.updateIncrement(this.bookYaw, yaw, this.bookYawIncrement);
                  this.bookYaw = this.updateAngle(this.bookYaw, this.bookYawIncrement);
               }

               this.degreesFloppedTo = 0.0F;
               this.degreesFloppedSpeed = (3.0F + 6.0F * Math.abs(this.floppedPercent - 0.66F)) / 90.0F / 2.0F;
               this.degreesOpenedTo = 0.0F;
               this.degreesOpenedSpeed = (2.0F + 5.0F * (0.5F - Math.abs(0.5F - this.openedPercent))) / 90.0F / 2.0F;
            }

            this.degreesSpun = this.bookYaw;
            this.floppedPercent = HexereiUtil.moveTo(this.floppedPercent, this.degreesFloppedTo, this.degreesFloppedSpeed);
            this.degreesFlopped = this.floppedPercent * 90.0F;
            this.openedPercent = HexereiUtil.moveTo(this.openedPercent, this.degreesOpenedTo, this.degreesOpenedSpeed);
            this.degreesOpened = easeOpened(this.openedPercent) * 90.0F;
            if (this.turnPage == 1) {
               if (this.pageOneRotation == 180.0F) {
                  bookData = this.clickedNext(bookData, 1);
                  this.pageOneRotationRender = 0.0F;
                  this.pageOneRotation = 0.0F;
                  this.pageOneRotationTo = 0.0F;
                  this.turnPage = 0;
                  this.pageOneRotationLast = this.pageOneRotation;
               } else {
                  if (this.pageOneRotation == 0.0F) {
                     if (fromItem) {
                        if (EffectiveSide.get().isClient()) {
                           BookOfShadowsAltarTile.ClientSounds.playTurnPageSound();
                        }
                     } else if (!this.level.isClientSide) {
                        this.level
                           .playSound(
                              null,
                              this.worldPosition.above(),
                              (SoundEvent)ModSounds.BOOK_TURN_PAGE_SLOW.get(),
                              SoundSource.BLOCKS,
                              this.level.random.nextFloat() * 0.25F + 0.5F,
                              this.level.random.nextFloat() * 0.25F + 0.75F
                           );
                     }
                  }

                  float f = (float)Math.sin(this.pageOneRotation / 180.0F * 3.141592653589793);
                  this.pageOneRotationSpeed = f * f * 35.0F + 10.0F;
                  this.pageOneRotationTo = 180.0F;
               }
            } else if (this.turnPage == 2) {
               if (this.pageTwoRotation == 180.0F) {
                  bookData = this.clickedBack(bookData, 1);
                  this.pageTwoRotationRender = 0.0F;
                  this.pageTwoRotation = 0.0F;
                  this.pageTwoRotationTo = 0.0F;
                  this.pageTwoRotationLast = this.pageTwoRotation;
                  this.turnPage = 0;
               } else {
                  if (this.pageTwoRotation == 0.0F) {
                     if (fromItem) {
                        if (EffectiveSide.get().isClient()) {
                           BookOfShadowsAltarTile.ClientSounds.playTurnPageSound();
                        }
                     } else if (!this.level.isClientSide) {
                        this.level
                           .playSound(
                              null,
                              this.worldPosition.above(),
                              (SoundEvent)ModSounds.BOOK_TURN_PAGE_SLOW.get(),
                              SoundSource.BLOCKS,
                              this.level.random.nextFloat() * 0.25F + 0.5F,
                              this.level.random.nextFloat() * 0.25F + 0.75F
                           );
                     }
                  }

                  float f = (float)Math.sin(this.pageTwoRotation / 180.0F * 3.141592653589793);
                  this.pageTwoRotationSpeed = f * f * 35.0F + 10.0F;
                  this.pageTwoRotationTo = 180.0F;
               }
            } else if (this.turnPage == -1) {
               BookEntries bookEntries = BookManager.getBookEntries(this.currentBook.getBook());
               if (bookEntries != null) {
                  int chapter = bookData.getChapter();
                  int page = bookData.getPage();
                  int pageOnNum = bookEntries.chapterList.get(chapter).startPage + page;
                  if (this.turnToChapter >= bookEntries.chapterList.size()) {
                     this.turnToChapter = bookEntries.chapterList.size() - 1;
                  }

                  if (this.turnToPage >= bookEntries.chapterList.get(this.turnToChapter).pages.size()) {
                     this.turnToPage = bookEntries.chapterList.get(this.turnToChapter).pages.size() - 1;
                  }

                  int destPageNum = bookEntries.chapterList.get(this.turnToChapter).startPage + this.turnToPage;
                  int numPagesToDest = Math.abs(destPageNum - pageOnNum);
                  if (page % 2 == 1) {
                     page--;
                  }

                  int pagesToTurn = numPagesToDest > 90
                     ? 13
                     : (
                        numPagesToDest > 75
                           ? 11
                           : (numPagesToDest > 60 ? 9 : (numPagesToDest > 45 ? 7 : (numPagesToDest > 30 ? 5 : (numPagesToDest > 15 ? 3 : 1))))
                     );
                  if (chapter > this.turnToChapter || chapter == this.turnToChapter && page > this.turnToPage) {
                     if (this.pageTwoRotation == 180.0F) {
                        bookData = this.clickedBack(bookData, pagesToTurn);
                        this.pageTwoRotation = 0.0F;
                        this.pageTwoRotationRender = 0.0F;
                        this.pageTwoRotationLast = this.pageTwoRotation;
                        this.pageTwoRotationTo = 0.0F;
                        this.pageTwoRotationSpeed = 0.01F;
                     } else {
                        if (this.pageTwoRotation == 0.0F && numPagesToDest > 1) {
                           if (fromItem) {
                              if (EffectiveSide.get().isClient()) {
                                 BookOfShadowsAltarTile.ClientSounds.playTurnPageFastSound();
                              }
                           } else if (!this.level.isClientSide) {
                              this.level
                                 .playSound(
                                    null,
                                    this.worldPosition.above(),
                                    (SoundEvent)ModSounds.BOOK_TURN_PAGE_FAST.get(),
                                    SoundSource.BLOCKS,
                                    this.level.random.nextFloat() * 0.25F + 0.5F,
                                    this.level.random.nextFloat() * 0.3F + 0.7F
                                 );
                           }
                        }

                        float f = 1.0F + Math.min(numPagesToDest, 50) / 200.0F;
                        this.pageTwoRotationSpeed = 65.0F * f * f + 15.0F;
                        this.pageTwoRotationTo = 180.0F;
                     }
                  }

                  if (chapter < this.turnToChapter || chapter == this.turnToChapter && page < this.turnToPage) {
                     if (this.pageOneRotation == 180.0F) {
                        bookData = this.clickedNext(bookData, pagesToTurn);
                        this.pageOneRotation = 0.0F;
                        this.pageOneRotationRender = 0.0F;
                        this.pageOneRotationTo = 0.0F;
                        this.pageOneRotationLast = this.pageOneRotation;
                        this.pageOneRotationSpeed = 0.01F;
                     } else {
                        if (this.pageOneRotation == 0.0F && numPagesToDest > 0) {
                           if (fromItem) {
                              if (EffectiveSide.get().isClient()) {
                                 BookOfShadowsAltarTile.ClientSounds.playTurnPageFastSound();
                              }
                           } else if (!this.level.isClientSide) {
                              this.level
                                 .playSound(
                                    null,
                                    this.worldPosition.above(),
                                    (SoundEvent)ModSounds.BOOK_TURN_PAGE_FAST.get(),
                                    SoundSource.BLOCKS,
                                    this.level.random.nextFloat() * 0.3F + 0.7F,
                                    this.level.random.nextFloat() * 0.25F + 0.5F
                                 );
                           }
                        }

                        float f = 1.0F + Math.min(numPagesToDest, 50) / 200.0F;
                        this.pageOneRotationSpeed = 65.0F * f * f + 15.0F;
                        this.pageOneRotationTo = 180.0F;
                     }
                  }

                  if (chapter == this.turnToChapter && (page == this.turnToPage || page + 1 == this.turnToPage)) {
                     this.turnPage = 0;
                     this.pageTwoRotation = 0.0F;
                     this.pageTwoRotationTo = 0.0F;
                     this.pageTwoRotationRender = 0.0F;
                     this.pageTwoRotationSpeed = 0.01F;
                     this.pageOneRotation = 0.0F;
                     this.pageOneRotationTo = 0.0F;
                     this.pageOneRotationRender = 0.0F;
                     this.pageOneRotationSpeed = 0.01F;
                     this.pageTwoRotationLast = this.pageTwoRotation;
                     this.pageOneRotationLast = this.pageOneRotation;
                  }
               }
            } else if (this.turnPage == 0) {
               this.currentBook = (BookData)this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
               this.pageTwoRotation = 0.0F;
               this.pageTwoRotationTo = 0.0F;
               this.pageTwoRotationRender = 0.0F;
               this.pageTwoRotationSpeed = 0.01F;
               this.pageOneRotation = 0.0F;
               this.pageOneRotationTo = 0.0F;
               this.pageOneRotationRender = 0.0F;
               this.pageOneRotationSpeed = 0.01F;
               this.pageTwoRotationLast = this.pageTwoRotation;
               this.pageOneRotationLast = this.pageOneRotation;
            }

            this.pageOneRotation = HexereiUtil.moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
            this.pageTwoRotation = HexereiUtil.moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
         }

         BookData bookData1 = !fromItem && !this.level.isClientSide
            ? (BookData)this.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK)
            : this.currentBook;
         if (bookData1 != bookData) {
            if (!fromItem && !this.level.isClientSide) {
               ItemStack stack1 = this.itemHandler.getStackInSlot(0).copy();
               stack1.set(ModDataComponents.BOOK, bookData);
               this.itemHandler.setStackInSlot(0, stack1);
            } else {
               this.currentBook = bookData;
            }
         }
      } else {
         this.currentBook = null;
         this.floppedPercent = 1.0F;
         this.degreesFlopped = 90.0F;
         this.degreesFloppedRender = 90.0F;
         this.openedPercent = 1.0F;
         this.degreesOpened = 90.0F;
         this.degreesOpenedRender = 90.0F;
         this.degreesSpun = 0.0F;
         this.degreesSpunRender = 0.0F;
         this.degreesSpunTo = 0.0F;
         this.pageOneRotation = 0.0F;
         this.pageOneRotationTo = 0.0F;
         this.pageOneRotationRender = 0.0F;
         this.pageTwoRotation = 0.0F;
         this.pageTwoRotationTo = 0.0F;
         this.pageTwoRotationRender = 0.0F;
      }
   }

   public void tickClient() {
      this.openedPercentOld = this.openedPercent;
      this.floppedPercentOld = this.floppedPercent;
      this.degreesSpunOld = this.degreesSpun;
      this.tooltipScaleOld = this.tooltipScale;
      this.buttonScaleOld = this.buttonScale;
      this.drawing.tick();
      if (this.drawTooltip) {
         this.tooltipScale = HexereiUtil.moveTo(this.tooltipScale, 1.0F, 0.075F);
      } else {
         this.tooltipScale = HexereiUtil.moveTo(this.tooltipScale, 0.0F, 0.15F);
      }

      ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
      BookData bookData = (BookData)stack.get(ModDataComponents.BOOK);
      if (this.turnPage == 0 && (bookData == null || bookData.isOpened())) {
         this.buttonScaleSpeed = 0.15F * (this.buttonScale + 0.25F);
         this.buttonScaleTo = 1.0F;
      } else {
         this.buttonScaleSpeed = 0.1F * (this.buttonScale + 0.25F);
         this.buttonScaleTo = 0.0F;
      }

      this.closestPlayerPos = null;
   }

   public void tick() {
      if (this.level.isClientSide) {
         this.tickClient();
      }

      this.tickCandles();
      this.closestDist = 5.0;
      if (this.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem) {
         Player playerEntity = this.level.getNearestPlayer(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 5.0, false);
         if (playerEntity != null) {
            double dist = getDistanceToEntity(playerEntity, this.worldPosition);
            if (dist < 5.0 && dist < this.closestDist) {
               this.closestDist = dist;
               this.closestPlayerPos = playerEntity.position();
               this.closestPlayer = playerEntity;
            }
         }
      }

      this.tickBook(this.itemHandler.getStackInSlot(0));
   }

   public float updateIncrement(float currentAngle, float targetAngle, float lastIncrement) {
      targetAngle = this.normalizeAngle(targetAngle);
      currentAngle = this.normalizeAngle(currentAngle);
      float angleDifference = targetAngle - currentAngle;
      if (angleDifference > 180.0F) {
         angleDifference -= 360.0F;
      } else if (angleDifference < -180.0F) {
         angleDifference += 360.0F;
      }

      float distance = Math.abs(angleDifference);
      if (Mth.abs(lastIncrement) < 0.1F && distance < 0.9F) {
         return 0.0F;
      } else {
         float adjustment = (distance / 180.0F * (distance / 180.0F) + 0.175F) * (angleDifference > 0.0F ? 1 : -1);
         return Mth.abs(lastIncrement) < 0.8F && distance < 10.0F
            ? (lastIncrement + adjustment) * (0.72F + 0.2F * Mth.abs(lastIncrement) / 0.8F)
            : (lastIncrement + adjustment) * 0.92F;
      }
   }

   public float updateAngle(float currentAngle, float maxIncrement) {
      currentAngle = this.normalizeAngle(currentAngle);
      currentAngle += maxIncrement;
      return this.normalizeAngle(currentAngle);
   }

   private float normalizeAngle(float angle) {
      while (angle > 90.0F) {
         angle -= 360.0F;
      }

      while (angle < -270.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public BookData clickedNext(BookData bookData, int pages) {
      if (bookData == null) {
         return bookData;
      } else {
         BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
         if (bookEntries != null) {
            for (int i = 0; i < pages; i++) {
               int currentPage = bookData.getPage();
               int currentChapter = bookData.getChapter();
               if (currentPage < bookEntries.chapterList.get(currentChapter).pages.size() - 2) {
                  bookData = bookData.setPage(currentPage + 2);
                  if (currentChapter < bookEntries.chapterList.size() - 1 && currentPage + 2 > bookEntries.chapterList.get(currentChapter).pages.size() - 1) {
                     bookData = bookData.setChapter(++currentChapter);
                     bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
                  }
               } else if (currentChapter < bookEntries.chapterList.size() - 1) {
                  bookData = bookData.setChapter(++currentChapter);
                  bookData = bookData.setPage(0);
               } else {
                  bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
               }
            }
         }

         return bookData;
      }
   }

   public BookData clickedBack(BookData bookData, int pages) {
      if (bookData == null) {
         return bookData;
      } else {
         BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
         if (bookEntries != null) {
            for (int i = 0; i < pages; i++) {
               int currentPage = bookData.getPage();
               int currentChapter = bookData.getChapter();
               if (currentPage > 0) {
                  if (currentChapter > 0 && currentPage - 2 < 0) {
                     bookData = bookData.setChapter(--currentChapter);
                     bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
                  } else {
                     bookData = bookData.setPage(Math.max(currentPage - 2, 0));
                  }
               } else if (currentChapter > 0) {
                  bookData = bookData.setChapter(--currentChapter);
                  bookData = bookData.setPage(bookEntries.chapterList.get(currentChapter).pages.size() - 1);
               } else {
                  bookData = bookData.setPage(0);
               }
            }
         }

         return bookData;
      }
   }

   public void forceTurnPage(int turnPage, int chapter, int page) {
      if (this.currentBook != null) {
         if (turnPage == -2) {
            turnPage += 2;
            if (EffectiveSide.get().isClient()) {
               BookOfShadowsAltarTile.ClientSounds.playBookCloseSound();
            }

            if (this.currentBook.isOpened()) {
               this.currentBook = this.currentBook.setOpened(false);
            }
         }

         this.turnPage = turnPage;
         this.turnToChapter = chapter;
         this.turnToPage = page;
         this.pageOneRotationRender = 0.0F;
         this.pageOneRotation = 0.0F;
         this.pageOneRotationTo = 0.0F;
         this.pageOneRotationLast = this.pageOneRotation;
         this.pageTwoRotationRender = 0.0F;
         this.pageTwoRotation = 0.0F;
         this.pageTwoRotationTo = 0.0F;
         this.pageTwoRotationLast = this.pageTwoRotation;
      }
   }

   public void setTurnPage(int turnPage, int chapter, int page) {
      if (this.fromItem) {
         this.forceTurnPage(turnPage, chapter, page);
      } else if (this.level.isClientSide) {
         HexereiPacketHandler.sendToServer(new BookTurnPageToServer(this, turnPage, chapter, page));
      } else {
         ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
         BookData bookData = (BookData)stack.get(ModDataComponents.BOOK);
         HexereiPacketHandler.sendToNearbyClient(
            this.level, this.worldPosition, new ClientboundBookTurnPage(this, turnPage, chapter, page, bookData.getChapter(), bookData.getPage())
         );
         this.turnToChapter = chapter;
         this.turnToPage = page;
         boolean flag = false;
         if (turnPage == -2) {
            turnPage += 2;
            flag = true;
         }

         if (flag) {
            this.level
               .playSound(
                  null,
                  this.worldPosition.above(),
                  (SoundEvent)ModSounds.BOOK_CLOSE.get(),
                  SoundSource.BLOCKS,
                  1.0F,
                  this.level.random.nextFloat() * 0.25F + 0.75F
               );
            ItemStack stackx = this.itemHandler.getStackInSlot(0).copy();
            BookData bookDatax = (BookData)stackx.get(ModDataComponents.BOOK);
            if (bookDatax != null && bookDatax.isOpened()) {
               bookDatax = bookDatax.setOpened(false);
            }

            stackx.set(ModDataComponents.BOOK, bookDatax);
            this.itemHandler.setStackInSlot(0, stackx);
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookDatax));
         }

         this.turnPage = turnPage;
      }
   }

   public void setTurnPage(int turnPage) {
      if (turnPage == -1) {
         this.setTurnPage(turnPage, 0, 0);
      } else {
         this.setTurnPage(turnPage, -1, -1);
      }
   }

   public void forcePageBookmark(int chapter, int page) {
      if (EffectiveSide.get().isClient()) {
         BookOfShadowsAltarTile.ClientSounds.playBookmarkSound();
      }

      if (this.currentBook != null) {
         BookEntries bookEntries = BookManager.getBookEntries(this.currentBook.getBook());
         if (bookEntries != null) {
            List<BookData.Bookmarks.Slot> slots = this.currentBook.getBookmarks().getSlots();
            boolean flag = false;
            BookData.Bookmarks.Slot firstEmpty = null;

            for (BookData.Bookmarks.Slot slot : slots) {
               if (!slot.getId().isEmpty()) {
                  if (bookEntries.chapterList.get(chapter).pages.get(page).location.equals(slot.getId())) {
                     slot.setColor(DyeColor.byId(slot.getColor().getId() + 1 >= DyeColor.values().length ? 0 : slot.getColor().getId() + 1));
                     flag = true;
                     break;
                  }
               } else if (firstEmpty == null) {
                  firstEmpty = slot;
               }
            }

            if (!flag && firstEmpty != null) {
               firstEmpty.setId(bookEntries.chapterList.get(chapter).pages.get(page).location);
               firstEmpty.setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
            }
         }
      }
   }

   public void clickPageBookmark(int chapter, int page) {
      if (this.fromItem) {
         this.forcePageBookmark(chapter, page);
      } else if (this.level != null) {
         if (this.level.isClientSide) {
            HexereiPacketHandler.sendToServer(new BookBookmarkPageToServer(this, chapter, page));
         } else {
            this.level
               .playSound(
                  null,
                  this.worldPosition.above(),
                  (SoundEvent)ModSounds.BOOKMARK_BUTTON.get(),
                  SoundSource.BLOCKS,
                  0.75F,
                  this.level.random.nextFloat() * 0.25F + 0.75F
               );
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = (BookData)stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
               BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
               if (bookEntries != null) {
                  List<BookData.Bookmarks.Slot> slots = bookData.getBookmarks().getSlots();
                  boolean flag = false;
                  BookData.Bookmarks.Slot firstEmpty = null;

                  for (BookData.Bookmarks.Slot slot : slots) {
                     if (!slot.getId().isEmpty()) {
                        if (bookEntries.chapterList.get(chapter).pages.get(page).location.equals(slot.getId())) {
                           slot.setColor(DyeColor.byId(slot.getColor().getId() + 1 >= DyeColor.values().length ? 0 : slot.getColor().getId() + 1));
                           flag = true;
                           break;
                        }
                     } else if (firstEmpty == null) {
                        firstEmpty = slot;
                     }
                  }

                  if (!flag && firstEmpty != null) {
                     firstEmpty.setId(bookEntries.chapterList.get(chapter).pages.get(page).location);
                     firstEmpty.setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
                  }
               }
            }

            stack.set(ModDataComponents.BOOK, bookData);
            this.itemHandler.setStackInSlot(0, stack);
            HexereiPacketHandler.sendToNearbyClient(this.level, this.getBlockPos(), new BookSyncDataPacket(this.getBlockPos()));
            this.setChanged();
         }
      }
   }

   public void forceSwapBookmarks(int slot1, int slot2) {
      if (EffectiveSide.get().isClient()) {
         BookOfShadowsAltarTile.ClientSounds.playBookmarkSwapSound();
      }

      List<BookData.Bookmarks.Slot> slots = new ArrayList<>(this.currentBook.getBookmarks().getSlots());
      BookData.Bookmarks.Slot temp = slots.get(slot1).copyWithIndex(slot2);
      slots.set(slot1, slots.get(slot2).copyWithIndex(slot1));
      slots.set(slot2, temp);
      this.currentBook = this.currentBook.setBookmarks(new BookData.Bookmarks(slots));
   }

   public void swapBookmarks(int slot1, int slot2) {
      if (this.fromItem) {
         this.forceSwapBookmarks(slot1, slot2);
      } else {
         if (this.level.isClientSide) {
            HexereiPacketHandler.sendToServer(new BookBookmarkSwapToServer(this, slot1, slot2));
         } else {
            this.level
               .playSound(
                  null,
                  this.worldPosition.above(),
                  (SoundEvent)ModSounds.BOOKMARK_SWAP.get(),
                  SoundSource.BLOCKS,
                  0.75F,
                  this.level.random.nextFloat() * 0.25F + 0.75F
               );
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = (BookData)stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
               List<BookData.Bookmarks.Slot> slots = new ArrayList<>(bookData.getBookmarks().getSlots());
               BookData.Bookmarks.Slot temp = slots.get(slot1).copyWithIndex(slot2);
               slots.set(slot1, slots.get(slot2).copyWithIndex(slot1));
               slots.set(slot2, temp);
               bookData = bookData.setBookmarks(new BookData.Bookmarks(slots));
               stack.set(ModDataComponents.BOOK, bookData);
               this.itemHandler.setStackInSlot(0, stack);
               HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));
            }

            this.setChanged();
         }
      }
   }

   public void forceDeleteBookmark(int slot1) {
      if (EffectiveSide.get().isClient()) {
         BookOfShadowsAltarTile.ClientSounds.playBookmarkDeleteSound();
      }

      List<BookData.Bookmarks.Slot> slots = new ArrayList<>(this.currentBook.getBookmarks().getSlots());
      slots.set(slot1, new BookData.Bookmarks.Slot("", DyeColor.WHITE, slot1));
      this.currentBook = this.currentBook.setBookmarks(new BookData.Bookmarks(slots));
   }

   public void deleteBookmark(int slot1) {
      if (this.fromItem) {
         this.forceDeleteBookmark(slot1);
      } else {
         if (this.level.isClientSide) {
            HexereiPacketHandler.sendToServer(new BookBookmarkDeleteToServer(this, slot1));
         } else {
            this.level
               .playSound(
                  null,
                  this.worldPosition.above(),
                  (SoundEvent)ModSounds.BOOKMARK_DELETE.get(),
                  SoundSource.BLOCKS,
                  1.0F,
                  this.level.random.nextFloat() * 0.25F + 0.75F
               );
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = (BookData)stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
               List<BookData.Bookmarks.Slot> slots = new ArrayList<>(bookData.getBookmarks().getSlots());
               slots.set(slot1, new BookData.Bookmarks.Slot("", DyeColor.WHITE, slot1));
               bookData = bookData.setBookmarks(new BookData.Bookmarks(slots));
               stack.set(ModDataComponents.BOOK, bookData);
               this.itemHandler.setStackInSlot(0, stack);
               HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new ClientboundBookDataUpdate(this, bookData));
            }

            this.setChanged();
         }
      }
   }

   public int getContainerSize() {
      return 0;
   }

   static class ClientSounds {
      public static void playTurnPageSound() {
         Minecraft.getInstance()
            .getSoundManager()
            .play(
               SimpleSoundInstance.forUI(
                  (SoundEvent)ModSounds.BOOK_TURN_PAGE_SLOW.get(), new Random().nextFloat() * 0.25F + 0.75F, 0.25F * (new Random().nextFloat() * 0.25F + 0.5F)
               )
            );
      }

      public static void playTurnPageFastSound() {
         Minecraft.getInstance()
            .getSoundManager()
            .play(
               SimpleSoundInstance.forUI(
                  (SoundEvent)ModSounds.BOOK_TURN_PAGE_FAST.get(), new Random().nextFloat() * 0.3F + 0.7F, 0.25F * (new Random().nextFloat() * 0.25F + 0.5F)
               )
            );
      }

      public static void playBookCloseSound() {
         Minecraft.getInstance()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI((SoundEvent)ModSounds.BOOK_CLOSE.get(), new Random().nextFloat() * 0.25F + 0.75F));
      }

      public static void playBookmarkSound() {
         Minecraft.getInstance()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI((SoundEvent)ModSounds.BOOKMARK_BUTTON.get(), new Random().nextFloat() * 0.25F + 0.75F));
      }

      public static void playBookmarkSwapSound() {
         Minecraft.getInstance()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI((SoundEvent)ModSounds.BOOKMARK_SWAP.get(), new Random().nextFloat() * 0.25F + 0.75F, 0.1875F));
      }

      public static void playBookmarkDeleteSound() {
         Minecraft.getInstance()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI((SoundEvent)ModSounds.BOOKMARK_DELETE.get(), new Random().nextFloat() * 0.25F + 0.75F));
      }
   }
}
