# Crime Record Management System

A hybrid console and web-based Java application for managing criminal records with role-based access control, MongoDB integration, and Spring Boot web interface.

## Prerequisites

1. **Java 11 or higher** - Make sure Java is installed and `JAVA_HOME` is set
2. **Maven 3.6+** - For building and running the application
3. **MongoDB** - Running on localhost:27017 (default port)

## Setup Instructions

### 1. Start MongoDB
Make sure MongoDB is running on your system:
```bash
mongod
```

### 2. Initialize Database
Run the MongoDB setup script to create collections and sample data:
```bash
mongo < scripts/mongodb-setup.js
```

### 3. Compile the Application

**Using Maven:**
```bash
mvn clean compile
```

**On Linux/Mac:**
```bash
chmod +x scripts/compile.sh
./scripts/compile.sh
```

**On Windows:**
```cmd
scripts\compile.bat
```

### 4. Run the Application

**Option 1: Run Console Application (Recommended):**
```bash
# Run the console interface
mvn exec:java -Dexec.mainClass="com.crimemanagement.CrimeManagementApplication"
```

**Option 2: Run Web Application:**
```bash
# Run the web interface independently
mvn spring-boot:run
```

**Alternative Scripts:**

**On Linux/Mac:**
```bash
chmod +x scripts/run.sh
./scripts/run.sh  # Runs console application
```

**On Windows (CMD):**
```cmd
scripts\run.bat  # Runs console application
```

**On Windows (PowerShell):**
```powershell
# Chain commands with ';' (PowerShell doesn't support '&&')
Set-Location E:\inteconsole\console; mvn -q -DskipTests clean spring-boot:run
```

**Note:** 
- The console app launches the web interface when you choose "View Crime Records (Web Interface)" and auto-opens your browser.
- If you want to run only the web interface, use `mvn spring-boot:run`.
- When launched from the console, the web server picks a free port automatically and opens the correct URL (for example `http://localhost:56589/crime-records`). When started directly with Maven, the default is `http://localhost:8080/crime-records`.

## Default Users

The system comes with pre-configured users:

| Email | Password | Role |
|----------|----------|------|
| admin@gmail.com | admin123 | Admin |
| special@gmail.com | special123 | Special User |
| user@gmail.com | user123 | Normal User |

## Features

- **Role-based Access Control**: Admin, Special User, and Normal User roles
- **Crime Record Management**: Add, edit, delete, and view crime records
- **Advanced Search**: Search by name, city, or crime type with HashMap indexing
- **Image Upload**: Upload and store criminal photos (without ASCII display)
- **Web Interface**: Spring Boot page for viewing crime records
- **Realtime updates**: Web page auto-refreshes on add/edit/delete (WebSocket + fallback polling)
- **Newest-first ordering**: Latest records shown first; grouped as Today, Yesterday, Earlier
- **Data Structures**: Uses ArrayList, Stack, HashMap, and Queue for efficient operations
- **MongoDB Integration**: Persistent storage with MongoDB database
- **Input Validation**: Comprehensive validation and error handling

## Usage

### Console Interface
1. Start the application using one of the run commands above
2. Login with one of the default users
3. Navigate through the role-specific menus:
   - **Admin Menu**: Add users, manage crime records, view web interface
   - **Special User Menu**: Manage crime records, view web interface  
   - **Normal User Menu**: View and search crime records, access web interface

### Web Interface
1. From any console menu, select "View Crime Records (Web Interface)"
2. The system will automatically start a Spring Boot web server
3. Your default browser will open automatically to the correct URL (dynamic port if launched from the console). If running only the web app with Maven, open `http://localhost:8080/crime-records`.
4. Use the web interface to:
   - View all crime records with newest-first ordering
   - Records grouped by date: Today, Yesterday, Earlier
   - Search records by name, city, or crime type
   - Close the page to return to console

## Menu Options by Role

### Admin Menu
1. Add User
2. Add Crime Record  
3. Edit Crime Record
4. Delete Crime Record
5. View All Crimes
6. Search Crime
7. **View Crime Records (Web Interface)**
8. Logout

### Special User Menu
1. Add Crime Record
2. Edit Crime Record  
3. Delete Crime Record
4. View All Crimes
5. Search Crime
6. **View Crime Records (Web Interface)**
7. Logout

### Normal User Menu
1. View All Crimes
2. Search Crime
3. **View Crime Records (Web Interface)**
4. Logout

## Project Structure

```
src/main/java/com/crimemanagement/
├── config/          # Database configuration
├── controller/      # Spring Boot web controllers
├── exception/       # Custom exceptions
├── model/          # Data models (User, Crime)
├── service/        # Business logic services
├── ui/             # Console user interface
└── util/           # Utility classes and helpers

src/main/resources/
├── templates/      # HTML templates for web interface
└── static/         # Static web resources (CSS, JS)

scripts/            # Build and run scripts
```

## Technical Details

- **Backend**: Java with Spring Boot for web interface
- **Database**: MongoDB with Java driver
- **Frontend**: Thymeleaf templates with Bootstrap CSS
- **Data Structures**: ArrayList (storage), Stack (LIFO display), HashMap (fast search), Queue (FIFO web display)
- **Architecture**: Hybrid console + web application

## Troubleshooting

1. **MongoDB Connection Issues**: Ensure MongoDB is running on localhost:27017
2. **Port Already in Use**: When launching from the console, the app auto-selects a free port. When running `mvn spring-boot:run` directly, ensure 8080 is free or set a different port via `-Dserver.port=<port>`.
3. **PowerShell '&&' Not Supported**: Use a semicolon `;` to chain commands in PowerShell.
4. **Java Version**: Ensure you're using Java 11 or higher
5. **Maven Issues**: Run `mvn clean install` to resolve dependency issues

## Changes from Previous Version

- **Removed**: ASCII art photo display functionality
- **Added**: Spring Boot web interface for viewing crime records
- **Added**: Newest-first ordering and date grouping on the web page
- **Enhanced**: Image upload capability (storage only, no ASCII conversion)
- **Improved**: User experience with hybrid console/web approach
