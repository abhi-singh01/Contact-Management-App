• Built a multi-user contact manager in which every repository call is scoped to the signed-in account, so no user can read or modify 
another user's records; login is handled by a custom UserDetailsService with BCrypt-hashed passwords and role-based route 
protection.
• Exposed 15+ REST endpoints across the contact, profile, dashboard and assistant modules, and centralized error handling in a 
@RestControllerAdvice that maps missing-entity and validation failures to proper 404 and 400 responses instead of raw stack traces.
• Added a rule-based query assistant that turns plain-English requests such as "show my work contacts" or "what is Rahul's phone?" into 
intents, resolves the target contact through word-level scoring, and answers straight from JPA queries without any paid AI API.
• Implemented CSV import and export with a hand-written parser for quoted fields, email format validation, a 2 MB upload cap and 
duplicate detection on phone and email, returning a row-wise report of imported versus skipped entries.
• Wrote a parameterized JPQL search over name, phone and email with a switchable search type, plus category filters 
(Family/Friend/Work/Other), a favorites toggle and a dashboard endpoint that aggregates per-category counts for the logged-in user
