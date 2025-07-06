## Website authorization and cookies test
1. Authorization test:
- **GIVEN:**
    - Website is opened
    - User is not signed up
- **WHEN:**
    - User signs up entering email and password
    - User presses "sign up" button
- **THEN:**
    - Website parent panel is opened
	
2. Cookies test:
- **GIVEN:**
    - Website is closed
    - User is already signed up (all actions above are completed)
- **WHEN:**
    - User opens the website using the same device and browser
- **THEN:**
    - Website parent panel is opened immediately, no need to log in

## Website theme changing test
- **GIVEN:**
    - Website is opened
    - User is signed up
    - User is located in the dashboard
- **WHEN:**
    - User presses the settings button
    - User presses the button with the sign of sun
- **THEN:**
    - The website is changing its theme from dark to light

## Android app authentication and setting test
- **GIVEN:**
    - App is installed
- **WHEN:**
    - User opens app
    - User clicks "Sign up" button,
    - "Sign up" screen appears and user enters email and password in and clicks "Sign up" button again
    - User enters email and password and clicks "Log in" button
    - Device admin screen appears and user presses "Activate this device admin app"
    - User gives permission for notifications
    - User sets password for disabling Device admin mode
- **THEN:**
    - App's main screen appears with buttons for giving necessary permissions and disabling Device admin mode
    - App is ready for receiving further permissions

##Android app deletion test
1. Test on deletion with Device admin mode enabled:
- **GIVEN:**
    - App is installed
    - Device admin mode is enabled
    - Device admin password is set
    - User gave permission for blocking service
- **WHEN:**/n
    *1st option:*
    - User tries to uninstall the app dragging it to the top of the screen
    - User clicks "Ok" in the pop-up uninstall window
    - User opens notification and clicks "Manage device admin apps"
    - User clicks on FamilyGuard icon
	*2nd option:*
    - User opens app info and clicks "uninstall"
    *3rd option:*
    - User opens settings and opens "Device admin" menu
    - User opens list of apps with Device admin mode enabled
    - User clicks on FamilyGuard icon
- **THEN:**
	*holds for each option:* 
    - Device admin mode managing menu is opened
    - Blocking screen appears immediately, preventing user from disabling Device admin mode

2. Test on disabling Device admin mode and deleting the app:
- **GIVEN:**
    - App is installed
    - Device admin mode is enabled
    - Device admin password is set
- **WHEN:**
    - User opens the app and logs in
    - User enter the password to the field above "Disable device admin" button
    - User clicks on "Disable device admin" button
    - User closes the app and tries any of uninstalling options listed above
- **THEN:**
    - The app is uninstalled

## Automatical child adding, when logging in  the app
- **GIVEN:**
    - User is signed up in the website
- **WHEN:**
    - User logs in in the app
    - User opens website
- **THEN:**
    - One child with default name (Magomed) appears on the website dashboard

## Malicious content blocking
- **GIVEN:**
    - User is logged in the website
    - User is logged in the Android app
    - User gave all necessary permissions in Android app
- **WHEN:**
    - User searches for some prohibited content (e.g. drugs, hate, violence), such that there is available text in the screen
- **THEN:**
    - Blocking screen appears (within a minute)
    - After closing the blocking screen, the app, where illegal content was detected, will be unavailable for two minutes (blocking screen will appear once again, if app is opened)
    - Category of detected content appears in the website, bellow the child's name
