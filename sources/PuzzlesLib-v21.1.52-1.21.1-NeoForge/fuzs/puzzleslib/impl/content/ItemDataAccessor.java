package fuzs.puzzleslib.impl.content;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument.NbtPath;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentPatch.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands.DataProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemDataAccessor implements DataAccessor {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
      entity -> Component.translatableEscape("commands.enchant.failed.entity", new Object[]{entity})
   );
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(
      entity -> Component.translatableEscape("commands.enchant.failed.itemless", new Object[]{entity})
   );
   public static final Function<String, DataProvider> PROVIDER = argumentName -> new DataProvider() {
      public DataAccessor access(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
         Entity entity = EntityArgument.getEntity(context, argumentName);
         if (entity instanceof LivingEntity livingEntity) {
            ItemStack itemStack = livingEntity.getMainHandItem();
            if (itemStack.isEmpty()) {
               throw ItemDataAccessor.ERROR_NO_ITEM.create(entity.getName().getString());
            } else {
               return new ItemDataAccessor(((CommandSourceStack)context.getSource()).registryAccess(), itemStack);
            }
         } else {
            throw ItemDataAccessor.ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
         }
      }

      public ArgumentBuilder<CommandSourceStack, ?> wrap(
         ArgumentBuilder<CommandSourceStack, ?> builder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> action
      ) {
         return builder.then(Commands.literal("item").then(action.apply(Commands.argument(argumentName, EntityArgument.entity()))));
      }
   };
   private final RegistryAccess registryAccess;
   private final ItemStack itemStack;

   public ItemDataAccessor(RegistryAccess registryAccess, ItemStack itemStack) {
      this.registryAccess = registryAccess;
      this.itemStack = itemStack;
   }

   public void setData(CompoundTag compoundTag) {
      RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      DataComponentMap.CODEC.parse(registryOps, compoundTag).resultOrPartial().ifPresent(newComponents -> {
         DataComponentMap oldComponents = this.itemStack.getComponents();
         this.itemStack.applyComponents(this.constructDataComponentPatch(oldComponents, newComponents));
      });
   }

   <T> DataComponentPatch constructDataComponentPatch(DataComponentMap oldComponents, DataComponentMap newComponents) {
      Builder builder = DataComponentPatch.builder();
      UnmodifiableIterator var4 = Sets.union(oldComponents.keySet(), newComponents.keySet()).iterator();

      while (var4.hasNext()) {
         DataComponentType<?> dataComponentType = (DataComponentType<?>)var4.next();
         T t = (T)newComponents.get(dataComponentType);
         if (!newComponents.has(dataComponentType)) {
            builder.remove(dataComponentType);
         } else if (!Objects.equals(oldComponents.get(dataComponentType), t)) {
            builder.set(dataComponentType, t);
         }
      }

      return builder.build();
   }

   public CompoundTag getData() {
      RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      return DataComponentMap.CODEC
         .encodeStart(registryOps, this.itemStack.getComponents())
         .resultOrPartial(PuzzlesLib.LOGGER::error)
         .map(tag -> tag instanceof CompoundTag compoundTag ? compoundTag : null)
         .orElseGet(CompoundTag::new);
   }

   public Component getModifiedSuccess() {
      return Component.translatable("commands.data.entity.modified", new Object[]{this.itemStack.getDisplayName()});
   }

   public Component getPrintSuccess(Tag tag) {
      return Component.translatable("commands.data.entity.query", new Object[]{this.itemStack.getDisplayName(), NbtUtils.toPrettyComponent(tag)});
   }

   public Component getPrintSuccess(NbtPath path, double scale, int value) {
      return Component.translatable(
         "commands.data.entity.get", new Object[]{path.asString(), this.itemStack.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), value}
      );
   }
}
