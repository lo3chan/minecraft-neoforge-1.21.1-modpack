package vazkii.psi.common.block.tile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.AssembleCADEvent;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICADComponent;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.cad.ITileCADAssembler;
import vazkii.psi.api.cad.PostCADCraftEvent;
import vazkii.psi.common.block.BlockCADAssembler;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.container.ContainerCADAssembler;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.ItemCAD;

public class TileCADAssembler extends BlockEntity implements ITileCADAssembler, MenuProvider {
   private final TileCADAssembler.CADStackHandler inventory = new TileCADAssembler.CADStackHandler();
   private ItemStack cachedCAD = null;

   public TileCADAssembler(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlocks.cadAssemblerType.get(), pos, state);
   }

   public IItemHandlerModifiable getInventory() {
      return this.inventory;
   }

   @Override
   public void clearCachedCAD() {
      this.cachedCAD = null;
   }

   @Override
   public ItemStack getCachedCAD(Player player) {
      ItemStack cad = this.cachedCAD;
      if (cad == null) {
         ItemStack assembly = this.getStackForComponent(EnumCADComponent.ASSEMBLY);
         if (!assembly.isEmpty()) {
            List<ItemStack> components = IntStream.range(1, 6).<ItemStack>mapToObj(this.inventory::getStackInSlot).collect(Collectors.toList());
            cad = ItemCAD.makeCADWithAssembly(assembly, components);
         } else {
            cad = ItemStack.EMPTY;
         }

         AssembleCADEvent assembling = new AssembleCADEvent(cad, this, player);
         NeoForge.EVENT_BUS.post(assembling);
         if (assembling.isCanceled()) {
            cad = ItemStack.EMPTY;
         } else {
            cad = assembling.getCad();
         }

         this.cachedCAD = cad;
      }

      return cad;
   }

   @Override
   public ItemStack getStackForComponent(EnumCADComponent componentType) {
      return this.inventory.getStackInSlot(componentType.ordinal() + 1);
   }

   @Override
   public boolean setStackForComponent(EnumCADComponent componentType, ItemStack component) {
      int slot = componentType.ordinal() + 1;
      if (component.isEmpty()) {
         this.inventory.setStackInSlot(slot, component);
         return true;
      } else if (component.getItem() instanceof ICADComponent componentItem && componentItem.getComponentType(component) == componentType) {
         this.inventory.setStackInSlot(slot, component);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public ItemStack getSocketableStack() {
      return this.inventory.getStackInSlot(0);
   }

   @Override
   public ISocketable getSocketable() {
      return ISocketable.socketable(this.getSocketableStack());
   }

   @Override
   public boolean setSocketableStack(ItemStack stack) {
      if (!stack.isEmpty() && !ISocketable.isSocketable(stack)) {
         return false;
      } else {
         this.inventory.setStackInSlot(0, stack);
         return true;
      }
   }

   @Override
   public void onCraftCAD(ItemStack cad) {
      NeoForge.EVENT_BUS.post(new PostCADCraftEvent(cad, this));

      for (int i = 1; i < 6; i++) {
         this.inventory.setStackInSlot(i, ItemStack.EMPTY);
      }

      if (this.level != null) {
         if (!this.level.isClientSide) {
            this.level
               .playSound(
                  null,
                  this.getBlockPos().getX() + 0.5,
                  this.getBlockPos().getY() + 0.5,
                  this.getBlockPos().getZ() + 0.5,
                  PsiSoundHandler.cadCreate,
                  SoundSource.BLOCKS,
                  0.5F,
                  1.0F
               );
         }
      }
   }

   @Override
   public boolean isBulletSlotEnabled(int slot) {
      if (this.getSocketableStack().isEmpty()) {
         return false;
      } else {
         ISocketable socketable = this.getSocketable();
         return socketable != null && socketable.isSocketSlotAvailable(slot);
      }
   }

   protected void saveAdditional(@NotNull CompoundTag tag, @NotNull Provider provider) {
      super.saveAdditional(tag, provider);
      ContainerHelper.saveAllItems(tag, this.inventory.getItems(), provider);
   }

   public void loadAdditional(@NotNull CompoundTag cmp, @NotNull Provider provider) {
      super.loadAdditional(cmp, provider);
      this.readPacketNBT(cmp, provider);
   }

   public void readPacketNBT(@NotNull CompoundTag tag, Provider provider) {
      ListTag items = tag.getList("Items", 10);
      if (items.size() == 19) {
         for (int i = 0; i < this.inventory.getSlots(); i++) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
         }

         ISocketable socketable = null;

         for (int i = 0; i < items.size(); i++) {
            if (i != 0) {
               ItemStack stack = ItemStack.parseOptional(provider, items.getCompound(i));
               if (i == 6) {
                  this.setSocketableStack(stack);
                  if (!stack.isEmpty()) {
                     socketable = (ISocketable)stack.getCapability(PsiAPI.SOCKETABLE_CAPABILITY);
                  }
               } else if (i == 1) {
                  this.setStackForComponent(EnumCADComponent.CORE, stack);
               } else if (i == 2) {
                  this.setStackForComponent(EnumCADComponent.ASSEMBLY, stack);
               } else if (i == 3) {
                  this.setStackForComponent(EnumCADComponent.SOCKET, stack);
               } else if (i == 4) {
                  this.setStackForComponent(EnumCADComponent.BATTERY, stack);
               } else if (i == 5) {
                  this.setStackForComponent(EnumCADComponent.DYE, stack);
               } else {
                  int idx = i - 7;
                  if (socketable != null) {
                     socketable.setBulletInSocket(idx, stack);
                  }
               }
            }
         }
      } else {
         for (int ix = 0; ix < items.size(); ix++) {
            CompoundTag compoundtag = items.getCompound(ix);
            int j = compoundtag.getByte("Slot") & 255;
            if (j < this.inventory.getItems().size()) {
               this.inventory.getItems().set(j, ItemStack.parse(provider, compoundtag).orElse(ItemStack.EMPTY));
            }
         }
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (e, provider) -> this.getUpdateTag(provider));
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider provider) {
      CompoundTag cmp = new CompoundTag();
      this.saveAdditional(cmp, provider);
      return cmp;
   }

   @NotNull
   public Component getDisplayName() {
      return Component.translatable(((BlockCADAssembler)ModBlocks.cadAssembler.get()).getDescriptionId());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
      return new ContainerCADAssembler(i, playerInventory, this);
   }

   private class CADStackHandler extends ItemStackHandler {
      private CADStackHandler() {
         super(6);
      }

      private NonNullList<ItemStack> getItems() {
         return this.stacks;
      }

      protected void onContentsChanged(int slot) {
         super.onContentsChanged(slot);
         if (0 < slot && slot < 6) {
            TileCADAssembler.this.clearCachedCAD();
         }

         TileCADAssembler.this.setChanged();
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         if (stack.isEmpty()) {
            return true;
         } else if (slot == 0) {
            return ISocketable.isSocketable(stack);
         } else {
            return slot >= 6
               ? false
               : stack.getItem() instanceof ICADComponent && ((ICADComponent)stack.getItem()).getComponentType(stack).ordinal() == slot - 1;
         }
      }
   }
}
