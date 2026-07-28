# Summer Daily Effects

# Normal random tick speed
gamerule random_tick_speed 3
scoreboard players set #global seasons_growth 100

# Clear skies more often
execute if predicate seasons:random/15_percent run weather clear 1200

# Occasional thunderstorms for drama
execute if predicate seasons:random/3_percent run weather thunder 400

# Desert becomes more dangerous
execute as @a[predicate=seasons:biomes/hot_biomes] run effect give @s minecraft:hunger 3 0 true

# Cacti grow faster in deserts
execute as @a[predicate=seasons:biomes/desert] at @s run execute positioned ~-10 ~-5 ~-10 if predicate seasons:random/8_percent run fill ~0 ~0 ~0 ~20 ~10 ~20 minecraft:cactus[age=15] replace minecraft:cactus[age=14]