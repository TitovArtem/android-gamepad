import os
from abc import ABCMeta, abstractmethod


class Listener(object):
    __metaclass__ = ABCMeta

    @abstractmethod
    def update(self, *args, **kwargs):
        pass


class DriverDataProvider(Listener):

    def __init__(self, device_driver_path):
        if not device_driver_path:
            raise ValueError("The given path to device driver is none.")
        if not isinstance(device_driver_path, str):
            raise TypeError("The given path have to be string.")

        self._device_driver_path = device_driver_path
        self._device_fd = None

    def open(self):
        if self._device_fd:
            self.close()
        self._device_fd = os.open(self._device_driver_path, os.O_RDWR)

    def close(self):
        os.close(self._device_fd)
        self._device_fd = None

    def send(self, msg):
        if msg:
            os.write(self._device_fd, msg)

    def update(self, data):
        self.send(data)
