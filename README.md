# 멍이냥이
Community blog related to missing reporting and rescue of abandoned animals.

## Table of content

* [Background](#background)
* [Build & Run](#build--run)
* [Test Setting](#intellij-idea)
* [About project](#about-project)
  * [Home](#home)
  * [Lost](#lost)
  * [Bulletin & Like](#bulletin--like)
  * [Item & Message](#item--message)
  * [Inquiry & Comment](#inquiry--comment)
  * [User](#user)
* [Problem and Solution](#problem-and-solution)
* [In the project](#in-the-project)

## Background
On social media, I saw many people supporting and sponsoring the writer who said that he rescued and treated abandoned animals at his own expense.
Until I read that article, I didn't know that there were people who did such good deeds. I also didn't know that there are people who support people like the writer.
I started the project to help them, thinking that maybe more people are doing good deeds than I thought, and that more animals might be in danger.

## Build & Run
To build the source you will need to install JDK 11.

This is a web project using Spring Data JPA. After installing H2Database to run, you can use it in your local environment.
```
H2 JDBC URL: jdbc:h2:tcp://localhost/~/catdog
```

If you want to run in memory environment without DB installation, change application.yml to in-memory setting and run in memory environment.
```
spring:
  datasource:
    url: jdbc:h2:mem
```
After the DB setting is complete, you can run it.
```
$ ./gradlew bootRun
```

## Intellij IDEA
This setting makes it easier to run the test code out of the box.
```
// Gradle Build and run with IntelliJ IDEA
File > Preferences > Build, Execution, Deployment > Build Tools > Gradle > Run tests using > IntelliJ IDEA
```

## About project
### Home
![Home](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/34821469-0575-433e-a201-750a856ebe2d)
* Improved user convenience by adding a table so that users can move directly to articles by region

TODO
* Add blog guide

### Lost
![lost board](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/abebd1f4-6c77-446a-ad17-f10eec38e255)
* Companion animal missing report board
* Be able to communicate about missing animals through comment and reply functions.
* Be able to find boards user want using the search function.

TODO
* Notification function to receive immediately news of discovery

### Bulletin & Like
![bulletin board](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/c9938afb-2bc3-4125-b2e6-c2389a69cea3)
* A bulletin board that shares articles requesting rescue or doing good deeds for abandoned animals
* Be able to communicate with comments, and set it as favorites through the like function.

TODO
* Place popular boards at the top of the page list

### Item & Message
![item board and message](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/308abcf8-a563-4206-abd9-0ae8fe276c83)
* The sharing market helps each other rather than selling. Therefore, be able to only set an amount of up to 10,000 won or less.
* If there is something you want, you can click Like or communicate with the person sharing it through the message function.

![sold out](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/13c67ecd-a673-45c1-8974-93bacf1aade3)
* Items that have been shared will not receive any more messages through the sharing completion function.

### Inquiry & Comment
![inquiry and comment](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/bef6030e-89ee-4a10-bac5-78bd2b3c975d)
* Be able to set whether or not the inquiry board is confidential.
* All boards and comments can be edited and deleted only by the writer.

### User
![user info](https://github.com/uotoua/Helping-Abandoned-pets/assets/118912510/a0a63d73-6f43-493c-bf70-4a27873e86bf)
* Users can be active through simple membership registration and login.
* On My Page, users can see boards written and liked by themselves, and  change user information.

TODO
* User password encryption

## Problem and Solution
* [Here!](https://jhl221123.notion.site/e36180f879a544a292910bcb2dcf3135?pvs=4)

## In the project
> All of the pets in the description above are actually missing animals. Some pets have found their owners, while others have yet to return to their owners.
While working on the project, I saw various boards about missing pets, and it hurt to see  writers nickname that seemed to call their pets. For example bom~, and Cori?
Skills are, of course, important for developers. However, I think the desire to provide good service to customers is as important as skill.
I was able to improve my skills while progressing from development to deploy on my own. But above all, it was a good time to think about a good developer.
