package net.mcreator.borninchaosv.item;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.mcreator.borninchaosv.client.model.Modelkillerrabbitears;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.KillerRabbitEarsKazhdyiTikDliaShliemaProcedure;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
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
public abstract class KillerRabbitEarsItem extends ArmorItem {
   public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

   @SubscribeEvent
   public static void registerArmorMaterial(RegisterEvent event) {
      event.register(
         Registries.ARMOR_MATERIAL,
         registerHelper -> {
            ArmorMaterial armorMaterial = new ArmorMaterial(
               (Map)Util.make(new EnumMap(Type.class), map -> {
                  map.put(Type.BOOTS, 0);
                  map.put(Type.LEGGINGS, 0);
                  map.put(Type.CHESTPLATE, 0);
                  map.put(Type.HELMET, 2);
                  map.put(Type.BODY, 0);
               }),
               25,
               DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_leather")),
               () -> Ingredient.of(new ItemStack[]{new ItemStack(Items.RABBIT_HIDE)}),
               List.of(new Layer(ResourceLocation.parse("born_in_chaos_v1:darkmetar"))),
               0.0F,
               0.0F
            );
            registerHelper.register(ResourceLocation.parse("born_in_chaos_v1:killer_rabbit_ears"), armorMaterial);
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
                        (new Modelkillerrabbitears(Minecraft.getInstance().getEntityModels().bakeLayer(Modelkillerrabbitears.LAYER_LOCATION))).Head,
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
         new Item[]{(Item)BornInChaosV1ModItems.KILLER_RABBIT_EARS_HELMET.get()}
      );
   }

   public KillerRabbitEarsItem(Type type, Properties properties) {
      super(ARMOR_MATERIAL, type, properties);
   }

   public static class Helmet extends KillerRabbitEarsItem {
      public Helmet() {
         super(Type.HELMET, new Properties().durability(Type.HELMET.getDurability(20)));
      }

      public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
         return ResourceLocation.parse("born_in_chaos_v1:textures/entities/killerrabbitears.png");
      }

      public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
         super.inventoryTick(itemstack, world, entity, slot, selected);
         if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
            KillerRabbitEarsKazhdyiTikDliaShliemaProcedure.execute(entity);
         }
      }
   }
}
