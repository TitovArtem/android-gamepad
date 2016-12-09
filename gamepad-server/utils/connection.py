from utils.driver_connector import DriverDataProvider
from utils.server import TcpServer, TcpServerThreadManager


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
        self._server_thread_manager = None
        self._driver_provider = None

    @property
    def server_thread_manager(self):
        return self._server_thread_manager

    @server_thread_manager.setter
    def server_thread_manager(self, obj):
        if obj is None:
            ValueError("The given server object is none.")
        self._server_thread_manager = obj

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
    server_thread_manager = TcpServerThreadManager(server)

    connection_manager = ConnectionManager()
    connection_manager.driver_data_provider = driver_data_provider
    connection_manager.server_thread_manager = server_thread_manager

    return connection_manager


def open_connection(connection_manager):
    connection_manager.driver_data_provider.open()
    connection_manager.server_thread_manager.start()


def close_connection(connection_manager):
    if connection_manager.server_thread_manager:
        connection_manager.server_thread_manager.stop()
    if connection_manager.driver_data_provider:
        connection_manager.driver_data_provider.close()
