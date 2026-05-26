#!/usr/bin/env python3
"""
Fourth-pass migration fixes:
- Undo double-replacement: RandomSourceSource → RandomSource
- Undo double-replacement: RandomSourceLookAroundGoal → RandomLookAroundGoal
- Undo double-replacement: HolderLookup.HolderLookup.Provider → HolderLookup.Provider
- VillagerEntityRenderState → VillagerRenderState
- VillagerModel / VillagerResemblingModel → VillagerLikeModel
- SkeletonEntityModel → SkeletonModel
- CustomPacketPayload.Id → CustomPacketPayload.Type + getId() → type()
- ImplementedInventory: LevellyContainer import → WorldlyContainer
- LockedBoneChestBlockEntity: WriteView/ReadView → ValueOutput/ValueInput
- DragonGuardianModel: add ModelPart import
- net.minecraft.world.ServerLevelAccessor → net.minecraft.world.level.ServerLevelAccessor
- CollisionContext import injection where still missing
"""

import os
import re

IMPORT_REPLACEMENTS = [
    # Undo double-replacement: RandomSourceSource
    ("import net.minecraft.util.RandomSourceSource;",
     "import net.minecraft.util.RandomSource;"),

    # Undo double-replacement: RandomSourceLookAroundGoal
    ("import net.minecraft.world.entity.ai.goal.RandomSourceLookAroundGoal;",
     "import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;"),

    # VillagerEntityRenderState → VillagerRenderState
    ("import net.minecraft.client.renderer.entity.state.VillagerEntityRenderState;",
     "import net.minecraft.client.renderer.entity.state.VillagerRenderState;"),

    # VillagerModel → VillagerLikeModel
    ("import net.minecraft.client.model.VillagerModel;",
     "import net.minecraft.client.model.VillagerLikeModel;"),

    # VillagerResemblingModel (Fabric API wrapper removed — use VillagerLikeModel directly)
    ("import net.fabricmc.fabric.api.client.rendering.v1.entity.VillagerResemblingModel;",
     "import net.minecraft.client.model.VillagerLikeModel;"),

    # SkeletonEntityModel → SkeletonModel
    ("import net.minecraft.client.model.SkeletonEntityModel;",
     "import net.minecraft.client.model.monster.skeleton.SkeletonModel;"),

    # ImplementedInventory: wrong package for WorldlyContainer
    ("import net.minecraft.world.level.LevellyContainer;",
     "import net.minecraft.world.WorldlyContainer;"),

    # LockedBoneChestBlockEntity: WriteView/ReadView → ValueOutput/ValueInput
    ("import net.minecraft.storage.WriteView;",
     "import net.minecraft.world.level.storage.ValueOutput;"),
    ("import net.minecraft.storage.ReadView;",
     "import net.minecraft.world.level.storage.ValueInput;"),

    # ServerLevelAccessor import fix (wrong package — not net.minecraft.world)
    ("import net.minecraft.world.ServerLevelAccessor;",
     "import net.minecraft.world.level.ServerLevelAccessor;"),
]

CODE_REPLACEMENTS = [
    # === Undo double-replacements FIRST ===

    # RandomSourceSource → RandomSource (double-replacement from pass 3)
    (r'\bRandomSourceSource\b', 'RandomSource'),

    # RandomSourceLookAroundGoal → RandomLookAroundGoal
    (r'\bRandomSourceLookAroundGoal\b', 'RandomLookAroundGoal'),

    # HolderLookup.HolderLookup.Provider → HolderLookup.Provider (double-replacement)
    (r'\bHolderLookup\.HolderLookup\.Provider\b', 'HolderLookup.Provider'),

    # net.minecraft.world.ServerLevelAccessor → correct package (inline FQNs)
    (r'net\.minecraft\.world\.ServerLevelAccessor\b',
     'net.minecraft.world.level.ServerLevelAccessor'),

    # === Villager rendering ===

    # VillagerEntityRenderState → VillagerRenderState
    (r'\bVillagerEntityRenderState\b', 'VillagerRenderState'),

    # VillagerResemblingModel → VillagerLikeModel
    (r'\bVillagerResemblingModel\b', 'VillagerLikeModel'),

    # VillagerModel → VillagerLikeModel (bare class name, not VillagerLikeModel itself)
    (r'\bVillagerModel\b', 'VillagerLikeModel'),

    # === Skeleton model ===

    # SkeletonEntityModel → SkeletonModel
    (r'\bSkeletonEntityModel\b', 'SkeletonModel'),

    # === CustomPacketPayload networking ===

    # CustomPacketPayload.Id → CustomPacketPayload.Type
    (r'\bCustomPacketPayload\.Id\b', 'CustomPacketPayload.Type'),

    # getId() override → type() for CustomPacketPayload implementors
    # Match "public Id<? extends CustomPacketPayload> getId()" → "public Type<? extends CustomPacketPayload> type()"
    (r'\bpublic\s+Id<\??\s*extends\s+CustomPacketPayload>\s+getId\(\)',
     'public Type<? extends CustomPacketPayload> type()'),

    # Also handle: public CustomPacketPayload.Id<...> getId() pattern
    (r'\bpublic\s+CustomPacketPayload\.Type<[^>]+>\s+getId\(\)',
     lambda m: m.group(0).replace('getId()', 'type()')),

    # === LockedBoneChestBlockEntity save/load API ===

    # writeData(WriteView → saveAdditional(ValueOutput
    (r'\bvoid\s+writeData\s*\(\s*WriteView\b', 'void saveAdditional(ValueOutput'),
    (r'\bprotected\s+void\s+writeData\s*\(\s*WriteView\b',
     'protected void saveAdditional(ValueOutput'),

    # readData(ReadView → loadAdditional(ValueInput
    (r'\bvoid\s+readData\s*\(\s*ReadView\b', 'void loadAdditional(ValueInput'),
    (r'\bprotected\s+void\s+readData\s*\(\s*ReadView\b',
     'protected void loadAdditional(ValueInput'),

    # Remaining WriteView / ReadView type refs in code (not yet caught)
    (r'\bWriteView\b', 'ValueOutput'),
    (r'\bReadView\b', 'ValueInput'),
]


def add_modelpart_import(content):
    """Add ModelPart import to files that use it but only have the wildcard client.model.* import."""
    uses_modelpart = bool(re.search(r'\bModelPart\b', content))
    has_modelpart_import = "import net.minecraft.client.model.geom.ModelPart;" in content
    has_wildcard = "import net.minecraft.client.model.*;" in content

    if uses_modelpart and not has_modelpart_import:
        if has_wildcard:
            content = content.replace(
                "import net.minecraft.client.model.*;",
                "import net.minecraft.client.model.*;\nimport net.minecraft.client.model.geom.ModelPart;"
            )
        else:
            # Find a good insertion point
            match = re.search(r'(import net\.minecraft\.client[^;]+;)', content)
            if match:
                last = list(re.finditer(r'^import net\.minecraft\.client[^;]+;', content, re.MULTILINE))
                if last:
                    pos = last[-1].end()
                    content = content[:pos] + "\nimport net.minecraft.client.model.geom.ModelPart;" + content[pos:]
    return content


def add_server_level_accessor_import(content):
    """Add ServerLevelAccessor import if used but not imported."""
    uses = bool(re.search(r'\bServerLevelAccessor\b', content))
    has_import = "import net.minecraft.world.level.ServerLevelAccessor;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.world.level.ServerLevelAccessor;" + content[pos:]
    return content


def add_value_io_imports(content):
    """Add ValueInput/ValueOutput imports where needed."""
    uses_out = bool(re.search(r'\bValueOutput\b', content))
    uses_in = bool(re.search(r'\bValueInput\b', content))
    has_out = "import net.minecraft.world.level.storage.ValueOutput;" in content
    has_in = "import net.minecraft.world.level.storage.ValueInput;" in content

    last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
    if not last:
        return content

    if uses_out and not has_out:
        pos = last[-1].end()
        content = content[:pos] + "\nimport net.minecraft.world.level.storage.ValueOutput;" + content[pos:]
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))

    if uses_in and not has_in:
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.world.level.storage.ValueInput;" + content[pos:]

    return content


def add_villager_like_model_import(content):
    """Add VillagerLikeModel import if code uses it but has no import for it."""
    uses = bool(re.search(r'\bVillagerLikeModel\b', content))
    has_import = "import net.minecraft.client.model.VillagerLikeModel;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.client.model.VillagerLikeModel;" + content[pos:]
    return content


def add_skeleton_model_import(content):
    """Add SkeletonModel import if code uses it but has no import for it."""
    uses = bool(re.search(r'\bSkeletonModel\b', content))
    has_import = "import net.minecraft.client.model.monster.skeleton.SkeletonModel;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.client.model.monster.skeleton.SkeletonModel;" + content[pos:]
    return content


def fix_duplicate_imports(content):
    """Remove duplicate import lines."""
    lines = content.split('\n')
    seen_imports = set()
    new_lines = []
    for line in lines:
        if line.startswith('import ') and line.endswith(';'):
            if line in seen_imports:
                continue
            seen_imports.add(line)
        new_lines.append(line)
    return '\n'.join(new_lines)


def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content

    for old, new in IMPORT_REPLACEMENTS:
        content = content.replace(old, new)

    for pattern, replacement in CODE_REPLACEMENTS:
        if callable(replacement):
            content = re.sub(pattern, replacement, content)
        else:
            content = re.sub(pattern, replacement, content)

    content = add_modelpart_import(content)
    content = add_server_level_accessor_import(content)
    content = add_value_io_imports(content)
    content = add_villager_like_model_import(content)
    content = add_skeleton_model_import(content)
    content = fix_duplicate_imports(content)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    src_dir = os.path.join(os.path.dirname(__file__), 'src', 'main', 'java')
    changed = 0
    total = 0
    for root, dirs, files in os.walk(src_dir):
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                changed += 1
                print(f"  Updated: {os.path.relpath(path, src_dir)}")
    print(f"\nDone: {changed}/{total} files updated.")


if __name__ == '__main__':
    main()
