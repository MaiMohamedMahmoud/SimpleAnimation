package com.udacity

enum class DownloadingLinks(val title: Int, val url: String) {
    GLIDE(R.string.glide, "https://github.com/bumptech/glide"),
    RETROFIT(R.string.retrofit, "https://github.com/square/retrofit"),
    LoadApp(
        R.string.loadApp,
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
    )
}