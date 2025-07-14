# Architecture

## Static view
[Static view diagram](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/1c5784a76e01b40c0add49f4b7de7770ccc26130/docs/architecture/Static%20view/ComponentDiagram.png)

Our system demonstrates high cohesion and low-to-moderate coupling, which enhances maintainability and scalability. Each package in the architecture groups related responsibilities, ensuring cohesive functionality. 
For example, the Web_components package focuses solely on the web UI, while Backend_components cleanly separates backend logic into the content provider, coordinator, and data collector. 
Similarly, Android_side isolates mobile-specific functionality, and Side_services encapsulates auxiliary services like AI and the database.

We minimized coupling by using <\<uses>> relationships between components. This allows individual modules to evolve independently — for example, improvements to the AI agent or switching database technology would not directly impact the Web application or Android application UI layers.

These design decisions improve maintainability, as each module can be tested, debugged, and modified with minimal side effects on others. Additionally, the clear separation of concerns reduces complexity, making the codebase easier to understand and extend over time.

## Dynamic view
[Dynamic view diagram](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/1c5784a76e01b40c0add49f4b7de7770ccc26130/docs/architecture/Dynamic%20view/SeqDiagram.png)

- While child uses his/her phone, Screen scanner captures text from the screen. 
- Once in a minute captured text is sent to the cloud through Dispatch service. 
- In the cloud, AI agent analyses the text and detects inapropriate content.
- After analysis, information about detected categories is stored in Database.
- If inapropriate content is detected, server sends positive response to the Blocker that blocks the phone screen

## Deployment view
[Deployment View Diagram](https://gitlab.pg.innopolis.university/familyguard-devs/familyguard/-/blob/974d57753ab3b38e13def0082d71ad57f6979891/docs/architecture/Deployment%20view/development-view.svg)


## Tech Stack

### Android application
**Programming Languages:**
- Kotlin  

### Server application
**Programming languages:**
- Python _(FastAPI)_
- JavaScript  

**Databases**
- PostgreSQL  

**Containerisation:**
- Docker  

**Web Pages**
- HTML
- CSS