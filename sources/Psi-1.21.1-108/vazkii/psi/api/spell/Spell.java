package vazkii.psi.api.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

public final class Spell {
   public static final String TAG_SPELL_NAME = "spellName";
   public static final String TAG_UUID_MOST = "uuidMost";
   public static final String TAG_UUID_LEAST = "uuidLeast";
   public static final String TAG_MODS_REQUIRED = "modsRequired";
   public static final String TAG_MOD_NAME = "modName";
   public static final String TAG_MOD_VERSION = "modVersion";
   public static final StreamCodec<RegistryFriendlyByteBuf, Spell> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL,
      s -> true,
      ByteBufCodecs.STRING_UTF8,
      s -> s.name,
      Spell.ModInformation.STREAM_CODEC.apply(ByteBufCodecs.list()),
      Spell::getModInformationForCodec,
      ByteBufCodecs.VAR_LONG,
      s -> s.uuid.getMostSignificantBits(),
      ByteBufCodecs.VAR_LONG,
      s -> s.uuid.getLeastSignificantBits(),
      NeoForgeStreamCodecs.lazy(() -> SpellGrid.STREAM_CODEC),
      s -> s.grid,
      Spell::fromCodecData
   );
   private static final String TAG_VALID = "validSpell";
   public static final MapCodec<Spell> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.fieldOf("validSpell").forGetter(s -> true),
            Codec.STRING.fieldOf("spellName").forGetter(s -> s.name),
            Codec.list(Spell.ModInformation.CODEC.codec()).fieldOf("modsRequired").forGetter(Spell::getModInformationForCodec),
            Codec.LONG.fieldOf("uuidMost").forGetter(s -> s.uuid.getMostSignificantBits()),
            Codec.LONG.fieldOf("uuidLeast").forGetter(s -> s.uuid.getLeastSignificantBits()),
            Codec.lazyInitialized(SpellGrid.CODEC::codec).fieldOf("spellList").forGetter(s -> s.grid)
         )
         .apply(instance, Spell::fromCodecData)
   );
   public final SpellGrid grid = new SpellGrid(this);
   public String name = "";
   public UUID uuid = UUID.randomUUID();

   @Nullable
   public static Spell createFromNBT(CompoundTag cmp) {
      if (cmp != null && cmp.getBoolean("validSpell")) {
         Spell spell = new Spell();
         spell.readFromNBT(cmp);
         return spell;
      } else {
         return null;
      }
   }

   private static Spell fromCodecData(boolean valid, String spellName, List<Spell.ModInformation> modsRequired, long uuidMost, long uuidLeast, SpellGrid grid) {
      Spell spell = new Spell();
      spell.name = spellName;
      spell.uuid = new UUID(uuidMost, uuidLeast);
      spell.grid.gridData = new SpellPiece[9][9];

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece piece = grid.gridData[i][j];
            if (piece != null) {
               spell.grid.gridData[i][j] = piece.copyFromSpell(spell);
               spell.grid.gridData[i][j].x = i;
               spell.grid.gridData[i][j].y = j;
            }
         }
      }

      return spell;
   }

   @OnlyIn(Dist.CLIENT)
   public void draw(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      this.grid.draw(pPoseStack, buffers, light);
   }

   public void readFromNBT(CompoundTag cmp) {
      this.name = cmp.getString("spellName");
      if (cmp.contains("uuidMost")) {
         long uuidMost = cmp.getLong("uuidMost");
         long uuidLeast = cmp.getLong("uuidLeast");
         if (this.uuid.getMostSignificantBits() != uuidMost || this.uuid.getLeastSignificantBits() != uuidLeast) {
            this.uuid = new UUID(uuidMost, uuidLeast);
         }
      }

      this.grid.readFromNBT(cmp);
   }

   public Set<String> getPieceNamespaces() {
      Set<String> temp = Collections.newSetFromMap(new HashMap<>());

      for (SpellPiece[] gridDatum : this.grid.gridData) {
         for (SpellPiece spellPiece : gridDatum) {
            if (spellPiece != null) {
               temp.add(spellPiece.registryKey.getNamespace());
            }
         }
      }

      return temp;
   }

   private List<Spell.ModInformation> getModInformationForCodec() {
      List<Spell.ModInformation> info = new ArrayList<>();

      for (String namespace : this.getPieceNamespaces()) {
         Optional<? extends ModContainer> optionalMod = ModList.get().getModContainerById(namespace);
         if (!optionalMod.isEmpty()) {
            ModContainer mod = optionalMod.get();
            info.add(new Spell.ModInformation(mod.getModId(), mod.getModInfo().getVersion().toString()));
         }
      }

      info.sort(Comparator.comparing(i -> i.name));
      return info;
   }

   public void writeToNBT(CompoundTag cmp) {
      cmp.putBoolean("validSpell", true);
      cmp.putString("spellName", this.name);
      ListTag modList = new ListTag();

      for (String namespace : this.getPieceNamespaces()) {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("modName", namespace);
         if (ModList.get().getModContainerById(namespace).isPresent()) {
            nbt.putString("modVersion", ((ModContainer)ModList.get().getModContainerById(namespace).get()).getModInfo().getVersion().toString());
         }

         modList.add(nbt);
      }

      cmp.put("modsRequired", modList);
      cmp.putLong("uuidMost", this.uuid.getMostSignificantBits());
      cmp.putLong("uuidLeast", this.uuid.getLeastSignificantBits());
      this.grid.writeToNBT(cmp);
   }

   public Spell copy() {
      CompoundTag cmp = new CompoundTag();
      this.writeToNBT(cmp);
      return createFromNBT(cmp);
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof Spell o && Objects.equals(this.name, o.name) && Objects.equals(this.grid, o.grid);
   }

   @Override
   public int hashCode() {
      return this.name.hashCode() * 31 + this.grid.hashCode();
   }

   record ModInformation(String name, String version) {
      public static final MapCodec<Spell.ModInformation> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.fieldOf("modName").forGetter(Spell.ModInformation::name),
               Codec.STRING.fieldOf("modVersion").forGetter(Spell.ModInformation::version)
            )
            .apply(instance, Spell.ModInformation::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, Spell.ModInformation> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8, Spell.ModInformation::name, ByteBufCodecs.STRING_UTF8, Spell.ModInformation::version, Spell.ModInformation::new
      );
   }
}
