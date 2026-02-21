# Advance to the next season
scoreboard players add #global seasons_current 1
scoreboard players set #global seasons_day 1

# Wrap around from winter (4) back to spring (1)
execute if score #global seasons_current matches 5.. run scoreboard players set #global seasons_current 1

# Announce season change to all players
execute if score #global seasons_current matches 1 run tellraw @a ["",{"text":"🌸 ","color":"green"},{"text":"Spring has arrived! ","color":"green","bold":true},{"text":"Plants grow faster and flowers bloom.","color":"dark_green"}]
execute if score #global seasons_current matches 2 run tellraw @a ["",{"text":"☀ ","color":"yellow"},{"text":"Summer is here! ","color":"gold","bold":true},{"text":"Crops yield more and days are longer.","color":"yellow"}]
execute if score #global seasons_current matches 3 run tellraw @a ["",{"text":"🍂 ","color":"red"},{"text":"Fall begins! ","color":"red","bold":true},{"text":"Harvest time - trees drop more resources.","color":"dark_red"}]
execute if score #global seasons_current matches 4 run tellraw @a ["",{"text":"❄ ","color":"aqua"},{"text":"Winter arrives! ","color":"aqua","bold":true},{"text":"Bundle up - it's getting cold!","color":"dark_aqua"}]

# Play sound effect for season change
execute at @a run playsound minecraft:entity.experience_orb.pickup master @a ~ ~ ~ 1 0.5

# Apply season transition effects
execute if score #global seasons_current matches 1 run function seasons:seasons/spring/transition
execute if score #global seasons_current matches 2 run function seasons:seasons/summer/transition
execute if score #global seasons_current matches 3 run function seasons:seasons/fall/transition
execute if score #global seasons_current matches 4 run function seasons:seasons/winter/transition