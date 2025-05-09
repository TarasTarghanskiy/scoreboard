# Score Board

A solution for the Sportradar Coding Exercise.

[![Build](https://img.shields.io/badge/build-passing-brightgreen)](https://shields.io)
[![Java](https://img.shields.io/badge/java-22-blue)](https://openjdk.org/projects/jdk/22/)

---

## 🚀 How to Run

1. Clone this repository.

2. Build the project using Maven:

   ```bash
   mvn clean install
   ```

3. Run tests:

   ```bash
   mvn test
   ```

This is a library, not a standalone app — so there is no command-line interface or REST API.

---

## 📁 Project Structure

* `src/main/java` — main application code
* `src/test/java` — unit tests

---

## 📦 Requirements

* Java 22
* Maven

---

## 🧠 Design Notes

* **Why UUID?**
  I chose to use `UUID` to uniquely identify matches. At first, I considered a key like `homeTeam_awayTeam`, but it wasn't the obvious or safest choice for unique identification. UUIDs offer simplicity and clarity without relying on string manipulation.

* **Limits & Constraints**
  I understand it might seem like a conservative move to limit:

   * Maximum score per team: **50**
   * Maximum team name length: **30** characters
   * Maximum number of active matches: **100**

  However, this is a **Live Football World Cup** scoreboard, not a general-purpose data system. These constraints are realistic and help avoid pathological edge cases like `Integer.MAX_VALUE + Integer.MAX_VALUE` scenarios.

---

## ✅ Features

* Start a new match (score starts at 0–0)
* Update scores by match ID
* Finish a match
* View summary of active matches

   * Sorted by total score
   * Secondary sort by most recent start time
* Input validation:

   * Unique teams per match
   * Valid characters in team names
   * Score range enforcement

---

## 🧪 Test Strategy

This project was developed using **Test-Driven Development (TDD)**. Key characteristics of the test suite:

* JUnit 5 based
* Covers both positive and negative scenarios
* Tests for:

   * Input validation
   * Duplicate team detection
   * Match ordering logic
   * Score boundaries
   * Concurrency behavior

Run all tests with:

```bash
mvn test
```

---

## 🙏 Acknowledgements

Thanks to Sportradar for this opportunity. 
To be honest, I was initially skeptical about the TDD approach. 
However, I’ve come to appreciate several key benefits:
- It encourages me to build only what can be tested, 
  helping define realistic boundaries for the application and avoid overengineering.
- While writing tests often takes more time than writing the code itself, 
  starting with tests helps structure the work better and ultimately improves time management.

## 🔧 Areas for Improvement

Throughout this project, I also identified areas where I can continue to grow:
- I often skip verifying exception messages in my tests. 
  While I do check that the correct exceptions are thrown, I rarely assert the message content, which can be important.
- I should be more consistent in separating unit and integration tests.
- My test method names could be better.
- I’d like to use parameterized tests.

