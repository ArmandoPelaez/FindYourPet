## Context

The home screen currently renders each reported-pet card with the pet name, a purple breed chip, and attribute blocks for color, species, breed, and reported features. This creates two presentation issues: species is still visible on the main feed, and breed is duplicated as both the purple identity chip and a separate `Raza` attribute block.

The request is presentation-focused. The existing post model, local database, backend mapping, create-post flow, filters, and demo seed data may still carry `species` as a data field until a dedicated data-model migration is planned.

## Goals / Non-Goals

**Goals:**

- Remove the visible `Especie` attribute from home feed reported-pet cards.
- Remove the visible duplicate `Raza` attribute block from home feed reported-pet cards.
- Preserve the purple breed chip when `post.breed` is present.
- Remove or update affected user-facing and project-text references to `Especie` that describe, test, or share home feed presentation.
- Keep the current card hierarchy, image loading, status pill, reported information, location, and bottom spacing behavior.

**Non-Goals:**

- Do not remove the `species` field from Room, Firestore documents, repository models, remote mappers, or post creation.
- Do not introduce a database migration or backend data migration.
- Do not remove the create-post breed input, saved breed value, or purple breed chip.
- Do not change auth, contact privacy, permissions, location capture, notifications, or chat behavior.

## Decisions

1. Remove species and duplicate breed at the home card presentation layer.
   - Rationale: the requested visual cleanup is localized to `HomeScreen.kt`; removing data fields would expand the change into persistence and backend compatibility.
   - Alternative considered: deleting `species` from the full data model. This was rejected for this change because it would require Room schema changes, remote document changes, seed data updates, form changes, filters, and migration validation.

2. Treat breed as identity, not as a repeated attribute.
   - Rationale: the purple chip already communicates breed clearly beside the pet name and matches the requested retained element.
   - Alternative considered: remove all breed presentation from the main feed. This was rejected because the request explicitly says the purple breed label must stay visible.

3. Audit `Especie` as user-facing/project text tied to the main feed.
   - Rationale: visible strings, share summaries, tests, specs, and docs can accidentally reintroduce species presentation even if the card layout removes the attribute block.
   - Alternative considered: remove every internal `species` identifier. This was rejected as a separate data-model change with migration risk.

4. Keep privacy behavior unchanged.
   - Rationale: this change removes public-facing information rather than exposing new information; contact, coordinates, and private messages remain excluded from share content.
   - Alternative considered: changing the shared post summary format more broadly. This was rejected to keep the change narrowly scoped.

## Risks / Trade-offs

- [Risk] Tests may currently assert that `Especie` is displayed. -> Mitigation: update home feed tests to assert absence of `Especie` and duplicate `Raza`, plus presence of the breed chip.
- [Risk] Removing the `Raza` attribute block could be mistaken for removing breed entirely. -> Mitigation: add an explicit test and spec scenario for the retained purple breed chip.
- [Risk] Share text or docs may keep the `Especie` label after UI cleanup. -> Mitigation: include a repository audit step using text search for `Especie`/`especie` and update affected references in the change.
- [Risk] Internal `species` fields remain in code and could be confused with incomplete cleanup. -> Mitigation: document that internal data-model removal is out of scope and requires a separate migration-focused change.

## Migration Plan

No data migration is required. Implement as a UI/test/docs/spec update, then validate with Compose tests, debug build, and OpenSpec strict validation.

Rollback is limited to restoring the previous home feed attribute blocks and prior home-feed-presentation requirements/tests if species and duplicate breed need to return.

## Open Questions

None.
