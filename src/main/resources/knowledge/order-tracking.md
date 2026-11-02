
# Order Tracking Guide

## 1. Overview

Order Service manages investment orders created by users in Investra.

It is responsible for:
- Creating investment orders.
- Initiating payment creation.
- Updating payment-related order statuses.
- Managing background investment-processing jobs.
- Calculating investment units.
- Storing completed investment details.

## 2. How to Track an Order

1. Log in to Investra.
2. Navigate to My Orders.
3. View your available investment orders.
4. Use the available status filters to find an order.
5. Open an order to inspect its details.
6. Review its current status.
7. Once completed, check the corresponding investment in My Portfolio.

## 3. Order Statuses

### PENDING

The investment order has been created.

The payment process has not yet reached the payment-created stage.

### PAYMENT_CREATED

A Razorpay payment order has been created and its identifiers have been associated with the investment order.

The user must complete the payment process.

### PAID

Payment has been successfully verified and the investment order has been marked as paid.

Investment processing may still be pending.

### COMPLETED

The background worker has successfully processed the investment.

The completed order contains the processed fund and investment information, including NAV and calculated units.

### PAYMENT_FAILED

The payment-order creation or payment process has failed.

The user should check the actual payment and order records before attempting another transaction.

## 4. Background Job Processing

After successful payment handling, Order Service creates a background processing job.

The job worker:
1. Finds pending jobs.
2. Claims a job for processing.
3. Retrieves the associated order.
4. Fetches fund information from Fund Service.
5. Retrieves the relevant NAV.
6. Calculates investment units.
7. Updates the investment order.
8. Marks the job as completed.

The worker is configured to poll for jobs every five seconds by default.

Failed jobs can be retried up to five attempts.

Jobs stuck in PROCESSING for more than ten minutes can be reset for recovery.

## 5. Job Statuses

PENDING:
The job is waiting to be processed.

PROCESSING:
A worker is processing the job.

COMPLETED:
The job has completed successfully.

FAILED:
The job has exhausted its retry attempts.

Job status and investment order status are different concepts.

## 6. Common Questions

### Why is my order showing PAYMENT_CREATED?

A Razorpay payment order has been created, but the investment order has not yet been marked as paid.

### Why is my order showing PAID?

Payment has been verified, but the background investment-processing job may still be running.

### Why is my order not COMPLETED?

The worker may still be processing it, or the job may have encountered an error.

### Why is my order showing PAYMENT_FAILED?

The payment process failed. Check the payment record and transaction result before retrying.

### When will my investment appear in My Portfolio?

The current portfolio implementation includes completed investment orders. A PAID order may not yet appear as a completed holding.

## 7. Order API

GET /api/orders/user/{userId}

The endpoint returns orders associated with the authenticated user after checking the requested user ID.

## 8. Important Safety Rules

- Never invent an order ID.
- Never invent a transaction status.
- Never claim that a payment succeeded without verified payment data.
- Never claim that an investment is completed when its status is PAID.
- Never expose another user's orders.
- Do not recommend repeating payments without checking the actual transaction status.

The chatbot can explain order statuses but cannot retrieve private order records without an authenticated backend integration.
