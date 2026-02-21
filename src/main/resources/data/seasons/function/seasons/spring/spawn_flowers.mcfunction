# Spawn flowers randomly in spring

# Check for valid grass blocks nearby and spawn flowers
execute positioned ~-5 ~-2 ~-5 if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:grass_block run setblock ~ ~ ~ minecraft:poppy

execute positioned ~3 ~-1 ~2 if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:grass_block run setblock ~ ~ ~ minecraft:dandelion

execute positioned ~-2 ~-2 ~4 if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:grass_block run setblock ~ ~ ~ minecraft:cornflower

# Flower particles
particle minecraft:happy_villager ~ ~1 ~ 3 1 3 0.1 5