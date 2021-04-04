package com.weather.photo.remoteConnection.remoteService

interface RemoteCallback {
     fun onStartConnection()

     fun onFailureConnection(errorMessage: Any?)

     fun onSuccessConnection(response: Any?){}
     fun onLoginAgain(errorMessage: Any?)
}