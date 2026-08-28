/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Vector2d
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector4d
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.diebuddies.opengl.Replacement;
import net.diebuddies.render.shader.ShaderResourceProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;

public class Shader {
    private String vertexLocation;
    private String fragmentLocation;
    private String geometryLocation;
    private int vertex;
    private int fragment;
    private int geometry;
    protected int linked;
    protected Object2IntMap<String> uniformLocations;
    protected Object2IntMap<String> attributeLocations;
    protected boolean destroyed;
    private Replacement[] replacements;

    public Shader(String vertex, String fragment, String geometry, Object2IntMap<String> attributeLocations, Replacement ... replacements) {
        this.attributeLocations = attributeLocations;
        if (this.attributeLocations == null) {
            this.attributeLocations = new Object2IntOpenHashMap();
        }
        this.attributeLocations.defaultReturnValue(-1);
        StringBuilder sb = new StringBuilder();
        sb.append(vertex).append(fragment).append(geometry);
        if (replacements != null) {
            for (int i = 0; i < replacements.length; ++i) {
                Replacement r = replacements[i];
                sb.append(r.key);
                sb.append(r.value);
                sb.append(Integer.toString(r.stage));
            }
        }
        this.vertexLocation = vertex;
        this.fragmentLocation = fragment;
        this.geometryLocation = geometry;
        this.replacements = replacements;
        this.loadShader();
    }

    private void loadShader() {
        this.uniformLocations = new Object2IntOpenHashMap();
        this.uniformLocations.defaultReturnValue(-1);
        String vertexShader = Shader.loadShaderFile(this.vertexLocation, 1, this.replacements);
        String fragmentShader = Shader.loadShaderFile(this.fragmentLocation, 3, this.replacements);
        String geometryShader = null;
        if (this.geometryLocation != null) {
            geometryShader = Shader.loadShaderFile(this.geometryLocation, 2, this.replacements);
        }
        this.loadVertexShader(this.vertexLocation, vertexShader);
        this.loadFragmentShader(this.fragmentLocation, fragmentShader);
        if (this.geometryLocation == null) {
            this.linkShader(this.vertex, this.fragment);
        } else {
            this.loadGeometryShader(this.geometryLocation, geometryShader);
            this.linkShader(this.vertex, this.fragment, this.geometry);
        }
        this.bindShader();
        this.getAllUniformLocations();
    }

    public Shader(String vertex, String fragment, String geometry) {
        this(vertex, fragment, geometry, null, (Replacement[])null);
    }

    public Shader(String vertex, String fragment, Replacement ... replacements) {
        this(vertex, fragment, (String)null, (Object2IntMap<String>)null, replacements);
    }

    public Shader(String vertex, String fragment) {
        this(vertex, fragment, (String)null, (Object2IntMap<String>)null, (Replacement[])null);
    }

    public void bindShader() {
        GL32C.glUseProgram((int)this.linked);
    }

    public void bind() {
        this.bindShader();
    }

    public static void unbind() {
        GL32C.glUseProgram((int)0);
    }

    protected void getAllUniformLocations() {
        int count = GL32C.glGetProgrami((int)this.linked, (int)35718);
        for (int i = 0; i < count; ++i) {
            int arraySize = 1;
            try (MemoryStack stack = MemoryStack.stackPush();){
                IntBuffer size = stack.mallocInt(1);
                IntBuffer type = stack.mallocInt(1);
                GL32C.glGetActiveUniform((int)this.linked, (int)i, (IntBuffer)size, (IntBuffer)type);
                arraySize = size.get();
            }
            String name = GL32C.glGetActiveUniformName((int)this.linked, (int)i);
            if (arraySize > 1) {
                if (name.contains("[0]")) {
                    name = name.substring(0, name.length() - 3);
                }
                for (int j = 0; j < arraySize; ++j) {
                    StringBuilder sb = new StringBuilder(name);
                    String currentName = sb.append("[").append(Integer.toString(j)).append("]").toString();
                    int location = GL32C.glGetUniformLocation((int)this.linked, (CharSequence)currentName);
                    this.uniformLocations.put((Object)currentName, location);
                }
                continue;
            }
            int location = GL32C.glGetUniformLocation((int)this.linked, (CharSequence)name);
            this.uniformLocations.put((Object)name, location);
        }
    }

    public void bindAttributes() {
        for (Object2IntMap.Entry entry : this.attributeLocations.object2IntEntrySet()) {
            String name = (String)entry.getKey();
            int location = entry.getIntValue();
            this.bindAttributeOld(name, location);
        }
    }

    protected void bindFragData() {
    }

    public static String loadShaderFile(String filename, int stage, Replacement ... replacements) {
        String shader = null;
        try {
            shader = Shader.loadShaderFile(filename);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't find shader file! " + filename);
            return null;
        }
        if (replacements != null) {
            for (Replacement replacement : replacements) {
                if (replacement.stage != 0 && stage != replacement.stage) continue;
                shader = shader.replace(replacement.key, replacement.value);
            }
        }
        return shader;
    }

    public void loadVertexShader(String filename, String shader) {
        this.vertex = this.createShader(filename, shader, 35633);
    }

    public void loadFragmentShader(String filename, String shader) {
        this.fragment = this.createShader(filename, shader, 35632);
    }

    public void loadGeometryShader(String filename, String shader) {
        this.geometry = this.createShader(filename, shader, 36313);
    }

    private int createShader(String filename, String shader, int type) {
        int shaderId = GL32C.glCreateShader((int)type);
        if (shaderId == 0) {
            System.err.println("Couldn't create shader! " + filename);
            return 0;
        }
        GL32C.glShaderSource((int)shaderId, (CharSequence)shader);
        GL32C.glCompileShader((int)shaderId);
        if (GL32C.glGetShaderi((int)shaderId, (int)35713) == 0) {
            System.err.println(filename + " compile error:\n" + Shader.getShaderCompileInfo(shaderId));
        }
        return shaderId;
    }

    private static String getShaderCompileInfo(int shaderId) {
        return GL32C.glGetShaderInfoLog((int)shaderId);
    }

    private static String getProgramCompileInfo(int programId) {
        return GL32C.glGetProgramInfoLog((int)programId);
    }

    private static String loadShaderFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Shader.processResourceAsStream(filename)));){
            String line;
            StringBuilder shader = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#include")) {
                    shader.append(Shader.loadShaderFile(line.split(" ")[1].trim())).append("\n");
                    continue;
                }
                shader.append(line).append("\n");
            }
            String string = shader.toString();
            return string;
        }
    }

    private static boolean isResourceUrlValid(String string, URL url) throws IOException {
        return url != null && (url.getProtocol().equals("jar") || Shader.validatePath(new File(url.getFile()), string));
    }

    private static boolean validatePath(File file, String string) throws IOException {
        String canonicalPath = file.getCanonicalPath();
        return canonicalPath.endsWith(string);
    }

    private static InputStream processResourceAsStream(String path) {
        try {
            URL url = ShaderResourceProvider.class.getResource(path);
            if (Shader.isResourceUrlValid(path, url)) {
                return url.openStream();
            }
            return ShaderResourceProvider.class.getResourceAsStream(path);
        }
        catch (IOException iOException) {
            return ShaderResourceProvider.class.getResourceAsStream(path);
        }
    }

    public void linkShader(int ... shader) {
        int i;
        this.linked = GL32C.glCreateProgram();
        for (i = 0; i < shader.length; ++i) {
            GL32C.glAttachShader((int)this.linked, (int)shader[i]);
        }
        this.bindAttributes();
        this.bindFragData();
        GL32C.glLinkProgram((int)this.linked);
        if (GL32C.glGetProgrami((int)this.linked, (int)35714) == 0) {
            System.err.println("linking error (" + this.vertexLocation + ", " + this.fragmentLocation + ", " + this.geometryLocation + "):\n" + Shader.getProgramCompileInfo(this.linked));
        }
        this.validate();
        for (i = 0; i < shader.length; ++i) {
            GL32C.glDetachShader((int)this.linked, (int)shader[i]);
            GL32C.glDeleteShader((int)shader[i]);
        }
    }

    public void validate() {
        GL32C.glValidateProgram((int)this.linked);
        if (GL32C.glGetProgrami((int)this.linked, (int)35715) == 0) {
            System.err.println("validation error (" + this.vertexLocation + ", " + this.fragmentLocation + ", " + this.geometryLocation + "):\n" + Shader.getProgramCompileInfo(this.linked));
        }
    }

    public void setUniform1(int location, FloatBuffer value) {
        if (location == -1) {
            return;
        }
        GL32C.glUniform1fv((int)location, (FloatBuffer)value);
    }

    public void setUniform1(int location, float value) {
        if (location == -1) {
            return;
        }
        GL32C.glUniform1f((int)location, (float)value);
    }

    public void setUniform(int location, float[] value) {
        if (location == -1) {
            return;
        }
        if (value.length == 4) {
            GL32C.glUniform4fv((int)location, (float[])value);
        } else if (value.length == 3) {
            GL32C.glUniform3fv((int)location, (float[])value);
        } else if (value.length == 2) {
            GL32C.glUniform2fv((int)location, (float[])value);
        } else if (value.length == 1) {
            GL32C.glUniform1fv((int)location, (float[])value);
        }
    }

    public void setUniform1(int location, int value) {
        if (location == -1) {
            return;
        }
        GL32C.glUniform1i((int)location, (int)value);
    }

    public void setUniform2(int location, FloatBuffer values) {
        if (location == -1) {
            return;
        }
        GL32C.glUniform2fv((int)location, (FloatBuffer)values);
    }

    public void setUniform2(int location, Vector2f vector) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector2dBuffer = stack.mallocFloat(2);
            GL32C.glUniform2fv((int)location, (FloatBuffer)vector2dBuffer.put(0, vector.x).put(1, vector.y));
        }
    }

    public void setUniform2(int location, float v0, float v1) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector2dBuffer = stack.mallocFloat(2);
            GL32C.glUniform2fv((int)location, (FloatBuffer)vector2dBuffer.put(0, v0).put(1, v1));
        }
    }

    public void setUniform2(int location, Vector2d vector) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector2dBuffer = stack.mallocFloat(2);
            GL32C.glUniform2fv((int)location, (FloatBuffer)vector2dBuffer.put(0, (float)vector.x).put(1, (float)vector.y));
        }
    }

    public void setUniform3(int location, Vector3d vector) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector3dBuffer = stack.mallocFloat(3);
            GL32C.glUniform3fv((int)location, (FloatBuffer)vector3dBuffer.put(0, (float)vector.x).put(1, (float)vector.y).put(2, (float)vector.z));
        }
    }

    public void setUniform3(int location, Vec3 vector) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector3dBuffer = stack.mallocFloat(3);
            GL32C.glUniform3fv((int)location, (FloatBuffer)vector3dBuffer.put(0, (float)vector.x).put(1, (float)vector.y).put(2, (float)vector.z));
        }
    }

    public void setUniform3(int location, Vector3f vector) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector3dBuffer = stack.mallocFloat(3);
            GL32C.glUniform3fv((int)location, (FloatBuffer)vector3dBuffer.put(0, vector.x()).put(1, vector.y()).put(2, vector.z()));
        }
    }

    public void setUniform3(int location, FloatBuffer values) {
        if (location == -1) {
            return;
        }
        GL32C.glUniform3fv((int)location, (FloatBuffer)values);
    }

    public void setUniform3(int location, float x, float y, float z) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector3dBuffer = stack.mallocFloat(3);
            GL32C.glUniform3fv((int)location, (FloatBuffer)vector3dBuffer.put(0, x).put(1, y).put(2, z));
        }
    }

    public void setUniform4(String name, FloatBuffer values) {
        int location = this.getUniformLocation(name);
        if (location == -1) {
            return;
        }
        GL32C.glUniform4fv((int)location, (FloatBuffer)values);
    }

    public void setUniform4(int location, Vector4d vector) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector4dBuffer = stack.mallocFloat(4);
            GL32C.glUniform4fv((int)location, (FloatBuffer)vector4dBuffer.put(0, (float)vector.x).put(1, (float)vector.y).put(2, (float)vector.z).put(3, (float)vector.w));
        }
    }

    public void setUniform4(int location, float x, float y, float z, float w) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer vector4dBuffer = stack.mallocFloat(4);
            GL32C.glUniform4fv((int)location, (FloatBuffer)vector4dBuffer.put(0, x).put(1, y).put(2, z).put(3, w));
        }
    }

    public void setUniformMatrix2(String name, FloatBuffer values, boolean transpose) {
        int location = this.getUniformLocation(name);
        if (location == -1) {
            return;
        }
        GL32C.glUniformMatrix2fv((int)location, (boolean)transpose, (FloatBuffer)values);
    }

    public void setUniformMatrix3(String name, FloatBuffer values, boolean transpose) {
        int location = this.getUniformLocation(name);
        if (location == -1) {
            return;
        }
        GL32C.glUniformMatrix3fv((int)location, (boolean)transpose, (FloatBuffer)values);
    }

    public void setUniformMatrix3(int location, FloatBuffer values, boolean transpose) {
        if (location == -1) {
            return;
        }
        GL32C.glUniformMatrix3fv((int)location, (boolean)transpose, (FloatBuffer)values);
    }

    public void setUniformMatrix4(String name, FloatBuffer values, boolean transpose) {
        int location = this.getUniformLocation(name);
        if (location == -1) {
            return;
        }
        GL32C.glUniformMatrix4fv((int)location, (boolean)transpose, (FloatBuffer)values);
    }

    public void setUniformMatrix4(int location, FloatBuffer values, boolean transpose) {
        if (location == -1) {
            return;
        }
        GL32C.glUniformMatrix4fv((int)location, (boolean)transpose, (FloatBuffer)values);
    }

    public void uploadMatrix(int location, Matrix4d matrix) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            this.setUniformMatrix4(location, matrix.get(matrixBuffer), false);
        }
    }

    public void uploadMatrix(int location, Matrix4f matrix, boolean transpose) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            matrix.get(matrixBuffer);
            this.setUniformMatrix4(location, matrixBuffer, transpose);
        }
    }

    public void uploadMatrix(int location, Matrix4f matrix) {
        this.uploadMatrix(location, matrix, false);
    }

    public void uploadMatrix(int location, Matrix3d matrix) {
        if (location == -1) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer matrixBuffer = stack.mallocFloat(9);
            this.setUniformMatrix3(location, matrix.get(matrixBuffer), false);
        }
    }

    public int getAttrLoc(String name) {
        this.bindShader();
        int loc = GL32C.glGetAttribLocation((int)this.linked, (CharSequence)name);
        GL32C.glLinkProgram((int)this.linked);
        return loc;
    }

    public int getUniformLocation(String name) {
        return this.uniformLocations.getInt((Object)name);
    }

    public void destroy() {
        Shader.unbind();
        GL32C.glDeleteProgram((int)this.linked);
        this.destroyed = true;
    }

    public int getProgramID() {
        return this.linked;
    }

    public void bindFragData(String name, int location) {
        GL32C.glBindFragDataLocation((int)this.linked, (int)location, (CharSequence)name);
    }

    public void bindAttribute(String attribute, int location) {
        this.attributeLocations.put((Object)attribute, location);
        GL32C.glBindAttribLocation((int)this.linked, (int)location, (CharSequence)attribute);
    }

    public void bindAttributeOld(String attribute, int location) {
        GL32C.glBindAttribLocation((int)this.linked, (int)location, (CharSequence)attribute);
    }

    public void bindAttributesOrder(String ... attributes) {
        for (int i = 0; i < attributes.length; ++i) {
            GL32C.glBindAttribLocation((int)this.linked, (int)i, (CharSequence)attributes[i]);
        }
    }

    public String getFragmentLocation() {
        return this.fragmentLocation;
    }

    public String getVertexLocation() {
        return this.vertexLocation;
    }

    public String getGeometryLocation() {
        return this.geometryLocation;
    }

    public Object2IntMap<String> getUniformLocations() {
        return this.uniformLocations;
    }

    public void setUniformLocations(Object2IntMap<String> uniformLocations) {
        this.uniformLocations = uniformLocations;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public Object2IntMap<String> getAttributeLocations() {
        return this.attributeLocations;
    }
}

