from connect import *


def login(phone_no):
    res = execute_fetch("select * from passenger where phonenumber = %s;", [phone_no])
    return len(res) == 1


def sign_up(fname, lname, phoneNo, email, birthdate, gender):
    return execute("insert into passenger values (%s, %s, %s, %s, %s, %s, %s)",
                   (phoneNo, fname, lname, gender, birthdate, "0", email))
