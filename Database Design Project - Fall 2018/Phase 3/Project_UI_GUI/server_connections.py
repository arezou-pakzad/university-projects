from connect import *
import random
import datetime


def insert_trip(driver_phone, passenger_phone, origin, destination, travel_cost):
    now = datetime.datetime.now()
    date = str(now.year) + "-" + str(now.month) + "-" + str(now.day)
    time = str(now.hour) + ":" + str(now.minute) + ":" + str(now.second)
    code = str(random.randint(0, 99999999999))
    execute("insert into travel values (%s, %s, %s, %s, %s, NULL, %s,"
            " 'economic', 'On the way to the origin', FALSE, NULL, %s, %s, NULL)",
            [code, driver_phone, passenger_phone, origin, destination, travel_cost, date, time])
