#!/usr/bin/env python3
"""Pass 12: Fix remaining compile errors."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

# Line-level replacements (applied with re.sub per file content)
CODE_REPLACEMENTS = [
    # StreamCodec.tuple( -> StreamCodec.composite(
    (r'StreamCodec\.tuple\(', 'StreamCodec.composite('),

    # Monster.createHostileAttributes() -> Monster.createMonsterAttributes()
    (r'Monster\.createHostileAttributes\(\)', 'Monster.createMonsterAttributes()'),

    # player.getStackInHand(hand) -> player.getItemInHand(hand)
    (r'\.getStackInHand\(', '.getItemInHand('),

    # bossBar.clearPlayers() -> bossBar.removeAllPlayers()
    (r'\.clearPlayers\(\)', '.removeAllPlayers()'),

    # ChestBlockEntity::clientTick -> ChestBlockEntity::lidAnimateTick
    (r'ChestBlockEntity::clientTick\b', 'ChestBlockEntity::lidAnimateTick'),

    # this.stateManager.defaultBlockState() -> this.defaultBlockState()
    (r'this\.stateManager\.defaultBlockState\(\)', 'this.defaultBlockState()'),

    # world.updateComparators(pos, this) -> world.updateNeighbourForOutputSignal(pos, this)
    (r'\.updateComparators\(', '.updateNeighbourForOutputSignal('),

    # ItemScatterer.spawn( -> Containers.dropContents(
    (r'\bItemScatterer\.spawn\(', 'Containers.dropContents('),

    # ServerLevel.getRecipeManager() -> .getServer().getRecipeManager()
    # Pattern: ).getRecipeManager() on a cast expression
    (r'\)\s*\.getRecipeManager\(\)', ').getServer().getRecipeManager()'),

    # player.sendMessage( -> player.sendSystemMessage( (for entity method)
    # Only do this when it's a single-arg call to avoid breaking multi-arg ones
    # This is risky globally, do only for specific pattern
    # (r'\bplayer\.sendMessage\(', 'player.sendSystemMessage('),

    # Level.isAir(pos) -> Level.getBlockState(pos).isAir()
    # Pattern: .isAir(someExpr) where someExpr is an expression
    (r'\.isAir\(([^)]+)\)', r'.getBlockState(\1).isAir()'),

    # IronChestBlockEntity -> handle getContainerName -> getDefaultName
    # Too risky globally - handled below per-file

    # BlockPos.add( variable args) - remaining cases (non-literal)
    # Pattern: identifier.add(nonliteral) for BlockPos
    # Too risky to do globally without more context
]

# Per-file line removal: remove lines matching these patterns entirely
LINE_REMOVAL_PATTERNS = [
    # Remove .registryKey(ResourceKey.create(Registries.BLOCK, ...)) lines
    re.compile(r'^\s*\.registryKey\(ResourceKey\.create\(Registries\.BLOCK,\s*Identifier\.fromNamespaceAndPath\(.*?\)\)\)\s*$'),
    re.compile(r'^\s*\.registryKey\(ResourceKey\.create\(Registries\.BLOCK,\s*Identifier\.fromNamespaceAndPath\(.*?\)\)\)\s*\)\s*\);\s*$'),
]

# Per-file content replacements for specific files
FILE_SPECIFIC = {
    'ModBlocks.java': [
        # Remove .registryKey(itemKey) from Item.Properties
        (r'new Item\.Properties\(\)\.registryKey\(itemKey\)', 'new Item.Properties()'),
        # Remove .registryKey(ResourceKey.create(Registries.ITEM, ...))
        (r'\.registryKey\(ResourceKey\.create\(Registries\.ITEM,\s*Identifier\.fromNamespaceAndPath\([^)]+\)\)\)', ''),
    ],
    'IronChestBlockEntity.java': [
        # getContainerName -> getDefaultName
        (r'\bgetContainerName\(\)', 'getDefaultName()'),
    ],
    'JobBoardBlock.java': [
        # getCodec() -> codec() for the @Override method
        (r'public MapCodec<JobBoardBlock> getCodec\(\)', 'public MapCodec<JobBoardBlock> codec()'),
    ],
    'QuestManager.java': [
        # player.sendMessage( -> player.sendSystemMessage( (single-arg)
        (r'player\.sendMessage\(', 'player.sendSystemMessage('),
    ],
    'GemPolishingStationBlockEntity.java': [
        # size() -> getCount() in ContainerData anonymous class
        (r'public int size\(\)\s*\{', 'public int getCount() {'),
        # markDirty -> setChanged, fix field names
        (r'world != null', 'level != null'),
        (r'world\.sendBlockUpdated\(pos,', 'level.sendBlockUpdated(worldPosition,'),
        (r'return this\.pos;', 'return this.worldPosition;'),
        # getStack -> getItem (residual)
        (r'\bgetStack\(OUTPUT_SLOT\)', 'getItem(OUTPUT_SLOT)'),
        # Override markDirty -> setChanged
        (r'public void markDirty\(\)', 'public void setChanged()'),
    ],
}


def remove_registrykey_lines(content):
    """Remove .registryKey(ResourceKey.create(Registries.BLOCK, ...)) lines."""
    lines = content.split('\n')
    result = []
    for line in lines:
        stripped = line.strip()
        # Match lines that are ONLY a .registryKey(...) call for BLOCK registry
        if (stripped.startswith('.registryKey(ResourceKey.create(Registries.BLOCK,') and
                stripped.endswith('))')):
            continue  # skip this line
        result.append(line)
    return '\n'.join(result)


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    fname = os.path.basename(path)

    # Global code replacements
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    # File-specific replacements
    if fname in FILE_SPECIFIC:
        for pattern, replacement in FILE_SPECIFIC[fname]:
            content = re.sub(pattern, replacement, content)

    # Remove .registryKey(ResourceKey.create(Registries.BLOCK, ...)) lines
    if 'registryKey(ResourceKey.create(Registries.BLOCK,' in content:
        content = remove_registrykey_lines(content)

    if content != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    updated = 0
    total = 0
    for root, dirs, files in os.walk(SRC):
        dirs[:] = [d for d in dirs if d != '__pycache__']
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                updated += 1
                print(f"  Updated: {os.path.relpath(path, SRC)}")

    print(f"\nPass 12 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
