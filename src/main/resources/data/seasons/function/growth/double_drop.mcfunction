# Double Drop Function for Summer Bonus
# Chance to duplicate crop drops

execute if predicate seasons:random/30_percent run summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:wheat",count:1},Motion:[0.0,0.1,0.0]}
execute if predicate seasons:random/30_percent run particle minecraft:happy_villager ~ ~ ~ 0.5 0.5 0.5 0.1 5