package vazkii.psi.common.spell.trick.block;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.ConfigHandler;

public class PieceTrickBreakBlock extends PieceTrick {
   public static final ThreadLocal<Boolean> doingHarvestCheck = ThreadLocal.withInitial(() -> false);
   private static final List<List<ItemStack>> HARVEST_TOOLS_BY_LEVEL = List.of(
      stacks(Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_SHOVEL),
      stacks(Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_SHOVEL),
      stacks(Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SHOVEL),
      stacks(Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_SHOVEL),
      stacks(Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE, Items.NETHERITE_SHOVEL)
   );
   SpellParam<Vector3> position;

   public PieceTrickBreakBlock(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(20.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(50.0));
   }

   public static void removeBlockWithDrops(SpellContext context, Player player, Level world, ItemStack stack, BlockPos pos, Predicate<BlockState> filter) {
      if (stack.isEmpty()) {
         stack = PsiAPI.getPlayerCAD(player);
      }

      if (world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false) != null) {
         BlockState blockstate = world.getBlockState(pos);
         boolean unminable = blockstate.getDestroySpeed(world, pos) == -1.0F;
         if (!world.isClientSide && !unminable && filter.test(blockstate) && !blockstate.isAir()) {
            ItemStack save = player.getMainHandItem();
            boolean wasChecking = doingHarvestCheck.get();
            doingHarvestCheck.set(true);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            ServerPlayer serverPlayer = (ServerPlayer)player;
            boolean did = serverPlayer.gameMode.destroyBlock(pos);
            if (did) {
               serverPlayer.connection.send(new ClientboundLevelEventPacket(2001, pos, Block.getId(blockstate), false));
            }

            doingHarvestCheck.set(wasChecking);
            player.setItemInHand(InteractionHand.MAIN_HAND, save);
         }
      }
   }

   public static BreakEvent createBreakEvent(BlockState state, Player player, Level world, BlockPos pos) {
      return new BreakEvent(world, pos, state, player);
   }

   public static boolean canHarvestBlock(BlockState state, Player player, Level world, BlockPos pos, ItemStack stack) {
      boolean wasChecking = doingHarvestCheck.get();
      doingHarvestCheck.set(true);
      ItemStack oldHeldStack = player.getMainHandItem();
      player.getInventory().items.set(player.getInventory().selected, stack);
      boolean canHarvest = state.canHarvestBlock(world, pos, player);
      player.getInventory().items.set(player.getInventory().selected, oldHeldStack);
      doingHarvestCheck.set(wasChecking);
      return canHarvest;
   }

   private static List<ItemStack> stacks(Item... items) {
      return Stream.of(items).<ItemStack>map(ItemStack::new).collect(Collectors.toList());
   }

   public static boolean canHarvest(int harvestLevel, BlockState state) {
      return !getTool(harvestLevel, state).isEmpty();
   }

   private static ItemStack getTool(int harvestLevel, BlockState state) {
      if (!state.requiresCorrectToolForDrops()) {
         return (ItemStack)((List)HARVEST_TOOLS_BY_LEVEL.getFirst()).getFirst();
      } else {
         int idx = Math.min(harvestLevel, HARVEST_TOOLS_BY_LEVEL.size() - 1);

         for (ItemStack tool : HARVEST_TOOLS_BY_LEVEL.get(idx)) {
            if (tool.isCorrectToolForDrops(state)) {
               return tool;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public static int getHarvestLevel(BlockState state) {
      if (Items.AIR.isCorrectToolForDrops(Items.AIR.getDefaultInstance(), state)) {
         return 0;
      } else {
         for (int i = 0; i < HARVEST_TOOLS_BY_LEVEL.size(); i++) {
            for (ItemStack tool : HARVEST_TOOLS_BY_LEVEL.get(i)) {
               if (tool.isCorrectToolForDrops(state)) {
                  return i + 1;
               }
            }
         }

         return HARVEST_TOOLS_BY_LEVEL.size() + 1;
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 20);
      meta.addStat(EnumSpellStat.COST, 50);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      ItemStack tool = context.getHarvestTool();
      Vector3 positionVal = this.getParamValue(context, this.position);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         BlockPos pos = positionVal.toBlockPos();
         removeBlockWithDrops(
            context,
            context.caster,
            context.focalPoint.getCommandSenderWorld(),
            tool,
            pos,
            v -> tool.isCorrectToolForDrops(v) || canHarvest((Integer)ConfigHandler.COMMON.cadHarvestLevel.get(), v)
         );
         return null;
      }
   }
}
