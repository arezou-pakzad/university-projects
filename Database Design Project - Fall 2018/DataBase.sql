-- Create Domain
CREATE extension citext;
CREATE domain email_domain as citext CHECK ( value ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );
CREATE domain phone_number_domain as CHAR(11) CHECK(value not like '%[^0-9]%' and value like '09%' );


-- Create table
CREATE TABLE PASSENGER (
	phoneNumber phone_number_domain,
	firstName  	CHAR(15),
	lastName   	CHAR(15),
    sex			CHAR(1),
	birthDate 	DATE,
	currency 	NUMERIC(4,1) DEFAULT 0,
	email		email_domain,
  	PRIMARY KEY (phoneNumber),
	CHECK(sex IN ('M', 'F')));

CREATE TABLE DRIVER (
	phoneNumber phone_number_domain,
	firstName  	CHAR(15),
	lastName   	CHAR(15),
	image 		bytea,
    sex			CHAR(1),
	currency 	NUMERIC(4,1) DEFAULT 0,
	bankAccount	NUMERIC(16,0),
	score		NUMERIC(4,4) DEFAULT 0,
  	PRIMARY KEY (phoneNumber),
	CHECK(sex IN ('M', 'F')));

CREATE TABLE VEHICLE (
	licensePlate 	CHAR(11)	NOT NULL,
	vehicleType  	CHAR(15)	NOT NULL,
	color   		CHAR(15)	NOT NULL,
	legalInformation	BOOLEAN	NOT NULL,
  	PRIMARY KEY (licensePlate));

CREATE TABLE DOWNV (
	phoneNumber 	phone_number_domain,
	licensePlate 	CHAR(11),
  	PRIMARY KEY (phoneNumber,licensePlate),
	FOREIGN KEY (phoneNumber) REFERENCES DRIVER(phoneNumber)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (licensePlate) REFERENCES VEHICLE(licensePlate)
	ON DELETE CASCADE
	ON UPDATE CASCADE);

CREATE TABLE TRAVEL (
	travelCode 				CHAR(11),
	driverPhoneNumber 		phone_number_domain,
	passengerPhoneNumber 	phone_number_domain,
	origin					point	NOT NULL,
	destination				point	NOT NULL,
	driverScore				NUMERIC(4,4),
	travelCost				NUMERIC(2,1),
	travelType				VARCHAR(20)	NOT NULL,
	travelSituation			VARCHAR(30) DEFAULT 'On the way to the origin',
	onlinePeyment			BOOLEAN		DEFAULT false,
	discountCode			CHAR(15),
	travelDate				DATE		NOT NULL,
	startTime				time	NOT NULL,
	finishTime				time,
	CHECK(travelType IN ('economic', 'luxury', 'female driver', 'motorcycle')),
	CHECK(travelSituation IN ('finished', 'canceled', 'On the way to the origin', 'Waiting for passenger', 'On the way to the destination')),
  	PRIMARY KEY (travelCode));

CREATE TABLE TRANSACTIONs(
	transactionCode 	CHAR(11),
	date				DATE,
	time				time,
	cost				NUMERIC(2,1),
  	PRIMARY KEY (transactionCode));

CREATE TABLE CHARGE (
	passengerPhoneNumber 	phone_number_domain,
	transactionCode 	CHAR(11),
  	PRIMARY KEY (passengerPhoneNumber,transactionCode),
	FOREIGN KEY (passengerPhoneNumber) REFERENCES PASSENGER(phoneNumber)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (transactionCode) REFERENCES TRANSACTIONs(transactionCode)
	ON DELETE CASCADE
	ON UPDATE CASCADE);

CREATE TABLE LIQUIDATION (
	driverPhoneNumber 	phone_number_domain,
	transactionCode 	CHAR(11),
	increase_decrease	BOOLEAN,
  	PRIMARY KEY (driverPhoneNumber,transactionCode),
	FOREIGN KEY (driverPhoneNumber) REFERENCES DRIVER(phoneNumber)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (transactionCode) REFERENCES TRANSACTIONs(transactionCode)
	ON DELETE CASCADE
	ON UPDATE CASCADE);

CREATE TABLE FAVOURITE_LOCATIONS (
	passengerPhoneNumber 	phone_number_domain,
	locationName			varCHAR(20),
	loc 					point,
	address					varCHAR(100),
  	PRIMARY KEY (passengerPhoneNumber,locationName),
	FOREIGN KEY (passengerPhoneNumber) REFERENCES PASSENGER(phoneNumber)
	ON DELETE CASCADE
	ON UPDATE CASCADE);


-- Insert
INSERT INTO PASSENGER VALUES
('09355893669', 'Alireza', 'Ziabari', 'M', '1998-02-17', 0 , 'alireza.ziabari@gmail.com'),
('09031479910', 'Javad', 'Abdi', 'M', '1998-03-17', 0 , 'abdijavad110@gmail.com'),
('09123456789', 'Arezou', 'Pakzad', 'F', '1998-04-17', 0 , 'arezou.pakzad@gmail.com');


-- Trigger

-- for changing how to pay
CREATE FUNCTION change_to_online_payment_function()
  RETURNS TRIGGER AS $change_to_online_payment$
BEGIN
  IF (select currency from passenger where phoneNumber like new.passengerPhoneNumber) < new.travelCost
  THEN
    RAISE 'You are not permitted for changing the way of peyment';
  END IF;
  RETURN NEW;
END
$change_to_online_payment$ LANGUAGE plpgsql;

CREATE TRIGGER change_to_online_payment
BEFORE
update ON travel
FOR EACH ROW
EXECUTE PROCEDURE change_to_online_payment_function();

-- peyment of online travel
CREATE FUNCTION travel_payment_online_function()
  RETURNS TRIGGER AS $travel_online_payment$
BEGIN
  IF new.onlinePeyment is true and new.travelSituation like 'finished'
  THEN
    UPDATE PASSENGER set currency = currency - new.travelCost where phoneNumber like new.passengerPhoneNumber;
	UPDATE DRIVER set currency = currency + 0.95 * new.travelCost where phoneNumber like new.driverPhoneNumber;
  END IF;
  RETURN NEW;
END
$travel_online_payment$ LANGUAGE plpgsql;

CREATE TRIGGER travel_online_payment
BEFORE
update ON travel
FOR EACH ROW
EXECUTE PROCEDURE travel_payment_online_function();

-- peyment of offline travel
CREATE FUNCTION travel_payment_offline_function()
  RETURNS TRIGGER AS $travel_offline_payment$
BEGIN
  IF new.onlinePeyment is false and new.travelSituation like 'finished'
  THEN
	UPDATE DRIVER set currency = currency - 0.05 * new.travelCost where phoneNumber like new.driverPhoneNumber;
  END IF;
  RETURN NEW;
END
$travel_offline_payment$ LANGUAGE plpgsql;

CREATE TRIGGER travel_offline_payment
BEFORE
update ON travel
FOR EACH ROW
EXECUTE PROCEDURE travel_payment_offline_function();

-- passenger charge account when their have a travel
CREATE FUNCTION charge_account_function()
  RETURNS TRIGGER AS $charge_account$
BEGIN
  IF EXISTS (select * from travel WHERE passengerPhoneNumber = new.phoneNumber and travelSituation not like 'finished' and travelCost < new.currency)
  THEN
	UPDATE travel set onlinePeyment = true where passengerPhoneNumber like new.phoneNumber and travelSituation not like 'finished';
END IF;
RETURN NEW;
END
$charge_account$ LANGUAGE plpgsql;

CREATE TRIGGER charge_account
AFTER
update ON PASSENGER
FOR EACH ROW
EXECUTE PROCEDURE charge_account_function();

-- when passenger have more money than travel cost, the way of payment set to online 
CREATE FUNCTION change_way_of_payment_function()
  RETURNS TRIGGER AS $change_way_of_payment$
BEGIN
  IF (select currency from passenger where phoneNumber like new.passengerPhoneNumber) > new.travelCost
  THEN
	UPDATE travel set onlinePeyment = true where travelCode = new.travelCode;
END IF;
RETURN NEW;
END
$change_way_of_payment$ LANGUAGE plpgsql;

CREATE TRIGGER change_way_of_payment
AFTER of
insert ON Travel
FOR EACH ROW
EXECUTE PROCEDURE change_way_of_payment_function();

-- Assertion
CREATE ASSERTION phone_number_checking_passenger
	CHECK NOT EXISTS (SELECT phuneNumber FROM PASSENGER WHERE phuneNumber.length < 8);

CREATE ASSERTION phone_number_checking_driver
	CHECK NOT EXISTS (SELECT phuneNumber FROM DRIVER WHERE phuneNumber.length < 8);
