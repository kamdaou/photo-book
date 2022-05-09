package com.example.photobook.utils

object Constants {
    const val IMAGE_NAME = "IMAGE"
    const val VIDEO_NAME = "VIDEO"

    /**
     * Status - Status of data
     * @LOADING - Data are being load
     * @ERROR - There was an error while loading data
     * @DONE - Data loaded successfully
     */
    enum class Status{
        LOADING,
        ERROR,
        DONE
    }


    /**
     * VoteType - Type of votes
     * @UP: an upvote
     * @DOWN: a down vote
     */
    enum class VoteType {
        UP,
        DOWN
    }
}