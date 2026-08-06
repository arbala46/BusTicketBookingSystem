# Smart Bus Ticket Booking System

A production-inspired Bus Ticket Booking System developed using Java and Spring Boot. This project is designed to simulate how real-world online bus reservation platforms work while following clean architecture and REST API best practices.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Lombok
- Postman
- Git & GitHub

---

## Features Implemented

### Authentication
- User Registration
- User Login
- JWT Token Generation
- JWT Authentication Filter
- Protected APIs

### Trip Management
- Search buses by
  - Source
  - Destination
  - Journey Date
- View available trips

### Seat Layout
- View complete seat layout
- Seat status
  - Available
  - Booked
- Seat Position
  - Left Window
  - Left Middle
  - Left Aisle
  - Right Aisle
  - Right Middle
  - Right Window

### Ticket Booking
- Book multiple passengers in a single booking
- Duplicate seat validation
- Trip validation
- Trip Seat validation
- Seat availability validation
- Passenger details mapping
- Booking confirmation
- Available seat count update
- Dummy payment simulation
- Transaction management using @Transactional

---

## Database Design

Entities implemented:

- User
- Bus
- Seat
- Trip
- TripSeat
- Booking
- BookingPassenger

Relationships

- One Bus → Many Trips
- One Trip → Many TripSeats
- One Trip → Many Bookings
- One User → Many Bookings
- One Booking → Many BookingPassengers
- One BookingPassenger → One TripSeat

---

## REST APIs

### Authentication
- Register
- Login

### Trips
- Search Trips
- View Seat Layout

### Booking
- Book Tickets

---

## Features Planned

- Gender-based seat allocation
- Booking cancellation
- Booking history
- Seat locking mechanism
- Race condition handling
- Email notification(Gmail SMTP)
- Admin module
- React Frontend
- AWS Deployment

---

## Project Goal

The objective of this project is to build a production-inspired backend application while strengthening knowledge in:

- Spring Boot
- REST APIs
- Spring Security
- JWT Authentication
- Hibernate & JPA
- Database Design
- Exception Handling
- Transaction Management
- Clean Code Practices
- Git & GitHub

---

## Author

**Bala A.R.**

Java Backend Developer

**Bala A.R.**

Java Backend Developer
