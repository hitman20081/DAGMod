

# Mark as needing to select a class
#scoreboard players set @s select_class 0

# Process class selection
execute if score @s select_class matches 1 run tag @s add cleric
execute if score @s select_class matches 2 run tag @s add mage
execute if score @s select_class matches 3 run tag @s add ranger
execute if score @s select_class matches 4 run tag @s add warrior

# Give class-specific starting items
execute if score @s select_class matches 1 run function dag000:player/classes/cleric/cleric_init
execute if score @s select_class matches 2 run function dag000:player/classes/mage/mage_init
execute if score @s select_class matches 3 run function dag000:player/classes/ranger/ranger_init
execute if score @s select_class matches 4 run function dag000:player/classes/warrior/warrior_init

# Mark as having selected a class
scoreboard players set @s select_class 0
#scoreboard players set @s has_class 1

# Notify player
title @s title {"text":"Class Selected!","color":"green"}
tellraw @s {"text":"Your journey begins!","color":"gold"}