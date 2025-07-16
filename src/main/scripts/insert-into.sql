-- Insert authors
INSERT INTO authors (id, name) VALUES 
(1, 'Victor Hugo'),
(2, 'Albert Camus'),
(3, 'J.K. Rowling');

-- Insert categories
INSERT INTO categories (id, name) VALUES 
(1, 'Littérature classique'),
(2, 'Philosophie'),
(3, 'Jeunesse / Fantastique');

-- Insert books
INSERT INTO books (id, title, author_id) VALUES 
(1, 'Les Misérables', 1),
(2, 'L''Étranger', 2),
(3, 'Harry Potter à l''école des sorciers', 3);

-- Link books to categories
INSERT INTO book_categories (book_id, category_id) VALUES 
(1, 1),
(2, 2),
(3, 3);

-- Insert exemplaires (copies)
INSERT INTO exemplaires (id, code, status, book_id) VALUES 
(1, 'MIS001', 'AVAILABLE', 1),
(2, 'MIS002', 'AVAILABLE', 1),
(3, 'MIS003', 'AVAILABLE', 1),
(4, 'ETR001', 'AVAILABLE', 2),
(5, 'ETR002', 'AVAILABLE', 2),
(6, 'HAR001', 'AVAILABLE', 3);

INSERT INTO peoples (id, name) VALUES 
(1, 'Amine Bensaïd'),
(2, 'Sarah El Khattabi'),
(3, 'Youssef Moujahid'),
(4, 'Nadia Benali'),
(5, 'Karim Haddadi'),
(6, 'Salima Touhami'),
(7, 'Rachid El Mansouri'),
(8, 'Amina Zerouali');

-- Insert membership types (quotas) with unlimited library time
INSERT INTO membership_types (id, name, max_books_allowed_home, max_time_hours_home, 
                            max_books_allowed_library, max_time_hours_library, max_extensions_allowed, punishment_time) VALUES
(1, 'Etudiant', 2, 7*24, 9999999, 9999999, 3, 10*24),       
(2, 'Enseignant', 3, 9*24, 9999999, 9999999, 5, 9*24),     
(3, 'Professionnel', 4, 12*24, 9999999, 9999999, 7, 8*24); 

-- Insert memberships (abonnements)
INSERT INTO memberships (id, start_date, end_date, membership_type_id, people_id) VALUES 
(1, '2025-02-01', '2025-07-24', 1, 1),  -- ETU001 (Amine Bensaïd)
(2, '2025-02-01', '2025-07-01', 1, 2),  -- ETU002 (Sarah El Khattabi)
(3, '2025-04-01', '2025-12-01', 1, 3),  -- ETU003 (Youssef Moujahid)
(4, '2025-07-01', '2026-07-01', 2, 4),  -- ENS001 (Nadia Benali)
(5, '2025-08-01', '2026-05-01', 2, 5),  -- ENS002 (Karim Haddadi)
(6, '2025-07-01', '2026-06-01', 2, 6),  -- ENS003 (Salima Touhami)
(7, '2025-06-01', '2025-12-01', 3, 7),  -- PROF001 (Rachid El Mansouri)
(8, '2024-10-01', '2025-06-01', 3, 8); -- PROF002 (Amina Zerouali)
