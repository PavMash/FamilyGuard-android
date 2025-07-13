### Automated tests

- Tools used:
    - Python: pytest, unittest
    - Kotlin: Android JUnit, Espresso
    - SQL: manual seed/test scripts

- Types of tests implemented:
    - Unit tests
    - Integration tests
    - Offline/connection fallback behavior

- Where to find tests in repository:
    - [backend-tests](https://gitlab.pg.innopolis.university/familyguard-devs/cloud-storage/-/tree/database-changes/fastapi-tests?ref_type=heads) – Python backend unit & integration tests
    - app/src/test/ – Kotlin unit tests
    - app/src/androidTest/ – Kotlin UI and instrumentation tests
    - init.sql and test seeds – For DB test verification