#!/usr/bin/env python3
"""
Sixth-pass migration fixes:
- java.util.RandomSource import (wrong - from java.util.Random → RandomSource bug) → remove
- net.minecraft.server.network.ServerPlayer FQN → net.minecraft.server.level.ServerPlayer
- world.isClientSide (private field) → world.isClientSide() (public method)
- player.sendMessage(component, false) → player.sendSystemMessage(component)
- player.sendMessage(component, true) → player.sendOverlayMessage(component)
- player.getUuid() → player.getUUID()
- getStackInHand(Hand.MAIN_HAND) → getMainHandItem()
- getStackInHand(Hand.OFF_HAND) → getOffhandItem()
- stack.isOf(item) → stack.getItem() == item
- player.giveItemStack(stack) → player.addItem(stack)
- inventory.removeStack(i) → inventory.removeItemNoUpdate(i)
- inventory.getStack(i) → inventory.getItem(i)
- Shapes.cuboid(...) → Shapes.box(...)
- createCodec(...) → simpleCodec(...) (Block/BaseEntityBlock)
- createBlockEntity(...) → newBlockEntity(...) (EntityBlock interface)
- net.minecraft.text.Component FQN → net.minecraft.network.chat.Component
- net.minecraft.world.level.block.CollisionContext FQN → correct phys.shapes package
- BlocksAttacksComponent → BlocksAttacks
- TexturedModelData → LayerDefinition
- ModelData → MeshDefinition
- ModelPartData → PartDefinition
- ModelPartBuilder → CubeListBuilder
- ModelTransform → PartPose
- FeaturePlacementContext → PlacementContext
- serverWorld.spawnParticles(...) → serverWorld.sendParticles(...)
- ClientCommandManager → ClientCommands
- bare Id<Type> in CustomPacketPayload context → CustomPacketPayload.Type<Type>
- .formatted(variable) → .withStyle(variable) for component styling
- Duplicate/wrong ChunkGeneratorStructureState import → remove wrong one
- Wrong StructureStart import → correct Mojang package
- BlocksAttacksComponent import → BlocksAttacks
- PlacementModifier.getType() → type()
"""

import os
import re


IMPORT_REPLACEMENTS = [
    # Remove wrong java.util.RandomSource (created by pass 3 bug: java.util.Random → RandomSource)
    ("import java.util.RandomSource;", ""),

    # Remove wrong ChunkGeneratorStructureState in structure.placement (wrong package)
    ("import net.minecraft.world.level.levelgen.structure.placement.ChunkGeneratorStructureState;", ""),

    # Fix StructureStart import from Yarn to Mojang
    ("import net.minecraft.structure.StructureStart;",
     "import net.minecraft.world.level.levelgen.structure.StructureStart;"),

    # BlocksAttacksComponent → BlocksAttacks
    ("import net.minecraft.world.item.component.BlocksAttacksComponent;",
     "import net.minecraft.world.item.component.BlocksAttacks;"),

    # ClientCommandManager → ClientCommands
    ("import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;",
     "import net.fabricmc.fabric.api.client.command.v2.ClientCommands;"),
]


CODE_REPLACEMENTS = [
    # === ServerPlayer in wrong package (FQN references in code) ===
    (r'net\.minecraft\.server\.network\.ServerPlayer\b',
     'net.minecraft.server.level.ServerPlayer'),

    # === isClientSide: private field → public method ===
    # Match .isClientSide not followed by (
    (r'\.isClientSide\b(?!\()', '.isClientSide()'),

    # === getUuid() → getUUID() ===
    (r'\.getUuid\(\)', '.getUUID()'),

    # === getStackInHand → getMainHandItem / getOffhandItem ===
    (r'\.getStackInHand\((?:Hand|InteractionHand)\.MAIN_HAND\)', '.getMainHandItem()'),
    (r'\.getStackInHand\((?:Hand|InteractionHand)\.OFF_HAND\)', '.getOffhandItem()'),

    # === stack.isOf(item) → stack.getItem() == item ===
    (r'\.isOf\(([^)]+)\)', r'.getItem() == \1'),

    # === giveItemStack → addItem ===
    (r'\.giveItemStack\(', '.addItem('),

    # === inventory.removeStack → removeItemNoUpdate ===
    (r'\.getInventory\(\)\.removeStack\(', '.getInventory().removeItemNoUpdate('),

    # === inventory.getStack → getItem ===
    (r'\.getInventory\(\)\.getStack\(', '.getInventory().getItem('),

    # === Shapes.cuboid → Shapes.box ===
    (r'\bShapes\.cuboid\(', 'Shapes.box('),

    # === createCodec → simpleCodec (Block/BaseEntityBlock static method) ===
    (r'\bcreateCodec\b', 'simpleCodec'),

    # === createBlockEntity → newBlockEntity (EntityBlock interface) ===
    (r'\bcreateBlockEntity\b', 'newBlockEntity'),

    # === net.minecraft.text.Component FQN → net.minecraft.network.chat.Component ===
    (r'net\.minecraft\.text\.Component\b', 'net.minecraft.network.chat.Component'),

    # === CollisionContext in wrong block package → correct ===
    (r'net\.minecraft\.world\.level\.block\.CollisionContext\b',
     'net.minecraft.world.phys.shapes.CollisionContext'),

    # === BlocksAttacksComponent → BlocksAttacks ===
    (r'\bBlocksAttacksComponent\b', 'BlocksAttacks'),

    # === Yarn model builder class names → Mojang equivalents ===
    (r'\bTexturedModelData\b', 'LayerDefinition'),
    (r'\bModelData\b(?!\w)', 'MeshDefinition'),
    (r'\bModelPartData\b', 'PartDefinition'),
    (r'\bModelPartBuilder\b', 'CubeListBuilder'),
    (r'\bModelTransform\b', 'PartPose'),

    # === FeaturePlacementContext → PlacementContext ===
    (r'\bFeaturePlacementContext\b', 'PlacementContext'),

    # === spawnParticles → sendParticles (ServerLevel) ===
    (r'\.spawnParticles\(', '.sendParticles('),

    # === ClientCommandManager → ClientCommands ===
    (r'\bClientCommandManager\b', 'ClientCommands'),

    # === Bare Id<Type> in CustomPacketPayload context → CustomPacketPayload.Type<Type> ===
    (r'\bpublic\s+static\s+final\s+Id<(\w+)>',
     r'public static final CustomPacketPayload.Type<\1>'),
    (r'\bnew\s+Id<>\s*\(', 'new CustomPacketPayload.Type<>('),

    # === .formatted(variable) → .withStyle(variable) for component styling ===
    # Only match variables/enum refs (identifiers), not string literals or digits
    (r'\.formatted\(([A-Za-z]\w*(?:\.[A-Za-z]\w*)*)\)', r'.withStyle(\1)'),
]


def fix_send_message(content):
    """Convert player.sendMessage(component, bool) → sendSystemMessage / sendOverlayMessage."""
    # Handle .sendMessage(..., false) → .sendSystemMessage(...)
    # Handle .sendMessage(..., true) → .sendOverlayMessage(...)
    # Use DOTALL to handle multiline calls; non-greedy to get smallest match
    content = re.sub(
        r'\.sendMessage\((.+?),\s*false\s*\)',
        r'.sendSystemMessage(\1)',
        content,
        flags=re.DOTALL
    )
    content = re.sub(
        r'\.sendMessage\((.+?),\s*true\s*\)',
        r'.sendOverlayMessage(\1)',
        content,
        flags=re.DOTALL
    )
    return content


def add_geom_builder_imports(content):
    """Add model geom builder imports for renamed Yarn classes."""
    needs = {
        r'\bLayerDefinition\b': 'import net.minecraft.client.model.geom.builders.LayerDefinition;',
        r'\bMeshDefinition\b': 'import net.minecraft.client.model.geom.builders.MeshDefinition;',
        r'\bPartDefinition\b': 'import net.minecraft.client.model.geom.builders.PartDefinition;',
        r'\bCubeListBuilder\b': 'import net.minecraft.client.model.geom.builders.CubeListBuilder;',
        r'\bPartPose\b': 'import net.minecraft.client.model.geom.PartPose;',
    }
    last = list(re.finditer(r'^import [^;]+;', content, re.MULTILINE))
    if not last:
        return content
    for pattern, imp in needs.items():
        if re.search(pattern, content) and imp not in content:
            pos = last[-1].end()
            content = content[:pos] + '\n' + imp + content[pos:]
            last = list(re.finditer(r'^import [^;]+;', content, re.MULTILINE))
    return content


def add_structure_start_import(content):
    """Add StructureStart import if used but not imported."""
    uses = bool(re.search(r'\bStructureStart\b', content))
    has_import = 'import net.minecraft.world.level.levelgen.structure.StructureStart;' in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + '\nimport net.minecraft.world.level.levelgen.structure.StructureStart;' + content[pos:]
    return content


def add_blocks_attacks_import(content):
    """Add BlocksAttacks import if used but not imported."""
    uses = bool(re.search(r'\bBlocksAttacks\b', content))
    has_import = 'import net.minecraft.world.item.component.BlocksAttacks;' in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + '\nimport net.minecraft.world.item.component.BlocksAttacks;' + content[pos:]
    return content


def fix_duplicate_imports(content):
    """Remove duplicate import lines."""
    lines = content.split('\n')
    seen_imports = set()
    new_lines = []
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('import ') and stripped.endswith(';'):
            if stripped in seen_imports:
                continue
            seen_imports.add(stripped)
        new_lines.append(line)
    return '\n'.join(new_lines)


def remove_empty_import_lines(content):
    """Remove lines that are just 'import ;' (from empty replacements)."""
    lines = content.split('\n')
    new_lines = [l for l in lines if l.strip() != 'import ;' and l.strip() != ';']
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

    content = fix_send_message(content)
    content = add_geom_builder_imports(content)
    content = add_structure_start_import(content)
    content = add_blocks_attacks_import(content)
    content = remove_empty_import_lines(content)
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
