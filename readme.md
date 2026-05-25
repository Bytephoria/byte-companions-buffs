# ByteCompanionsBuffs

<p align="center">
  <a href="https://www.java.com/">
    <img src="https://img.shields.io/badge/Java-21+-blue" alt="Java"/>
  </a>
  <a href="https://papermc.io/">
    <img src="https://img.shields.io/badge/PaperMC-1.21%2B-green" alt="PaperMC"/>
  </a>
  <a href="license">
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/>
  </a>
  <a href="https://discord.com/invite/3K9yrZQRmS">
    <img src="https://img.shields.io/discord/1350369915521204276?label=Discord&color=7289DA&logo=discord&logoColor=white" alt="Discord"/>
  </a>
</p>

**ByteCompanionsBuffs** is an addon for **ByteCompanions** that grants configurable buffs to players while a companion is equipped.  
Buffs are applied and removed entirely through events — no polling, no scheduled tasks.

---

## Overview

ByteCompanionsBuffs sits on top of ByteCompanions and reacts to game events in real time.  
When a player equips a companion, their configured buffs become active immediately. When the companion is unequipped, buffs are removed just as fast.

Each companion can have its own independent set of buffs, all configured through a single `config.yml`. The system is designed to be extended — Jobs, Skills, and other plugin integrations can be added as additional buff categories without touching the vanilla layer.

---

<p align="center">
  <a href="https://discord.com/invite/3K9yrZQRmS">
    <img src="https://imgur.com/DvyC4jL.png" width="600" alt="ByteChat preview">
  </a>
  <br/>
  <i>If you need help, join the Discord server.</i>
</p>

---

## Features

- Buffs activate and deactivate instantly based on companion equip/unequip events.
- Per-companion buff configuration — each companion ID maps to its own buff set.
- Vanilla buff support out of the box:
    - Experience (general fallback + mob, block, fishing sources)
    - Mob loot with `ALL / HOSTILE / PASSIVE` filter
    - Block drops and block experience
    - Fishing loot and fishing experience
- Three operation modes per buff: `SUM`, `MULTIPLY`, `ADD_PERCENT`.
- Optional per-buff cooldown (in-memory, cleared on disconnect).
- Per-buff feedback: action bar message with `{value}` and `{operation}` placeholders, and a configurable sound.
- Designed for addon extensibility — Jobs and Skills support can be layered on top.

---

## Requirements

- Java 21+
- PaperMC 1.21+
- [ByteCompanions](https://builtbybit.com/resources/bytecompanions.83544/) (required dependency)

---

## Installation

1. Make sure **ByteCompanions** is installed and running.
2. Download the latest **ByteCompanionsBuffs** release from the [releases page](https://github.com/Bytephoria/companion-buffs/releases).
3. Place the JAR inside your server's `plugins/` folder.
4. Start your Paper server — `config.yml` will be generated automatically.
5. Configure each companion entry in `config.yml` using the companion IDs defined in ByteCompanions.
6. Restart.

---

## Configuration

Buffs are defined per companion ID under the `companions` key:

```yaml
companions:
  my_companion:
    vanilla:
      experience:
        enabled: true
        operation: MULTIPLY   # SUM | MULTIPLY | ADD_PERCENT
        value: 1.5
        cooldown: 0
        feedback:
          action-bar:
            enabled: true
            message: "<yellow>Experience boost: <white>{value} <gray>({operation})"
          sound:
            enabled: true
            key: "minecraft:entity.player.levelup"
```

If a specific experience buff (`experience-mob`, `experience-block`, etc.) is enabled, the general `experience` fallback will not apply for that source to prevent double-boosting.

Cooldowns are stored in memory and cleared on player disconnect — they are not persistent. Recommended for short durations (20–200 ticks). Use `0` to disable.

---

## Contributing

1. Fork the repository.
2. Create a branch: `git checkout -b feature/my-feature` or `git checkout -b fix/my-fix`
3. Commit your changes and open a Pull Request.

Please follow the existing code style:

- Use `this.` for all instance field references.
- Use `final` on parameters, local variables and fields wherever applicable.
- Keep buff categories separated by concern — vanilla, jobs, skills, etc.
- No breaking changes to existing configuration structure without prior discussion.

---

## License

This project is released under the [MIT License](license).  
You are free to use, modify, and distribute it with attribution.
