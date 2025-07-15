-- Create exemplaires table
CREATE TABLE exemplaires (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    code VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    location VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Update loans table to reference exemplaires instead of books
ALTER TABLE loans 
ADD COLUMN exemplaire_id BIGINT,
ADD FOREIGN KEY (exemplaire_id) REFERENCES exemplaires(id);

-- Update reservations table to reference exemplaires instead of books
ALTER TABLE reservations 
ADD COLUMN exemplaire_id BIGINT,
ADD FOREIGN KEY (exemplaire_id) REFERENCES exemplaires(id);

-- Create some sample exemplaires for existing books
INSERT INTO exemplaires (book_id, code, status, location) 
SELECT id, CONCAT('EX', LPAD(id, 3, '0'), '-01'), 'AVAILABLE', 'Main Library' FROM books;

INSERT INTO exemplaires (book_id, code, status, location) 
SELECT id, CONCAT('EX', LPAD(id, 3, '0'), '-02'), 'AVAILABLE', 'Main Library' FROM books WHERE id <= 10;

-- Update existing loans to reference exemplaires
-- This assumes each book has at least one exemplaire
UPDATE loans l
JOIN exemplaires e ON e.book_id = l.book_id
SET l.exemplaire_id = e.id
WHERE l.exemplaire_id IS NULL
AND e.id = (
    SELECT MIN(e2.id) 
    FROM exemplaires e2 
    WHERE e2.book_id = l.book_id
);

-- Update existing reservations to reference exemplaires
-- This assumes each book has at least one exemplaire
UPDATE reservations r
JOIN exemplaires e ON e.book_id = r.book_id
SET r.exemplaire_id = e.id
WHERE r.exemplaire_id IS NULL
AND e.id = (
    SELECT MIN(e2.id) 
    FROM exemplaires e2 
    WHERE e2.book_id = r.book_id
);

-- Update exemplaire status for active loans
UPDATE exemplaires e
JOIN loans l ON l.exemplaire_id = e.id
SET e.status = 'BORROWED'
WHERE l.return_date IS NULL;

-- Make exemplaire_id NOT NULL after updating existing records
ALTER TABLE loans MODIFY exemplaire_id BIGINT NOT NULL;
ALTER TABLE reservations MODIFY exemplaire_id BIGINT NOT NULL;

-- Remove the old book_id column from loans table
-- ALTER TABLE loans DROP FOREIGN KEY loans_ibfk_1; -- Adjust constraint name as needed
-- ALTER TABLE loans DROP COLUMN book_id;

-- Remove the old book_id column from reservations table
-- ALTER TABLE reservations DROP FOREIGN KEY reservations_ibfk_1; -- Adjust constraint name as needed
-- ALTER TABLE reservations DROP COLUMN book_id;
