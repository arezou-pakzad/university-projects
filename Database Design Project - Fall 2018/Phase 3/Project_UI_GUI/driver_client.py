import socket
import pickle


class driver_client:
    HOST = '127.0.0.1'
    PORT = 57332

    def __init__(self):
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect((self.HOST, self.PORT))

    def wait(self):
        self.s.recv(1024)

    def declare(self):
        self.s.sendall(b'driver')

    def send_info(self, score, no):
        self.s.sendall(pickle.dumps([score, no]))

    def get_passenger(self):
        a = self.s.recv(1024)
        return pickle.loads(a)

    def accept(self):
        self.s.sendall(b'Y')
        return pickle.loads(self.s.recv(1024))

    def reject(self):
        self.s.sendall(b'N')
