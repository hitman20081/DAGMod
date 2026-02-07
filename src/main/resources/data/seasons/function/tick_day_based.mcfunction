# Day-based season system using actual Minecraft days

# Get current game day
execute store result score #current_game_day seasons_timer run time query day

# Check if we've moved to a new game day
execute unless score #current_game_day seasons_timer = #last_game_day seasons_timer run function seasons:progress/new_game_day

# Update last known game day
scoreboard players operation #last_game_day seasons_timer = #current_game_day seasons_timer

# Apply seasonal effects to all players
execute as @a run function seasons:effects/apply_to_player

# Handle seasonal weather (every 200 ticks = 10 seconds)
execute if score #weather_timer seasons_timer matches 200.. if score #enable_weather seasons_config matches 1 run function seasons:weather/manage
execute if score #weather_timer seasons_timer matches 200.. run scoreboard players set #weather_timer seasons_timer 0
scoreboard players add #weather_timer seasons_timer 1

# Handle crop growth modifications (every 100 ticks = 5 seconds)
execute if score #growth_timer seasons_timer matches 100.. if score #enable_growth seasons_config matches 1 run function seasons:growth/modify
execute if score #growth_timer seasons_timer matches 100.. run scoreboard players set #growth_timer seasons_timer 0
scoreboard players add #growth_timer seasons_timer 1

# Display season info (every 600 ticks = 30 seconds)
execute if score #display_timer seasons_timer matches 6000.. run function seasons:display/update_actionbar
execute if score #display_timer seasons_timer matches 6000.. run scoreboard players set #display_timer seasons_timer 0
scoreboard players add #display_timer seasons_timer 1

# Schedule next tick
schedule function seasons:tick_day_based 1t