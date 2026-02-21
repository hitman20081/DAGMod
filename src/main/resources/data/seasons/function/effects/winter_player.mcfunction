# Winter effects for individual players

# Hypothermia in cold biomes without heat source
execute if predicate seasons:biomes/cold_biomes unless entity @s[distance=..5,predicate=seasons:near_heat_source] run effect give @s minecraft:hunger 3 0 true

# Slower movement in snow
execute if block ~ ~-1 ~ minecraft:snow_block run effect give @s minecraft:slowness 2 0 true
execute if block ~ ~-1 ~ minecraft:powder_snow run effect give @s minecraft:slowness 3 1 true

# Shivering particles when cold
execute if predicate seasons:biomes/cold_biomes if predicate seasons:random/10_percent run particle minecraft:cloud ~ ~1 ~ 0.3 0.3 0.3 0.01 3