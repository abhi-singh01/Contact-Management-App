# Implemented Features

## Existing features preserved
- Spring Security login/logout
- User registration
- Contact CRUD
- Advanced search by all/name/phone/email
- Favorites
- Categories: Family, Friend, Work, Other
- Sorting and filtering in UI
- CSV export
- User details shown on dashboard

## New features added
1. Layered user service for account operations.
2. Auth API: current-user profile, registration, profile update and password change.
3. Dashboard API with total/favorite/category statistics.
4. Profile page with editable first/last name.
5. Settings page with password change.
6. Domain-specific `UserNotFoundException` and `ContactNotFoundException`.
7. Global REST exception handler returning JSON errors.
8. Contact update now verifies ownership before changing a record.
9. Logout now performs a real Spring Security POST logout instead of only clearing localStorage.
10. Existing user/contact APIs remain compatible with the current UI.
