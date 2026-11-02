
# Payment Process Guide

## 1. Payment Integration

Investra uses Razorpay Checkout for processing investment payments.

Payment Service is responsible for:
- Creating Razorpay payment orders.
- Storing payment records.
- Verifying payment signatures.
- Updating payment statuses.
- Handling refunds.
- Receiving Razorpay webhook events.

## 2. How to Make a Payment

1. Select a mutual fund.
2. Enter the investment amount.
3. Continue with investment order creation.
4. Order Service requests Payment Service to create a payment.
5. Payment Service creates a Razorpay order in INR.
6. The frontend opens Razorpay Checkout.
7. Select an available payment method in Razorpay Checkout.
8. Complete the payment.
9. Razorpay returns payment details.
10. Payment Service verifies the payment signature on the backend.
11. Once verified, the payment status is updated.
12. Order Service is notified about the payment result.
13. Investment processing begins after successful payment handling.

Available payment methods depend on the Razorpay Checkout configuration and account eligibility. Do not promise a specific method without verifying its availability.

## 3. Payment Verification

Payment Service verifies the Razorpay payment using:
- Razorpay order ID.
- Razorpay payment ID.
- Razorpay signature.
- The configured Razorpay secret key.

The signature must be verified on the server.

The chatbot must never ask users to share:
- Card numbers.
- CVV.
- UPI PIN.
- Banking passwords.
- Razorpay secret keys.
- Authentication tokens.

## 4. Payment Status

CREATED:
A payment record has been created.

VERIFIED:
The payment signature has been verified.

PAID:
Payment has been marked as successfully paid where applicable in the payment flow.

CAPTURED:
Razorpay has reported a captured payment.

FAILED:
The payment failed.

REFUND_PENDING:
A refund has been initiated and is awaiting completion.

Exact status transitions depend on the Payment Service implementation. Do not assume that every payment follows every status.

## 5. Payment and Order Status Difference

Payment status and investment order status are separate.

For example:

Payment status: VERIFIED

Order status: PAID

A successful payment does not necessarily mean that investment processing is complete.

The Order Service background worker must process the investment before the order reaches COMPLETED.

## 6. Payment Troubleshooting

### Payment failed
Check the payment status in the application. Do not retry blindly if the result is uncertain.

### Money deducted but order is not completed
Payment verification and investment processing are separate. Check My Orders and allow the processing workflow to update the status.

### Payment still pending
Check the order and payment status. Contact support if it remains unresolved.

### Refund requested
A refund can be initiated through the supported refund process. Refund completion is not guaranteed immediately.

## 7. Payment APIs

POST /api/payments

POST /api/payments/verify

POST /api/payments/{id}/refund

GET /api/payments/order/{orderId}

GET /api/payments/user/{userId}

GET /api/payments/recheck/{orderId}

POST /api/payments/webhook

## 8. Chatbot Limitations

The chatbot cannot:
- Process payments.
- Verify a user's actual payment without backend data.
- Issue refunds.
- Confirm that money has been credited or refunded.
- Access Razorpay account credentials.

Users should use the application and its verified payment records for actual transaction information.
