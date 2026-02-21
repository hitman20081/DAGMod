# Set Season Command
# Usage: /function seasons:commands/set_season

tellraw @s ["",{"text":"=== Set Season ===","color":"gold","bold":true}]
tellraw @s ["",{"text":"Use these commands to change seasons:","color":"gray"}]
tellraw @s ["",{"text":"Spring: ","color":"green"},{"text":"/scoreboard players set #global seasons_current 1","color":"yellow"}]
tellraw @s ["",{"text":"Summer: ","color":"gold"},{"text":"/scoreboard players set #global seasons_current 2","color":"yellow"}]
tellraw @s ["",{"text":"Fall: ","color":"red"},{"text":"/scoreboard players set #global seasons_current 3","color":"yellow"}]
tellraw @s ["",{"text":"Winter: ","color":"aqua"},{"text":"/scoreboard players set #global seasons_current 4","color":"yellow"}]

scoreboard players set #global seasons_day 1