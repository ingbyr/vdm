# !/usr/bin/env python3
# -*- coding: utf-8 -*-
from PyQt5.QtCore import QSettings
from PyQt5.QtWidgets import QButtonGroup
from PyQt5.QtWidgets import QDialog

from app.ui.ui_proxy_dialog import Ui_Dialog
from app.util.config_utils import s2b

__author__ = "ingbyr"


class ProxyDialog(Ui_Dialog):
    def __init__(self):
        super().__init__()
        self.proxy_dialog = QDialog()
        self.setupUi(self.proxy_dialog)
        self.set_slot()
        self.proxy_dialog.show()

        self.config = QSettings('config.ini', QSettings.IniFormat)
        self.init_config()

        self.group = QButtonGroup()
        self.group.addButton(self.socks5_checkbox)
        self.group.addButton(self.http_checkbox)

    def set_slot(self):
        self.button_box.accepted.connect(self.save_config)

    def init_config(self):
        ip = self.config.value('ip', '127.0.0.1')
        port = self.config.value('port', '1080')
        self.ip_text_edit.setText(ip)
        self.port_text_edit.setText(port)

        is_socks_proxy = self.config.value('is_socks_proxy', 'true')
        is_http_proxy = self.config.value('is_http_proxy', 'false')
        self.socks5_checkbox.setChecked(s2b(is_socks_proxy))
        self.http_checkbox.setChecked(s2b(is_http_proxy))

    def save_config(self):
        ip = self.ip_text_edit.text()
        port = self.port_text_edit.text()
        is_socks_proxy = self.socks5_checkbox.isChecked()
        is_http_proxy = self.http_checkbox.isChecked()
        self.config.setValue('ip', ip)
        self.config.setValue('port', port)
        self.config.setValue('is_socks_proxy', is_socks_proxy)
        self.config.setValue('is_http_proxy', is_http_proxy)
        self.proxy_dialog.close()
