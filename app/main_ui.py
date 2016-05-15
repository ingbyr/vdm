# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

from PyQt5.QtGui import *
from PyQt5.QtCore import *
from PyQt5.QtWidgets import *
from app.download_thread import *
from app import mlog, base_dir
import app.custom_you_get.status as status

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
        about_action = QAction(QIcon(base_dir + 'app/res/icon/about.png'), '&About', self)
        about_action.setStatusTip('About this application')
        about_action.triggered.connect(self.about_message)

        exit_action = QAction(QIcon(base_dir + 'app/res/icon/exit.png'), '&Exit', self)
        exit_action.setShortcut('Ctrl+Q')
        exit_action.setStatusTip('Exit application')
        exit_action.triggered.connect(qApp.quit)

        file_path_action = QAction(QIcon(base_dir + '/app/res/icon/file_path.png'), '&FilePath', self)
        file_path_action.setStatusTip('Set file path')

        menu_bar = self.menuBar()
        setting_menu = menu_bar.addMenu('&Setting')
        help_menu = menu_bar.addMenu('&Help')
        setting_menu.addAction(file_path_action)
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
        # self.spb = QProgressBar()
        # self.spb.setMaximum(100)
        # self.spb.setMinimum(0)
        # self.progress = 0

        self.init_ui()

    def init_ui(self):
        url = QLabel('Url')
        search = QLabel('Search')
        information = QLabel('Information')
        # progress = QLabel('Progress')
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
        grid.addWidget(information, 4, 0)
        grid.addWidget(self.informationEdit, 4, 1, 4, 1)
        # grid.addWidget(progress, 9, 0)
        # grid.addWidget(self.spb, 9, 1)

        self.setLayout(grid)

    def gui_download_by_url(self):
        status.set_default()
        self.update_inf_ui(['[TIP] Ready to start download',
                            '[INFO] Get the information of video...'])

        self.urls = str(self.urlEdit.text()).split(';')
        self.kwargs = {'output_dir': '../tmpVideos',
                       'merge': True,
                       'json_output': False,
                       'caption': True}

        # show the result first
        try:
            self.get_inf_thread = GetVideoInfoThread(self.informationEdit, self.urls, **self.kwargs)
            self.get_inf_thread.finish_signal.connect(self.start_download)
            self.get_inf_thread.start()
        except Exception as e:
            log.debug(e)
        finally:
            r_obj.flush()

    def update_inf_ui(self, ls):
        for inf in ls:
            if str(inf).startswith('[TIP]'):
                self.informationEdit.insertHtml('<font color=blue>' + inf + '</font><br>')
            elif str(inf).startswith('[ERROR]'):
                self.informationEdit.insertHtml('<font color=red>' + inf + '</font><br>')
            elif str(inf).startswith('[INFO]'):
                self.informationEdit.insertHtml('<font color=green>' + inf + '</font><br>')
            else:
                pass
            self.edittext2bottom()

    def finish_download(self, ls):
        self.update_inf_ui(ls)
        # self.progress = 100
        # self.spb.setValue(self.progress)

    def start_download(self, ls, can_download):
        self.update_inf_ui(ls)
        # self.progress += 5
        # self.spb.setValue(self.progress)

        if can_download:
            self.update_inf_ui(['[INFO] Start downloading the video...'])
            self.download_thread = DownloadThread(self.informationEdit, self.urls, **self.kwargs)
            self.download_thread.finishSignal.connect(self.finish_download)
            self.download_thread.start()

            percent = 0
            is_exits = False
            show_inf = ''

            progressDialog = QProgressDialog(self)
            progressDialog.setAutoReset(True)
            progressDialog.setWindowModality(Qt.WindowModal)
            progressDialog.setMinimumDuration(5)
            progressDialog.setWindowTitle(self.tr('Progress'))
            progressDialog.setLabelText(self.tr('Downloading file to ' + base_dir + ' ...'))
            progressDialog.setCancelButtonText(self.tr("Cancel"))
            progressDialog.setRange(0, 100)

            while percent < 100 and not is_exits:
                percent = status.get_percent()
                is_exits = status.get_exist()
                if is_exits:
                    show_inf = '[TIP] File already exists'
                    percent = 100
                progressDialog.setValue(percent)
                QThread.msleep(100)
                if progressDialog.wasCanceled():
                    return
            self.update_inf_ui([show_inf])
        else:
            # self.progress = 0
            # self.spb.setValue(self.progress)
            return

    def edittext2bottom(self):
        c = self.informationEdit.textCursor()
        self.informationEdit.setTextCursor(c)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = GUI()
    sys.exit(app.exec_())
