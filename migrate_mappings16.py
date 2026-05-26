#!/usr/bin/env python3
"""Pass 16: Fix remaining renames found after pass 15."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # BlockPos.iterate( -> BlockPos.betweenClosed(
    (r'\bBlockPos\.iterate\(', 'BlockPos.betweenClosed('),

    # blockPos.toImmutable() -> blockPos.immutable()
    (r'\.toImmutable\(\)', '.immutable()'),

    # blockPos.mutableCopy() -> blockPos.mutable()
    (r'\.mutableCopy\(\)', '.mutable()'),

    # BlockPos.Mutable -> BlockPos.MutableBlockPos
    (r'\bBlockPos\.Mutable\b', 'BlockPos.MutableBlockPos'),

    # Properties.HORIZONTAL_AXIS -> BlockStateProperties.HORIZONTAL_AXIS
    (r'\bProperties\.HORIZONTAL_AXIS\b', 'BlockStateProperties.HORIZONTAL_AXIS'),

    # Registries.WORLD -> Registries.DIMENSION
    (r'\bRegistries\.WORLD\b', 'Registries.DIMENSION'),

    # world.getRegistryKey() -> world.dimension()
    (r'\.getRegistryKey\(\)', '.dimension()'),

    # server.getWorld( -> server.getLevel(
    (r'\.getWorld\(', '.getLevel('),

    # Block.Properties.dropsNothing() -> noLootTable()
    (r'\.dropsNothing\(\)', '.noLootTable()'),

    # getEquippedStack( -> getItemBySlot(
    (r'\bgetEquippedStack\(', 'getItemBySlot('),

    # ItemStack.contains(DataComponents -> ItemStack.has(DataComponents
    (r'\.contains\(DataComponents', '.has(DataComponents'),

    # ItemStack/Component .getName().getString() -> .getHoverName().getString()
    (r'\.getName\(\)\.getString\(\)', '.getHoverName().getString()'),

    # MutableComponent.formatted( -> .withStyle(
    (r'\.formatted\(', '.withStyle('),

    # UseOnContext.blockPosition() -> .getClickedPos()
    (r'context\.blockPosition\(\)', 'context.getClickedPos()'),

    # UseOnContext.getItem() -> .getItemInHand()
    (r'context\.getItem\(\)', 'context.getItemInHand()'),

    # Player.sendMessage( -> sendSystemMessage(  (1-arg form only, not sendMessage(Component, UUID))
    (r'\bsendMessage\(Component\.', 'sendSystemMessage(Component.'),

    # hasPortalCooldown() -> isOnPortalCooldown()
    (r'\bhasPortalCooldown\(\)', 'isOnPortalCooldown()'),

    # VoxelShape.getBoundingBox() -> .bounds()
    (r'\.getBoundingBox\(\)', '.bounds()'),

    # world.scheduleBlockTick( -> world.scheduleTick(
    (r'\.scheduleBlockTick\(', '.scheduleTick('),

    # Block.onBlockAdded( -> onPlace(
    (r'\bonBlockAdded\b', 'onPlace'),

    # Block.scheduledTick( -> tick(  (block scheduled tick method)
    (r'\bscheduledTick\(BlockState', 'tick(BlockState'),

    # Block.getStateForNeighborUpdate( -> updateShape(
    (r'\bgetStateForNeighborUpdate\b', 'updateShape'),

    # Level.setBlockState( -> Level.setBlock(
    (r'\bsetBlockState\(', 'setBlock('),

    # EntityType.Builder.create( -> EntityType.Builder.of(
    (r'\bEntityType\.Builder\.create\(', 'EntityType.Builder.of('),

    # Entity.initialize(ServerLevelAccessor -> finalizeSpawn(ServerLevelAccessor
    (r'\binitialize\(ServerLevelAccessor', 'finalizeSpawn(ServerLevelAccessor'),

    # super.initialize( -> super.finalizeSpawn(  (in entity overrides)
    (r'\bsuper\.initialize\(', 'super.finalizeSpawn('),

    # Skeleton.createAbstractSkeletonAttributes() -> Skeleton.createAttributes()
    (r'\bSkeleton\.createAbstractSkeletonAttributes\(\)', 'Skeleton.createAttributes()'),

    # Item.useOnBlock(UseOnContext -> Item.useOn(UseOnContext
    (r'\buseOnBlock\(UseOnContext', 'useOn(UseOnContext'),

    # Item.hasGlint( -> isFoil(
    (r'\bhasGlint\b', 'isFoil'),

    # TeleportTransition.NO_OP -> TeleportTransition.NOOP
    (r'\bTeleportTransition\.NO_OP\b', 'TeleportTransition.NOOP'),

    # player.teleportTo(TeleportTransition) -> player.teleport(TeleportTransition)
    (r'player\.teleportTo\(', 'player.teleport('),

    # Fix wrong ResourceLocation references (I introduced these in earlier pass)
    (r'net\.minecraft\.resources\.ResourceLocation\.fromNamespaceAndPath\(',
     'net.minecraft.resources.Identifier.fromNamespaceAndPath('),
    (r'net\.minecraft\.resources\.ResourceLocation\.parse\(',
     'net.minecraft.resources.Identifier.parse('),

    # .up(N) -> .above(N)  for all single-arg forms
    (r'\.up\((\w+)\)', lambda m: f'.above({m.group(1)})'),

    # .down(N) -> .below(N)  any remaining
    (r'\.down\((\w+)\)', lambda m: f'.below({m.group(1)})'),

    # Box (bounding box local var type, when AABB is already imported) -> AABB
    # Only replace "Box " as a local variable type (not method names)
    (r'\bBox\b(?=\s+\w)', 'AABB'),
]


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    for pattern, replacement in CODE_REPLACEMENTS:
        if callable(replacement):
            content = re.sub(pattern, replacement, content)
        else:
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

    print(f"\nPass 16 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
