/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  org.apache.commons.io.FileUtils
 */
package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.util.HttpRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.io.FileUtils;

public final class ConfigCloth {
    private static final String DIR = "config/physicsmod";
    private static final String CONFIG = "physics_cloth_config.json";
    public static final String YOURSELF = "physicsmod:yourself";
    public static final String OTHER_PLAYER = "physicsmod:player:";
    public static final String ALL_PLAYER = "minecraft:player";
    private static volatile String version;
    private static Map<String, Map<String, ClothList>> entityCustomizations;
    public static volatile boolean clothUpToDate;
    public static volatile boolean isChangingPlayer;

    public static Map<String, ClothList> getCustomizationParts(Entity entity) {
        EntityType entityType = entity.getType();
        String uuid = entity.getStringUUID();
        Map<String, ClothList> customization = entityCustomizations.get(uuid);
        if (customization != null) {
            return customization;
        }
        if (entity == Minecraft.getInstance().player) {
            return entityCustomizations.get(YOURSELF);
        }
        if (entityType == EntityType.PLAYER && entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer)entity;
            customization = entityCustomizations.get(OTHER_PLAYER + player.getGameProfile().getName());
            if (customization == null) {
                return entityCustomizations.get(ALL_PLAYER);
            }
            return customization;
        }
        String entityLocation = EntityType.getKey((EntityType)entityType).toString();
        return entityCustomizations.get(entityLocation);
    }

    public static Map<String, Map<String, ClothList>> getEntityCustomizations() {
        return entityCustomizations;
    }

    public static Map<String, ClothList> getCustomizationParts(String selection) {
        return entityCustomizations.get(selection);
    }

    public static void setCustomizationParts(String selection, Map<String, ClothList> parts) {
        if (parts.isEmpty()) {
            entityCustomizations.remove(selection);
        } else {
            entityCustomizations.put(selection, parts);
        }
    }

    @Nullable
    public static ClothList getPart(Entity entity, String part) {
        Map<String, ClothList> customizations = ConfigCloth.getCustomizationParts(entity);
        if (customizations == null) {
            return null;
        }
        return customizations.get(part);
    }

    public static boolean hasCategory(Entity entity, String category) {
        Map<String, ClothList> customizations = ConfigCloth.getCustomizationParts(entity);
        if (customizations == null) {
            return false;
        }
        for (ClothList clothList : customizations.values()) {
            for (String clothPiece : clothList.clothPieces) {
                Cloth cloth = PhysicsMod.cloth.get(clothPiece);
                if (cloth == null || !cloth.rules.getCategory().equals(category)) continue;
                return true;
            }
        }
        return false;
    }

    public static Cloth getCategory(Entity entity, String category) {
        Map<String, ClothList> customizations = ConfigCloth.getCustomizationParts(entity);
        if (customizations == null) {
            return null;
        }
        for (ClothList clothList : customizations.values()) {
            for (String clothPiece : clothList.clothPieces) {
                Cloth cloth = PhysicsMod.cloth.get(clothPiece);
                if (cloth == null || !cloth.rules.getCategory().equals(category)) continue;
                return cloth;
            }
        }
        return null;
    }

    @Nullable
    public static String getCategory(String entity, String category) {
        Map<String, ClothList> customizations = ConfigCloth.getCustomizationParts(entity);
        if (customizations == null) {
            return null;
        }
        for (ClothList clothList : customizations.values()) {
            for (String clothPiece : clothList.clothPieces) {
                Cloth cloth = PhysicsMod.cloth.get(clothPiece);
                if (cloth == null || !cloth.rules.getCategory().equals(category)) continue;
                return clothPiece;
            }
        }
        return null;
    }

    public static Map<String, ClothList> resetCustomization(String entity) {
        return entityCustomizations.remove(entity);
    }

    public static void setCategory(String entity, String category, @Nullable String clothPiece) {
        Map customizations = ConfigCloth.getCustomizationParts(entity);
        if (customizations != null) {
            for (ClothList clothList : customizations.values()) {
                Iterator<String> it = clothList.clothPieces.iterator();
                while (it.hasNext()) {
                    String piece = it.next();
                    Cloth cloth = PhysicsMod.cloth.get(piece);
                    if (cloth == null || !cloth.rules.getCategory().equals(category)) continue;
                    it.remove();
                }
            }
        }
        if (clothPiece != null) {
            customizations = entityCustomizations.computeIfAbsent(entity, key -> new Object2ObjectOpenHashMap());
            Cloth cloth = PhysicsMod.cloth.get(clothPiece);
            String part = cloth.rules.getAllowedParts().iterator().next();
            ClothList clothList = customizations.computeIfAbsent(part, key -> new ClothList());
            clothList.add(clothPiece);
            for (Map.Entry entry : customizations.entrySet()) {
                ClothList list = (ClothList)entry.getValue();
                Iterator<String> it = list.getClothPieces().iterator();
                while (it.hasNext()) {
                    String piece = it.next();
                    Cloth toRemove = PhysicsMod.cloth.get(piece);
                    if (toRemove == null || toRemove == cloth || !cloth.rules.isBreaking(toRemove) && !toRemove.rules.isBreaking(cloth)) continue;
                    it.remove();
                }
            }
        }
        ConfigCloth.cleanUpEntries();
    }

    private static void cleanUpEntries() {
        Iterator<Map.Entry<String, Map<String, ClothList>>> itEntities = entityCustomizations.entrySet().iterator();
        while (itEntities.hasNext()) {
            Map.Entry<String, Map<String, ClothList>> entryEntity = itEntities.next();
            String entity = entryEntity.getKey();
            Map<String, ClothList> customizations = entryEntity.getValue();
            Iterator<Map.Entry<String, ClothList>> itMap = customizations.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry<String, ClothList> entry = itMap.next();
                ClothList clothList = entry.getValue();
                if (!clothList.getClothPieces().isEmpty()) continue;
                itMap.remove();
            }
            if (!customizations.isEmpty()) continue;
            itEntities.remove();
        }
    }

    @Nullable
    public static ClothList getPart(String selection, String part) {
        Map<String, ClothList> customizations = entityCustomizations.get(selection);
        if (customizations == null) {
            return null;
        }
        return customizations.get(part);
    }

    public static void addPart(String entity, String part, String cloth) {
        Map customizations = entityCustomizations.computeIfAbsent(entity, key -> new Object2ObjectOpenHashMap());
        ClothList clothList = customizations.computeIfAbsent(part, key -> new ClothList());
        clothList.add(cloth);
    }

    public static void init() {
        ConfigCloth.downloadClothModels();
    }

    public static UUID getMinecraftUUID() {
        if (Minecraft.getInstance().getUser() != null && Minecraft.getInstance().getUser().getProfileId() != null) {
            return Minecraft.getInstance().getUser().getProfileId();
        }
        return null;
    }

    public static String getMinecraftUUIDString() {
        if (Minecraft.getInstance().getUser() != null && Minecraft.getInstance().getUser().getProfileId() != null) {
            return Minecraft.getInstance().getUser().getProfileId().toString();
        }
        return null;
    }

    private static void downloadClothModels() {
        Thread clothNetworking = new Thread(() -> {
            while (true) {
                try {
                    String currentVersion = HttpRequest.get("http://customize.minecraftphysicsmod.com/version");
                    File destDir = new File(".physics_mod_cache");
                    if (!version.equals(currentVersion) || !destDir.exists()) {
                        version = currentVersion;
                        while (true) {
                            try {
                                if (destDir.exists()) {
                                    FileUtils.forceDelete((File)destDir);
                                }
                                if (!destDir.exists()) {
                                    destDir.mkdirs();
                                }
                                try (InputStream stream = HttpRequest.getStream("http://customize.minecraftphysicsmod.com/cloth.zip");){
                                    byte[] buffer = new byte[1024];
                                    ZipInputStream zis = new ZipInputStream(stream);
                                    ZipEntry zipEntry = zis.getNextEntry();
                                    while (zipEntry != null) {
                                        File newFile = ConfigCloth.newFile(destDir, zipEntry);
                                        if (zipEntry.isDirectory()) {
                                            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                                                zis.close();
                                                throw new IOException("Failed to create directory " + String.valueOf(newFile));
                                            }
                                        } else {
                                            int len;
                                            File parent = newFile.getParentFile();
                                            if (!parent.isDirectory() && !parent.mkdirs()) {
                                                zis.close();
                                                throw new IOException("Failed to create directory " + String.valueOf(parent));
                                            }
                                            FileOutputStream fos = new FileOutputStream(newFile);
                                            while ((len = zis.read(buffer)) > 0) {
                                                fos.write(buffer, 0, len);
                                            }
                                            fos.close();
                                        }
                                        zipEntry = zis.getNextEntry();
                                    }
                                    zis.closeEntry();
                                    zis.close();
                                }
                                clothUpToDate = true;
                                PhysicsMod.reloadCloth = true;
                                return;
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Thread.sleep(120000L);
                                continue;
                            }
                            break;
                        }
                    }
                    clothUpToDate = true;
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(60000L);
                        continue;
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                        continue;
                    }
                }
                break;
            }
        });
        clothNetworking.setName("Cloth Network Thread");
        clothNetworking.setDaemon(true);
        clothNetworking.start();
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        JsonObject entityCustomizationsElement = new JsonObject();
        for (Map.Entry<String, Map<String, ClothList>> entry : entityCustomizations.entrySet()) {
            Map<String, ClothList> partAndClothPieces = entry.getValue();
            JsonObject partAndClothElement = new JsonObject();
            for (Map.Entry<String, ClothList> partAndClothPiece : partAndClothPieces.entrySet()) {
                JsonArray cloth = new JsonArray();
                for (String clothPiece : partAndClothPiece.getValue().getClothPieces()) {
                    cloth.add(clothPiece);
                }
                partAndClothElement.add(partAndClothPiece.getKey(), (JsonElement)cloth);
            }
            entityCustomizationsElement.add(entry.getKey(), (JsonElement)partAndClothElement);
        }
        config.add("entityCustomizations", (JsonElement)entityCustomizationsElement);
        config.add("version", (JsonElement)new JsonPrimitive(version));
        return config;
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_cloth_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigCloth.createConfig();
        try {
            configFile.createNewFile();
            try (FileWriter writer = new FileWriter(configFile);){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson((JsonElement)config, (Appendable)writer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPlayerUpdate() {
        Map<String, ClothList> customization = ConfigCloth.getCustomizationParts(YOURSELF);
        JsonArray array = new JsonArray();
        if (customization != null) {
            for (Map.Entry<String, ClothList> entry : customization.entrySet()) {
                for (String part : entry.getValue().getClothPieces()) {
                    array.add(part);
                }
            }
        }
        return array.toString();
    }

    static {
        JsonObject config;
        File configFile;
        version = "";
        entityCustomizations = new Object2ObjectOpenHashMap();
        clothUpToDate = false;
        isChangingPlayer = false;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_cloth_config.json")).exists()) {
            config = ConfigCloth.createConfig();
            try {
                configFile.createNewFile();
                try (FileWriter writer = new FileWriter(configFile);){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    gson.toJson((JsonElement)config, (Appendable)writer);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Gson gson = new Gson();
            config = ConfigCloth.createConfig();
            try {
                config = (JsonObject)gson.fromJson((Reader)new FileReader(configFile), JsonObject.class);
            }
            catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            JsonObject entityCustomizationsElement = config.get("entityCustomizations").getAsJsonObject();
            for (Map.Entry entry : entityCustomizationsElement.entrySet()) {
                String entity = (String)entry.getKey();
                JsonObject partAndClothElement = ((JsonElement)entry.getValue()).getAsJsonObject();
                Object2ObjectOpenHashMap partAndClothPieces = new Object2ObjectOpenHashMap();
                for (Map.Entry partEntry : partAndClothElement.entrySet()) {
                    String part = (String)partEntry.getKey();
                    JsonArray cloth = ((JsonElement)partEntry.getValue()).getAsJsonArray();
                    ClothList clothList = new ClothList();
                    for (int i = 0; i < cloth.size(); ++i) {
                        clothList.add(cloth.get(i).getAsString());
                    }
                    partAndClothPieces.put(part, clothList);
                }
                if (partAndClothPieces.isEmpty()) continue;
                entityCustomizations.put(entity, (Map<String, ClothList>)partAndClothPieces);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            version = config.get("version").getAsString();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static class ClothList {
        private Set<String> clothPieces = new ObjectOpenHashSet();

        public ClothList(String clothPiece) {
            this.clothPieces.add(clothPiece);
        }

        public ClothList() {
        }

        public ClothList add(String clothPiece) {
            this.clothPieces.add(clothPiece);
            return this;
        }

        public Set<String> getClothPieces() {
            return this.clothPieces;
        }

        public ClothList copy() {
            ClothList list = new ClothList();
            for (String piece : this.clothPieces) {
                list.clothPieces.add(piece);
            }
            return list;
        }

        public int hashCode() {
            return Objects.hash(this.clothPieces);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ClothList other = (ClothList)obj;
            return Objects.equals(this.clothPieces, other.clothPieces);
        }
    }
}

