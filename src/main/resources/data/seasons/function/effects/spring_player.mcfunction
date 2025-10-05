# Spring effects for individual players

# Regeneration boost near flowers
execute if entity @e[type=minecraft:area_effect_cloud,distance=..5,tag=flower_area] run effect give @s minecraft:regeneration 3 0 true

# Speed boost in flower biomes
execute if predicate seasons:biomes/flower_spawning if predicate seasons:random/5_percent run effect give @s minecraft:speed 5 0 true

# Happy particles around player in spring
execute if predicate seasons:random/8_percent run particle minecraft:happy_villager ~ ~1 ~ 0.5 0.5 0.5 0.01 2