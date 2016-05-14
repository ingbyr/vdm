# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os

from PyQt5.QtWidgets import *
from PyQt5.QtGui import QIcon
from app.download_thread import *
from app import log

__author__ = 'InG_byr'


class GUI(QMainWindow):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        # menu
        self.initMenu()

        # initMain
        self.initMain()
        # move to the center
        self.center()

        # status bar
        self.statusBar()
        self.statusBar().showMessage('Ready')

        self.setWindowTitle('GUI-YouGet')
        self.setWindowIcon(QIcon(os.getcwd() + '/res/icon/download.png'))
        self.show()

    def initMain(self):
        ingMain = InGMain()
        self.setCentralWidget(ingMain)

    def initMenu(self):
        aboutAction = QAction(QIcon('/res/icon/about.png'), '&About', self)
        aboutAction.setStatusTip('About this application')
        aboutAction.triggered.connect(self.aboutMessage)

        exitAction = QAction(QIcon('/res/icon/exit.png'), '&Exit', self)
        exitAction.setShortcut('Ctrl+Q')
        exitAction.setStatusTip('Exit application')
        exitAction.triggered.connect(qApp.quit)

        menubar = self.menuBar()
        settingMenu = menubar.addMenu('&Setting')
        helpMenu = menubar.addMenu('&Help')
        helpMenu.addAction(aboutAction)
        helpMenu.addAction(exitAction)

    # about this app
    def aboutMessage(self):
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
        self.initUI()

    def initUI(self):
        url = QLabel('Url')
        search = QLabel('Search')
        information = QLabel('Information')

        self.urlEdit = QLineEdit()
        searchEdit = QLineEdit()
        self.informationEdit = QTextEdit()
        self.informationEdit.setReadOnly(True)
        self.informationEdit.setOverwriteMode(False)

        downloadBTN = QPushButton('Download')
        downloadBTN.setStatusTip('Downlaod into your PC')
        downloadBTN.clicked.connect(self.gui_download_by_url)
        searchBTN = QPushButton('Search')
        searchBTN.setStatusTip('Search in Google and download auto')

        grid = QGridLayout()
        grid.setSpacing(10)

        grid.addWidget(url, 1, 0)
        grid.addWidget(self.urlEdit, 1, 1)
        grid.addWidget(downloadBTN, 1, 2)

        grid.addWidget(search, 2, 0)
        grid.addWidget(searchEdit, 2, 1)
        grid.addWidget(searchBTN, 2, 2)

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
        canDownload = False

        # show the result first
        try:
            kwargs['info_only'] = True
            self.get_inf_thread = GetVideoInfoThread(self.informationEdit, urls, **kwargs)
            self.get_inf_thread.finish_signal.connect(self.update_inf_ui)
            self.get_inf_thread.start()
            canDownload = True
        except Exception as e:
            log.debug(e)
        finally:
            self.informationEdit.insertPlainText(show_inf)
            r_obj.flush()
            if canDownload:
                self.bwThread = DownloadThread(self.informationEdit, urls, **kwargs)
                self.bwThread.finishSignal.connect(self.update_inf_ui)
                self.bwThread.start()
            else:
                self.informationEdit.insertPlainText('\n[ERROR]Download failed!!!\n')

    def update_inf_ui(self, ls):
        for inf in ls:
            self.informationEdit.insertPlainText(inf)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = GUI()
    sys.exit(app.exec_())
