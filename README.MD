### Task description
I need to realize console CRUD app.
It needs to interact with database and allow to perform some CRUD operations with the next entities:

```
Writer(id,name,List<Post> posts)
Post(id, content, List<Tag> tags, PostStatus status)
Tag(id,name)
PostStatus(enum ACTIVE,DELETED)
```
Requirements:
1. Need to follow MVC pattern (packages model,repository,service,controller,view)
2. [LiquidBase](https://www.liquibase.org/) shall be used for database migration
3. Service layer shall be covered with unit tests (JUnit + Mockito)
4. Maven shall be used for libraries import.

The project result is separate repository on GitHub, with:
- [x] task description
- [x] instruction for local run

Technologies: Java, MySQL, JDBC, Maven, Liquibase, JUnit, Mockito

###Instruction
1. Download project from GitHub
2. Open it in IntelliJ IDEA
3. Update dependencies for pom.xml file
4. Update "application.properties" with your DB credentials
5. Run maven build for "liquibase:update" plugin
6. Run tests located in 'test/java/service' package
7. Run "StartView" in "src/main/java/view" package
