import socket

PORT = 8047
HOST = "192.168.43.178"
MAX_USERS = 5

def main():
    sock = socket.socket()
    sock.bind((HOST, PORT))
    sock.listen(MAX_USERS)
    print(sock)

    while True:
        conn, addr = sock.accept()
        print(conn.__class__)
        print("Connect to: ", addr)
        with conn:
            data = conn.recv(1024)
            while data:
                print("Recived data {0} from {1}".format(str(data), addr))
                data = conn.recv(1024)
        conn.close()

    sock.close()


if __name__ == '__main__':
    main()
