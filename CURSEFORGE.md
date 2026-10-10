# CurseForge Release Sheet

## Project

- **Name:** Experience Emerald
- **Summary:** Store experience in emeralds, batch-convert whole stacks, and recover the stored experience safely with a grindstone.
- **Author:** ArborElvel
- **License:** LGPL-3.0-or-later
- **Source:** https://github.com/ArborElvel/ExperienceEmerald
- **Issue tracker:** https://github.com/ArborElvel/ExperienceEmerald/issues
- **Environment:** Client and server
- **Game version:** Minecraft 1.21.1
- **Mod loader:** NeoForge
- **Required dependency:** NeoForge 21.1.238 or newer
- **Release type:** Release
- **File to upload:** `build/libs/experience_emerald-1.0.0.jar`

## Description

Experience Emerald adds a simple experience-storage loop for NeoForge 1.21.1.

Right-click while holding emeralds to absorb nearby experience orbs and the player's
own experience. The mod converts as many emeralds as the available experience can
pay for in one batch, leaving any unaffordable emeralds in the player's hand.

Each Infused Emerald can later be placed in a grindstone to recover a normal
emerald and the configured amount of experience. The conversion cost, cooldown,
orb scan radius, sound and particle feedback, and grindstone return behavior can
all be adjusted in the common config.

## Features

- Batch-convert held emeralds based on available experience.
- Prioritize nearby experience orbs before using the player's own experience.
- Support partial conversion when only part of the held stack is affordable.
- Return emeralds and experience safely through a grindstone.
- Configurable conversion and feedback values.
- English and Simplified Chinese localization.

## Changelog

See `CHANGELOG.md`.

## Upload Checklist

- Build with `.\gradlew.bat clean build`.
- Upload `build/libs/experience_emerald-1.0.0.jar`.
- Set the project license to `LGPL-3.0-or-later`.
- Select Minecraft 1.21.1 and NeoForge.
- Mark the file as a Release.
- Add the GitHub repository and issue tracker links.
- Add a project logo and in-game screenshots if they are not already configured.
