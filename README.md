# Patient Management System

## Overview

**Patient Management** is a microservices-based system built with Spring Boot.  
It consists of three domain-driven services that communicate using both synchronous and asynchronous patterns.

The full infrastructure, application services, Kafka, and PostgreSQL is containerized and orchestrated using **Docker Compose**.

This project is **not focused on the Domain**, but on **how to work with Microservices Kafka, gRPC and Docker**.

---

## Architecture

The system is composed of three microservices:

- **patient-service**
- **billing-service**
- **analytics-service**

### Communication Model

#### gRPC (Synchronous Communication)

- `patient-service` → `billing-service`
- Used for real-time billing operations
- Low-latency internal RPC communication

#### Kafka (Asynchronous Communication)

- `patient-service` → `analytics-service`
- Event-driven architecture
- Loose coupling between services

---

## Technology Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Apache Kafka
- gRPC
- Maven
- Docker & Docker Compose

---

## Microservices Description

### 1. patient-service

**Responsibilities:**
- Manages patient records
- Persists data using JPA + Hibernate
- Publishes domain events to Kafka
- Calls billing-service via gRPC

**Dependencies:**
- PostgreSQL
- Kafka
- billing-service (gRPC)

---

### 2. billing-service

**Responsibilities:**
- Handles billing logic
- Exposes gRPC endpoints
- Persists billing data

**Dependencies:**
- PostgreSQL

---

### 3. analytics-service

**Responsibilities:**
- Consumes patient-related events from Kafka
- Processes analytical logic asynchronously

**Dependencies:**
- Kafka

---

## Database

The system uses **PostgreSQL** as its relational database.

- Managed via Docker
- Configured through environment variables
- Hibernate schema management enabled (`ddl-auto=update`)

---

## Event Flow (Kafka)

1. A patient operation occurs in `patient-service`.
2. An event is published to Kafka.
3. `analytics-service` consumes the event.
4. Analytical processing is executed asynchronously.

This ensures scalability and loose coupling.

---

## gRPC Flow

1. `patient-service` requires billing processing.
2. A gRPC request is sent to `billing-service`.
3. `billing-service` processes the request and returns a response.

This ensures efficient internal service communication.

---

## Running the System

### Prerequisites

- Docker
- Docker Compose

### Build and Start All Services

From the root directory:

```bash
docker compose up --build
