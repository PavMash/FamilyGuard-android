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

# Build and Deployment

### Continuous Integration(CI)

**CI workflow link**: [CI Workflow](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/deb2a403cefc0ef81483aded12616c619325dbe9/.gitlab-ci.yml)

**Static analysis tools**: ktlint

**Testing tools**: Espresso, JUnit, Mockito

**Espresso**: This tool was used to create Integration tests, which mostly check UI interactions

**JUnit**: This tool was used to create Unit tests, which mostly check Api services

**Mockito**: This tool was used in Unit tests to to simulate the behavior of API services

**All CI workflow runs**: [All CI Workflow Runs](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/pipelines)