# Bulk Contact Import

The Contact Manager now supports importing many contacts from a CSV file.

## CSV format

```text
Name,Phone Number,Email,Category,Favorite
Rahul Sharma,9876543210,rahul@example.com,FRIEND,false
Priya Singh,9876501234,priya@example.com,FAMILY,true
```

The **Download Template** button on the dashboard creates this format automatically.
The CSV exported by the application can also be imported.

## What happens during import

1. The browser checks that a CSV file is selected and is no larger than 2 MB.
2. Spring Boot receives the file through `MultipartFile`.
3. The server identifies the currently authenticated user from Spring Security.
4. Each row is validated for required fields and email format.
5. Category is normalized to FAMILY, FRIEND, WORK, or OTHER.
6. Existing contacts are detected using phone number or email and skipped.
7. Valid rows are saved with the logged-in user.
8. The UI shows the number imported and skipped, including row-level reasons for skipped rows.

## Example workflow

Export contacts from another copy of the application (or prepare the CSV using the template), edit the CSV in Excel/Google Sheets, save it as CSV, then use **Import CSV** on the dashboard.

## Security

The import endpoint does not accept a user email from the browser to decide ownership. It uses the authenticated Spring Security principal, so an uploaded file is associated with the account that is currently logged in.
