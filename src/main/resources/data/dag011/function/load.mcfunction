# Test message
tellraw @a {"text":"DAG011 datapack loaded!","color":"green"}

# Initialize scoreboard
scoreboard objectives add dag011.config dummy

# Run spawn setup
function dag011:setup_spawn