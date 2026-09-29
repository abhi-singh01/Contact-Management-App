# Next Gen Contact Manager - Added Features

This version keeps the existing authentication, contact CRUD, favorites, and advanced search and adds three practical features.

## 1. Contact Categories
Each contact can be assigned to FAMILY, FRIEND, WORK, or OTHER.

Flow:
UI -> Contact JSON -> ContactController -> ContactService -> ContactRepository -> MySQL

The `category` field is stored in the Contact entity. Hibernate updates the existing table because `spring.jpa.hibernate.ddl-auto=update` is enabled.

## 2. Filter and Sort
Users can filter contacts by category and sort the currently displayed results by name A-Z, name Z-A, or category.

Category filtering uses a repository method:
`findByUserEmailAndCategoryIgnoreCase(...)`

Sorting is done in the browser on the currently loaded list, so it does not require an extra database query.

## 3. Export Contacts to CSV
The Export button calls:
`GET /contacts/export?userEmail=...`

The service creates a CSV containing:
Name, Phone Number, Email, Category, Favorite

The controller returns it with `Content-Disposition: attachment`, so the browser downloads `contacts.csv`.

## Existing Features Kept
- User registration/login
- Spring Security
- Add/update/delete contacts
- User-specific contacts
- Favorite contacts
- Advanced search by name, phone, email, or all fields

## Database
Run the application once after updating the code. Hibernate should add the `category` column automatically with `ddl-auto=update`.

## Suggested test order
1. Start MySQL.
2. Start Spring Boot in IntelliJ.
3. Login.
4. Add contacts with different categories.
5. Verify category appears on contact cards.
6. Test category filter.
7. Test A-Z/Z-A sorting.
8. Mark a contact as favorite and verify it still shows as favorite.
9. Search by name/phone/email.
10. Click Export Contacts to CSV and open the downloaded file.
