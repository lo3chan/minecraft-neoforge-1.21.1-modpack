package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemModArmor extends ArmorItem implements IClientExtensionItem {
   private static final ResourceLocation[] ARMOR_MODIFIERS = new ResourceLocation[]{
      AMCompat.rl("alexsmobs", "armor_modifier_0"),
      AMCompat.rl("alexsmobs", "armor_modifier_1"),
      AMCompat.rl("alexsmobs", "armor_modifier_2"),
      AMCompat.rl("alexsmobs", "armor_modifier_3")
   };
   protected final AMArmorMaterial amMaterial;
   protected final Type amType;
   private Multimap<Attribute, AttributeModifier> attributeMapCroc;
   private Multimap<Attribute, AttributeModifier> attributeMapMoose;
   private Multimap<Attribute, AttributeModifier> attributeMapFlyingFish;
   private Multimap<Attribute, AttributeModifier> attributeMapKimono;
   private ItemAttributeModifiers amModifiers;

   public ItemModArmor(AMArmorMaterial armorMaterial, Type slot) {
      super(armorMaterial.holder(), slot, new Properties().durability(slot.getDurability(armorMaterial.getDurability())));
      this.amMaterial = armorMaterial;
      this.amType = slot;
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getArmorRenderProperties());
   }

   public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      if (this.amMaterial == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.centipede_leggings.desc").withStyle(ChatFormatting.GRAY));
      }

      if (this.amMaterial == AMItemRegistry.EMU_ARMOR_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.emu_leggings.desc").withStyle(ChatFormatting.GRAY));
      }

      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (this.amMaterial == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.roadrunner_boots.desc").withStyle(ChatFormatting.BLUE));
      }

      if (this.amMaterial == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.frontier_cap.desc").withStyle(ChatFormatting.BLUE));
      }

      if (this.amMaterial == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.froststalker_helmet.desc").withStyle(ChatFormatting.AQUA));
      }

      if (this.amMaterial == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.rocky_chestplate.desc").withStyle(ChatFormatting.GRAY));
      }

      if (this.amMaterial == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL && AlexsMobs.isAprilFools()) {
         tooltip.add(Component.translatable("item.alexsmobs.sombrero.special_desc").withStyle(ChatFormatting.GRAY));
      }

      if (this.amMaterial == AMItemRegistry.FLYING_FISH_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.flying_fish_boots.desc").withStyle(ChatFormatting.GRAY));
      }

      if (this.amMaterial == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.novelty_hat.desc").withStyle(ChatFormatting.GRAY));
      }

      if (this.amMaterial == AMItemRegistry.KIMONO_MATERIAL) {
         tooltip.add(Component.translatable("item.alexsmobs.unsettling_kimono.desc").withStyle(ChatFormatting.GRAY));
      }
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      if (this.amMaterial != AMItemRegistry.CROCODILE_ARMOR_MATERIAL
         && this.amMaterial != AMItemRegistry.MOOSE_ARMOR_MATERIAL
         && this.amMaterial != AMItemRegistry.FLYING_FISH_MATERIAL
         && this.amMaterial != AMItemRegistry.KIMONO_MATERIAL) {
         return super.getDefaultAttributeModifiers();
      } else {
         if (this.amModifiers == null) {
            this.amModifiers = this.buildAMModifiers();
         }

         return this.amModifiers;
      }
   }

   private ItemAttributeModifiers buildAMModifiers() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.amType.getSlot());
      ResourceLocation uuid = ARMOR_MODIFIERS[this.amType.ordinal()];
      Builder builder = ItemAttributeModifiers.builder();
      builder.add(
         Attributes.ARMOR, AMCompat.attributeModifier(uuid, "Armor modifier", this.amMaterial.getDefenseForType(this.amType), Operation.ADD_VALUE), group
      );
      builder.add(Attributes.ARMOR_TOUGHNESS, AMCompat.attributeModifier(uuid, "Armor toughness", this.amMaterial.getToughness(), Operation.ADD_VALUE), group);
      if (this.amMaterial.knockbackResistance > 0.0F && this.amMaterial != AMItemRegistry.FLYING_FISH_MATERIAL) {
         builder.add(
            Attributes.KNOCKBACK_RESISTANCE,
            AMCompat.attributeModifier(uuid, "Armor knockback resistance", this.amMaterial.knockbackResistance, Operation.ADD_VALUE),
            group
         );
      }

      Holder<Attribute> swimSpeed = AMPlatform.swimSpeed();
      Holder<Attribute> blockReach = AMPlatform.blockReach();
      Holder<Attribute> entityReach = AMPlatform.entityReach();
      if (this.amMaterial == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
         if (swimSpeed != null) {
            builder.add(swimSpeed, AMCompat.attributeModifier(uuid, "Swim speed", 1.0, Operation.ADD_VALUE), group);
         }
      } else if (this.amMaterial == AMItemRegistry.FLYING_FISH_MATERIAL) {
         if (swimSpeed != null) {
            builder.add(swimSpeed, AMCompat.attributeModifier(uuid, "Swim speed", 0.5, Operation.ADD_VALUE), group);
         }
      } else if (this.amMaterial == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
         builder.add(Attributes.ATTACK_KNOCKBACK, AMCompat.attributeModifier(uuid, "Knockback", 2.0, Operation.ADD_VALUE), group);
      } else if (this.amMaterial == AMItemRegistry.KIMONO_MATERIAL) {
         if (blockReach != null) {
            builder.add(blockReach, AMCompat.attributeModifier(uuid, "Block Reach distance", 2.0, Operation.ADD_VALUE), group);
         }

         if (entityReach != null) {
            builder.add(entityReach, AMCompat.attributeModifier(uuid, "Entity Reach distance", 2.0, Operation.ADD_VALUE), group);
         }
      }

      return builder.build();
   }

   @Nullable
   private String armorTexturePath() {
      if (this.amMaterial == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/crocodile_chestplate.png";
      } else if (this.amMaterial == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/roadrunner_boots.png";
      } else if (this.amMaterial == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/centipede_leggings.png";
      } else if (this.amMaterial == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/moose_headgear.png";
      } else if (this.amMaterial == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/frontier_cap.png";
      } else if (this.amMaterial == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/sombrero.png";
      } else if (this.amMaterial == AMItemRegistry.SPIKED_TURTLE_SHELL_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/spiked_turtle_shell.png";
      } else if (this.amMaterial == AMItemRegistry.FEDORA_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/fedora.png";
      } else if (this.amMaterial == AMItemRegistry.EMU_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/emu_leggings.png";
      } else if (this.amMaterial == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/froststalker_helmet.png";
      } else if (this.amMaterial == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
         return "alexsmobs:textures/armor/rocky_chestplate.png";
      } else if (this.amMaterial == AMItemRegistry.FLYING_FISH_MATERIAL) {
         return "alexsmobs:textures/armor/flying_fish_boots.png";
      } else if (this.amMaterial == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
         return "alexsmobs:textures/armor/novelty_hat.png";
      } else {
         return this.amMaterial == AMItemRegistry.KIMONO_MATERIAL ? "alexsmobs:textures/armor/unsettling_kimono.png" : null;
      }
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      String path = this.armorTexturePath();
      return path == null ? super.getArmorTexture(stack, entity, slot, layer, innerModel) : ResourceLocation.tryParse(path);
   }
}
