/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 */
package mezz.jei.common.config.file;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;

public class JsonArrayWriter {
    private final Gson gson = new Gson();
    private final BufferedWriter out;
    private boolean firstLine = true;

    public static JsonArrayWriter start(BufferedWriter out) throws IOException {
        JsonArrayWriter writer = new JsonArrayWriter(out);
        out.write("[\n");
        return writer;
    }

    private JsonArrayWriter(BufferedWriter out) {
        this.out = out;
    }

    public void add(JsonElement line) throws IOException {
        if (this.firstLine) {
            this.out.write("  ");
            this.firstLine = false;
        } else {
            this.out.write(",\n  ");
        }
        this.gson.toJson(line, (Appendable)this.out);
    }

    public void end() throws IOException {
        this.out.write("\n]");
        this.out.flush();
    }
}

