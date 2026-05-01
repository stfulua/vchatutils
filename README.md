<div align="center">

<h1>ChatControl</h1>

[![Version](https://img.shields.io/modrinth/v/chatcontrol?label=Version&color=24b47e)](https://modrinth.com/plugin/chatcontrol)
[![Downloads](https://img.shields.io/modrinth/dt/chatcontrol?label=Downloads&color=24b47e)](https://modrinth.com/plugin/chatcontrol)
[![License](https://img.shields.io/badge/License-vProLabs%20General%20License-blue)](https://www.vprolabs.xyz/projects/license/raw)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%2F%20Folia%20%2F%20Spigot-red)](https://papermc.io)

<p>Advanced chat management and control plugin for Paper 1.21.x</p>
<p>Chat toggle, message filtering, slowmode, multi-language support, and LuckPerms integration</p>
<p><em>Formerly known as <strong>vChatUtils</strong></em></p>

</div>

---

### TO-DO (in the future)
- [Folia scheduler support]
- [Spigot compatibility testing]
> These will be made in the future of this plugin, if you wanna support us to add these features faster then join our discord!

---

### Features

- **Chat Toggle**, Enable/disable chat globally with broadcast notifications
- **Message Hiding**, Hide join/leave messages and advancement announcements
- **Chat Filter**, Regex-based word and link filtering with configurable patterns
- **Slowmode**, Cooldown between messages with configurable delay
- **Bypass System**, Permission and LuckPerms group-based bypass for disabled chat
- **Multi-Language**, English and Polish built-in, easy to add more via lang files
- **LuckPerms**, Prefix and suffix formatting with group-level permission checks

---

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/chat clear` | Clear the chat | `chatcontrol.admin` |
| `/chat on` | Enable chat | `chatcontrol.admin` |
| `/chat off` | Disable chat | `chatcontrol.admin` |
| `/chat status` | Show plugin status | `chatcontrol.admin` |
| `/chat reload` | Reload configuration | `chatcontrol.admin` |

*Alias: `/czat` = `/vchat` = `/chat`*

---

### Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chatcontrol.admin` | Access to all commands | op |
| `chatcontrol.bypass` | Write when chat is disabled | op |

---

### Configuration

<details>
<summary><b>View config.yml</b></summary>

```yaml
# ChatControl Configuration
# Use MiniMessage format for prefix: <red>, <bold>, <gradient:gold:yellow>, etc.

prefix: "<dark_gray>[<red>vProLabs<dark_gray>] "
language: "en"

# Commands Aliases
polish-aliases: true
english-aliases: true

# Chat Settings
chat-enabled: true
hide-join-messages: false
hide-leave-messages: false
hide-advancements: false

# Integrations
luckperms-integration: true

# Chat Settings
chat-slowmode: 3

# Chat Filter (regex patterns)
chat-filter:
  - "(?i)fuck"
  - "(?i)shit"
  - "(?i)asshole"
  - "(?i)nigger"
  - "(?i)discord\\.gg"
```

</details>

---

<div align="center">
  <a href="https://www.vprolabs.xyz/foliumhosting">
    <img src="https://cdn.modrinth.com/data/cached_images/4a06749284b8ac33f9754f15990dee97e9d57892.png" alt="FoliumHosting">
  </a>
  <h2>
    <a href="https://www.vprolabs.xyz/foliumhosting">Check out FoliumHosting!</a>
  </h2>
</div>

---

### Links

- 🌐 **Website:** https://vprolabs.xyz
- 💬 **Discord:** https://discord.gg/SNzUYWbc5Q
- 📦 **Modrinth:** https://modrinth.com/plugin/chatcontrol
- ☕ **Support:** https://ko-fi.com/v4bi

---

### License

This project is licensed under the **vProLabs General License**.

- Non-Commercial Use Only
- Attribution Required
- Share Alike
- [View Full License](https://www.vprolabs.xyz/projects/license/raw)

---

<div align="center">

<sub>Made with 🔥 by <strong>vProLabs</strong></sub>

</div>
