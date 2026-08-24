package vazkii.psi.common.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.base.ModDataComponents;

public class ItemFlashRing extends Item {
   public ItemFlashRing(Properties properties) {
      super(properties.stacksTo(1));
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
      if (!ISpellAcceptor.hasSpell(stack)) {
         return super.getName(stack);
      } else {
         Spell cmp = (Spell)stack.getOrDefault(ModDataComponents.SPELL, new Spell());
         String name = cmp.name;
         return (Component)(name.isEmpty() ? super.getName(stack) : Component.literal(name));
      }
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
      TooltipHelper.tooltipIfShift(
         tooltip,
         () -> tooltip.add(Component.translatable("psimisc.bullet_cost", new Object[]{(int)(ISpellAcceptor.acceptor(stack).getCostModifier() * 100.0)}))
      );
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
      ItemStack held = player.getItemInHand(usedHand);
      boolean isSneaking = player.isShiftKeyDown();
      if (isSneaking && level.isClientSide) {
         Psi.proxy.openFlashRingGUI(held);
         return new InteractionResultHolder(InteractionResult.SUCCESS, held);
      } else if (!isSneaking && ISpellAcceptor.hasSpell(held)) {
         PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
         ItemStack cad = data.getCAD();
         if (cad.isEmpty()) {
            return new InteractionResultHolder(InteractionResult.PASS, held);
         } else {
            boolean casted = ItemCAD.cast(level, player, data, held, cad, 100, 25, 0.5F, ctx -> ctx.castFrom = usedHand).isPresent();
            return new InteractionResultHolder(casted ? InteractionResult.SUCCESS : InteractionResult.PASS, held);
         }
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, held);
      }
   }

   public static class SpellAcceptor implements ICapabilityProvider<ItemCapability<?, Void>, Void, ItemFlashRing.SpellAcceptor>, ISpellAcceptor {
      protected final ItemStack stack;

      public SpellAcceptor(ItemStack stack) {
         this.stack = stack;
      }

      public ItemFlashRing.SpellAcceptor getCapability(@NotNull ItemCapability<?, Void> capability, Void facing) {
         return capability == PsiAPI.SPELL_ACCEPTOR_CAPABILITY ? this : null;
      }

      @Override
      public void setSpell(Player player, Spell spell) {
         ItemSpellDrive.setSpell(this.stack, spell);
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
         context.cspell.safeExecute(context);
         return new ArrayList<>();
      }

      @Override
      public boolean loopcastSpell(SpellContext context) {
         return false;
      }

      @Override
      public double getCostModifier() {
         return 2.0;
      }

      @Override
      public boolean requiresSneakForSpellSet() {
         return true;
      }
   }
}
