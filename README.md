# Blog Application

This is a simple blog application built with Spring Boot. It allows users to create and view blog posts.

## Features

- Create new blog posts
- View all blog posts
- View a single blog post by ID

## Technologies Used

- Spring Boot
- Spring Data JPA
- Thymeleaf
- MySQL Database (for development)

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd blog-app
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

### Accessing the Application

Once the application is running, you can access it at:
```
http://localhost:8080
```

### Database

The application uses an in-memory H2 database for storing blog posts. You can access the H2 console at:
```
http://localhost:8080/h2-console
```
Use the following credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (leave blank)

## Usage

- To create a new blog post, fill out the form on the main page and submit.
- To view all blog posts, simply navigate to the main page.
- To view a specific blog post, click on the post title from the list.

## Contributing

Feel free to submit issues or pull requests for any improvements or features you'd like to see!

## License

This project is licensed under the MIT License.
