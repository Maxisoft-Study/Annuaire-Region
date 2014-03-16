#!/usr/bin/env python2
from lxml import etree
import shutil
from glob import glob
if __name__ == '__main__':
	shutil.move('regions.xml', 'regions.back')
	tree = etree.parse('regions.back')
	root = tree.getroot()
	for carteE in root.xpath("//carte"):
		carteE.text = unicode(carteE.text, 'utf-8').lower()

	print etree.tostring(root)
	tree.write('regions.xml', pretty_print=True)

	for filename in glob("*.jpg") + glob("*.png"):
			shutil.move(filename, filename.lower())