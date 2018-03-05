# BizagiVacationsApp
Mobile application developed in Android language, which allows managing the vacation approval process of company employees.

## Instructions

1. Clone or download the code contained in this repository.
2. Open the project using Android Studio.
3. Execute clean project and rebuild project.
4. Build APK File
5. Select the emulator or device and install the application.

## Considerations

- You can use your own web service to synchronize the information, you only need to modify the default parameters stored in the "Constants" file of the project.
- If you want the application to synchronize data periodically and automatically, you must ensure that the device has this autosync option enabled.
- All code is documented. You can identify the purpose of each class and function in each .java file.

## Packages description

- **dbsql**: This folder contains the classes responsible for managing requests to the local SQLite3 database.
- **provider**: This folder includes the necessary files to provide and share information between the application, with other applications and / or external services.
- **model**: This folder includes the structure of all the objects that will be used within the application, among the most important: User and RequestVacation.
- **utilities**: In this folder there are stored different files that are of usefulness for the application, facilitating tasks like Validations, checks of connection, customization of visual components, etc.
- **sync**: This is where you set up the data synchronization system with the server.
