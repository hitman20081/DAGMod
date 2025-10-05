# Fall Transition Effects  
# Leaves start to change, cooler weather

# Start with mild weather
weather rain 600

# Begin leaf color change effects (particle simulation)
execute as @a at @s run particle minecraft:falling_spore_blossom ~0 ~8 ~0 8 3 8 0.1 25

# Trees start dropping more items
execute as @a at @s run execute positioned ~-12 ~-8 ~-12 if predicate seasons:random/10_percent run summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:stick",count:1}}

# Pumpkins appear randomly
execute as @a at @s run execute positioned ~-10 ~-3 ~-10 if predicate seasons:random/3_percent if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:grass_block run setblock ~ ~ ~ minecraft:pumpkin

# Mushrooms start to spread
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 if predicate seasons:random/8_percent if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ #minecraft:dirt run setblock ~ ~ ~ minecraft:brown_mushroom

# Cool autumn wind particles
execute as @a at @s run particle minecraft:cloud ~ ~2 ~ 5 2 5 0.05 10