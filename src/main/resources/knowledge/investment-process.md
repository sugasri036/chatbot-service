
# Investment Process Guide

## 1. Overview

Investra allows authenticated users to select a mutual fund, enter an investment amount and initiate an investment order.

The current frontend implements an amount-based investment flow.

Do not claim that recurring SIP registration is available unless it is implemented separately.

## 2. How to Invest

1. Log in to Investra.
2. Navigate to the mutual fund discovery page.
3. Browse or search for a mutual fund.
4. Select the fund you want to explore.
5. Review its NAV, minimum investment, returns and risk information.
6. Select the Invest Now option.
7. Enter the investment amount.
8. Select the available investment horizon.
9. Review the investment details.
10. Continue to create the investment order.
11. Complete the payment through Razorpay Checkout.
12. Wait for payment verification and investment processing.
13. Check the order status in My Orders.
14. Once processing is completed, check the investment in My Portfolio.

## 3. Order Creation

Order Service handles investment order creation.

When the frontend submits investment details:

1. Order Service validates the user ID, fund ID and amount.
2. A new order is created with PENDING status.
3. Order Service requests Payment Service to create a payment order.
4. The payment and Razorpay order identifiers are saved.
5. The investment order moves to PAYMENT_CREATED when payment creation succeeds.

## 4. Investment Unit Calculation

The background worker retrieves fund information and NAV from Fund Service.

The calculation is:

Units = Investment Amount / NAV

Example:

Investment amount: ₹5,000

NAV: ₹50

Calculated units: 100

The actual investment processing and allotment may depend on external fund-house processes and applicable rules. The chatbot must not claim that an internal calculation guarantees actual mutual fund allotment.

## 5. Important Investment Limitations

- Enter an amount that meets the minimum investment requirement displayed for the selected fund.
- Review the fund details and risks before investing.
- Payment success and investment completion are separate stages.
- A PAID order is not necessarily a COMPLETED order.
- Do not repeat a payment merely because the order is still processing.
- Check My Orders before taking further action.

## 6. SIP Availability

SIP means Systematic Investment Plan.

The currently inspected frontend implements an amount-based investment process. A recurring SIP setup workflow has not been verified.

If a user asks how to create an SIP, explain what SIP means and clarify that recurring SIP creation is not currently confirmed as an implemented Investra feature.

## 7. Chatbot Responsibilities

The chatbot can explain the investment process and help users understand order statuses.

It cannot:
- Place an investment order.
- Initiate a payment.
- Choose a fund on behalf of a user.
- Guarantee unit allotment.
- Confirm an investment without actual order data.
