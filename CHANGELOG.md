# Changelog

## 1.1.0 - Rebrand to ChatControl

### Major Changes
- **Rebrand**: Project renamed from `vChatUtils` to **ChatControl**
- **Package Rename**: `pl.vprolabs.vchatutils` → `xyz.vprolabs.chatcontrol`
- **Permission Rename**: `vchatutils.*` → `chatcontrol.*`
- **Main Class**: `vChatUtils` → `ChatControl`

### Removals
- Removed vManager integration completely
- Removed stale `pom.xml` (legacy Maven build file)

### Improvements
- Updated README with new template and improved documentation
- Cleaned up hardcoded log prefixes, config headers, and lang file headers
- Increased default slowmode from 0 to 3 seconds
- General code cleanup and consistency improvements

### Changes
- **Command**: Main command changed from `/chat` to `/chatcontrol`
- **Alias**: Added `/cc` alias (toggleable via `short-alias` in config)
- **Removed**: Old `/czat` and `/vchat` aliases
- **Platform**: Added guaranteed Spigot, Paper, and Purpur support (switched to Spigot API)

### New Features
- **Chat Formatting**: Configurable chat format with `{prefix}`, `{suffix}`, `{username}`, `{message}` placeholders
- **PlaceholderAPI**: Full PlaceholderAPI integration for chat format placeholders
- **Velocity/Bungeecord**: Listed as future support targets
- **Version Support**: Confirmed compatibility with Paper 1.21.x through 26.1.2
