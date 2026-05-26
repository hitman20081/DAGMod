#!/usr/bin/env python3
"""Pass 10: Fix remaining compile issues after pass 9."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # Identifier.of(ns, path) -> Identifier.fromNamespaceAndPath(ns, path)
    (r'Identifier\.of\(', 'Identifier.fromNamespaceAndPath('),

    # BlockBehaviour.Properties.create() -> Properties.of()
    (r'(?<!\w)Properties\.create\(\)', 'Properties.of()'),

    # ChatFormatting wrong package (FQN references)
    (r'net\.minecraft\.util\.ChatFormatting', 'net.minecraft.ChatFormatting'),

    # SoundEvents rename
    (r'\bSoundEvents\.STONECUTTER_TAKE_RESULT\b', 'SoundEvents.UI_STONECUTTER_TAKE_RESULT'),

    # BlockPos.add(x,y,z) -> offset(x,y,z) - remaining cases
    (r'(\w+)\.add\((-?\d+),\s*(-?\d+),\s*(-?\d+)\)', r'\1.offset(\2, \3, \4)'),

    # getWorld() -> getLevel() (on BlockEntity)
    (r'\.getWorld\(\)', '.getLevel()'),

    # Level.getRecipeManager() -> level().recipeManager() or similar
    # Actually ServerLevel has getRecipeManager()
    # Don't touch this - might be wrong context

    # onStateReplaced(BlockState,Level,...) -> onRemove(BlockState,Level,...) in some places
    # Actually onStateReplaced is the Mojang name; if it says "does not override", the signature changed
    # Skip for now

    # ItemScatterer - Fabric API, needs different approach
    # Skip for now

    # up() remaining -> above()
    # Pattern: .up() that wasn't caught before
    (r'(?<!\w)\.up\(\)', '.above()'),

    # Variable "world" in BlockEntity context -> "level"
    # Too risky to do globally; skip

    # Missing Registries import - add it to files that use Registries
    # Can't do in code replacement; needs import addition

    # BlockEntityType.Builder.create(factory, blocks...) - check API
    # Currently FabricBlockEntityTypeBuilder.create(...) - skip (Fabric API)

    # ContainerData.size() in anonymous class body -> getCount()
    # This is method declaration: @Override public int size() -> getCount()
    # Pattern: in anonymous ContainerData class, "int size()" -> "int getCount()"
    # Too risky to do broadly, skip

    # GemPolishingStationBlockEntity getStack/setStack -> getItem/setItem
    # These should have been caught by pass 9

    # CraftingInput.create(int,int) -> CraftingInput.of(int,int,List)
    # Pattern: new CraftingInput(...)
    (r'\bnew CraftingInput\(', 'CraftingInput.of(1, 1, java.util.List.of('),

    # WildDragonEntity save/load: CompoundTag -> ValueInput/ValueOutput
    # Handle in specific file
]


def add_registries_import(content):
    """Add Registries import if file uses Registries but doesn't import it."""
    if 'Registries.' in content and 'import net.minecraft.core.registries.Registries;' not in content:
        # Add after existing imports block
        content = re.sub(
            r'(import net\.minecraft\.core\.registries\.BuiltInRegistries;)',
            r'\1\nimport net.minecraft.core.registries.Registries;',
            content
        )
        # If BuiltInRegistries not present either, add after first import
        if 'import net.minecraft.core.registries.Registries;' not in content:
            content = re.sub(
                r'(^import .*;)',
                r'\1\nimport net.minecraft.core.registries.Registries;',
                content,
                count=1,
                flags=re.MULTILINE
            )
    return content


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    content = add_registries_import(content)

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

    print(f"\nPass 10 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
