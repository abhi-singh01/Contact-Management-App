# Next Gen Contact Manager - Extended Architecture

## Flow
Browser/Thymeleaf UI -> Controller -> Service -> Repository -> MySQL

## Added modules
- `Controller/AuthController`: current user, registration API, profile update, password change.
- `Controller/DashboardController`: authenticated dashboard statistics.
- `Service/UserService`: user profile and account business logic.
- `Service/DashboardService`: contact statistics.
- `DTO/RegisterRequest`, `UserProfileUpdateRequest`, `PasswordChangeRequest`, `DashboardDTO`.
- `Exception/*`: domain exceptions and common REST error responses.
- `/profile`: profile page.
- `/settings`: password/settings page.

## Main API flow
- `GET /api/auth/me`
- `POST /api/auth/register`
- `PUT /api/auth/profile`
- `PUT /api/auth/password`
- `GET /api/dashboard`

Existing contact APIs remain available for the current UI: add, update, delete, search, favorites, categories and CSV export.
