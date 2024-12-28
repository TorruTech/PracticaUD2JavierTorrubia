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
    labels JSON,
    location ENUM('Madrid', 'Barcelona', 'Valencia', 'Sevilla', 'Zaragoza') NOT NULL,
    image VARCHAR(255)
);
--
CREATE TABLE IF NOT EXISTS activities (
    id_activity INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration FLOAT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
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
    ADD CONSTRAINT fk_events_category FOREIGN KEY (id_category) REFERENCES categories(id_category);
--
ALTER TABLE activities
    ADD CONSTRAINT fk_activities_event FOREIGN KEY (id_event) REFERENCES events(id_event);
--
ALTER TABLE reservations
    ADD CONSTRAINT fk_reservations_user FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE,
    ADD CONSTRAINT fk_reservations_event FOREIGN KEY (id_activity) REFERENCES activities(id_activity) ON DELETE CASCADE;
--
CREATE FUNCTION existsUserByDni(f_dni VARCHAR(9))
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
CREATE FUNCTION existsEventByName(f_name VARCHAR(100))
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
CREATE FUNCTION existsActivityByName(f_name VARCHAR(100))
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
