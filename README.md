Rambam's Mishneh Torah.

Note that all .htm files are copyright Mechon Mamre, downloaded from http://www.mechon-mamre.org/dlit.htm.

Download them, and prepended r_ to all of the files, and put them in res/raw (.pl script available in raw)


Notes for future reference:
To create the titles.txt file:

grep TITLE *.htm
Then search and replace :<TITLE> with \t, and </TITLE> with empty string.
Stripping out the section before the first hyphen is probably also a good idea, because it just says "Mishneh Torah" on every line.

At some point, I should split on the hyphen, and make a two-level menu.  I could just do that in Java, though.
