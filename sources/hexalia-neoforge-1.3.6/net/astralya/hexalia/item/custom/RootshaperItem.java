package net.astralya.hexalia.item.custom;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RootshaperItem extends ShovelItem {
   public static final int MODE_PICKAXE = 0;
   public static final int MODE_SHOVEL = 1;
   public static final float MINING_SPEED = 9.0F;
   public static final int DAMAGE_PER_BLOCK = 1;
   public static final float ATTACK_DAMAGE_BONUS = 4.0F;
   public static final float ATTACK_SPEED = -2.8F;

   public RootshaperItem(Tier tier, Properties properties) {
      super(new RootshaperItem.RootshaperTier(tier), properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
      tooltipComponents.add(Component.translatable("tooltip.hexalia.rootshaper.mode_3x3_hint").withStyle(ChatFormatting.GRAY));
   }

   public static Tool createTool(Tier tier, int mode) {
      TagKey<Block> mineableBlocks = mode == 1 ? BlockTags.MINEABLE_WITH_SHOVEL : BlockTags.MINEABLE_WITH_PICKAXE;
      return new Tool(List.of(Rule.deniesDrops(tier.getIncorrectBlocksForDrops()), Rule.minesAndDrops(mineableBlocks, 9.0F)), 1.0F, 1);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.799999952316284, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   public static int computeMode(BlockState state) {
      if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
         return 0;
      } else {
         return state.is(BlockTags.MINEABLE_WITH_SHOVEL) ? 1 : 0;
      }
   }

   public float getDestroySpeed(ItemStack stack, BlockState state) {
      int mode = getMode(stack);
      if (mode == 0 && state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
         return 9.0F;
      } else {
         return mode == 1 && state.is(BlockTags.MINEABLE_WITH_SHOVEL) ? 9.0F : 1.0F;
      }
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      int mode = getMode(stack);
      if (mode == 0) {
         return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
      } else {
         return mode == 1 ? state.is(BlockTags.MINEABLE_WITH_SHOVEL) : false;
      }
   }

   public static int getMode(ItemStack stack) {
      CustomModelData customModelData = (CustomModelData)stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT);
      return customModelData.value();
   }

   public static void setMode(ItemStack stack, int mode) {
      stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(mode));
      updateToolComponent(stack, mode);
   }

   public static void playMorphSound(Level level, BlockPos pos) {
      level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.6F, 1.2F);
   }

   private static void updateToolComponent(ItemStack stack, int mode) {
      if (stack.getItem() instanceof TieredItem tieredItem) {
         stack.set(DataComponents.TOOL, createTool(tieredItem.getTier(), mode));
      }
   }

   private record RootshaperTier(Tier delegate) implements Tier {
      public int getUses() {
         return this.delegate.getUses();
      }

      public float getSpeed() {
         return this.delegate.getSpeed();
      }

      public float getAttackDamageBonus() {
         return this.delegate.getAttackDamageBonus();
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return this.delegate.getIncorrectBlocksForDrops();
      }

      public int getEnchantmentValue() {
         return this.delegate.getEnchantmentValue();
      }

      public Ingredient getRepairIngredient() {
         return this.delegate.getRepairIngredient();
      }

      public Tool createToolProperties(TagKey<Block> blockTag) {
         return RootshaperItem.createTool(this.delegate, 0);
      }
   }
}
