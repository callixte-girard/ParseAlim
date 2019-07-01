This little pure Java software parses open source food database on this website : https://informationsnutritionnelles.fr/ (it's in French.)

After parsing is done, it saves :
- the arraylist containing all the parsed food objects (class is called Aliment in code), for loading it directly next time instead of scraping the whole database again each time.
- a CSV files with chosen attributes (which can be easily modified directly in code). Please keep in mind that each Aliment object has something like 30 or 40 nutrients, hence the need to filter which data is important for you or not.
- a JSON file with chosen attributes.

The idea is to have a quickly usable database in order to create various things. Get out your paring knives ;)
