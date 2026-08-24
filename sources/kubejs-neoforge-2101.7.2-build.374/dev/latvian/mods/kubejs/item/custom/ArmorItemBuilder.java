package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.item.ArmorItem.Type;

@ReturnsSelf
public class ArmorItemBuilder extends ItemBuilder {
   public final Type armorType;
   public Holder<ArmorMaterial> material;

   protected ArmorItemBuilder(ResourceLocation id, Type t) {
      super(id);
      this.armorType = t;
      this.material = ArmorMaterials.IRON;
      this.unstackable();
   }

   @Override
   public Item createObject() {
      return new ArmorItem(this.material, this.armorType, this.createItemProperties());
   }

   public ArmorItemBuilder material(Holder<ArmorMaterial> material) {
      this.material = material;
      return this;
   }

   @ReturnsSelf
   public static class AnimalArmor extends ArmorItemBuilder {
      public BodyType bodyType = BodyType.CANINE;
      public boolean overlay = true;

      public AnimalArmor(ResourceLocation id) {
         super(id, Type.BODY);
      }

      @Override
      public Item createObject() {
         return new AnimalArmorItem(this.material, this.bodyType, this.overlay, this.createItemProperties());
      }

      public ArmorItemBuilder.AnimalArmor bodyType(BodyType type) {
         this.bodyType = type;
         return this;
      }

      public ArmorItemBuilder.AnimalArmor overlay(boolean o) {
         this.overlay = o;
         return this;
      }
   }

   public static class Boots extends ArmorItemBuilder {
      public static final ResourceLocation[] BOOT_TAGS = new ResourceLocation[]{ItemTags.FOOT_ARMOR.location()};

      public Boots(ResourceLocation id) {
         super(id, Type.BOOTS);
         this.tag(BOOT_TAGS);
      }
   }

   public static class Chestplate extends ArmorItemBuilder {
      public static final ResourceLocation[] CHESTPLATE_TAGS = new ResourceLocation[]{ItemTags.CHEST_ARMOR.location()};

      public Chestplate(ResourceLocation id) {
         super(id, Type.CHESTPLATE);
         this.tag(CHESTPLATE_TAGS);
      }
   }

   public static class Helmet extends ArmorItemBuilder {
      public static final ResourceLocation[] HELMET_TAGS = new ResourceLocation[]{ItemTags.HEAD_ARMOR.location()};

      public Helmet(ResourceLocation id) {
         super(id, Type.HELMET);
         this.tag(HELMET_TAGS);
      }
   }

   public static class Leggings extends ArmorItemBuilder {
      public static final ResourceLocation[] LEGGING_TAGS = new ResourceLocation[]{ItemTags.LEG_ARMOR.location()};

      public Leggings(ResourceLocation id) {
         super(id, Type.LEGGINGS);
         this.tag(LEGGING_TAGS);
      }
   }
}
