
# Portfolio Guide

## 1. What is My Portfolio?

My Portfolio is the section of Investra where users can view their completed investment information and portfolio performance.

The current implementation calculates portfolio information from completed investment orders.

## 2. How to View Your Portfolio

1. Log in to Investra.
2. Navigate to My Portfolio.
3. Review your completed investments.
4. Inspect the available fund and investment information.
5. Review the portfolio value and profit or loss information displayed by the application.

## 3. Portfolio Calculation

The current frontend uses completed investment orders for its portfolio calculations.

Investment cost is derived from the completed investment amounts.

Calculated units are based on the investment amount divided by the NAV saved during investment processing.

Current investment value is calculated using the available current NAV and units.

Formula:

Current Value = Units × Current NAV

Profit or Loss = Current Value - Investment Cost

Example:

Investment cost = ₹5,000

Units = 100

Current NAV = ₹55

Current value = 100 × 55 = ₹5,500

Profit = ₹5,500 - ₹5,000 = ₹500

These values are illustrative.

Actual portfolio values depend on the latest available NAV and completed investment records.

## 4. Why Is My Order Missing From My Portfolio?

The current implementation includes completed investment orders in portfolio calculations.

An order with PENDING, PAYMENT_CREATED or PAID status is not necessarily included as a completed holding.

Check the order status in My Orders.

## 5. Understanding Profit and Loss

Positive profit or loss means the current calculated investment value is greater than the recorded investment cost.

Negative profit or loss means the current calculated investment value is lower than the recorded investment cost.

Market-linked investment values can change as NAV changes.

Past performance does not guarantee future results.

## 6. Important Limitations

The chatbot does not automatically have access to:
- The user's actual portfolio.
- The user's investment cost.
- Current portfolio valuation.
- Actual profit or loss.
- Personal holdings or units.

It can explain how portfolio calculations work but must not invent personal portfolio figures.

## 7. Portfolio Safety

Never ask a user to share their password, OTP, JWT, card information or banking credentials.

Private portfolio information must only be retrieved through authenticated and authorized backend APIs.

The chatbot must not expose another user's investment information.
