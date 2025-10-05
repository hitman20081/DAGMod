# Calculate temperature based on biome and season

# Base temperature from biome
execute if predicate seasons:biomes/hot_biomes run scoreboard players set @s seasons_temp 80
execute if predicate seasons:biomes/cold_biomes run scoreboard players set @s seasons_temp 20
execute unless predicate seasons:biomes/hot_biomes unless predicate seasons:biomes/cold_biomes run scoreboard players set @s seasons_temp 50

# Seasonal modifiers
execute if score #global seasons_current matches 1 run scoreboard players add @s seasons_temp 10
execute if score #global seasons_current matches 2 run scoreboard players add @s seasons_temp 25
execute if score #global seasons_current matches 3 run scoreboard players remove @s seasons_temp 10
execute if score #global seasons_current matches 4 run scoreboard players remove @s seasons_temp 30

# Heat sources nearby increase temperature
execute if block ~ ~-1 ~ minecraft:lava run scoreboard players add @s seasons_temp 40
execute if block ~ ~-1 ~ minecraft:magma_block run scoreboard players add @s seasons_temp 20
execute if entity @e[type=minecraft:area_effect_cloud,tag=fire_source,distance=..3] run scoreboard players add @s seasons_temp 15