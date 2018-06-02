# Project Manager

Project Manager is an app that facilitates the planning and development of small-scale projects. With
Project Manager, you can handle multiple projects with ease. For each project, you can create
specification lists, task lists, and deadline lists. Apart from rigid lists, you can write ideas and notes
about your projects. In addition, you can set notifications about upcoming deadlines and view the
remaining tasks and deadlines of your projects in separate home screen widgets. In order to use the app,
you need to sign up with your Google Account. Once you are signed up you can access your data from
any Android device.


<p align="center">
  <img src="https://drive.google.com/uc?id=1j292_lw5Fy1SVE1x1aO7Flxj5gOM19LQ" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1ET50h2M73vLQG34LbxtsDaxLjZuwje-V" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=1LWwY3ti-UiUT-19ZTwPwWH3gFVq4RTMG" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1ay5Rxcb27M5uBJbe3Wbm3siFsC1IElHi" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=1w6Si7Jk4OUKsIhjKdfTYQbVGMeSWRx-U" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1SxRveZn_9b5ug190DHat8sKGVNV9MYLq" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=1vWDaZN_j70IGZeDLk4eu7NT080SnLpk_" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1hk3yy9O4KMK5AA9yMBcwI-35Zqhxx8M8" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=1fE92ADKdaZdI80hOLLXiSwluj_ksIGLN" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=1mEd_Hzj1usnEWEpPjCVmfo4gZF2cixvV" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1_IPFSdOzMxhjmcb7sS7OG5jbV6nK13tW" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=10UKI19Cb2-FfnzXVDxrbly0wH7cRZ8hz" width="300" height="450">
</p>


## Usage

You should use the installRelease Gradle Task to test the app.

## Technical Details

1. The user should sign in with the [**Firebase Authentication**](https://firebase.google.com/docs/auth/).

2. All the data is store with the help of [**Firebase Realtime Database**](https://firebase.google.com/docs/database/).

1. In order to get the recipe data the app makes a [**network request**](https://developer.android.com/training/basics/network-ops/) and then [**parses the JSON data**](https://developer.android.com/reference/org/json/package-summary) that it receives. Each network request is made on a seperate thread with the help of the [**Intent Service**](https://developer.android.com/reference/android/app/IntentService). The Intent Service makes a [**Broadcast**](https://developer.android.com/guide/components/broadcasts) in order to inform the main thread about the success or the failure of the process.
2. The app uses a [**RecyclerView**](https://developer.android.com/guide/topics/ui/layout/recyclerview) to show the list of recipes.
3. When the user clicks on a recipe the detail activity opens. The detail activity contains the following information: Ingredients and Recipe Steps. In order to show the ingredients and the steps at the same list the activity uses a [**RecyclerView**](https://developer.android.com/guide/topics/ui/layout/recyclerview) with two different View Types. When the user clicks on a recipe step a new activity opens.
4. The step activity makes use of a [**ViewPager**](https://developer.android.com/reference/android/support/v4/view/ViewPager) and a [**FragmentStatePagerAdapter**](https://developer.android.com/reference/android/support/v4/app/FragmentStatePagerAdapter) so that the user can swipe through the different steps without having to return to the previous activity.
5. In order to show a short video about every step the app uses the [**ExoPlayer**](https://developer.android.com/guide/topics/media/exoplayer) library.
5.  All the data is stored in an [**SQLite Database**](https://developer.android.com/training/data-storage/sqlite) and is accessed with the help of a [**Content Provider**](https://developer.android.com/guide/topics/providers/content-providers). If the user chooses to refresh the data, the old data will be deleted if the refresh procedure was successful.
6. The app has a [**Home-Screen Widget**](https://developer.android.com/guide/topics/appwidgets/overview) that shows the ingredients for each recipe.
7. The app makes use of [**Espresso**](https://developer.android.com/training/testing/espresso/) to test aspects of the UI.
