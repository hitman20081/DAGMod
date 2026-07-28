# Day-based season system using actual Minecraft days

# Detect new Minecraft day using daytime (0-23999).
# time query day is unreliable in MC 26.2 (increments every tick).
# Instead: fire new_game_day once per day when daytime is in the first
# 100 ticks (dawn), guarded by #day_processed so it only fires once.
execute store result score #current_daytime seasons_timer run time query daytime

# Fire new_game_day at dawn (ticks 0-99) if not already processed today
execute if score #current_daytime seasons_timer matches 0..99 unless score #day_processed seasons_timer matches 1 run function seasons:progress/new_game_day
execute if score #current_daytime seasons_timer matches 0..99 run scoreboard players set #day_processed seasons_timer 1
execute unless score #current_daytime seasons_timer matches 0..99 run scoreboard players set #day_processed seasons_timer 0

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
schedule function seasons:tick_day_based 1t replace