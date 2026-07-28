# Day-based season system using actual Minecraft days

# Natural day counter: fires new_game_day every 24000 real server ticks.
scoreboard players add #day_tick_counter seasons_timer 1
execute if score #day_tick_counter seasons_timer matches 24000.. run function seasons:progress/new_game_day
execute if score #day_tick_counter seasons_timer matches 24000.. run scoreboard players set #day_tick_counter seasons_timer 0

# Sleep detection via minecraft.custom:minecraft.sleep_in_bed stat.
# Sum all online players' sleep counts. If total increased, someone slept —
# fire new_game_day immediately and reset the day counter so it doesn't
# double-count 24000 ticks later.
scoreboard players set #sleep_sum seasons_timer 0
execute as @a run scoreboard players operation #sleep_sum seasons_timer += @s dagmod_sleep_track
execute if score #sleep_sum seasons_timer > #sleep_last seasons_timer run function seasons:progress/new_game_day
execute if score #sleep_sum seasons_timer > #sleep_last seasons_timer run scoreboard players set #day_tick_counter seasons_timer 0
scoreboard players operation #sleep_last seasons_timer = #sleep_sum seasons_timer

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
execute if score #display_timer seasons_timer matches 600.. run function seasons:display/update_actionbar
execute if score #display_timer seasons_timer matches 600.. run scoreboard players set #display_timer seasons_timer 0
scoreboard players add #display_timer seasons_timer 1

# Schedule next tick
schedule function seasons:tick_day_based 1t replace