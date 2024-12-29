CREATE DATABASE IF NOT EXISTS eventsdb;
--
USE eventsdb;
--
CREATE TABLE IF NOT EXISTS categories (
    id_category INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);
--
CREATE TABLE IF NOT EXISTS events (
    id_event INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    id_category INT NOT NULL,
    attendees INT NOT NULL,
    labels JSON,
    location ENUM('Madrid', 'Barcelona', 'Valencia', 'Sevilla', 'Zaragoza',
    'Tarragona', 'Girona', 'Lleida', 'Palma') NOT NULL,
    image VARCHAR(255)
);
--
CREATE TABLE IF NOT EXISTS activities (
    id_activity INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(100) NOT NULL,
    duration FLOAT NOT NULL,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    vacants INT NOT NULL,
    id_event INT NOT NULL
);
--
CREATE TABLE IF NOT EXISTS users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    dni VARCHAR(9) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    birthdate DATE NOT NULL
);
--
CREATE TABLE IF NOT EXISTS reservations (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    id_activity INT NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
--
ALTER TABLE events
    ADD FOREIGN KEY (id_category) REFERENCES categories(id_category);
--
ALTER TABLE activities
    ADD FOREIGN KEY (id_event) REFERENCES events(id_event);
--
ALTER TABLE reservations
    ADD FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE,
    ADD FOREIGN KEY (id_activity) REFERENCES activities(id_activity) ON DELETE CASCADE;
--
INSERT INTO categories (name, description) VALUES
    ('Cultural', 'Eventos culturales'),
    ('Cientifico', 'Eventos científicos'),
    ('Tecnológico', 'Eventos tecnológicos'),
    ('Divulgativo', 'Eventos divulgativos'),
    ('Tecnológico', 'Eventos tecnológicos');
--
CREATE FUNCTION IF NOT EXISTS existsUserByDni(f_dni VARCHAR(9))
RETURNS BIT
BEGIN
    DECLARE user_exists INT;
    SET user_exists = (SELECT COUNT(*) FROM users WHERE dni = f_dni);

    IF user_exists > 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
--
CREATE FUNCTION IF NOT EXISTS existsUserByEmail(f_email VARCHAR(50))
RETURNS BIT
BEGIN
    DECLARE user_exists INT;
    SET user_exists = (SELECT COUNT(*) FROM users WHERE email = f_email);

    IF user_exists > 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
--
CREATE FUNCTION IF NOT EXISTS existsEventByName(f_name VARCHAR(100))
RETURNS BIT
BEGIN
    DECLARE event_exists INT;
    SET event_exists = (SELECT COUNT(*) FROM events WHERE name = f_name);

    IF event_exists > 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
--
CREATE FUNCTION  IF NOT EXISTS existsActivityByName(f_name VARCHAR(100))
RETURNS BIT
BEGIN
    DECLARE activity_exists INT;
    SET activity_exists = (SELECT COUNT(*) FROM activities WHERE name = f_name);

    IF activity_exists > 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
