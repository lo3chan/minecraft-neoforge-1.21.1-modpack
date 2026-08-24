package jeresources.api.restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jeresources.api.util.BiomeHelper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeRestriction {
   public static final BiomeRestriction NO_RESTRICTION = new BiomeRestriction();
   public static final BiomeRestriction TAIGA = new BiomeRestriction(Biomes.TAIGA);
   public static final BiomeRestriction JUNGLE = new BiomeRestriction(Biomes.JUNGLE);
   public static final BiomeRestriction PLAINS = new BiomeRestriction(Biomes.PLAINS);
   public static final BiomeRestriction SAVANNA = new BiomeRestriction(Biomes.SAVANNA);
   public static final BiomeRestriction ICE_SPIKES = new BiomeRestriction(Biomes.ICE_SPIKES);
   public static final BiomeRestriction THE_END = new BiomeRestriction(Biomes.THE_END);
   public static final BiomeRestriction BEACH = new BiomeRestriction(Biomes.BEACH);
   public static final BiomeRestriction FOREST = new BiomeRestriction(Biomes.FOREST);
   public static final BiomeRestriction OCEAN = new BiomeRestriction(Biomes.OCEAN);
   public static final BiomeRestriction DESERT = new BiomeRestriction(Biomes.DESERT);
   public static final BiomeRestriction RIVER = new BiomeRestriction(Biomes.RIVER);
   public static final BiomeRestriction SWAMP = new BiomeRestriction(Biomes.SWAMP);
   public static final BiomeRestriction MUSHROOM_FIELDS = new BiomeRestriction(Biomes.MUSHROOM_FIELDS);
   public static final BiomeRestriction NETHER_WASTES = new BiomeRestriction(Biomes.NETHER_WASTES);
   public static final BiomeRestriction DRIPSTONE_CAVES = new BiomeRestriction(Biomes.DRIPSTONE_CAVES);
   public static final BiomeRestriction BADLANDS = new BiomeRestriction(Biomes.BADLANDS);
   private List<Biome> biomes = new ArrayList<>();
   private Restriction.Type restrictionType;

   public BiomeRestriction() {
      this.restrictionType = Restriction.Type.NONE;
   }

   public BiomeRestriction(ResourceKey<Biome> biome) {
      this(BiomeHelper.getBiome(biome));
   }

   public BiomeRestriction(Biome biome) {
      this(Restriction.Type.WHITELIST, biome);
   }

   public BiomeRestriction(Restriction.Type restrictionType, Biome biome) {
      this(restrictionType, biome);
   }

   public BiomeRestriction(Biome biome, Biome... moreBiomes) {
      this(Restriction.Type.WHITELIST, biome, moreBiomes);
   }

   public BiomeRestriction(Restriction.Type restrictionType, Biome biome, Biome... moreBiomes) {
      this.restrictionType = restrictionType;
      switch (restrictionType) {
         case NONE:
            break;
         case WHITELIST:
            this.biomes.add(biome);
            this.biomes.addAll(Arrays.asList(moreBiomes));
            break;
         default:
            this.biomes = BiomeHelper.getAllBiomes();
            this.biomes.remove(biome);
            this.biomes.removeAll(Arrays.asList(moreBiomes));
      }
   }

   public BiomeRestriction(ResourceKey<Biome> biomeCategory, ResourceKey<Biome>... biomeCategories) {
      this(Restriction.Type.WHITELIST, biomeCategory, biomeCategories);
   }

   public BiomeRestriction(Restriction.Type restrictionType, ResourceKey<Biome> biomeCategory, ResourceKey<Biome>... biomeCategories) {
      this.restrictionType = restrictionType;
      switch (restrictionType) {
         case NONE:
            break;
         case WHITELIST:
            this.biomes = this.getBiomes(biomeCategory, biomeCategories);
            break;
         default:
            this.biomes = BiomeHelper.getAllBiomes();
            this.biomes.removeAll(this.getBiomes(biomeCategory, biomeCategories));
      }
   }

   private ArrayList<Biome> getBiomes(ResourceKey<Biome> biomeCategory, ResourceKey<Biome>... biomeCategories) {
      ArrayList<Biome> biomes = new ArrayList<>();
      biomes.addAll(BiomeHelper.getBiomes(biomeCategory));

      for (int i = 1; i < biomeCategories.length; i++) {
         ArrayList<Biome> newBiomes = new ArrayList<>();

         for (Biome biome : BiomeHelper.getBiomes(biomeCategories[i])) {
            if (biomes.remove(biome)) {
               newBiomes.add(biome);
            }
         }

         biomes = newBiomes;
      }

      return biomes;
   }

   public List<String> toStringList() {
      return this.biomes
         .stream()
         .filter(biome -> !biome.toString().equals(""))
         .map(biome -> "  " + I18n.get("biome." + biome.toString().replace(":", "."), new Object[0]))
         .collect(Collectors.toList());
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof BiomeRestriction other) ? false : other.biomes.size() == this.biomes.size() && other.biomes.containsAll(this.biomes);
   }

   public boolean isMergeAble(BiomeRestriction other) {
      return other.restrictionType == Restriction.Type.NONE
         || this.restrictionType != Restriction.Type.NONE && !this.biomes.isEmpty() && other.biomes.containsAll(this.biomes);
   }

   @Override
   public String toString() {
      return "Biomes: " + this.restrictionType + (this.restrictionType != Restriction.Type.NONE ? " - " + this.biomes.size() : "");
   }

   @Override
   public int hashCode() {
      return this.restrictionType.hashCode() ^ this.biomes.hashCode();
   }
}
