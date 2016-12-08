import sys
from PyQt5.QtWidgets import QApplication

from views.mainwindow import MainWindowView


def main():
    app = QApplication(sys.argv)

    window = MainWindowView()
    window.show()

    return app.exec()


if __name__ == '__main__':
    sys.exit(main())
