/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package net.diebuddies.physics.verlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Set;
import net.diebuddies.physics.verlet.Cloth;

public class ClothRules {
    public static final String UNKNOWN_CATEGORY = "Unknown";
    public static final String BREAKS_ALL = "all";
    private String category = "Unknown";
    private Set<String> allowedParts = new ObjectOpenHashSet();
    private Set<String> breaks = new ObjectOpenHashSet();
    private Set<String> hiddenParts = new ObjectOpenHashSet();
    private Set<String> ignoreParts = new ObjectOpenHashSet();
    private Set<String> hideArmor = new ObjectOpenHashSet();
    private String specialTexture;
    private boolean dynamic = true;
    private boolean local;

    public boolean canUseOn(String part) {
        if (this.allowedParts.isEmpty()) {
            return true;
        }
        return this.allowedParts.contains(part);
    }

    public boolean isBreaking(Cloth cloth) {
        ClothRules rules = cloth.rules;
        return this.breaks.contains(rules.getCategory()) || this.breaks.contains(BREAKS_ALL);
    }

    public static ClothRules load(File file, boolean local) {
        if (!file.exists()) {
            return new ClothRules();
        }
        Gson gson = new Gson();
        ClothRules rules = new ClothRules();
        rules.local = local;
        try {
            int i;
            JsonObject config = (JsonObject)gson.fromJson((Reader)new FileReader(file), JsonObject.class);
            if (config.has("parts")) {
                JsonArray parts = config.get("parts").getAsJsonArray();
                for (i = 0; i < parts.size(); ++i) {
                    rules.allowedParts.add(parts.get(i).getAsString());
                }
            }
            if (config.has("dynamic")) {
                rules.dynamic = config.get("dynamic").getAsBoolean();
            }
            if (config.has("category")) {
                rules.category = config.get("category").getAsString();
            }
            if (config.has("breaks")) {
                JsonArray breaks = config.get("breaks").getAsJsonArray();
                for (i = 0; i < breaks.size(); ++i) {
                    rules.breaks.add(breaks.get(i).getAsString());
                }
            }
            if (config.has("hideParts")) {
                JsonArray hideParts = config.get("hideParts").getAsJsonArray();
                for (i = 0; i < hideParts.size(); ++i) {
                    rules.hiddenParts.add(hideParts.get(i).getAsString());
                }
            }
            if (config.has("ignoreParts")) {
                JsonArray ignoreParts = config.get("ignoreParts").getAsJsonArray();
                for (i = 0; i < ignoreParts.size(); ++i) {
                    rules.ignoreParts.add(ignoreParts.get(i).getAsString());
                }
            }
            if (config.has("hideArmor")) {
                JsonArray hideArmor = config.get("hideArmor").getAsJsonArray();
                for (i = 0; i < hideArmor.size(); ++i) {
                    rules.hideArmor.add(hideArmor.get(i).getAsString());
                }
            }
            if (config.has("specialTexture")) {
                rules.specialTexture = config.get("specialTexture").getAsString();
            }
        }
        catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return rules;
    }

    public String getSpecialTexture() {
        return this.specialTexture;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public Set<String> getAllowedParts() {
        return this.allowedParts;
    }

    public String getCategory() {
        return this.category;
    }

    public Set<String> getHiddenParts() {
        return this.hiddenParts;
    }

    public Set<String> getIgnoreParts() {
        return this.ignoreParts;
    }

    public Set<String> getHiddenArmorPieces() {
        return this.hideArmor;
    }

    public boolean isLocal() {
        return this.local;
    }
}

