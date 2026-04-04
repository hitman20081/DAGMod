# Seasons Configuration Menu
# Run this to configure seasons before starting

tellraw @s ["",{"text":"══════════════════════════════════","color":"gold"}]
tellraw @s ["",{"text":"        ⚙  SEASONS SETUP","color":"gold","bold":true}]
tellraw @s ["",{"text":"══════════════════════════════════","color":"gold"}]
tellraw @s ""

# Season length selector
tellraw @s ["",{"text":"  Season Length  ","color":"gray"},{"text":"[ 7d ]","color":"yellow","clickEvent":{"type":"run_command","command":"/function seasons:commands/set_length_7"},"hoverEvent":{"type":"show_text","value":{"text":"7 days per season"}}},{"text":"  "},{"text":"[ 14d ]","color":"yellow","clickEvent":{"type":"run_command","command":"/function seasons:commands/set_length_14"},"hoverEvent":{"type":"show_text","value":{"text":"14 days per season"}}},{"text":"  "},{"text":"[ 20d ]","color":"yellow","clickEvent":{"type":"run_command","command":"/function seasons:commands/set_length_20"},"hoverEvent":{"type":"show_text","value":{"text":"20 days per season"}}},{"text":"  "},{"text":"[ 28d ]","color":"yellow","clickEvent":{"type":"run_command","command":"/function seasons:commands/set_length_28"},"hoverEvent":{"type":"show_text","value":{"text":"28 days per season"}}}]
tellraw @s ["",{"text":"  Currently: ","color":"dark_gray"},{"score":{"name":"#season_length","objective":"seasons_config"},"color":"white"},{"text":" days per season","color":"dark_gray"}]
tellraw @s ""

# Weather Effects toggle
execute if score #enable_weather seasons_config matches 1 run tellraw @s ["",{"text":"  Weather Effects   ","color":"gray"},{"text":"[✔ ON]","color":"green","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/weather_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}},{"text":"  "},{"text":"[ OFF ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/weather_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}}]
execute unless score #enable_weather seasons_config matches 1 run tellraw @s ["",{"text":"  Weather Effects   ","color":"gray"},{"text":"[ ON ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/weather_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}},{"text":"  "},{"text":"[✘ OFF]","color":"red","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/weather_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}}]

# Growth Effects toggle
execute if score #enable_growth seasons_config matches 1 run tellraw @s ["",{"text":"  Growth Effects    ","color":"gray"},{"text":"[✔ ON]","color":"green","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/growth_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}},{"text":"  "},{"text":"[ OFF ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/growth_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}}]
execute unless score #enable_growth seasons_config matches 1 run tellraw @s ["",{"text":"  Growth Effects    ","color":"gray"},{"text":"[ ON ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/growth_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}},{"text":"  "},{"text":"[✘ OFF]","color":"red","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/growth_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}}]

# Temperature toggle
execute if score #enable_temperature seasons_config matches 1 run tellraw @s ["",{"text":"  Temperature       ","color":"gray"},{"text":"[✔ ON]","color":"green","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/temperature_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}},{"text":"  "},{"text":"[ OFF ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/temperature_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}}]
execute unless score #enable_temperature seasons_config matches 1 run tellraw @s ["",{"text":"  Temperature       ","color":"gray"},{"text":"[ ON ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/temperature_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}},{"text":"  "},{"text":"[✘ OFF]","color":"red","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/temperature_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}}]

# Season Display toggle
execute if score #enable_display seasons_config matches 1 run tellraw @s ["",{"text":"  Season Display    ","color":"gray"},{"text":"[✔ ON]","color":"green","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/display_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}},{"text":"  "},{"text":"[ OFF ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/display_off"},"hoverEvent":{"type":"show_text","value":{"text":"Click to disable"}}}]
execute unless score #enable_display seasons_config matches 1 run tellraw @s ["",{"text":"  Season Display    ","color":"gray"},{"text":"[ ON ]","color":"dark_gray","clickEvent":{"type":"run_command","command":"/function seasons:commands/display_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}},{"text":"  "},{"text":"[✘ OFF]","color":"red","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/display_on"},"hoverEvent":{"type":"show_text","value":{"text":"Click to enable"}}}]

tellraw @s ""

# Start button (not yet initialized) or status + reset (already running)
execute unless score #seasons_initialized seasons_config matches 1 run tellraw @s ["",{"text":"  "},{"text":"[ ▶  START SEASONS ]","color":"green","bold":true,"clickEvent":{"type":"run_command","command":"/function seasons:commands/start"},"hoverEvent":{"type":"show_text","value":{"text":"Save settings and start the seasons system"}}}]
execute if score #seasons_initialized seasons_config matches 1 run tellraw @s ["",{"text":"  "},{"text":"✔ Seasons is running","color":"green"},{"text":"   "},{"text":"[ ⚠ Reset to Spring Day 1 ]","color":"dark_red","clickEvent":{"type":"run_command","command":"/function seasons:commands/reset"},"hoverEvent":{"type":"show_text","value":{"text":"Reset season progression to Spring, Day 1"}}}]

tellraw @s ["",{"text":"══════════════════════════════════","color":"gold"}]
