# Maps

- Which partner’s Stars, Autocomplete, and Bacon were used (Remember that each partner must supply at least one of the three)

We used Autocomplete from Sang Ha's (sp86), Stars and Bacon from Jaehyun's (jjeon5).

- Known bugs


-Design details specific to your code, including how you fit each of the prior project’s codebases together


-Any runtime/space optimizations you made beyond the minimum requirements
- How to run your tests

For TA tests, ./tests/cs32-test ./tests/ta/*.test
For student tests, ./tests/cs32-test ./tests/student/*.test
Rest are standard JUnit tests that can be run with mvn package.

- Any tests you wrote and tried by hand
- How to build/run your program from the command line
0) Traffic Server
run on port 8080. our database is maps.sqlite3 (locally we had it on ./data/maps/maps.sqlite3)

Inside our traffic server code, we request to traffic server every ONESECOND and we do this by using the thread.sleep command on a separate thread. therefore when running system tests it may take an additional second after fully outputting the results because we safely terminate this separately running thread 

1) terminal
./run, then set database (ex: map data/maps/maps.sqlite3), then run 5 commands 
ways <lat1> <lon1> <lat2> <lon2>  (ex: ways 42.0 -71.0 43.0 -72.0)
nearest <latitude> <longitude> (ex: nearest 42.0 -71.0)
route <lat1> <lon1> <lat2> <lon2> (ex: route 42.0 -71.0 43.0 -72.0)
route "Street 1" "Cross-street 1" "Street 2" "Cross-street 2"
suggest <input street name> (ex: suggest Broadw)

2) GUI
./run --gui, go to localhost:4567/maps, wait for the page to load (it takes about 30 secs  - 1 min depending on computer) *** please wait until it loads without refreshing


-What browser you used to test your GUI (we prefer Chrome, but we'll accept other common web browsers)
We tested our GUI on chrome. 


-Checkstyle
In Main.java line 120, 