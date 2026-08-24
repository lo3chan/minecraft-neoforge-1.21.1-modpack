package at.petrak.paucal.api.contrib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class Contributor {
   private final UUID uuid;
   private final int level;
   private final boolean isDev;
   private final float pitchCenter;
   private final float pitchVariance;
   private final List<HeadpatSpec> headpats;
   private final JsonObject otherVals;

   @Internal
   public Contributor(UUID uuid, JsonObject cfg) {
      this.uuid = uuid;
      this.otherVals = cfg;
      this.level = this.getInt("paucal:contributor_level", 0);
      this.isDev = this.getBool("paucal:is_dev", false);
      this.pitchCenter = this.getFloat("paucal:pat_pitch", 1.0F);
      this.pitchVariance = this.getFloat("paucal:pat_variance", 0.5F);
      JsonElement patsRaw = this.otherVals.get("paucal:pat_sound");
      this.headpats = HeadpatSpec.loadFromJson(patsRaw);
   }

   public int getLevel() {
      return this.level;
   }

   public boolean isDev() {
      return this.isDev;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   @Internal
   public Collection<String> neededGithubSounds() {
      ArrayList<String> out = new ArrayList<>();

      for (HeadpatSpec hp : this.headpats) {
         if (hp.type == HeadpatSpec.Type.GITHUB) {
            out.add(hp.location);
         }
      }

      return out;
   }

   public boolean doHeadpatSound(Vec3 patteePos, @Nullable Player patter, Level level) {
      if (this.headpats.isEmpty()) {
         return false;
      } else {
         if (level instanceof ServerLevel slevel) {
            int idx = level.random.nextInt(this.headpats.size());
            HeadpatSpec patspec = this.headpats.get(idx);
            float pitch = this.pitchCenter + (float)(Math.random() - 0.5) * this.pitchVariance;
            List<ServerPlayer> closeBy = slevel.getPlayers(pl -> pl.position().distanceToSqr(patteePos) <= 4096.0);
            NetworkManager.sendToPlayers(closeBy, patspec.makePacket(patteePos, pitch, patter));
         }

         return true;
      }
   }

   @Nullable
   public String getString(String key) {
      return this.getString(key, null);
   }

   public String getString(String key, String fallback) {
      return GsonHelper.getAsString(this.otherVals, key, fallback);
   }

   @Nullable
   public Integer getInt(String key) {
      return this.otherVals.has(key) ? GsonHelper.getAsInt(this.otherVals, key) : null;
   }

   public int getInt(String key, int fallback) {
      return GsonHelper.getAsInt(this.otherVals, key, fallback);
   }

   @Nullable
   public Float getFloat(String key) {
      return this.otherVals.has(key) ? GsonHelper.getAsFloat(this.otherVals, key) : null;
   }

   public float getFloat(String key, float fallback) {
      return GsonHelper.getAsFloat(this.otherVals, key, fallback);
   }

   @Nullable
   public Boolean getBool(String key) {
      return this.otherVals.has(key) ? GsonHelper.getAsBoolean(this.otherVals, key) : null;
   }

   public boolean getBool(String key, boolean fallback) {
      return GsonHelper.getAsBoolean(this.otherVals, key, fallback);
   }

   public Set<String> allKeys() {
      return this.otherVals.keySet();
   }

   public JsonObject otherVals() {
      return this.otherVals;
   }
}
