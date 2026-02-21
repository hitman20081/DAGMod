# Apply seasonal effects to individual players

# Temperature effects based on biome and season
execute if score #enable_temperature seasons_config matches 1 run function seasons:temperature/calculate

# Season-specific player effects
execute if score #global seasons_current matches 1 run function seasons:effects/spring_player
execute if score #global seasons_current matches 2 run function seasons:effects/summer_player
execute if score #global seasons_current matches 3 run function seasons:effects/fall_player
execute if score #global seasons_current matches 4 run function seasons:effects/winter_player