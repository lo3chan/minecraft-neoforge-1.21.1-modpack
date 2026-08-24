package at.petrak.hexcasting.common.recipe.ingredient.brainsweep;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class VillagerIngredient extends BrainsweepeeIngredient {
   @Nullable
   public final VillagerProfession profession;
   @Nullable
   public final VillagerType biome;
   public final int minLevel;

   public VillagerIngredient(@Nullable VillagerProfession profession, @Nullable VillagerType biome, int minLevel) {
      this.profession = profession;
      this.biome = biome;
      this.minLevel = minLevel;
   }

   @Override
   public boolean test(Entity entity, ServerLevel level) {
      if (!(entity instanceof Villager villager)) {
         return false;
      } else {
         VillagerData data = villager.getVillagerData();
         return (this.profession == null || this.profession.equals(data.getProfession()))
            && (this.biome == null || this.biome.equals(data.getType()))
            && this.minLevel <= data.getLevel();
      }
   }

   @Override
   public Entity exampleEntity(Level level) {
      VillagerType biome = Objects.requireNonNullElse(this.biome, VillagerType.PLAINS);
      VillagerProfession profession = Objects.requireNonNullElse(this.profession, VillagerProfession.TOOLSMITH);
      int tradeLevel = Math.min(this.minLevel, 1);
      Villager out = new Villager(EntityType.VILLAGER, level);
      VillagerData data = out.getVillagerData();
      data.setProfession(profession).setType(biome).setLevel(tradeLevel);
      out.setVillagerData(data);
      CompoundTag tag = new CompoundTag();
      out.save(tag);
      return out;
   }

   @Override
   public List<Component> getTooltip(boolean advanced) {
      List<Component> tooltip = new ArrayList<>();
      tooltip.add(this.getName());
      if (advanced) {
         if (this.minLevel >= 5) {
            tooltip.add(Component.translatable("hexcasting.tooltip.brainsweep.level", new Object[]{5}).withStyle(ChatFormatting.DARK_GRAY));
         } else if (this.minLevel > 1) {
            tooltip.add(Component.translatable("hexcasting.tooltip.brainsweep.min_level", new Object[]{this.minLevel}).withStyle(ChatFormatting.DARK_GRAY));
         }

         if (this.biome != null) {
            tooltip.add(Component.literal(this.biome.toString()).withStyle(ChatFormatting.DARK_GRAY));
         }

         if (this.profession != null) {
            tooltip.add(Component.literal(this.profession.toString()).withStyle(ChatFormatting.DARK_GRAY));
         }
      }

      tooltip.add(
         BrainsweepeeIngredient.getModNameComponent(
            this.profession == null ? "minecraft" : BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.profession).getNamespace()
         )
      );
      return tooltip;
   }

   @Override
   public Component getName() {
      MutableComponent component = Component.literal("");
      boolean addedAny = false;
      if (this.minLevel >= 5) {
         component.append(Component.translatable("merchant.level.5"));
         addedAny = true;
      } else if (this.minLevel > 1) {
         component.append(Component.translatable("merchant.level." + this.minLevel));
         addedAny = true;
      } else if (this.profession != null) {
         component.append(Component.translatable("merchant.level.1"));
         addedAny = true;
      }

      if (this.biome != null) {
         if (addedAny) {
            component.append(" ");
         }

         ResourceLocation biomeLoc = BuiltInRegistries.VILLAGER_TYPE.getKey(this.biome);
         component.append(Component.translatable("biome." + biomeLoc.getNamespace() + "." + biomeLoc.getPath()));
         addedAny = true;
      }

      if (this.profession != null) {
         component.append(" ");
         ResourceLocation professionLoc = BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.profession);
         component.append(Component.translatable("entity.minecraft.villager." + professionLoc.getPath()));
      } else {
         if (addedAny) {
            component.append(" ");
         }

         component.append(EntityType.VILLAGER.getDescription());
      }

      return component;
   }

   @Override
   public JsonObject serialize() {
      JsonObject obj = new JsonObject();
      obj.addProperty("type", BrainsweepeeIngredient.Type.VILLAGER.getSerializedName());
      if (this.profession != null) {
         obj.addProperty("profession", this.profession.toString());
      }

      if (this.biome != null) {
         obj.addProperty("biome", this.biome.toString());
      }

      obj.addProperty("minLevel", this.minLevel);
      return obj;
   }

   @Override
   public void write(FriendlyByteBuf buf) {
      if (this.profession != null) {
         buf.writeVarInt(1);
         buf.writeVarInt(BuiltInRegistries.VILLAGER_PROFESSION.getId(this.profession));
      } else {
         buf.writeVarInt(0);
      }

      if (this.biome != null) {
         buf.writeVarInt(1);
         buf.writeVarInt(BuiltInRegistries.VILLAGER_TYPE.getId(this.biome));
      } else {
         buf.writeVarInt(0);
      }

      buf.writeInt(this.minLevel);
   }

   public static VillagerIngredient deserialize(JsonObject json) {
      VillagerProfession profession = null;
      if (json.has("profession") && !json.get("profession").isJsonNull()) {
         profession = (VillagerProfession)BuiltInRegistries.VILLAGER_PROFESSION.get(ResourceLocation.parse(GsonHelper.getAsString(json, "profession")));
      }

      VillagerType biome = null;
      if (json.has("biome") && !json.get("biome").isJsonNull()) {
         biome = (VillagerType)BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(GsonHelper.getAsString(json, "biome")));
      }

      int minLevel = GsonHelper.getAsInt(json, "minLevel");
      return new VillagerIngredient(profession, biome, minLevel);
   }

   public static VillagerIngredient read(FriendlyByteBuf buf) {
      VillagerProfession profession = null;
      int hasProfession = buf.readVarInt();
      if (hasProfession != 0) {
         profession = (VillagerProfession)BuiltInRegistries.VILLAGER_PROFESSION.byId(buf.readVarInt());
      }

      VillagerType biome = null;
      int hasBiome = buf.readVarInt();
      if (hasBiome != 0) {
         biome = (VillagerType)BuiltInRegistries.VILLAGER_TYPE.byId(buf.readVarInt());
      }

      int minLevel = buf.readInt();
      return new VillagerIngredient(profession, biome, minLevel);
   }

   @Override
   public BrainsweepeeIngredient.Type ingrType() {
      return BrainsweepeeIngredient.Type.VILLAGER;
   }

   @Override
   public String getSomeKindOfReasonableIDForEmi() {
      StringBuilder bob = new StringBuilder();
      if (this.profession != null) {
         ResourceLocation profLoc = BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.profession);
         bob.append(profLoc.getNamespace()).append("//").append(profLoc.getPath());
      } else {
         bob.append("null");
      }

      bob.append("_");
      if (this.biome != null) {
         ResourceLocation biomeLoc = BuiltInRegistries.VILLAGER_TYPE.getKey(this.biome);
         bob.append(biomeLoc.getNamespace()).append("//").append(biomeLoc.getPath());
      } else {
         bob.append("null");
      }

      bob.append(this.minLevel);
      return bob.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VillagerIngredient that = (VillagerIngredient)o;
         return this.minLevel == that.minLevel && Objects.equals(this.profession, that.profession) && Objects.equals(this.biome, that.biome);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.profession, this.biome, this.minLevel);
   }
}
