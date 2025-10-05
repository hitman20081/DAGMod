# Store last item
data modify storage id:data temporary.drop_item set from storage id:data temporary.inventory[-1]

# Summon that item
function dag000:graves/summon_items_macro with storage id:data temporary


# Run again if there still are items
function dag000:graves/summon_items with storage id:data temporary.inventory[-1]
