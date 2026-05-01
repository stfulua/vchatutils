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
