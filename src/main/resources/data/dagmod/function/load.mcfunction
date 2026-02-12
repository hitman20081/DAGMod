# DAGMod datapack initialization

# Boss system scoreboards
scoreboard objectives add spider_timer dummy
scoreboard objectives add skeleton_timer dummy
scoreboard objectives add skeleton_king dummy

# Dimensions/spawn config scoreboard
scoreboard objectives add dagmod.config dummy

# GUI system scoreboards
scoreboard objectives add ui dummy
scoreboard objectives add ui.id dummy
setblock 0 -64 0 yellow_shulker_box

# Run spawn setup
function dagmod:setup_spawn

say DAGMod data pack loaded successfully!
