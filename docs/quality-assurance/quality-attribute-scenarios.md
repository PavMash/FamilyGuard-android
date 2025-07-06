# Quality Attribute Scenarios

## Availability

### 📶 Internet Loss Scenario

| Element             | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| **Source**          | Application                                                                 |
| **Stimulus**        | User has no internet                                                        |
| **Artifact**        | Text sending service                                                        |
| **Environment**     | Production environment without Internet connection                          |
| **Response**        | The system saves captured data locally and sends it to the server when there is an Internet connection |
| **Response Measure**| 5h maximum for storing a piece of data without losing the data              |

#### 🔬 Test Plan

- **Type**: Integration & Unit Test
- **Tools**: Python (Pytest), Kotlin (JUnit)
- **Test Steps**:
  1. Disable network on client application.
  2. Attempt to send text via the application.
  3. Re-enable network after 1h.
  4. Verify that the text is uploaded to the server.
- **Expected Result**: Message is successfully saved and synced when connection is restored.

---

## Performance Efficiency: Resource Utilization

### 🗃️ Storage Overflow Scenario

| Element             | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| **Source**          | Server application                                                          |
| **Stimulus**        | Too much user data stored                                                   |
| **Artifact**        | Server database                                                             |
| **Environment**     | Production                                                                  |
| **Response**        | Delete oldest data to store new one                                         |
| **Response Measure**| Logs are stored for one year                                                |

#### 🔬 Test Plan

- **Type**: Unit Test
- **Tools**: Python (Pytest with SQLite mocks)
- **Test Steps**:
  1. Simulate log entries until disk space exceeds a threshold.
  2. Trigger log-writing logic.
  3. Check if oldest logs are deleted.
- **Expected Result**: Only logs from the past year remain, disk usage stays under the limit.

---

## Security: Authenticity

### 🔐 Brute Force Attack Scenario

| Element             | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| **Source**          | Server application                                                          |
| **Stimulus**        | Username and password may be brute forced                                   |
| **Artifact**        | Server database                                                             |
| **Environment**     | Production                                                                  |
| **Response**        | Block the user for an hour                                                  |
| **Response Measure**| After 5 invalid attempts (sequential) user is banned                        |

#### 🔬 Test Plan

- **Type**: Integration & Manual Test
- **Tools**: Python (requests), Kotlin (mock login UI)
- **Test Steps**:
  1. Simulate 5 incorrect login attempts in a row.
  2. Attempt login with correct credentials within the block window.
- **Expected Result**: Login is denied and user is blocked for 1 hour.

---

Let me know if you'd like this in a downloadable `.md` file as well.
