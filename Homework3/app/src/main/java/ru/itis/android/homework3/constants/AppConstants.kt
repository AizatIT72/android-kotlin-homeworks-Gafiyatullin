package ru.itis.android.homework3.constants

object AppConstants {
    const val MIN_PASSWORD_LENGTH = 8
    const val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

    object Navigation {
        const val LOGIN_SCREEN = "login"
        const val NOTES_SCREEN = "notes/{email}"
        const val ADD_NOTE_SCREEN = "addNote"
        const val EMAIL_ARGUMENT = "email"
    }
}