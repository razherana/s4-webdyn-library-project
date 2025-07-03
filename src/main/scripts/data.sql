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