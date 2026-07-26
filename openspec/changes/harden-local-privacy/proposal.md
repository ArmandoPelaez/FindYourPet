## Why

FindYourPet currently behaves like a local demo while handling data that would be sensitive in production, including phone, email, address, location, photos, and private messages. Before adding backend, real auth, camera, GPS, or release workflows, the app needs a verifiable local privacy baseline so it does not expose personal data through cleartext storage, Android backups, unused permissions, or inaccurate privacy copy.

## What Changes

- Prevent Android backup from copying local databases and sensitive app files, either by disabling backup or by using explicit backup/data extraction exclusions.
- Define which personal data may be stored locally, whether it is temporary cache or durable storage, and when it must be omitted, redacted, or cleared.
- Decide and document whether local data encryption is implemented in this stage; remove user-facing claims of encryption until the code actually enforces it.
- Remove Android permissions that do not have a working, user-visible flow in the current app, such as camera or precise location if those flows remain simulated.
- Align privacy UI text, settings text, and app copy with the actual guarantees provided by the code.
- Add validation through static tests or manual checks for manifest permissions, backup rules, and privacy-sensitive copy.

## Capabilities

### New Capabilities
- `local-storage`: Covers local persistence of sensitive data, backup/data extraction behavior, encryption claims, and the local data retention policy.
- `contact-privacy`: Covers how phone, email, address, owner details, and location are exposed or withheld in app UI and local state.
- `device-permissions`: Covers Android permission declarations and the rule that sensitive permissions must only be requested when a real feature flow uses them.

### Modified Capabilities
- `release-readiness`: Adds a release gate requiring privacy, backup, and permission checks before production or store-facing builds.

## Impact

- Affected Android configuration: `app/src/main/AndroidManifest.xml`, `app/src/main/res/xml/backup_rules.xml`, and `app/src/main/res/xml/data_extraction_rules.xml`.
- Affected local data layer: Room entities, DAO/repository usage, and any seeded/demo data containing phone, email, address, coordinates, photos, or private messages.
- Affected UI/copy: privacy messages, contact detail cards, profile/detail screens, and any text that currently implies encryption or stronger privacy guarantees than implemented.
- Affected validation: static guardrail tests, Android debug build, and manual review of backup rules, manifest permissions, and privacy-sensitive screens.
- User impact: users should see fewer unnecessary permission prompts and more accurate privacy messaging; no intentional product functionality should be removed except unsupported permission-driven flows or misleading claims.
- Rollback strategy: restore the previous manifest, backup rules, and privacy copy if the change blocks critical demo usage, while keeping any tests that detect unsafe backup or misleading encryption claims disabled only with an explicit follow-up issue.
- Applies guardrails: do not expose phone, email, address, or coordinates without explicit consent; do not promise encryption if not implemented; do not request Android permissions without a real flow; do not allow Android backup to copy sensitive local data.
