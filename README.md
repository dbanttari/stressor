# stressor
Java-based programmable stress tester

Requirements
------------
- Java 1.7 or better
- [Maven]

Installation
------------
- Check out [stressor] into your workspace:
```
git checkout https://github.com/dbanttari/stressor.git
```
- Import the Stressor artifact into your local Maven repo by running `./build.sh` in the Stressor directory
- Create your own implementation project, and add [stressor] to its build path.  In [Maven], you might add this:
```
  <dependency>
    <groupId>net.darylb</groupId>
    <artifactId>stressor</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
```

Implementation
--------------
To build a stressor test, you will need to create a minimum of four classes, and usually a stressor.properties file.
- A TestDefinition to return a StoryFactory
- A StoryFactory to configure Stories
- A Story will return one or more Actions
- An Action will do some small task (eg, load a page)

Helpers exist for Actions:
- An HttpGetAction will retrieve a page
- An HttpPostAction will post a form
 - A JsonForm makes posting JSON easy
 - UrlEncodedForm makes posting application/x-www-form-urlencoded forms easy
- An HttpPutAction will 'put' a file to a website
  - see org.apache.http.entity.FileEntity.FileEntity(File)
- A DatabaseAction will pass in a shiny new Connection for you to (ab)use

