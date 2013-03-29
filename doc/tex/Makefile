all:
	pdflatex iMonitor.tex
	open iMonitor.pdf
	latexdiff submitted_iMonitor.tex iMonitor.tex > diff.tex
	pdflatex diff.tex
	open diff.pdf
