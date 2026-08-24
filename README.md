# 🧠 MindSpace – AI-Powered Mental Health & Emergency Care System

A full-stack **Mental Health & Emergency Care System** developed using **Java, Spring Boot, Spring Data JPA, Hibernate, MySQL, HTML, CSS, and JavaScript**.

The platform provides students with mental-health support features such as mood tracking, screening, AI assistance, counseling appointments, doctor communication, emergency contacts, and SOS alerts.

---

## 🚀 Features

### 👨‍🎓 Student Module

* Student registration and login
* Personal dashboard
* Mood tracking
* Mental-health screening
* Gratitude journal
* AI-powered chat assistance
* Community/forum support
* Counselor appointment booking
* Doctor communication
* Digital prescriptions
* Emergency contact management
* SOS emergency alert

### 👨‍⚕️ Doctor Module

* Doctor dashboard
* View assigned students
* Student information management
* Appointment management
* Patient communication
* Digital prescription management

### 🧑‍💼 Counselor Module

* Counselor dashboard
* View appointments
* Manage counseling sessions
* Student communication
* Appointment management

### 👨‍💻 Admin Module

* Admin dashboard
* Student management
* Doctor management
* Counselor management
* System monitoring
* Application analytics

### 🤖 AI Features

* AI-powered mental-health assistant
* AI-based conversational support
* Crisis-related analysis
* AI integration using Groq API

### 🚨 Emergency Support

* SOS emergency system
* Emergency contact management
* Email notifications
* SMS notifications using Twilio
* Crisis alert workflow

---

## 🛠️ Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Hibernate
* Spring Security
* REST APIs
* Maven

### Database

* MySQL

### Frontend

* HTML5
* CSS3
* JavaScript
* Bootstrap
* Chart.js
* Font Awesome

### Integrations

* Groq AI
* Spring AI
* Twilio SMS
* JavaMail
* Swagger / OpenAPI

---

## 🏗️ Project Architecture

```text
                         ┌─────────────────────┐
                         │     Frontend        │
                         │ HTML / CSS / JS     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    REST APIs        │
                         │  Spring Boot        │
                         └──────────┬──────────┘
                                    │
                 ┌──────────────────┼──────────────────┐
                 ▼                  ▼                  ▼
          ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
          │ Controllers │    │  Services   │    │ Repositories│
          └─────────────┘    └─────────────┘    └──────┬──────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │      MySQL      │
                                              └─────────────────┘

                 External Integrations
                 ──────────────────────

                 Spring AI / Groq
                        │
                        ▼
                   AI Assistant

                 Twilio
                        │
                        ▼
                    SMS Alerts

                 JavaMail
                        │
                        ▼
                   Email Alerts
```

---

## 📂 Project Structure

```text
MentalHealthApplication/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ...
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🗄️ Database

The application uses **MySQL** for persistent data storage.

Major entities/modules include:

* Students
* Doctors
* Counselors
* Appointments
* Mood Records
* Mental-Health Screening
* Gratitude Journal
* Forum
* Emergency Contacts
* Prescriptions
* SOS Alerts
* User Communication

Spring Data JPA and Hibernate are used for database interaction and ORM.

---

## 🔐 Configuration

For security, API keys and passwords should **not** be stored directly in GitHub.

Configure sensitive values using environment variables.

Example:

```properties
# Database
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Twilio
twilio.account.sid=${TWILIO_ACCOUNT_SID}
twilio.auth.token=${TWILIO_AUTH_TOKEN}
twilio.phone.number=${TWILIO_PHONE_NUMBER}

# Groq
spring.ai.openai.api-key=${GROQ_API_KEY}

# Email
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

Create your own environment variables locally.

**Never commit API keys, passwords, tokens, or `.env` files to GitHub.**

---

## ⚙️ How to Run the Project

### 1. Clone the repository

```bash
git clone https://github.com/Adityaraut1423/AI-Powered-MHHS.git
```

### 2. Open the project

Open the project in:

* Eclipse
* IntelliJ IDEA
* VS Code

### 3. Configure MySQL

Create the database:

```sql
CREATE DATABASE mental_health_db;
```

Update your database environment variables:

```text
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
```

### 4. Configure API credentials

Set the required environment variables:

```text
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
TWILIO_PHONE_NUMBER=your_twilio_number

GROQ_API_KEY=your_groq_api_key

MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password
```

### 5. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application from Eclipse/IntelliJ.

### 6. Open the application

```text
http://localhost:8080
```

---

## 📡 API Documentation

The project uses **Swagger / OpenAPI** for API documentation.

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger provides an interactive interface for exploring and testing the REST APIs.

---

## 🔒 Security

The project includes security-related functionality such as:

* Password hashing using BCrypt
* Spring Security integration
* Role-based application modules
* Environment-based configuration for API credentials
* Protected application data

> **Note:** This project is intended for educational and portfolio purposes and should undergo additional security hardening before production use.

---

## 🤖 AI Integration

The application integrates AI functionality through **Spring AI and Groq's API-compatible interface**.

The AI module can provide conversational support and assist with mental-health-related interactions.

AI-generated responses are intended as **supportive assistance and not as a replacement for qualified medical or mental-health professionals**.

---

## 🚨 Emergency System

The emergency module provides an SOS workflow that can:

```text
Student
   │
   ▼
SOS Request
   │
   ▼
Crisis Analysis
   │
   ├───────────────┐
   ▼               ▼
Email Alert     SMS Alert
   │               │
   ▼               ▼
Emergency       Emergency
Contact         Contact
```

The system uses:

* Twilio for SMS
* JavaMail for email
* Emergency contact information
* Crisis analysis workflow

---

## 📊 Dashboard & Analytics

The application provides dashboard functionality for different user roles.

### Student

* Mood information
* Screening results
* Appointments
* AI assistance
* Emergency support

### Doctor

* Student information
* Appointments
* Prescriptions
* Communication

### Counselor

* Counseling appointments
* Student interactions
* Session management

### Admin

* User management
* System overview
* Application statistics

---

## 📸 Screenshots

Add screenshots of the application here.

### Home Page

```text
Add your screenshot here
```

### Student Dashboard

```text
Add your screenshot here
```

### AI Chat

```text
Add your screenshot here
```

### Doctor Dashboard

```text
Add your screenshot here
```

### Admin Dashboard

```text
Add your screenshot here
```

### Emergency / SOS System

```text
Add your screenshot here
```

---

## 🔮 Future Enhancements

* Implement complete JWT-based authentication
* Improve role-based authorization
* Add video counseling
* Add real-time chat using WebSocket
* Improve AI safety and crisis-response workflows
* Add advanced analytics
* Add Docker containerization
* Add CI/CD pipeline
* Deploy the application to cloud infrastructure
* Add automated unit and integration testing

---

## 🎯 Learning Outcomes

Through this project, I gained practical experience in:

* Java backend development
* Spring Boot application development
* REST API development
* Spring Data JPA
* Hibernate ORM
* MySQL database management
* Frontend development
* API integration
* AI integration
* Email and SMS integration
* MVC architecture
* Git and GitHub
* Application configuration
* Full-stack application development

---

## 👨‍💻 Author

**Aditya Raut**

Final-Year B.Tech IT Student
Aspiring Java Full Stack Developer

### Skills

```text
Java | Spring Boot | Spring Data JPA | Hibernate
REST APIs | MySQL | JavaScript | HTML | CSS
React.js | Git | GitHub | DSA
```

---

## ⭐ If You Like This Project

If you find this project useful or interesting, consider giving the repository a ⭐ on GitHub.

---

## ⚠️ Disclaimer

MindSpace is an academic/portfolio project designed to demonstrate software development and system-integration skills.

It is **not a medical diagnostic system** and should not be used as a substitute for professional medical or mental-health advice.
