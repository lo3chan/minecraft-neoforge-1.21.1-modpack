package io.wispforest.owo.util;

import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class VectorSerializer {
   private VectorSerializer() {
   }

   public static CompoundTag put(CompoundTag nbt, String key, Vec3 vec3d) {
      ListTag vectorArray = new ListTag();
      vectorArray.add(DoubleTag.valueOf(vec3d.x));
      vectorArray.add(DoubleTag.valueOf(vec3d.y));
      vectorArray.add(DoubleTag.valueOf(vec3d.z));
      nbt.put(key, vectorArray);
      return nbt;
   }

   public static CompoundTag putf(CompoundTag nbt, String key, Vector3f vec3f) {
      ListTag vectorArray = new ListTag();
      vectorArray.add(FloatTag.valueOf(vec3f.x));
      vectorArray.add(FloatTag.valueOf(vec3f.y));
      vectorArray.add(FloatTag.valueOf(vec3f.z));
      nbt.put(key, vectorArray);
      return nbt;
   }

   public static CompoundTag puti(CompoundTag nbt, String key, Vec3i vec3i) {
      nbt.putIntArray(key, List.of(vec3i.getX(), vec3i.getY(), vec3i.getZ()));
      return nbt;
   }

   public static Vec3 get(CompoundTag nbt, String key) {
      ListTag vectorArray = nbt.getList(key, 6);
      double x = vectorArray.getDouble(0);
      double y = vectorArray.getDouble(1);
      double z = vectorArray.getDouble(2);
      return new Vec3(x, y, z);
   }

   public static Vector3f getf(CompoundTag nbt, String key) {
      ListTag vectorArray = nbt.getList(key, 5);
      float x = vectorArray.getFloat(0);
      float y = vectorArray.getFloat(1);
      float z = vectorArray.getFloat(2);
      return new Vector3f(x, y, z);
   }

   public static Vec3i geti(CompoundTag nbt, String key) {
      int[] vectorArray = nbt.getIntArray(key);
      int x = vectorArray[0];
      int y = vectorArray[1];
      int z = vectorArray[2];
      return new Vec3i(x, y, z);
   }

   public static void write(FriendlyByteBuf buffer, Vec3 vec3d) {
      buffer.writeDouble(vec3d.x);
      buffer.writeDouble(vec3d.y);
      buffer.writeDouble(vec3d.z);
   }

   public static void writef(FriendlyByteBuf buffer, Vector3f vec3f) {
      buffer.writeFloat(vec3f.x);
      buffer.writeFloat(vec3f.y);
      buffer.writeFloat(vec3f.z);
   }

   public static void writei(FriendlyByteBuf buffer, Vec3i vec3i) {
      buffer.writeInt(vec3i.getX());
      buffer.writeInt(vec3i.getY());
      buffer.writeInt(vec3i.getZ());
   }

   public static Vec3 read(FriendlyByteBuf buffer) {
      return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
   }

   public static Vector3f readf(FriendlyByteBuf buffer) {
      return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
   }

   public static Vec3i readi(FriendlyByteBuf buffer) {
      return new Vec3i(buffer.readInt(), buffer.readInt(), buffer.readInt());
   }
}
