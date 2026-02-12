data modify storage ui mask set from storage dagmod current
execute on passengers run function dagmod:menu/get_mask with entity @s data.page
data modify storage ui current set from storage ui mask

execute if score .type ui matches 1 run function dagmod:minecart/load_page