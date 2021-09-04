package com.example.sosnew

data class DataContact(val name : String, val phoneNumber : String)

val listContact = mutableListOf<DataContact>()
var listTemp = mutableListOf<DataContact>(DataContact("me","0393363274"))//
