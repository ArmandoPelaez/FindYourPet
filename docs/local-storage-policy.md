# Local Storage Policy

FindYourPet is still a local Android demo. Room stores demo/cache data so the prototype can show lost-pet posts, sightings, local chat messages, and local notifications without a backend.

Sensitive local fields:

- Owner name, phone, email, and address in pet posts.
- Pet and sighting location names plus latitude/longitude values.
- Photo URLs for pet posts, sightings, and chat attachments.
- Reporter names, chat messages, sighting notes, and notification previews.

Current storage guarantees:

- Android backup is disabled in the manifest.
- Backup and data extraction rules exclude app private files, databases, shared preferences, and app-owned external files.
- Cache and no-backup directories remain excluded by Android platform behavior.
- FindYourPet does not implement app-level encryption for Room or files in this stage.
- Contact values must stay hidden in public UI unless a local contact-reveal flow explicitly shows them.

Future production work must decide whether sensitive local state becomes encrypted cache, account-synced backend data, or short-lived local state after authentication and backend rules are defined.
