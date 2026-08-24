package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.SpawnStructureMoundHoundsPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;

public class SpawnStructureMoundHoundsItem extends Item {
   public SpawnStructureMoundHoundsItem() {
      super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
   }

   public InteractionResult useOn(UseOnContext context) {
      super.useOn(context);
      SpawnStructureMoundHoundsPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure.execute(
         context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer()
      );
      return InteractionResult.SUCCESS;
   }
}
