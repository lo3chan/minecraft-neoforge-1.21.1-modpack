package vazkii.psi.api.spell;

import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;

public interface ISpellAcceptor {
   static boolean isAcceptor(ItemStack stack) {
      return !stack.isEmpty() && Objects.nonNull(stack.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY));
   }

   static boolean isContainer(ItemStack stack) {
      ISpellAcceptor capability = (ISpellAcceptor)stack.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY);
      return capability == null ? false : capability.castableFromSocket();
   }

   static boolean hasSpell(ItemStack stack) {
      ISpellAcceptor capability = (ISpellAcceptor)stack.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY);
      return capability == null ? false : capability.containsSpell();
   }

   static ISpellAcceptor acceptor(ItemStack stack) {
      return Objects.requireNonNull((ISpellAcceptor)stack.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY));
   }

   void setSpell(Player var1, Spell var2);

   default boolean castableFromSocket() {
      return false;
   }

   @Nullable
   default Spell getSpell() {
      return null;
   }

   default boolean containsSpell() {
      return false;
   }

   default ArrayList<Entity> castSpell(SpellContext context) {
      return null;
   }

   default boolean loopcastSpell(SpellContext context) {
      this.castSpell(context);
      return false;
   }

   default double getCostModifier() {
      return 1.0;
   }

   default boolean isCADOnlyContainer() {
      return false;
   }

   default boolean requiresSneakForSpellSet() {
      return false;
   }
}
