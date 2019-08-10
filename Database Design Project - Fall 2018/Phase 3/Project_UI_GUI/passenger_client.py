import socket
import pickle


class passenger_client:
    HOST = '127.0.0.1'
    PORT = 57332

    def __init__(self):
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect((self.HOST, self.PORT))

    def wait(self):
        self.s.recv(1024)

    def declare(self):
        self.s.sendall(b'passenger')

    def get_driver(self):
        return pickle.loads(self.s.recv(1024))

    def send_info(self, src_x, src_y, des_x, des_y, price, no):
        self.s.sendall(pickle.dumps([no]))
        self.wait()
        self.s.sendall(pickle.dumps([src_x]))
        self.wait()
        self.s.sendall(pickle.dumps([src_y]))
        self.wait()
        self.s.sendall(pickle.dumps([des_x]))
        self.wait()
        self.s.sendall(pickle.dumps([des_y]))
        self.wait()
        self.s.sendall(pickle.dumps([price]))

    def close(self):
        self.s.close()

    def test(self, src_x, src_y, des_x, des_y, price, passenger):
        self.wait()
        self.declare()
        self.wait()
        self.send_info(src_x, src_y, des_x, des_y, price, passenger)
        self.wait()
        self.get_driver()
        # driver = conn.get_driver()
        # if driver != "nope":
        #    # todo: bere safhe On trip
        #    pass
        # else:
        #    print("not any driver!")
        #    self.back_clicked()
        self.close()

