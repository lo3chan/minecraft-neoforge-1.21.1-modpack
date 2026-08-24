package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.registry.ModelledBuilderBase;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class ItemBuilder extends ModelledBuilderBase<Item> implements ItemBehaviorFunctions {
   public transient Map<Object, Object> components;
   public transient int maxStackSize;
   public transient int maxDamage;
   public transient int burnTime;
   private ResourceLocation containerItem;
   public transient Function<ItemStack, Collection<ItemStack>> subtypes;
   public transient Rarity rarity;
   public transient boolean fireResistant;
   @Nullable
   public transient ItemTintFunction tint;
   public transient FoodBuilder foodBuilder;
   public transient JukeboxPlayable jukeboxPlayable;
   public final transient ItemBehavior behavior = new ItemBehavior();
   public transient Tool tool;
   public transient ItemAttributeModifiers itemAttributeModifiers;
   public transient boolean canRepair;

   public ItemBuilder(ResourceLocation id) {
      super(id);
      this.baseTexture = id.withPath(ID.ITEM).toString();
      this.maxStackSize = -1;
      this.maxDamage = 0;
      this.burnTime = 0;
      this.containerItem = null;
      this.subtypes = null;
      this.rarity = null;
      this.foodBuilder = null;
      this.fireResistant = false;
      this.tool = null;
      this.itemAttributeModifiers = null;
      this.canRepair = true;
   }

   public Item createObject() {
      return new Item(this.createItemProperties());
   }

   public Item transformObject(Item obj) {
      this.displayName(this.displayName, this.formattedDisplayName);
      obj.kjs$setItemBehavior(this.behavior);
      return obj;
   }

   @Override
   public void generateAssets(KubeAssetGenerator generator) {
      this.generateItemModels(generator);
   }

   protected void generateItemModels(KubeAssetGenerator generator) {
      generator.itemModel(this.id, m -> {
         if (this.modelGenerator != null) {
            this.modelGenerator.accept(m);
         } else {
            m.parent(this.parentModel != null ? this.parentModel : KubeAssetGenerator.GENERATED_ITEM_MODEL);
            if (this.textures.isEmpty()) {
               m.texture("layer0", this.baseTexture);
            } else {
               m.textures(this.textures);
            }
         }
      });
   }

   public <T> ItemBuilder component(DataComponentType<T> type, T value) {
      if (this.components == null) {
         this.components = new HashMap<>();
      }

      this.components.put(type, value);
      return this;
   }

   @Info("Sets the item's max stack size. Default is 64.")
   public ItemBuilder maxStackSize(int v) {
      this.maxStackSize = v;
      return this;
   }

   @Info("Makes the item not stackable, equivalent to setting the item's max stack size to 1.")
   public ItemBuilder unstackable() {
      return this.maxStackSize(1);
   }

   @Info("Sets the item's max damage. Default is 0 (No durability).")
   public ItemBuilder maxDamage(int v) {
      this.maxDamage = v;
      return this;
   }

   @Info("Sets the item's burn time. Default is 0 (Not a fuel).")
   public ItemBuilder burnTime(TickDuration v) {
      this.burnTime = v.intTicks();
      return this;
   }

   @Info("Sets the item's container item, e.g. a bucket for a milk bucket.")
   public ItemBuilder containerItem(ResourceLocation id) {
      this.containerItem = id;
      return this;
   }

   @Info("Adds subtypes to the item. The function should return a collection of item stacks, each with a different subtype.\n\nEach subtype will appear as a separate item in JEI and the creative inventory.\n")
   public ItemBuilder subtypes(Function<ItemStack, Collection<ItemStack>> fn) {
      this.subtypes = fn;
      return this;
   }

   @Info("Sets the item's rarity.")
   public ItemBuilder rarity(Rarity v) {
      this.rarity = v;
      return this;
   }

   @Deprecated
   public ItemBuilder group(@Nullable String g) {
      ConsoleJS.STARTUP.error("Item builder .group() is no longer supported, use StartupEvents.modifyCreativeTab!");
      return this;
   }

   @Info("Colorizes item's texture of the given index. Index is used when you have multiple layers, e.g. a crushed ore (of rock + ore).")
   public ItemBuilder color(int index, ItemTintFunction color) {
      if (!(this.tint instanceof ItemTintFunction.Mapped)) {
         this.tint = new ItemTintFunction.Mapped();
      }

      ((ItemTintFunction.Mapped)this.tint).map.put(index, color);
      return this;
   }

   @Info("Colorizes item's texture of the given index. Useful for coloring items, like GT ores ore dusts.")
   public ItemBuilder color(ItemTintFunction callback) {
      this.tint = callback;
      return this;
   }

   @Info("Set the food properties of the item.\n")
   public ItemBuilder food(Consumer<FoodBuilder> b) {
      if (this.foodBuilder == null) {
         this.foodBuilder = new FoodBuilder();
      }

      b.accept(this.foodBuilder);
      this.foodEaten(this.foodBuilder.eaten);
      return this;
   }

   @Info("Set the food nutrition and saturation of the item.\n")
   public ItemBuilder food(int nutrition, float saturation) {
      return this.food(b -> b.nutrition(nutrition).saturation(saturation));
   }

   @Info("Makes the item fire resistant like netherite tools (or not).")
   public ItemBuilder fireResistant(boolean isFireResistant) {
      this.fireResistant = isFireResistant;
      return this;
   }

   @Info("Makes the item fire resistant like netherite tools.")
   public ItemBuilder fireResistant() {
      return this.fireResistant(true);
   }

   @Override
   public ItemBehavior kjs$getOrCreateBehavior() {
      return this.behavior;
   }

   public ItemBuilder jukeboxPlayable(ResourceKey<JukeboxSong> song, boolean showInTooltip) {
      this.jukeboxPlayable = new JukeboxPlayable(new EitherHolder(song), showInTooltip);
      return this;
   }

   public ItemBuilder jukeboxPlayable(ResourceKey<JukeboxSong> song) {
      return this.jukeboxPlayable(song, true);
   }

   public ItemBuilder disableRepair() {
      this.canRepair = false;
      return this;
   }

   public Properties createItemProperties() {
      KubeJSItemProperties properties = new KubeJSItemProperties(this);
      if (this.components != null && !this.components.isEmpty()) {
         for (Entry<Object, Object> entry : this.components.entrySet()) {
            DataComponentType<?> type = DataComponentWrapper.wrapType(entry.getKey());
            if (type != null) {
               properties.component(type, entry.getValue());
            } else {
               ConsoleJS.STARTUP.error("Component '" + entry.getKey() + "' not found for item " + this.id);
            }
         }
      }

      if (this.maxDamage > 0) {
         properties.durability(this.maxDamage);
      } else if (this.maxStackSize != -1) {
         properties.stacksTo(this.maxStackSize);
      }

      if (this.rarity != null) {
         properties.rarity(this.rarity);
      }

      Item item = this.containerItem == null ? Items.AIR : ItemWrapper.getItem(this.containerItem);
      if (item != Items.AIR) {
         properties.craftRemainder(item);
      }

      if (this.foodBuilder != null) {
         properties.food(this.foodBuilder.build());
      }

      if (this.fireResistant) {
         properties.fireResistant();
      }

      if (this.tool != null) {
         properties.component(DataComponents.TOOL, this.tool);
      }

      if (this.itemAttributeModifiers != null) {
         properties.attributes(this.itemAttributeModifiers);
      }

      if (this.jukeboxPlayable != null) {
         properties.component(DataComponents.JUKEBOX_PLAYABLE, this.jukeboxPlayable);
      }

      if (!this.canRepair) {
         properties.setNoRepair();
      }

      return properties;
   }
}
