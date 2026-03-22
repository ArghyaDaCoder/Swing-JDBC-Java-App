# Swing-JDBC-Java-App
# Java Swing + JDBC Student Registration

A simple desktop application I built to practice integrating Java UIs with Oracle Database and Multithreading. 
:D

## Features
* Connects to a local Oracle XE database using a background thread (`Runnable`).
* Uses `synchronized` methods to safely pass the database connection.
* Captures user input via Swing GUI and inserts it using `PreparedStatement`.
* Handles robust error checking (SQL constraints and number formatting).

**Note:** If you want to run this locally, you will need the `ojdbc11.jar` file included in your classpath.

While practicing JDBC I thought it would be cool to add Swing and Multithreading and, here we are!
