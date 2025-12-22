CREATE DATABASE lateral_test;
\connect lateral_test

CREATE TABLE Customers (
   customer_id INTEGER PRIMARY KEY,
   name VARCHAR(100) NOT NULL
);

CREATE TABLE Orders (
    order_id INTEGER PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    order_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

INSERT INTO Customers (customer_id, name) VALUES
(101, 'Alice'),
(102, 'Bob'),
(103, 'Charlie'); -- Charlie has no orders

INSERT INTO Orders (order_id, customer_id, order_date, amount) VALUES
(1001, 101, '2023-10-01', 50.00),
(1002, 102, '2023-10-05', 120.00),
(1003, 101, '2023-10-15', 75.50), -- Alice's most recent order
(1004, 102, '2023-10-10', 30.00),
(1005, 102, '2023-11-01', 90.00); -- Bob's most recent order

-- ALTER TABLE Orders ADD COLUMN items jsonb;
--
-- -- sample items: arrays of objects or simple strings
-- UPDATE Orders SET items = '[
--   {"sku":"A1","name":"Widget","qty":1},
--   {"sku":"A2","name":"Gadget","qty":2}
-- ]' WHERE order_id = 1001;  -- Alice's older order, two items
--
-- UPDATE Orders SET items = '[
--   {"sku":"B1","name":"Thing","qty":3}
-- ]' WHERE order_id = 1003;  -- Alice's most recent order, 1 item
--
-- UPDATE Orders SET items = '[
--   {"sku":"C1","name":"Foo","qty":1},
--   {"sku":"C2","name":"Bar","qty":1},
--   {"sku":"C3","name":"Baz","qty":5}
-- ]' WHERE order_id = 1002;  -- Bob
-- -- leave some orders NULL or empty array to test OUTER behavior


CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE Roads (
   road_id INTEGER PRIMARY KEY,
   name VARCHAR(100),
   geometry GEOMETRY(LINESTRING, 4326)
);

INSERT INTO Roads (road_id, name, geometry) VALUES
    (1, 'Main Street', ST_GeomFromText('LINESTRING(10 10, 20 20, 30 15)', 4326)),
    (2, 'Oak Avenue', ST_GeomFromText('LINESTRING(5 5, 15 10, 25 5, 35 10)', 4326)),
    (3, 'Point Road', ST_GeomFromText('LINESTRING(0 0, 1 1)', 4326));

CREATE TABLE Documents (
   doc_id INTEGER PRIMARY KEY,
   title VARCHAR(100),
   tags TEXT[]  -- PostgreSQL array type
);

INSERT INTO Documents (doc_id, title, tags) VALUES
    (1, 'AI Research Paper', ARRAY['AI', 'Machine Learning', 'Neural Networks']),
    (2, 'Database Tutorial', ARRAY['SQL', 'PostgreSQL', 'Indexing']),
    (3, 'Empty Document', ARRAY[]::TEXT[]);

COMMIT;