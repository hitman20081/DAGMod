# Modify crop growth based on current season

# Spring: Boost crop growth (150% speed)
execute if score #global seasons_current matches 1 run execute as @e[type=minecraft:area_effect_cloud,tag=seasons_growth_boost] at @s run effect give @e[type=!player,distance=..10] minecraft:speed 1 0 true

# Summer: Normal growth with bonus yields (100% speed, bonus drops)
execute if score #global seasons_current matches 2 run function seasons:growth/summer_bonus

# Fall: Slower growth but trees drop more (75% speed)
execute if score #global seasons_current matches 3 run function seasons:growth/fall_effects

# Winter: Significantly slower growth (25% speed)
execute if score #global seasons_current matches 4 run function seasons:growth/winter_effects

# Apply growth modifiers to farmland
execute if score #global seasons_current matches 1 run execute as @a at @s run fill ~-8 ~-3 ~-8 ~8 ~3 ~8 minecraft:farmland[moisture=7] replace minecraft:farmland[moisture=0]
execute if score #global seasons_current matches 1 run execute as @a at @s run fill ~-8 ~-3 ~-8 ~8 ~3 ~8 minecraft:farmland[moisture=7] replace minecraft:farmland[moisture=1]
execute if score #global seasons_current matches 1 run execute as @a at @s run fill ~-8 ~-3 ~-8 ~8 ~3 ~8 minecraft:farmland[moisture=7] replace minecraft:farmland[moisture=2]