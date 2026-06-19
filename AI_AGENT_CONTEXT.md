# TreeCapitator AI Agent Context

Use this file as portable context for AI agents working on this project.

## Project

- Project path: `D:\Documents\Minecraft\TreeCapitator`
- Plugin name: `TreeCapitator`
- Target: Paper/Purpur Minecraft `26.2`
- Java: `25`, not Java 8
- Local dev server: `D:\Documents\Minecraft\server-dev`
- Local dev port: `25566`
- Production `/srv/minecraft` is out of scope unless explicitly requested.

## Build And Install

Build from the project root:

```powershell
cd D:\Documents\Minecraft\TreeCapitator
.\scripts\gradle-local.ps1 build
```

Built jar:

```text
D:\Documents\Minecraft\TreeCapitator\build\libs\TreeCapitator-0.1.0.jar
```

Install only to local dev:

```powershell
.\scripts\install-server-dev.ps1
```

Installed jar:

```text
D:\Documents\Minecraft\server-dev\plugins\TreeCapitator-0.1.0.jar
```

## Architecture

Preserve this structure:

- `TreeCapitatorPlugin` - plugin entrypoint
- `TreeSettings` - config parsing and validated defaults
- `block/*` - material and block identity helpers
- `detect/*` - tree/log/leaf detection
- `breaking/*` - scheduled block breaking and durability handling
- `listener/*` - Bukkit event listeners
- `command/*` - `/treecapitator` command tree
- `message/*` - `messages.yml` MiniMessage text

Player-facing text belongs in `messages.yml`. Mechanics and tuning belong in `config.yml`.

Normal tree chopping should stay silent by default. Only command responses and refusal/error cases should send chat messages.

## Commands

```text
/treecapitator status
/treecapitator reload
/treecapitator selftest
/treecapitator help
```

Aliases:

```text
/tcap
/tcapi
```

## Permissions

```text
treecapitator.use
treecapitator.reload
treecapitator.selftest
treecapitator.admin
```

## Test Expectations

Run:

```powershell
.\scripts\gradle-local.ps1 clean build
```

Then install to `server-dev`, restart the local server, and check `logs/latest.log` for `ERROR`, `SEVERE`, or `Exception`.

Manual gameplay checks:

- axe breaks whole tree;
- leaves break after logs and drop vanilla loot;
- sneaking breaks only one block;
- player-placed leaves are not removed;
- large/wooden-build-like structures are not mass-chopped.
