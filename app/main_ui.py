# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import json
from urllib import request
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
        # init main
        self.init_main()

        # menu
        self.init_menu()

        # move to the center
        self.center()

        # status bar
        self.statusBar()
        self.statusBar().showMessage('Ready')

        self.setWindowTitle('YouGet')
        mlog.debug('>>>base dir: ' + base_dir)
        self.setWindowIcon(QIcon(':res/favicon.ico'))
        self.show()

        self.ing_main.update_inf_ui(['[TIP] Input the url, then click the download button'])

    def init_main(self):
        self.ing_main = InGMain()
        self.setCentralWidget(self.ing_main)

        self.about_message = AboutMessage()

    def init_menu(self):
        about_action = QAction('&About', self)
        about_action.setStatusTip('About this application')
        about_action.triggered.connect(self.about_message.show)

        exit_action = QAction('&Exit', self)
        # exit_action.setShortcut('Ctrl+Q')
        exit_action.setStatusTip('Exit application')
        exit_action.triggered.connect(qApp.quit)

        file_path_action = QAction('&FilePath', self)
        file_path_action.setStatusTip('Set file path')
        file_path_action.triggered.connect(self.get_file)

        report_bugs_action = QAction('&Report bugs', self)
        report_bugs_action.setStatusTip('Report bugs on GitHub')
        report_bugs_action.triggered.connect(self.report_bugs)

        check_for_updates_action = QAction('&Check for updates', self)
        check_for_updates_action.setStatusTip('Check for updates and get the latest')
        check_for_updates_action.triggered.connect(self.check_for_updates)

        menu_bar = self.menuBar()

        setting_menu = menu_bar.addMenu('&Setting')
        setting_menu.addAction(file_path_action)

        help_menu = menu_bar.addMenu('&Help')
        help_menu.addAction(check_for_updates_action)
        help_menu.addAction(report_bugs_action)
        help_menu.addAction(about_action)
        help_menu.addAction(exit_action)

    def check_for_updates(self):
        self.ing_main.update_inf_ui(['[INFO] Check for updates, please wait a moment'])

        try:
            with open('version.json', 'r') as f:
                local_inf = json.load(f)
            self.ing_main.update_inf_ui(['[INFO] Local version is ' + local_inf['version']])
        except Exception:
            for item in sys.exc_info():
                mlog.error('>>>main ui: ' + str(item))
            self.ing_main.update_inf_ui(['[ERROR] Get local version failed'])
            return

        try:
            with request.urlopen('https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/version.json') as f:
                raw_inf = str(f.read())[2:-1]
                mlog.debug(str(f.read())[2:-1])
                remote_inf = json.loads(raw_inf)
                mlog.debug('>>>main ui: remote version is ' + remote_inf['version'])
            self.ing_main.update_inf_ui(['[INFO] Latest version is ' + remote_inf['version']])
        except Exception:
            for item in sys.exc_info():
                mlog.error('>>>main ui: ' + str(item))
            self.ing_main.update_inf_ui(['[ERROR] Get latest version failed', '[ERROR] Check you internet'])
            return

        if local_inf['version'] >= remote_inf['version']:
            self.ing_main.update_inf_ui(['[TIP] No available updates'])
        else:
            self.ing_main.update_inf_ui(['[TIP] Ready to get latest version'])
            QDesktopServices.openUrl(QUrl('http://www.ingbyr.tk/2016/05/16/youget/'))

    def report_bugs(self):
        QDesktopServices.openUrl(QUrl('https://github.com/ingbyr/GUI-YouGet/issues'))

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
        if fname:
            self.ing_main.update_inf_ui(['[INFO] Set file path to: ' + fname])
            self.ing_main.set_file_path(fname)


class InGMain(QWidget):
    def __init__(self):
        super().__init__()

        self.init_ui()
        self.init_data()

    def init_ui(self):
        self.urlEdit = QLineEdit()
        self.searchEdit = QLineEdit()
        self.informationEdit = QTextEdit()
        self.informationEdit.setReadOnly(True)
        self.informationEdit.setOverwriteMode(False)

        url = QLabel('Url')
        search = QLabel('Search')
        information = QLabel('Information')
        self.download_btn = QPushButton('Download')
        self.download_btn.setStatusTip('Download videos')
        self.download_btn.clicked.connect(self.gui_download_by_url)
        search_btn = QPushButton('Search')
        search_btn.setStatusTip('Search on Google')

        # TODO: search viedo on
        self.searchEdit.setStyleSheet("color:gray")
        self.searchEdit.insert('Don\'t work in this version')
        self.searchEdit.setReadOnly(True)

        grid = QGridLayout()
        grid.setSpacing(10)
        grid.addWidget(url, 1, 0)
        grid.addWidget(self.urlEdit, 1, 1)
        grid.addWidget(self.download_btn, 1, 2)
        grid.addWidget(search, 2, 0)
        grid.addWidget(self.searchEdit, 2, 1)
        grid.addWidget(search_btn, 2, 2)
        grid.addWidget(information, 4, 0)
        grid.addWidget(self.informationEdit, 4, 1, 4, 1)

        self.setLayout(grid)

    def init_data(self):
        self.kwargs = {'output_dir': base_dir + '/YouGetVideos',
                       'merge': True,
                       'json_output': False,
                       'caption': True}

    def gui_download_by_url(self):
        self.download_btn.setEnabled(False)
        status.set_default()
        self.update_inf_ui(['[INFO] Here we go ~',
                            '[INFO] Get the information by url...',
                            '[INFO] Please wait a moment...'])

        self.urls = str(self.urlEdit.text()).split(';')

        self.get_inf_thread = GetVideoInfoThread(self.informationEdit, self.urls, **self.kwargs)
        self.get_inf_thread.finish_signal.connect(self.start_download)
        self.get_inf_thread.start()

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
        if not status.get_stop_thread():
            self.update_inf_ui(ls)
        self.download_btn.setEnabled(True)
        r_obj.flush()

    def start_download(self, ls, can_download):
        r_obj.flush()
        self.update_inf_ui(ls)
        if can_download:
            self.update_inf_ui(['[INFO] Start downloading the files...'])
            self.download_thread = DownloadThread(self.informationEdit, self.urls, **self.kwargs)
            self.download_thread.setTerminationEnabled(True)
            self.download_thread.finish_signal.connect(self.finish_download)
            self.download_thread.start()

            self.show_progress_bar()
            # percent = 0
            # is_exits = False
            # show_inf = ''
            #
            # progressDialog = QProgressDialog(self)
            # progressDialog.setAutoReset(True)
            # progressDialog.setWindowModality(Qt.WindowModal)
            # progressDialog.setMinimumDuration(5)
            # progressDialog.setWindowTitle(self.tr('Progress'))
            # progressDialog.setLabelText(self.tr('Downloading files to ' + self.kwargs['output_dir'] + ' ...'))
            # progressDialog.setCancelButtonText(self.tr("Cancel"))
            # progressDialog.setRange(0, 100)
            #
            # while percent < 100 and not is_exits:
            #     percent = status.get_percent()
            #     is_exits = status.get_exist()
            #     if is_exits:
            #         show_inf = '[TIP] Files already exists'
            #         percent = 100
            #     progressDialog.setValue(percent)
            #     QThread.msleep(100)
            #     if progressDialog.wasCanceled():
            #         pass
            #         # todo: can not cancel
            #         status.set_stop_thread(True)
            #         self.download_thread.wait()
            #         mlog.debug('>>>main ui: stop the download thread')
            #         mlog.debug('>>>main ui: download_thread.isRunning ' + str(self.download_thread.isRunning()))
            #         percent = 100
            #         self.stop_by_user()
            #
            # self.update_inf_ui([show_inf])
        else:
            self.download_btn.setEnabled(True)
            return

    def show_progress_bar(self):
        percent = 0
        is_exits = False
        show_inf = ''

        progressDialog = QProgressDialog(self)
        progressDialog.setAutoReset(True)
        progressDialog.setWindowModality(Qt.WindowModal)
        progressDialog.setMinimumDuration(5)
        progressDialog.setWindowTitle(self.tr('Downloading'))
        progressDialog.setLabelText(self.tr('Current speed: '))
        progressDialog.setCancelButtonText(self.tr("Cancel"))
        progressDialog.setRange(0, 100)

        while percent < 100 and not is_exits:
            percent = status.get_percent()
            is_exits = status.get_exist()
            if is_exits:
                show_inf = '[TIP] Files already exists'
                percent = 100
            progressDialog.setValue(percent)
            progressDialog.setLabelText(self.tr('Current speed: ' + str(status.get_speed())))
            QThread.msleep(100)
            if progressDialog.wasCanceled():
                pass
                # todo: can not cancel
                status.set_stop_thread(True)
                self.download_thread.wait()
                mlog.debug('>>>main ui: stop the download thread')
                mlog.debug('>>>main ui: download_thread.isRunning ' + str(self.download_thread.isRunning()))
                percent = 100
                self.stop_by_user()

        self.update_inf_ui([show_inf])

    def edittext2bottom(self):
        c = self.informationEdit.textCursor()
        self.informationEdit.setTextCursor(c)

    def set_file_path(self, path):
        self.kwargs['output_dir'] = path

    def stop_by_user(self):
        self.update_inf_ui(['[TIP] Force to stop the downloading', '[TIP] You can resume this download at any time'])
        self.download_btn.setEnabled(True)


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
        with open('version.json', 'r') as f:
            inf = json.load(f)
        ver = inf['version']
        message.setText(
            '<a><a href ="http://www.ingbyr.tk/2016/05/16/youget/">GUI-YouGet</a> is a video download software made by ingbyr</a><br><br>'
            '<a>Version ' + ver + ' | License </a><a href = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt">MIT</a><br><br>'
                                  '<a>Based on the open source program</a> <a href="https://github.com/soimort/you-get">you-get</a><br><br>'
                                  '<a>About me: &nbsp;&nbsp;&nbsp;</a>'
                                  '<a href="http://www.ingbyr.tk">My Blog</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://www.weibo.com/zwkv5">Sina Weibo</a>')

        grid.addWidget(laber, 1, 0)
        grid.addWidget(message, 1, 1)
        self.setLayout(grid)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = GUI()
    font = app.font()
    font.setPointSize(12)
    app.setFont(font)
    sys.exit(app.exec_())
