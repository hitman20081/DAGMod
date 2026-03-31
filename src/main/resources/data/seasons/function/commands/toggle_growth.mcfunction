# Toggle growth effects on/off

scoreboard players set #toggle_temp seasons_config 0
execute if score #enable_growth seasons_config matches 1 run scoreboard players set #toggle_temp seasons_config 1
execute if score #toggle_temp seasons_config matches 1 run scoreboard players set #enable_growth seasons_config 0
execute if score #toggle_temp seasons_config matches 0 run scoreboard players set #enable_growth seasons_config 1

execute if score #enable_growth seasons_config matches 1 run tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Growth effects enabled","color":"green"}]
execute if score #enable_growth seasons_config matches 0 run tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Growth effects disabled","color":"red"}]