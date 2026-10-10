# Modrinth Release Sheet

## Project

- **Name:** Experience Emerald
- **Suggested slug:** `experience-emerald`
- **Summary:** Store experience in emeralds, batch-convert whole stacks, and recover stored experience safely with a grindstone.
- **Project type:** Mod
- **Author:** Unddefined
- **License:** LGPL-3.0-or-later
- **Source:** https://github.com/ArborElvel/ExperienceEmerald
- **Issue tracker:** https://github.com/ArborElvel/ExperienceEmerald/issues
- **Environment:** Client and server

## Tags

- Game Mechanics
- Storage
- Utility

## Version

- **Name:** Experience Emerald 1.0.0
- **Version number:** `1.0.0`
- **Release channel:** Release
- **Game version:** Minecraft 1.21.1
- **Mod loader:** NeoForge
- **File to upload:** `build/libs/experience_emerald-1.0.0.jar`
- **Required dependency:** NeoForge 21.1.238 or newer

## Description

Experience Emerald adds a simple experience-storage loop for NeoForge 1.21.1.

Right-click while holding emeralds to absorb nearby experience orbs and the player's
own experience. The mod converts as many emeralds as the available experience can
pay for in one batch, leaving any unaffordable emeralds in the player's hand.

Each Infused Emerald can later be placed in a grindstone to recover a normal
emerald and the configured amount of experience. Conversion costs, cooldowns,
orb scan radius, sound and particle feedback, and grindstone return behavior can
all be adjusted in the common config.

## Changelog

The complete changelog is maintained in `CHANGELOG.md`. For version `1.0.0`, use:

```markdown
Initial Modrinth release.

- Added the Infused Emerald item.
- Added batch conversion based on available experience.
- Prioritized nearby experience orbs before the player's own experience.
- Added partial conversion when only part of the held stack is affordable.
- Added safe emerald and experience recovery through the grindstone.
- Added English and Simplified Chinese localization.
```

## Upload Checklist

- Build with `.\gradlew.bat clean build`.
- Upload `build/libs/experience_emerald-1.0.0.jar`.
- Select `NeoForge` and Minecraft `1.21.1`.
- Set the release channel to `Release`.
- Set the license to `LGPL-3.0-or-later`.
- Add the GitHub source and issue tracker links.
- Add a project icon and in-game screenshots if they are not already configured.
- Confirm the required NeoForge version is displayed as `21.1.238+`.
