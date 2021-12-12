--
-- db2 -td"@" -f P2.clp
--
CONNECT TO CS157A@
--
--
DROP PROCEDURE P2.CUST_CRT@
DROP PROCEDURE P2.CUST_LOGIN@
DROP PROCEDURE P2.ACCT_OPN@
DROP PROCEDURE P2.ACCT_CLS@
DROP PROCEDURE P2.ACCT_DEP@
DROP PROCEDURE P2.ACCT_WTH@
DROP PROCEDURE P2.ACCT_TRX@
DROP PROCEDURE P2.ADD_INTEREST@
DROP PROCEDURE P2.IsOwned@
--
--create customer
CREATE PROCEDURE P2.CUST_CRT
(IN p_name CHAR(15), IN p_gender CHAR(1), IN p_age INTEGER, IN p_pin INTEGER, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE pinCode INTEGER;
    IF p_gender != 'M' AND p_gender != 'F' THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid gender';
    ELSEIF p_age <= 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid age';
    ELSEIF p_pin < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid pin!!!';
    ELSE
      SET pinCode = p2.encrypt(p_pin);
      INSERT INTO P2.Customer (Name, Gender, Age, Pin) VALUES (p_name, p_gender, p_age, pinCode);
      SET id = IDENTITY_VAL_LOCAL();
      SET err_msg = '-';
      SET sql_code = 0;
    END IF;
  END@
--
--customer login
CREATE PROCEDURE P2.CUST_LOGIN
(IN p_id INTEGER, IN p_pin INTEGER, OUT valid INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
        DECLARE pinCode INTEGER;
        SET pinCode = (SELECT pin FROM p2.customer WHERE ID = p_id);
        IF EXISTS(SELECT * FROM p2.customer WHERE ID=p_id) AND (P2.decrypt(pinCode) = p_pin) THEN
            SET valid = 1;
            SET sql_code = 0;
            SET err_msg = '-';
        ELSE
            SET valid = 0;
            SET sql_code = -100;
            SET err_msg = 'Incorrect id or pin';
        END IF;
    END@
--
--open account
CREATE PROCEDURE P2.ACCT_OPN
(IN p_id INTEGER, IN p_balance INTEGER, IN p_type CHAR(1), OUT number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    IF NOT EXISTS (SELECT * FROM P2.Customer WHERE ID = p_id) THEN
       SET sql_code = -100;
       SET err_msg = 'Invalid customer id';
    ELSEIF p_balance < 0 THEN
       SET sql_code = -100;
       SET err_msg = 'Invalid balance';
    ELSEIF p_type != 'S' AND p_type != 'C' THEN
       SET sql_code = -100;
       SET err_msg = 'Invalid type';
    ELSE
       INSERT INTO P2.account (ID, Balance, Type, Status) VALUES (p_id, p_balance, p_type, 'A');
       SET number = IDENTITY_VAL_LOCAL();
       SET sql_code = 0;
       SET err_msg = '-';
    END IF;
  END@
--
--close account
CREATE PROCEDURE P2.ACCT_CLS
(IN p_number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
        IF NOT EXISTS (SELECT * FROM p2.account WHERE number = p_number) THEN
           SET sql_code = -100;
           SET err_msg = 'Invalid account number';
        ELSE
           UPDATE p2.account set balance=0, status='I' where number= p_number;
           SET sql_code = 0;
           SET err_msg = '-';
        END IF;
    END@
--
--deposit into account
CREATE PROCEDURE P2.ACCT_DEP
(IN p_number INTEGER, IN p_amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
        IF NOT EXISTS(SELECT * FROM p2.account WHERE number = p_number AND status = 'A') THEN
            SET sql_code = -100;
            SET err_msg = 'Invalid account number';
        ELSEIF p_amt < 0 THEN
            SET sql_code = -100;
            SET err_msg = 'Invalid amount';
        ELSE
            UPDATE p2.account SET balance = balance + p_amt WHERE number = p_number;
            SET sql_code = 0;
            SET err_msg = '-';
        END IF;
    END@
--
--withdraw from account
CREATE PROCEDURE P2.ACCT_WTH
(IN p_number INTEGER, IN p_amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
        IF NOT EXISTS(SELECT * FROM p2.account WHERE number = p_number AND status = 'A') THEN
            SET sql_code = -100;
            SET err_msg = 'Invalid account number';
        ELSEIF p_amt < 0 THEN
            SET sql_code = -100;
            SET err_msg = 'Invalid amount';
        ELSEIF NOT EXISTS(SELECT * FROM p2.account WHERE number=p_number AND status='A' AND balance>=p_amt) THEN
            SET sql_code = -100;
            SET err_msg = 'Not enough funds';
        ELSE
            UPDATE p2.account SET balance = balance - p_amt WHERE number = p_number;
            SET sql_code = 0;
            SET err_msg = '-';
        END IF;
    END@
--
--transfer to another account
CREATE PROCEDURE P2.ACCT_TRX
(IN src_acct INTEGER, IN dest_acct INTEGER, IN p_amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
--        IF NOT EXISTS(SELECT * FROM p2.account WHERE number = src_acct AND status = 'A') THEN
--             SET sql_code = -100;
--             SET err_msg = 'Invalid source account number';
--        ELSEIF NOT EXISTS(SELECT * FROM p2.account WHERE number = dest_acct AND status = 'A') THEN
--             SET sql_code = -100;
--             SET err_msg = 'Invalid destination account number';
--        ELSEIF NOT (SELECT balance FROM p2.account WHERE number = src_acct AND status = 'A') >= p_amt THEN
--             SET sql_code = -100;
--             SET err_msg = 'Not enough funds';
--        ELSE
--             UPDATE p2.account SET balance=balance - p_amt where number= src_acct;
--             UPDATE p2.account SET balance=balance + p_amt where number= dest_acct;
--             SET sql_code = 0;
--             SET err_msg = '-';
--        END IF;
        CALL P2.ACCT_WTH(src_acct, p_amt, sql_code, err_msg);
        CALL P2.ACCT_DEP(dest_acct, p_amt, sql_code, err_msg);
    END@
--
--interest
CREATE PROCEDURE P2.ADD_INTEREST
(IN savings_rate REAL, IN checking_rate REAL, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
         UPDATE p2.account SET balance=balance * (1+savings_rate) WHERE type='S' AND status='A';
         UPDATE p2.account SET balance=balance * (1+checking_rate) WHERE type='C' AND status='A';
         SET sql_code = 0;
         SET err_msg = '-';
    END@
--
--to check whether the customer owner this account
CREATE PROCEDURE P2.IsOwned
(IN p_id INTEGER, IN p_number INTEGER, OUT is_owned INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
    BEGIN
        IF EXISTS(SELECT * FROM p2.account WHERE id=p_id AND number=p_number) THEN
            SET is_owned = 1;
            SET sql_code = 0;
            SET err_msg = '-';
        ELSE
            SET is_owned = 0;
            SET sql_code = -100;
            SET err_msg = 'The customer ID does not owned this account.';
        END IF;
    END@
--
--
TERMINATE@
--
--
