# Gym Management System - API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require authentication with role-based access control. Include the JWT token in the `Authorization` header:
```
Authorization: Bearer <your-token>
```

### Roles
- `ADMIN` - Full system access
- `BUSINESS_OWNER` - Manage their own businesses
- `BUSINESS_MANAGER` - Manage business operations
- `TRAINER` - Manage training programs
- `STAFF` - General staff operations
- `CLIENT` - Client-level access

---

## Table of Contents
1. [Authentication](#authentication-endpoints)
2. [Users](#user-endpoints)
3. [Businesses](#business-endpoints)
4. [Exercises](#exercise-endpoints)
5. [Training Plans](#training-plan-endpoints)
6. [Subscriptions](#subscription-endpoints)
7. [Membership Plans](#membership-plan-endpoints)
8. [Products](#product-endpoints)
9. [Orders](#order-endpoints)
10. [Dashboard](#dashboard)

---

## Authentication Endpoints

### 1. Sign In
**POST** `/authentication/sign-in`

**Access:** Public

**Request Body:**
```json
{
  "username": "john_doe",
  "passwordHash": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "CLIENT",
    "status": "ACTIVE"
  }
}
```

**Error Response (500):**
```json
"Invalid credentials"
```

---

### 2. Sign Up
**POST** `/authentication/sign-up`

**Access:** Public

**Request Body:**
```json
{
  "username": "new_user",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "passwordHash": "securepassword",
  "role": "CLIENT",
  "phone": "1234567890",
  "gender": "FEMALE",
  "status": "ACTIVE",
  "businesses": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001"
    }
  ]
}
```

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "username": "new_user",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "role": "CLIENT"
}
```

**Error Response (403 Forbidden):**
```json
"You cannot sign-up with a role other than client"
```

---

## User Endpoints

### 1. Get Users by Business
**GET** `/businesses/{businessId}/users`

**Access:** `ADMIN`, `BUSINESS_OWNER`, `BUSINESS_MANAGER`, `TRAINER`, `STAFF`, `CLIENT`

**Path Parameters:**
- `businessId` (UUID) - Business ID

**Query Parameters:**
- `userId` (UUID, optional) - Filter by specific user ID
- `searchQuery` (string, optional) - Search by name, email, or username
- `userRole` (int, optional) - Filter by role
- `limit` (int, default: 10) - Number of results
- `offset` (int, default: 0) - Pagination offset

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "CLIENT",
    "gender": "MALE",
    "phone": "1234567890",
    "enrollmentDate": "2024-01-15",
    "status": "ACTIVE",
    "profilePicUrl": "https://example.com/pic.jpg",
    "businesses": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "Downtown Gym",
        "type": "GYM",
        "address": "123 Main St",
        "isActive": true
      }
    ],
    "cDate": "2024-01-15T10:30:00",
    "mDate": "2024-01-15T10:30:00"
  }
]
```

**Error Response (403 Forbidden):**
```json
"Forbidden: You cannot access users from this business"
```

---

### 2. Get User by ID
**GET** `/users/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - User ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "role": "CLIENT",
  "businesses": [...]
}
```

**Error Response (404 Not Found):**
```json
"User not found"
```

---

### 3. Create User
**POST** `/users`

**Access:** `ADMIN`, `BUSINESS_OWNER`, `BUSINESS_MANAGER`, `TRAINER`, `STAFF`

**Request Body:**
```json
{
  "username": "trainer_mike",
  "firstName": "Mike",
  "lastName": "Johnson",
  "email": "mike@example.com",
  "passwordHash": "password123",
  "role": "TRAINER",
  "gender": "MALE",
  "phone": "5551234567",
  "status": "ACTIVE",
  "businesses": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001"
    }
  ]
}
```

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440100",
  "username": "trainer_mike",
  "firstName": "Mike",
  "lastName": "Johnson",
  "role": "TRAINER"
}
```

**Error Response (403 Forbidden):**
```json
"Forbidden: You cannot create user"
```

---

### 4. Update User
**PUT** `/users/{id}`

**Access:** `ADMIN`, `CLIENT` (own profile only)

**Path Parameters:**
- `id` (UUID) - User ID

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe Updated",
  "email": "john.updated@example.com",
  "phone": "9998887777"
}
```

**Success Response (200 OK):**
```json
"User updated successfully"
```

**Error Response (403 Forbidden):**
```json
"Forbidden: Cannot modify other users"
```

---

### 5. Delete User
**DELETE** `/users/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - User ID

**Success Response (200 OK):**
```json
"User deleted successfully"
```

**Error Response (403 Forbidden):**
```json
"Forbidden: Cannot delete other users"
```

---

## Business Endpoints

### 1. Get All Businesses
**GET** `/businesses`

**Access:** `ADMIN`

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Downtown Gym",
    "type": "GYM",
    "address": "123 Main St, City",
    "latitude": 40.712776,
    "longitude": -74.005974,
    "isActive": true,
    "cDate": "2024-01-01T10:00:00",
    "mDate": "2024-01-01T10:00:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "Fitness Store",
    "type": "SHOP",
    "address": "456 Oak Ave, City",
    "isActive": true
  }
]
```

---

### 2. Get Business by ID
**GET** `/businesses/{id}`

**Access:** `ADMIN`, `BUSINESS_OWNER`, `BUSINESS_MANAGER`, `TRAINER`, `STAFF`, `CLIENT`

**Path Parameters:**
- `id` (UUID) - Business ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Downtown Gym",
  "type": "GYM",
  "address": "123 Main St, City",
  "latitude": 40.712776,
  "longitude": -74.005974,
  "isActive": true,
  "cDate": "2024-01-01T10:00:00",
  "mDate": "2024-01-01T10:00:00"
}
```

**Error Response (403 Forbidden):**
```json
"Forbidden: You cannot view this business"
```

---

### 3. Create Business
**POST** `/businesses`

**Access:** `ADMIN`

**Request Body:**
```json
{
  "name": "New Fitness Center",
  "type": "GYM",
  "address": "789 Elm St, City",
  "latitude": 40.730610,
  "longitude": -73.935242,
  "isActive": true
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "name": "New Fitness Center",
  "type": "GYM",
  "address": "789 Elm St, City",
  "isActive": true
}
```

---

### 4. Update Business
**PUT** `/businesses/{id}`

**Access:** `ADMIN`, `BUSINESS_OWNER`

**Path Parameters:**
- `id` (UUID) - Business ID

**Request Body:**
```json
{
  "name": "Updated Gym Name",
  "address": "123 New Address",
  "isActive": true
}
```

**Success Response (200 OK):**
```json
"Business updated successfully"
```

**Error Response (403 Forbidden):**
```json
"Forbidden: Cannot modify this business"
```

---

### 5. Delete Business
**DELETE** `/businesses/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Business ID

**Success Response (200 OK):**
```json
"Business deleted successfully"
```

---

## Exercise Endpoints

### 1. Get All Exercises
**GET** `/exercises`

**Access:** `ADMIN`

**Query Parameters:**
- `userId` (UUID, optional) - Filter by user
- `trainingPlanId` (UUID, optional) - Filter by training plan
- `offset` (int, default: 0) - Pagination offset
- `limit` (int, default: 10) - Number of results

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440010",
    "title": "Bench Press",
    "description": "Chest exercise with barbell",
    "cDate": "2024-01-01T10:00:00",
    "mDate": "2024-01-01T10:00:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440011",
    "title": "Squat",
    "description": "Leg exercise with barbell"
  }
]
```

---

### 2. Get Exercise by ID
**GET** `/exercises/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Exercise ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440010",
  "title": "Bench Press",
  "description": "Chest exercise with barbell",
  "cDate": "2024-01-01T10:00:00",
  "mDate": "2024-01-01T10:00:00"
}
```

**Error Response (404 Not Found):**
```json
"Exercise not found"
```

---

### 3. Get Training Plan Exercises
**GET** `/exercises/users/{userId}/training-plans/{trainingPlanId}/exercises`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `trainingPlanId` (UUID) - Training Plan ID

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440020",
    "trainingPlanId": "550e8400-e29b-41d4-a716-446655440015",
    "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
    "exerciseOrder": 1,
    "notes": "Focus on form, controlled movement",
    "exercise": {
      "id": "550e8400-e29b-41d4-a716-446655440010",
      "title": "Bench Press",
      "description": "Chest exercise with barbell"
    },
    "prescribedSets": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440030",
        "trainingPlanExerciseId": "550e8400-e29b-41d4-a716-446655440020",
        "setNumber": 1,
        "prescribedReps": 10,
        "prescribedWeight": 60.00,
        "restSeconds": 90,
        "notes": "Warm-up set"
      },
      {
        "setNumber": 2,
        "prescribedReps": 8,
        "prescribedWeight": 70.00,
        "restSeconds": 120,
        "notes": "Increase weight"
      },
      {
        "setNumber": 3,
        "prescribedReps": 6,
        "prescribedWeight": 80.00,
        "restSeconds": 180,
        "notes": "Working set"
      }
    ],
    "recentProgress": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440040",
        "set": 1,
        "reps": 10,
        "weight": 60.00,
        "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
        "trainingPlanId": "550e8400-e29b-41d4-a716-446655440015",
        "cDate": "2024-04-05T14:30:00"
      },
      {
        "set": 2,
        "reps": 8,
        "weight": 70.00,
        "cDate": "2024-04-05T14:35:00"
      }
    ],
    "cDate": "2024-01-10T10:00:00",
    "mDate": "2024-01-10T10:00:00"
  }
]
```

---

### 4. Get Training Plan Exercise by ID
**GET** `/exercises/users/{userId}/training-plans/{trainingPlanId}/exercises/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `trainingPlanId` (UUID) - Training Plan ID
- `id` (UUID) - Training Plan Exercise ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440020",
  "trainingPlanId": "550e8400-e29b-41d4-a716-446655440015",
  "exerciseId": "550e8400-e29b-41d4-a716-446655440010",
  "exerciseOrder": 1,
  "notes": "Focus on form",
  "exercise": {...},
  "prescribedSets": [...],
  "recentProgress": [...]
}
```

---

### 5. Create Exercise
**POST** `/exercises`

**Access:** `ADMIN`

**Request Body:**
```json
{
  "title": "Deadlift",
  "description": "Compound lower body and back exercise"
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "title": "Deadlift",
  "description": "Compound lower body and back exercise"
}
```

---

### 6. Update Exercise
**PUT** `/exercises/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Exercise ID

**Request Body:**
```json
{
  "title": "Deadlift (Conventional)",
  "description": "Updated description"
}
```

**Success Response (200 OK):**
```json
"Exercise updated successfully"
```

---

### 7. Delete Exercise
**DELETE** `/exercises/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Exercise ID

**Success Response (200 OK):**
```json
"Exercise deleted successfully"
```

---

## Training Plan Endpoints

### 1. Get Training Plans by User
**GET** `/training-plans/users/{userId}/training-plans`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID

**Query Parameters:**
- `offset` (int, default: 0) - Pagination offset
- `limit` (int, default: 10) - Number of results

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440015",
    "title": "Beginner Strength Program",
    "description": "3-day full body routine for beginners",
    "cDate": "2024-01-01T10:00:00",
    "mDate": "2024-01-01T10:00:00"
  }
]
```

---

### 2. Get Training Plan by ID
**GET** `/training-plans/users/{userId}/training-plans/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `id` (UUID) - Training Plan ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440015",
  "title": "Beginner Strength Program",
  "description": "3-day full body routine for beginners",
  "cDate": "2024-01-01T10:00:00",
  "mDate": "2024-01-01T10:00:00"
}
```

**Error Response (404 Not Found):**
```json
"Training plan not found"
```

---

### 3. Create Training Plan
**POST** `/training-plans/users/{userId}/training-plans`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID

**Request Body:**
```json
{
  "title": "Advanced Hypertrophy Program",
  "description": "4-day split for muscle growth"
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "title": "Advanced Hypertrophy Program",
  "description": "4-day split for muscle growth"
}
```

---

### 4. Update Training Plan
**PUT** `/training-plans/users/{userId}/training-plans/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `id` (UUID) - Training Plan ID

**Request Body:**
```json
{
  "title": "Updated Program Name",
  "description": "Updated description"
}
```

**Success Response (200 OK):**
```json
"Training plan updated successfully"
```

---

### 5. Delete Training Plan
**DELETE** `/training-plans/users/{userId}/training-plans/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `id` (UUID) - Training Plan ID

**Success Response (200 OK):**
```json
"Training plan deleted successfully"
```

---

## Subscription Endpoints

### 1. Get Subscriptions by User
**GET** `/subscriptions/users/{userId}/subscriptions`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID

**Query Parameters:**
- `offset` (int, default: 0) - Pagination offset
- `limit` (int, default: 10) - Number of results

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440050",
    "startDate": "2024-01-01",
    "endDate": "2024-02-01",
    "active": true,
    "cDate": "2024-01-01T10:00:00",
    "mDate": "2024-01-01T10:00:00"
  }
]
```

---

### 2. Get Subscription by ID
**GET** `/subscriptions/users/{userId}/subscriptions/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `id` (UUID) - Subscription ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440050",
  "startDate": "2024-01-01",
  "endDate": "2024-02-01",
  "active": true,
  "cDate": "2024-01-01T10:00:00",
  "mDate": "2024-01-01T10:00:00"
}
```

**Error Response (404 Not Found):**
```json
"Subscription not found"
```

---

### 3. Create Subscription
**POST** `/subscriptions/users/{userId}/subscriptions`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID

**Request Body:**
```json
{
  "startDate": "2024-05-01",
  "endDate": "2024-06-01",
  "active": true
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "startDate": "2024-05-01",
  "endDate": "2024-06-01",
  "active": true
}
```

---

### 4. Update Subscription
**PUT** `/subscriptions/users/{userId}/subscriptions/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `id` (UUID) - Subscription ID

**Request Body:**
```json
{
  "endDate": "2024-07-01",
  "active": true
}
```

**Success Response (200 OK):**
```json
"Subscription updated successfully"
```

---

### 5. Delete Subscription
**DELETE** `/subscriptions/users/{userId}/subscriptions/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `userId` (UUID) - User ID
- `id` (UUID) - Subscription ID

**Success Response (200 OK):**
```json
"Subscription deleted successfully"
```

---

## Membership Plan Endpoints

### 1. Get All Membership Plans
**GET** `/membership-plans`

**Access:** `ADMIN`

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440060",
    "title": "Monthly Premium",
    "description": "Full access to all facilities",
    "price": 49.99,
    "durationMonths": 1,
    "cDate": "2024-01-01T10:00:00",
    "mDate": "2024-01-01T10:00:00"
  }
]
```

---

### 2. Get Membership Plan by ID
**GET** `/membership-plans/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Membership Plan ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440060",
  "title": "Monthly Premium",
  "description": "Full access to all facilities",
  "price": 49.99,
  "durationMonths": 1
}
```

**Error Response (404 Not Found):**
```json
"Membership plan not found"
```

---

### 3. Create Membership Plan
**POST** `/membership-plans`

**Access:** `ADMIN`

**Request Body:**
```json
{
  "title": "Annual VIP",
  "description": "Full access + personal training",
  "price": 499.99,
  "durationMonths": 12
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "title": "Annual VIP",
  "description": "Full access + personal training",
  "price": 499.99,
  "durationMonths": 12
}
```

---

### 4. Update Membership Plan
**PUT** `/membership-plans/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Membership Plan ID

**Request Body:**
```json
{
  "title": "Annual VIP Updated",
  "price": 449.99
}
```

**Success Response (200 OK):**
```json
"Membership plan updated successfully"
```

---

### 5. Delete Membership Plan
**DELETE** `/membership-plans/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Membership Plan ID

**Success Response (200 OK):**
```json
"Membership plan deleted successfully"
```

---

## Product Endpoints

### 1. Get All Products
**GET** `/products`

**Access:** `ADMIN`

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440070",
    "name": "Protein Powder",
    "description": "Whey protein isolate",
    "price": 39.99,
    "stockQuantity": 50,
    "categoryId": "550e8400-e29b-41d4-a716-446655440065",
    "cDate": "2024-01-01T10:00:00",
    "mDate": "2024-01-01T10:00:00"
  }
]
```

---

### 2. Get Product by ID
**GET** `/products/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Product ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440070",
  "name": "Protein Powder",
  "description": "Whey protein isolate",
  "price": 39.99,
  "stockQuantity": 50,
  "categoryId": "550e8400-e29b-41d4-a716-446655440065"
}
```

**Error Response (404 Not Found):**
```json
"Product not found"
```

---

### 3. Create Product
**POST** `/products`

**Access:** `ADMIN`

**Request Body:**
```json
{
  "name": "Pre-Workout",
  "description": "Energy and focus supplement",
  "price": 29.99,
  "stockQuantity": 100,
  "categoryId": "550e8400-e29b-41d4-a716-446655440065"
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "name": "Pre-Workout",
  "price": 29.99
}
```

---

### 4. Update Product
**PUT** `/products/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Product ID

**Request Body:**
```json
{
  "name": "Pre-Workout Updated",
  "price": 24.99,
  "stockQuantity": 120
}
```

**Success Response (200 OK):**
```json
"Product updated successfully"
```

---

### 5. Delete Product
**DELETE** `/products/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Product ID

**Success Response (200 OK):**
```json
"Product deleted successfully"
```

---

## Order Endpoints

### 1. Get All Orders
**GET** `/orders`

**Access:** `ADMIN`

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440080",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 79.98,
    "status": "COMPLETED",
    "orderDate": "2024-04-01",
    "cDate": "2024-04-01T14:30:00",
    "mDate": "2024-04-01T14:30:00"
  }
]
```

---

### 2. Get Order by ID
**GET** `/orders/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Order ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440080",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 79.98,
  "status": "COMPLETED",
  "orderDate": "2024-04-01"
}
```

**Error Response (404 Not Found):**
```json
"Order not found"
```

---

### 3. Create Order
**POST** `/orders`

**Access:** `ADMIN`

**Request Body:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 59.99,
  "status": "PENDING",
  "orderDate": "2024-04-07"
}
```

**Success Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440099",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 59.99,
  "status": "PENDING"
}
```

---

### 4. Update Order
**PUT** `/orders/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Order ID

**Request Body:**
```json
{
  "status": "COMPLETED",
  "totalAmount": 59.99
}
```

**Success Response (200 OK):**
```json
"Order updated successfully"
```

---

### 5. Delete Order
**DELETE** `/orders/{id}`

**Access:** `ADMIN`

**Path Parameters:**
- `id` (UUID) - Order ID

**Success Response (200 OK):**
```json
"Order deleted successfully"
```

---

## Dashboard

### 1. Get dashboard data
**GET** `/dashboard/metrics?businessId=${businessId}&${startDate}&${endDate}`
businessId=xxxx-xxxx.... startDate=2026-04-01 endDate=2026-04-30
**Access:** `ADMIN`

**Success Response (200 OK):**
```json
{
  "periodInfo": {
    "days": 7,
    "startDate": "2026-04-17",
    "endDate": "2026-04-23",
    "previousStartDate": "2026-04-10",
    "previousEndDate": "2026-04-16"
  },
  "metrics": {
    "activeMembersNow": {
      "current": 7,
      "total": 42
    },
    "checkInsToday": {
      "current": 9,
      "percentChange": 125.0,
      "previous": 4
    },
    "revenue": {
      "current": 0,
      "percentChange": -100.0,
      "previous": 15998
    },
    "totalClients": {
      "current": 31,
      "percentChange": -8.823529411764707,
      "previous": 34
    },
    "newClients": 9
  },
  "graphs": {
    "revenueTimeSeries": [
      {
        "date": "2026-04-17",
        "value": 0
      },
      {
        "date": "2026-04-18",
        "value": 0
      },
      {
        "date": "2026-04-19",
        "value": 0
      },
      {
        "date": "2026-04-20",
        "value": 0
      },
      {
        "date": "2026-04-21",
        "value": 0
      },
      {
        "date": "2026-04-22",
        "value": 0
      },
      {
        "date": "2026-04-23",
        "value": 0
      }
    ],
    "subscriptions": [
      {
        "date": "2026-04-17",
        "value": 0
      },
      {
        "date": "2026-04-18",
        "value": 0
      },
      {
        "date": "2026-04-19",
        "value": 0
      },
      {
        "date": "2026-04-20",
        "value": 0
      },
      {
        "date": "2026-04-21",
        "value": 0
      },
      {
        "date": "2026-04-22",
        "value": 0
      },
      {
        "date": "2026-04-23",
        "value": 0
      }
    ],
    "rushHours": [
      {
        "value": 3,
        "label": "08:00"
      },
      {
        "value": 3,
        "label": "09:00"
      },
      {
        "value": 3,
        "label": "10:00"
      },
      {
        "value": 2,
        "label": "12:00"
      },
      {
        "value": 3,
        "label": "13:00"
      },
      {
        "value": 5,
        "label": "14:00"
      },
      {
        "value": 5,
        "label": "15:00"
      },
      {
        "value": 1,
        "label": "16:00"
      },
      {
        "value": 1,
        "label": "17:00"
      },
      {
        "value": 4,
        "label": "19:00"
      },
      {
        "value": 3,
        "label": "20:00"
      },
      {
        "value": 4,
        "label": "21:00"
      },
      {
        "value": 3,
        "label": "22:00"
      }
    ],
    "clientGrowth": [
      {
        "date": "2026-04-18",
        "value": 1
      },
      {
        "date": "2026-04-19",
        "value": 1
      },
      {
        "date": "2026-04-20",
        "value": 1
      },
      {
        "date": "2026-04-21",
        "value": 1
      },
      {
        "date": "2026-04-22",
        "value": 1
      },
      {
        "date": "2026-04-23",
        "value": 4
      }
    ]
  }
}
```

## Common Error Responses

### 400 Bad Request
```json
"Invalid input data"
```

### 401 Unauthorized
```json
"Authentication required"
```

### 403 Forbidden
```json
"Forbidden: Insufficient permissions"
```

### 404 Not Found
```json
"Resource not found"
```

### 500 Internal Server Error
```json
"Internal server error"
```

---

## Notes

### UUID Format
All IDs are UUIDs in the format: `550e8400-e29b-41d4-a716-446655440000`

### Date Formats
- Dates: `YYYY-MM-DD` (e.g., `2024-04-07`)
- Timestamps: `YYYY-MM-DDTHH:mm:ss` (e.g., `2024-04-07T11:30:00`)

### Pagination
Most list endpoints support pagination with `offset` and `limit` query parameters.

### Filtering
User and exercise endpoints support filtering by various criteria. Check individual endpoint documentation.

### Nested Data
Training Plan Exercise endpoints return nested data including:
- Exercise details
- Prescribed sets (what client should do)
- Recent progress (what client actually did)

This follows **Option B** strategy with multiple queries for optimal performance.
