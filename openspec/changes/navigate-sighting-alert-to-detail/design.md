## Context

`NotificationsScreen` currently marks the selected notification as read and passes `notif.chatId ?: notif.targetId` to its callback. `SignedInPetAppNavigation` then interprets that value as a chat identifier and always navigates to `ChatDetailScreen`. The notification model and backend fan-out already carry `sightingId`, and `SightingDetailScreen` already accepts a `sightingId` route argument.

The change is limited to notification selection and routing. Sighting creation, backend fan-out, notification persistence, chat functionality, and the existing sighting detail screen remain outside the implementation scope.

## Goals / Non-Goals

**Goals:**

- Route sighting notifications directly to `sighting/{sightingId}`.
- Prefer the explicit `sightingId` field for identifying sighting notifications.
- Reject blank or missing sighting identifiers before navigation without crashing.
- Preserve notification read-state updates and existing routes for non-sighting notifications.
- Keep legacy notifications controlled without reconstructing a chat destination for new sighting alerts.

**Non-Goals:**

- Do not change `PetRepository.reportSighting` or notification creation.
- Do not remove chat sessions, chat routes, chat entities, or legacy chat data.
- Do not change `SightingDetailScreen` content or backend authorization.
- Do not change Bottom Navigation, the sighting form, or notification persistence schema.

## Decisions

1. **Resolve routing from notification type and `sightingId`.**

   The notification click boundary will receive the notification identity needed for routing, and the sighting path will be selected only when the notification represents a sighting and has a non-blank `sightingId`. This is preferred over `chatId ?: targetId` because `targetId` is polymorphic and legacy chat values are not valid sighting destinations.

2. **Keep the read update before the route decision.**

   The existing click behavior continues to call `markNotificationAsRead`. A malformed notification is still acknowledged according to current behavior, then handled without navigation. This avoids unread items becoming permanently stuck while preventing an invalid route.

3. **Use the existing sighting route.**

   Navigation will call the existing `sighting/{sightingId}` route and let `SightingDetailScreen` load the authorized record. No duplicate detail screen or alternate data lookup is introduced.

4. **Preserve non-sighting routing.**

   Chat notifications continue to use their chat identifier and existing chat route. Other notification types retain their current destination contract unless they are invalid sighting notifications, which are handled safely and do not fall through to Chat.

5. **Handle invalid sighting alerts at the UI boundary.**

   Blank or missing `sightingId` values are logged with the notification id/type and ignored for navigation. The implementation will use the project's existing error/status presentation pattern if a visible state is required; it will not add hardcoded visual tokens or a new backend fallback.

## Risks / Trade-offs

- [Legacy alert lacks `sightingId`] → Affected item is marked read and no navigation occurs; diagnostics identify the notification without attempting Chat fallback.
- [A non-sighting notification has an unexpected payload] → Existing non-sighting routing remains unchanged and is covered by regression tests.
- [Routing contract changes across composables] → Keep the route construction in `MainActivity` and add static/unit coverage for both sighting and chat notification paths.
- [Detail load fails after valid navigation] → Reuse `SightingDetailScreen`'s existing loading/error handling and backend authorization.

## Migration Plan

1. Update the notification click callback contract and handler to preserve the full notification identity or an equivalent typed route decision.
2. Route valid sighting notifications to the existing sighting detail destination.
3. Keep chat notification routing and read-state behavior unchanged.
4. Add routing and invalid-payload tests, then run OpenSpec validation, unit tests, and debug assembly.

Rollback restores the previous callback/route behavior without changing persisted notification records or sighting data.

## Open Questions

None. SCRUM-22 defines the sighting destination and explicitly prohibits Chat as the destination for new sighting alerts.
