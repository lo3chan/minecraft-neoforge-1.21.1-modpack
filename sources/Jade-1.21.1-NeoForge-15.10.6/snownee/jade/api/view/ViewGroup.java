package snownee.jade.api.view;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ViewGroup<T> {
   public List<T> views;
   @Nullable
   public String id;
   @Nullable
   protected CompoundTag extraData;

   public static <B extends ByteBuf, T> StreamCodec<B, ViewGroup<T>> codec(StreamCodec<B, T> viewCodec) {
      return StreamCodec.composite(
         ByteBufCodecs.list().apply(viewCodec),
         $ -> $.views,
         ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
         $ -> Optional.ofNullable($.id),
         ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG),
         $ -> Optional.ofNullable($.extraData),
         ViewGroup::new
      );
   }

   public static <B extends ByteBuf, T> StreamCodec<B, Entry<ResourceLocation, List<ViewGroup<T>>>> listCodec(StreamCodec<B, T> viewCodec) {
      return StreamCodec.composite(ResourceLocation.STREAM_CODEC, Entry::getKey, ByteBufCodecs.list().apply(codec(viewCodec)), Entry::getValue, Map::entry);
   }

   public ViewGroup(List<T> views) {
      this(views, Optional.empty(), Optional.empty());
   }

   public ViewGroup(List<T> views, Optional<String> id, Optional<CompoundTag> extraData) {
      this.views = views;
      this.id = id.orElse(null);
      this.extraData = extraData.orElse(null);
   }

   public void save(CompoundTag tag, Function<T, CompoundTag> writer) {
      ListTag list = new ListTag();

      for (T view : this.views) {
         list.add((Tag)writer.apply(view));
      }

      tag.put("Views", list);
      if (this.id != null) {
         tag.putString("Id", this.id);
      }

      if (this.extraData != null) {
         tag.put("Data", this.extraData);
      }
   }

   public static <T> ViewGroup<T> read(CompoundTag tag, Function<CompoundTag, T> reader) {
      ListTag list = tag.getList("Views", 10);
      List<T> views = Lists.newArrayList();

      for (Tag view : list) {
         views.add(reader.apply((CompoundTag)view));
      }

      ViewGroup<T> group = new ViewGroup<>(views);
      if (tag.contains("Id")) {
         group.id = tag.getString("Id");
      }

      if (tag.contains("Data")) {
         group.extraData = tag.getCompound("Data");
      }

      return group;
   }

   public static <T> boolean saveList(CompoundTag tag, String key, List<ViewGroup<T>> groups, Function<T, CompoundTag> writer) {
      if (groups == null) {
         return false;
      } else if (groups.isEmpty()) {
         return true;
      } else {
         ListTag groupList = new ListTag();

         for (ViewGroup<T> group : groups) {
            if (!group.views.isEmpty()) {
               CompoundTag groupTag = new CompoundTag();
               group.save(groupTag, writer);
               groupList.add(groupTag);
            }
         }

         if (!groupList.isEmpty()) {
            tag.put(key, groupList);
         }

         return true;
      }
   }

   @Nullable
   public static <T> List<ViewGroup<T>> readList(CompoundTag tag, String key, Function<CompoundTag, T> reader) {
      ListTag list = tag.getList(key, 10);
      if (list.isEmpty()) {
         return null;
      } else {
         List<ViewGroup<T>> groups = Lists.newArrayList();

         for (Tag item : list) {
            ViewGroup<T> group = read((CompoundTag)item, reader);
            if (!group.views.isEmpty()) {
               groups.add(group);
            }
         }

         return groups;
      }
   }

   public CompoundTag getExtraData() {
      if (this.extraData == null) {
         this.extraData = new CompoundTag();
      }

      return this.extraData;
   }

   public void setProgress(float progress) {
      this.getExtraData().putFloat("Progress", progress);
   }
}
