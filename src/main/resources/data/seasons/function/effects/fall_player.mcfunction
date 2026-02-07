# Fall effects for individual players

# Harvest luck - better drops when breaking crops
execute if predicate seasons:random/5_percent run effect give @s minecraft:luck 10 0 true

# Mushroom detection - night vision in mushroom biomes
execute if predicate seasons:biomes/mushroom run effect give @s minecraft:night_vision 15 0 true

# Falling leaf particles around player
execute if predicate seasons:random/6_percent run particle minecraft:falling_spore_blossom ~ ~2 ~ 1 1 1 0.01 3