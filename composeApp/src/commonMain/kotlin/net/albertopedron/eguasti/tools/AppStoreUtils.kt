package net.albertopedron.eguasti.tools

expect class AppStoreUtils() {
    fun openForReview()

    fun openUrl(url: String)
}