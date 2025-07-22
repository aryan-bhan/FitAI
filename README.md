# 🏋️‍♂️ AI-Driven Fitness Microservices Platform

A **scalable, secure fitness management platform** built using **Microservices Architecture**, enabling **AI-powered personalized recommendations** based on user activities. Integrated with **Keycloak** for robust authentication and **RabbitMQ** for asynchronous processing.

---

## 🚀 Features

✅ Track user fitness activities  
✅ Generate **AI-based personalized recommendations** based on user progress  
✅ **Role-based authentication and authorization** using **Keycloak**  
✅ **Service discovery** using **Eureka Server**  
✅ **Asynchronous event-driven workflows** with **RabbitMQ**  
✅ Clean separation of concerns using **Microservices**

---

## 🛠️ Tech Stack

- **Backend:** Java 24, Spring Boot, Spring Web, Spring Data JPA
- **Service Discovery:** Spring Cloud Netflix Eureka
- **Communication:**
  - REST APIs using `WebClient` with LoadBalancer
  - Event-driven messaging with **RabbitMQ**
- **Database:** PostgreSQL
- **Authentication:** **Keycloak**
- **AI:** Embedded AI models for generating personalized recommendations
- **Build Tool:** Maven
- **Others:** Lombok, Hibernate, HikariCP

---

## 🗂️ Microservices Structure

### **1️⃣ User Service**
- Manages user registration, validation, and profile data.
- Communicates securely using **Keycloak tokens**.

### **2️⃣ Activity Service**
- Tracks user activities (e.g., workouts, steps).
- Calls embedded **AI models** to generate personalized recommendations.
- Publishes and consumes events via **RabbitMQ** for decoupled workflows.

### **3️⃣ Eureka Server**
- Provides **service discovery** and health monitoring of microservices.

### **4️⃣ Authentication Layer**
- Utilizes **Keycloak** for:
  - User management
  - Role-based authentication
  - Token issuance and validation.

---

## 🧩 Architecture

- **Microservices:** Independently deployable, scalable services.
- **REST APIs:** For synchronous inter-service calls.
- **RabbitMQ:** For asynchronous message-driven communication.
- **PostgreSQL:** Dedicated database per service for clear separation of data.
- **AI Layer:** Triggers AI-based recommendation logic on activity ingestion.
- **Keycloak:** Secures APIs with OAuth2 / JWT.

---

## 🖥️ Running the Project

### Prerequisites:
✅ Java 24  
✅ Maven  
✅ PostgreSQL  
✅ RabbitMQ  
✅ Keycloak  
✅ Eureka Server

---

### 1️⃣ Clone the repository:
```bash
git clone https://github.com/aryan-bhan/FitAI.git
cd fitness-microservice
```
---

### 2️⃣ Start supporting services:
✅ Start PostgreSQL and create databases per service.  
✅ Start RabbitMQ.  
✅ Start Keycloak and configure realms, clients, and users.  
✅ Start Eureka Server.

---

### 3️⃣ Build and Run Services:
```bash
cd userservice
mvn clean install
mvn spring-boot:run

cd ../activityservice
mvn clean install
mvn spring-boot:run

# Repeat for all services
```
---

### 4️⃣ Environment Variables:
Set **GEMINI_API_URL** and **GEMINI_API_KEY** as the environment variables for aiservice.

---

### 5️⃣ Access:
Eureka Dashboard: http://localhost:8761  
Keycloak Admin Console: http://localhost:8080 (default)



