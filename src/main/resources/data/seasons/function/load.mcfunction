# Seasons Datapack - Load Function
# Creates all scoreboards and conditionally starts the tick loop

# Create scoreboards (no-op if they already exist — scores are preserved)
scoreboard objectives add seasons_day dummy "Current Season Day"
scoreboard objectives add seasons_current dummy "Current Season"
scoreboard objectives add seasons_timer dummy "Season Timer"
scoreboard objectives add seasons_temp dummy "Temperature"
scoreboard objectives add seasons_growth dummy "Growth Modifier"
scoreboard objectives add seasons_config dummy "Season Config"

# Initialize global season variables if they don't exist
execute unless score #global seasons_current matches 1.. run scoreboard players set #global seasons_current 1
execute unless score #global seasons_day matches 1.. run scoreboard players set #global seasons_day 1

# Initialize natural day tick counter
execute unless score #day_tick_counter seasons_timer matches 0.. run scoreboard players set #day_tick_counter seasons_timer 0

# Create sleep tracking objective and set baseline so first load doesn't spuriously fire
scoreboard objectives add dagmod_sleep_track minecraft.custom:minecraft.sleep_in_bed "Sleep Tracker"
scoreboard players set #sleep_sum seasons_timer 0
execute as @a run scoreboard players operation #sleep_sum seasons_timer += @s dagmod_sleep_track
scoreboard players operation #sleep_last seasons_timer = #sleep_sum seasons_timer

# Initialize timers (only if missing)
execute unless score #display_timer seasons_timer matches 0.. run scoreboard players set #display_timer seasons_timer 0
execute unless score #weather_timer seasons_timer matches 0.. run scoreboard players set #weather_timer seasons_timer 0
execute unless score #growth_timer seasons_timer matches 0.. run scoreboard players set #growth_timer seasons_timer 0

# Set default configurations only if missing (preserves player settings across restarts)
execute unless score #season_length seasons_config matches 1.. run scoreboard players set #season_length seasons_config 20
execute unless score #enable_weather seasons_config matches 0.. run scoreboard players set #enable_weather seasons_config 1
execute unless score #enable_growth seasons_config matches 0.. run scoreboard players set #enable_growth seasons_config 1
execute unless score #enable_temperature seasons_config matches 0.. run scoreboard players set #enable_temperature seasons_config 1
execute unless score #enable_display seasons_config matches 0.. run scoreboard players set #enable_display seasons_config 1
execute unless score #announce_days seasons_config matches 0.. run scoreboard players set #announce_days seasons_config 0

# If already configured: resume the tick loop and show season
execute if score #seasons_initialized seasons_config matches 1 run schedule function seasons:tick_day_based 1t replace
execute if score #seasons_initialized seasons_config matches 1 run scoreboard players set #seasons_running seasons_config 1
execute if score #seasons_initialized seasons_config matches 1 run function seasons:display/show_season

# If not configured: prompt server operators to run setup
execute unless score #seasons_initialized seasons_config matches 1 run function seasons:commands/first_run_notice