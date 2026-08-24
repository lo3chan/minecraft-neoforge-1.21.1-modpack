package vazkii.psi.common.item;

import java.util.ArrayList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.LoopcastTrackingHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler;

public class ItemLoopcastSpellBullet extends ItemSpellBullet {
   public ItemLoopcastSpellBullet(Properties properties) {
      super(properties);
   }

   @Override
   public ArrayList<Entity> castSpell(ItemStack stack, SpellContext context) {
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(context.caster);
      if (!data.loopcasting || context.castFrom != data.loopcastHand) {
         data.loopcasting = true;
         data.loopcastHand = context.castFrom;
         data.lastTickLoopcastStack = null;
         data.loopcastTime = 1;
         data.loopcastAmount = 0;
         context.cspell.safeExecute(context);
         if (context.caster instanceof ServerPlayer) {
            LoopcastTrackingHandler.syncForTrackersAndSelf((ServerPlayer)context.caster);
         }
      }

      return new ArrayList<>();
   }

   @Override
   public boolean loopcastSpell(ItemStack stack, SpellContext context) {
      context.cspell.safeExecute(context);
      return true;
   }

   @Override
   public String getBulletType() {
      return "loopcast";
   }

   @Override
   public boolean isCADOnlyContainer(ItemStack stack) {
      return true;
   }
}
