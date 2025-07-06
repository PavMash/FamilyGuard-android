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

# Build and Deployment

### Continuous Integration(CI)

**CI workflow link**: [CI Workflow](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/deb2a403cefc0ef81483aded12616c619325dbe9/.gitlab-ci.yml)

**Static analysis tools**: ktlint

**Testing tools**: Espresso, JUnit, Mockito

**Espresso**: This tool was used to create Integration tests, which mostly check UI interactions

**JUnit**: This tool was used to create Unit tests, which mostly check Api services

**Mockito**: This tool was used in Unit tests to to simulate the behavior of API services

**All CI workflow runs**: [All CI Workflow Runs](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/pipelines)