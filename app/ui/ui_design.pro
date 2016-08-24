#-------------------------------------------------
#
# Project created by QtCreator 2016-05-19T18:45:36
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ui_design
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp

HEADERS  += mainwindow.h

FORMS    += main_window.ui \
    about_widget.ui \
    files_list_dialog.ui \
    task_item.ui

RESOURCES += \
    icon.qrc
