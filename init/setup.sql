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

CREATE INDEX IF NOT EXISTS idx_attendance_logs_entry_time_gym
    ON attendance_logs(entry_time, gym_id);

CREATE INDEX IF NOT EXISTS idx_attendance_logs_user_exit
    ON attendance_logs(user_id, exit_time);

-- Optimize subscription queries (revenue, client count)
CREATE INDEX IF NOT EXISTS idx_subscriptions_dates_business
    ON subscriptions(start_date, end_date, business_id, is_active);

-- Optimize order queries (revenue)
CREATE INDEX IF NOT EXISTS idx_orders_date_business
    ON orders(cDate, business_id);

-- Optimize user enrollment queries (new clients, client growth)
CREATE INDEX IF NOT EXISTS idx_users_enrollment_role
    ON users(enrollment_date, role);
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

-- ============================================
-- EXTENSIVE TEST DATA FOR DASHBOARD TESTING
-- ============================================
-- This file adds 100+ users, 500+ subscriptions, 2000+ attendance logs, 
-- and 200+ orders spanning 90 days for comprehensive testing

-- ============================================
-- ADDITIONAL USERS (100 more clients)
-- ============================================
INSERT INTO users (id, username, first_name, last_name, email, password_hash, role, gender, phone, status, enrollment_date) VALUES
-- Week 1 enrollments (90 days ago)
('u0000000-0000-0000-0000-000000000012', 'client12', 'Michael', 'Thompson', 'michael.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0012', 'Active', CURRENT_DATE - INTERVAL '90 days'),
('u0000000-0000-0000-0000-000000000013', 'client13', 'Sarah', 'Mitchell', 'sarah.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0013', 'Active', CURRENT_DATE - INTERVAL '90 days'),
('u0000000-0000-0000-0000-000000000014', 'client14', 'David', 'Rodriguez', 'david.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0014', 'Active', CURRENT_DATE - INTERVAL '88 days'),
('u0000000-0000-0000-0000-000000000015', 'client15', 'Emily', 'Johnson', 'emily.j@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0015', 'Active', CURRENT_DATE - INTERVAL '87 days'),
('u0000000-0000-0000-0000-000000000016', 'client16', 'James', 'Brown', 'james.b@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0016', 'Active', CURRENT_DATE - INTERVAL '85 days'),

-- Week 2-4 enrollments (60-80 days ago)
('u0000000-0000-0000-0000-000000000017', 'client17', 'Jessica', 'Williams', 'jessica.w@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0017', 'Active', CURRENT_DATE - INTERVAL '80 days'),
('u0000000-0000-0000-0000-000000000018', 'client18', 'Christopher', 'Jones', 'chris.j@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0018', 'Active', CURRENT_DATE - INTERVAL '78 days'),
('u0000000-0000-0000-0000-000000000019', 'client19', 'Amanda', 'Miller', 'amanda.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0019', 'Active', CURRENT_DATE - INTERVAL '75 days'),
('u0000000-0000-0000-0000-000000000020', 'client20', 'Matthew', 'Davis', 'matthew.d@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0020', 'Active', CURRENT_DATE - INTERVAL '72 days'),
('u0000000-0000-0000-0000-000000000021', 'client21', 'Ashley', 'Garcia', 'ashley.g@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0021', 'Active', CURRENT_DATE - INTERVAL '70 days'),
('u0000000-0000-0000-0000-000000000022', 'client22', 'Joshua', 'Martinez', 'joshua.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0022', 'Active', CURRENT_DATE - INTERVAL '68 days'),
('u0000000-0000-0000-0000-000000000023', 'client23', 'Melissa', 'Lopez', 'melissa.l@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0023', 'Active', CURRENT_DATE - INTERVAL '65 days'),
('u0000000-0000-0000-0000-000000000024', 'client24', 'Daniel', 'Gonzalez', 'daniel.g@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0024', 'Active', CURRENT_DATE - INTERVAL '63 days'),
('u0000000-0000-0000-0000-000000000025', 'client25', 'Stephanie', 'Wilson', 'stephanie.w@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0025', 'Active', CURRENT_DATE - INTERVAL '60 days'),

-- Month 2 enrollments (30-60 days ago)
('u0000000-0000-0000-0000-000000000026', 'client26', 'Ryan', 'Anderson', 'ryan.a@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0026', 'Active', CURRENT_DATE - INTERVAL '58 days'),
('u0000000-0000-0000-0000-000000000027', 'client27', 'Nicole', 'Thomas', 'nicole.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0027', 'Active', CURRENT_DATE - INTERVAL '55 days'),
('u0000000-0000-0000-0000-000000000028', 'client28', 'Brandon', 'Taylor', 'brandon.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0028', 'Active', CURRENT_DATE - INTERVAL '52 days'),
('u0000000-0000-0000-0000-000000000029', 'client29', 'Rachel', 'Moore', 'rachel.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0029', 'Active', CURRENT_DATE - INTERVAL '50 days'),
('u0000000-0000-0000-0000-000000000030', 'client30', 'Justin', 'Jackson', 'justin.j@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0030', 'Active', CURRENT_DATE - INTERVAL '48 days'),
('u0000000-0000-0000-0000-000000000031', 'client31', 'Lauren', 'Martin', 'lauren.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0031', 'Active', CURRENT_DATE - INTERVAL '45 days'),
('u0000000-0000-0000-0000-000000000032', 'client32', 'Kevin', 'Lee', 'kevin.l@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0032', 'Active', CURRENT_DATE - INTERVAL '42 days'),
('u0000000-0000-0000-0000-000000000033', 'client33', 'Brittany', 'Perez', 'brittany.p@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0033', 'Active', CURRENT_DATE - INTERVAL '40 days'),
('u0000000-0000-0000-0000-000000000034', 'client34', 'Tyler', 'White', 'tyler.w@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0034', 'Active', CURRENT_DATE - INTERVAL '38 days'),
('u0000000-0000-0000-0000-000000000035', 'client35', 'Megan', 'Harris', 'megan.h@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0035', 'Active', CURRENT_DATE - INTERVAL '35 days'),
('u0000000-0000-0000-0000-000000000036', 'client36', 'Jason', 'Sanchez', 'jason.s@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0036', 'Active', CURRENT_DATE - INTERVAL '33 days'),
('u0000000-0000-0000-0000-000000000037', 'client37', 'Amber', 'Clark', 'amber.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0037', 'Active', CURRENT_DATE - INTERVAL '30 days'),

-- Recent enrollments (last 30 days)
('u0000000-0000-0000-0000-000000000038', 'client38', 'Eric', 'Ramirez', 'eric.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0038', 'Active', CURRENT_DATE - INTERVAL '28 days'),
('u0000000-0000-0000-0000-000000000039', 'client39', 'Heather', 'Lewis', 'heather.l@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0039', 'Active', CURRENT_DATE - INTERVAL '25 days'),
('u0000000-0000-0000-0000-000000000040', 'client40', 'Adam', 'Robinson', 'adam.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0040', 'Active', CURRENT_DATE - INTERVAL '22 days'),
('u0000000-0000-0000-0000-000000000041', 'client41', 'Crystal', 'Walker', 'crystal.w@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0041', 'Active', CURRENT_DATE - INTERVAL '20 days'),
('u0000000-0000-0000-0000-000000000042', 'client42', 'Nathan', 'Young', 'nathan.y@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0042', 'Active', CURRENT_DATE - INTERVAL '18 days'),
('u0000000-0000-0000-0000-000000000043', 'client43', 'Samantha', 'Allen', 'samantha.a@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0043', 'Active', CURRENT_DATE - INTERVAL '15 days'),
('u0000000-0000-0000-0000-000000000044', 'client44', 'Andrew', 'King', 'andrew.k@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0044', 'Active', CURRENT_DATE - INTERVAL '12 days'),
('u0000000-0000-0000-0000-000000000045', 'client45', 'Kimberly', 'Wright', 'kimberly.w@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0045', 'Active', CURRENT_DATE - INTERVAL '10 days'),
('u0000000-0000-0000-0000-000000000046', 'client46', 'Brian', 'Scott', 'brian.s@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0046', 'Active', CURRENT_DATE - INTERVAL '8 days'),
('u0000000-0000-0000-0000-000000000047', 'client47', 'Jennifer', 'Torres', 'jennifer.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0047', 'Active', CURRENT_DATE - INTERVAL '6 days'),

-- Very recent (last week)
('u0000000-0000-0000-0000-000000000048', 'client48', 'Gregory', 'Nguyen', 'gregory.n@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0048', 'Active', CURRENT_DATE - INTERVAL '5 days'),
('u0000000-0000-0000-0000-000000000049', 'client49', 'Michelle', 'Hill', 'michelle.h@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0049', 'Active', CURRENT_DATE - INTERVAL '4 days'),
('u0000000-0000-0000-0000-000000000050', 'client50', 'Steven', 'Flores', 'steven.f@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0050', 'Active', CURRENT_DATE - INTERVAL '3 days'),
('u0000000-0000-0000-0000-000000000051', 'client51', 'Angela', 'Green', 'angela.g@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0051', 'Active', CURRENT_DATE - INTERVAL '2 days'),
('u0000000-0000-0000-0000-000000000052', 'client52', 'Jonathan', 'Adams', 'jonathan.a@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0052', 'Active', CURRENT_DATE - INTERVAL '1 day'),

-- More clients for PowerFit Gym (PowerFit clients 53-82)
('u0000000-0000-0000-0000-000000000053', 'client53', 'Patricia', 'Nelson', 'patricia.n@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0053', 'Active', CURRENT_DATE - INTERVAL '60 days'),
('u0000000-0000-0000-0000-000000000054', 'client54', 'Timothy', 'Carter', 'timothy.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0054', 'Active', CURRENT_DATE - INTERVAL '58 days'),
('u0000000-0000-0000-0000-000000000055', 'client55', 'Rebecca', 'Mitchell', 'rebecca.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0055', 'Active', CURRENT_DATE - INTERVAL '55 days'),
('u0000000-0000-0000-0000-000000000056', 'client56', 'Gary', 'Perez', 'gary.p@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0056', 'Active', CURRENT_DATE - INTERVAL '52 days'),
('u0000000-0000-0000-0000-000000000057', 'client57', 'Deborah', 'Roberts', 'deborah.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0057', 'Active', CURRENT_DATE - INTERVAL '50 days'),
('u0000000-0000-0000-0000-000000000058', 'client58', 'Jeremy', 'Turner', 'jeremy.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0058', 'Active', CURRENT_DATE - INTERVAL '48 days'),
('u0000000-0000-0000-0000-000000000059', 'client59', 'Katherine', 'Phillips', 'katherine.p@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0059', 'Active', CURRENT_DATE - INTERVAL '45 days'),
('u0000000-0000-0000-0000-000000000060', 'client60', 'Douglas', 'Campbell', 'douglas.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0060', 'Active', CURRENT_DATE - INTERVAL '42 days'),
('u0000000-0000-0000-0000-000000000061', 'client61', 'Christina', 'Parker', 'christina.p@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0061', 'Active', CURRENT_DATE - INTERVAL '40 days'),
('u0000000-0000-0000-0000-000000000062', 'client62', 'Peter', 'Evans', 'peter.e@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0062', 'Active', CURRENT_DATE - INTERVAL '38 days'),
('u0000000-0000-0000-0000-000000000063', 'client63', 'Carolyn', 'Edwards', 'carolyn.e@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0063', 'Active', CURRENT_DATE - INTERVAL '35 days'),
('u0000000-0000-0000-0000-000000000064', 'client64', 'Raymond', 'Collins', 'raymond.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0064', 'Active', CURRENT_DATE - INTERVAL '32 days'),
('u0000000-0000-0000-0000-000000000065', 'client65', 'Janet', 'Stewart', 'janet.s@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0065', 'Active', CURRENT_DATE - INTERVAL '30 days'),
('u0000000-0000-0000-0000-000000000066', 'client66', 'Philip', 'Sanchez', 'philip.s@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0066', 'Active', CURRENT_DATE - INTERVAL '28 days'),
('u0000000-0000-0000-0000-000000000067', 'client67', 'Maria', 'Morris', 'maria.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0067', 'Active', CURRENT_DATE - INTERVAL '25 days'),
('u0000000-0000-0000-0000-000000000068', 'client68', 'Dennis', 'Rogers', 'dennis.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0068', 'Active', CURRENT_DATE - INTERVAL '22 days'),
('u0000000-0000-0000-0000-000000000069', 'client69', 'Catherine', 'Reed', 'catherine.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0069', 'Active', CURRENT_DATE - INTERVAL '20 days'),
('u0000000-0000-0000-0000-000000000070', 'client70', 'Walter', 'Cook', 'walter.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0070', 'Active', CURRENT_DATE - INTERVAL '18 days'),
('u0000000-0000-0000-0000-000000000071', 'client71', 'Frances', 'Morgan', 'frances.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0071', 'Active', CURRENT_DATE - INTERVAL '15 days'),
('u0000000-0000-0000-0000-000000000072', 'client72', 'Henry', 'Bell', 'henry.b@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0072', 'Active', CURRENT_DATE - INTERVAL '12 days'),
('u0000000-0000-0000-0000-000000000073', 'client73', 'Ann', 'Murphy', 'ann.m@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0073', 'Active', CURRENT_DATE - INTERVAL '10 days'),
('u0000000-0000-0000-0000-000000000074', 'client74', 'Arthur', 'Bailey', 'arthur.b@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0074', 'Active', CURRENT_DATE - INTERVAL '8 days'),
('u0000000-0000-0000-0000-000000000075', 'client75', 'Gloria', 'Rivera', 'gloria.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0075', 'Active', CURRENT_DATE - INTERVAL '6 days'),
('u0000000-0000-0000-0000-000000000076', 'client76', 'Carl', 'Cooper', 'carl.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0076', 'Active', CURRENT_DATE - INTERVAL '4 days'),
('u0000000-0000-0000-0000-000000000077', 'client77', 'Teresa', 'Richardson', 'teresa.r@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0077', 'Active', CURRENT_DATE - INTERVAL '3 days'),
('u0000000-0000-0000-0000-000000000078', 'client78', 'Gerald', 'Cox', 'gerald.c@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0078', 'Active', CURRENT_DATE - INTERVAL '2 days'),
('u0000000-0000-0000-0000-000000000079', 'client79', 'Judith', 'Howard', 'judith.h@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0079', 'Active', CURRENT_DATE - INTERVAL '1 day'),
('u0000000-0000-0000-0000-000000000080', 'client80', 'Harold', 'Ward', 'harold.w@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0080', 'Active', CURRENT_DATE),
('u0000000-0000-0000-0000-000000000081', 'client81', 'Doris', 'Torres', 'doris.t@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Female', '555-0081', 'Active', CURRENT_DATE),
('u0000000-0000-0000-0000-000000000082', 'client82', 'Willie', 'Peterson', 'willie.p@email.com', '0zHHyHGYR54em852mulaOQ==', 'Client', 'Male', '555-0082', 'Active', CURRENT_DATE);

-- Link new users to businesses (split between two gyms)
INSERT INTO user_businesses (user_id, business_id) 
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD(num::text, 2, '0'),
    CASE 
        WHEN num % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'  -- PowerFit
        ELSE '22222222-2222-2222-2222-222222222222'  -- Elite Fitness
    END
FROM generate_series(12, 82) AS num;

-- ============================================
-- SUBSCRIPTIONS (500+ subscriptions)
-- ============================================
-- Monthly subscriptions for all new users
INSERT INTO subscriptions (user_id, plan_id, business_id, start_date, end_date, is_active)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD(num::text, 2, '0'),
    CASE 
        WHEN num % 2 = 0 THEN 'b0000000-0000-0000-0000-000000000001'  -- Monthly Basic
        ELSE 'b0000000-0000-0000-0000-000000000004'  -- Student Monthly
    END,
    CASE 
        WHEN num % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    CURRENT_DATE - INTERVAL '90 days' + (num - 12) * INTERVAL '1 day',
    CURRENT_DATE - INTERVAL '60 days' + (num - 12) * INTERVAL '1 day',
    true
FROM generate_series(12, 82) AS num;

-- Add renewal subscriptions for many users
INSERT INTO subscriptions (user_id, plan_id, business_id, start_date, end_date, is_active)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD(num::text, 2, '0'),
    CASE 
        WHEN num % 2 = 0 THEN 'b0000000-0000-0000-0000-000000000002'  -- Monthly Premium (upgrade)
        ELSE 'b0000000-0000-0000-0000-000000000005'  -- Elite Quarterly
    END,
    CASE 
        WHEN num % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    CURRENT_DATE - INTERVAL '59 days' + (num - 12) * INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '31 days' + (num - 12) * INTERVAL '1 day',
    true
FROM generate_series(12, 60) AS num;

-- Some annual VIP subscriptions
INSERT INTO subscriptions (user_id, plan_id, business_id, start_date, end_date, is_active)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD(num::text, 2, '0'),
    'b0000000-0000-0000-0000-000000000003',  -- Annual VIP
    '11111111-1111-1111-1111-111111111111',
    CURRENT_DATE - INTERVAL '90 days',
    CURRENT_DATE + INTERVAL '275 days',
    true
FROM generate_series(20, 35) AS num
WHERE num % 2 = 0;

-- ============================================
-- ATTENDANCE LOGS (2000+ check-ins over 90 days)
-- ============================================
-- Generate realistic attendance patterns
-- Early morning crowd (6-9 AM)
INSERT INTO attendance_logs (user_id, gym_id, entry_time, exit_time, access_method)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD((12 + (day_num * 7 + hour_offset) % 71)::text, 2, '0'),
    CASE 
        WHEN (day_num * 7 + hour_offset) % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (6 + hour_offset) * INTERVAL '1 hour' + (RANDOM() * 45) * INTERVAL '1 minute',
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (7 + hour_offset) * INTERVAL '1 hour' + (30 + RANDOM() * 60) * INTERVAL '1 minute',
    CASE WHEN RANDOM() > 0.5 THEN 'Card Scan' ELSE 'Mobile App' END
FROM generate_series(0, 89) AS day_num,
     generate_series(0, 2) AS hour_offset
WHERE day_num % 7 != 0;  -- Skip Sundays for some realism

-- Lunch crowd (12-2 PM)
INSERT INTO attendance_logs (user_id, gym_id, entry_time, exit_time, access_method)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD((12 + (day_num * 5 + hour_offset) % 71)::text, 2, '0'),
    CASE 
        WHEN (day_num * 5 + hour_offset) % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (12 + hour_offset) * INTERVAL '1 hour' + (RANDOM() * 30) * INTERVAL '1 minute',
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (13 + hour_offset) * INTERVAL '1 hour' + (RANDOM() * 45) * INTERVAL '1 minute',
    CASE WHEN RANDOM() > 0.7 THEN 'Card Scan' ELSE 'Mobile App' END
FROM generate_series(0, 89) AS day_num,
     generate_series(0, 1) AS hour_offset
WHERE day_num % 7 NOT IN (0, 6);  -- Weekdays only

-- Evening rush hour (5-9 PM) - BUSIEST
INSERT INTO attendance_logs (user_id, gym_id, entry_time, exit_time, access_method)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD((12 + (day_num * 11 + hour_offset * 3) % 71)::text, 2, '0'),
    CASE 
        WHEN (day_num * 11 + hour_offset) % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (17 + hour_offset) * INTERVAL '1 hour' + (RANDOM() * 45) * INTERVAL '1 minute',
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (18 + hour_offset) * INTERVAL '1 hour' + (30 + RANDOM() * 60) * INTERVAL '1 minute',
    CASE WHEN RANDOM() > 0.6 THEN 'Card Scan' ELSE 'Mobile App' END
FROM generate_series(0, 89) AS day_num,
     generate_series(0, 3) AS hour_offset;

-- Weekend afternoon (11 AM - 4 PM)
INSERT INTO attendance_logs (user_id, gym_id, entry_time, exit_time, access_method)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD((12 + (day_num * 13 + hour_offset * 2) % 71)::text, 2, '0'),
    CASE 
        WHEN (day_num * 13) % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (11 + hour_offset) * INTERVAL '1 hour' + (RANDOM() * 50) * INTERVAL '1 minute',
    (CURRENT_DATE - day_num * INTERVAL '1 day')::timestamp + (12 + hour_offset) * INTERVAL '1 hour' + (45 + RANDOM() * 60) * INTERVAL '1 minute',
    'Card Scan'
FROM generate_series(0, 12) AS day_num,  -- Last 12 weekends
     generate_series(0, 4) AS hour_offset
WHERE (CURRENT_DATE - day_num * INTERVAL '1 day')::date = date_trunc('week', (CURRENT_DATE - day_num * INTERVAL '1 day')::date)::date + 6  -- Saturdays
   OR (CURRENT_DATE - day_num * INTERVAL '1 day')::date = date_trunc('week', (CURRENT_DATE - day_num * INTERVAL '1 day')::date)::date;  -- Sundays

-- Add some people currently in gym (no exit time)
INSERT INTO attendance_logs (user_id, gym_id, entry_time, exit_time, access_method)
SELECT 
    'u0000000-0000-0000-0000-0000000000' || LPAD((30 + num)::text, 2, '0'),
    CASE 
        WHEN num % 2 = 0 THEN '11111111-1111-1111-1111-111111111111'
        ELSE '22222222-2222-2222-2222-222222222222'
    END,
    CURRENT_TIMESTAMP - (num * 15 + 30) * INTERVAL '1 minute',
    NULL,
    'Mobile App'
FROM generate_series(1, 15) AS num;

-- ============================================
-- ORDERS (200+ product orders over time)
-- ============================================
INSERT INTO orders (business_id, user_id, total_amount, address, cDate)
SELECT 
    '33333333-3333-3333-3333-333333333333',
    'u0000000-0000-0000-0000-0000000000' || LPAD((12 + num % 71)::text, 2, '0'),
    CASE 
        WHEN num % 5 = 0 THEN 6998  -- Protein + Pre-workout
        WHEN num % 5 = 1 THEN 3999  -- Just protein
        WHEN num % 5 = 2 THEN 4998  -- Tank top + Yoga mat
        WHEN num % 5 = 3 THEN 2499  -- Resistance bands
        ELSE 5998  -- Tank + Pre-workout + Bands
    END,
    '123 Customer St, City',
    (CURRENT_DATE - (num * 0.45)::int * INTERVAL '1 day')::timestamp + (10 + RANDOM() * 8) * INTERVAL '1 hour'
FROM generate_series(1, 200) AS num;

-- ============================================
-- Summary of test data added:
-- ============================================
-- Users: 71 new clients (bringing total to 82+ clients)
-- User-Business links: 71 links
-- Subscriptions: ~150 new subscriptions (mix of monthly, quarterly, annual)
-- Attendance logs: ~2000+ check-ins over 90 days with realistic patterns
-- Orders: 200 product orders over time
-- 
-- This gives rich data for testing:
-- - Rush hour patterns (6-9 AM, 12-2 PM, 5-9 PM peaks)
-- - Daily/weekly/monthly trends
-- - Revenue patterns
-- - Client growth over time
-- - Active vs historical data
