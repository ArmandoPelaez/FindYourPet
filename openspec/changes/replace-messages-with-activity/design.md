## Context

The authenticated shell currently exposes `Mensajes` through `BottomPrimaryActionBanner`, routes it to `ChatListScreen`, and renders conversation/session data. SCRUM-23 requires the fourth primary destination to become `Actividad`, an informational inbox of sightings received for the signed-in user's own posts. `SightingAlertEntity` already contains the sighting identity, owner, reporter, location, optional photo, notes, and timestamp, but `PetViewModel` and `PetRepository` currently expose sightings only by selected post or id.

The change is visual and cross-layer: Compose navigation/components, a new activity screen, an owner-scoped repository/DAO query, ViewModel state, and tests. It must preserve the existing Chat code and routes for compatibility while keeping Activity independent of Chat.

## Goals / Non-Goals

**Goals:**

- Replace the primary `Mensajes` destination with `Actividad` while preserving the other four destinations and their order.
- Load only sightings whose `ownerId` matches the authenticated user.
- Render a newest-first informational list with available pet, sighting, location, timestamp, photo, and `sightingId` data.
- Provide loading, success, empty, and error states using existing Compose components and Design System tokens.
- Support Light Theme, Dark Theme, accessible touch targets, and responsive bottom-navigation spacing without new hardcoded visual values.
- Keep the existing Chat entities, repository flows, routes, and detail screen available outside the primary Activity destination.

**Non-Goals:**

- Do not delete Chat entities, DAOs, repositories, routes, or historical data.
- Do not implement Activity-item to sighting-detail navigation in this change.
- Do not modify sighting creation, alert generation, notification routing, or `SightingDetailScreen`.
- Do not add reply, send, online, typing, conversation, or contact-sharing actions.
- Do not add permissions, dependencies, or backend schema changes.

## Decisions

1. **Add a dedicated owner-scoped sightings flow.**

   Add `getSightingsForOwnerState(ownerId)` through the repository and DAO, backed by a Firestore query on `ownerId` and a Room owner filter for local/cache mode. The ViewModel derives the owner id from the authenticated `currentUser`. This avoids loading Chat sessions or inferring ownership from local conversation data.

   Alternative: load all sightings and filter in Compose. Rejected because it would expose unrelated sensitive sighting data to the client layer and weaken the owner-scoped access contract.

2. **Create `ActivityScreen` as a read-only list.**

   The screen observes the owner-scoped state and existing post data to enrich each item with a pet name/photo when available. It renders a reusable activity card with the existing `AppSpacing`, `AppShapes`, `MaterialTheme` colors/typography, `EmptyState`, `SyncStatusBanner`, and Material icons. An item retains `sightingId` as stable identity but has no navigation action in this change.

   Alternative: adapt `ChatListScreen` and `ChatSessionCard`. Rejected because that would preserve conversation concepts and make Activity depend on `ChatSessionEntity`/`lastMessage`.

3. **Replace only the primary navigation destination.**

   Rename the bottom-navigation callback and visible label to `Actividad`, use a semantic activity/sighting icon available in the existing Material icon set, and route it to `ROUTE_ACTIVITY`. Keep the legacy chat route and `ChatListScreen` code for compatibility, but do not expose Chat as the fourth primary item.

4. **Preserve authorized data handling.**

   The repository query remains constrained by `ownerId`; existing Firestore rules remain the authorization boundary. Activity must not display phone, email, address, exact contact data, chat previews, message bodies, `lastMessage`, or `chatId`.

5. **Use existing state patterns.**

   Loading and synchronization feedback reuse `BackendSyncState` and `SyncStatusBanner`. Empty and error content reuse `EmptyState` and existing theme tokens. No experimental Compose API or new visual dependency is introduced.

## Risks / Trade-offs

- [Owner has many sightings] → Use the existing backend ordering query and a lazy list; avoid loading Chat sessions or unrelated sightings.
- [A sighting references a post unavailable to the current cache] → Render the sighting's available fields and use a neutral existing fallback for missing pet metadata; do not invent a new data source.
- [Legacy Chat routes still exist] → Keep them intact but ensure the primary navigation and Activity screen have no Chat dependency; cover with static and state tests.
- [Remote query fails] → Surface the existing synchronization/error state without crashing and keep the cached data behavior consistent with current repository patterns.
- [Visual drift across themes or device sizes] → Reuse `docs/design-system.md` tokens and validate both themes and the responsive navigation shell.

## Migration Plan

1. Add owner-scoped sighting access and ViewModel state without changing the sighting write path.
2. Add the Activity screen and route.
3. Replace the fourth bottom-navigation item and update its selection state/tests.
4. Validate privacy boundaries, ordering, states, themes, tests, and debug build.

Rollback removes the Activity route/screen and restores the prior primary Chat destination; sighting records, Chat data, and backend rules remain unchanged.

## Open Questions

None. SCRUM-23 explicitly defers Activity-item to sighting-detail navigation and Chat cleanup to later tasks.
