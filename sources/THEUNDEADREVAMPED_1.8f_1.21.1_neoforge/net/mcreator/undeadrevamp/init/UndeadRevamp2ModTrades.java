package net.mcreator.undeadrevamp.init;

import java.util.List;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

@EventBusSubscriber
public class UndeadRevamp2ModTrades {
   @SubscribeEvent
   public static void registerWanderingTrades(WandererTradesEvent event) {
      event.getGenericTrades()
         .add(
            new BasicItemListing(
               new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get()),
               new ItemStack(Blocks.EMERALD_BLOCK),
               new ItemStack((ItemLike)UndeadRevamp2ModBlocks.WOODENNEST.get()),
               10,
               10,
               0.05F
            )
         );
   }

   @SubscribeEvent
   public static void registerTrades(VillagerTradesEvent event) {
      if (event.getType() == VillagerProfession.ARMORER) {
         ((List)event.getTrades().get(4))
            .add(new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModItems.RAWBOSTROX.get(), 5), new ItemStack(Items.EMERALD, 5), 5, 7, 0.05F));
      }

      if (event.getType() == VillagerProfession.WEAPONSMITH) {
         ((List)event.getTrades().get(3))
            .add(new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModItems.RAWBOSTROX.get(), 5), new ItemStack(Items.EMERALD, 5), 5, 5, 0.05F));
      }

      if (event.getType() == VillagerProfession.CLERIC) {
         ((List)event.getTrades().get(1))
            .add(new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModBlocks.ARAPHOLIA.get(), 8), new ItemStack(Items.EMERALD, 4), 5, 4, 0.04F));
         ((List)event.getTrades().get(2))
            .add(new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModItems.ACIDSACK.get()), new ItemStack(Items.EMERALD, 5), 3, 5, 0.05F));
         ((List)event.getTrades().get(3))
            .add(
               new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get()), new ItemStack(Items.EMERALD, 3), 10, 5, 0.05F)
            );
      }

      if (event.getType() == VillagerProfession.BUTCHER) {
         ((List)event.getTrades().get(3))
            .add(new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModItems.CLOOGERRIBS.get(), 3), new ItemStack(Items.EMERALD, 6), 10, 4, 0.05F));
      }

      if (event.getType() == VillagerProfession.ARMORER) {
         ((List)event.getTrades().get(5))
            .add(
               new BasicItemListing(
                  new ItemStack(Items.EMERALD, 20),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get(), 3),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_HELMET.get()),
                  2,
                  5,
                  0.05F
               )
            );
      }

      if (event.getType() == VillagerProfession.ARMORER) {
         ((List)event.getTrades().get(5))
            .add(
               new BasicItemListing(
                  new ItemStack(Items.EMERALD, 20),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get(), 2),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_BOOTS.get()),
                  2,
                  5,
                  0.05F
               )
            );
         ((List)event.getTrades().get(5))
            .add(
               new BasicItemListing(
                  new ItemStack(Items.EMERALD, 20),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get(), 5),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_CHESTPLATE.get()),
                  2,
                  5,
                  0.05F
               )
            );
      }

      if (event.getType() == VillagerProfession.CLERIC) {
         ((List)event.getTrades().get(4))
            .add(new BasicItemListing(new ItemStack((ItemLike)UndeadRevamp2ModItems.BEESPHEROMONES.get()), new ItemStack(Items.EMERALD, 25), 3, 5, 0.05F));
      }

      if (event.getType() == VillagerProfession.ARMORER) {
         ((List)event.getTrades().get(4))
            .add(
               new BasicItemListing(
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.CLOOGERRIBS.get()),
                  new ItemStack(Blocks.EMERALD_BLOCK, 4),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.CLOGGERUPGRADE.get()),
                  2,
                  5,
                  0.05F
               )
            );
      }

      if (event.getType() == VillagerProfession.ARMORER) {
         ((List)event.getTrades().get(3))
            .add(
               new BasicItemListing(
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get()),
                  new ItemStack(Blocks.EMERALD_BLOCK),
                  new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYUPGRADE.get()),
                  2,
                  5,
                  0.05F
               )
            );
      }
   }
}
