Localisation is a problem faced by developers from all over the world. A service that was initially created for one region, now needs to be launched in other regions, but this is not easy as many a times messages to users are hardcoded within the codebase.
Our tool aims to eradicate this by using a preventive strategy,  no hardcoded strings. All strings and messages will be driven by database according to visitor’s region.
Ex. String welcome = “Welcome to our page” should be automatically displayed as “Bienvenida a nuestra pagina” to a visitor from Spain.
Hence, the actual code should have been DB Driven i.e. String welcome = message.getWelcome(user.getLocation()); This is called as localisation.


Execution Flow:

1) Developer creates a PR on Github Repo.

2) Our program fetches the code changes in the PR via a periodic CRON Job.

3) The Code Changes are then ran on our NLP(Tokenisation) & RegEX Model.

4) If current statement is found out to be a Language HardCoded statement, then merging access is blocked and user is notified.

5) Our program identifies the string & language and sends it to Google Translate API, which then translates it into five languages of developer’s choice.

6) Then these messages are allocated a message ID & locations(en,uk,us,in) and inserted into a central database.

7) The developer can then use location information sent by frontend and use it to display a region friendly message to the visitor.
