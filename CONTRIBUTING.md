## Development

### Kanban board

**Kanban Board**: [Kanban Board](https://miro.com/app/board/uXjVIgjOSh4=/?share_link_id=200233752828)

Kanban board columns and entry criteria:
*  To Do - The isssue must be well-defined using the As/I want/So template and its acceptance criteria have to be specified.
*  In Progress - some team member must be assigned to the issue.
*  In Review - the implementation must be complete and the solution can be tested by team members.
*  Ready to Deploy - the code must have passed all tests and must be ready to be merged into the "main" brach.
*  User Testing - the feature is deployed and being tested by the customer.
*  Done - issue has been verified by customer feedback.

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

**Secrets Management**: Secret information is stored in "env" files or in CI/CD variables. Files with secret information are added to 
.gitignore