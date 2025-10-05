# Create teams/races
team add dwarf
team add elf
team add human
team add orc

scoreboard objectives add select_race trigger
scoreboard objectives add select_class trigger
scoreboard objectives add has_race dummy
scoreboard objectives add has_class dummy

# Setup scoreboards
scoreboard objectives add ability_cooldown dummy
scoreboard objectives add mana dummy
scoreboard objectives add strength dummy
scoreboard objectives add agility dummy
scoreboard objectives add spell_power dummy
scoreboard objectives add healing_power dummy
scoreboard objectives add mana_regen dummy

# Setup triggers
scoreboard objectives add use_ability trigger
scoreboard objectives add use_class_ability trigger