#!/usr/bin/env python2
from PIL import Image
from glob import glob
import shutil
MAX_IMG_WIDTH = 500
MAX_IMG_HEIGHT = 500

if __name__ == '__main__':
	for filename in glob("*.jpg") + glob("*.png"):
		back_ = filename + ".back"
		shutil.move(filename, back_)
		img = Image.open(back_)
		img.thumbnail((MAX_IMG_WIDTH, MAX_IMG_HEIGHT), Image.ANTIALIAS)
		img.save(filename)
