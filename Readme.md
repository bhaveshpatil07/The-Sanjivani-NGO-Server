# Sanjivani NGO - Spring Boot Project

This repository contains the source code for the Sanjivani NGO project, a comprehensive web server application built using Spring Boot. ReactJs Frontend repo is also created for consuming this service. Do visit it.

## Features

- **User Registration and Authentication**: The application provides a secure signup process with email verification. It uses Spring Security to ensure the safety of user data.
- **Donation Management**: The application records and manages donations made by users. It integrates with Razorpay for seamless and secure transactions.
- **Admin Dashboard**: An exclusive admin login is provided. Admins can perform a Google-like search functionality on users and view their data.
- **Data Storage**: The application uses MongoDB as its primary database, ensuring efficient storage and retrieval of user and donation data.

## Technologies Used

- **Spring Boot**: Used for creating the backend of the application.
- **MongoDB**: Used as the primary database for storing user and donation data.
- **Spring Security**: Used for secure user authentication and authorization.
- **Razorpay**: Integrated for handling donation transactions.
- **Java Mail**: Used for sending email verification when a user signs up.

## Setup

##### Clone the repository

```bash
git clone https://github.com/bhaveshpatil07/The-Sanjivani-NGO-Server.git
```

##### Move to the desired folder

```bash
cd NGOServer
```

##### Make sure you have jdk-17. Then go to `NGOServer\src\main\resources\application.properties`
```
 Update your DB_URI, EMAIL, Password, Razorpay Credentials.
```
##### Now we're almost done run the `NGOServerApplication.java` file
##### Your server is live at
```bash
http://localhost:8080/api/v1/
```
---


This project aims to provide a robust and secure platform for managing the operations of the Sanjivani NGO. Contributions are welcome!


### If you encounter any difficulty running it, feel free to contact on my email :smile:

### If you liked my work do give us a star :star::star::star: It Encourages us to do more :wink: :dizzy:
