# Switch to day-based season system

# Stop old tick system
schedule clear seasons:tick
schedule clear seasons:tick_fast

# Initialize day tracking
execute store result score #current_game_day seasons_timer run time query day
execute store result score #last_game_day seasons_timer run time query day

# Reset timers for new system
scoreboard players set #display_timer seasons_timer 0
scoreboard players set #weather_timer seasons_timer 0
scoreboard players set #growth_timer seasons_timer 0

# Start new day-based system
schedule function seasons:tick_day_based 1t

tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Switched to day-based system!","color":"green"}]
tellraw @s ["",{"text":"Seasons now advance every Minecraft day.","color":"yellow"}]
tellraw @s ["",{"text":"Sleep cycles and time changes will work properly!","color":"yellow"}]