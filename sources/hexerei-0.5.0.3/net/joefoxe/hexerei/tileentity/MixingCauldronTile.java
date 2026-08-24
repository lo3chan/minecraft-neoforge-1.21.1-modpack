package net.joefoxe.hexerei.tileentity;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.CauldronParticleData;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.EmitParticlesPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class MixingCauldronTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider, IFluidHandler {
   public boolean crafting;
   public int craftDelay;
   public int craftDelayOld;
   public int emitParticles = 0;
   public boolean emitParticleSpout = false;
   public float degrees;
   private boolean crafted;
   private boolean extracted = false;
   private int isColliding = 0;
   public static final int craftDelayMax = 100;
   private long tickedGameTime;
   public int dyeColor = 4337438;
   public boolean hasHeatSource = false;
   public boolean usingRecipeNeedsHeat = false;
   public MoonPhases.MoonCondition usingRecipeNeedsMoonPhase = MoonPhases.MoonCondition.NONE;
   public Component customName;
   public NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);
   private FluidStack fluidStack = FluidStack.EMPTY;
   private static final int[] SLOTS_INPUT = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
   private static final int[] SLOTS_OUTPUT = new int[]{8};
   VoxelShape BLOOD_SIGIL_SHAPE = Block.box(2.0, 3.0, 2.0, 14.0, 6.0, 14.0);
   VoxelShape HOPPER_SHAPE = Block.box(2.0, 3.0, 2.0, 14.0, 6.0, 14.0);
   public float fluidRenderLevel = 0.0F;
   public FluidStack renderedFluid;
   public boolean checkCraft = true;

   public MixingCauldronTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   public MixingCauldronTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.MIXING_CAULDRON_TILE.get(), blockPos, blockState);
   }

   public FluidStack getFluidStack() {
      return this.fluidStack;
   }

   public void setFluidStack(FluidStack fluidStack) {
      this.fluidStack = fluidStack;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
   }

   public Component getDisplayName() {
      return (Component)(this.customName != null ? this.customName : Component.literal(""));
   }

   public Component getCustomName() {
      return this.customName;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public void setDyeColor(int dyeColor) {
      this.dyeColor = dyeColor;
   }

   public int getDyeColor() {
      DyeColor dye = HexereiUtil.getDyeColorNamed(this.getDisplayName().getString());
      return dye != null ? HexereiUtil.getColorValue(dye) : this.dyeColor;
   }

   public void setChanged() {
      super.setChanged();
      this.checkCraft = true;
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
            this.level.sendBlockUpdated(this.getPos(), this.level.getBlockState(this.getPos()), this.level.getBlockState(this.getPos()), 2);
         }
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   protected Component getDefaultName() {
      return Component.translatable("container.hexerei.mixing_cauldron");
   }

   public void setContents(List<ItemStack> stacks, Player player) {
      for (int i = 0; i < stacks.size(); i++) {
         if ((i < 8 || !stacks.get(i).isEmpty()) && !ItemStack.isSameItemSameComponents((ItemStack)this.items.get(i), stacks.get(i))) {
            int slot = player.getInventory().findSlotMatchingItem(stacks.get(i));
            ItemStack stack = ContainerHelper.removeItem(player.getInventory().items, slot, 1);
            player.getInventory().placeItemBackInInventory((ItemStack)this.items.get(i));
            this.items.set(i, stack);
         }
      }

      this.setChanged();
   }

   public ItemStack getItem(int index) {
      return index >= 0 && index < this.items.size() ? (ItemStack)this.items.get(index) : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemStack = ContainerHelper.removeItem(this.items, index, count);
      if (itemStack.getCount() < 1) {
         itemStack.setCount(1);
      }

      return itemStack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.items, index);
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < this.items.size()) {
         ItemStack itemStack = stack.copy();
         this.items.set(index, itemStack);
      }

      this.setChanged();
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      if (index == 8) {
         return false;
      } else {
         return index == 9 && !stack.is(HexereiTags.Items.SIGILS) ? false : ((ItemStack)this.items.get(index)).isEmpty();
      }
   }

   public int[] getSlotsForFace(Direction side) {
      return side == Direction.DOWN ? SLOTS_OUTPUT : SLOTS_INPUT;
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return this.canPlaceItem(index, itemStackIn);
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return true;
   }

   public int getCraftDelay() {
      return this.craftDelay;
   }

   public void setCraftDelay(int delay) {
      this.craftDelay = delay;
   }

   protected AbstractContainerMenu createMenu(int id, Inventory player) {
      return new MixingCauldronContainer(id, this.level, this.getPos(), player, player.player);
   }

   public void clearContent() {
      super.clearContent();
      this.items.clear();
   }

   public void requestModelDataUpdate() {
      super.requestModelDataUpdate();
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      this.fluidStack = tag.contains("fluid") ? FluidStack.parseOptional(registries, tag.getCompound("fluid")) : FluidStack.EMPTY;
      if (tag.contains("CustomName", 8)) {
         this.customName = Serializer.fromJson(tag.getString("CustomName"), registries);
      }

      if (tag.contains("hasHeatSource")) {
         this.hasHeatSource = tag.getBoolean("hasHeatSource");
      }

      if (tag.contains("DyeColor")) {
         this.dyeColor = tag.getInt("DyeColor");
      }

      if (tag.contains("delay")) {
         this.craftDelay = tag.getInt("delay");
      }

      if (tag.contains("delayOld")) {
         this.craftDelayOld = tag.getInt("delayOld");
      }

      if (tag.contains("usingRecipeNeedsHeat")) {
         this.usingRecipeNeedsHeat = tag.getBoolean("usingRecipeNeedsHeat");
      }

      if (tag.contains("usingRecipeNeedsMoonPhase")) {
         this.usingRecipeNeedsMoonPhase = MoonPhases.MoonCondition.getMoonCondition(tag.getString("usingRecipeNeedsMoonPhase"));
      }

      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      ContainerHelper.saveAllItems(compound, this.items, registries);
      if (!this.fluidStack.isEmpty()) {
         compound.put("fluid", this.fluidStack.save(registries));
      }

      compound.putInt("delay", this.craftDelay);
      compound.putInt("delayOld", this.craftDelayOld);
      compound.putInt("DyeColor", this.dyeColor);
      compound.putBoolean("hasHeatSource", this.hasHeatSource);
      compound.putBoolean("usingRecipeNeedsHeat", this.usingRecipeNeedsHeat);
      compound.putString("usingRecipeNeedsMoonPhase", this.usingRecipeNeedsMoonPhase.getSerializedName());
      if (this.customName != null) {
         compound.putString("CustomName", Serializer.toJson(this.customName, registries));
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      this.saveAdditional(tag, registries);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public void onLoad() {
      super.onLoad();
   }

   public Item getItemInSlot(int slot) {
      return ((ItemStack)this.items.get(slot)).getItem();
   }

   public ItemStack getItemStackInSlot(int slot) {
      return (ItemStack)this.items.get(slot);
   }

   public int getCraftMaxDelay() {
      return 100;
   }

   public boolean getCrafted() {
      return this.crafted;
   }

   public int getNumberOfItems() {
      int num = 0;

      for (int i = 0; i < 8; i++) {
         if (this.items.get(i) != ItemStack.EMPTY) {
            num++;
         }
      }

      return num;
   }

   private void strikeLightning() {
      if (!this.level.isClientSide()) {
         LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.level);
         if (lightningbolt != null) {
            lightningbolt.moveTo(Vec3.atBottomCenterOf(this.worldPosition));
            this.level.addFreshEntity(lightningbolt);
         }
      }
   }

   public void entityInside(Entity entity) {
      BlockPos blockpos = this.getPos();
      if (entity instanceof ItemEntity) {
         if (Shapes.joinIsNotEmpty(
               Shapes.create(entity.getBoundingBox().move(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())), this.HOPPER_SHAPE, BooleanOp.AND
            )
            && this.captureItem((ItemEntity)entity)
            && !this.level.isClientSide) {
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new EmitParticlesPacket(this.worldPosition, 2, true));
         }
      } else if (Shapes.joinIsNotEmpty(
         Shapes.create(entity.getBoundingBox().move(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())), this.BLOOD_SIGIL_SHAPE, BooleanOp.AND
      )) {
         if (this.isColliding <= 1 && this.getItemInSlot(9).asItem() == ModItems.BLOOD_SIGIL.get()) {
            entity.hurt(entity.damageSources().magic(), 3.0F);
            if (this.fluidStack.isEmpty()
               || FluidStack.isSameFluidSameComponents(this.fluidStack, new FluidStack((Fluid)ModFluids.BLOOD_FLUID.get(), 1))
                  && this.getFluidStack().getAmount() < this.getTankCapacity(0)) {
               if (this.fluidStack.isEmpty()) {
                  this.fill(new FluidStack((Fluid)ModFluids.BLOOD_FLUID.get(), 250), FluidAction.EXECUTE);
               } else {
                  this.getFluidStack().grow(250);
                  this.setChanged();
               }

               this.level.playSound(null, entity.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.BLOCKS, 1.0F, 1.0F);
               if (!this.level.isClientSide) {
                  HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new EmitParticlesPacket(this.worldPosition, 2, true));
               }
            }
         }

         this.isColliding = 6;
      }
   }

   public boolean captureItem(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.getItem().copy();
      if (this.getFirstOpenSlot() >= 0) {
         ItemStack temp = itemstack.copy();
         temp.setCount(1);
         this.setItem(this.getFirstOpenSlot(), temp);
         itemEntity.getItem().shrink(1);
         return true;
      } else {
         return false;
      }
   }

   public int getFirstOpenSlot() {
      for (int i = 0; i < 8; i++) {
         if (((ItemStack)this.items.get(i)).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public void craft() {
      this.crafting = false;
      boolean usingRecipeNeedsHeatOld = this.usingRecipeNeedsHeat;
      this.usingRecipeNeedsHeat = false;
      MoonPhases.MoonCondition usingRecipeNeedsMoonPhaseOld = this.usingRecipeNeedsMoonPhase;
      this.usingRecipeNeedsMoonPhase = MoonPhases.MoonCondition.NONE;
      MixingCauldronRecipe.MixingCauldronRecipeInput inv = MixingCauldronRecipe.createInput(this.items.stream().limit(8L).toList());
      if (PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty()) {
         PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes(this.level.potionBrewing());
         PotionMixingRecipes.BY_ITEM = PotionMixingRecipes.sortRecipesByItem(PotionMixingRecipes.ALL);
      }

      RecipeManager rm = this.level.getRecipeManager();
      Optional<RecipeHolder<MixingCauldronRecipe>> recipe = rm.getRecipeFor((RecipeType)ModRecipeTypes.MIXING_CAULDRON_TYPE.get(), inv, this.level);
      List<RecipeHolder<FluidMixingRecipe>> non_potion_mixing = rm.getAllRecipesFor((RecipeType)ModRecipeTypes.FLUID_MIXING_TYPE.get())
         .stream()
         .filter(potionRecipe -> ((FluidMixingRecipe)potionRecipe.value()).matches(inv, this.level))
         .toList();
      List<FluidMixingRecipe> potion_mixing = PotionMixingRecipes.ALL.stream().filter(potionRecipe -> {
         if (potionRecipe.getIngredients().isEmpty()) {
            PotionMixingRecipes.ALL = null;
            return false;
         } else {
            return potionRecipe.matches(inv, this.level);
         }
      }).toList();
      List<FluidMixingRecipe> recipe2 = Stream.concat(non_potion_mixing.stream().map(RecipeHolder::value).toList().stream(), potion_mixing.stream())
         .collect(Collectors.toList());
      boolean matchesRecipe = false;
      ResourceLocation fl2 = BuiltInRegistries.FLUID.getKey(this.fluidStack.getFluid());

      for (FluidMixingRecipe fluidMixingRecipe : recipe2) {
         boolean fluidEqual = FluidStack.isSameFluidSameComponents(fluidMixingRecipe.getLiquid(), this.fluidStack);
         if (fluidEqual) {
            matchesRecipe = true;
            break;
         }
      }

      if (!matchesRecipe) {
         recipe2 = this.level
            .getRecipeManager()
            .getRecipeFor((RecipeType)ModRecipeTypes.FLUID_MIXING_TYPE.get(), inv, this.level)
            .stream()
            .<FluidMixingRecipe>map(RecipeHolder::value)
            .toList();
      }

      AtomicBoolean firstRecipe = new AtomicBoolean(false);
      if (recipe.isPresent()) {
         RecipeHolder<MixingCauldronRecipe> iRecipe = recipe.get();
         ItemStack output = ((MixingCauldronRecipe)iRecipe.value()).assemble(inv, this.level.registryAccess());
         FluidStack recipeFluid = ((MixingCauldronRecipe)iRecipe.value()).getLiquid();
         FluidStack containerFluid = this.getFluidStack();
         boolean fluidEqual = FluidStack.isSameFluidSameComponents(recipeFluid, containerFluid);
         boolean outputClear = this.items.get(8) == ItemStack.EMPTY
            || ((ItemStack)this.items.get(8)).getCount() == 0
            || ItemStack.isSameItem(output, (ItemStack)this.items.get(8))
               && ((ItemStack)this.items.get(8)).getCount() + output.getCount() <= ((ItemStack)this.items.get(8)).getMaxStackSize();
         boolean hasEnoughFluid = ((MixingCauldronRecipe)iRecipe.value()).getFluidLevelsConsumed() <= this.getFluidStack().getAmount();
         boolean needsHeat = ((MixingCauldronRecipe)iRecipe.value()).getHeatCondition() != FluidMixingRecipe.HeatCondition.NONE;
         boolean needsMoonPhase = ((MixingCauldronRecipe)iRecipe.value()).getMoonCondition() != MoonPhases.MoonCondition.NONE;
         BlockState heatSource = this.level.getBlockState(this.getPos().below());
         this.usingRecipeNeedsHeat = needsHeat;
         this.usingRecipeNeedsMoonPhase = ((MixingCauldronRecipe)iRecipe.value()).getMoonCondition();
         if ((!needsMoonPhase || MoonPhases.MoonCondition.getMoonPhase(this.level) == ((MixingCauldronRecipe)iRecipe.value()).getMoonCondition())
            && fluidEqual
            && !this.crafted
            && hasEnoughFluid
            && outputClear) {
            firstRecipe.set(true);
            if (!needsHeat
               || heatSource.is(HexereiTags.Blocks.HEAT_SOURCES)
                  && (!heatSource.hasProperty(BlockStateProperties.LIT) || (Boolean)heatSource.getValue(BlockStateProperties.LIT))) {
               this.crafting = true;
               if (this.craftDelay >= 100) {
                  this.craftTheItem(output);
                  int temp = this.getFluidStack().getAmount();
                  DataComponentPatch patch = ((MixingCauldronRecipe)iRecipe.value()).getLiquidOutput().copy().getComponentsPatch();
                  this.getFluidStack().shrink(this.getTankCapacity(0));
                  FluidStack fluidStack1 = new FluidStack(
                     ((MixingCauldronRecipe)iRecipe.value()).getLiquidOutput().getFluid(),
                     temp - ((MixingCauldronRecipe)iRecipe.value()).getFluidLevelsConsumed()
                  );
                  fluidStack1.applyComponents(patch);
                  this.fill(fluidStack1, FluidAction.EXECUTE);
                  this.crafted = true;
                  HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new EmitParticlesPacket(this.worldPosition, 10, true));
                  this.normalizeTank();
                  this.setChanged();
               }
            }
         }
      }

      if (!firstRecipe.get() && !recipe2.isEmpty()) {
         for (FluidMixingRecipe fluidMixingRecipex : recipe2) {
            ItemStack output = fluidMixingRecipex.assemble(inv, this.level.registryAccess());
            FluidStack recipeFluid = fluidMixingRecipex.getLiquid();
            FluidStack containerFluid = this.getFluidStack();
            boolean fluidEqual = FluidStack.isSameFluidSameComponents(recipeFluid, containerFluid);
            ResourceLocation fl1 = BuiltInRegistries.FLUID.getKey(fluidMixingRecipex.getLiquid().getFluid());
            if (!fluidEqual && fl1 != null && fl2 != null && fl1.getPath().equals(fl2.getPath())) {
               boolean flag = FluidStack.isSameFluidSameComponents(fluidMixingRecipex.getLiquid().copy(), this.fluidStack.copy());
               if (flag) {
                  fluidEqual = true;
               }
            }

            boolean hasEnoughFluid = this.getFluidStack().getAmount() >= 50;
            boolean needsHeat = fluidMixingRecipex.getHeatCondition() != FluidMixingRecipe.HeatCondition.NONE;
            if (fluidEqual && !this.crafted && hasEnoughFluid) {
               BlockState heatSource = this.level.getBlockState(this.getPos().below());
               this.usingRecipeNeedsHeat = needsHeat;
               this.usingRecipeNeedsMoonPhase = MoonPhases.MoonCondition.NONE;
               if ((!needsHeat || heatSource.is(HexereiTags.Blocks.HEAT_SOURCES))
                  && (!heatSource.hasProperty(BlockStateProperties.LIT) || (Boolean)heatSource.getValue(BlockStateProperties.LIT))) {
                  this.crafting = true;
                  if (this.craftDelay >= 100) {
                     this.craftTheItem(output);
                     int temp = this.getFluidStack().getAmount();
                     DataComponentPatch patch = fluidMixingRecipex.getLiquidOutput().copy().getComponentsPatch();
                     this.getFluidStack().shrink(this.getTankCapacity(0));
                     FluidStack fluidStack1 = new FluidStack(fluidMixingRecipex.getLiquidOutput().getFluid(), temp);
                     fluidStack1.applyComponents(patch);
                     this.fill(fluidStack1, FluidAction.EXECUTE);
                     this.crafted = true;
                     HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new EmitParticlesPacket(this.worldPosition, 10, true));
                     this.setChanged();
                  }
               }
            }
         }
      }

      if (usingRecipeNeedsHeatOld != this.usingRecipeNeedsHeat || usingRecipeNeedsMoonPhaseOld != this.usingRecipeNeedsMoonPhase) {
         this.setChanged();
      }
   }

   private void craftTheItem(ItemStack output) {
      if (output.getItem() == ModItems.TALLOW_IMPURITY.get()) {
         Random random = new Random();
         if (random.nextInt(5) != 0) {
            output = ItemStack.EMPTY;
         }
      }

      this.setItem(0, ItemStack.EMPTY);
      this.setItem(1, ItemStack.EMPTY);
      this.setItem(2, ItemStack.EMPTY);
      this.setItem(3, ItemStack.EMPTY);
      this.setItem(4, ItemStack.EMPTY);
      this.setItem(5, ItemStack.EMPTY);
      this.setItem(6, ItemStack.EMPTY);
      this.setItem(7, ItemStack.EMPTY);
      if (ItemStack.isSameItem(output, this.getItem(8))) {
         this.getItem(8).setCount(this.getItem(8).getCount() + output.getCount());
      } else {
         this.setItem(8, output);
      }
   }

   public void tick() {
      this.craftDelayOld = this.craftDelay;
      if (this.level.isClientSide) {
         if (!this.fluidStack.isEmpty()) {
            this.renderedFluid = this.fluidStack.copy();
         }

         if (this.renderedFluid != null) {
            this.renderedFluid.setAmount((int)this.fluidRenderLevel);
         }

         this.renderParticles();
      } else {
         this.tickedGameTime = this.level.getGameTime();
         if (this.checkCraft) {
            this.craft();
            this.checkCraft = false;
         }

         if (this.crafting) {
            this.craftDelay += 2;
         }

         if (this.craftDelay >= 1) {
            this.craftDelay--;
         }

         if (this.craftDelay > 0 && !this.level.isClientSide) {
            this.sync();
         }

         if (this.craftDelay > 100) {
            this.craft();
         }

         if (this.craftDelay < 10 && this.crafted) {
            this.checkCraft = true;
            this.crafted = false;
         }

         if (this.extracted) {
            this.extracted = false;
            this.setChanged();
         }

         this.isColliding--;
      }
   }

   private void renderParticles() {
      float fillPercentage = 0.0F;
      FluidStack fluidStack = this.getFluidInTank(0).copy();
      if (!fluidStack.isEmpty()) {
         fillPercentage = Math.min(1.0F, (float)fluidStack.getAmount() / this.getTankCapacity(0));
      }

      float height = 0.25F + 0.6875F * fillPercentage;
      Random rand = new Random();
      if (fluidStack.isEmpty() && this.renderedFluid != null) {
         fluidStack = this.renderedFluid.copy();
      }

      if (!fluidStack.isEmpty()) {
         if (this.emitParticles > 0 && this.level != null) {
            for (int i = 0; i < 3; i++) {
               if (this.emitParticleSpout) {
                  if (rand.nextInt(3) == 0) {
                     this.level
                        .addParticle(
                           new BlockParticleOption(ParticleTypes.BLOCK, this.level.getBlockState(this.worldPosition)),
                           this.worldPosition.getX() + 0.5F,
                           this.worldPosition.getY() + height,
                           this.worldPosition.getZ() + 0.5F,
                           (rand.nextDouble() - 0.5) / 20.0,
                           (rand.nextDouble() + 0.5) * 2.0,
                           (rand.nextDouble() - 0.5) / 20.0
                        );
                  }

                  if (rand.nextInt(3) == 0) {
                     this.level
                        .addParticle(
                           new BlockParticleOption(ParticleTypes.BLOCK, this.level.getBlockState(this.worldPosition)),
                           this.worldPosition.getX() + 0.5F,
                           this.worldPosition.getY() + height,
                           this.worldPosition.getZ() + 0.5F,
                           (rand.nextDouble() - 0.5) / 20.0,
                           (rand.nextDouble() + 0.5) * 2.0,
                           (rand.nextDouble() - 0.5) / 20.0
                        );
                  }

                  if (rand.nextInt(3) == 0) {
                     this.level
                        .addParticle(
                           new BlockParticleOption(ParticleTypes.BLOCK, this.level.getBlockState(this.worldPosition)),
                           this.worldPosition.getX() + 0.5F,
                           this.worldPosition.getY() + height,
                           this.worldPosition.getZ() + 0.5F,
                           (rand.nextDouble() - 0.5) / 20.0,
                           (rand.nextDouble() + 0.5) * 2.0,
                           (rand.nextDouble() - 0.5) / 20.0
                        );
                  }

                  if (rand.nextInt(3) == 0) {
                     this.level
                        .addParticle(
                           ParticleTypes.SMOKE,
                           this.worldPosition.getX() + 0.5F,
                           this.worldPosition.getY() + height,
                           this.worldPosition.getZ() + 0.5F,
                           (rand.nextDouble() - 0.5) / 50.0,
                           (rand.nextDouble() + 0.5) * 0.045,
                           (rand.nextDouble() - 0.5) / 50.0
                        );
                  }

                  if (rand.nextInt(3) == 0) {
                     this.level
                        .addParticle(
                           ParticleTypes.SMOKE,
                           this.worldPosition.getX() + 0.5F,
                           this.worldPosition.getY() + height,
                           this.worldPosition.getZ() + 0.5F,
                           (rand.nextDouble() - 0.5) / 50.0,
                           (rand.nextDouble() + 0.5) * 0.045,
                           (rand.nextDouble() - 0.5) / 50.0
                        );
                  }
               }

               if (rand.nextInt(3) == 0) {
                  this.level
                     .addParticle(
                        new CauldronParticleData(fluidStack),
                        this.worldPosition.getX() + 0.2 + 0.6 * rand.nextDouble(),
                        this.worldPosition.getY() + height,
                        this.worldPosition.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                        (rand.nextDouble() - 0.5) / 50.0,
                        (rand.nextDouble() + 0.5) * 0.024,
                        (rand.nextDouble() - 0.5) / 50.0
                     );
               }

               if (rand.nextInt(3) == 0) {
                  this.level
                     .addParticle(
                        new CauldronParticleData(fluidStack),
                        this.worldPosition.getX() + 0.2 + 0.6 * rand.nextDouble(),
                        this.worldPosition.getY() + height,
                        this.worldPosition.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                        (rand.nextDouble() - 0.5) / 50.0,
                        (rand.nextDouble() + 0.5) * 0.024,
                        (rand.nextDouble() - 0.5) / 50.0
                     );
               }

               if (rand.nextInt(3) == 0) {
                  this.level
                     .addParticle(
                        new CauldronParticleData(fluidStack),
                        this.worldPosition.getX() + 0.2 + 0.6 * rand.nextDouble(),
                        this.worldPosition.getY() + height,
                        this.worldPosition.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                        (rand.nextDouble() - 0.5) / 50.0,
                        (rand.nextDouble() + 0.5) * 0.024,
                        (rand.nextDouble() - 0.5) / 50.0
                     );
               }

               if (rand.nextInt(3) == 0) {
                  this.level
                     .addParticle(
                        new CauldronParticleData(fluidStack),
                        this.worldPosition.getX() + 0.2 + 0.6 * rand.nextDouble(),
                        this.worldPosition.getY() + height,
                        this.worldPosition.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                        (rand.nextDouble() - 0.5) / 50.0,
                        (rand.nextDouble() + 0.5) * 0.024,
                        (rand.nextDouble() - 0.5) / 50.0
                     );
               }

               if (rand.nextInt(3) == 0) {
                  this.level
                     .addParticle(
                        new CauldronParticleData(fluidStack),
                        this.worldPosition.getX() + 0.2 + 0.6 * rand.nextDouble(),
                        this.worldPosition.getY() + height,
                        this.worldPosition.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                        (rand.nextDouble() - 0.5) / 50.0,
                        (rand.nextDouble() + 0.5) * 0.024,
                        (rand.nextDouble() - 0.5) / 50.0
                     );
               }
            }

            this.emitParticles--;
         }
      }
   }

   public BlockPos getPos() {
      return this.worldPosition;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for (ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public int getTanks() {
      return 1;
   }

   @NotNull
   public FluidStack getFluidInTank(int tank) {
      return this.fluidStack.copy();
   }

   public int getTankCapacity(int tank) {
      return 2000;
   }

   public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
      return true;
   }

   public int fill(FluidStack resource, FluidAction action) {
      if (!resource.isEmpty() && (this.fluidStack.isEmpty() || FluidStack.isSameFluidSameComponents(this.fluidStack, resource))) {
         int amount = Math.min(resource.getAmount(), this.getTankCapacity(0) - this.fluidStack.getAmount());
         if (action.execute()) {
            FluidStack newStack = resource.copy();
            newStack.setAmount(this.fluidStack.getAmount() + amount);
            this.fluidStack = newStack;
            this.setChanged();
         }

         return amount;
      } else {
         return 0;
      }
   }

   public void normalizeTank() {
      if (this.getFluidStack().getAmount() % 10 == 1) {
         this.getFluidStack().shrink(1);
      }

      if (this.getFluidStack().getAmount() % 10 == 9) {
         this.getFluidStack().grow(1);
      }
   }

   @Nonnull
   public FluidStack drain(FluidStack resource, FluidAction action) {
      if (!resource.isEmpty() && !this.fluidStack.isEmpty() && this.fluidStack.getFluid().isSame(resource.getFluid())) {
         int amount = Math.min(resource.getAmount(), this.fluidStack.getAmount());
         FluidStack returnStack = this.fluidStack.copy();
         returnStack.setAmount(amount);
         if (action.execute()) {
            this.fluidStack.shrink(amount);
            this.setChanged();
         }

         return returnStack;
      } else {
         return FluidStack.EMPTY;
      }
   }

   @Nonnull
   public FluidStack drain(int maxDrain, FluidAction action) {
      if (maxDrain > 0 && !this.fluidStack.isEmpty()) {
         int amount = Math.min(maxDrain, this.fluidStack.getAmount());
         FluidStack returnStack = this.fluidStack.copy();
         returnStack.setAmount(amount);
         if (action.execute()) {
            this.fluidStack.shrink(amount);
            this.setChanged();
         }

         return returnStack;
      } else {
         return FluidStack.EMPTY;
      }
   }

   public boolean interactWithFluid(IFluidHandlerItem fluidHandler) {
      if (fluidHandler.getTanks() == 0) {
         return false;
      } else {
         FluidStack cauldronFluid = fluidHandler.getFluidInTank(0);
         Random random = new Random();
         if (cauldronFluid.isEmpty()) {
            if (!this.fluidStack.isEmpty() && fluidHandler.isFluidValid(0, this.fluidStack)) {
               int amount = fluidHandler.fill(this.fluidStack.copy(), FluidAction.EXECUTE);
               if (amount > 0) {
                  if (this.getLevel() != null) {
                     this.getLevel()
                        .playSound(
                           null,
                           this.getPos().getX() + 0.5F,
                           this.getPos().getY() + 0.5F,
                           this.getPos().getZ() + 0.5F,
                           fluidHandler.getFluidInTank(1).getFluid().getPickupSound().isPresent()
                              ? (SoundEvent)fluidHandler.getFluidInTank(1).getFluid().getPickupSound().get()
                              : SoundEvents.BUCKET_EMPTY,
                           SoundSource.BLOCKS,
                           1.0F,
                           0.8F + 0.4F * random.nextFloat()
                        );
                  }

                  this.fluidStack.shrink(amount);
                  this.setChanged();
                  return true;
               }
            }
         } else if (this.fluidStack.isEmpty() || FluidStack.isSameFluidSameComponents(this.fluidStack, cauldronFluid)) {
            cauldronFluid = cauldronFluid.copy();
            cauldronFluid.setAmount(this.getTankCapacity(0) - this.fluidStack.getAmount());
            FluidStack amount = fluidHandler.drain(cauldronFluid, FluidAction.SIMULATE);
            if (!amount.isEmpty() && (this.fluidStack.isEmpty() || FluidStack.isSameFluidSameComponents(this.fluidStack, amount))) {
               amount = fluidHandler.drain(cauldronFluid, FluidAction.EXECUTE);
               amount.grow(this.fluidStack.getAmount());
               this.fluidStack = amount;
               if (this.getLevel() != null) {
                  this.getLevel()
                     .playSound(
                        null,
                        this.getPos().getX() + 0.5F,
                        this.getPos().getY() + 0.5F,
                        this.getPos().getZ() + 0.5F,
                        amount.getFluid().getPickupSound().isPresent() ? (SoundEvent)amount.getFluid().getPickupSound().get() : SoundEvents.BUCKET_FILL,
                        SoundSource.BLOCKS,
                        1.0F,
                        0.8F + 0.4F * random.nextFloat()
                     );
               }

               this.setChanged();
               return true;
            }
         }

         return false;
      }
   }
}
