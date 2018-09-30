#!/usr/bin/env python3

from PIL import Image
import os, json

ASSETS_ROOT = os.path.join("src", "main", "resources", "assets")

fullsheet = Image.open(os.path.join(ASSETS_ROOT, "armorchroma", "textures", "gui", "armor_icons.png"))
data = json.load(open(os.path.join(ASSETS_ROOT, "armorchroma", "icons.json"), "r"))

def get_icon(i):
    SPAN = 256 // 9

    if i >= 0:
        u = (i % SPAN) * 9
        v = i // SPAN * 9
    else:
        u = 256 - ((-i - 1) % SPAN) * 9 - 9
        v = 256 + ((i+1) // SPAN - 1) * 9

    return (u, v, u+9, v+9)

for modid, mod in data.items():
    modsheet = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    i = 0
    j = -1
    collapse = {}

    for group in mod.values():
        for key, value in group.items():
            if value not in collapse:
                if value >= 0:
                    mapped = i
                    i += 1
                else:
                    mapped = j
                    j -= 1

                modsheet.paste(fullsheet.crop(get_icon(value)), get_icon(mapped))
                collapse[value] = mapped

            group[key] = collapse[value]

    mod_root = os.path.join(ASSETS_ROOT, modid, "textures", "gui")
    os.makedirs(mod_root, exist_ok=True)

    modsheet.save(os.path.join(mod_root, "armor_chroma.png"))
    with open(os.path.join(mod_root, "armor_chroma.json"), "w") as f:
        json.dump(mod, f, indent=4)
        print(file=f) # Add newline
