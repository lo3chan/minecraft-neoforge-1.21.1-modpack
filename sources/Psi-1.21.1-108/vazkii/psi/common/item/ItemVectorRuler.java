package vazkii.psi.common.item;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.common.item.base.IHUDItem;
import vazkii.psi.common.item.base.ModDataComponents;

public class ItemVectorRuler extends Item implements IHUDItem {
   public ItemVectorRuler(Properties properties) {
      super(properties.stacksTo(1));
   }

   public static Vector3 getRulerVector(Player player) {
      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (!stack.isEmpty() && stack.getItem() instanceof ItemVectorRuler) {
            return ((ItemVectorRuler)stack.getItem()).getVector(stack);
         }
      }

      return Vector3.zero;
   }

   @NotNull
   public InteractionResult useOn(UseOnContext ctx) {
      BlockPos pos = ctx.getClickedPos();
      if (ctx.getPlayer() == null) {
         return InteractionResult.FAIL;
      } else {
         ItemStack stack = ctx.getPlayer().getItemInHand(ctx.getHand());
         if (stack.has(ModDataComponents.SRC_POS) && !ctx.getPlayer().isShiftKeyDown()) {
            stack.set(ModDataComponents.DST_POS, pos);
         } else {
            stack.set(ModDataComponents.SRC_POS, pos);
            stack.remove(ModDataComponents.DST_POS);
         }

         return InteractionResult.SUCCESS;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag advanced) {
      tooltip.add(Component.literal(this.getVector(stack).toString()));
   }

   public Vector3 getVector(ItemStack stack) {
      BlockPos src = (BlockPos)stack.getOrDefault(ModDataComponents.SRC_POS, BlockPos.ZERO);
      if (!stack.has(ModDataComponents.DST_POS)) {
         return Vector3.fromBlockPos(src);
      } else {
         BlockPos dst = (BlockPos)stack.getOrDefault(ModDataComponents.DST_POS, BlockPos.ZERO);
         return Vector3.fromBlockPos(dst.subtract(src));
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void drawHUD(GuiGraphics graphics, float partTicks, int screenWidth, int screenHeight, ItemStack stack) {
      String s = this.getVector(stack).toString();
      Font font = Minecraft.getInstance().font;
      int w = font.width(s);
      graphics.drawString(font, s, screenWidth / 2.0F - w / 2.0F, screenHeight / 2.0F + 10.0F, -1, false);
   }
}
