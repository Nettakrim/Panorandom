# Panorandom

picks a random panorama from all enabled resourcepacks to be displayed, instead of just the topmost one

the [Mod Menu](https://modrinth.com/mod/modmenu) config allows you to change when it re-rolls the panorama, as well as allowing you to disable certain panoramas from showing up (eg the vanilla one)

### Resourcepacks

a single resourcepack can have multiple panoramas, the mod will use any complete set of `minecraft/textures/gui/title/background/[name]_[0-5].png`

this means you could have files in a resourcepack named `.../background/nether_0.png`, `.../background/nether_1.png` etc and it can pick that as the panorama, or you could put them into a subfolder, so its `".../background/nether/panorama_0.png` etc - as long as all numbers 0 to 5 are there it will count