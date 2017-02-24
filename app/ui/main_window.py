# !/usr/bin/env python3
# -*- coding: utf-8 -*-
import json
from urllib import request

from PyQt5.QtCore import QSettings
from PyQt5.QtCore import QUrl
from PyQt5.QtGui import QDesktopServices

from app.config import set_default, set_file_path
from app.ui.proxy_dialog import ProxyDialog
from app.ui.ui_main_window import Ui_MainWindow
from app.util.config_utils import s2b
from app.util.download_thread import GetVideoInfoThread
from app import mlog, mconfig

from PyQt5.QtWidgets import QMainWindow, QFileDialog, QMessageBox, QDesktopWidget
from PyQt5.QtGui import QIcon
from app.ui.about_widget import AboutWdiget
from app.ui.files_list_dialog import FilesListDialog
from app.ui.icon_rc import *
from app.util.status import get_buffer, clear_buffer


class MainWindow(Ui_MainWindow):
    def __init__(self):
        super().__init__()
        self.main_window = QMainWindow()
        self.setupUi(self.main_window)
        self.main_window.setFixedSize(self.main_window.width(), self.main_window.height())
        self.msg = QMessageBox()
        self.qr = self.main_window.frameGeometry()  # move to center of screen
        self.cp = QDesktopWidget().availableGeometry().center()
        self.qr.moveCenter(self.cp)
        self.main_window.move(self.qr.topLeft())
        self.text_edit_urls.setAcceptRichText(False)

        self.config = QSettings('config.ini', QSettings.IniFormat)
        self.init_config()
        self.set_slot()

    def init_config(self):
        # 读取设置
        mconfig.set_file_path(self.config.value(mconfig.file_path, mconfig.base_dir))
        enable_proxy = self.config.value('enable_proxy', False)

        # 界面更新
        self.file_path_label.setText(mconfig.get_file_path())
        self.proxy_checkBox.setChecked(s2b(enable_proxy))

    def set_slot(self):
        self.button_download.clicked.connect(self.get_info)
        self.check_update_button.clicked.connect(self.check_for_updates)
        self.about_button.clicked.connect(self.show_about)
        self.set_path_button.clicked.connect(self.set_file_path)
        self.set_proxy_button.clicked.connect(self.show_proxy_dialog)

        self.proxy_checkBox.stateChanged.connect(self.save_config)

        self.action_about.triggered.connect(self.show_about)
        self.action_file_path.triggered.connect(self.set_file_path)
        self.action_check_for_updates.triggered.connect(self.check_for_updates)
        self.action_report_bugs.triggered.connect(self.report_bugs)
        self.action_supported_sites.triggered.connect(self.get_supported_sites)

    def get_supported_sites(self):
        QDesktopServices.openUrl(QUrl('https://github.com/ingbyr/GUI-YouGet/wiki/Supported-Sites'))

    def get_info(self):
        mconfig.set_default()
        set_default()
        clear_buffer()

        self.button_download.setEnabled(False)
        self.urls = (str(self.text_edit_urls.toPlainText())).split(';')
        mlog.debug(self.urls[0])

        self.m_thread = GetVideoInfoThread(self.urls, **mconfig.kwargs)
        self.m_thread.finish_signal.connect(self.finish_get_info)
        self.m_thread.start()

    def finish_get_info(self, ls, can_download):
        mlog.debug('finish_get_info: ' + str(can_download))
        self.button_download.setEnabled(True)
        if can_download:
            self.files_list_dialog = FilesListDialog()
            self.files_list_dialog.update_files_list(ls)
            mconfig.set_urls(self.urls)
        else:
            # self.show_msg(QMessageBox.Critical, 'Failed ', 'Can not get the files list (╯°Д°)╯︵ ┻━┻')
            self.show_msg(QMessageBox.Critical, 'Failed ', ls)

    def show_about(self):
        mlog.debug('show about widget')
        self.about_widget = AboutWdiget()
        self.about_widget.about_widget.move(self.qr.topLeft())
        self.about_widget.about_widget.show()

    def set_file_path(self):
        fname = QFileDialog.getExistingDirectory(self.main_window, caption='Select Path', directory='',
                                                 options=QFileDialog.ShowDirsOnly)
        if fname:
            self.config.setValue(mconfig.file_path, fname)
            self.file_path_label.setText(fname)
            set_file_path(fname)
            mlog.debug('changed file path to ' + mconfig.get_file_path())
        else:
            set_file_path(mconfig.base_dir)
            self.file_path_label.setText(mconfig.base_dir)
            self.config.setValue(mconfig.file_path, mconfig.base_dir)

    def show_msg(self, icon, title, text):
        self.msg.setWindowTitle(title)
        self.msg.setWindowIcon(QIcon(':/res/favicon.ico'))
        self.msg.setIcon(icon)
        self.msg.setText(text)
        self.msg.setStandardButtons(QMessageBox.Ok)
        self.msg.show()

    def check_for_updates(self):
        try:
            with request.urlopen('https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/version.json') as f:
                raw_inf = str(f.read())[2:-1]
                mlog.debug(str(f.read())[2:-1])
                remote_inf = json.loads(raw_inf)
                mlog.debug('remote version is ' + remote_inf['version'])
        except Exception as e:
            mlog.exception(e)
            self.show_msg(QMessageBox.Critical, 'Failed', 'Check for updates failed')
            return

        if mconfig.version >= remote_inf['version']:
            self.show_msg(QMessageBox.Information, 'Check for updates', 'No available updates')
        else:
            self.show_msg(QMessageBox.Information, 'Check for updates', 'There is a new version')
            self.do_updates()

    def do_updates(self):
        QDesktopServices.openUrl(QUrl('https://github.com/ingbyr/GUI-YouGet/releases'))

    def report_bugs(self):
        QDesktopServices.openUrl(QUrl('https://github.com/ingbyr/GUI-YouGet/issues'))

    def get_more_infomation(self):
        QDesktopServices.openUrl(QUrl('https://github.com/ingbyr/GUI-YouGet'))

    def show_proxy_dialog(self):
        self.proxy_dialog = ProxyDialog()

    def save_config(self):
        enable_proxy = self.proxy_checkBox.isChecked()
        self.config.setValue('enable_proxy', enable_proxy)
        is_http_proxy = self.config.value('is_http_proxy')
        is_socks_proxy = self.config.value('is_socks_proxy')

        if is_http_proxy is None or is_socks_proxy is None:
            self.show_proxy_dialog()
