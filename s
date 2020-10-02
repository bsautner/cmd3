#!/bin/bash
rm *.snap
sudo snap remove cmd3
git add .
git commit -m "snap"
git push origin master
snapcraft clean
snapcraft
sudo snap install cmd3*.snap --devmode --dangerous