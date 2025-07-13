# FamilyGuard

## ATTENTION
Here you cannot find the certificate, without which this app will not work

## Header

**Name**: FamilyGuard App
**Logo**: [Logo](app/src/main/res/drawable/shield.png)
**Link to the product**: [Product](app)
**Link to the Demo Video**: [Demo Video](https://drive.google.com/file/d/1kt15AYeClRTht8oJ7UdaMU0IaKRjjjqA/view?usp=drivesdk)

## Body

**Project Goal and Description**: The main goal of this project is to create an application, which will protect children from harmful content

Description: The project consists of three main parts: application, server, and website. Application scans the child's screen and sends the data to the server. Server redirects this data to AI, which analyzes received information. Then, AI creates categories and decides, which of them can be harmful. Then, the categories are sent to the website and if they are harmful, the application receives a request to block the content of these categories

**Project Context Diagram**: TODO

**Feature Roadmap**: TODO

**Usage instructions/Short user guide**: See below

**Installation/Deployment instructions**: See below

## Usage

**How to install**:
- Download Android Studio
- Copy this project to your local machine
- Open the copy in Android Studio
- Build an apk file
- Share the apk file with your phone
- Install the app on your phone
- Run the app and use

**How to use**:
- Run the app on your child's phone
- Create an account
- Log in
- Enable needed permissions for blocking, overlaying, scanning
- Enable Device Admin with password to protect the app from uninstalling
- Enable notifications, if you want to notify your child about monitoring his phone
- Close the app and be calm, your child is now protected from harmful content

## Architecture

### Static view
[Static view diagram](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/1c5784a76e01b40c0add49f4b7de7770ccc26130/docs/architecture/Static%20view/ComponentDiagram.png)

Our system demonstrates high cohesion and low-to-moderate coupling, which enhances maintainability and scalability. Each package in the architecture groups related responsibilities, ensuring cohesive functionality. 
For example, the Web_components package focuses solely on the web UI, while Backend_components cleanly separates backend logic into the content provider, coordinator, and data collector. 
Similarly, Android_side isolates mobile-specific functionality, and Side_services encapsulates auxiliary services like AI and the database.

We minimized coupling by using <\<uses>> relationships between components. This allows individual modules to evolve independently — for example, improvements to the AI agent or switching database technology would not directly impact the Web application or Android application UI layers.

These design decisions improve maintainability, as each module can be tested, debugged, and modified with minimal side effects on others. Additionally, the clear separation of concerns reduces complexity, making the codebase easier to understand and extend over time.

### Dynamic view
[Dynamic view diagram](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/1c5784a76e01b40c0add49f4b7de7770ccc26130/docs/architecture/Dynamic%20view/SeqDiagram.png)

- While child uses his/her phone, Screen scanner captures text from the screen. 
- Once in a minute captured text is sent to the cloud through Dispatch service. 
- In the cloud, AI agent analyses the text and detects inapropriate content.
- After analysis, information about detected categories is stored in Database.
- If inapropriate content is detected, server sends positive response to the Blocker that blocks the phone screen

### Deployment view
[Deployment View Diagram](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/974d57753ab3b38e13def0082d71ad57f6979891/docs/architecture/Deployment%20view/development-view.svg)

## Development
To contribute to the project read [CONTRIBUTING.md](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/main/CONTRIBUTING.md?ref_type=heads)

## Quality assurance

We follow the ISO/IEC 25010 standard for software quality characteristics and documented scenarios based on team and customer discussions.

### Quality attribute scenarios

Detailed descriptions are available in [docs/quality-assurance/quality-attribute-scenarios.md](docs/quality-assurance/quality-attribute-scenarios.md).

### User acceptance tests

Detailed descriptions of user acceptance tests are available in [docs/quality-assurance/user-acceptance-tests.md](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/53e497e2f4dc59282af80a99d887886774c3ec8a/docs/quality-assurance/user-acceptance-tests.md).

### How exactly we plan to test:

- Unit tests and integration tests using Python (FastAPI backend)
- Instrumentation and logic tests using Kotlin (Android app)
- Manual testing for edge cases and offline scenarios
- SQL queries tested via seed/test data for DB reliability

### Automated tests

Detailed description can be found in [automated-tests.md](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/main/docs/quality-assurance/automated-tests.md)

## Build and Deployment

### Continuous Integration (CI)

**Link**: [continuous-integration.md](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/main/docs/automation/continuous-integration.md)

## 🧩 Issues & Templates

### 📌 Issue Types
We categorize our issues by type to maintain structure and clarity:

- Epic – Large feature sets or initiativess
- Task – Technical implementations or short-term goals

Each type includes the following templates:


#### 📋 Backlog Issue Templates
- [User Story](.gitlab/issue_templates/user_story.md)
- [Bug Report](.gitlab/issue_templates/bug_report.md)

#### 🛠 Task Templates
- [Task](.gitlab/issue_templates/technical_task.md)

### 🛠 Labels

To track progress and prioritize work, the following labels are used:

#### ✅ Status Labels
- To do
- In progress
- In review
- Ready to deploy
- User testing
- Done

#### ⚠️ Priority Labels
- Priority::Critical
- Priority::High
- Priority::Medium
- Priority::Low

### 🔀 Merge & Pull Requests

Each merge request must be tied to a specific Task issue.

#### 🧾 Pull Request Templates
- [Task-related PR](.gitlab/merge_request_templates/default.md)


Each pull request must include:
- A link to the Task issue it addresses.
- A checklist to confirm readiness for merge.

### 🏁 Milestones

Milestones reflect major phases or goals in the project. They are not equivalent to sprints. All issues should be assigned to a milestone to track progress.
