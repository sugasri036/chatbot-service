
# Registration and Login Guide

## 1. User Registration

Investra allows new users to create an account using their name, email address and password.

### Registration Process

1. Open the Investra application.
2. Navigate to the registration page.
3. Enter your name.
4. Enter your email address.
5. Create a password.
6. Confirm your password.
7. Submit the registration form.
8. The Auth Service validates the submitted information.
9. The email is checked for duplicates.
10. The password is securely hashed before storage.
11. The user account is saved in PostgreSQL.
12. The application returns authentication information, including a JWT.

### Registration API

POST /api/auth/register

## 2. Login

Existing users can log in using their registered email address and password.

### Login Process

1. Open the login page.
2. Enter your registered email address.
3. Enter your password.
4. Submit the login form.
5. Auth Service validates the credentials.
6. If valid, a JWT authentication token is generated.
7. The user can access authenticated application features.

### Login API

POST /api/auth/login

## 3. Google Login

Investra also supports Google OAuth2 authentication.

1. Select the Google login option.
2. Authenticate using your Google account.
3. Google returns the authenticated user information.
4. Investra identifies an existing account or creates a new one.
5. Investra generates its own JWT.
6. The user is redirected to the frontend.

The application supports LOCAL and GOOGLE authentication providers.

## 4. Forgot Password

Users who forget their password can use the OTP-based password recovery process.

### Password Recovery Process

1. Open the forgot-password page.
2. Enter the registered email address.
3. Submit the request.
4. Auth Service requests an OTP through OTP Service.
5. The user receives the OTP through the configured delivery mechanism.
6. Enter the OTP in the verification screen.
7. Submit the OTP for verification.
8. After successful verification, a password-reset session token is returned.
9. Enter and confirm the new password.
10. Submit the password-reset request.
11. Auth Service validates the reset session and updates the password.

### Relevant APIs

POST /api/auth/forgot-password

POST /api/auth/forgot-password/verify

POST /api/auth/forgot-password/reset

## 5. JWT Authentication

Investra uses JSON Web Tokens for authentication.

- Signing algorithm: HMAC-SHA256.
- Subject: User email.
- Custom claim: userId.
- Configured expiry: 24 hours.
- Signing secret: JWT_SECRET environment variable.

Protected endpoints require valid authentication.

## 6. Troubleshooting

### Invalid login credentials
Check that the email and password are correct.

### Email already registered
Try logging in instead of creating another account.

### Forgot-password OTP not received
Check the registered email and try again. If the problem continues, contact application support.

### Session expired
Log in again to obtain a valid authentication token.

## 7. Chatbot Limitations

The chatbot can explain registration, login and password recovery.

It cannot:
- Retrieve a user's password.
- Generate or reveal an OTP.
- Reset a user's password itself.
- Access private authentication records.
- Bypass authentication requirements.
