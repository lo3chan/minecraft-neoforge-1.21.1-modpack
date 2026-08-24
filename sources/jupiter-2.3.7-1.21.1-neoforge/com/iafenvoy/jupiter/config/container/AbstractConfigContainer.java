package com.iafenvoy.jupiter.config.container;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.ConfigDataFixer;
import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.TextUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractConfigContainer implements ConfigMetaProvider {
   protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   protected final List<ConfigGroup> configTabs = new ArrayList<>();
   protected final ResourceLocation id;
   protected final Component title;
   protected final ConfigDataFixer dataFixer = new ConfigDataFixer();
   @Nullable
   private Codec<List<ConfigGroup>> codecCache;

   public AbstractConfigContainer(ResourceLocation id, Component title) {
      this.id = id;
      this.title = title;
   }

   @Override
   public ResourceLocation getConfigId() {
      return this.id;
   }

   public Component getTitle() {
      return this.title;
   }

   public ConfigGroup createTab(String id, String translateKey) {
      return this.createTab(id, TextUtil.translatable(translateKey));
   }

   public ConfigGroup createTab(String id, Component name) {
      ConfigGroup category = new ConfigGroup(id, name, new ArrayList<>());
      this.configTabs.add(category);
      this.codecCache = null;
      return category;
   }

   public List<ConfigGroup> getConfigTabs() {
      return this.configTabs;
   }

   public final void onConfigsChanged() {
      this.save();
      this.load();
   }

   public abstract void init();

   public abstract void load();

   public abstract void save();

   public String serialize() {
      return GSON.toJson((JsonElement)this.getCodec().encodeStart(JsonOps.INSTANCE, this.configTabs).resultOrPartial(Jupiter.LOGGER::error).orElseThrow());
   }

   @Comment("For Network Usage Only")
   public final CompoundTag serializeNbt() {
      return (CompoundTag)this.getCodec().encodeStart(NbtOps.INSTANCE, this.configTabs).resultOrPartial(Jupiter.LOGGER::error).orElseThrow();
   }

   public void deserialize(String data) {
      if (JsonParser.parseString(data) instanceof JsonObject obj) {
         this.getCodec().parse(JsonOps.INSTANCE, obj);
      }
   }

   @Comment("For Network Usage Only")
   public final void deserializeNbt(CompoundTag element) {
      if (element != null) {
         this.getCodec().parse(NbtOps.INSTANCE, element);
      }
   }

   @NotNull
   public Codec<List<ConfigGroup>> getCodec() {
      if (this.codecCache == null) {
         this.codecCache = this.buildCodec();
      }

      return this.codecCache;
   }

   protected Codec<List<ConfigGroup>> buildCodec() {
      return new AbstractConfigContainer.GroupsCodec(this).codec();
   }

   @Nullable
   public ResourceLocation getBackgroundTexture(boolean ingame) {
      return null;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Only call on saving to disk, not on network")
   protected boolean shouldLoad(JsonObject obj) {
      return true;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Only call on saving to disk, not on network")
   protected void readCustomData(JsonObject obj) {
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Only call on saving to disk, not on network")
   protected void writeCustomData(JsonObject obj) {
   }

   private static class GroupsCodec extends MapCodec<List<ConfigGroup>> {
      private final AbstractConfigContainer parent;

      private GroupsCodec(AbstractConfigContainer parent) {
         this.parent = parent;
      }

      public <T> Stream<T> keys(DynamicOps<T> ops) {
         return this.parent.configTabs.stream().map(ConfigGroup::getId).map(ops::createString);
      }

      public <T> RecordBuilder<T> encode(List<ConfigGroup> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
         return input.stream().reduce(prefix, (p, c) -> p.add(c.getId(), c.encode(this.parent.dataFixer, ops)), (a, b) -> null);
      }

      public <T> DataResult<List<ConfigGroup>> decode(DynamicOps<T> ops, MapLike<T> input) {
         input.entries()
            .forEach(
               x -> ops.getStringValue(x.getFirst())
                  .resultOrPartial(Jupiter.LOGGER::error)
                  .map(this.parent.dataFixer::fixKey)
                  .ifPresent(
                     s -> this.parent
                        .configTabs
                        .stream()
                        .filter(y -> Objects.equals(y.getId(), s))
                        .forEach(y -> y.decode(this.parent.dataFixer, ops, x.getSecond()))
                  )
            );
         return DataResult.success(this.parent.configTabs);
      }
   }
}
