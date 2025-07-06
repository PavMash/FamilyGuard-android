# Quality Attribute Scenarios

This document outlines the selected quality attributes for the FamilyGuard system, along with scenarios and test plans as specified in ISO 25010.

---

## Availability

### Why it's important

Availability is crucial for ensuring that user data is not lost when the device is offline. This ensures resilience and trustworthiness in unpredictable network conditions, especially for child devices that may lack stable connections.

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
- **Steps**:
  1. Simulate internet loss on the child app.
  2. Attempt to log or send activity.
  3. Wait 1 hour and restore network.
  4. Check server if data was synced.
- **Expected Result**: Data is persisted locally and successfully synced after network is restored.

---

## Performance Efficiency: Resource Utilization

### Why it's important

Efficient use of disk and memory resources ensures the app performs well over time, even on low-end devices. This is particularly important when logs accumulate quickly on child phones.

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
- **Tools**: Python (Pytest + SQL mocking)
- **Steps**:
  1. Fill database with synthetic data simulating 13+ months of logs.
  2. Add one more log.
  3. Verify oldest entry is deleted.
- **Expected Result**: Only the most recent 12 months of logs are kept.

---

## Security: Authenticity

### Why it's important

Unauthorized access or brute force attacks on the child-parent platform could expose sensitive data or allow misuse. Strong authentication mechanisms must exist to prevent abuse.

### 🔐 Brute Force Login Scenario

| Element             | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| **Source**          | Server application                                                          |
| **Stimulus**        | Username and password may be brute forced                                   |
| **Artifact**        | Server database                                                             |
| **Environment**     | Production                                                                  |
| **Response**        | Block the user for an hour                                                  |
| **Response Measure**| After 5 invalid attempts (sequential) user is banned                        |

#### 🔬 Test Plan

- **Type**: Integration Test
- **Tools**: Python (requests), Kotlin
- **Steps**:
  1. Attempt 5 incorrect logins using test credentials.
  2. Attempt a correct login immediately after.
  3. Wait an hour and try again.
- **Expected Result**: Account is blocked after 5 invalid attempts and automatically unblocked after 1 hour.

---

