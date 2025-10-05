# Display current season information when datapack loads

execute if score #global seasons_current matches 1 run tellraw @a ["",{"text":"Current Season: ","color":"gray"},{"text":"🌸 Spring ","color":"green","bold":true},{"text":"(Day ","color":"gray"},{"score":{"name":"#global","objective":"seasons_day"},"color":"white"},{"text":")","color":"gray"}]

execute if score #global seasons_current matches 2 run tellraw @a ["",{"text":"Current Season: ","color":"gray"},{"text":"☀ Summer ","color":"gold","bold":true},{"text":"(Day ","color":"gray"},{"score":{"name":"#global","objective":"seasons_day"},"color":"white"},{"text":")","color":"gray"}]

execute if score #global seasons_current matches 3 run tellraw @a ["",{"text":"Current Season: ","color":"gray"},{"text":"🍂 Fall ","color":"red","bold":true},{"text":"(Day ","color":"gray"},{"score":{"name":"#global","objective":"seasons_day"},"color":"white"},{"text":")","color":"gray"}]

execute if score #global seasons_current matches 4 run tellraw @a ["",{"text":"Current Season: ","color":"gray"},{"text":"❄ Winter ","color":"aqua","bold":true},{"text":"(Day ","color":"gray"},{"score":{"name":"#global","objective":"seasons_day"},"color":"white"},{"text":")","color":"gray"}]