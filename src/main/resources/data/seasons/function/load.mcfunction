# Seasons Datapack - Load Function
# Creates all scoreboards and initializes the day-based system

tellraw @a ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Datapack loaded successfully!","color":"green"}]

# Create scoreboards
scoreboard objectives add seasons_day dummy "Current Season Day"
scoreboard objectives add seasons_current dummy "Current Season"
scoreboard objectives add seasons_timer dummy "Season Timer" 
scoreboard objectives add seasons_temp dummy "Temperature"
scoreboard objectives add seasons_growth dummy "Growth Modifier"
scoreboard objectives add seasons_config dummy "Season Config"

# Initialize global season variables if they don't exist
execute unless score #global seasons_current matches 1.. run scoreboard players set #global seasons_current 1
execute unless score #global seasons_day matches 0.. run scoreboard players set #global seasons_day 1

# Initialize day tracking system
execute store result score #current_game_day seasons_timer run time query day
execute store result score #last_game_day seasons_timer run time query day

# Initialize timers
execute unless score #display_timer seasons_timer matches 0.. run scoreboard players set #display_timer seasons_timer 0
execute unless score #weather_timer seasons_timer matches 0.. run scoreboard players set #weather_timer seasons_timer 0
execute unless score #growth_timer seasons_timer matches 0.. run scoreboard players set #growth_timer seasons_timer 0

# Set default configurations
scoreboard players set #season_length seasons_config 20
scoreboard players set #enable_weather seasons_config 1
scoreboard players set #enable_growth seasons_config 1
scoreboard players set #enable_temperature seasons_config 1
scoreboard players set #enable_display seasons_config 1
scoreboard players set #announce_days seasons_config 0

# Display current season
function seasons:display/show_season

# Start the day-based tick function
schedule function seasons:tick_day_based 1t

tellraw @a ["",{"text":"[Seasons] ","color":"gold"},{"text":"Now tracking real Minecraft days! Sleep away!","color":"green"}]