# TreeCapitator

Server-side Paper/Purpur 26.2 plugin that cuts a whole natural tree when a player breaks a log with an axe.

## Behavior

- Requires an axe by default.
- Sneaking disables mass chopping for the current block break.
- Detects connected logs/wood from the same wood family.
- Requires nearby natural leaves before treating overworld logs as a tree.
- Breaks natural non-persistent leaves near the detected tree so sticks, saplings, and apples use vanilla drops.
- Processes blocks over multiple ticks to avoid a single large tick spike.
- Consumes axe durability for logs by default.
- Normal chopping is silent in chat by default.

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
treecapitator.use    default: true
treecapitator.reload default: op
treecapitator.selftest default: op
treecapitator.admin  default: op
```

## Build

```powershell
.\scripts\gradle-local.ps1 clean build
```

The jar is written to:

```text
build\libs\TreeCapitator-0.1.0.jar
```
