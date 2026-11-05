# Draggable Lists for Minecraft 26.3

Drag servers and selected resource packs to reorder them, instead of pressing the arrows over and over.

This is an unofficial port of [Draggable Lists](https://github.com/MrMelon54/draggable_lists) by MrMelon54 to Minecraft 26.3. The original stopped at 1.21.8. Logo by [@CarbonGhost](https://github.com/CarbonGhost).

## Install

Fabric Loader 0.19.5 or newer and Java 25. Client-side only. No other mods are needed: this port no longer depends on Fabric API, Architectury or Cloth Config.

## Use

- Hold the left mouse button on a row, to the right of its icon, and drag it to a new place.
- The list scrolls when you hold the row at its top or bottom edge.
- Only selected resource packs can be dragged. Packs with a fixed position stay where they are.

## Settings

`config/draggable_lists.properties`:

| Key | Default | Meaning |
|---|---|---|
| `disableResourcePackArrows` | `true` | Hide the move arrows on pack rows |
| `disableServerArrows` | `true` | Hide the move arrows on server rows |
| `resourcePackDraggingEnabled` | `ENABLED` | `DISABLED`, `REQUIRES_MODIFIER` (hold Shift) or `ENABLED` |
| `serverDraggingEnabled` | `ENABLED` | Same values |

## Changes from the original

- Rewritten for the 26.x list widgets, where every row owns its position.
- One Fabric project. The NeoForge and Quilt builds are gone.
- The in-game config screen is gone, settings live in the file above.
- Dragging works with a search filter in the pack screen: positions come from the pack model.
- Only a press on a row can start a drag, so moving the mouse over the list with the button held does nothing.
- `disableServerArrows=false` now really brings the server arrows back.

## Build

```
./gradlew build
```

The jar is in `build/libs`. Needs JDK 26.

## License

The original declares `GPL-3.0-only` in its mod metadata and on Modrinth, while its repository holds the LGPL 3 text in `LICENSE.md`. This port is distributed under GPL 3, which both allow: `LICENSE-GPL-3.0.txt` holds the full text, and the original `LICENSE.md` stays as it was.
