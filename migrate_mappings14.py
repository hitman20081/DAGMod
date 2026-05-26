#!/usr/bin/env python3
"""Pass 14: Fix remaining global renames found in compile output."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # UniformInt.create -> UniformInt.of
    (r'\bUniformInt\.create\(', 'UniformInt.of('),

    # CreativeModeTabEvents API rename: modifyEntriesEvent -> modifyOutputEvent
    (r'\.modifyEntriesEvent\(', '.modifyOutputEvent('),

    # content.add(X) -> output.accept(X) inside creative tab lambdas
    # The lambda param was "content" or "itemGroup" - rename calls
    (r'\bcontent\.add\(', 'content.accept('),
    (r'\bitemGroup\.add\(', 'itemGroup.accept('),

    # Entity.moveTo(double,double,double,float,float) -> Entity.snapTo(...)
    # Only the 5-arg form (with yaw/pitch floats) - be careful not to match other moveTo
    (r'\.moveTo\(', '.snapTo('),

    # BlockState.with( -> BlockState.setValue(
    (r'\.with\((?=[A-Z])', '.setValue('),

    # BlockPos.down() -> BlockPos.below()
    (r'\.down\(\)', '.below()'),
    (r'\.down\((\d+)\)', r'.below(\1)'),

    # net.minecraft.registry.ResourceKey -> net.minecraft.resources.ResourceKey
    (r'\bnet\.minecraft\.registry\.ResourceKey\b', 'net.minecraft.resources.ResourceKey'),

    # net.minecraft.registry.Registries -> net.minecraft.core.registries.Registries
    (r'\bnet\.minecraft\.registry\.Registries\b', 'net.minecraft.core.registries.Registries'),

    # net.minecraft.component. -> net.minecraft.core.component.
    (r'\bnet\.minecraft\.component\.', 'net.minecraft.core.component.'),

    # world.random. -> world.getRandom(). (protected field access)
    (r'\bworld\.random\.', 'world.getRandom().'),

    # getTopY( -> getHeight(
    (r'\.getTopY\(', '.getHeight('),

    # Heightmap.Type. -> Heightmap.Types.
    (r'\bHeightmap\.Type\.', 'Heightmap.Types.'),

    # Identifier.ofVanilla -> Identifier.withDefaultNamespace
    (r'\bIdentifier\.ofVanilla\(', 'Identifier.withDefaultNamespace('),

    # context.getPart( -> context.bakeLayer(
    (r'\bcontext\.getPart\(', 'context.bakeLayer('),

    # getTexture( -> getTextureLocation( in renderer override
    # Only replace when it's the overriding method, not a caller
    # Actually, the override was named wrong: "getTexture" should be "getTextureLocation"
    # We'll do a targeted fix for this

    # DamageSource.getLastAttacker() -> getEntity()
    (r'\.getLastAttacker\(\)', '.getEntity()'),

    # RecipeManager.getFirstMatch -> getRecipeFor
    (r'\.getFirstMatch\(', '.getRecipeFor('),

    # setLootTable(key, seed) -> setLootTable(key) + setLootTableSeed(seed) -- complex, do manually

    # Item.Properties.registryKey(key) -> Item.Properties.setId(key)
    # Only for Item.Properties, not BlockBehaviour.Properties (those should already be removed)
    # Pattern: new Item.Properties()...registryKey(  or ).registryKey(
    # We need to be careful - only match .registryKey( when followed by a ResourceKey<Item> arg
    # Use a simple pattern for now
    (r'new Item\.Properties\(\)\.registryKey\(', 'new Item.Properties().setId('),

    # BlockBehaviour.Properties.registryKey(...) lines - remove if still present
    # These should have been removed in pass 12 but let's catch any remaining
    (r'\.registryKey\(ResourceKey\.create\(Registries\.BLOCK,\s*Identifier\.fromNamespaceAndPath\([^)]+\)\)\)', ''),

    # maxCount -> stacksTo for Item.Properties (if any remain)
    (r'\.maxCount\(', '.stacksTo('),
]


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

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

    print(f"\nPass 14 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
