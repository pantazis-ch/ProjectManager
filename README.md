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

1. The user is able to sign in with the [**Firebase Authentication**](https://firebase.google.com/docs/auth/).

2. All the data is stored with the help of the [**Firebase Realtime Database**](https://firebase.google.com/docs/database/).

3. The app uses a [**RecyclerView**](https://developer.android.com/guide/topics/ui/layout/recyclerview) for all the lists ( Projects, Tasks, Deadlines etc. ).

4. The detail activity makes use of a [**ViewPager**](https://developer.android.com/reference/android/support/v4/view/ViewPager) and a [**FragmentStatePagerAdapter**](https://developer.android.com/reference/android/support/v4/app/FragmentStatePagerAdapter) so that the user can swipe through the different sections ( Brainstorming, Tasks etc. ).

5. The app has a [**Home-Screen Widget**](https://developer.android.com/guide/topics/appwidgets/overview) that shows the tasks for each project.
