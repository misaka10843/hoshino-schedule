# Settings Migration QA Notes

This document covers regression testing for the Settings UI migration to Voyager screens with search functionality.

## Architecture Changes

- **Old**: Monolithic `SettingsScreen.kt` (~1,500 lines) with manual page navigation
- **New**: Modular Voyager screens with:
  - Master-detail layout (two-pane on tablets, single-pane on phones)
  - Search functionality with keyword indexing
  - Shared context via CompositionLocal for state/callbacks
  - Individual screen files per category

## Test Coverage

### 1. Appearance Settings

**Navigation:**
- ✅ Click "Appearance" from main settings list
- ✅ Search "background", "color", "image", "theme" - all navigate to Appearance

**Functionality:**
- ✅ Select color from palette
- ✅ Click "Pick image" - image picker launches
- ✅ Select image - displays preview
- ✅ Click "Use color" after selecting image - reverts to color mode
- ✅ Background changes persist to schedule screen

### 2. Basic Settings

**Navigation:**
- ✅ Click "Basic settings" from main list
- ✅ Search "term", "weeks", "weekend", "timetable" - navigates to Basic

**Functionality:**
- ✅ Click timetable name - dialog opens
- ✅ Enter/save timetable name - updates and persists
- ✅ Click term start date - date picker opens
- ✅ Select date - updates display
- ✅ Click "Clear start date" - clears the date
- ✅ Click total weeks - number dialog opens
- ✅ Enter valid weeks (1-40) - "Save changes" button enables
- ✅ Enter invalid weeks (>40, 0, negative) - error shown, button disabled
- ✅ Click "Save changes" - updates persist
- ✅ Toggle "Show non-current week courses" - affects schedule display
- ✅ Toggle "Show Saturday" / "Show Sunday" - columns appear/disappear
- ✅ Toggle "Hide empty weekend" - hides weekend when no classes scheduled

### 3. Display Fields

**Navigation:**
- ✅ Click "Course card fields" from main list
- ✅ Search "teacher", "location", "notes", "display", "fields" - navigates to Display Fields

**Functionality:**
- ✅ Check/uncheck Teacher - field shows/hides on course cards
- ✅ Check/uncheck Location - field shows/hides on course cards
- ✅ Check/uncheck Notes - field shows/hides on course cards
- ✅ Attempt to uncheck all fields - last field (Name) remains checked
- ✅ Changes persist across app restarts

### 4. Notifications (Reminders)

**Navigation:**
- ✅ Click "Reminders" from main list
- ✅ Search "notification", "reminder", "alert", "lead" - navigates to Notifications

**Functionality:**
- ✅ Drag slider for reminder lead time (0-180 minutes)
- ✅ Value updates live
- ✅ Changes persist and affect actual reminders

### 5. Do Not Disturb

**Navigation:**
- ✅ Click "Do Not Disturb" from main list
- ✅ Search "dnd", "silence", "mute", "do not disturb" - navigates to DoNotDisturb

**Functionality:**
- ✅ Toggle DND enabled/disabled
- ✅ When enabled, sliders appear:
  - ✅ "Enable before class" (0-60 minutes)
  - ✅ "Disable after class" (0-60 minutes)
  - ✅ "Keep DND if break shorter than" (0-180 minutes)
- ✅ Click "Grant access" - system settings open
- ✅ All slider changes persist
- ✅ DND automation works (test with scheduled class)

### 6. Course Times (Periods)

**Navigation:**
- ✅ Click "Course times" from main list
- ✅ Search "period", "schedule", "time" - navigates to Course Times

**Functionality:**
- ✅ Click "Add period" - new period appears
- ✅ Click start/end time fields - time picker opens
- ✅ Select times - fields update
- ✅ Enter label text - updates live
- ✅ Click "Apply" - period saves
- ✅ Click "Remove" - period deletes, sequences renumber
- ✅ Period changes reflect in schedule grid
- ✅ Validation: start time must be < end time (Apply button disabled otherwise)

### 7. Data Management

**Navigation:**
- ✅ Click "Data" from main list
- ✅ Search "backup", "restore", "import", "export", "json" - navigates to Data

**Functionality:**
- ✅ Click "Export" - file picker opens
- ✅ Select location - export begins, progress shown
- ✅ Export success - snackbar appears
- ✅ Export failure - error snackbar appears
- ✅ Click "Import" - file picker opens
- ✅ Select JSON file - import begins, progress shown
- ✅ Import success - data updates, snackbar appears
- ✅ Import failure - error snackbar appears
- ✅ Buttons disabled during transfer
- ✅ Progress indicator visible during operations

### 8. Developer Tools

**Navigation:**
- ✅ Click "Developer tools" from main list (below settings categories)
- ✅ Search "developer", "debug", "test" - navigates to Developer

**Functionality:**
- ✅ Toggle "Enable developer mode" - testing section appears/disappears
- ✅ If notifications disabled, warning shown with "Open notification settings" button
- ✅ Adjust "Test notification delay" slider (0-60 seconds)
- ✅ Click "Send test notification" - notification appears after delay, snackbar confirms
- ✅ Toggle "Automatically disable DND after tests"
- ✅ Adjust "Test DND duration" slider (1-120 minutes)
- ✅ Adjust "Gap between test classes" slider (0-180 minutes)
- ✅ Adjust "Skip break threshold" slider (0-240 minutes)
- ✅ Click "Toggle DND for test duration" - DND activates, snackbar confirms
- ✅ Click "Toggle DND across two classes" - DND activates for two test periods, snackbar confirms
- ✅ All test actions show snackbars
- ✅ All settings persist

### 9. About

**Navigation:**
- ✅ Click "About" from main list (below settings categories)
- ✅ Search "about", "version", "license" - navigates to About

**Functionality:**
- ✅ Version number displays correctly
- ✅ Author name displays
- ✅ Click "Open project repository" - browser opens to GitHub
- ✅ Click "View license" - browser opens to license page

### 10. Search Functionality

**General Search Tests:**
- ✅ Type in search box - results filter live
- ✅ Search "teacher" - Display Fields appears in results
- ✅ Search "backup" - Data Management appears in results
- ✅ Search "dnd" - Do Not Disturb appears in results
- ✅ Search "test" - Developer appears in results
- ✅ Search "xyz123" - "No settings matched" message shown
- ✅ Click search result - navigates to corresponding screen
- ✅ Clear search (X button) - returns to full list
- ✅ Search is case-insensitive
- ✅ Partial matches work ("notif" finds "Notifications")

**Tablet Two-Pane Search:**
- ✅ Search bar appears in left pane
- ✅ Search results appear in left pane
- ✅ Clicking result updates right pane without collapsing layout
- ✅ Clearing search returns to destination list in left pane
- ✅ Selected destination remains highlighted in left pane

### 11. Navigation & Layout

**Phone (Single-Pane):**
- ✅ Back button in top bar navigates to main settings list
- ✅ Back button from main list exits settings entirely
- ✅ Each detail screen has its own top bar with title
- ✅ Snackbars appear at bottom
- ✅ Search bar spans full width
- ✅ Content scrolls properly

**Tablet (Two-Pane, screen width ≥ 900dp):**
- ✅ Left pane (360dp): main list + search
- ✅ Right pane: detail content
- ✅ No top bar in detail pane (embedded mode)
- ✅ Clicking destination in left pane updates right pane
- ✅ Selected destination highlights in left pane
- ✅ Detail content scrolls independently
- ✅ Default selection: Basic settings

### 12. Callbacks & State

**All callbacks work correctly:**
- ✅ Image picker callback (appearance)
- ✅ Date picker callback (basic settings)
- ✅ Export launcher callback (data)
- ✅ Import launcher callback (data)
- ✅ Permission requests (DND access, notification settings)
- ✅ External links (about)
- ✅ ViewModel methods (all settings changes)
- ✅ Snackbar displays (test notifications, export/import results)

**State management:**
- ✅ Settings UI state from ViewModel flows properly
- ✅ Transfer state (import/export progress) updates correctly
- ✅ Notifications enabled state checked on entry
- ✅ All settings persist across app restarts
- ✅ Navigation state preserved on configuration change (rotation)

### 13. Edge Cases

- ✅ Empty periods list - "Add period" button still works
- ✅ No term start date - "Not set" shown, Clear button hidden
- ✅ Import invalid JSON - error snackbar, no crash
- ✅ Export to restricted location - error snackbar
- ✅ Trigger developer tools without permissions - appropriate warnings/errors
- ✅ Rotate device - state preserved, layout adapts
- ✅ Background image URI becomes invalid - fallback to color
- ✅ Rapid navigation clicks - no crashes or incorrect states

## Performance

- ✅ Search results update instantly (< 100ms)
- ✅ Navigation transitions smooth
- ✅ No jank when scrolling long lists
- ✅ Voyager screen transitions performant
- ✅ Two-pane layout stable on tablets

## Accessibility

- ✅ All buttons have content descriptions
- ✅ TalkBack reads labels correctly
- ✅ Touch targets ≥ 48dp
- ✅ Color contrast sufficient
- ✅ Focus order logical

## Known Limitations

- Search is keyword-based, not fuzzy matching
- Two-pane threshold is fixed at 900dp (not configurable)
- No animation between search results and destination content
- Tablet mode doesn't remember last-selected destination across app restarts

## Migration Checklist

- [x] All 9 settings categories migrated to Voyager screens
- [x] Search indexes destinations + key controls
- [x] Two-pane layout implemented
- [x] Single-pane navigation works
- [x] All callbacks wired correctly
- [x] Snackbars work in all contexts
- [x] Transfer state (import/export progress) reflected
- [x] ActivityResult launchers work (image, import, export)
- [x] Permission requests work (DND, notifications)
- [x] External intents work (settings, links)
- [x] No functionality lost from old implementation
- [x] String resources added for search/transfer
- [x] Old SettingsScreen.kt removed

## Regression Testing Complete

All features from the original monolithic settings screen are present and working in the new Voyager-based implementation. Search functionality adds new capability without breaking existing workflows.
