#!/usr/bin/env python3
"""
Fifth-pass migration fixes:
- Undo more Random* double-replacements (RandomSpreadStructurePlacement, RandomSpreadType)
- ConsumableComponent → Consumable
- ApplyEffectsConsumeEffect → ApplyStatusEffectsConsumeEffect
- TooltipType → TooltipFlag, appendTooltip → appendHoverText
- StructurePlacementCalculator → ChunkGeneratorStructureState
- SoundEvents: remove BLOCK_/ENTITY_/ITEM_ prefix
- Block method renames: appendProperties→createBlockStateDefinition, onUse→useWithoutItem,
  getPlacementState→getStateForPlacement, setDefaultState→registerDefaultState,
  getDefaultState()→defaultBlockState(), getOutlineShape→getShape, getRenderType→getRenderShape,
  randomDisplayTick→animateTick, getCodec()→codec()
- BlockState.get(PROP) → getValue(PROP), BlockState.with(PROP, v) → setValue(PROP, v)
- Properties.HORIZONTAL_FACING → BlockStateProperties.HORIZONTAL_FACING
- ItemStack.decrement → shrink
- addParticleClient → addParticle
- isClient() → isClientSide
- getHorizontalPlayerFacing() → getHorizontalDirection()
- Component.formatted(ChatFormatting) → withStyle(ChatFormatting)
- openHandledScreen → openMenu
- getMainHandStack → getMainHandItem
"""

import os
import re

IMPORT_REPLACEMENTS = [
    # Undo Random double-replacements
    ("import net.minecraft.world.level.levelgen.structure.placement.RandomSourceSpreadStructurePlacement;",
     "import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;"),
    ("import net.minecraft.world.level.levelgen.structure.placement.RandomSourceSpreadType;",
     "import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;"),

    # ConsumableComponent API
    ("import net.minecraft.world.item.component.ConsumableComponent;",
     "import net.minecraft.world.item.component.Consumable;"),
    ("import net.minecraft.world.item.component.ConsumableComponents;",
     "import net.minecraft.world.item.component.Consumables;"),
    ("import net.minecraft.world.item.consume.ApplyEffectsConsumeEffect;",
     "import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;"),

    # TooltipType → TooltipFlag
    ("import net.minecraft.world.item.tooltip.TooltipType;",
     "import net.minecraft.world.item.TooltipFlag;"),

    # EquipmentType (Yarn inner-class) → ArmorType (closest Mojang equivalent)
    ("import net.minecraft.world.item.equipment.EquipmentType;",
     "import net.minecraft.world.item.equipment.ArmorType;"),

    # BlockStateProperties (may have been imported as Properties)
    # No import fix needed — BlockStateProperties should already be imported from previous passes
]

CODE_REPLACEMENTS = [
    # === Undo Random double-replacements ===
    (r'\bRandomSourceSpreadStructurePlacement\b', 'RandomSpreadStructurePlacement'),
    (r'\bRandomSourceSpreadType\b',               'RandomSpreadType'),

    # SpreadType (Yarn inner-class name) → RandomSpreadType (Mojang standalone class)
    # Only when used as a type/constant, not inside other words
    (r'\bSpreadType\b(?!\.CODEC|\.LINEAR)', 'RandomSpreadType'),
    (r'\bSpreadType\.CODEC\b',  'RandomSpreadType.CODEC'),
    (r'\bSpreadType\.LINEAR\b', 'RandomSpreadType.LINEAR'),

    # StructurePlacementCalculator → ChunkGeneratorStructureState
    (r'\bStructurePlacementCalculator\b', 'ChunkGeneratorStructureState'),
    # isStartChunk → isPlacementChunk (RandomSpreadStructurePlacement override)
    (r'\bisStartChunk\b', 'isPlacementChunk'),

    # Getter method renames (RandomSpreadStructurePlacement / StructurePlacement)
    (r'\bgetSpreadType\(\)', 'spreadType()'),
    (r'\bgetSpacing\(\)',    'spacing()'),
    (r'\bgetSeparation\(\)', 'separation()'),
    (r'\bgetSalt\(\)',       'salt()'),

    # === ConsumableComponent API ===
    (r'\bConsumableComponent\b', 'Consumable'),
    (r'\bConsumableComponents\b', 'Consumables'),
    (r'\bApplyEffectsConsumeEffect\b', 'ApplyStatusEffectsConsumeEffect'),
    (r'net\.minecraft\.world\.item\.consume\.', 'net.minecraft.world.item.consume_effects.'),

    # === Tooltip API ===
    (r'\bTooltipType\b',   'TooltipFlag'),
    (r'\bappendTooltip\b', 'appendHoverText'),
    # EquipmentType → ArmorType
    (r'\bEquipmentType\b', 'ArmorType'),

    # === SoundEvents: strip category prefix ===
    (r'\bSoundEvents\.BLOCK_',  'SoundEvents.'),
    (r'\bSoundEvents\.ENTITY_', 'SoundEvents.'),
    (r'\bSoundEvents\.ITEM_',   'SoundEvents.'),
    (r'\bSoundEvents\.UI_',     'SoundEvents.'),
    (r'\bSoundEvents\.MUSIC_',  'SoundEvents.'),

    # === Block method overrides ===
    # appendProperties → createBlockStateDefinition
    (r'\bappendProperties\b', 'createBlockStateDefinition'),
    # randomDisplayTick → animateTick
    (r'\brandomDisplayTick\b', 'animateTick'),
    # getPlacementState → getStateForPlacement
    (r'\bgetPlacementState\b', 'getStateForPlacement'),
    # setDefaultState → registerDefaultState
    (r'\bsetDefaultState\b', 'registerDefaultState'),
    # getDefaultState() → defaultBlockState()
    (r'\bgetDefaultState\(\)', 'defaultBlockState()'),
    # onUse → useWithoutItem (block override)
    (r'\bonUse\b', 'useWithoutItem'),
    # getOutlineShape → getShape (VoxelShape method in BlockBehaviour)
    (r'\bgetOutlineShape\b', 'getShape'),
    # getRenderType → getRenderShape
    (r'\bgetRenderType\b', 'getRenderShape'),
    # getCodec() → codec() for @Override methods in blocks
    # Match the override method declaration specifically
    (r'\bMapCodec<\? extends (\w+)> getCodec\(\)',
     r'MapCodec<? extends \1> codec()'),

    # === BlockState property access ===
    # state.get(UPPERCASE_PROPERTY) → state.getValue(UPPERCASE_PROPERTY)
    # Only match where the argument looks like an uppercase constant (block property)
    (r'\.get\(([A-Z][A-Z0-9_]*(?:\.[A-Z][A-Z0-9_]*)?)\)',
     r'.getValue(\1)'),
    # state.with(UPPERCASE_PROPERTY, value) → state.setValue(UPPERCASE_PROPERTY, value)
    (r'\.with\(([A-Z][A-Z0-9_]*(?:\.[A-Z][A-Z0-9_]*)?),',
     r'.setValue(\1,'),

    # Properties.HORIZONTAL_FACING → BlockStateProperties.HORIZONTAL_FACING
    (r'\bProperties\.HORIZONTAL_FACING\b', 'BlockStateProperties.HORIZONTAL_FACING'),
    (r'\bProperties\.FACING\b', 'BlockStateProperties.FACING'),
    (r'\bProperties\.LIT\b', 'BlockStateProperties.LIT'),
    (r'\bProperties\.POWERED\b', 'BlockStateProperties.POWERED'),
    (r'\bProperties\.OPEN\b', 'BlockStateProperties.OPEN'),
    (r'\bProperties\.WATERLOGGED\b', 'BlockStateProperties.WATERLOGGED'),
    (r'\bProperties\.AXIS\b', 'BlockStateProperties.AXIS'),
    (r'\bProperties\.HALF\b', 'BlockStateProperties.HALF'),
    (r'\bProperties\.FACE\b', 'BlockStateProperties.FACE'),
    (r'\bProperties\.ATTACHED\b', 'BlockStateProperties.ATTACHED'),
    (r'\bProperties\.ENABLED\b', 'BlockStateProperties.ENABLED'),
    (r'\bProperties\.EYE\b', 'BlockStateProperties.EYE'),
    (r'\bProperties\.DELAY\b', 'BlockStateProperties.DELAY'),
    (r'\bProperties\.AGE_\d+\b', lambda m: 'BlockStateProperties.' + m.group(0).split('.')[1]),
    (r'\bProperties\.LEVEL_\d+\b', lambda m: 'BlockStateProperties.' + m.group(0).split('.')[1]),

    # === Player/Entity method renames ===
    # openHandledScreen → openMenu
    (r'\bopenHandledScreen\b', 'openMenu'),
    # getMainHandStack → getMainHandItem
    (r'\bgetMainHandStack\b', 'getMainHandItem'),
    # getHorizontalPlayerFacing → getHorizontalDirection (BlockPlaceContext)
    (r'\bgetHorizontalPlayerFacing\b', 'getHorizontalDirection'),
    # isClient() → isClientSide (Level field, no parens)
    (r'\.isClient\(\)', '.isClientSide'),

    # === ItemStack methods ===
    # decrement → shrink (ItemStack)
    (r'\bdecrement\b', 'shrink'),

    # === Particle methods ===
    # addParticleClient → addParticle (Level)
    (r'\baddParticleClient\b', 'addParticle'),

    # === Component styling ===
    # .formatted(ChatFormatting → .withStyle(ChatFormatting
    (r'\.formatted\(ChatFormatting', '.withStyle(ChatFormatting'),
]


def add_block_state_properties_import(content):
    """Add BlockStateProperties import if BlockStateProperties is used but not imported."""
    uses = bool(re.search(r'\bBlockStateProperties\b', content))
    has_import = "import net.minecraft.world.level.block.state.properties.BlockStateProperties;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.world.level.block.state.properties.BlockStateProperties;" + content[pos:]
    return content


def add_chunk_gen_structure_state_import(content):
    """Add ChunkGeneratorStructureState import if used but not imported."""
    uses = bool(re.search(r'\bChunkGeneratorStructureState\b', content))
    has_import = "import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.world.level.chunk.ChunkGeneratorStructureState;" + content[pos:]
    return content


def add_random_source_import(content):
    """Add RandomSource import if used but not imported."""
    uses = bool(re.search(r'\bRandomSource\b', content))
    has_import = "import net.minecraft.util.RandomSource;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.util.RandomSource;" + content[pos:]
    return content


def add_server_player_import(content):
    """Add ServerPlayer import if used but not imported."""
    uses = bool(re.search(r'\bServerPlayer\b', content))
    has_import = "import net.minecraft.server.level.ServerPlayer;" in content
    if uses and not has_import:
        last = list(re.finditer(r'^import net\.minecraft[^;]+;', content, re.MULTILINE))
        if last:
            pos = last[-1].end()
            content = content[:pos] + "\nimport net.minecraft.server.level.ServerPlayer;" + content[pos:]
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

    content = add_block_state_properties_import(content)
    content = add_chunk_gen_structure_state_import(content)
    content = add_random_source_import(content)
    content = add_server_player_import(content)
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
