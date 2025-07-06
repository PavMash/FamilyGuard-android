# FamilyGuard



##ATTENTION
Here you cannot find the certificate, without which this app will not work

# 🧩 Issues & Templates

## 📌 Issue Types
We categorize our issues by type to maintain structure and clarity:

- Epic – Large feature sets or initiativess
- Task – Technical implementations or short-term goals

Each type includes the following templates:


### 📋 Backlog Issue Templates
- [User Story](.gitlab/issue_templates/user_story.md)
- [Bug Report](.gitlab/issue_templates/bug_report.md)

### 🛠 Task Templates
- [Task](.gitlab/issue_templates/technical_task.md)

## 🛠 Labels

To track progress and prioritize work, the following labels are used:

### ✅ Status Labels
- To do
- In progress
- In review
- Ready to deploy
- User testing
- Done

### ⚠️ Priority Labels
- Priority::Critical
- Priority::High
- Priority::Medium
- Priority::Low

## 🔀 Merge & Pull Requests

Each merge request must be tied to a specific Task issue.

### 🧾 Pull Request Templates
- [Task-related PR](.gitlab/merge_request_templates/default.md)


Each pull request must include:
- A link to the Task issue it addresses.
- A checklist to confirm readiness for merge.

## 🏁 Milestones

Milestones reflect major phases or goals in the project. They are not equivalent to sprints. All issues should be assigned to a milestone to track progress.

## Development

### Kanban board

**Kanban Board**: [Kanban Board](https://miro.com/app/board/uXjVIgjOSh4=/?share_link_id=200233752828)

### Git workflow

**Base Workflow**: GitHub flow adapted to GitLab

**Creating issues**: When we have new feature and acceptance criteria for it, we create new issue

**Labelling issues**: Each issue is labeled as "Task", and every issue has priority(from Low to Critical) according to customer needs

**Assigning issues to team members**: In the beginning of every week our team distributes tasks between the members. Everyone considers what tasks he want to take, so there is no disagreement

**Creating, naming, merging branches**: We have "main" branch and from this branch every member creates a new branch for a new feature. Names of branches are assigned by their creators, names represents a feature. In the end of week all branches are merged into "main" branch by one member(sometimes each member merges only his branch)

**Commit messages format**: Commit messages provide relevant information, Present Simple is usually used

**Creating a pull request for an issue using a pull request template**: We do not use pull requests, instead we merge all branches in the end of a week and resolve merge conflicts

**Code reviews**: We do not have code reviews. If something is wrong in the code, the contributor, who made the mistake, solves the problem

**Merging pull requests**: We do not have pull requests, in the end of a week we merge all branches into "main" and resolve merge conflicts

**Resolving issues**: When we resolve an issue, we close them

**GitGraph Diagram**: [GitGraph Diagram](docs/images/GitGraphDiagram.png)

### Secrets Management

**Secrets Management**: Secret information is stored in "env" files or in CI/CD variables. Files with secret information are added to .gitignore

## Quality assurance

We follow the ISO/IEC 25010 standard for software quality characteristics and documented scenarios based on team and customer discussions.

### Quality attribute scenarios

Detailed descriptions are available in [docs/quality-assurance/quality-attribute-scenarios.md](docs/quality-assurance/quality-attribute-scenarios.md).

### Chosen quality sub-characteristics

#### Availability

Why it matters: Users may experience a loss of internet connection. Ensuring offline support improves reliability and user trust.

Scenario:
- Source: Application
- Stimulus: User has no internet
- Artifact: Text sending service
- Environment: Production environment without Internet connection
- Response: The system saves captured data locally and sends it to the server when internet is available
- Response Measure: Data is stored locally for up to 5 hours without loss

#### Performance Efficiency: Resource Utilization

Why it matters: Server resources such as disk space must be used efficiently to ensure scalability and performance.

Scenario:
- Source: Server application
- Stimulus: Too much user data stored
- Artifact: Server database
- Environment: Production
- Response: Delete oldest data to store new data
- Response Measure: Logs are stored for one year

#### Security: Authenticity

Why it matters: User accounts must be protected from brute-force attacks to prevent unauthorized access.

Scenario:
- Source: Server application
- Stimulus: Username and password may be brute-forced
- Artifact: Server database
- Environment: Production
- Response: Block the user for an hour
- Response Measure: After 5 failed (sequential) login attempts, user is banned

### How exactly we plan to test:

- Unit tests and integration tests using Python (FastAPI backend)
- Instrumentation and logic tests using Kotlin (Android app)
- Manual testing for edge cases and offline scenarios
- SQL queries tested via seed/test data for DB reliability

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
    - backend/tests/ – Python backend unit & integration tests
    - app/src/test/ – Kotlin unit tests
    - app/src/androidTest/ – Kotlin UI and instrumentation tests
    - init.sql and test seeds – For DB test verification

# Build and Deployment

### Continuous Integration(CI)

**CI workflow link**: [CI Workflow](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/deb2a403cefc0ef81483aded12616c619325dbe9/.gitlab-ci.yml)

**Static analysis tools**: ktlint

**Testing tools**: Espresso, JUnit, Mockito

**Espresso**: This tool was used to create Integration tests, which mostly check UI interactions

**JUnit**: This tool was used to create Unit tests, which mostly check Api services

**Mockito**: This tool was used in Unit tests to to simulate the behavior of API services

**All CI workflow runs**: [All CI Workflow Runs](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/pipelines)