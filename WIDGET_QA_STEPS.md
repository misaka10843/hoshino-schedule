# Schedule Widget QA Steps

## Overview
The Schedule Widget has been modernized with responsive sizing, improved visuals, and interactive elements.

## Test Cases

### 1. Widget Size Responsiveness
Test that the widget adapts appropriately to different sizes:

#### Compact Size (110x110 dp, 180x110 dp)
- [ ] Widget displays day-of-week and date in header
- [ ] Shows only one upcoming class (or empty state)
- [ ] "Next" pill appears for the first upcoming class
- [ ] Course name, time, teacher/location are visible
- [ ] Layout is clean and not cramped

#### Medium/Expanded Size (250x150 dp, 250x250 dp)
- [ ] Widget displays full day-of-week, timetable name, and date badge
- [ ] Shows up to two upcoming classes in card format for larger sizes
- [ ] Cards have proper background color (cardBackground)
- [ ] "Next" pill appears only on the first card
- [ ] Teacher and location appear as chips below course name
- [ ] Proper spacing between cards

### 2. Visual Design
Test visual consistency and theming:

#### Light Theme
- [ ] Background uses user's timetable color with 95% alpha (if color mode)
- [ ] Falls back to default dark background (0xFF1C1B1F) if no color
- [ ] Text is readable with proper contrast
- [ ] Status pill is green (#4CAF50) with white text
- [ ] Cards have darker background (0xFF2C2B30)

#### Dark Theme
- [ ] Background uses user's timetable color with 85% alpha
- [ ] All text colors are appropriate for dark mode
- [ ] Contrast is maintained for readability

#### Gradient/Background
- [ ] If user has set background mode to COLOR, widget uses that color
- [ ] Color is parsed correctly from hex format (#RRGGBB or #AARRGGBB)
- [ ] If background mode is IMAGE, falls back to default color
- [ ] Background has subtle transparency (not fully opaque)

### 3. Content Display

#### With Upcoming Classes
- [ ] Shows up to 2 classes for expanded widget
- [ ] Shows only 1 class for compact widget
- [ ] Classes are sorted by start time (earliest first)
- [ ] Only shows classes from today that haven't started yet
- [ ] Time is displayed in 12-hour format (AM/PM)
- [ ] Course name is prominent and readable
- [ ] Teacher name appears with bullet separator (if set)
- [ ] Location appears with bullet separator (if set)
- [ ] First class has "Next" status indicator

#### No Upcoming Classes (Empty State)
- [ ] Shows checkmark (✓) icon
- [ ] Displays "No upcoming classes" text (or localized equivalent)
- [ ] Centered in widget with appropriate styling
- [ ] Icon color matches theme (primary color: #80CBC4)

#### Error State
- [ ] Shows warning icon (⚠) when data cannot be loaded
- [ ] Displays "Widget unavailable" text (or localized equivalent)
- [ ] Text is styled in italics with secondary color
- [ ] Centered and readable

### 4. Interactions

#### Widget Tap
- [ ] Tapping anywhere on the widget opens MainActivity
- [ ] App launches to the Schedule screen (default start destination)
- [ ] PendingIntent is created with appropriate flags for Android 12+
- [ ] No crashes when tapping widget multiple times rapidly

#### App Launch
- [ ] Widget opens the main app without any navigation issues
- [ ] After app launch, widget continues to work normally
- [ ] Widget updates reflect when returning to home screen

### 5. Data Loading & Error Handling

#### Successful Data Load
- [ ] Widget loads courses, periods, and preferences without errors
- [ ] Displays data within reasonable time (< 1 second)
- [ ] Date/time calculations are correct for current timezone

#### Error Scenarios
- [ ] Widget doesn't crash if courses repository throws exception
- [ ] Widget doesn't crash if periods repository throws exception
- [ ] Widget doesn't crash if preferences are unavailable
- [ ] All data fetches are wrapped with runCatching
- [ ] Fallback values are sensible (empty lists, default preferences)

#### Edge Cases
- [ ] No courses defined: shows empty state
- [ ] All courses are in the past: shows empty state
- [ ] Courses with missing teacher/location: displays without error
- [ ] Invalid background color: falls back to default
- [ ] Very long course names: truncated or wrapped appropriately

### 6. Update Behavior

#### Widget Refresh
- [ ] Widget updates when app data changes
- [ ] Widget reflects new courses added in the app
- [ ] Widget reflects course deletions
- [ ] Widget updates on day change (midnight)

#### System Events
- [ ] Widget updates after device reboot
- [ ] Widget updates after timezone change
- [ ] Widget handles system time change gracefully

### 7. Multiple Widget Instances
- [ ] Can add multiple widgets to home screen
- [ ] All instances show the same data (today's schedule)
- [ ] All instances update independently
- [ ] Tapping different instances all open the app correctly

### 8. Accessibility
- [ ] Text sizes are readable (minimum 11sp)
- [ ] Color contrast meets accessibility guidelines
- [ ] Important information (course name, time) is prominent
- [ ] Icons have semantic meaning (✓ for empty, ⚠ for error)

## Test Scenarios

### Scenario 1: Set Upcoming Classes
1. Open app and add courses for today
2. Ensure at least 2 courses are scheduled in the future
3. Go to home screen and check widget
4. **Expected**: Widget shows next 1-2 classes with "Next" pill on first

### Scenario 2: No Courses Today
1. Remove all courses or ensure none are scheduled today
2. Go to home screen and check widget
3. **Expected**: Widget shows checkmark and "No upcoming classes"

### Scenario 3: Tap Widget Opens App
1. Ensure widget is visible on home screen
2. Tap anywhere on the widget
3. **Expected**: App opens to Schedule screen, no crashes

### Scenario 4: Change Timetable Color
1. Open app → Settings → Background settings
2. Change background mode to COLOR
3. Select a custom color (e.g., #FF5722)
4. Go to home screen
5. **Expected**: Widget background reflects the chosen color with transparency

### Scenario 5: Different Widget Sizes
1. Add widget in smallest size (1x1)
2. **Expected**: Compact layout with single class
3. Resize widget to larger size (3x2 or larger)
4. **Expected**: Expanded layout with card-based design and up to 2 classes

### Scenario 6: Light vs Dark Theme
1. Test widget in system light mode
2. Switch system to dark mode
3. **Expected**: Widget colors adapt, maintaining readability in both modes

## Regression Tests
- [ ] Widget doesn't interfere with app's normal operation
- [ ] Widget doesn't cause excessive battery drain
- [ ] Widget doesn't slow down home screen
- [ ] No crashes related to widget in crash logs
- [ ] WorkManager reminders still function correctly
- [ ] DND toggle worker is unaffected

## Performance Checks
- [ ] Widget loads in < 1 second on average device
- [ ] No ANR (Application Not Responding) errors
- [ ] Memory usage is reasonable (<10MB)
- [ ] Widget updates don't cause frame drops on home screen

## Notes for Developers
- Widget uses `SizeMode.Responsive` with 4 size breakpoints
- Background color parsing uses `parseHexColor()` helper
- All network/IO operations use `runCatching` for safety
- PendingIntent uses `actionStartActivity<MainActivity>()` for app launch
- Widget follows Material Design 3 principles for Glance
- Typography uses proper hierarchy (20sp headers down to 11sp chips)
