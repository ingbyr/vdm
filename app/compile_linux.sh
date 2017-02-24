#!/usr/bin/env bash
pyinstaller main.py -F --hiddenimport app.you_get.extractors --noconsole -n YouGet-0.1.1Linux --icon /home/ing/PycharmProjects/GUI-YouGet/app/favicon.ico
