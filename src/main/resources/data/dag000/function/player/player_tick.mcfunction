

# Enable triggers for players without selections
execute as @a[scores={has_race=0}] run scoreboard players enable @s select_race
execute as @a[scores={has_class=0}] run scoreboard players enable @s select_class

# Process selections
execute as @a[scores={select_race=1..}] run function dag000:player/process_race_selection
execute as @a[scores={select_class=1..}] run function dag000:player/process_class_selection

# Main game loop - Race abilities
execute as @a[team=elf] run function dag000:player/races/elf/elf_abilities
execute as @a[team=dwarf] run function dag000:player/races/dwarf/dwarf_abilities
execute as @a[team=human] run function dag000:player/races/human/human_abilities
execute as @a[team=orc] run function dag000:player/races/orc/orc_abilities

# Class abilities
execute as @a[tag=warrior] run function dag000:player/classes/warrior/warrior_abilities
execute as @a[tag=mage] run function dag000:player/classes/mage/mage_abilities
execute as @a[tag=ranger] run function dag000:player/classes/ranger/ranger_abilities
execute as @a[tag=cleric] run function dag000:player/classes/cleric/cleric_abilities
