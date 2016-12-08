from utils.driver_connector import DriverDataProvider
from utils.server import TcpServer


def singleton(cls):
    instances = {}

    def getinstance():
        if cls not in instances:
            instances[cls] = cls()
        return instances[cls]
    return getinstance


@singleton
class ConnectionManager(object):

    def __init__(self):
        self._server = None
        self._driver_provider = None

    @property
    def server(self):
        return self._server

    @server.setter
    def server(self, obj):
        if obj is None:
            ValueError("The given server object is none.")
        self._server = obj

    @property
    def driver_data_provider(self):
        return self._driver_provider

    @driver_data_provider.setter
    def driver_data_provider(self, obj):
        if obj is None:
            ValueError("The given driver data provider object is none.")
        self._driver_provider = obj


def create_connection(host, port, device_driver_path):
    server = TcpServer(host, port)
    driver_data_provider = DriverDataProvider(device_driver_path)

    server.add_listener(driver_data_provider)

    connection_manager = ConnectionManager()
    connection_manager.driver_data_provider = driver_data_provider
    connection_manager.server = server

    return connection_manager


def open_connection(connection_manager):
    connection_manager.driver_data_provider.open()
    connection_manager.server.start_listen()


def close_connection(connection_manager):
    connection_manager.server.stop_listen()
    connection_manager.driver_data_provider.close()
