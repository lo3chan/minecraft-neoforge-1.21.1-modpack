package net.mcreator.undeadrevamp.item;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.mcreator.undeadrevamp.client.model.Modelcloggercrown;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.procedures.CloggerarmorHelmetTickEventProcedure;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public abstract class CloggerarmorItem extends ArmorItem {
   public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

   @SubscribeEvent
   public static void registerArmorMaterial(RegisterEvent event) {
      event.register(
         Registries.ARMOR_MATERIAL,
         registerHelper -> {
            ArmorMaterial armorMaterial = new ArmorMaterial(
               (Map)Util.make(new EnumMap(Type.class), map -> {
                  map.put(Type.BOOTS, 3);
                  map.put(Type.LEGGINGS, 6);
                  map.put(Type.CHESTPLATE, 8);
                  map.put(Type.HELMET, 3);
                  map.put(Type.BODY, 8);
               }),
               30,
               DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_chain")),
               () -> Ingredient.of(new ItemStack[]{new ItemStack(Blocks.GOLD_BLOCK)}),
               List.of(new Layer(ResourceLocation.parse("undead_revamp2:bostrox"))),
               3.2F,
               0.0F
            );
            registerHelper.register(ResourceLocation.parse("undead_revamp2:cloggerarmor"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
         }
      );
   }

   @SubscribeEvent
   public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(
         new IClientItemExtensions() {
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
               HumanoidModel armorModel = new HumanoidModel(
                  new ModelPart(
                     Collections.emptyList(),
                     Map.of(
                        "head",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).helmet,
                        "hat",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "body",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "right_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "left_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "right_leg",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "left_leg",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap())
                     )
                  )
               );
               armorModel.crouching = living.isShiftKeyDown();
               armorModel.riding = defaultModel.riding;
               armorModel.young = living.isBaby();
               return armorModel;
            }
         },
         new Item[]{(Item)UndeadRevamp2ModItems.CLOGGERARMOR_HELMET.get()}
      );
      event.registerItem(
         new IClientItemExtensions() {
            @OnlyIn(Dist.CLIENT)
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
               HumanoidModel armorModel = new HumanoidModel(
                  new ModelPart(
                     Collections.emptyList(),
                     Map.of(
                        "body",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).chestar,
                        "left_arm",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).rightarmar,
                        "right_arm",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).leftarmar,
                        "head",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "hat",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "right_leg",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "left_leg",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap())
                     )
                  )
               );
               armorModel.crouching = living.isShiftKeyDown();
               armorModel.riding = defaultModel.riding;
               armorModel.young = living.isBaby();
               return armorModel;
            }
         },
         new Item[]{(Item)UndeadRevamp2ModItems.CLOGGERARMOR_CHESTPLATE.get()}
      );
      event.registerItem(
         new IClientItemExtensions() {
            @OnlyIn(Dist.CLIENT)
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
               HumanoidModel armorModel = new HumanoidModel(
                  new ModelPart(
                     Collections.emptyList(),
                     Map.of(
                        "left_leg",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).rightlegma,
                        "right_leg",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).leftlegma,
                        "head",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "hat",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "body",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "right_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "left_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap())
                     )
                  )
               );
               armorModel.crouching = living.isShiftKeyDown();
               armorModel.riding = defaultModel.riding;
               armorModel.young = living.isBaby();
               return armorModel;
            }
         },
         new Item[]{(Item)UndeadRevamp2ModItems.CLOGGERARMOR_LEGGINGS.get()}
      );
      event.registerItem(
         new IClientItemExtensions() {
            @OnlyIn(Dist.CLIENT)
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
               HumanoidModel armorModel = new HumanoidModel(
                  new ModelPart(
                     Collections.emptyList(),
                     Map.of(
                        "left_leg",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).rightlegma2,
                        "right_leg",
                        (new Modelcloggercrown(Minecraft.getInstance().getEntityModels().bakeLayer(Modelcloggercrown.LAYER_LOCATION))).leftlegma2,
                        "head",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "hat",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "body",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "right_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "left_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap())
                     )
                  )
               );
               armorModel.crouching = living.isShiftKeyDown();
               armorModel.riding = defaultModel.riding;
               armorModel.young = living.isBaby();
               return armorModel;
            }
         },
         new Item[]{(Item)UndeadRevamp2ModItems.CLOGGERARMOR_BOOTS.get()}
      );
   }

   public CloggerarmorItem(Type type, Properties properties) {
      super(ARMOR_MATERIAL, type, properties);
   }

   public static class Boots extends CloggerarmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(33)));
      }

      public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
         return ResourceLocation.parse("undead_revamp2:textures/entities/cloggerarmor.png");
      }

      @OnlyIn(Dist.CLIENT)
      public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
         super.appendHoverText(itemstack, context, list, flag);
         list.add(Component.translatable("item.undead_revamp2.cloggerarmor_boots.description_0"));
      }

      public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
         return true;
      }

      public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
         super.inventoryTick(itemstack, world, entity, slot, selected);
         if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
            CloggerarmorHelmetTickEventProcedure.execute(entity);
         }
      }
   }

   public static class Chestplate extends CloggerarmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(33)));
      }

      public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
         return ResourceLocation.parse("undead_revamp2:textures/entities/cloggerarmor.png");
      }

      @OnlyIn(Dist.CLIENT)
      public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
         super.appendHoverText(itemstack, context, list, flag);
         list.add(Component.translatable("item.undead_revamp2.cloggerarmor_chestplate.description_0"));
      }

      public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
         return true;
      }

      public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
         super.inventoryTick(itemstack, world, entity, slot, selected);
         if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
            CloggerarmorHelmetTickEventProcedure.execute(entity);
         }
      }
   }

   public static class Helmet extends CloggerarmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties().durability(Type.HELMET.getDurability(33)));
      }

      public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
         return ResourceLocation.parse("undead_revamp2:textures/entities/cloggerarmor.png");
      }

      @OnlyIn(Dist.CLIENT)
      public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
         super.appendHoverText(itemstack, context, list, flag);
         list.add(Component.translatable("item.undead_revamp2.cloggerarmor_helmet.description_0"));
      }

      public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
         return true;
      }

      public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
         super.inventoryTick(itemstack, world, entity, slot, selected);
         if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
            CloggerarmorHelmetTickEventProcedure.execute(entity);
         }
      }
   }

   public static class Leggings extends CloggerarmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(33)));
      }

      public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
         return ResourceLocation.parse("undead_revamp2:textures/entities/cloggerarmor.png");
      }

      @OnlyIn(Dist.CLIENT)
      public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
         super.appendHoverText(itemstack, context, list, flag);
         list.add(Component.translatable("item.undead_revamp2.cloggerarmor_leggings.description_0"));
      }

      public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
         return true;
      }

      public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
         super.inventoryTick(itemstack, world, entity, slot, selected);
         if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
            CloggerarmorHelmetTickEventProcedure.execute(entity);
         }
      }
   }
}
