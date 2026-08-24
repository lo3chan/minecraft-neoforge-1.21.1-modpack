package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public class SwordItemBuilder extends HandheldItemBuilder {
   public static final ResourceLocation[] SWORD_TAGS = new ResourceLocation[]{ItemTags.SWORDS.location()};
   public static final ResourceLocation SWORD_MODEL = ResourceLocation.withDefaultNamespace("item/iron_sword");

   public SwordItemBuilder(ResourceLocation i) {
      super(i, 3.0F, -2.4F);
      this.parentModel = SWORD_MODEL;
      this.tag(SWORD_TAGS);
   }

   @Override
   public Item createObject() {
      this.itemAttributeModifiers = SwordItem.createAttributes(this.toolTier, this.attackDamageBaseline, this.speedBaseline);
      return new SwordItem(this.toolTier, this.createItemProperties());
   }
}
