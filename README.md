# Election Voting Mechanism System

A desktop **Election Voting Mechanism System** built with **Java AWT** as an academic (college) **Object-Oriented Programming (OOP)** project. An administrator sets up candidates and controls a single election, voters register and log in to cast one vote each, and the results screen shows the vote counts, winner, or tie.

> **Academic project:** this is a learning prototype. It is not a secure or production-grade election platform. See [Limitations](#limitations) and the [Disclaimer](#academic-project-disclaimer).

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Objectives](#objectives)
3. [Features](#features)
4. [Technology Stack](#technology-stack)
5. [Project Structure](#project-structure)
6. [Application Workflow](#application-workflow)
7. [Admin Module](#admin-module)
8. [Voter Module](#voter-module)
9. [Election Results](#election-results)
10. [OOP Concepts Used](#oop-concepts-used)
11. [Prerequisites](#prerequisites)
12. [How to Run](#how-to-run)
13. [Admin Login Credentials](#admin-login-credentials)
14. [Validation and Error Handling](#validation-and-error-handling)
15. [Limitations](#limitations)
16. [Future Enhancements](#future-enhancements)
17. [Academic Project Disclaimer](#academic-project-disclaimer)

---

## Project Overview

Manual voting and counting are slow and easy to get wrong. This project demonstrates how a small, computerised voting process can work: candidates are managed in one place, each registered voter can vote only once, and votes are counted automatically.

There are two types of users:

* **Admin** – logs in, adds or removes candidates, starts and ends the election, and views the results.
* **Voter** – registers, logs in, selects one candidate, and casts a vote while the election is running.

All screens read and write the same shared data classes, so a change made on one screen (for example, the admin starting the election) is seen by the others during the same run.

**This is an Object-Oriented Programming project.** The system is divided into classes with clear responsibilities, and it demonstrates core OOP concepts:

* **Classes and objects** – each screen (menu, logins, dashboards, results) is its own class, and voters, candidates, and votes are objects.
* **Encapsulation** – voter, candidate, and vote details are private and accessed through getters; the shared lists are reached only through methods.
* **Inheritance and interfaces** – every screen extends AWT's `Frame` and implements `ActionListener`.
* **Polymorphism** – methods such as `actionPerformed()` and `dispose()` are overridden.
* **Abstraction** – screens call methods like `VotingData.castVote(...)` without knowing how the data is stored.
* **Enums, nested classes, and static members** – used for the election status, data records, and shared data.

See [OOP Concepts Used](#oop-concepts-used) for where each concept appears in the code.

## Objectives

* Demonstrate Object-Oriented Programming concepts through a real, working application.
* Build a working election workflow (setup, voting, results) in Java.
* Create a desktop user interface using only **Java AWT**.
* Allow voters to register and log in.
* Let an admin manage candidates and control the election stages.
* Enforce **one vote per voter** and block voting when the election is not running.
* Count votes and show the winner or a tie.
* Validate user input and show clear error messages.

## Features

All features below were checked in the source code.

| Area | Feature | Status |
| --- | --- | --- |
| Main menu | Buttons for Admin Login, Voter Registration, Voter Login, and Exit (with a confirmation dialog) | Implemented |
| Admin | Login with fixed demo credentials | Implemented |
| Admin | Add candidate (ID, name, party) | Implemented |
| Admin | View candidate list | Implemented |
| Admin | Remove a selected candidate (with confirmation) | Implemented |
| Admin | Start election (needs at least 2 candidates) and end election | Implemented |
| Admin | Summary of candidates, registered voters, votes cast, and election status | Implemented |
| Admin | View results | Implemented |
| Voter | Registration (full name, age, voter ID, username, password) | Implemented |
| Voter | Login with username and password | Implemented |
| Voter | Dashboard showing election status and the voter's own voting status | Implemented |
| Voter | Select a candidate and cast a vote, with a confirmation step | Implemented |
| Voter | "Check Voting Status" button | Implemented |
| Voter | One vote per voter | Implemented |
| Voter | Logout | Implemented |
| Results | Votes and percentage share per candidate, total votes, winner, tie message, provisional notice | Implemented |
| Data storage | Database or file storage | **Not implemented** (memory only) |

**Not implemented:** Aadhaar number, date of birth, OTP, biometric checks, encryption or password hashing, and a database. The registration form asks for **age**, not date of birth.

## Technology Stack

* **Language:** Java (Java 26)
* **GUI:** Java AWT only (`Frame`, `Panel`, `Label`, `TextField`, `Button`, `Dialog`, `Checkbox` / `CheckboxGroup`, `List`, `ScrollPane`)
* **Event handling:** `ActionListener`, `WindowAdapter`
* **Data storage:** Java collections (`ArrayList`) held in memory
* **Editor:** Visual Studio Code
* **External libraries / database:** none

## Project Structure

Based on the source files in the project's `src` folder:

```text
election-voting-management-system/
├── src/
│   ├── MainMenu.java          # Entry point and main menu
│   ├── AdminLogin.java        # Admin login screen
│   ├── AdminDashboard.java    # Candidate management and election control
│   ├── ResultsPage.java       # Election results screen
│   ├── VoterRegistration.java # Voter registration screen
│   ├── VoterLogin.java        # Voter login screen
│   ├── VoterDashboard.java    # Voting screen and voting status
│   ├── VoterData.java         # Shared data: registered voters, current voter
│   └── VotingData.java        # Shared data: candidates, election status, votes
└── README.md
```

| File | Role |
| --- | --- |
| `VoterData.java` | Stores registered voters and the currently logged-in voter. Used by registration, login, and the voter dashboard. |
| `VotingData.java` | Stores candidates, votes, and the election status (`NOT_STARTED`, `RUNNING`, `ENDED`). Used by the admin dashboard, voter dashboard, and results page. |

## Application Workflow

```text
                     MainMenu
        ┌───────────────┼────────────────┐
   Admin Login   Voter Registration   Voter Login
        │                │                 │
        ▼                ▼                 ▼
 AdminDashboard     (back to menu)   VoterDashboard
   │        │                          │
   ▼        ▼                          ▼
ResultsPage Logout                  Logout
   │         │                         │
   ▼         ▼                         ▼
AdminDashboard  MainMenu            MainMenu
```

1. The app starts at **MainMenu**.
2. **Admin Login** → on success opens **AdminDashboard**. From there the admin can open **ResultsPage** (its *Back to Dashboard* button returns to the dashboard) or log out to the menu.
3. **Voter Registration** → saves the voter, then the voter returns to the menu and uses **Voter Login**.
4. **Voter Login** → on success opens **VoterDashboard**; *Logout* returns to the menu.
5. The window's close (X) button acts as *Back* or *Logout* on these screens, and the main menu asks for exit confirmation. Only one window of each screen can be open at a time.

Typical election order: admin adds candidates → starts the election → voters register, log in, and vote → admin ends the election → admin views the final results.

## Admin Module

* **Login:** fixed demo username and password (see [Admin Login Credentials](#admin-login-credentials)).
* **Dashboard summary:** number of candidates, registered voters, votes cast, and the election status.
* **Add Candidate:** enter a candidate ID, name, and party. Only allowed before the election starts.
* **Remove Candidate:** select a candidate in the list and confirm. Only allowed before the election starts.
* **Start Election:** needs at least **2** candidates and a confirmation. After this, candidates can no longer be added or removed.
* **End Election:** closes voting with a confirmation. It cannot be reopened.
* **View Results:** opens the results screen.
* **Logout:** returns to the main menu.

## Voter Module

* **Registration:** full name, age, voter ID, username, and password. The voter must be at least 18 and the voter ID and username must be unique.
* **Login:** username (not case-sensitive) and password (case-sensitive). The logged-in voter is remembered while the program runs.
* **Voter dashboard:** shows the voter's name and ID, the election status, and whether they have already voted.
* **Cast Vote:** choose one candidate, confirm, and the vote is recorded. Voting is refused if there are no candidates, the election has not started or has ended, the voter has already voted, or no candidate is selected.
* **Check Voting Status:** shows whether the voter has voted, can still vote, or missed the election.
* **Logout:** forgets the current voter and returns to the main menu.

## Election Results

The results screen reads directly from `VotingData`:

* Shows the election status and the **total votes**.
* Lists every candidate (ID, name, party) sorted from most to fewest votes, with **votes** and **share** (`votes ÷ total votes × 100`, shown as `0.0%` when no votes exist, so there is no division by zero).
* Message under the table:
  * **Not started:** "The election has not started yet."
  * **Running:** "Election in progress – these results are provisional."
  * **Ended with no votes:** "The election has ended. No votes were cast."
  * **Ended with one top candidate:** shows the **winner** and their party.
  * **Ended with equal top votes:** shows a **tie** between the leading candidates instead of one winner.
* A winner or tie is declared **only after the election has ended**.

## OOP Concepts Used

This project is built from classes and objects. The concepts below were checked in the source code.

| Concept | Where it is used |
| --- | --- |
| **Classes and objects** | Every screen is a class (`MainMenu`, `AdminLogin`, `AdminDashboard`, `VoterDashboard`, etc.) and is created with `new`. Data is held in `Voter`, `Candidate` and `Vote` objects. |
| **Encapsulation** | Fields in `Voter`, `Candidate` and `Vote` are `private final` and read through getters. The voter, candidate and vote lists are private and reached only through methods. `getCandidates()` and `getAllVoters()` return read-only views. |
| **Constructors** | `Voter(...)`, `Candidate(...)`, `Vote(...)`, and each screen's constructor, which builds its window. |
| **Inheritance** | Every screen `extends Frame` and inherits its window behaviour from AWT. `PaddedPanel` (inside the two dashboards) `extends Panel`. |
| **Interface implementation** | Every screen `implements ActionListener` to handle button clicks. |
| **Polymorphism (method overriding)** | `actionPerformed()`, `dispose()`, `getInsets()`, `getPreferredSize()` and `windowClosing()` are overridden, and AWT calls these versions at run time. |
| **Nested classes** | `VoterData.Voter`, `VotingData.Candidate`, `VotingData.Vote` and `PaddedPanel` are static nested classes. |
| **Enum** | `VotingData.Status` (`NOT_STARTED`, `RUNNING`, `ENDED`) with a display label. |
| **Abstraction** | Screens call methods such as `VotingData.castVote(...)` and `VoterData.authenticate(...)` without knowing how the data is stored. |
| **Static members** | `VoterData` and `VotingData` use private constructors and static methods, so all screens share one copy of the data. |
| **Exception handling** | A `try/catch` for `NumberFormatException` when reading the age in `VoterRegistration`. |

**Not used:** a custom class hierarchy (such as `User` → `Admin` / `Voter`), your own abstract classes or interfaces, and custom exception classes.

## Prerequisites

* **JDK 26** (the project is written for Java 26; check with `java -version` and `javac -version`)
* **Visual Studio Code** with the *Extension Pack for Java* (optional, but recommended)
* A desktop environment (AWT opens real windows, so it cannot run on a headless server)

## How to Run

The entry point is **`MainMenu`**. Open a terminal in VS Code (PowerShell on Windows):

```powershell
cd src
Remove-Item *.class -ErrorAction SilentlyContinue   # clear old compiled files
javac *.java                                        # compile all screens together
java MainMenu                                       # start the application
```

All files must be compiled together because the screens refer to each other.

**Quick test (use a single run, because data is kept in memory):**

1. Admin Login → add at least 2 candidates → Start Election → Logout.
2. Voter Registration → register a voter → Back.
3. Voter Login → select a candidate → Cast Vote → Logout.
4. Admin Login → End Election → View Results.

## Admin Login Credentials

> **Demo credentials only.** These are hard-coded in `AdminLogin.java` for the prototype.

| Username | Password |
| --- | --- |
| `admin` | `1234` |

Voters have no default accounts; each voter must register first.

## Validation and Error Handling

**Admin login:** username and password must not be empty; wrong credentials show an error and clear the password.

**Voter registration:**
* All fields are required.
* Age must be a whole number, at least **18** and at most **120**.
* Voter ID must be unique (not case-sensitive).
* Username must be unique (not case-sensitive).
* The password is hidden while typing.

**Voter login:** both fields are required; an incorrect username or password shows an error.

**Add candidate:** ID, name, and party are required; ID up to 10 characters with no spaces; name up to 24 and party up to 20 characters; the ID must be unique.

**Election control:** candidates can only change before the start; at least 2 candidates are needed to start; ending requires a running election; start, end, and removal ask for confirmation.

**Voting:** a voter must be logged in; candidates must exist; the election must be running; the voter must not have voted already; exactly one candidate must be selected; the voter confirms before the vote is saved. The vote is checked again inside `VotingData.castVote(...)`.

Errors are shown in small modal dialogs. Input parsing uses `try/catch` for the age field.

## Limitations

* **All data is stored in memory only.** Voters, candidates, votes, and the election status are **lost when the program closes**; there is no database or file storage.
* The admin username and password are **hard-coded** and there is only one admin account.
* Voter passwords are stored as **plain text** (no hashing or encryption).
* Age is typed in by the voter and is **not verified** against any identity document. There is no Aadhaar, date of birth, OTP, or biometric verification.
* Vote records store the voter ID together with the chosen candidate, so votes are **not anonymous** inside the program.
* The system supports **one election** per run and cannot be reset without restarting the program.
* Admin and voter screens run in the same program, so it is a single-computer demonstration, not a networked system.
* Not tested at scale and has no automated tests.

## Future Enhancements

These are ideas only; none of them exist yet.

* Save data in a database (for example MySQL with JDBC) or in files so it survives restarts.
* Hash passwords and move admin credentials out of the source code.
* Verify voter eligibility with date of birth and an identity number.
* Separate vote records from voter identity to improve ballot secrecy.
* Support multiple elections and an option to reset or archive an election.
* Add a refresh button on the results page, result export, and audit logs.
* Add automated tests and a client/server design for use on several computers.

## Academic Project Disclaimer

This project was created for **educational purposes** to practise OOPS in Java, event-driven GUI programming with AWT, and application design. It is an academic demonstration and **must not be used for real elections** or for any situation that requires security, privacy, auditing, or legal compliance.