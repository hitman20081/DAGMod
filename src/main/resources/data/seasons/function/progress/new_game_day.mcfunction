# Called when a new Minecraft day begins

# Increment season day
scoreboard players add #global seasons_day 1

# Check if we need to advance to the next season
execute if score #global seasons_day > #season_length seasons_config run function seasons:progress/advance_season

# Apply daily effects based on current season
execute if score #global seasons_current matches 1 run function seasons:seasons/spring/daily
execute if score #global seasons_current matches 2 run function seasons:seasons/summer/daily
execute if score #global seasons_current matches 3 run function seasons:seasons/fall/daily
execute if score #global seasons_current matches 4 run function seasons:seasons/winter/daily

# Announce new day (optional, can be disabled)
execute if score #announce_days seasons_config matches 1 run tellraw @a ["",{"text":"[Seasons] ","color":"gold"},{"text":"Day ","color":"gray"},{"score":{"name":"#global","objective":"seasons_day"},"color":"white"},{"text":" of ","color":"gray"},{"score":{"name":"#season_length","objective":"seasons_config"},"color":"white"}]