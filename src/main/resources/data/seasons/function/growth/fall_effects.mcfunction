# Fall Growth and Environmental Effects
# Trees drop more resources, crop growth slows

# Trees drop extra apples, sticks, and saplings
execute as @a at @s run execute positioned ~-15 ~-10 ~-15 as @e[type=minecraft:item,distance=..30,nbt={Item:{id:"minecraft:oak_log"}}] at @s if predicate seasons:random/25_percent run summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:apple",count:1}}

execute as @a at @s run execute positioned ~-15 ~-10 ~-15 as @e[type=minecraft:item,distance=..30,nbt={Item:{id:"minecraft:birch_log"}}] at @s if predicate seasons:random/20_percent run summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:stick",count:2}}

# Leaves naturally drop saplings more often
execute as @a at @s run execute positioned ~-10 ~-5 ~-10 if predicate seasons:random/5_percent run fill ~0 ~0 ~0 ~20 ~15 ~20 minecraft:air replace minecraft:oak_leaves
execute as @a at @s run execute positioned ~-10 ~-5 ~-10 if predicate seasons:random/5_percent run execute positioned ~ ~ ~ run summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:oak_sapling",count:1}}

# Crop growth slows (75% normal speed)
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 run fill ~0 ~0 ~0 ~16 ~3 ~16 minecraft:farmland[moisture=3] replace minecraft:farmland[moisture=7]

# Mushrooms spread in dark areas
execute as @a at @s run execute positioned ~-10 ~-5 ~-10 if predicate seasons:random/2_percent if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ #minecraft:dirt run setblock ~ ~ ~ minecraft:brown_mushroom

# Fall particle effects (falling leaves)
execute as @a at @s if predicate seasons:random/5_percent run particle minecraft:falling_spore_blossom ~0 ~10 ~0 10 5 10 0.1 20