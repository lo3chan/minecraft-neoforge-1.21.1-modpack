package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityIota extends Iota {
   private static final Frozen BUILTIN_REGISTRY_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
   public static IotaType<EntityIota> TYPE = new IotaType<EntityIota>() {
      @Nullable
      public EntityIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
         CompoundTag ctag = HexUtils.downcast(tag, CompoundTag.TYPE);
         Tag uuidTag = ctag.get("uuid");
         if (uuidTag == null) {
            return null;
         } else {
            UUID uuid = NbtUtils.loadUUID(uuidTag);
            Entity entity = world.getEntity(uuid);
            return entity == null ? null : new EntityIota(entity);
         }
      }

      @Override
      public Component display(Tag tag) {
         if (tag instanceof CompoundTag ctag) {
            if (!ctag.contains("name", 8)) {
               return Component.translatable("hexcasting.spelldata.entity.whoknows");
            } else {
               String nameJson = ctag.getString("name");
               MutableComponent component = Serializer.fromJsonLenient(nameJson, EntityIota.BUILTIN_REGISTRY_ACCESS);
               return (component == null ? Component.translatable("hexcasting.spelldata.entity.whoknows") : component).withStyle(ChatFormatting.AQUA);
            }
         } else {
            return Component.translatable("hexcasting.spelldata.entity.whoknows");
         }
      }

      @Override
      public int color() {
         return -11141121;
      }
   };

   public EntityIota(@NotNull Entity e) {
      super(HexIotaTypes.ENTITY, e);
   }

   public Entity getEntity() {
      return (Entity)this.payload;
   }

   @Override
   public boolean toleratesOther(Iota that) {
      return typesMatch(this, that) && that instanceof EntityIota dent && this.getEntity() == dent.getEntity();
   }

   @Override
   public boolean isTruthy() {
      return true;
   }

   @NotNull
   @Override
   public Tag serialize() {
      CompoundTag out = new CompoundTag();
      out.putUUID("uuid", this.getEntity().getUUID());
      out.putString("name", Serializer.toJson(this.getEntity().getName(), BUILTIN_REGISTRY_ACCESS));
      return out;
   }

   @Override
   public Component display() {
      return this.getEntity().getName().copy().withStyle(ChatFormatting.AQUA);
   }
}
