package vazkii.psi.common.spell.trick.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbySmeltables;

public class PieceTrickSmeltItem extends PieceTrick {
   SpellParam<Entity> target;

   public PieceTrickSmeltItem(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(80.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(240.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 80);
      meta.addStat(EnumSpellStat.COST, 240);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity targetVal = this.getParamValue(context, this.target);
      if (targetVal instanceof ItemEntity eitem && targetVal.isAlive()) {
         ItemStack stack = eitem.getItem();
         ItemStack result = PieceSelectorNearbySmeltables.simulateSmelt(eitem.getCommandSenderWorld(), stack);
         if (!result.isEmpty()) {
            stack.shrink(1);
            eitem.setItem(stack);
            if (stack.getCount() == 0) {
               eitem.remove(RemovalReason.DISCARDED);
            }

            ItemEntity item = new ItemEntity(context.focalPoint.getCommandSenderWorld(), eitem.getX(), eitem.getY(), eitem.getZ(), result.copy());
            context.focalPoint.getCommandSenderWorld().addFreshEntity(item);
         }

         return null;
      } else {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      }
   }
}
