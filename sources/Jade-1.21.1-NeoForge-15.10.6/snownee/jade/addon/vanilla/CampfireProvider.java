package snownee.jade.addon.vanilla;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.JadeIds;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

public enum CampfireProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
   INSTANCE;

   private static final MapCodec<Integer> COOKING_TIME_CODEC = Codec.INT.fieldOf("jade:cooking");

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_CAMPFIRE;
   }

   @Override
   public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
      return ClientViewGroup.map(groups, stack -> {
         CustomData customData = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
         if (customData.isEmpty()) {
            return null;
         } else {
            Optional<Integer> result = customData.read(COOKING_TIME_CODEC).result();
            if (result.isEmpty()) {
               return null;
            } else {
               String text = IThemeHelper.get().seconds(result.get(), accessor.tickRate()).getString();
               return new ItemView(stack).amountText(text);
            }
         }
      }, null);
   }

   @Nullable
   @Override
   public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
      if (accessor.getTarget() instanceof CampfireBlockEntity campfire) {
         List<ItemStack> list = Lists.newArrayList();

         for (int i = 0; i < campfire.cookingTime.length; i++) {
            ItemStack stack = (ItemStack)campfire.getItems().get(i);
            if (!stack.isEmpty()) {
               stack = stack.copy();
               CustomData customData = (CustomData)((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY))
                  .update(NbtOps.INSTANCE, COOKING_TIME_CODEC, campfire.cookingTime[i] - campfire.cookingProgress[i])
                  .getOrThrow();
               stack.set(DataComponents.CUSTOM_DATA, customData);
               list.add(stack);
            }
         }

         return List.of(new ViewGroup<>(list));
      } else {
         return null;
      }
   }
}
