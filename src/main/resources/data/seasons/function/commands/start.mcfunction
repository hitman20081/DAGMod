# Finalizes configuration and starts the seasons tick loop

scoreboard players set #seasons_initialized seasons_config 1

# Initialize season state if this is a fresh start
execute unless score #global seasons_current matches 1.. run scoreboard players set #global seasons_current 1
execute unless score #global seasons_day matches 1.. run scoreboard players set #global seasons_day 1

# Start the tick loop only if not already running
execute unless score #seasons_running seasons_config matches 1 run schedule function seasons:tick_day_based 1t
scoreboard players set #seasons_running seasons_config 1

function seasons:display/show_season
tellraw @a ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"The seasons system is now active!","color":"green"}]
tellraw @s ["",{"text":"[Seasons] ","color":"gold"},{"text":"Run /function seasons:commands/setup any time to adjust settings.","color":"gray"}]
