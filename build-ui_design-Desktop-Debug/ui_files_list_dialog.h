/********************************************************************************
** Form generated from reading UI file 'files_list_dialog.ui'
**
** Created by: Qt User Interface Compiler version 5.5.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_FILES_LIST_DIALOG_H
#define UI_FILES_LIST_DIALOG_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QComboBox>
#include <QtWidgets/QDialog>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QTextEdit>

QT_BEGIN_NAMESPACE

class Ui_FilesListDialog
{
public:
    QTextEdit *text_files_list;
    QLabel *label;
    QLabel *label_2;
    QPushButton *push_button_confirm;
    QPushButton *push_button_cancel;
    QComboBox *combo_box_options;

    void setupUi(QDialog *FilesListDialog)
    {
        if (FilesListDialog->objectName().isEmpty())
            FilesListDialog->setObjectName(QStringLiteral("FilesListDialog"));
        FilesListDialog->resize(400, 400);
        text_files_list = new QTextEdit(FilesListDialog);
        text_files_list->setObjectName(QStringLiteral("text_files_list"));
        text_files_list->setGeometry(QRect(20, 20, 361, 291));
        text_files_list->setReadOnly(true);
        label = new QLabel(FilesListDialog);
        label->setObjectName(QStringLiteral("label"));
        label->setGeometry(QRect(20, 310, 361, 51));
        label_2 = new QLabel(FilesListDialog);
        label_2->setObjectName(QStringLiteral("label_2"));
        label_2->setGeometry(QRect(20, 360, 51, 20));
        push_button_confirm = new QPushButton(FilesListDialog);
        push_button_confirm->setObjectName(QStringLiteral("push_button_confirm"));
        push_button_confirm->setGeometry(QRect(220, 360, 80, 22));
        push_button_cancel = new QPushButton(FilesListDialog);
        push_button_cancel->setObjectName(QStringLiteral("push_button_cancel"));
        push_button_cancel->setGeometry(QRect(310, 360, 80, 22));
        combo_box_options = new QComboBox(FilesListDialog);
        combo_box_options->setObjectName(QStringLiteral("combo_box_options"));
        combo_box_options->setGeometry(QRect(78, 360, 101, 22));

        retranslateUi(FilesListDialog);

        QMetaObject::connectSlotsByName(FilesListDialog);
    } // setupUi

    void retranslateUi(QDialog *FilesListDialog)
    {
        FilesListDialog->setWindowTitle(QApplication::translate("FilesListDialog", "Files List", 0));
        label->setText(QApplication::translate("FilesListDialog", "Please select a spicifed files to download.", 0));
        label_2->setText(QApplication::translate("FilesListDialog", "Select", 0));
        push_button_confirm->setText(QApplication::translate("FilesListDialog", "Download", 0));
        push_button_cancel->setText(QApplication::translate("FilesListDialog", "Cancel", 0));
    } // retranslateUi

};

namespace Ui {
    class FilesListDialog: public Ui_FilesListDialog {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_FILES_LIST_DIALOG_H
