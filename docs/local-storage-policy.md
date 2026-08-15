# Local Storage Policy

FindYourPet uses Room for demo/cache data so the app can show lost-pet posts, sightings, moderation records, and local notifications when a backend is unavailable or while authenticated data is cached. Chat cache tables are retired by the directed Room 9-to-10 migration.

Sensitive local fields:

- Owner name in pet posts.
- Pet and sighting location names plus latitude/longitude values.
- Photo URLs for pet posts and sightings.
- Reporter names, sighting notes, moderation records, and notification previews.
- Legacy contact grant rows, contact-sharing flags, owner phone, owner email, and owner address values from older builds are retired and removed or ignored during migration.

Current storage guarantees:

- Android backup is disabled in the manifest.
- Backup and data extraction rules exclude app private files, databases, shared preferences, and app-owned external files.
- Cache and no-backup directories remain excluded by Android platform behavior.
- FindYourPet does not implement app-level encryption for Room or files in this stage.
- Local storage no longer keeps an authoritative or displayable contact-sharing cache.
- Historical remote Chat documents are retained by policy, but the active app has no local Chat tables, screens, listeners, or write path.

Future production work must decide whether sensitive local state becomes encrypted cache, account-synced backend data, or short-lived local state after authentication and backend rules are validated.
