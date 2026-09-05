# Election Voting Mechanism System

A Java-based **Election Voting Mechanism System** developed as an Object-Oriented Programming (OOP) project. The system provides voter registration, authentication, candidate management, voting, vote counting, and result generation for a single election.

The project uses **Java AWT (Abstract Window Toolkit)** to create the graphical user interface and demonstrates core Object-Oriented Programming concepts through the system's different modules.

> **Note:** This project is an academic prototype and is not intended for use in real-world government elections.

## 📌 Project Overview

The Election Voting Mechanism System provides separate functionality for two types of users:

* **Admin** – manages candidates, controls the election, and views results.
* **Voter** – registers, logs in, verifies eligibility, views candidates, and casts a vote.

The system also prevents a registered voter from casting more than one vote.

## ✨ Features

### 👨‍💼 Admin Module

* Admin login
* Add candidates
* View candidates
* Remove candidates
* Start election
* End election
* View election results
* Determine the winner

### 🗳️ Voter Module

* Voter registration
* Voter login
* Basic eligibility verification
* View candidate list
* Cast vote
* Check voting status
* One-vote-per-voter restriction
* Logout

### 📊 Voting System

* Election status management
* Candidate validation
* Vote validation
* Vote counting
* Result generation
* Winner identification
* Exception handling and input validation

---

## 🖥️ GUI Development Using Java AWT

The graphical user interface of the project is developed using **Java AWT (Abstract Window Toolkit)**.

AWT provides the components and event-handling mechanisms required to create the application's windows, forms, buttons, and user interactions.

### AWT Components Used

The project can use the following AWT components:

| AWT Component | Purpose                                           |
| ------------- | ------------------------------------------------- |
| `Frame`       | Creates application windows                       |
| `Panel`       | Groups related GUI components                     |
| `Label`       | Displays text and headings                        |
| `TextField`   | Accepts user input                                |
| `Button`      | Performs actions such as Login, Submit, Add, etc. |
| `Choice`      | Provides dropdown selections                      |
| `Checkbox`    | Provides selectable options                       |
| `Dialog`      | Displays messages or confirmations                |
| `Font`        | Customizes text appearance                        |

### AWT Event Handling

The application uses Java's event-handling mechanism to respond to user actions.

Examples include:

* `ActionListener` – handles button clicks
* `WindowListener` / `WindowAdapter` – handles window events
* `ActionEvent` – identifies actions performed by the user

For example, when a user clicks the **Login** button, an `ActionListener` processes the entered username and password and performs the appropriate login operation.

### Example AWT Components

```java
Frame frame;
Label usernameLabel;
Label passwordLabel;
TextField usernameField;
TextField passwordField;
Button loginButton;
Button clearButton;
```

A password field can hide the entered characters using:

```java
passwordField.setEchoChar('*');
```

---

## 🧩 OOP Concepts Demonstrated

The project demonstrates the following Java OOP concepts:

* **Classes and Objects**
* **Encapsulation**
* **Inheritance**
* **Polymorphism**
* **Abstraction**
* **Constructors**
* **Methods**
* **Exception Handling**

### Example Class Structure

```text
                 User
                /    \
               /      \
           Admin      Voter

        Candidate
           │
           ▼
          Vote
           │
           ▼
         Result

      VotingSystem
```

Possible classes include:

```text
User.java
Admin.java
Voter.java
Candidate.java
Vote.java
Result.java
VotingSystem.java
LoginPage.java
```

---

## 🔄 System Workflow

```text
                         START
                           │
                           ▼
                      Main Menu
                     /          \
                    /            \
                Admin            Voter
                  │                │
                  ▼                ▼
             Admin Login    Registration/Login
                  │                │
                  ▼                ▼
          Admin Dashboard    Eligibility Check
                  │                │
          ┌───────┼───────┐        ▼
          │       │       │   View Candidates
          ▼       ▼       ▼        │
       Manage   Start    Results   ▼
     Candidates Election       Cast Vote
                                    │
                                    ▼
                              Vote Validation
                                    │
                                    ▼
                                Record Vote
                                    │
                                    ▼
                               Mark Voted
                                    │
                                    ▼
                                  Logout

                           Election Ends
                                │
                                ▼
                           Count Votes
                                │
                                ▼
                         Generate Results
                                │
                                ▼
                              Winner
```

---

## 📂 Project Structure

```text
Election-Voting-Mechanism-System/
│
├── src/
│   ├── User.java
│   ├── Admin.java
│   ├── Voter.java
│   ├── Candidate.java
│   ├── Vote.java
│   ├── Result.java
│   ├── VotingSystem.java
│   ├── LoginPage.java
│   └── Main.java
│
├── README.md
└── .gitignore
```

The exact file structure may vary depending on the final implementation.

---

## 🔐 Voting Validation

Before recording a vote, the system checks:

1. Whether the voter is registered.
2. Whether the voter is eligible.
3. Whether the election is active.
4. Whether the voter has already voted.
5. Whether the selected candidate exists.

If all conditions are satisfied:

```text
Valid Voter
     ↓
Eligible
     ↓
Election Active
     ↓
Not Already Voted
     ↓
Valid Candidate
     ↓
Record Vote
     ↓
Mark Voter as Voted
```

---

## 🛠️ Technologies Used

* **Java**
* **Java OOP**
* **Java AWT**
* **Java Event Handling**
* **Java Collections**
* **Exception Handling**
* **MySQL + JDBC** *(if database connectivity is implemented)*

---

## 🎯 Project Objectives

* To develop a basic election voting system using Java.
* To demonstrate Object-Oriented Programming concepts.
* To develop a graphical user interface using Java AWT.
* To implement voter registration and authentication.
* To provide candidate management functionality.
* To implement one-vote-per-voter functionality.
* To implement vote counting and result generation.
* To handle invalid inputs and basic exceptions.

---

## ▶️ How to Run

### Prerequisites

Install Java on your system.

Check the Java version:

```bash
java -version
```

Check the Java compiler:

```bash
javac -version
```

### Compile

Navigate to the source directory:

```bash
cd src
```

Compile the Java files:

```bash
javac *.java
```

### Run

Run the main class:

```bash
java Main
```

If the application starts from another class, run that class instead.

---

## 📊 Expected Output

The system provides different functionality based on the user's role.

### Admin

```text
Admin Login
     ↓
Admin Dashboard
     ├── Add Candidate
     ├── View Candidates
     ├── Remove Candidate
     ├── Start Election
     ├── End Election
     └── View Results
```

### Voter

```text
Voter Registration/Login
          ↓
   Eligibility Check
          ↓
   View Candidates
          ↓
      Cast Vote
          ↓
    Voting Status
```

After the election ends, the system counts the votes and displays the results and winning candidate.

---

## 🚧 Limitations

* The system supports **one election only**.
* This is an academic prototype.
* It does not implement real-world election-grade security.
* No biometric authentication is implemented.
* No blockchain technology is used.
* No AI/ML-based verification is implemented.
* The system is not intended to replace an actual government election system.

---

## 🔮 Future Scope

The system can be extended with:

* Multiple election support
* Improved authentication
* Password hashing
* Enhanced database integration
* Digital voter verification
* Election analytics
* Advanced GUI design
* Role-based access control
* Audit logs
* Online deployment

---

## 📚 Learning Outcomes

This project provides practical experience with:

* Java programming
* Object-Oriented Programming
* Inheritance and polymorphism
* Encapsulation and abstraction
* Java AWT GUI development
* AWT event handling
* Java Collections
* Exception handling
* Basic database connectivity
* Application workflow design

---

## 📜 License

This project is developed for **educational and academic purposes**.
