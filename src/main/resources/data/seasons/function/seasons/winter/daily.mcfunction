# Winter Daily Effects

# Set growth modifier (very slow growth)
scoreboard players set #global seasons_growth 25

# Snow weather more common
execute if predicate seasons:random/20_percent run weather rain 600
execute if predicate seasons:random/15_percent run weather thunder 300

# Shorter daylight hours (speed up day time)
execute if predicate seasons:random/10_percent run time add 100

# Ice fishing - frozen water sources
execute as @a at @s run execute positioned ~-15 ~-8 ~-15 if predicate seasons:random/5_percent run fill ~0 ~0 ~0 ~30 ~5 ~30 minecraft:ice replace minecraft:water

# Animals move slower in cold
execute as @e[type=#seasons:farm_animals] run effect give @s minecraft:slowness 10 0 true

# Players need warmth near fire sources
execute as @a unless entity @s[distance=..5,predicate=seasons:near_heat_source] run effect give @s minecraft:hunger 2 0 true

# Wolves become more aggressive (hunger makes them hostile)
execute as @e[type=minecraft:wolf,predicate=!seasons:is_tamed] run data modify entity @s AngerTime set value 200