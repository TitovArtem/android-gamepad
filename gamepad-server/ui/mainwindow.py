# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'mainwindow.ui'
#
# Created by: PyQt5 UI code generator 5.5.1
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_Form(object):
    def setupUi(self, Form):
        Form.setObjectName("Form")
        Form.resize(404, 196)
        sizePolicy = QtWidgets.QSizePolicy(QtWidgets.QSizePolicy.Preferred, QtWidgets.QSizePolicy.Preferred)
        sizePolicy.setHorizontalStretch(0)
        sizePolicy.setVerticalStretch(0)
        sizePolicy.setHeightForWidth(Form.sizePolicy().hasHeightForWidth())
        Form.setSizePolicy(sizePolicy)
        self.gridLayout = QtWidgets.QGridLayout(Form)
        self.gridLayout.setSizeConstraint(QtWidgets.QLayout.SetMinimumSize)
        self.gridLayout.setObjectName("gridLayout")
        self.label = QtWidgets.QLabel(Form)
        self.label.setObjectName("label")
        self.gridLayout.addWidget(self.label, 2, 0, 1, 1)
        self.host_le = QtWidgets.QLineEdit(Form)
        self.host_le.setObjectName("host_le")
        self.gridLayout.addWidget(self.host_le, 2, 1, 1, 1)
        self.label_2 = QtWidgets.QLabel(Form)
        self.label_2.setObjectName("label_2")
        self.gridLayout.addWidget(self.label_2, 2, 2, 1, 1)
        self.port_le = QtWidgets.QLineEdit(Form)
        self.port_le.setObjectName("port_le")
        self.gridLayout.addWidget(self.port_le, 2, 3, 1, 1)
        self.start_server_btn = QtWidgets.QPushButton(Form)
        self.start_server_btn.setObjectName("start_server_btn")
        self.gridLayout.addWidget(self.start_server_btn, 3, 1, 1, 1)
        self.stop_server_btn = QtWidgets.QPushButton(Form)
        self.stop_server_btn.setObjectName("stop_server_btn")
        self.gridLayout.addWidget(self.stop_server_btn, 3, 3, 1, 1)

        self.retranslateUi(Form)
        QtCore.QMetaObject.connectSlotsByName(Form)

    def retranslateUi(self, Form):
        _translate = QtCore.QCoreApplication.translate
        Form.setWindowTitle(_translate("Form", "Virtual Gamepad"))
        self.label.setText(_translate("Form", "Host:"))
        self.label_2.setText(_translate("Form", "Port:"))
        self.start_server_btn.setText(_translate("Form", "Start Server"))
        self.stop_server_btn.setText(_translate("Form", "Stop Server"))

