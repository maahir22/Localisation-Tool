GitHub Localisation Tool
<br>
Track Automation
<br>
Localisation is a problem faced by developers from all over the world. A service that was initially created for one region, now needs to be launched in other regions, but this is not easy as many a times messages to users are hardcoded within the codebase.
Our tool aims to eradicate this by using a preventive strategy,  no hardcoded strings. All strings and messages will be driven by database according to visitor’s region.
<br>
Ex. String welcome = “Welcome to our page” should be automatically displayed as “Bienvenida a nuestra pagina” to a visitor from Spain.
Hence, the actual code should have been DB Driven i.e. String welcome = message.getWelcome(user.getLocation()); This is called as localisation. Our tool does this by automatically fetching open PRs & using RegEX/NLP to detect if a given string is hardcoded, and then uses Google Translate API to insert it's translations into a centralised DB.
<br>

Tech Stack Used
1) Github Developer APIs - CRUD PRS, Comments
2) Java Spring/ Spring Framework - CRON Jobs, Github Developer API Integration
3) Hibernate - Keeping list of active repositories, insertion into central DB
4) Python & Flask - File Parsing and RegEX/NLP Response Handling
6) Lexical Analysis - Tokenisation(NLP)
7) Jenkins* - HTTP Plugin as a part of pipeline to check if localisation is present.

<br>
Team Description
Team UAT

<br>
Screenshots and reference Images
<img src = "https://i.ibb.co/ckc9VxH/Screen-Shot-2022-04-17-at-2-08-38-PM.png">
<img src = "https://i.ibb.co/1mJnj6f/Screen-Shot-2022-04-17-at-2-07-06-PM.png">
It includes some screenshots and images of your project.
<br>
Links and References:
GitHub : https://github.com/maahir22/Localisation-Tool/edit/master/README.md
