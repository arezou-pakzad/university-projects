from connect import *


def login(phone_no):
    res = execute_fetch("select * from driver where phonenumber = %s;", [phone_no])
    return len(res) == 1


def sign_up(fname, lname, phoneNo, image, gender, bankaccount):
    return execute("insert into driver values (%s, %s, %s, %s, %s, %s, %s, %s)",
                   (phoneNo, fname, lname, image, gender, "0.0", bankaccount, "2.5"))


def change_name(no, fname, lname):
    return execute("update driver set firstname = %s, lastname = %s where phonenumber = %s", [fname, lname, no])


def change_bank_account(no, bank_account):
    return execute("update driver set bankaccount = %s where phonenumber = %s", [bank_account, no])


def change_image(no, image):
    return execute("update driver set image = %s where phonenumber = %s", [image, no])


def have_car(no):
    return 1 == len(execute_fetch("select * from downv natural join vehicle where phonenumber = %s and legalinformation = true ", [no]))


