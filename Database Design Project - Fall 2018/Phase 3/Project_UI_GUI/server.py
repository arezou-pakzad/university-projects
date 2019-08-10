import socket
import pickle
from server_connections import *

HOST = '127.0.0.1'
PORT = 57332


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("starting server")
s.bind((HOST, PORT))
s.listen()

drivers_connections = []

while True:
    print("incoming transaction by a ", end='')
    conn, addr = s.accept()
    conn.sendall(b'start')
    if conn.recv(1024) == b'driver':
        print("driver")
        conn.sendall(b'continue')
        score, phoneNo = pickle.loads(conn.recv(1024))
        drivers_connections.append([score, phoneNo, conn])
        conn.sendall(b'end')
    else:
        print("passenger")
        conn.sendall(b'continue')
        f = pickle.loads(conn.recv(1024))[0]
        conn.sendall(b'ready')
        a = pickle.loads(conn.recv(1024))[0]
        conn.sendall(b'ready')
        b = pickle.loads(conn.recv(1024))[0]
        conn.sendall(b'ready')
        c = pickle.loads(conn.recv(1024))[0]
        conn.sendall(b'ready')
        d = pickle.loads(conn.recv(1024))[0]
        conn.sendall(b'ready')
        e = pickle.loads(conn.recv(1024))[0]
        conn.sendall(b'ready')
        info = [a, b, c, d, e, f]
        conn.sendall(b'ok. wait')
        driver_number = None
        drivers_connections.sort(reverse=True)
        for driver_connection in drivers_connections:
            driver_connection[2].sendall(pickle.dumps(info))
            if driver_connection[2].recv(1024) == b'Y':
                driver_connection[2].sendall(pickle.dumps(info[5]))
                insert_trip(driver_number, info[5], '('+str(info[0])+','+str(info[1])+')', '('+str(info[2])+','+str(info[3])+')', info[4])
                driver_number = driver_connection[1]
                break
        if driver_number is None:
            conn.sendall(pickle.dumps("nope"))
        else:
            conn.sendall(pickle.dumps(driver_number))
