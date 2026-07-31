## 1. Screen Scope And Layout Foundation

- [x] 1.1 Identify the pet detail/sighting-detail composable that owns the media header, badges, info cards, and primary actions.
- [x] 1.2 Add a width-aware layout decision for compact below 600dp, expanded at 600dp and above, and centered single-column fallback for constrained wide viewports.
- [x] 1.3 Keep top navigation and bottom actions outside the main scroll body, applying scaffold/system inset padding so fixed surfaces do not overlap unsafe areas.

## 2. Responsive UI Implementation

- [x] 2.1 Implement compact single-column content with vertical scrolling, 16dp side margins, and bottom padding that keeps the last controls visible above fixed actions.
- [x] 2.2 Implement medium/expanded two-column content with the media header on the left and scrollable cards/actions on the right with 24dp gutters.
- [x] 2.3 Implement the centered single-column tablet fallback with a 640dp maximum content width when the two-column layout is not appropriate.
- [x] 2.4 Update the media header to use a 4:3 portrait ratio, center-crop scaling, and a 45vh landscape/compact-horizontal height cap.
- [x] 2.5 Position status/metadata badges as bottom-right overlays with independent dp margins and no overlap with fixed screen chrome.
- [x] 2.6 Update primary action controls to fill parent width up to 400dp per button and use a 56dp touch height.
- [x] 2.7 Ensure information cards use flexible height and wrap essential pet details instead of truncating long text.
- [x] 2.8 Confirm responsive changes do not reveal phone, email, exact address, exact coordinates, or private messages beyond existing privacy rules.

## 3. Tests

- [x] 3.1 Add or update Compose UI tests for compact single-column layout selection and visible/tappable primary actions.
- [x] 3.2 Add or update Compose UI tests for expanded layout selection, verifying media column and scrollable detail column are both present.
- [x] 3.3 Add or update tests or static assertions for protected contact data remaining masked when layout changes.

## 4. Validation

- [x] 4.1 Run `.\gradlew.bat testDebugUnitTest`.
- [x] 4.2 Run `.\gradlew.bat assembleDebug`.
- [x] 4.3 Manually validate phone portrait below 600dp: scrolling, fixed top/bottom surfaces, 56dp actions, and no content obstruction.
- [x] 4.4 Manually validate phone landscape: media height capped near 45vh and detail content remains readable.
- [x] 4.5 Manually validate tablet/expanded width: two-column layout or centered 640dp fallback appears as specified.
- [x] 4.6 Manually validate increased system font size: info cards grow, text wraps, and controls do not overlap.
- [x] 4.7 Manually validate privacy-sensitive UI: protected contact/location/message data remains hidden or masked in every responsive layout.
