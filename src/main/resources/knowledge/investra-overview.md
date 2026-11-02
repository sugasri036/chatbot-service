
# Investra - Application Overview

## 1. What is Investra?

Investra is a mutual fund investment application that allows users to explore mutual funds, view fund information, understand NAV movements, make investments, complete payments and track their investments.

The application uses a React frontend and Spring Boot microservices for backend operations.

## 2. Main Features

- User registration and login.
- Google authentication.
- OTP-based forgot-password functionality.
- Mutual fund discovery and search.
- Fund category filtering and sorting.
- Fund details and NAV information.
- Historical NAV charts.
- Investment order creation.
- Razorpay payment integration.
- Order tracking.
- Portfolio and investment performance overview.

## 3. Application Architecture

The application consists of the following services:

### Auth Service
Responsible for registration, login, Google authentication, JWT generation, and password reset.

### OTP Service
Responsible for OTP generation and verification during password recovery.

### Fund Service
Manages mutual fund information, current NAV, historical NAV and fund-related data.

### Order Service
Creates investment orders, tracks order statuses and processes paid investment orders through background jobs.

### Payment Service
Integrates Razorpay, creates payment orders, verifies payment signatures and handles refunds.

### API Gateway
Provides a common entry point for frontend requests to backend microservices.

### Chatbot Service
Provides AI-powered assistance using Ollama and the Qwen 2.5 3B model.

## 4. Important Financial Terms

### Mutual Fund
An investment product that pools money from investors and invests it in securities.

### NAV
Net Asset Value is the per-unit value of a mutual fund.

### SIP
Systematic Investment Plan is a method of investing a fixed amount periodically in a mutual fund.

### Units
Units represent the quantity of mutual fund holdings allocated to an investor.

## 5. Important Limitations

- The chatbot does not automatically have access to user accounts.
- It cannot independently retrieve live portfolio values or transaction statuses.
- It cannot execute investments, payments, SIPs or redemptions.
- It must not invent fund information or transaction details.
- Users must use the actual Investra application for transactions.
