# Spring-Security-JDBC-Authentication-in-MySQL


1. Create users table and dummy credentials
The credentials should be stored in database, so let’s create a new table named users with the following columns:

![image](https://user-images.githubusercontent.com/43759116/124358198-da16ae00-dc3c-11eb-8afd-b5a294cc1507.png)

2. Configure Data Source Properties

Next, specify database connection information in the application.properties file like this:
    spring.datasource.url=jdbc:mysql://localhost:3306/testdb
    spring.datasource.username=root
    spring.datasource.password=password
    Update the URL, username and password according to your MySQL database.
 
3. Declare Dependencies for Spring Security and MySQL JDBC Driver
To use Spring Security APIs for the project, declare the following dependency in the pom.xml file:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
And to use JDBC with Spring Boot and MySQL:

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
Note that the dependency versions are already defined by the Spring Boot starter parent project.
 
4. Configure JDBC Authentication Details
To use Spring Security with form-based authentication and JDBC, create the WebSecurityConfig class as follows:
package net.codejava;
 
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
    @Autowired
    private DataSource dataSource;
     
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            .dataSource(dataSource)
            .usersByUsernameQuery("select username, password, enabled from users where username=?")
            .authoritiesByUsernameQuery("select username, role from users where username=?")
        ;
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin().permitAll()
            .and()
            .logout().permitAll();     
    }
}
This security configuration class must be annotated with the @EnableWebSecurity annotation and is a subclass of WebSecurityConfigurerAdapter.
An instance of DataSource object will be created and injected by Spring framework:
  @Autowired
  private DataSource dataSource;
  It will read database connection information from the application.properties file.
  And to configure authentication using JDBC, write the following method:
  
@Autowired
public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
        .dataSource(dataSource)
        .usersByUsernameQuery("select username, password, enabled from users where username=?")
        .authoritiesByUsernameQuery("select username, role from users where username=?")
    ;
}

As you can see, we need to specify a password encoder (BCrypt is recommended), data source and two SQL statements: the first one selects a user based on username, and the second one selects role of the user. Note that Spring security requires the column names must be username, password, enabled and role.
And to configure form-based authentication, we override the configure(HttpSecurity) method as follows:

@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin().permitAll()
        .and()
        .logout().permitAll();     
}

Here, we specify that all requests must be authenticated, meaning the users must login to use the application. The default login form provided by Spring security is used.
To show username of the logged in user, write the following code in a Thymeleaf template file:
<h3 th:inline="text">Welcome [[${#httpServletRequest.remoteUser}]]</h3>
And to add a Logout button:
  <form th:action="@{/logout}" method="post">
    <input type="submit" value="Logout" />
  </form>
As you can see, Spring Security will handle login and logout for the application. We don’t have to write repetitive code, just specify some configurations.

![image](https://user-images.githubusercontent.com/43759116/124358400-b738c980-dc3d-11eb-8ec5-426206867b67.png)


