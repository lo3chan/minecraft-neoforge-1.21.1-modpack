import os, sys, zipfile, subprocess, shutil, concurrent.futures

client_mods_dir = r'C:\Users\hatir\AppData\Roaming\gdlauncher_carbon\data\instances\server pack\instance\mods'
repo_dir = r'f:\Documents\Zim\minecraft-neoforge-1.21.1-modpack'
out_root = os.path.join(repo_dir, 'sources')
vineflower_jar = os.path.join(repo_dir, 'vineflower.jar')

os.makedirs(out_root, exist_ok=True)

all_jars = sorted([f for f in os.listdir(client_mods_dir) if f.endswith('.jar')])
print(f'Starting batch decompilation of {len(all_jars)} mods into {out_root}...')

def process_mod(jar_name):
    mod_slug = os.path.splitext(jar_name)[0]
    dest_dir = os.path.join(out_root, mod_slug)
    
    # Check if already decompiled
    if os.path.exists(dest_dir) and len(os.listdir(dest_dir)) > 0:
        return f'[SKIPPED] {jar_name} (already present)'
        
    os.makedirs(dest_dir, exist_ok=True)
    jar_path = os.path.join(client_mods_dir, jar_name)
    
    # 1. Extract non-binary assets (json, mcmeta, toml, lang)
    try:
        with zipfile.ZipFile(jar_path, 'r') as z:
            for n in z.namelist():
                if n.endswith('/') or n.endswith('.class') or n.endswith('.png') or n.endswith('.ogg') or n.endswith('.wav') or n.endswith('.dll') or n.endswith('.so') or n.endswith('.dylib') or n.endswith('.zip') or n.endswith('.jar'):
                    continue
                target_f = os.path.join(dest_dir, n.replace('/', os.sep))
                os.makedirs(os.path.dirname(target_f), exist_ok=True)
                try:
                    with open(target_f, 'wb') as f:
                        f.write(z.read(n))
                except Exception:
                    pass
    except Exception as e:
        pass

    # 2. Decompile classes into .java using Vineflower
    cmd = [
        'java', '-jar', vineflower_jar,
        '-dgs=1', '-rsy=1', '-lit=1',
        jar_path, dest_dir
    ]
    try:
        res = subprocess.run(cmd, capture_output=True, text=True, timeout=120)
        return f'[OK] Decompiled {jar_name}'
    except subprocess.TimeoutExpired:
        return f'[TIMEOUT] {jar_name}'
    except Exception as e:
        return f'[ERROR] {jar_name}: {e}'

with concurrent.futures.ThreadPoolExecutor(max_workers=6) as executor:
    futures = [executor.submit(process_mod, j) for j in all_jars]
    for future in concurrent.futures.as_completed(futures):
        print(future.result())

print('Batch decompilation finished!')
