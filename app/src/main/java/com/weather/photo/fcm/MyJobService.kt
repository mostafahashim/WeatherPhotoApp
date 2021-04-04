package com.weather.photo.fcm

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class MyJobService:JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }
}