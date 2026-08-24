package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.advancements.criterion.DiscoverPropertyTrigger;
import net.cibernet.alchemancy.advancements.criterion.PerformForgeRecipeTrigger;
import net.cibernet.alchemancy.blocks.blockentities.AlchemancyCatalystBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.S2CDiscoverCodexIngredientsPayload;
import net.cibernet.alchemancy.network.S2CUnlockCodexEntriesPayload;
import net.cibernet.alchemancy.properties.TintedProperty;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyCriteriaTriggers;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class AlchemancyCatalystBlock extends TransparentBlock implements EntityBlock {
   private static CachedCheck<ForgeRecipeGrid, AbstractForgeRecipe<?>> RECIPE_CHECK;
   private static final MapCodec<AlchemancyCatalystBlock> CODEC = simpleCodec(AlchemancyCatalystBlock::new);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public AlchemancyCatalystBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(POWERED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{POWERED});
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         performRecipe(player, level, pos);
      }

      return InteractionResult.SUCCESS_NO_ITEM_USED;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      boolean isDye = stack.getItem() instanceof DyeItem;
      if (level.getBlockEntity(pos) instanceof AlchemancyCatalystBlockEntity catalyst && (isDye || stack.is(AlchemancyItems.CHROMA_LENS))) {
         int[] tint = this.getTint(stack);
         boolean success = false;
         if (tint.length == 0) {
            tint = null;
         }

         if (stack.getItem() instanceof DyeItem dye && !catalyst.getCrystalTexture().equals(dye.getDyeColor().getName())) {
            catalyst.setCrystalTexture(dye.getDyeColor());
            success = true;
         }

         if (!Arrays.equals(catalyst.getTintColors(), tint)) {
            catalyst.setTint(tint);
            success = true;
         }

         boolean muffled = InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.MUFFLED);
         if (muffled != catalyst.silent) {
            catalyst.silent = muffled;
            success = true;
         }

         if (success) {
            if (isDye) {
               stack.consume(1, player);
            }

            return ItemInteractionResult.SUCCESS;
         }
      }

      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }

   private int[] getTint(ItemStack stack) {
      int alpha = ARGB32.alpha(CommonUtils.getPropertyDrivenTint(stack));
      int[] tintedColors = Arrays.stream(((TintedProperty)AlchemancyProperties.TINTED.get()).getData(stack)).mapToInt(c -> ARGB32.color(alpha, c)).toArray();
      return tintedColors.length == 0 ? new int[]{ARGB32.color(alpha, 16777215)} : tintedColors;
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      boolean neighborPowered = level.hasNeighborSignal(pos);
      boolean powered = (Boolean)state.getValue(POWERED);
      BlockEntity blockentity = level.getBlockEntity(pos);
      if (neighborPowered && !powered) {
         level.scheduleTick(pos, this, 4);
         level.setBlock(pos, (BlockState)state.setValue(POWERED, Boolean.TRUE), 2);
         performRecipe(null, level, pos);
      } else if (!neighborPowered && powered) {
         level.setBlock(pos, (BlockState)state.setValue(POWERED, Boolean.FALSE), 2);
      }
   }

   public static void performRecipe(@Nullable Player player, Level level, BlockPos catalystPos) {
      if (RECIPE_CHECK == null) {
         RECIPE_CHECK = createCheck((RecipeType<AbstractForgeRecipe<?>>)AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get());
      }

      BlockPos forgePos = catalystPos.below(2);
      if (level.getBlockState(forgePos).is(AlchemancyBlocks.ALCHEMANCY_FORGE) && level.getBlockEntity(forgePos) instanceof ItemStackHolderBlockEntity forge) {
         ForgeRecipeGrid grid = new ForgeRecipeGrid(level, forgePos, forge);
         List<ResourceLocation> itemsToDiscover = grid.getItemPedestals()
            .stream()
            .filter(pedestal -> !pedestal.isEmpty())
            .map(pedestal -> BuiltInRegistries.ITEM.getKey(pedestal.getItem().getItem()))
            .toList();
         AtomicBoolean loop = new AtomicBoolean(true);

         for (int i = 0; i < 128 && loop.get() && !grid.isPerformingTransmutation(); i++) {
            RECIPE_CHECK.getRecipeFor(grid, level)
               .ifPresentOrElse(
                  recipe -> {
                     if (player instanceof ServerPlayer serverPlayer) {
                        ((PerformForgeRecipeTrigger)AlchemancyCriteriaTriggers.PERFORM_FORGE_RECIPE.get())
                           .trigger(serverPlayer, (RecipeHolder<AbstractForgeRecipe<?>>)recipe, grid);
                     }

                     grid.processRecipe((AbstractForgeRecipe<?>)recipe.value(), level.registryAccess());
                  },
                  () -> loop.set(false)
               );
         }

         ItemStack output = grid.getCurrentOutput();
         if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new S2CUnlockCodexEntriesPayload(output), new CustomPacketPayload[0]);
            if (!itemsToDiscover.isEmpty()) {
               ((DiscoverPropertyTrigger)AlchemancyCriteriaTriggers.DISCOVER_PROPERTY.get()).trigger(serverPlayer, output);
               PacketDistributor.sendToPlayer(serverPlayer, new S2CDiscoverCodexIngredientsPayload(itemsToDiscover), new CustomPacketPayload[0]);
            }
         }

         if (grid.shouldConsumeWarped()) {
            InfusedPropertiesHelper.removeProperty(output, AlchemancyProperties.WARPED);
         }

         forge.removeItem(1);
         InfusedPropertiesHelper.truncateProperties(output);
         grid.applyGlint.ifPresent(aBoolean -> output.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, aBoolean));
         ItemStackHolderBlockEntity.dropItem(level, forgePos, output);
         if (level.getBlockEntity(catalystPos) instanceof AlchemancyCatalystBlockEntity catalyst) {
            catalyst.playAnimation(false);
            level.gameEvent(GameEvent.BLOCK_ACTIVATE, catalystPos, new Context(player, level.getBlockState(catalystPos)));
         }
      }
   }

   public static CachedCheck<ForgeRecipeGrid, AbstractForgeRecipe<?>> createCheck(RecipeType<AbstractForgeRecipe<?>> recipeType) {
      return (input, level) -> level.getRecipeManager()
         .getRecipesFor(recipeType, input, level)
         .stream()
         .filter(recipe -> ((AbstractForgeRecipe)recipe.value()).matches(input, level))
         .min(Comparator.comparingInt(recipe -> ((AbstractForgeRecipe)recipe.value()).getRecipeCompareValue(input)));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new AlchemancyCatalystBlockEntity(pos, state);
   }

   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (level.getBlockEntity(pos) instanceof AlchemancyCatalystBlockEntity catalyst) {
         catalyst.randomizeSpinOffset(level.random);
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return (BlockEntityTicker<T>)createTicker(level, blockEntityType);
   }

   @javax.annotation.Nullable
   protected static <T extends AlchemancyCatalystBlockEntity> BlockEntityTicker<AlchemancyCatalystBlockEntity> createTicker(
      Level level, BlockEntityType<? extends BlockEntity> serverType
   ) {
      return serverType != AlchemancyBlockEntities.ALCHEMANCY_CATALYST.get()
         ? null
         : (level.isClientSide ? AlchemancyCatalystBlockEntity::clientTick : AlchemancyCatalystBlockEntity::serverTick);
   }
}
