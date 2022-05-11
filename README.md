# Shopme
clone this project

# Setting up project

Create database "shopme" and "shopme_test"

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/database.png)

Uncomment run method from the Initializer class. This is meant to run first-time only.

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/initializer.png)

Uncomment ddl-auto in application.properties

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/ddl-auto.png)

Run ShopmeBackEndApplication.java

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/console.png)

After running first time, comment the method body of run and ddl-auto.

# Or test add many users right away

testAddManyUsers() test method from UserServiceTest. Be sure it succeeds.

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/testaddrolesthenusers.png)

# Using the project

Go to http://localhost:8080/ShopmeAdmin/Login. Use the credentials of the root user which was saved to DB 
when the run method from the Initializer.java was called first time.

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/login.png)

Users page

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/users.png)

Add user form

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/create-users.png)

Edit user form

![Initializer](https://raw.githubusercontent.com/raymond-tiongco/shopme/daryll-shopme/ShopmeWebParent/ShopmeBackEnd/guides/edit-user.png)

