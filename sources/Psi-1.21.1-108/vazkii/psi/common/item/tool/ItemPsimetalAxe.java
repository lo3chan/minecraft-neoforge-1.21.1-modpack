package vazkii.psi.common.item.tool;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.common.item.base.ModDataComponents;

@EventBusSubscriber(
   modid = "psi"
)
public class ItemPsimetalAxe extends AxeItem implements IPsimetalTool {
   public ItemPsimetalAxe(Properties properties) {
      super(
         PsiAPI.PSIMETAL_TOOL_MATERIAL,
         properties.attributes(AxeItem.createAttributes(PsiAPI.PSIMETAL_TOOL_MATERIAL, 5.0F, -3.0F))
            .component((DataComponentType)ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY)
      );
   }

   @SubscribeEvent
   public static void adjustAttributes(ItemAttributeModifierEvent event) {
      if (event.getItemStack().getItem() instanceof ItemPsimetalAxe && !IPsimetalTool.isEnabled(event.getItemStack())) {
         event.removeAllModifiersFor(Attributes.ATTACK_DAMAGE);
      }
   }

   public boolean mineBlock(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity player) {
      super.mineBlock(itemstack, world, state, pos, player);
      if (!(player instanceof Player)) {
         return false;
      } else {
         this.castOnBlockBreak(itemstack, (Player)player);
         return true;
      }
   }

   @Nullable
   @Override
   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
      return IPsimetalTool.super.initCapabilities(stack, nbt);
   }

   public void setDamage(ItemStack stack, int damage) {
      if (damage > stack.getMaxDamage()) {
         damage = stack.getDamageValue();
      }

      super.setDamage(stack, damage);
   }

   @NotNull
   public String getDescriptionId(@NotNull ItemStack stack) {
      String name = super.getDescriptionId(stack);
      if (!IPsimetalTool.isEnabled(stack)) {
         name = name + ".broken";
      }

      return name;
   }

   public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
      return !IPsimetalTool.isEnabled(stack) ? 1.0F : super.getDestroySpeed(stack, state);
   }

   public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
      IPsimetalTool.regen(stack, entityIn);
   }

   public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
      return slotChanged;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag advanced) {
      Component componentName = ISocketable.getSocketedItemName(stack, "psimisc.none");
      tooltip.add(Component.translatable("psimisc.spell_selected", new Object[]{componentName}));
   }
}
