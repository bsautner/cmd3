#!/bin/bash
rm *.snap
git add .
git commit -m "snap"
git push origin master
snapcraft clean
snapcraft