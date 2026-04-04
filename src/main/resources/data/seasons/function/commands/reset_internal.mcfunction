scoreboard players set #global seasons_current 1
scoreboard players set #global seasons_day 1
tellraw @s ["",{"text":"[Seasons] ","color":"gold","bold":true},{"text":"Seasons reset to Spring, Day 1.","color":"green"}]
function seasons:display/show_season
