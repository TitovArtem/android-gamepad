from utils.connection import create_connection, open_connection, \
    close_connection, ConnectionManager


class MainWindowController(object):

    DRIVER_PATH = "/sys/devices/platform/virtual_gamepad/commands"

    def connect(self, host, port):
        conn_manager = ConnectionManager()
        if conn_manager.server_thread_manager and \
                not conn_manager.server_thread_manager.stopped():
            raise ConnectionError("TCP Server is already listening.")

        conn_manager = create_connection(host, port, self.DRIVER_PATH)
        open_connection(conn_manager)

    def disconnect(self):
        conn_manager = ConnectionManager()
        close_connection(conn_manager)
