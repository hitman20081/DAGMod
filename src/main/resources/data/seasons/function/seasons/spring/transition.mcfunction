# Spring Transition Effects
# Melt ice, spawn flowers, clear snow

# Melt ice back to water
execute as @a at @s run execute positioned ~-20 ~-10 ~-20 run fill ~0 ~0 ~0 ~40 ~20 ~40 minecraft:water replace minecraft:ice

# Remove snow layers
execute as @a at @s run execute positioned ~-15 ~-5 ~-15 run fill ~0 ~0 ~0 ~30 ~10 ~30 minecraft:air replace minecraft:snow

# Spawn flowers randomly
execute as @a at @s run execute positioned ~-10 ~-3 ~-10 if predicate seasons:random/15_percent if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:grass_block run setblock ~ ~ ~ minecraft:poppy

execute as @a at @s run execute positioned ~-10 ~-3 ~-10 if predicate seasons:random/15_percent if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:grass_block run setblock ~ ~ ~ minecraft:dandelion

# Spring rain to wash away winter
weather rain 1200

# Particle effects - rebirth and growth
execute as @a at @s run particle minecraft:happy_villager ~ ~2 ~ 5 2 5 0.1 30
execute as @a at @s run particle minecraft:composter ~ ~1 ~ 8 1 8 0.1 50

# Restore farmland moisture
execute as @a at @s run execute positioned ~-12 ~-5 ~-12 run fill ~0 ~0 ~0 ~24 ~10 ~24 minecraft:farmland[moisture=7] replace minecraft:farmland[moisture=0]