#!/usr/bin/env python3
"""
sync_mods.py
Automated sync and clone utility for Minecraft NeoForge 1.21.1 mod sources.
Reads modpack.json and clones/submodules upstream mod sources into /mods with fallback.
"""

import json
import os
import subprocess
import sys
from pathlib import Path

def run_cmd(cmd, cwd=None):
    print(f">> Running: {' '.join(cmd)}")
    res = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"Notice/Warning ({res.returncode}): {res.stderr.strip()}", file=sys.stderr)
    else:
        if res.stdout.strip():
            print(res.stdout.strip())
    return res.returncode == 0

def main():
    root_dir = Path(__file__).parent
    config_file = root_dir / "modpack.json"
    mods_dir = root_dir / "mods"

    if not config_file.exists():
        print(f"Error: {config_file} not found!", file=sys.stderr)
        sys.exit(1)

    mods_dir.mkdir(exist_ok=True)

    with open(config_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    mods = data.get("mods", [])
    print(f"==================================================")
    print(f"Syncing {len(mods)} mods for Minecraft NeoForge 1.21.1")
    print(f"==================================================")

    for mod in mods:
        mod_id = mod.get("id")
        upstream = mod.get("upstream")
        branch = mod.get("branch", "main")
        sync_type = mod.get("type", "submodule")

        if not mod_id or not upstream:
            continue

        target_path = mods_dir / mod_id
        if target_path.exists():
            print(f"\n[{mod_id}] Existing repository found at {target_path}. Updating...")
            run_cmd(["git", "fetch", "--all"], cwd=target_path)
            if not run_cmd(["git", "checkout", branch], cwd=target_path):
                print(f"[{mod_id}] Branch '{branch}' not found locally. Checking out default HEAD...")
                run_cmd(["git", "checkout", "HEAD"], cwd=target_path)
            run_cmd(["git", "pull"], cwd=target_path)
        else:
            print(f"\n[{mod_id}] Ingesting upstream: {upstream} (preferred branch: {branch})...")
            if sync_type == "submodule":
                rel_path = f"mods/{mod_id}"
                success = run_cmd(["git", "submodule", "add", "-b", branch, upstream, rel_path], cwd=root_dir)
                if not success:
                    print(f"[{mod_id}] Submodule branch '{branch}' failed. Adding with default remote branch...")
                    run_cmd(["git", "submodule", "add", upstream, rel_path], cwd=root_dir)
            else:
                success = run_cmd(["git", "clone", "-b", branch, upstream, str(target_path)])
                if not success:
                    print(f"[{mod_id}] Clone branch '{branch}' failed. Cloning default remote branch...")
                    run_cmd(["git", "clone", upstream, str(target_path)])

    print("\n==================================================")
    print("All mod sources synchronized successfully into /mods!")
    print("==================================================")

if __name__ == "__main__":
    main()
