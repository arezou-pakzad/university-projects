from driver_connections import *
import base64
import pickle
from driver_client import driver_client


def welcome(client):
    print("HI.")
    phone_no = input("enter your phone number: ")
    if login(phone_no):
        main_window(phone_no, client)
        return
    else:
        print("you don't have account. sign up:")
        while True:
            f_name = input("first name: ")
            l_name = input("last name: ")
            gender = input("gender: ")
            bank_account = input("bank account: ")
            img_dir = input("enter your image directory: ")
            img_byte_array = base64.b64encode(open(img_dir, 'rb').read())
            if sign_up(f_name, l_name, phone_no, img_byte_array, gender, bank_account):
                print("succesfully signed up!")
                main_window(phone_no, client)
                return
            else:
                print("sign up error! again:\n")


def main_window(drv_ph_no, client):
    while True:
        instruction = input("instruction?\n(waiting/settings/exit)")
        if instruction == "waiting":
            if not have_car(drv_ph_no):
                print("you dont have an approved car. contact hiring department.")
            else:
                print("connecting to server...")
                client.wait()
                client.declare()
                client.wait()
                client.send_info(execute_fetch("select score from driver where phonenumber = %s", [drv_ph_no]), drv_ph_no)
                client.wait()
                print("wait for passenger:")
                while True:
                    px = client.get_passenger()
                    print("passenger :: ("+str(px[0])+", "+str(px[1])+") -> ("+str(px[2])+", "+str(px[3])+")   :: "+str(px[4]))
                    if input("yes/no?") == "yes":
                        on_trip(drv_ph_no, client.accept())
                        break
                    else:
                        client.reject()
                return
        elif instruction == "settings":
            instruction = input("what do you want?\n(changeName/changeImage/changeBankAccount)")
            res = False
            if instruction == "changeName":
                res = change_name(drv_ph_no, input("new first name: "), input("new last name: "))
            elif instruction == "changeImage":
                img_dir = input("enter image directory: ")
                img_byte_array = base64.b64encode(open(img_dir, 'rb').read())
                res = change_image(drv_ph_no, img_byte_array)
            elif instruction == "changeBankAccount":
                res = change_bank_account(drv_ph_no, input("enter bank account number: "))
            if res:
                print("successful\n")
            else:
                print("UnSuccessful\n")
        elif instruction == "exit":
            break


def on_trip(drv_ph_no, psg_ph_no):
    print(drv_ph_no, psg_ph_no)
    #while True:


client = driver_client()
welcome(client)
