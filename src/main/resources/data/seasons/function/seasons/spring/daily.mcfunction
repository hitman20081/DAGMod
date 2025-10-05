# Spring Daily Effects

# Set growth modifier (faster crop growth)
scoreboard players set #global seasons_growth 150

# Spawn flowers randomly in flower-friendly biomes
execute as @a at @s if predicate seasons:biomes/flower_spawning if predicate seasons:random/5_percent run function seasons:seasons/spring/spawn_flowers

# Increase animal breeding chance
execute as @e[type=#seasons:animals] at @s if predicate seasons:random/2_percent run function seasons:seasons/spring/encourage_breeding

# Make rain more likely
execute if predicate seasons:random/10_percent run weather rain 600