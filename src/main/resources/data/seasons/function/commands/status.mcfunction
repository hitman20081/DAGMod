# Show current season status

tellraw @s ["",{"text":"=== Season Status ===","color":"gold","bold":true}]

execute if score #global seasons_current matches 1 run tellraw @s ["",{"text":"Current Season: ","color":"gray"},{"text":"🌸 Spring","color":"green","bold":true}]
execute if score #global seasons_current matches 2 run tellraw @s ["",{"text":"Current Season: ","color":"gray"},{"text":"☀ Summer","color":"gold","bold":true}]
execute if score #global seasons_current matches 3 run tellraw @s ["",{"text":"Current Season: ","color":"gray"},{"text":"🍂 Fall","color":"red","bold":true}]
execute if score #global seasons_current matches 4 run tellraw @s ["",{"text":"Current Season: ","color":"gray"},{"text":"❄ Winter","color":"aqua","bold":true}]

tellraw @s ["",{"text":"Day: ","color":"gray"},{"score":{"name":"#global","objective":"seasons_day"},"color":"white"},{"text":" / ","color":"gray"},{"score":{"name":"#season_length","objective":"seasons_config"},"color":"white"}]

execute if score #enable_weather seasons_config matches 1 run tellraw @s ["",{"text":"Weather Effects: ","color":"gray"},{"text":"Enabled","color":"green"}]
execute if score #enable_weather seasons_config matches 0 run tellraw @s ["",{"text":"Weather Effects: ","color":"gray"},{"text":"Disabled","color":"red"}]

execute if score #enable_growth seasons_config matches 1 run tellraw @s ["",{"text":"Growth Effects: ","color":"gray"},{"text":"Enabled","color":"green"}]
execute if score #enable_growth seasons_config matches 0 run tellraw @s ["",{"text":"Growth Effects: ","color":"gray"},{"text":"Disabled","color":"red"}]

execute if score #enable_temperature seasons_config matches 1 run tellraw @s ["",{"text":"Temperature System: ","color":"gray"},{"text":"Enabled","color":"green"}]
execute if score #enable_temperature seasons_config matches 0 run tellraw @s ["",{"text":"Temperature System: ","color":"gray"},{"text":"Disabled","color":"red"}]