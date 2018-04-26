doc=Thesis
all : $(doc)

$(doc) : $(doc).tex
	pdflatex $(doc).tex
	bibtex $(doc).aux
	pdflatex $(doc).tex
	pdflatex $(doc).tex
	# evince $(doc).pdf

clean :
	rm -f $(doc).log
	rm -f $(doc).aux
	rm -f $(doc).dvi
	rm -f $(doc).pdf
	rm -f $(doc).toc
	rm -f $(doc).lof
	rm -f $(doc).lot
	rm -f chapter?.log
	rm -f chapter?.aux
	rm -f chapter?.dvi
	rm -f chapter?.pdf
	rm -f chapter?.toc
	rm -f *.aux
	rm -f $(doc).bbl
	rm -f $(doc).blg
	rm -f $(doc).out
