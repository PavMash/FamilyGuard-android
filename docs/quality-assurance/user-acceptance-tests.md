## Website authorization and cookies test
1. Authorization test:
**GIVEN:**
- Website is opened
- User is not signed up
**WHEN:**
- User signs up entering email and password
- User presses "sign up" button
**THEN:**
- Website parent panel is opened
	
2. Cookies test:
**GIVEN:**
- Website is closed
- User is already signed up (all actions above are completed)
**WHEN:**
- User opens the website using the same device and browser
**THEN:**
- Website parent panel is opened immediately, no need to log in
