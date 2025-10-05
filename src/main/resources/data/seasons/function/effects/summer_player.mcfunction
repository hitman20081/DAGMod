# Summer effects for individual players

# Heat exhaustion in hot biomes
execute if predicate seasons:biomes/hot_biomes run effect give @s minecraft:hunger 2 0 true

# Fire resistance near lava (adapted to heat)
execute if block ~ ~-1 ~ minecraft:lava run effect give @s minecraft:fire_resistance 10 0 true

# Energy boost during day
execute if predicate seasons:time/day run effect give @s minecraft:haste 1 0 true