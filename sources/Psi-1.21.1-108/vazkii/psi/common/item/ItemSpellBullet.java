package vazkii.psi.common.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.base.ModDataComponents;

public class ItemSpellBullet extends Item {
   public ItemSpellBullet(Properties properties) {
      super(properties.stacksTo(16).rarity(Rarity.COMMON));
   }

   public void verifyComponentsAfterLoad(ItemStack pStack) {
      if (pStack.has(DataComponents.CUSTOM_DATA)) {
         CustomData patch = (CustomData)pStack.get(DataComponents.CUSTOM_DATA);
         if (patch == null) {
            return;
         }

         CompoundTag compound = patch.copyTag();
         if (compound.contains("spell")) {
            pStack.set(DataComponents.RARITY, Rarity.RARE);
            Spell spell = Spell.createFromNBT(compound.getCompound("spell"));
            pStack.set(ModDataComponents.SPELL, spell);
            compound.remove("spell");
         } else {
            pStack.set(DataComponents.RARITY, Rarity.COMMON);
         }

         CustomData.set(DataComponents.CUSTOM_DATA, pStack, compound);
      }
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      if (ISpellAcceptor.hasSpell(stack)) {
         Spell cmp = (Spell)stack.getOrDefault(ModDataComponents.SPELL, new Spell());
         String name = cmp.name;
         return (Component)(name.isEmpty() ? super.getName(stack) : Component.literal(name));
      } else {
         return super.getName(stack);
      }
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
      TooltipHelper.tooltipIfShift(tooltip, () -> {
         tooltip.add(Component.translatable("psimisc.bullet_type", new Object[]{Component.translatable("psi.bullet_type_" + this.getBulletType())}));
         tooltip.add(Component.translatable("psimisc.bullet_cost", new Object[]{(int)(ISpellAcceptor.acceptor(stack).getCostModifier() * 100.0)}));
      });
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
      ItemStack itemStackIn = playerIn.getItemInHand(hand);
      if (ItemSpellDrive.getSpell(itemStackIn) != null && playerIn.isShiftKeyDown()) {
         if (!worldIn.isClientSide) {
            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), PsiSoundHandler.compileError, SoundSource.PLAYERS, 0.5F, 1.0F);
         } else {
            playerIn.swing(hand);
         }

         ItemSpellDrive.setSpell(itemStackIn, null);
         return new InteractionResultHolder(InteractionResult.SUCCESS, itemStackIn);
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, itemStackIn);
      }
   }

   public String getBulletType() {
      return "basic";
   }

   public ArrayList<Entity> castSpell(ItemStack stack, SpellContext context) {
      context.cspell.safeExecute(context);
      return new ArrayList<>();
   }

   public boolean loopcastSpell(ItemStack stack, SpellContext context) {
      this.castSpell(stack, context);
      return false;
   }

   public double getCostModifier(ItemStack stack) {
      return 1.0;
   }

   public boolean isCADOnlyContainer(ItemStack stack) {
      return false;
   }

   public static class SpellAcceptor implements ICapabilityProvider<ItemCapability<?, Void>, Void, ItemSpellBullet.SpellAcceptor>, ISpellAcceptor {
      protected final ItemStack stack;

      public SpellAcceptor(ItemStack stack) {
         this.stack = stack;
      }

      private ItemSpellBullet bulletItem() {
         return (ItemSpellBullet)this.stack.getItem();
      }

      public ItemSpellBullet.SpellAcceptor getCapability(@NotNull ItemCapability<?, Void> capability, Void facing) {
         return capability == PsiAPI.SPELL_ACCEPTOR_CAPABILITY ? this : null;
      }

      @Override
      public void setSpell(Player player, Spell spell) {
         if (this.stack.getCount() == 1) {
            ItemSpellDrive.setSpell(this.stack, spell);
         } else {
            this.stack.shrink(1);
            ItemStack newStack = this.stack.copy();
            newStack.setCount(1);
            ItemSpellDrive.setSpell(newStack, spell);
            if (!player.addItem(newStack)) {
               player.drop(newStack, false);
            }
         }
      }

      @Override
      public Spell getSpell() {
         return ItemSpellDrive.getSpell(this.stack);
      }

      @Override
      public boolean containsSpell() {
         return this.stack.has(ModDataComponents.SPELL);
      }

      @Override
      public ArrayList<Entity> castSpell(SpellContext context) {
         return this.bulletItem().castSpell(this.stack, context);
      }

      @Override
      public boolean loopcastSpell(SpellContext context) {
         return this.bulletItem().loopcastSpell(this.stack, context);
      }

      @Override
      public double getCostModifier() {
         return this.bulletItem().getCostModifier(this.stack);
      }

      @Override
      public boolean castableFromSocket() {
         return true;
      }

      @Override
      public boolean isCADOnlyContainer() {
         return this.bulletItem().isCADOnlyContainer(this.stack);
      }
   }
}
