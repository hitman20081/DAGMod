# Fall Daily Effects

# Set growth modifier (slower growth)
scoreboard players set #global seasons_growth 75

# Moderate weather patterns
execute if predicate seasons:random/8_percent run weather rain 800
execute if predicate seasons:random/12_percent run weather clear 1000

# Pumpkin and melon stems grow faster (using correct stem blocks)
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 if predicate seasons:random/12_percent run fill ~0 ~0 ~0 ~16 ~6 ~16 minecraft:pumpkin_stem[age=7] replace minecraft:pumpkin_stem[age=6]
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 if predicate seasons:random/12_percent run fill ~0 ~0 ~0 ~16 ~6 ~16 minecraft:melon_stem[age=7] replace minecraft:melon_stem[age=6]

# Wild mushrooms spread more
execute as @a at @s run execute positioned ~-10 ~-5 ~-10 if predicate seasons:random/4_percent if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ #minecraft:dirt run setblock ~ ~ ~ minecraft:red_mushroom

# Shorter days begin (speed up time slightly)
execute if predicate seasons:random/8_percent run time add 25

# Harvest moon effect - extra light at night
execute if score #global seasons_day matches 15..20 run execute as @a at @s run fill ~-5 ~10 ~-5 ~5 ~15 ~5 minecraft:light[level=8] replace minecraft:air