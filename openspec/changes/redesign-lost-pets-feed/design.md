## Context

The home feed is implemented in `HomeScreen.kt` with a horizontally paged `PetPostCard`. The current card renders photo, status, pet identity, a breed chip, `Color` and `Señas` attribute pills, a separate `Información reportada` section, a titled location section, date, and the existing report/share actions.

The create-post form has already been simplified and can save backend-compatible defaults for fields that are no longer collected as dedicated visible inputs. Because the feed is the public trust surface, it should not promote default/internal classification values as verified user-reported facts. The requested red-marked removals apply to home-feed presentation and its associated helper/test/spec layers, while preserving the app's navigation and action behavior.

## Goals / Non-Goals

**Goals:**

- Make the home card easier to scan by emphasizing photo, status, pet name, last-seen location, and actions.
- Move `post.lastSeenLocation` directly below `post.petName` as a compact row with only the location icon and the location text.
- Remove the breed chip, `Color` pill, and `Señas` pill from the home card.
- Remove the titled location section from the lower card content.
- Ensure home-feed share text, tests, and specs do not reintroduce deleted breed/color/signs presentation or default values.
- Preserve existing post loading, paging, image loading, status chip, report-sighting action, share action, ownership policy, and bottom navigation behavior.

**Non-Goals:**

- Do not remove `species`, `breed`, `color`, or `features` from Room entities, remote documents, repository mappings, or backend documents.
- Do not change create-post form validation, backend-compatible field mapping, or data migration strategy.
- Do not change auth, ownership, chat, notifications, privacy rules, permissions, or location capture.
- Do not redesign the full app shell, top app bar, bottom action banner, empty state, or detail/sighting screens.

## Decisions

1. Keep the change in the home-feed presentation boundary.
   - Rationale: the request is to redesign the main screen without changing the base behavior of the app. Removing database or backend fields would require migration, mapper updates, rules review, and broader compatibility work.
   - Alternative considered: delete marked fields from all persistence layers. This was rejected for this change because those fields may still be needed by existing documents, detail flows, backend compatibility, or future product decisions.

2. Treat last-seen location as secondary identity context.
   - Rationale: placing the titleless location row directly below the pet name makes the most actionable context visible immediately and avoids the heavy lower section currently shown in the card.
   - Alternative considered: leave the titled location section at the bottom. This was rejected because the request explicitly moves the location under the pet name and removes the title.

3. Remove breed, color, and signs from home-feed display and home-feed sharing.
   - Rationale: these are the elements marked for deletion, and share text is another presentation surface for the same card. Keeping them in the share helper would undermine the cleanup.
   - Alternative considered: hide only the visual components while keeping share text unchanged. This was rejected because the user asked to remove references across application layers tied to this screen.

4. Preserve existing actions and privacy posture.
   - Rationale: the report-sighting and share controls are established behavior and are not part of the requested deletion. Share output should remain privacy-safe and exclude owner contact, exact coordinates, hidden contact data, and private messages.
   - Alternative considered: redesign card actions together with the visual cleanup. This was rejected to keep scope controlled and avoid behavior regressions.

## Risks / Trade-offs

- [Risk] Existing tests still assert the old chip, attribute pills, or titled location section. -> Mitigation: update home-feed Compose/static tests to assert their absence and assert the new titleless location placement.
- [Risk] Removing `Señas` from the main card may reduce quick recognition detail in the feed. -> Mitigation: keep photo, pet name, location, status, and report/share actions prominent; defer richer details to a dedicated detail surface if product wants it later.
- [Risk] Internal fields remain in models and can be mistaken for incomplete deletion. -> Mitigation: document that model-level removal is out of scope and that this change removes home-feed presentation references, not storage contracts.
- [Risk] Share text may still include removed values. -> Mitigation: update `buildPetPostShareText` and tests so shared summaries use only retained home-feed facts.

## Migration Plan

No data migration is required. Implement as a Compose UI, helper-text, test, and spec update.

Rollback is limited to restoring the previous breed chip, attribute grid, titled location section, share-text lines, and tests if the product decides to show those details again.

## Open Questions

None.
