CREATE TABLE businesses (
                      id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      name    VARCHAR(100) NOT NULL,
                      address     TEXT,
                      latitude    NUMERIC(9, 6),
                      longitude   NUMERIC(9, 6),
                      is_active    BOOLEAN DEFAULT TRUE not null ,
                        type varchar(30) not null ,
                      cDate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE,
                       password_hash TEXT,
                       role varchar(30) NOT NULL,
                       profile_pic_url VARCHAR(255),
                        gender varchar(10) not null ,
                       phone VARCHAR(15),
                       enrollment_date DATE DEFAULT CURRENT_DATE,
                       status varchar(30) not null ,
                       cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- measurements: weight, height, body fat percentage, muscle mass, measurement date for user
CREATE TABLE user_measurements (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   weight       NUMERIC(5, 2),
                                   height       NUMERIC(5, 2),
                                   body_fat_percent NUMERIC(4, 2),
                                   muscle_mass_kg  NUMERIC(5, 2),
                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- training_plan: title, description, creation date, last modified date
CREATE TABLE training_plans (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   title TEXT NOT NULL,
                                   description TEXT,

                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- exercise: title, description, creation date, last modified date
CREATE TABLE exercises (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           title         TEXT NOT NULL,
                           description      TEXT,

                           cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- kjo esht nje tabel lidhese shume ne shume midis training_plan dhe exercises, ku nje plan trajnimi mund te kete shume ushtrime dhe nje ushtrim mund te jete ne shume plane trajnimi
CREATE TABLE training_plan_exercises (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   training_plan_id         UUID NOT NULL REFERENCES training_plans(id) ON DELETE CASCADE,
                                   exercise_id         UUID NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
                                   exercise_order       INTEGER NOT NULL DEFAULT 1, -- Order to perform exercises in the plan
                                   notes                TEXT, -- General instructions for this exercise
                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- This table stores the PRESCRIBED sets for each exercise in a training plan
-- Example: Bench Press - Set 1: 10 reps @ 60kg, Set 2: 8 reps @ 70kg, Set 3: 6 reps @ 80kg
CREATE TABLE training_plan_exercise_sets (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   training_plan_exercise_id UUID NOT NULL REFERENCES training_plan_exercises(id) ON DELETE CASCADE,
                                   set_number           INTEGER NOT NULL, -- 1, 2, 3, etc.
                                   prescribed_reps      INTEGER NOT NULL, -- How many reps for this set
                                   prescribed_weight    NUMERIC(5, 2), -- Target weight for this set (NULL for bodyweight)
                                   rest_seconds         INTEGER, -- Rest after this set
                                   notes                TEXT, -- Set-specific notes like "to failure" or "drop set"
                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE(training_plan_exercise_id, set_number)
);

-- This table stores ACTUAL workout progress - what the user DID
-- Progress is permanent workout history, independent of training plans
CREATE TABLE exercise_progress (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   exercise_id UUID NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
                                   training_plan_id UUID REFERENCES training_plans(id) ON DELETE SET NULL, -- Optional: which plan they were following
                                   set_number INTEGER NOT NULL,
                                   reps INTEGER NOT NULL,
                                   weight NUMERIC(5, 2) NOT NULL,
                                   workout_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                   notes TEXT,
                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- kjo esht nje tabel lidhese shume ne shume midis training_plan dhe users, ku nje plan trajnimi mund te jete i lidhur me shume se nje user dhe nje user mund te kete shume plane trajnimi
CREATE TABLE user_training_plans (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   training_plan_id         UUID NOT NULL REFERENCES training_plans(id) ON DELETE CASCADE,
                                   user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   assignee_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- category: name, description, creation date, last modified date
CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            title TEXT NOT NULL,
                            description TEXT,
                            cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- product categories: name, description, creation date, last modified date
CREATE TABLE products (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   title TEXT NOT NULL,
                                      description TEXT,
                                    stock INTEGER NOT NULL,
                                    price BIGINT NOT NULL,
                                    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
                                    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                                   cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- kjo esht nje tabel lidhese shume ne shume midis business dhe products, ku nje biznes mund te kete shume produkte dhe nje produkt mund te jete ne shume biznese
CREATE TABLE business_products (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            business_id         UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                            product_id         UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                            cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        business_id         UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                        user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        total_amount        BIGINT NOT NULL,
                        address TEXT NOT NULL,
                        cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shopping_cart (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               order_id         UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                               cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shopping_cart_products (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            shopping_cart_id         UUID NOT NULL REFERENCES shopping_cart(id) ON DELETE CASCADE,
                            product_id         UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                            cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_businesses (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           business_id          UUID NOT NULL REFERENCES businesses(id),
                           cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membership_plans (
                                  id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  gym_id       UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                                  plan_name    VARCHAR(50) NOT NULL,
                                  price_cents        BIGINT NOT NULL,
                                  duration_days INTEGER NOT NULL,
                                  description  TEXT,
                                  is_active    BOOLEAN DEFAULT TRUE not null ,
                                  cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscriptions (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               plan_id         UUID NOT NULL REFERENCES membership_plans(id),
                               business_id     UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE ,
                               start_date      DATE NOT NULL DEFAULT CURRENT_DATE,
                               end_date        DATE NOT NULL,
                               is_active    BOOLEAN DEFAULT TRUE not null ,
                               cDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               mDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE attendance_logs (
                                 id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 gym_id         UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                                 entry_time     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 exit_time      TIMESTAMPTZ,

                                 access_method  VARCHAR(20)
);

CREATE INDEX idx_active_sessions ON attendance_logs (user_id) WHERE exit_time IS NULL;

-- ============================================
-- TEST DATA
-- ============================================

INSERT INTO businesses (id, name, address, latitude, longitude, is_active, type) VALUES
                                                                                     ('11111111-1111-1111-1111-111111111111', 'PowerFit Gym', '123 Main Street, New York, NY', 40.712776, -74.005974, TRUE, 'Gym'),
                                                                                     ('22222222-2222-2222-2222-222222222222', 'Elite Fitness Center', '456 Oak Avenue, Los Angeles, CA', 34.052235, -118.243683, TRUE, 'Gym'),
                                                                                     ('33333333-3333-3333-3333-333333333333', 'FitGear Shop', '789 Broadway, Chicago, IL', 41.878113, -87.629799, TRUE, 'Shop');

-- Insert Users (Admin, Business Owners, Managers, Trainers, Staff, Clients)
-- Note: password_hash would normally be encrypted, using placeholder 'hashed_password_123'
INSERT INTO users (id, username, first_name, last_name, email, password_hash, role, gender, phone, status) VALUES
                                                                                                               ('a0000000-0000-0000-0000-000000000001', 'admin', 'John', 'Admin', 'admin@gym.com', '0zHHyHGYR54em852mulaOQ==', 'Admin', 'Male', '555-0001', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000002', 'owner1', 'Sarah', 'Johnson', 'sarah.j@powerfit.com', '0zHHyHGYR54em852mulaOQ==', 'Business_Owner', 'Female', '555-0002', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000003', 'owner2', 'Michael', 'Chen', 'michael.c@elitefitness.com', '0zHHyHGYR54em852mulaOQ==', 'Business_Owner', 'Male', '555-0003', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000004', 'manager1', 'Emily', 'Davis', 'emily.d@powerfit.com', '0zHHyHGYR54em852mulaOQ==', 'Business_Manager', 'Female', '555-0004', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000005', 'trainer1', 'James', 'Wilson', 'james.w@powerfit.com', '0zHHyHGYR54em852mulaOQ==', 'Trainer', 'Male', '555-0005', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000006', 'trainer2', 'Lisa', 'Martinez', 'lisa.m@elitefitness.com', '0zHHyHGYR54em852mulaOQ==', 'Trainer', 'Female', '555-0006', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000007', 'staff1', 'David', 'Brown', 'david.b@powerfit.com', '0zHHyHGYR54em852mulaOQ==', 'Staff', 'Male', '555-0007', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000008', 'client1', 'Anna', 'Taylor', 'anna.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0008', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000009', 'client2', 'Robert', 'Anderson', 'robert.a@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0009', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000010', 'client3', 'Jennifer', 'Garcia', 'jennifer.g@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0010', 'Active'),
                                                                                                               ('a0000000-0000-0000-0000-000000000011', 'client4', 'Thomas', 'Lee', 'thomas.l@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0011', 'Active');

-- Link Users to Businesses
INSERT INTO user_businesses (user_id, business_id) VALUES
                                                       ('a0000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111'), -- Owner1 -> PowerFit
                                                       ('a0000000-0000-0000-0000-000000000003', '22222222-2222-2222-2222-222222222222'), -- Owner2 -> Elite Fitness
                                                       ('a0000000-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111'), -- Manager1 -> PowerFit
                                                       ('a0000000-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111'), -- Trainer1 -> PowerFit
                                                       ('a0000000-0000-0000-0000-000000000006', '22222222-2222-2222-2222-222222222222'), -- Trainer2 -> Elite Fitness
                                                       ('a0000000-0000-0000-0000-000000000007', '11111111-1111-1111-1111-111111111111'), -- Staff1 -> PowerFit
                                                       ('a0000000-0000-0000-0000-000000000008', '11111111-1111-1111-1111-111111111111'), -- Client1 -> PowerFit
                                                       ('a0000000-0000-0000-0000-000000000009', '11111111-1111-1111-1111-111111111111'), -- Client2 -> PowerFit
                                                       ('a0000000-0000-0000-0000-000000000010', '22222222-2222-2222-2222-222222222222'), -- Client3 -> Elite Fitness
                                                       ('a0000000-0000-0000-0000-000000000011', '22222222-2222-2222-2222-222222222222'); -- Client4 -> Elite Fitness

-- Insert Membership Plans
INSERT INTO membership_plans (id, gym_id, plan_name, price_cents, duration_days, description, is_active) VALUES
                                                                                                             ('b0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Monthly Basic', 4999, 30, 'Access to gym facilities during regular hours', TRUE),
                                                                                                             ('b0000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Monthly Premium', 7999, 30, 'Access to all facilities + 4 personal training sessions', TRUE),
                                                                                                             ('b0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'Annual VIP', 79999, 365, 'Unlimited access + unlimited training + nutrition consulting', TRUE),
                                                                                                             ('b0000000-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222222', 'Student Monthly', 3499, 30, 'Special student rate with valid ID', TRUE),
                                                                                                             ('b0000000-0000-0000-0000-000000000005', '22222222-2222-2222-2222-222222222222', 'Elite Quarterly', 19999, 90, '3-month commitment with group classes included', TRUE);

-- Insert Subscriptions
INSERT INTO subscriptions (id, user_id, plan_id, business_id, start_date, end_date, is_active) VALUES
                                                                                      ('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '2026-03-01', '2026-03-31', TRUE),
                                                                                      ('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', '2026-02-15', '2026-03-15', TRUE),
                                                                                      ('c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222222', '2026-03-10', '2026-04-10', TRUE),
                                                                                      ('c0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000011', 'b0000000-0000-0000-0000-000000000005', '22222222-2222-2222-2222-222222222222', '2026-01-01', '2026-03-31', TRUE);

-- Insert Categories
INSERT INTO categories (id, title, description) VALUES
                                                    ('d0000000-0000-0000-0000-000000000001', 'Supplements', 'Protein powders, vitamins, and nutrition supplements'),
                                                    ('d0000000-0000-0000-0000-000000000002', 'Apparel', 'Workout clothes and accessories'),
                                                    ('d0000000-0000-0000-0000-000000000003', 'Equipment', 'Home gym equipment and accessories');

-- Insert Products
INSERT INTO products (id, title, description, stock, price, category_id, business_id) VALUES
                                                                                          ('e0000000-0000-0000-0000-000000000001', 'Whey Protein - Chocolate', '2lb container of premium whey protein', 50, 3999, 'd0000000-0000-0000-0000-000000000001', '33333333-3333-3333-3333-333333333333'),
                                                                                          ('e0000000-0000-0000-0000-000000000002', 'Pre-Workout Energy', 'High-caffeine pre-workout supplement', 30, 2999, 'd0000000-0000-0000-0000-000000000001', '33333333-3333-3333-3333-333333333333'),
                                                                                          ('e0000000-0000-0000-0000-000000000003', 'Gym Tank Top - Black', 'Moisture-wicking tank top', 100, 1999, 'd0000000-0000-0000-0000-000000000002', '33333333-3333-3333-3333-333333333333'),
                                                                                          ('e0000000-0000-0000-0000-000000000004', 'Resistance Bands Set', 'Set of 5 resistance bands with varying strengths', 25, 2499, 'd0000000-0000-0000-0000-000000000003', '33333333-3333-3333-3333-333333333333'),
                                                                                          ('e0000000-0000-0000-0000-000000000005', 'Yoga Mat - Blue', 'Non-slip yoga mat with carrying strap', 40, 2999, 'd0000000-0000-0000-0000-000000000003', '33333333-3333-3333-3333-333333333333');

-- Insert Exercises
INSERT INTO exercises (id, title, description) VALUES
                                                   ('f0000000-0000-0000-0000-000000000001', 'Bench Press', 'Barbell bench press for chest development'),
                                                   ('f0000000-0000-0000-0000-000000000002', 'Squats', 'Barbell back squats for leg strength'),
                                                   ('f0000000-0000-0000-0000-000000000003', 'Deadlift', 'Conventional deadlift for full body strength'),
                                                   ('f0000000-0000-0000-0000-000000000004', 'Pull-ups', 'Wide-grip pull-ups for back development'),
                                                   ('f0000000-0000-0000-0000-000000000005', 'Overhead Press', 'Standing barbell shoulder press'),
                                                   ('f0000000-0000-0000-0000-000000000006', 'Bicep Curls', 'Dumbbell bicep curls'),
                                                   ('f0000000-0000-0000-0000-000000000007', 'Tricep Dips', 'Bodyweight tricep dips'),
                                                   ('f0000000-0000-0000-0000-000000000008', 'Plank', 'Core stability plank hold'),
                                                   ('f0000000-0000-0000-0000-000000000009', 'Running', '30-minute cardio run');

-- Insert Training Plans
INSERT INTO training_plans (id, title, description) VALUES
('f0000000-0000-0000-0000-000000000004', 'Beginner Full Body', 'Full body workout for beginners, 3x per week'),
('f0000000-0000-0000-0000-000000000002', 'Advanced Strength', 'Advanced powerlifting program'),
('f0000000-0000-0000-0000-000000000003', 'Weight Loss Program', 'Combination of cardio and resistance training for fat loss');

-- Link Exercises to Training Plans (with order)
INSERT INTO training_plan_exercises (id, training_plan_id, exercise_id, exercise_order, notes) VALUES
-- Beginner Plan
('a0000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000004', 'f0000000-0000-0000-0000-000000000002', 1, 'Focus on form'), -- Squats
('a0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 'f0000000-0000-0000-0000-000000000001', 2, 'Keep back flat'), -- Bench Press
('a0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000004', 'f0000000-0000-0000-0000-000000000004', 3, 'Use assistance if needed'), -- Pull-ups
('a0000000-0000-0000-0000-000000000004', 'f0000000-0000-0000-0000-000000000004', 'f0000000-0000-0000-0000-000000000008', 4, 'Hold for time'), -- Plank
-- Advanced Plan
('a0000000-0000-0000-0000-000000000005', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000002', 1, 'Heavy weight'), -- Squats
('a0000000-0000-0000-0000-000000000006', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000001', 2, 'Competition style'), -- Bench Press
('a0000000-0000-0000-0000-000000000007', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000003', 3, 'Conventional stance'), -- Deadlift
('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000005', 4, 'Strict form'), -- Overhead Press
-- Weight Loss Plan
('a0000000-0000-0000-0000-000000000009', 'f0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000009', 1, 'Moderate pace'), -- Running
('a0000000-0000-0000-0000-000000000010', 'f0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000002', 2, 'Higher reps'), -- Squats
('a0000000-0000-0000-0000-000000000011', 'f0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000008', 3, 'Core strength'); -- Plank

-- Define PRESCRIBED sets for each exercise in training plans
INSERT INTO training_plan_exercise_sets (training_plan_exercise_id, set_number, prescribed_reps, prescribed_weight, rest_seconds, notes) VALUES
-- Beginner Plan - Squats (3 sets of 10 reps)
('a0000000-0000-0000-0000-000000000001', 1, 10, 40.00, 90, NULL),
('a0000000-0000-0000-0000-000000000001', 2, 10, 40.00, 90, NULL),
('a0000000-0000-0000-0000-000000000001', 3, 10, 40.00, 90, NULL),
-- Beginner Plan - Bench Press (3 sets of 8 reps)
('a0000000-0000-0000-0000-000000000002', 1, 8, 30.00, 90, NULL),
('a0000000-0000-0000-0000-000000000002', 2, 8, 30.00, 90, NULL),
('a0000000-0000-0000-0000-000000000002', 3, 8, 30.00, 90, NULL),
-- Beginner Plan - Pull-ups (3 sets of 5 reps, bodyweight)
('a0000000-0000-0000-0000-000000000003', 1, 5, NULL, 120, NULL),
('a0000000-0000-0000-0000-000000000003', 2, 5, NULL, 120, NULL),
('a0000000-0000-0000-0000-000000000003', 3, 5, NULL, 120, NULL),
-- Beginner Plan - Plank (3 sets of 30 seconds)
('a0000000-0000-0000-0000-000000000004', 1, 30, NULL, 60, 'Hold for 30 seconds'),
('a0000000-0000-0000-0000-000000000004', 2, 30, NULL, 60, 'Hold for 30 seconds'),
('a0000000-0000-0000-0000-000000000004', 3, 30, NULL, 60, 'Hold for 30 seconds'),
-- Advanced Plan - Squats (pyramid: 5, 3, 1 reps with increasing weight)
('a0000000-0000-0000-0000-000000000005', 1, 5, 100.00, 180, 'Warm-up set'),
('a0000000-0000-0000-0000-000000000005', 2, 3, 120.00, 240, 'Heavy set'),
('a0000000-0000-0000-0000-000000000005', 3, 1, 140.00, 300, 'Max effort'),
-- Advanced Plan - Bench Press (5x5)
('a0000000-0000-0000-0000-000000000006', 1, 5, 80.00, 180, NULL),
('a0000000-0000-0000-0000-000000000006', 2, 5, 80.00, 180, NULL),
('a0000000-0000-0000-0000-000000000006', 3, 5, 80.00, 180, NULL),
('a0000000-0000-0000-0000-000000000006', 4, 5, 80.00, 180, NULL),
('a0000000-0000-0000-0000-000000000006', 5, 5, 80.00, 180, NULL),
-- Advanced Plan - Deadlift (3x3)
('a0000000-0000-0000-0000-000000000007', 1, 3, 140.00, 240, NULL),
('a0000000-0000-0000-0000-000000000007', 2, 3, 140.00, 240, NULL),
('a0000000-0000-0000-0000-000000000007', 3, 3, 140.00, 240, NULL),
-- Weight Loss Plan - Running (1 set of 30 minutes)
('a0000000-0000-0000-0000-000000000009', 1, 30, NULL, 0, '30 minutes at moderate pace'),
-- Weight Loss Plan - Squats (3 sets of 15 reps, lighter weight)
('a0000000-0000-0000-0000-000000000010', 1, 15, 30.00, 60, NULL),
('a0000000-0000-0000-0000-000000000010', 2, 15, 30.00, 60, NULL),
('a0000000-0000-0000-0000-000000000010', 3, 15, 30.00, 60, NULL);

-- Assign Training Plans to Users
INSERT INTO user_training_plans (training_plan_id, user_id, assignee_id) VALUES
                                                                             ('f0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000005'), -- Client1 assigned Beginner by Trainer1
                                                                             ('f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000005'), -- Client2 assigned Advanced by Trainer1
                                                                             ('f0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000006'); -- Client3 assigned Weight Loss by Trainer2

-- Insert Exercise Progress
-- Insert Exercise Progress (permanent workout history)
INSERT INTO exercise_progress (user_id, exercise_id, training_plan_id, set_number, reps, weight, workout_date, notes) VALUES
    -- User a0000000-0000-0000-0000-000000000008 (John Client) - Workout 1
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 1, 10, 60.00, '2024-04-01', 'Felt strong'),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 2, 10, 60.00, '2024-04-01', NULL),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 3, 10, 60.00, '2024-04-01', NULL),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000004', 1, 8, 45.00, '2024-04-01', NULL),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000004', 2, 8, 45.00, '2024-04-01', NULL),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000004', 3, 8, 45.00, '2024-04-01', NULL),
    -- Workout 2
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 1, 10, 65.00, '2024-04-03', 'Increased weight'),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 2, 9, 65.00, '2024-04-03', 'Struggled on last rep'),
    ('a0000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000004', 3, 8, 65.00, '2024-04-03', NULL),
    -- User a0000000-0000-0000-0000-000000000009 (Jane Client)
    ('a0000000-0000-0000-0000-000000000009', 'f0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000003', 1, 12, 40.00, '2024-04-02', NULL),
    ('a0000000-0000-0000-0000-000000000009', 'f0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000003', 2, 12, 40.00, '2024-04-02', NULL),
    ('a0000000-0000-0000-0000-000000000009', 'f0000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000003', 3, 10, 40.00, '2024-04-02', 'Fatigue on last set');

-- Insert User Measurements
INSERT INTO user_measurements (user_id, weight, height, body_fat_percent, muscle_mass_kg) VALUES
                                                                                              ('a0000000-0000-0000-0000-000000000008', 70.50, 165.00, 22.50, 35.20),
                                                                                              ('a0000000-0000-0000-0000-000000000009', 85.00, 180.00, 18.00, 48.50),
                                                                                              ('a0000000-0000-0000-0000-000000000010', 62.00, 160.00, 25.00, 28.00);

-- Insert Orders
INSERT INTO orders (business_id, user_id, total_amount, address) VALUES
                                                                     ('33333333-3333-3333-3333-333333333333', 'a0000000-0000-0000-0000-000000000008', 6998, '123 Client Street, NYC'),
                                                                     ('33333333-3333-3333-3333-333333333333', 'a0000000-0000-0000-0000-000000000009', 2999, '456 Customer Ave, LA');

-- Insert Attendance Logs
INSERT INTO attendance_logs (user_id, gym_id, entry_time, exit_time, access_method) VALUES
                                                                                        ('a0000000-0000-0000-0000-000000000008', '11111111-1111-1111-1111-111111111111', '2026-04-07 08:00:00+00', '2026-04-07 09:30:00+00', 'Card Scan'),
                                                                                        ('a0000000-0000-0000-0000-000000000009', '11111111-1111-1111-1111-111111111111', '2026-04-07 07:00:00+00', '2026-04-07 08:45:00+00', 'Mobile App'),
                                                                                        ('a0000000-0000-0000-0000-000000000010', '22222222-2222-2222-2222-222222222222', '2026-04-07 18:00:00+00', NULL, 'Card Scan'); -- Still in gym

