from PyQt5.QtWidgets import QMessageBox

from PyQt5.QtWidgets import QWidget

from controllers.mainwindow import MainWindowController
from ui.mainwindow import Ui_Form


class MainWindowView(QWidget):

    def __init__(self):
        super(MainWindowView, self).__init__()
        self._ui = Ui_Form()
        self._ui.setupUi(self)
        self._controller = MainWindowController()
        self.__connect_signals()

    def __connect_signals(self):
        self._ui.start_server_btn.clicked.connect(
            self.handle_start_server_btn_clicked)
        self._ui.stop_server_btn.clicked.connect(
            self.handle_stop_server_btn_clicked)

    def handle_start_server_btn_clicked(self):
        try:
            host = str(self._ui.host_le.text())
            port = int(self._ui.port_le.text())
        except ValueError:
            self.error_msg_box("Invalid input data")
            return

        try:
            self._controller.connect(host, port)
            QMessageBox.information(self, "Virtual Gamepad",
                                    "Server was started successful.",
                                    QMessageBox.Ok)
        except FileNotFoundError:
            self.error_msg_box("Device driver wasn't loaded or the path "
                               "to the device driver file is invalid.")
        except PermissionError:
            self.error_msg_box(
                title="Permission Error",
                msg="Try to run the server application with root privileges.")
        except OverflowError:
            self.error_msg_box(
                title="Connection Error",
                msg="Invalid port. The port must be in range [0, 65535].")
        except ConnectionError:
            self.error_msg_box(title="Connection Error",
                               msg="The server has been already run.")
        except OSError as exc:
            self.error_msg_box(title="Connection Error", msg=str(exc))

    def handle_stop_server_btn_clicked(self):
        self._controller.disconnect()
        QMessageBox.information(self, "Virtual Gamepad",
                                "Server was stopped", QMessageBox.Ok)

    def error_msg_box(self, msg, title="Error"):
        QMessageBox.critical(self, title, msg, QMessageBox.Ok)
