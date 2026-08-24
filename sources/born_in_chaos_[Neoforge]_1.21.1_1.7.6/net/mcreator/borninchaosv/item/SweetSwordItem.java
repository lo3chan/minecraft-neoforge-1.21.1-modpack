package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.SweetSwordDopolnitielnaiaInformatsiiaProcedure;
import net.mcreator.borninchaosv.procedures.SweetSwordPriPoluchieniiPriedmietaPoRietsieptuProcedure;
import net.mcreator.borninchaosv.procedures.SweetSwordPriShchielchkiePKMProcedure;
import net.mcreator.borninchaosv.procedures.SweetSwordPriUdariePoSushchnostiInstrumientomProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SweetSwordItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 600;
      }

      public float getSpeed() {
         return 9.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
      }

      public int getEnchantmentValue() {
         return 26;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(
            new ItemStack[]{
               new ItemStack((ItemLike)BornInChaosV1ModItems.HOLIDAY_CANDY.get()),
               new ItemStack((ItemLike)BornInChaosV1ModItems.MAGICAL_HOLIDAY_CANDY.get()),
               new ItemStack((ItemLike)BornInChaosV1ModItems.CARAMEL_PEPPER.get()),
               new ItemStack((ItemLike)BornInChaosV1ModItems.GUMMY_VAMPIRE_TEETH.get()),
               new ItemStack((ItemLike)BornInChaosV1ModItems.CHOCOLATE_HEART.get()),
               new ItemStack((ItemLike)BornInChaosV1ModItems.COFFEE_CANDY.get()),
               new ItemStack(Items.SUGAR),
               new ItemStack((ItemLike)BornInChaosV1ModBlocks.CULTIVATED_PUMPKIN.get()),
               new ItemStack((ItemLike)BornInChaosV1ModItems.MINT_CANDY.get())
            }
         );
      }
   };

   public SweetSwordItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 5.5F, -2.4F)).fireResistant());
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      SweetSwordPriUdariePoSushchnostiInstrumientomProcedure.execute(entity.level(), entity);
      return retval;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      SweetSwordPriShchielchkiePKMProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, (ItemStack)ar.getObject());
      return ar;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      if (itemstack.getEntityRepresentation() != null) {
         itemstack.getEntityRepresentation();
      }

      String hoverText = SweetSwordDopolnitielnaiaInformatsiiaProcedure.execute();
      if (hoverText != null) {
         for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
         }
      }
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      SweetSwordPriPoluchieniiPriedmietaPoRietsieptuProcedure.execute(entity);
   }
}
