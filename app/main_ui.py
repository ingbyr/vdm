# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

from PyQt5.QtWidgets import *
from PyQt5.QtGui import QIcon
from app.download_thread import *
from app import log, base_dir

__author__ = 'InG_byr'


class GUI(QMainWindow):
    def __init__(self):
        super().__init__()
        self.init_ui()

    def init_ui(self):
        # menu
        self.init_menu()

        # init main
        self.init_main()
        # move to the center
        self.center()

        # status bar
        self.statusBar()
        self.statusBar().showMessage('Ready')

        self.setWindowTitle('GUI-YouGet')
        self.setWindowIcon(QIcon(base_dir + '/app/res/icon/logo.jpg'))
        self.show()

    def init_main(self):
        ing_main = InGMain()
        self.setCentralWidget(ing_main)

    def init_menu(self):
        about_action = QAction(QIcon('/res/icon/about.png'), '&About', self)
        about_action.setStatusTip('About this application')
        about_action.triggered.connect(self.about_message)

        exit_action = QAction(QIcon('/res/icon/exit.png'), '&Exit', self)
        exit_action.setShortcut('Ctrl+Q')
        exit_action.setStatusTip('Exit application')
        exit_action.triggered.connect(qApp.quit)

        menubar = self.menuBar()
        setting_menu = menubar.addMenu('&Setting')
        help_menu = menubar.addMenu('&Help')
        help_menu.addAction(about_action)
        help_menu.addAction(exit_action)

    def about_message(self):
        # todo: about index not commpleted
        print('about')

    # show the app in the center
    def center(self):
        self.setGeometry(300, 300, 700, 500)
        qr = self.frameGeometry()
        cp = QDesktopWidget().availableGeometry().center()
        qr.moveCenter(cp)
        self.move(qr.topLeft())


class InGMain(QWidget):
    def __init__(self):
        super().__init__()

        self.urlEdit = QLineEdit()
        self.searchEdit = QLineEdit()
        self.informationEdit = QTextEdit()
        self.informationEdit.setReadOnly(True)
        self.informationEdit.setOverwriteMode(False)

        self.init_ui()

    def init_ui(self):
        url = QLabel('Url')
        search = QLabel('Search')
        information = QLabel('Information')
        download_btn = QPushButton('Download')
        download_btn.setStatusTip('Downlaod into your PC')
        download_btn.clicked.connect(self.gui_download_by_url)
        search_btn = QPushButton('Search')
        search_btn.setStatusTip('Search in Google and download auto')

        grid = QGridLayout()
        grid.setSpacing(10)
        grid.addWidget(url, 1, 0)
        grid.addWidget(self.urlEdit, 1, 1)
        grid.addWidget(download_btn, 1, 2)
        grid.addWidget(search, 2, 0)
        grid.addWidget(self.searchEdit, 2, 1)
        grid.addWidget(search_btn, 2, 2)
        grid.addWidget(information, 3, 0)
        grid.addWidget(self.informationEdit, 3, 1, 5, 1)
        self.setLayout(grid)

    def gui_download_by_url(self):
        self.informationEdit.insertPlainText(
            '****************************\n'
            '[INFO]Start get the information of video...\n')

        urls = str(self.urlEdit.text()).split(';')
        kwargs = {'output_dir': './tmpVideos',
                  'merge': True,
                  'json_output': False,
                  'caption': True}
        show_inf = ''
        can_download = False

        # show the result first
        try:
            self.get_inf_thread = GetVideoInfoThread(self.informationEdit, urls, **kwargs)
            self.get_inf_thread.finish_signal.connect(self.update_inf_ui)
            self.get_inf_thread.start()
            can_download = True
        except Exception as e:
            log.debug(e)
        finally:
            self.informationEdit.insertPlainText(show_inf)
            r_obj.flush()
            if can_download:
                self.download_thread = DownloadThread(self.informationEdit, urls, **kwargs)
                self.download_thread.finishSignal.connect(self.update_inf_ui)
                self.download_thread.start()
            else:
                self.informationEdit.insertPlainText('\n[ERROR]Download failed!!!\n')

    def update_inf_ui(self, ls):
        for inf in ls:
            self.informationEdit.insertPlainText(inf)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = GUI()
    sys.exit(app.exec_())
