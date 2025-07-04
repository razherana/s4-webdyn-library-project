-- Active: 1738011862925@@127.0.0.1@3306@library
INSERT INTO
    `authors` (`name`)
VALUES ('Razafindratandra Jean'),
    ('Dupont Marie'),
    ('Rakoto David'),
    ('Smith John'),
    ('Andrianaivo Pierre'),
    ('Martin Sophie'),
    ('Rabe Paul'),
    ('Johnson Emily'),
    ('Rakotonanahary Julie'),
    ('Tremblay Luc'),
    ('Williams Robert'),
    ('Brown Linda'),
    ('Jones Michael'),
    ('Davis Jennifer'),
    ('Miller Christopher'),
    ('Wilson Jessica'),
    ('Moore Matthew'),
    ('Taylor Ashley'),
    ('Anderson Joshua'),
    ('Thomas Brittany');

INSERT INTO
    `categories` (`name`)
VALUES ('Fiction'),
    ('Non-Fiction'),
    ('Science Fiction'),
    ('Fantasy'),
    ('Mystery'),
    ('Romance'),
    ('Horror'),
    ('Biography'),
    ('History'),
    ('Self-Help');

INSERT INTO
    membership_types (`name`)
VALUES ('Student'),
    ('Teacher'),
    ('Staff'),
    ('Visitor');

INSERT INTO
    `reservation_status_types` (`name`)
VALUES ('Pending'),
    ('Approved');

INSERT INTO
    punishment_types (`name`)
VALUES ('Warning'),
    ('Fine'),
    ('Suspension'),
    ('Ban');

INSERT INTO
    roles (`name`, `description`)
VALUES (
        'admin',
        'Manages all aspects of the library system, including user management, book management, and system settings.'
    ),
    (
        'librarian',
        'Manages books, approves reservations, and handles day-to-day library operations.'
    );

-- Add sample users
INSERT INTO
    users (username, password)
VALUES ('admin', 'admin123'),
    ('librarian', 'librarian123'),
    ('member', 'member123');

INSERT INTO users_roles (user_id, role_id) VALUES 
(1, 1),
(2, 2);