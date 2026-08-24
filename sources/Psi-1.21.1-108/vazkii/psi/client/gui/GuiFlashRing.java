package vazkii.psi.client.gui;

import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.common.item.ItemSpellDrive;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageFlashRingSync;
import vazkii.psi.common.spell.SpellCompiler;

@OnlyIn(Dist.CLIENT)
public class GuiFlashRing extends GuiProgrammer {
   public GuiFlashRing(ItemStack stack) {
      super(null, ItemSpellDrive.getSpell(stack));
   }

   @Override
   public void onSpellChanged(boolean nameOnly) {
      this.spell.uuid = UUID.randomUUID();
      MessageRegister.sendToServer(new MessageFlashRingSync(this.spell));
      this.onSelectedChanged();
      this.spellNameField.setFocused(nameOnly);
      if (!nameOnly
         || this.compileResult.right().isPresent()
            && ((SpellCompilationException)this.compileResult.right().get()).getMessage().equals("psi.spellerror.noname")
         || this.spell.name.isEmpty()) {
         this.compileResult = new SpellCompiler().compile(this.spell);
      }
   }
}
