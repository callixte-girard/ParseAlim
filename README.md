This little pure Java software parses food database at https://informationsnutritionnelles.fr/ (it's in French.)

After parsing is done, it saves :
- the arraylist containing all the parsed food objects (class is called Aliment in code), for loading it directly next time instead of scraping the whole database again each time.
- a CSV files with chosen attributes (which can be easily modified directly in code). Please keep in mind that each Aliment object has something like 30 or 40 nutrients, hence the need to filter which data is important for you or not.
- a JSON file with chosen attributes.

The final goal of this tool was to have a quickly usable database in order to create a nutrient-adviser app, but this project has been abandonned (or, at least, postponed) because it was too time-demanding.
