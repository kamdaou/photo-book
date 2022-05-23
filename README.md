# photoBook

For my last project on udacity, I decided to create photoBook.

My application is a simple app that allows users to take picture and post some contents to share with other users (like a social media). It uses Firebase firestore and Firebase database. In addition, it uses also room database to ensure data persistence as requested in the rubric.

# Application UI

The application has two activity, authentication activity and MainActivity.
* The authentication activity allows users to authenticate before they can use the app.
* The main activity includes 4 fragments that displays some contents.
About the fragments:
  * The main fragment, displays posts from firestore. It uses recycler view and if the post contains a pictures, Glide is used to display these pictures (For a personal reason, a post cannot contain more than five pictures).
  * The add post fragment allows user to create and post their post. To open it, there is a button in main fragment. Users can just choose to write some thing or they can take picture also. And if they are taking picture, permission is asked first. Also, we use bundle in that fragment to make sure users’ posts are not losted.
  * The detail fragment displays the detail of the post with and the pictures, if any. To open the detail fragment, we use navigation and an argument is passed between the fragments using safe args.
  * From the detail fragment, user can hit on a picture to open it widely in the media fragment. In that fragment, the picture is displayed in a small dimension and if the user hits on it, it will fit all the place horizontally. This is done using motion layout.
  Almost in whole the application, constraint layouts are used to display data.

# Descriptions

To meet specification and only for that, images are stored in the local database. A coroutine worker is implemented so that these pictures can be retrieved daily. Hence, there is a remote repository and the database. But because I implemented the coroutine worker at the end after figuring out that I didn’t meet specification, the view model sometimes access data from the database or remote repository directly without passing through the repository (that should combine both remote and local data source).

The architecture is MVVM, logic operations are done in the different view models, data are accessed via local or remote repository and processed by the view models before they are displayed in the UI via fragments.

Another specification is that the whole code uses firebase emulator, in both test and the main code. The reason is just that I don’t want firebase to charge me since it’s just a simple app:). And because of that, you may see that in some of my tests, I don’t clear the remote repository ; it will be done once the emulator will be closed.

All the code was tested on samsung galaxy s8 and s10 respectively API 27 and API 29.

# How to install the app

Because it's just a sample and it's not stored on playStore, you must have android studio so that you can compile the application and install it.
For more information about how to run an app from android studio, please refers to [android blog](https://developer.android.com/training/basics/firstapp/running-app)

# License

The project is open source, you can modify and use it if you wish
