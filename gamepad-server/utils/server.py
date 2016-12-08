import socket
from threading import Thread

from utils.driver_connector import Listener


class TcpServer(object):

    def __init__(self, host, port, verbose=True):
        self._host = None
        self._port = None
        self.host = host
        self.port = port

        self._verbose = verbose
        self._sock = None
        self._listen_thread = None
        self._is_running = False
        self._listeners = []

    @property
    def host(self):
        return self._host

    @host.setter
    def host(self, val):
        if not isinstance(val, str):
            raise TypeError("The given host must be string.")
        self._host = val

    @property
    def port(self):
        return self._port

    @port.setter
    def port(self, val):
        if not isinstance(val, int):
            raise TypeError("The given port must be integer.")
        self._port = val

    @property
    def listeners(self):
        return self._listeners

    @property
    def is_running(self):
        return self._is_running

    def add_listener(self, listener):
        if not isinstance(listener, Listener):
            raise TypeError("The given listener is not a subclass of Listener")
        self._listeners.append(listener)

    def remove_listener(self, listener):
        self._listeners.remove(listener)

    def listen(self, max_connections=1, msg_buf_size=1024):
        self._sock.listen(max_connections)

        print("[TcpServer]: the server is started at address {}:{}"
              .format(self._host, self._port))
        with self._sock:
            while self._is_running:
                conn, addr = self._sock.accept()
                if self._verbose:
                    print("[TcpServer]: connection with {} "
                          "was started: ".format(addr))
                with conn:
                    self._recv_msg(conn, msg_buf_size)
                if self._verbose:
                    print("[TcpServer]: connection with {} "
                          "was stopped: ".format(addr))

        print("[TcpServer]: the server was stopped at address {}:{}"
              .format(self._host, self._port))

    def start_listen(self, max_connections=1, msg_buf_size=1024):
        if self._listen_thread and self._listen_thread.is_alive():
            raise InterruptedError("The listen thread is already running.")

        self._bind()
        self._listen_thread = Thread(
            target=self.listen, args=(max_connections, msg_buf_size))
        self._is_running = True
        self._listen_thread.start()

    def stop_listen(self):
        self._is_running = False

    def _notify_listeners(self, data):
        for listener in self._listeners:
            listener.update(data)

    def _recv_msg(self, conn, buf_size):
        data = conn.recv(buf_size)
        self._notify_listeners(data)
        while data:
            data = conn.recv(buf_size)
            self._notify_listeners(data)

    def _bind(self):
        if self._sock is not None:
            self._sock.close()

        self._sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._sock.bind((self._host, self._port))
