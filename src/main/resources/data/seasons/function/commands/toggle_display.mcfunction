# Toggle action bar display on/off

execute unless score #enable_display seasons_config matches 0 run scoreboard players set #enable_display seasons_config 0
execute unless score #enable_display seasons_config matches 1 unless score #enable_display seasons_config matches 0 run scoreboard players set #enable_display seasons_config 1

execute if score #enable_display seasons_config matches 1 run tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Action bar display enabled","color":"green"}]
execute if score #enable_display seasons_config matches 0 run tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Action bar display disabled","color":"red"}]
execute if score #enable_display seasons_config matches 0 run title @a actionbar ""