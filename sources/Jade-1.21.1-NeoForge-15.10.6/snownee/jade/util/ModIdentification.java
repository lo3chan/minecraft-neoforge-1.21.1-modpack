package snownee.jade.util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.TraceableException;
import snownee.jade.api.callback.JadeItemModNameCallback;
import snownee.jade.impl.WailaClientRegistration;

public class ModIdentification implements ResourceManagerReloadListener {
   public static final ModIdentification INSTANCE = new ModIdentification();
   private static final Map<String, Optional<String>> NAMES = Maps.newConcurrentMap();

   public static void invalidateCache() {
      NAMES.clear();
   }

   public static Optional<String> getModName(String namespace) {
      return NAMES.computeIfAbsent(namespace, $ -> {
         Optional<String> optional = ClientProxy.getModName($);
         if (optional.isPresent()) {
            return optional;
         } else {
            String key = "jade.modName." + $;
            return I18n.exists(key) ? Optional.of(I18n.get(key, new Object[0])) : Optional.empty();
         }
      });
   }

   public static String getModName(ResourceLocation id) {
      return getModName(id.getNamespace()).orElse(id.getNamespace());
   }

   public static String getModName(Block block) {
      ResourceLocation id;
      try {
         id = CommonProxy.getId(block);
      } catch (Throwable var3) {
         throw TraceableException.create(var3, BuiltInRegistries.BLOCK.getKey(block).getNamespace());
      }

      return getModName(id);
   }

   public static String getModName(ItemStack stack) {
      String id;
      try {
         for (JadeItemModNameCallback callback : WailaClientRegistration.instance().itemModNameCallback.callbacks()) {
            String s = callback.gatherItemModName(stack);
            if (!Strings.isNullOrEmpty(s)) {
               return s;
            }
         }

         id = CommonProxy.getModIdFromItem(stack);
      } catch (Throwable var5) {
         throw TraceableException.create(var5, BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
      }

      return getModName(id).orElse(id);
   }

   public static String getModName(Entity entity) {
      if (entity instanceof Painting painting) {
         return getModName(((ResourceKey)painting.getVariant().unwrapKey().orElseThrow()).location());
      } else if (entity instanceof ItemEntity itemEntity) {
         return getModName(itemEntity.getItem());
      } else if (entity instanceof FallingBlockEntity fallingBlock) {
         return getModName(fallingBlock.getBlockState().getBlock());
      } else if (entity instanceof Villager villager) {
         return getModName(BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()));
      } else {
         ResourceLocation id;
         try {
            id = CommonProxy.getId(entity.getType());
         } catch (Throwable var3) {
            throw TraceableException.create(var3, BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
         }

         return getModName(id);
      }
   }

   public void onResourceManagerReload(ResourceManager manager) {
      invalidateCache();
   }
}
