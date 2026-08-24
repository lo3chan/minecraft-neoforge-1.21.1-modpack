package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
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
import vazkii.psi.client.core.handler.HUDHandler;
import vazkii.psi.common.block.BlockConjured;
import vazkii.psi.common.block.base.DirectionBlockItemUseContext;
import vazkii.psi.common.block.base.ModBlocks;

public class PieceTrickPlaceBlock extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> direction;

   public PieceTrickPlaceBlock(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(8.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(8.0));
   }

   public static void placeBlock(Player player, Level world, BlockPos pos, int slot, boolean particles, Direction direction, Direction horizontalDirection) {
      placeBlock(player, world, pos, slot, particles, false, direction, horizontalDirection);
   }

   public static void placeBlock(
      Player player, Level world, BlockPos pos, int slot, boolean particles, boolean conjure, Direction direction, Direction horizontalDirection
   ) {
      if (world.hasChunkAt(pos) && world.mayInteract(player, pos)) {
         BlockState state = world.getBlockState(pos);
         EntityPlaceEvent placeEvent = new EntityPlaceEvent(
            BlockSnapshot.create(world.dimension(), world, pos), world.getBlockState(pos.relative(Direction.UP)), player
         );
         NeoForge.EVENT_BUS.post(placeEvent);
         if (state.isAir() || state.canBeReplaced() && !placeEvent.isCanceled()) {
            if (conjure) {
               world.setBlockAndUpdate(pos, ((BlockConjured)ModBlocks.conjured.get()).defaultBlockState());
            } else {
               ItemStack stack = player.getInventory().getItem(slot);
               if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                  ItemStack rem = removeFromInventory(player, stack, true);
                  BlockItem iblock = (BlockItem)rem.getItem();
                  BlockHitResult hit = new BlockHitResult(Vec3.ZERO, direction, pos, false);
                  UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
                  ItemStack save = player.getItemInHand(ctx.getHand());
                  player.setItemInHand(ctx.getHand(), rem);
                  UseOnContext newCtx = new UseOnContext(player, ctx.getHand(), hit);
                  player.setItemInHand(newCtx.getHand(), save);
                  InteractionResult result = iblock.place(new DirectionBlockItemUseContext(newCtx, horizontalDirection));
                  if (result != InteractionResult.FAIL) {
                     removeFromInventory(player, stack, false);
                     if (world.isClientSide()) {
                        if (player.isCreative()) {
                           HUDHandler.setRemaining(rem, -1);
                        } else {
                           HUDHandler.setRemaining(player, rem, null);
                        }
                     }
                  }
               }
            }

            if (particles) {
               world.levelEvent(2001, pos, Block.getId(world.getBlockState(pos)));
            }
         }
      }
   }

   public static ItemStack removeFromInventory(Player player, ItemStack stack, boolean copy) {
      if (player.isCreative()) {
         return stack.copy();
      } else {
         Inventory inv = player.getInventory();

         for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
            ItemStack invStack = inv.getItem(i);
            if (!invStack.isEmpty() && ItemStack.isSameItem(invStack, stack) && ItemStack.matches(stack, invStack)) {
               ItemStack retStack = invStack.copy();
               if (!copy) {
                  invStack.shrink(1);
                  if (invStack.getCount() == 0) {
                     inv.setItem(i, ItemStack.EMPTY);
                  }
               }

               return retStack;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.direction = new ParamVector("psi.spellparam.direction", 4117034, true, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 8);
      meta.addStat(EnumSpellStat.COST, 8);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      Vector3 directionVal = this.getParamValue(context, this.direction);
      Direction facing = Direction.NORTH;
      Direction horizontalFacing = Direction.NORTH;
      if (directionVal != null) {
         facing = Direction.getNearest(directionVal.x, directionVal.y, directionVal.z);
         horizontalFacing = Direction.getNearest(directionVal.x, 0.0, directionVal.z);
      }

      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         BlockPos pos = positionVal.toBlockPos();
         placeBlock(context.caster, context.focalPoint.getCommandSenderWorld(), pos, context.getTargetSlot(), false, facing, horizontalFacing);
         return null;
      }
   }
}
