package at.petrak.hexcasting.common.items.storage;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.gui.PatternTooltipComponent;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import at.petrak.hexcasting.common.misc.PatternTooltip;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ItemScroll extends Item implements IotaHolderItem {
   public static final String TAG_OP_ID = "op_id";
   public static final String TAG_PATTERN = "pattern";
   public static final ResourceLocation ANCIENT_PREDICATE = HexAPI.modLoc("ancient");
   public final int blockSize;

   public ItemScroll(Properties pProperties, int blockSize) {
      super(pProperties);
      this.blockSize = blockSize;
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag(ItemStack stack) {
      CompoundTag pattern = NBTHelper.getCompound(stack, "pattern");
      if (pattern == null) {
         return null;
      } else {
         CompoundTag out = new CompoundTag();
         out.putString("hexcasting:type", "hexcasting:pattern");
         out.put("hexcasting:data", pattern);
         return out;
      }
   }

   @Override
   public boolean writeable(ItemStack stack) {
      return true;
   }

   @Override
   public boolean canWrite(ItemStack stack, Iota datum) {
      return datum instanceof PatternIota || datum == null;
   }

   @Override
   public void writeDatum(ItemStack stack, Iota datum) {
      if (this.canWrite(stack, datum)) {
         if (datum instanceof PatternIota pat) {
            NBTHelper.putCompound(stack, "pattern", pat.getPattern().serializeToNBT());
         } else if (datum == null) {
            NBTHelper.remove(stack, "pattern");
         }
      }
   }

   public InteractionResult useOn(UseOnContext ctx) {
      BlockPos posClicked = ctx.getClickedPos();
      Direction direction = ctx.getClickedFace();
      BlockPos posInFront = posClicked.relative(direction);
      Player player = ctx.getPlayer();
      ItemStack itemstack = ctx.getItemInHand();
      if (player != null && !this.mayPlace(player, direction, itemstack, posInFront)) {
         return InteractionResult.FAIL;
      } else {
         Level level = ctx.getLevel();
         ItemStack scrollStack = itemstack.copy();
         scrollStack.setCount(1);
         EntityWallScroll scrollEntity = new EntityWallScroll(level, posInFront, direction, scrollStack, false, this.blockSize);
         CustomData stackTag = (CustomData)itemstack.get(DataComponents.CUSTOM_DATA);
         if (stackTag != null) {
            EntityType.updateCustomEntityTag(level, player, scrollEntity, stackTag);
         }

         if (scrollEntity.survives()) {
            if (!level.isClientSide) {
               scrollEntity.playPlacementSound();
               level.gameEvent(player, GameEvent.ENTITY_PLACE, posClicked);
               level.addFreshEntity(scrollEntity);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.CONSUME;
         }
      }
   }

   protected boolean mayPlace(Player pPlayer, Direction pDirection, ItemStack pHangingEntityStack, BlockPos pPos) {
      return !pDirection.getAxis().isVertical() && pPlayer.mayUseItemAt(pPos, pDirection, pHangingEntityStack);
   }

   public Component getName(ItemStack pStack) {
      String descID = this.getDescriptionId(pStack);
      String ancientId = NBTHelper.getString(pStack, "op_id");
      if (ancientId != null) {
         return Component.translatable(descID + ".of", new Object[]{Component.translatable("hexcasting.action." + ResourceLocation.tryParse(ancientId))});
      } else {
         return NBTHelper.hasCompound(pStack, "pattern") ? Component.translatable(descID) : Component.translatable(descID + ".empty");
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
      CompoundTag compound = NBTHelper.getCompound(stack, "pattern");
      if (compound != null) {
         HexPattern pattern = HexPattern.fromNBT(compound);
         return Optional.of(
            new PatternTooltip(pattern, NBTHelper.hasString(stack, "op_id") ? PatternTooltipComponent.ANCIENT_BG : PatternTooltipComponent.PRISTINE_BG)
         );
      } else {
         return Optional.empty();
      }
   }
}
