# Manual setup command to ensure all scoreboards exist

tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Setting up scoreboards...","color":"yellow"}]

# Force create all scoreboards (will give error if they exist, but that's okay)
scoreboard objectives add seasons_day dummy "Current Season Day"
scoreboard objectives add seasons_current dummy "Current Season" 
scoreboard objectives add seasons_timer dummy "Season Timer"
scoreboard objectives add seasons_temp dummy "Temperature"
scoreboard objectives add seasons_growth dummy "Growth Modifier"
scoreboard objectives add seasons_config dummy "Season Config"

# Initialize all scores
scoreboard players set #global seasons_current 1
scoreboard players set #global seasons_day 1
scoreboard players set #global seasons_timer 0
scoreboard players set #timer seasons_timer 0
scoreboard players set #display_timer seasons_timer 0

# Set default configurations
scoreboard players set #season_length seasons_config 20
scoreboard players set #enable_weather seasons_config 1
scoreboard players set #enable_growth seasons_config 1
scoreboard players set #enable_temperature seasons_config 1

tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Setup complete! Current season set to Spring.","color":"green"}]

# Show status
function seasons:commands/status