# Copy last item
#data modify storage id:data drop.item set from storage id:data inventory[-1]

# Summon that item
#function dag000:summon/summon_items_macro with storage id:data drop


# Run again if there still are items
#function dag000:summon/summon_items with storage id:data inventory[-1]
