doc=Thesis

C=\033[1;035m # Color
NC=\033[0m # No Color

default: compile

compile:
	@latexmk -silent -pdf -pdflatex='pdflatex -interaction=nonstopmode' $(doc).tex

error:
	@latexmk -pdf -pdflatex='pdflatex -file-line-error -interaction=errorstopmode' $(doc).tex

# Run the watch shell script that
# watches the current directory for changes and builds the PDF
.PHONY: watch
watch:
	@printf "$(C)Watching sources for changes$(NC)\n"
	@./watch

view:
	@xdg-open $(doc).pdf

# Clean all latex crap
clean:
	@rm -f *.aux
	@rm -f *.bbl
	@rm -f *.blg
	@rm -f *.dvi
	@rm -f *.fdb_latexmk
	@rm -f *.fls
	@rm -f *.lof
	@rm -f *.log
	@rm -f *.lot
	@rm -f *.out
	@rm -f *.toc
