-- Database I - ITEC 340 Spring 2018
-- Ski Club Schema

SET ECHO ON;
set serveroutput on;

DROP table Payment;
DROP TABLE Condo_Assign;
DROP TABLE Condo_Reservation;
DROP TABLE SkiClub;
DROP TABLE Trip;

CREATE TABLE Trip
(
	TID	NUMBER
,	Resort	VARCHAR2(25) not null
,	Sun_Date date
,	City varchar2(15)
,	State varchar2(15)
,   CONSTRAINT Trip_PK Primary Key (TID)
);

CREATE TABLE Condo_Reservation
(
	RID varchar(5)
,   TID NUMBER
,	Name	VARCHAR2(30) not null
,	Unit_NO NUMBER
,   Bldg NUMBER
,   Gender char(1) not null
,	CONSTRAINT RID_PK PRIMARY KEY(RID)
,	CONSTRAINT Trip_Res Foreign Key (TID) references Trip 
,   Constraint Gen_CK Check (Gender in ('M', 'F'))
);

CREATE TABLE SkiClub
(
	MID			NUMBER
,	First        VARCHAR2(16) not null
,	Last   	    Varchar2(20) not null
,	Exp_Level   Char(1)
,   Gender      Char(1) not null
,	CONSTRAINT  SkiClub_PK PRIMARY KEY(MID)
,	CONSTRAINT  ELevel_CK_Ski CHECK(Exp_Level in ('B', 'I', 'E'))
,   CONSTRAINT  Gen_SkiClub_CK Check (Gender in ('M', 'F'))
);

CREATE TABLE Condo_Assign
(
	MID NUMBER
,   RID varchar2(5)
,	CONSTRAINT  Condo_Assign_PK PRIMARY KEY(MID, RID)
,	CONSTRAINT  Condo_Res_FK FOREIGN KEY (RID) REFERENCES Condo_Reservation
,   CONSTRAINT  Ski_Mem_FK Foreign key (MID) references SkiClub
);

CREATE TABLE Payment
(
	MID NUMBER
,   RID varchar2(5)
,   PaymentDate date
,	Payment decimal(5,2) 
,	CONSTRAINT  Payment_Ski_PK PRIMARY KEY(MID, RID, PaymentDate)
,	CONSTRAINT  Trip_Pay_FK FOREIGN KEY (MID, RID) REFERENCES Condo_Assign
,   CONSTRAINT  Ski_Mem_Pay_FK Foreign key (MID) references SkiClub
);

CREATE TABLE reserveError
(
	errorNumber number,
	MID NUMBER,
	RID NUMBER,
	errorDate date,
	errorCode varchar2(30),
	errorMessage varchar2(200)	
);

DROP SEQUENCE Error_seq;

CREATE SEQUENCE Error_seq
MINVALUE 1
START WITH 1
INCREMENT BY 1
CACHE 10;

CREATE OR REPLACE TRIGGER gender_Match_Trigger
	BEFORE INSERT ON Condo_Assign
	FOR EACH ROW
DECLARE
	aMemGender Char(1);
	aRoomGender Char(1);
BEGIN
	select Gender into aMemGender from SkiClub Where MID = :new.MID;
	select Gender into aRoomGender from Condo_Reservation Where RID = :new.RID;
	IF (aMemGender != aRoomGender) then
		INSERT INTO reserveError (errorNumber, MID, RID, errorDate, errorCode, errorMessage)
		VALUES (Error_seq.nextVal, :new.MID, :new.RID, SYSDATE, '-20098', 'Incompatible genders.');
	END IF;
END gender_Match_Trigger;
/

CREATE OR REPLACE TRIGGER check_Payment
	AFTER INSERT ON Condo_Assign
	FOR EACH ROW
DECLARE
	aMemPayment = Payment.Payment%TYPE; 
BEGIN
	aMemPayment := (SELECT SUM(Payment) FROM Payment WHERE MID = :new.MID);
	if (aMemPayment >= 150) then
	{
		INSERT INTO reserveError VALUES(Error_seq.nextVal, :new.MID, :new.RID, CURRENT_DATE, '21000', 'Member exceeds the $150 limit');
	}
	end if;
END check_Payment;
/

CREATE OR REPLACE TRIGGER check_Beds
	AFTER INSERT ON Condo_Assign
	FOR Each ROW
DECLARE
	number aBedAmt; 
BEGIN
	aBedAmt := (SELECT COUNT(RID) FROM Condo_Assign WHERE RID = :old.RID); 
	if aBedAmt > 4 then
		INSERT INTO reserveError VALUES (Error_seq.nextVal, :new.MID, :new.RID, CURRENT_DATE, '21000', 'Member exceeds the $150 limit');
	end if;
END check_Beds;
/

CREATE OR REPLACE PROCEDURE addTrip 
(aTID in Trip.TID%TYPE,
 aResort in Trip.Resort%TYPE,
 aDate in Trip.Sun_Date%TYPE,
 aCity in Trip.City%TYPE,
 aState in Trip.State%Type) IS
BEGIN
INSERT INTO Trip (TID, Resort, Sun_Date, City, State)
VALUES (aTID, aResort, aDate, aCity, aState);
dbms_output.put_line('Added a row to the table');
EXCEPTION
WHEN OTHERS THEN dbms_output.put_line('Failed to add a row to the table');
END;
/ 

CREATE OR REPLACE PROCEDURE addCondo
(aRID in Condo_Reservation.RID%TYPE,
 aTID in Condo_Reservation.TID%TYPE,
 aName in Condo_Reservation.Name%TYPE,
 aUnitNO in Condo_Reservation.Unit_NO%TYPE,
 aBldg in Condo_Reservation.Bldg%TYPE,
 aGender in Condo_Reservation.Gender%TYPE) IS
BEGIN
INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES (aRID, aTID, aName, aUnitNO, aBldg, aGender);
dbms_output.put_line('Added a row to the table');
EXCEPTION
WHEN OTHERS THEN dbms_output.put_line('Failed to add a row to the table');
END;
/

CREATE OR REPLACE PROCEDURE addSkiClub
(aMID in SkiClub.MID%TYPE,
 fName in SkiClub.First%TYPE,
 lName in SkiClub.Last%TYPE,
 aLevel in SkiClub.Exp_Level%TYPE,
 aGender in SkiClub.Gender%TYPE) IS
BEGIN
INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (aMID, fName, lName, aLevel, aGender);
dbms_output.put_line('Added a row to the table');
EXCEPTION
WHEN OTHERS THEN dbms_output.put_line('Failed to add a row to the table');
END;
/

CREATE OR REPLACE PROCEDURE addCondo_Assign
(aMID in Condo_Assign.MID%TYPE,
 aRID in Condo_Assign.RID%TYPE) IS
BEGIN
INSERT INTO Condo_Assign (MID, RID)
VALUES (aMID, aRID);
dbms_output.put_line('Added a row to the table');
EXCEPTION
WHEN OTHERS THEN dbms_output.put_line('Failed to add a row to the table');
END;
/ 

CREATE OR REPLACE PROCEDURE pr_insertPayment
(aMID in Payment.MID%TYPE,
 aRID in Payment.RID%TYPE,
 aDate in Payment.PaymentDate%TYPE,
 anAmnt in Payment.Payment%TYPE) IS
BEGIN
INSERT INTO Payment (MID, RID, PaymentDate, Payment)
VALUES (aMID, aRID, aDate, anAmnt);
dbms_output.put_line('Added a row to the table');
EXCEPTION
WHEN OTHERS THEN dbms_output.put_line('Failed to add a row to the table');
END;
/ 

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (100, 'John', 'Snyder', 'I', 'M');

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (600, 'Sally', 'Treville', 'E', 'F');

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (102, 'Gerald', 'Warner', 'I', 'M');

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (104, 'Katie', 'Johnson', 'I', 'F');

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (601, 'Matt', 'Kingston', 'E', 'M');

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (108, 'Tom', 'Rivers', 'I', 'M');

INSERT INTO SkiClub (MID, First, Last, Exp_Level, Gender)
VALUES (109, 'Tom', 'Singleton', 'E', 'M');


INSERT INTO Trip (TID, Resort, Sun_Date, City, State)
VALUES (1, 'Copper Mtn', '21-Jan-18', 'Copper', 'CO');

INSERT INTO Trip (TID, Resort, Sun_Date, City, State)
VALUES (2, 'Heavenly Mtn', '28-Jan-18', 'Lake Tahoo', 'CA');

INSERT INTO Trip (TID, Resort, Sun_Date, City, State)
VALUES (3, 'Squaw Valley', '4-Feb-18', 'Lake Tahoo', 'CA');

INSERT INTO Trip (TID, Resort, Sun_Date, City, State)
VALUES (4, 'Taos Ski Valley', '11-Feb-18', 'Taos', 'NM');


INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R10', 1, 'Lewis Ranch', 320, 3, 'M');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R11', 1, 'Lewis Ranch', 321, 3, 'F');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R12', 2, 'Heavenly Village', 304, 2, 'M');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R13', 2, 'Heavenly Village', 284, 1, 'F');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R14', 3, 'South Shore', 262, 1, 'M');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R15', 3, 'South Shore', 263, 4, 'F');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R16', 4, 'Cozy Mtn', 301, 3, 'F');

INSERT INTO Condo_Reservation (RID, TID, Name, Unit_NO, Bldg, Gender)
VALUES ('R17', 4, 'Cozy Mtn', 302, 3, 'M');


INSERT INTO Condo_Assign (MID, RID)
VALUES (100, 'R10');

INSERT INTO Condo_Assign (MID, RID)
VALUES (600, 'R11');

INSERT INTO Condo_Assign (MID, RID)
VALUES (102, 'R10');

INSERT INTO Condo_Assign (MID, RID)
VALUES (104, 'R11');

INSERT INTO Condo_Assign (MID, RID)
VALUES (601, 'R10');

INSERT INTO Condo_Assign (MID, RID)
VALUES (600, 'R13');

INSERT INTO Condo_Assign (MID, RID)
VALUES (104, 'R13');

INSERT INTO Condo_Assign (MID, RID)
VALUES (601, 'R12');

INSERT INTO Condo_Assign (MID, RID)
VALUES (108, 'R12');

INSERT INTO Condo_Assign (MID, RID)
VALUES (109, 'R13');

INSERT INTO Condo_Assign (MID, RID)
VALUES (600, 'R15');

INSERT INTO Condo_Assign (MID, RID)
VALUES (104, 'R15');

INSERT INTO Condo_Assign (MID, RID)
VALUES (108, 'R14');

INSERT INTO Condo_Assign (MID, RID)
VALUES (601, 'R14');

INSERT INTO Condo_Assign (MID, RID)
VALUES (102, 'R14');

INSERT INTO Condo_Assign (MID, RID)
VALUES (600, 'R16');

INSERT INTO Condo_Assign (MID, RID)
VALUES (104, 'R16');

INSERT INTO Condo_Assign (MID, RID)
VALUES (109, 'R16');

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(100, 'R10', '3-Jan-18', 100.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(600, 'R11', '4-Jan-18', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(600, 'R11', '10-Jan-18', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(102, 'R10', '28-Dec-17', 75.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(104, 'R11', '21-Dec-17', 75.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(104, 'R11', '28-Dec-17', 25.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(601, 'R10', '3-Jan-18', 75.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(600, 'R13', '15-Dec-17', 100.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(104, 'R13', '14-Dec-17', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(601, 'R12', '30-Dec-17', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(108, 'R12', '3-Dec-17', 75.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(109, 'R13', '24-Dec-17', 100.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(600, 'R15', '2-Jan-18', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(104, 'R15', '3-Jan-18', 75.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(108, 'R14', '8-Jan-18', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(601, 'R14', '2-Dec-17', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(102, 'R14', '8-Dec-17', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(600, 'R16', '5-Jan-18', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(104, 'R16', '8-Jan-18', 50.00);

Insert into Payment (MID, RID, PaymentDate, Payment)
Values(109, 'R16', '30-Dec-17', 50.00);

 

select * from Trip;
select * from SkiClub;
select * from Condo_Reservation;
select * from Condo_Assign;
select * from Payment;

execute addTrip (5, 'Slippery Slopes', '15-Jan-19', 'Seattle', 'Washington');
execute addCondo ('R20', 5, 'Beat-up Shack', 306, 15, 'M');
execute addSkiClub (117, 'Rick', 'Lee', 'I', 'M');
execute addCondo_Assign (117, 'R20');
execute pr_insertPayment (117, 'R20', '25-Dec-18', 68.00);

select * from Trip;
select * from SkiClub;
select * from Condo_Reservation;
select * from Condo_Assign;
select * from Payment;



show errors;
show exceptions;