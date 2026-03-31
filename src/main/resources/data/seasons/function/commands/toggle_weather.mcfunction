# Toggle weather effects on/off

scoreboard players set #toggle_temp seasons_config 0
execute if score #enable_weather seasons_config matches 1 run scoreboard players set #toggle_temp seasons_config 1
execute if score #toggle_temp seasons_config matches 1 run scoreboard players set #enable_weather seasons_config 0
execute if score #toggle_temp seasons_config matches 0 run scoreboard players set #enable_weather seasons_config 1

execute if score #enable_weather seasons_config matches 1 run tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Weather effects enabled","color":"green"}]
execute if score #enable_weather seasons_config matches 0 run tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Weather effects disabled","color":"red"}]