## vChatUtils v1.0.2

### New Features
- LuckPerms Integration (Optional)
  - Full support for LuckPerms prefixes and suffixes in chat
  - Group-based chat bypass permissions - Give entire groups the ability to chat when chat is disabled
  - Use /lp group &lt;group&gt; permission set vchatutils.bypass true to allow specific ranks to talk during chat lockdown
  - Automatic detection and hooking on server startup

- Command Alias Toggles
  - New config options: polish-aliases and english-aliases
  - Enable/disable Polish commands (/chat wyczysc, /chat wlacz, etc.) independently
  - Enable/disable English commands (/chat clear, /chat on, etc.) independently
  - Mix and match however you want

- Leave Message Control
  - Added hide-leave-messages option (separate from join messages)
  - Now you can control join and leave messages independently

### Changes
- Default values changed:
  - hide-join-messages: true -&gt; false
  - hide-leave-messages: false (new)
  - hide-advancements: true -&gt; false
  - Chat remains enabled by default (chat-enabled: true)

- Enhanced Status Command
  - Now shows LuckPerms integration status
  - Shows which groups have vchatutils.bypass permission (admin only)

### Technical
- Added softdepend: [LuckPerms] in plugin.yml
- Improved permission checking with group inheritance support
- Better error handling for LuckPerms hook failures
