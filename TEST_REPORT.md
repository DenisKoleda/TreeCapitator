# TreeCapitator Test Report

Date: 2026-06-19

## Build

Command:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\gradle-local.ps1 clean build
```

Result:

```text
BUILD SUCCESSFUL
```

Artifact:

```text
D:\Documents\Minecraft\TreeCapitator\build\libs\TreeCapitator-0.1.0.jar
SHA256 4FEDF4EAB4CBC431C4F0C28ECF5DF2EF71A73F453F611F2146E0AB2020DD6BD5
```

Standardization pass:

- Runtime mechanics live in `config.yml`.
- Player-facing command/error text lives in `messages.yml`.
- Normal tree chopping is silent by default via `feedback.show-success: false`.
- Refusal warnings can be controlled with `feedback.warn-too-large`, `feedback.warn-tool-would-break`, and `feedback.warn-already-chopping`.
- Command surface follows the local custom plugin pattern: `/treecapitator status`, `/treecapitator reload`, `/treecapitator selftest`, and `/treecapitator help`.

## Local Server Smoke Test

Copied to:

```text
D:\Documents\Minecraft\server-dev\plugins\TreeCapitator-0.1.0.jar
```

Started local Paper `26.2` server-dev. Startup log confirmed:

```text
[TreeCapitator] Loading server plugin TreeCapitator v0.1.0
[TreeCapitator] Enabling TreeCapitator v0.1.0
[TreeCapitator] TreeCapitator enabled.
```

The initialized Bukkit plugin list included:

```text
TreeCapitator (0.1.0)
```

Log check:

```text
latest.log grep ERROR|SEVERE|Exception -> no matches
```

Current local runtime check:

```text
D:\Documents\Minecraft\server-dev\plugins\TreeCapitator-0.1.0.jar
SHA256 4FEDF4EAB4CBC431C4F0C28ECF5DF2EF71A73F453F611F2146E0AB2020DD6BD5
server-dev was stopped after testing; 25566 has no listener
latest.log ERROR|SEVERE|Exception -> no matches
```

Note: local server command-file control did not process console commands in this run, and local RCON is disabled in `server-dev`, so the smoke test verifies build and plugin startup, not in-game chopping behavior. Gameplay should be checked with a real client before or immediately after production rollout.

## Production Rollout

TreeChop was replaced on production on 2026-06-19.

```text
pre-change online check -> 1 player online: Zmeyya
pre-change backup -> /opt/mc-backups/mc-2026-06-19_14-48-30.tar.gz
installed jar -> /srv/minecraft/plugins/TreeCapitator-0.1.0.jar
installed jar SHA256 -> 4fedf4eab4cbc431c4f0c28ecf5df2ef71a73f453f611f2146e0ab2020dd6bd5
old TreeChop jar -> /srv/minecraft/plugin-backups/TreeChop-2.0.1.jar.2026-06-19_14-49-38.bak
TreeChop config directory -> left in /srv/minecraft/plugins/TreeChop
```

Verification:

```text
systemctl is-active minecraft.service -> active
mc "plugins" -> TreeCapitator green; TreeChop absent
mc "version TreeCapitator" -> TreeCapitator version 0.1.0
mc "treecapitator status" -> enabled: true; showSuccess: false
production config -> /srv/minecraft/plugins/TreeCapitator/config.yml has feedback.show-success: false
production messages -> /srv/minecraft/plugins/TreeCapitator/messages.yml exists
latest.log last 300 lines ERROR/SEVERE/Exception -> no matches
post-restart online check -> 1 player online: Zmeyya
```
