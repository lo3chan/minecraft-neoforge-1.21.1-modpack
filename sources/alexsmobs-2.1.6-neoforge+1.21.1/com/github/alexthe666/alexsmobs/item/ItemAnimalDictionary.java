package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityAnacondaPart;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeTail;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class ItemAnimalDictionary extends Item {
   private boolean usedOnEntity = false;

   public ItemAnimalDictionary(Properties properties) {
      super(properties);
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      ItemStack itemStackIn = playerIn.getItemInHand(hand);
      if (playerIn instanceof ServerPlayer serverplayerentity) {
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
         serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
      }

      if (playerIn.level().isClientSide() && target.getEncodeId() != null && target.getEncodeId().contains("alexsmobs:")) {
         this.usedOnEntity = true;
         String id = target.getEncodeId().replace("alexsmobs:", "");
         if (target instanceof EntityBoneSerpent || target instanceof EntityBoneSerpentPart) {
            id = "bone_serpent";
         }

         if (target instanceof EntityCentipedeHead || target instanceof EntityCentipedeBody || target instanceof EntityCentipedeTail) {
            id = "cave_centipede";
         }

         if (target instanceof EntityVoidWorm || target instanceof EntityVoidWormPart) {
            id = "void_worm";
         }

         if (target instanceof EntityAnaconda || target instanceof EntityAnacondaPart) {
            id = "anaconda";
         }

         if (target instanceof EntityMurmur || target instanceof EntityMurmurHead) {
            id = "murmur";
         }

         AlexsMobs.PROXY.openBookGUI(itemStackIn, id);
      }

      return InteractionResult.CONSUME;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemStackIn = playerIn.getItemInHand(handIn);
      if (!this.usedOnEntity) {
         if (playerIn instanceof ServerPlayer serverplayerentity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
            serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
         }

         if (worldIn.isClientSide()) {
            AlexsMobs.PROXY.openBookGUI(itemStackIn);
         }
      }

      this.usedOnEntity = false;
      return AMCompat.pass(itemStackIn);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
      tooltip.add(Component.translatable("item.alexsmobs.animal_dictionary.desc").withStyle(ChatFormatting.GRAY));
   }
}
