package vazkii.akashictome;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.DataComponents;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import vazkii.akashictome.data_components.ToolContentComponent;

public final class Registries {
   public static final Items ITEMS = DeferredRegister.createItems("akashictome");
   public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, "akashictome");
   public static final DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents("akashictome");
   public static final Supplier<DataComponentType<ToolContentComponent>> TOOL_CONTENT = DATA_COMPONENTS.registerComponentType(
      "tool_content", builder -> builder.persistent(ToolContentComponent.CODEC).networkSynchronized(ToolContentComponent.STREAM_CODEC).cacheEncoding()
   );
   public static final Supplier<DataComponentType<Boolean>> IS_MORPHED = DATA_COMPONENTS.registerComponentType(
      "is_morphed", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
   );
   public static final Supplier<DataComponentType<Component>> OG_DISPLAY_NAME = DATA_COMPONENTS.register(
      "og_display_name",
      () -> DataComponentType.builder().persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).build()
   );
   public static final Supplier<DataComponentType<String>> DEFINED_MOD = DATA_COMPONENTS.register(
      "defined_mod", () -> DataComponentType.builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build()
   );
   public static final Supplier<DataComponentType<Component>> CUSTOM_TOME_NAME = DATA_COMPONENTS.register(
      "custom_tome_name",
      () -> DataComponentType.builder().persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).build()
   );
   public static final DeferredItem<Item> TOME = ITEMS.registerItem("tome", TomeItem::new);
   public static final Supplier<RecipeSerializer<AttachementRecipe>> ATTACHMENT = SERIALIZERS.register(
      "attachment", () -> new SimpleCraftingRecipeSerializer(AttachementRecipe::new)
   );
}
