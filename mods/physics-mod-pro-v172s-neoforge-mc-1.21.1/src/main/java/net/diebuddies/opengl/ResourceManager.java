/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 *  org.jetbrains.annotations.Nullable
 */
package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.TextureData;
import net.diebuddies.opengl.TextureFilter;
import net.diebuddies.render.shader.ShaderResourceProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

public class ResourceManager {
    private static final int THREAD_POOL_SIZE = 1;
    private Object2ObjectMap<ResourceLocation, Texture> textures;
    private ExecutorService asynchronousLoadingExecutor;
    private List<Future<Runnable>> tasks = new ObjectArrayList();
    private Set<Texture> destructionQueue;

    public ResourceManager() {
        this.textures = new Object2ObjectOpenHashMap();
        this.asynchronousLoadingExecutor = Executors.newFixedThreadPool(1);
        this.destructionQueue = new ObjectOpenHashSet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update() {
        for (int i = 0; i < this.tasks.size(); ++i) {
            Future<Runnable> result = this.tasks.get(i);
            if (!result.isDone()) continue;
            try {
                Runnable event = result.get();
                if (event == null) continue;
                event.run();
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            catch (ExecutionException e) {
                e.printStackTrace();
                continue;
            }
            finally {
                this.tasks.remove(i--);
            }
        }
        Iterator<Texture> it = this.destructionQueue.iterator();
        while (it.hasNext()) {
            Texture texture = it.next();
            if (texture.getID() == -1) continue;
            texture.destroy();
            it.remove();
        }
    }

    public boolean isLoading() {
        return !this.tasks.isEmpty();
    }

    private void doAsynchronous(Callable<Runnable> runnable) {
        this.tasks.add(this.asynchronousLoadingExecutor.submit(runnable));
    }

    public Texture loadTexture(final ResourceLocation path, boolean immediate, final TextureFilter filter) {
        if (!immediate) {
            final Texture texture = new Texture(-1);
            this.textures.put((Object)path, (Object)texture);
            this.doAsynchronous(new Callable<Runnable>(){

                @Override
                public Runnable call() throws Exception {
                    try {
                        TextureData[] tmpData = null;
                        try (InputStream stream = ResourceManager.this.processResourceAsStream(path);){
                            tmpData = new TextureData[]{Texture.loadTextureData(stream)};
                        }
                        final TextureData[] data = tmpData;
                        return new Runnable(){

                            @Override
                            public void run() {
                                texture.set(Texture.loadTexture(data[0], filter));
                                for (int i = 0; i < data.length; ++i) {
                                    data[i].destroy();
                                }
                            }
                        };
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
            return texture;
        }
        try {
            TextureData[] data = null;
            try (InputStream stream = this.processResourceAsStream(path);){
                data = new TextureData[]{Texture.loadTextureData(stream)};
            }
            Texture texture = new Texture(0);
            texture.set(Texture.loadTexture(data[0], filter));
            for (int i = 0; i < data.length; ++i) {
                data[i].destroy();
            }
            this.textures.put((Object)path, (Object)texture);
            return texture;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Texture getTexture(ResourceLocation path, boolean immediate, TextureFilter filter) {
        Texture texture = (Texture)this.textures.get((Object)path);
        if (texture == null) {
            Texture result = this.loadTexture(path, immediate, filter);
            return result;
        }
        return texture;
    }

    public Texture getTexture(ResourceLocation path, boolean immediate) {
        return this.getTexture(path, immediate, null);
    }

    public Texture getTexture(ResourceLocation path) {
        return this.getTexture(path, false);
    }

    public void destroy() {
        this.asynchronousLoadingExecutor.shutdown();
        this.update();
        for (Texture texture : this.textures.values()) {
            texture.destroy();
        }
        this.textures.clear();
    }

    public void destroyTexture(ResourceLocation imageLocation) {
        Texture texture = (Texture)this.textures.remove((Object)imageLocation);
        if (texture != null) {
            if (texture.getID() != -1) {
                texture.destroy();
            } else {
                this.destructionQueue.add(texture);
            }
        }
    }

    private boolean isResourceUrlValid(String string, @Nullable URL url) throws IOException {
        return url != null && (url.getProtocol().equals("jar") || this.validatePath(new File(url.getFile()), string));
    }

    private boolean validatePath(File file, String string) throws IOException {
        String canonicalPath = file.getCanonicalPath();
        return canonicalPath.endsWith(string);
    }

    private InputStream processResourceAsStream(ResourceLocation resourceLocation) {
        String path = this.createPath(resourceLocation);
        try {
            URL url = ShaderResourceProvider.class.getResource(path);
            if (this.isResourceUrlValid(path, url)) {
                return url.openStream();
            }
            return ShaderResourceProvider.class.getResourceAsStream(path);
        }
        catch (IOException iOException) {
            return ShaderResourceProvider.class.getResourceAsStream(path);
        }
    }

    private String createPath(ResourceLocation resourceLocation) {
        return "/" + PackType.CLIENT_RESOURCES.getDirectory() + "/physicsmod/" + resourceLocation.getPath();
    }

    public int getLoadedTexturesSize() {
        return this.textures.size();
    }
}

