# Conversion status

## Verified automatically
- Spring application context: PASS
- `GET /api/ping`: PASS
- Public classes catalog: PASS in H2
- Public teachers catalog: PASS in H2
- Maven compile and package: PASS

## PHP baseline
The existing Laravel suite was executed. Several pre-existing feature failures remain, including the teacher dropdown assertion, pending/cancelled registration assertions and missing-customer-link assertions. They are not introduced by the Java backend and need a separate PHP baseline/fix review before a claim of 100% parity.

## Database
No database migration or data change is required. JPA uses `validate`; existing Laravel column names and soft-delete columns are preserved by entities. `attendance.attendance_date` is explicitly mapped.

## Not yet verified end-to-end
- Blade/form POST pages against Spring controllers (page actions currently remain PHP-rendered)
- Real MySQL data and schema differences
- Admin and teacher HTML dashboards
- Laravel Sanctum token parity
- BCrypt cost/seeded account login against real database
- Every dashboard aggregate and validation message
- Browser E2E

## Known integration constraint
The original frontend is Blade server-rendered. CSS and JavaScript have been copied into the Java resources tree, but Blade templates have not been converted by a converter. A production cutover must retain Laravel rendering temporarily or convert/render the templates with Thymeleaf and migrate routes/action URLs.
