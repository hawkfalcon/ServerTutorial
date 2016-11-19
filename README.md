# ServerTutorial
==============

Have you ever wanted to make a cool tutorial for players that doesn't use boring signs? Now you can make one!

### Description

**NOW 1.11 COMPATIBLE**

This is the ultimate tutorial plugin for Bukkit. It allows easy creation of 'views' which the player sees after typing /tutorial. 

### Features
* Easy tutorial creation
* Unlimited tutorials, unlimited scenes per tutorial
* Two types of tutorials (CLICK/TIME)
* Two types of view (TEXT/TITLE/ACTIONBAR)
* Show a player a tutorial when they first join your server
* 100% configurable
* Economy tie-in
* Easy to set up and use
* Give your players a tutorial that isn't boring
* Versatile - Use it for a play, or a plugin tutorial, or a welcome tutorial, or anything you can think of.
* Lightweight

###Commands
|Command|Description|
|-------|-----------|
|/tutorial|List tutorials|
|/tutorial help|Help page|
|/tutorial <name>|Puts you in the tutorial|
|/tutorial create <name>|Create a new tutorial|
|/tutorial addview <name>|Add a view to a tutorial|
|/tutorial remove <name>|Removes a tutorial|
|/tutorial remove <name> <view number>|Removes a tutorial view|

### Permissions
|Permission|Command|Description|
|----------|-------|-----------|
|tutorial.create|Use /tutorial create <name>| Creating a tutorial|
|tutorial.use|Use /tutorial <name>|Allows a player to use any tutorial|
|tutorial.tutorial.<name>|Use /tutorial <name>|Allows a player to use a specific tutorial| 
|tutorial.remove|Use /tutorial remove <name>|Remove a tutorial|

### Tutorial type
|Tutorial Type|Description|
|-------------|-----------|
|CLICK|Right click an item to go to the next view|
|TIME|Automatically go to the next view after a customizable amount of time|

### View types
|View Type|Description|
|---------|-----------|
|TEXT|Simple chat text|
|TITLE|Title (subtitle)|
|ACTIONBAR|Action bar above item bar|

### Miscellaneous
* To make a sign, the top line must be [Tutorial] - (changeable in the config), then the second line must be the name of the tutorial.
* To make a tutorial start when a player first joins the server, edit the appropriate fields in the config.

We also include an update checker. It will download the latest updates for you. If you'd like to disable it, go into /plugins/ServerTutorial/config.yml

### Bugs? Suggestions?
Make a new issue

### Maven Repo
        <repository>
            <id>ServerTutorial</id>
            <url>http://repo.frostalf.net</url>
        </repository>
        <dependency>
            <groupId>pw.hwk</groupId>
            <artifactId>ServerTutorial</artifactId>
            <version>{version or LATEST}</version>
        </dependency>
