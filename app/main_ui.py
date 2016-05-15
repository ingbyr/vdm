# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

from PyQt5.QtGui import *
from PyQt5.QtCore import *
from PyQt5.QtWidgets import *
from app.download_thread import *
from app import mlog, base_dir
import app.custom_you_get.status as status
import app.images_qr

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

        self.setWindowTitle('YouGet')
        mlog.debug('>>>base dir: ' + base_dir)
        self.setWindowIcon(QIcon(':res/favicon.ico'))
        self.show()

        self.ing_main.update_inf_ui(['[TIP] Welcome to use YouGet'])

    def init_main(self):
        self.ing_main = InGMain()
        self.setCentralWidget(self.ing_main)

    def init_menu(self):
        self.about_message = AboutMessage()

        about_action = QAction('&About', self)
        about_action.setStatusTip('About this application')
        about_action.triggered.connect(self.about_message.show)

        exit_action = QAction('&Exit', self)
        exit_action.setShortcut('Ctrl+Q')
        exit_action.setStatusTip('Exit application')
        exit_action.triggered.connect(qApp.quit)

        file_path_action = QAction('&FilePath', self)
        file_path_action.setStatusTip('Set file path')
        file_path_action.triggered.connect(self.get_file)

        menu_bar = self.menuBar()
        setting_menu = menu_bar.addMenu('&Setting')
        help_menu = menu_bar.addMenu('&Help')
        setting_menu.addAction(file_path_action)
        help_menu.addAction(about_action)
        help_menu.addAction(exit_action)

    # show the app in the center
    def center(self):
        self.setGeometry(300, 300, 700, 500)
        qr = self.frameGeometry()
        cp = QDesktopWidget().availableGeometry().center()
        qr.moveCenter(cp)
        self.move(qr.topLeft())

    def get_file(self):
        fname = QFileDialog.getExistingDirectory(self, caption='Select Path', directory='',
                                                 options=QFileDialog.ShowDirsOnly)
        self.ing_main.update_inf_ui(['[INFO] Set file path to: ' + fname])
        self.ing_main.set_file_path(fname)


class InGMain(QWidget):
    def __init__(self):
        super().__init__()

        self.urlEdit = QLineEdit()
        self.searchEdit = QLineEdit()
        self.informationEdit = QTextEdit()
        self.informationEdit.setReadOnly(True)
        self.informationEdit.setOverwriteMode(False)

        self.init_ui()
        self.init_data()

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
        grid.addWidget(information, 4, 0)
        grid.addWidget(self.informationEdit, 4, 1, 4, 1)

        self.setLayout(grid)

    def init_data(self):
        self.kwargs = {'output_dir': base_dir+'/YouGetVideos',
                       'merge': True,
                       'json_output': False,
                       'caption': True}

    def gui_download_by_url(self):
        status.set_default()
        self.update_inf_ui(['[TIP] Ready to start download',
                            '[INFO] Get the information of video...'])

        self.urls = str(self.urlEdit.text()).split(';')

        # show the result first
        try:
            self.get_inf_thread = GetVideoInfoThread(self.informationEdit, self.urls, **self.kwargs)
            self.get_inf_thread.finish_signal.connect(self.start_download)
            self.get_inf_thread.start()
        except Exception:
            mlog.error(sys.exc_info()[0])
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

    def start_download(self, ls, can_download):
        self.update_inf_ui(ls)

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
            progressDialog.setLabelText(self.tr('Downloading file to ' + self.kwargs['output_dir'] + ' ...'))
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
                    # todo: can not to cancel?
                    self.download_thread.exit()
            self.update_inf_ui([show_inf])
        else:
            return

    def edittext2bottom(self):
        c = self.informationEdit.textCursor()
        self.informationEdit.setTextCursor(c)

    def set_file_path(self, path):
        self.kwargs['output_dir'] = path


class AboutMessage(QWidget):
    def __init__(self):
        super().__init__()
        self.center()
        self.init_ui()

    # show the app in the center
    def center(self):
        self.setGeometry(300, 300, 500, 200)
        qr = self.frameGeometry()
        cp = QDesktopWidget().availableGeometry().center()
        qr.moveCenter(cp)
        self.move(qr.topLeft())

    def init_ui(self):
        self.setWindowTitle('About')
        self.setWindowIcon(QIcon(':res/favicon.ico'))

        grid = QGridLayout()
        grid.setSpacing(10)

        pixmap = QPixmap(':res/favicon.ico')
        laber = QLabel()
        laber.setPixmap(pixmap)

        message = QLabel()
        message.setOpenExternalLinks(True)
        message.setText(
            '<a>GUI-YouGet is a video download software written by ingbyr</a><br><br>'
            '<a>Version 0.1 License </a><a href = "https://zh.wikipedia.org/wiki/MIT%E8%A8%B1%E5%8F%AF%E8%AD%89">MIT</a><br><br>'
            '<a>Based on the open source program</a> <a href="https://github.com/soimort/you-get">you-get</a><br><br>'
            '<a>About me: </a>'
            '<br><a href="http://www.ingbyr.tk">My Blog</a>'
            '<br><a href="http://www.weibo.com/zwkv5">Sina Weibo</a>')

        grid.addWidget(laber, 1, 0)
        grid.addWidget(message, 1, 1)
        self.setLayout(grid)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = GUI()
    sys.exit(app.exec_())
