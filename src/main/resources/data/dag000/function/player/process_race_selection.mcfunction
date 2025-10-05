execute if score @s has_race matches 0 run tellraw @s {"text":"Now choose your class!","color":"yellow"}

# Mark as needing to select a race


# Process race selection
execute if score @s select_race matches 1 run team join dwarf @s
execute if score @s select_race matches 2 run team join elf @s
execute if score @s select_race matches 3 run team join human @s
execute if score @s select_race matches 4 run team join orc @s

# Give race-specific starting items
execute if score @s select_race matches 1 run function dag000:player/races/dwarf/dwarf_init
execute if score @s select_race matches 2 run function dag000:player/races/elf/elf_init
execute if score @s select_race matches 3 run function dag000:player/races/human/human_init
execute if score @s select_race matches 4 run function dag000:player/races/orc/orc_init

# Mark as having selected a race
scoreboard players set @s select_race 0
scoreboard players set @s has_race 1

# Notify player
title @s title {"text":"Race Selected!","color":"green"}
execute if score @s has_class matches 1 run tellraw @s {"text":"You chose your class!","color":"green"}