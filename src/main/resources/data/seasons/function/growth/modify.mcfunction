# Modify crop growth based on current season

# Spring: randomTickSpeed handled in spring/daily.mcfunction and spring/transition.mcfunction
# Keep farmland moist near players so crops don't stall waiting for hydration
execute if score #global seasons_current matches 1 run execute as @a at @s run fill ~-8 ~-3 ~-8 ~8 ~1 ~8 minecraft:farmland[moisture=7] replace minecraft:farmland

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