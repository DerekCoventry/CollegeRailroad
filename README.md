I either need the following things added to the database or I have tried to get them from the database and haven't been able to
===================
- User: name (John Smith)
- User: email (I can get them for any books the user has, but I couldn't figure out how to get it for the user if they have no books. I need this for the profiles)
- Book: Author (Need this for searching. There are three search options: title, author, and ISBN. ISBN will just webscrape the data from the website then combine the title and author it gets from that website)
- Book: Latitude/Longitude

List of things I have left to do:
===================
- Get email and name for profile (and set name)

- Sort by author and ISBN (which will just sort by title+author)

- Display locations from books
	    Get city from latitude and longitude in database and show locations of books

Other stuff
=====================
- Change password from edit profile page (I can't really do this because I don't know the routes of the website. I assume you would do an http post and send the three text fields needed)
