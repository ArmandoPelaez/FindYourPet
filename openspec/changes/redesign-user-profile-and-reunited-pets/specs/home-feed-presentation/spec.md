## ADDED Requirements

### Requirement: Reunited Posts Are Excluded From Public Presentation
The home feed and its public search results SHALL exclude posts with status `REUNIDO` for users who are not the owner. The exclusion SHALL apply to remote results and local/cache results before cards or public actions are rendered.

#### Scenario: Reunited post is absent from the feed
- **GIVEN** a signed-in user is not the owner of a post with status `REUNIDO`
- **WHEN** the home feed loads or refreshes
- **THEN** no card for that post is rendered

#### Scenario: Reunited post is absent from search
- **GIVEN** a signed-in user searches for a post whose status is `REUNIDO` and whose owner is another user
- **WHEN** the search filters are applied
- **THEN** the post is not returned as a search result

#### Scenario: Public actions are not exposed for reunited posts
- **GIVEN** a post with status `REUNIDO` is present in a stale source or cache
- **WHEN** the public presentation list is built
- **THEN** the post is removed before rendering and no sighting/share action is exposed for it
