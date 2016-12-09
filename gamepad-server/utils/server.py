import socket
import threading
from abc import ABCMeta, abstractmethod

from utils.driver_connector import Listener


class TcpServer(object):
    """ Simple TCP server based on sockets. """

    def __init__(self, host, port, verbose=True):
        self._host = host
        self._port = port
        self.host, self.port = host, port
        self._verbose = verbose

        self._sock = None
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

    def start(self, max_connections=1, msg_buf_size=1024):
        if self._is_running:
            raise ConnectionResetError("The server has been already started.")

        self._is_running = True
        self._sock.listen(max_connections)

        print("[TcpServer]: the server is started at address {}:{}"
              .format(self._host, self._port))

        with self._sock:
            while self._is_running:
                try:
                    conn, addr = self._sock.accept()

                    if self._verbose:
                        print("[TcpServer]: connection with {} "
                              "was started: ".format(addr))
                    with conn:
                        self._recv_msg(conn, msg_buf_size)
                except OSError as e:
                    print("[TcpServer]: ", str(e))
                    continue
                if self._verbose:
                    print("[TcpServer]: connection with {} "
                          "was stopped: ".format(addr))

        print("[TcpServer]: the server was stopped at address {}:{}"
              .format(self._host, self._port))

    def stop(self):
        if self._is_running:
            self._is_running = False
            self._sock.shutdown(socket.SHUT_RDWR)
            self._sock.close()

    def _notify_listeners(self, data):
        for listener in self._listeners:
            listener.update(data)

    def _recv_msg(self, conn, buf_size):
        data = conn.recv(buf_size)
        self._notify_listeners(data)
        while data and self._is_running:
            data = conn.recv(buf_size)
            self._notify_listeners(data)

    def bind(self):
        if self._sock is not None:
            self._sock.close()

        self._sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._sock.bind((self._host, self._port))

    def __repr__(self):
        return "TcpServer(host: {}, port: {}, launched: {})".format(
            self._host, self._port, self._is_running)


class ThreadManager(metaclass=ABCMeta):
    """ Manager to control thread. """

    @abstractmethod
    def start(self, **kwargs):
        """ Start thread. """
        pass

    @abstractmethod
    def stop(self):
        """ Stop thread. """
        pass

    @abstractmethod
    def stopped(self):
        """ Return True if the managed thread is stopped. """
        pass


class TcpServerThreadManager(ThreadManager):
    """ The wrapper for TcpServer to run server in the other thread. """

    def __init__(self, server):
        self._server = None
        self.server = server
        self._listening_thread = None

    @property
    def server(self):
        return self._server

    @server.setter
    def server(self, obj):
        if obj is None:
            raise ValueError("The given server is none.")
        self._server = obj

    def start(self, kwargs=None):
        if self._listening_thread and self._listening_thread.is_alive():
            raise InterruptedError("The listen thread is already running.")

        self._server.bind()
        self._listening_thread = threading.Thread(
            target=self._server.start, kwargs=kwargs)
        self._listening_thread.start()

    def stop(self):
        if self._listening_thread:
            self._server.stop()
            self._listening_thread.join()
            print(self._listening_thread.is_alive())

    def stopped(self):
        if self._listening_thread is None:
            return False

        return not self._listening_thread.is_alive()
