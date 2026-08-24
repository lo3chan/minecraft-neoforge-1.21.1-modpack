package dev.latvian.mods.kubejs.item.custom;

import java.util.function.BiFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.common.Tags.Items;

public class DiggerItemBuilder extends HandheldItemBuilder {
   public final BiFunction<Tier, Properties, DiggerItem> function;

   public DiggerItemBuilder(ResourceLocation i, float d, float s, BiFunction<Tier, Properties, DiggerItem> f) {
      super(i, d, s);
      this.function = f;
   }

   @Override
   public Item createObject() {
      this.itemAttributeModifiers = DiggerItem.createAttributes(this.toolTier, this.attackDamageBaseline, this.speedBaseline);
      return (Item)this.function.apply(this.toolTier, this.createItemProperties());
   }

   public static class Axe extends DiggerItemBuilder {
      public static final ResourceLocation[] AXE_TAGS = new ResourceLocation[]{ItemTags.AXES.location()};
      public static final ResourceLocation AXE_MODEL = ResourceLocation.withDefaultNamespace("item/iron_axe");

      public Axe(ResourceLocation i) {
         super(i, 6.0F, -3.1F, AxeItem::new);
         this.parentModel = AXE_MODEL;
         this.tag(AXE_TAGS);
      }
   }

   public static class Hoe extends DiggerItemBuilder {
      public static final ResourceLocation[] HOE_TAGS = new ResourceLocation[]{ItemTags.HOES.location()};
      public static final ResourceLocation HOE_MODEL = ResourceLocation.withDefaultNamespace("item/iron_hoe");

      public Hoe(ResourceLocation i) {
         super(i, 0.0F, -3.0F, HoeItem::new);
         this.parentModel = HOE_MODEL;
         this.tag(HOE_TAGS);
      }
   }

   public static class Pickaxe extends DiggerItemBuilder {
      public static final ResourceLocation[] PICKAXE_TAGS = new ResourceLocation[]{
         ItemTags.PICKAXES.location(), ItemTags.CLUSTER_MAX_HARVESTABLES.location(), Items.MINING_TOOL_TOOLS.location()
      };
      public static final ResourceLocation PICKAXE_MODEL = ResourceLocation.withDefaultNamespace("item/iron_pickaxe");

      public Pickaxe(ResourceLocation i) {
         super(i, 1.0F, -2.8F, PickaxeItem::new);
         this.parentModel = PICKAXE_MODEL;
         this.tag(PICKAXE_TAGS);
      }
   }

   public static class Shovel extends DiggerItemBuilder {
      public static final ResourceLocation[] SHOVEL_TAGS = new ResourceLocation[]{ItemTags.SHOVELS.location()};
      public static final ResourceLocation SHOVEL_MODEL = ResourceLocation.withDefaultNamespace("item/iron_shovel");

      public Shovel(ResourceLocation i) {
         super(i, 1.5F, -3.0F, ShovelItem::new);
         this.parentModel = SHOVEL_MODEL;
         this.tag(SHOVEL_TAGS);
      }
   }
}
