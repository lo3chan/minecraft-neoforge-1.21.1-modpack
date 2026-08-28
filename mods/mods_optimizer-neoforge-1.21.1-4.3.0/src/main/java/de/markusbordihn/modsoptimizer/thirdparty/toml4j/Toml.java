/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Keys;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.TomlParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Toml {
    private static final Gson DEFAULT_GSON = new Gson();
    private Map<String, Object> values = new HashMap<String, Object>();
    private final Toml defaults;

    public Toml() {
        this(null);
    }

    public Toml(Toml defaults) {
        this(defaults, new HashMap<String, Object>());
    }

    public Toml read(File file) {
        try {
            return this.read(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Toml read(InputStream inputStream) {
        return this.read(new InputStreamReader(inputStream));
    }

    public Toml read(Reader reader) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(reader);
            StringBuilder w = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                w.append(line).append('\n');
                line = bufferedReader.readLine();
            }
            this.read(w.toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                bufferedReader.close();
            }
            catch (IOException iOException) {}
        }
        return this;
    }

    public Toml read(Toml otherToml) {
        this.values = otherToml.values;
        return this;
    }

    public Toml read(String tomlString) throws IllegalStateException {
        Results results = TomlParser.run(tomlString);
        if (results.errors.hasErrors()) {
            throw new IllegalStateException(results.errors.toString());
        }
        this.values = results.consume();
        return this;
    }

    public String getString(String key) {
        return (String)this.get(key);
    }

    public String getString(String key, String defaultValue) {
        String val = this.getString(key);
        return val == null ? defaultValue : val;
    }

    public Long getLong(String key) {
        return (Long)this.get(key);
    }

    public Long getLong(String key, Long defaultValue) {
        Long val = this.getLong(key);
        return val == null ? defaultValue : val;
    }

    public <T> List<T> getList(String key) {
        List list = (List)this.get(key);
        return list;
    }

    public <T> List<T> getList(String key, List<T> defaultValue) {
        List<T> list = this.getList(key);
        return list != null ? list : defaultValue;
    }

    public Boolean getBoolean(String key) {
        return (Boolean)this.get(key);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean val = this.getBoolean(key);
        return val == null ? defaultValue : val;
    }

    public Date getDate(String key) {
        return (Date)this.get(key);
    }

    public Date getDate(String key, Date defaultValue) {
        Date val = this.getDate(key);
        return val == null ? defaultValue : val;
    }

    public Double getDouble(String key) {
        return (Double)this.get(key);
    }

    public Double getDouble(String key, Double defaultValue) {
        Double val = this.getDouble(key);
        return val == null ? defaultValue : val;
    }

    public Toml getTable(String key) {
        Map map = (Map)this.get(key);
        return map != null ? new Toml(null, map) : null;
    }

    public List<Toml> getTables(String key) {
        List tableArray = (List)this.get(key);
        if (tableArray == null) {
            return null;
        }
        ArrayList<Toml> tables = new ArrayList<Toml>();
        for (Map table : tableArray) {
            tables.add(new Toml(null, table));
        }
        return tables;
    }

    public boolean contains(String key) {
        return this.get(key) != null;
    }

    public boolean containsPrimitive(String key) {
        Object object = this.get(key);
        return object != null && !(object instanceof Map) && !(object instanceof List);
    }

    public boolean containsTable(String key) {
        Object object = this.get(key);
        return object != null && object instanceof Map;
    }

    public boolean containsTableArray(String key) {
        Object object = this.get(key);
        return object != null && object instanceof List;
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public <T> T to(Class<T> targetClass) {
        JsonElement json = DEFAULT_GSON.toJsonTree(this.toMap());
        if (targetClass == JsonElement.class) {
            return targetClass.cast(json);
        }
        return (T)DEFAULT_GSON.fromJson(json, targetClass);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> valuesCopy = new HashMap<String, Object>(this.values);
        if (this.defaults != null) {
            for (Map.Entry<String, Object> entry : this.defaults.values.entrySet()) {
                if (valuesCopy.containsKey(entry.getKey())) continue;
                valuesCopy.put(entry.getKey(), entry.getValue());
            }
        }
        return valuesCopy;
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        LinkedHashSet<Map.Entry<String, Object>> entries = new LinkedHashSet<Map.Entry<String, Object>>();
        for (Map.Entry<String, Object> entry : this.values.entrySet()) {
            Class<?> entryClass = entry.getValue().getClass();
            if (Map.class.isAssignableFrom(entryClass)) {
                entries.add(new Entry(entry.getKey(), this.getTable(entry.getKey())));
                continue;
            }
            if (List.class.isAssignableFrom(entryClass)) {
                List value = (List)entry.getValue();
                if (!value.isEmpty() && value.get(0) instanceof Map) {
                    entries.add(new Entry(entry.getKey(), this.getTables(entry.getKey())));
                    continue;
                }
                entries.add(new Entry(entry.getKey(), value));
                continue;
            }
            entries.add(new Entry(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    private Object get(String key) {
        Keys.Key[] keys;
        if (this.values.containsKey(key)) {
            return this.values.get(key);
        }
        HashMap<String, Object> current = new HashMap<String, Object>(this.values);
        for (Keys.Key k : keys = Keys.split(key)) {
            if (k.index == -1 && current instanceof Map && ((Map)current).containsKey(k.path)) {
                return ((Map)current).get(k.path);
            }
            current = ((Map)current).get(k.name);
            if (k.index > -1 && current != null) {
                if (k.index >= ((List)((Object)current)).size()) {
                    return null;
                }
                current = ((List)((Object)current)).get(k.index);
            }
            if (current != null) continue;
            return this.defaults != null ? this.defaults.get(key) : null;
        }
        return current;
    }

    private Toml(Toml defaults, Map<String, Object> values) {
        this.values = values;
        this.defaults = defaults;
    }

    private class Entry
    implements Map.Entry<String, Object> {
        private final String key;
        private final Object value;

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public Object setValue(Object value) {
            throw new UnsupportedOperationException("TOML entry values cannot be changed.");
        }

        private Entry(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}

