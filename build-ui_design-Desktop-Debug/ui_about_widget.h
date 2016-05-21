/********************************************************************************
** Form generated from reading UI file 'about_widget.ui'
**
** Created by: Qt User Interface Compiler version 5.5.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ABOUT_WIDGET_H
#define UI_ABOUT_WIDGET_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QLabel>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_AboutForm
{
public:
    QLabel *label_logo;
    QLabel *label_info;
    QLabel *label_title;
    QLabel *label_version;

    void setupUi(QWidget *AboutForm)
    {
        if (AboutForm->objectName().isEmpty())
            AboutForm->setObjectName(QStringLiteral("AboutForm"));
        AboutForm->resize(500, 250);
        label_logo = new QLabel(AboutForm);
        label_logo->setObjectName(QStringLiteral("label_logo"));
        label_logo->setGeometry(QRect(10, 70, 131, 121));
        label_logo->setStyleSheet(QStringLiteral("image: url(:/res/favicon.ico);"));
        label_info = new QLabel(AboutForm);
        label_info->setObjectName(QStringLiteral("label_info"));
        label_info->setGeometry(QRect(170, 50, 311, 191));
        label_info->setTextFormat(Qt::RichText);
        label_title = new QLabel(AboutForm);
        label_title->setObjectName(QStringLiteral("label_title"));
        label_title->setGeometry(QRect(170, 10, 121, 41));
        QFont font;
        font.setPointSize(16);
        label_title->setFont(font);
        label_title->setTextFormat(Qt::PlainText);
        label_title->setIndent(-1);
        label_version = new QLabel(AboutForm);
        label_version->setObjectName(QStringLiteral("label_version"));
        label_version->setGeometry(QRect(300, 20, 131, 20));

        retranslateUi(AboutForm);

        QMetaObject::connectSlotsByName(AboutForm);
    } // setupUi

    void retranslateUi(QWidget *AboutForm)
    {
        AboutForm->setWindowTitle(QApplication::translate("AboutForm", "About", 0));
        label_logo->setText(QApplication::translate("AboutForm", "<html><head/><body><p><br/></p></body></html>", 0));
        label_info->setText(QApplication::translate("AboutForm", "<html><head/><body><p><a href=\"http://www.ingbyr.tk/2016/05/16/youget/\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">GUI-YouGet</span></a><span style=\" font-size:11pt;\"> is a video download software </span></p><p><span style=\" font-size:11pt;\">Follow open source License </span><a href=\"https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">MIT</span></a></p><p><span style=\" font-size:11pt;\">Based on the program </span><a href=\"https://github.com/soimort/you-get\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">you-get</span></a></p><p><span style=\" font-size:11pt;\">Coder: InG_byr ( </span><a href=\"http://www.weibo.com/zwkv5\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">Sina Weibo</span></a><span style=\" font-size:11pt;\"> )</span></p><p><span style=\" font-size:11pt;\">Designer: InG_byr ( </span><a href=\"http://ww"
                        "w.weibo.com/zwkv5\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">Sina Weibo</span></a><span style=\" font-size:11pt;\"> )</span></p></body></html>", 0));
        label_title->setText(QApplication::translate("AboutForm", "GUI-YouGet", 0));
        label_version->setText(QString());
    } // retranslateUi

};

namespace Ui {
    class AboutForm: public Ui_AboutForm {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ABOUT_WIDGET_H
