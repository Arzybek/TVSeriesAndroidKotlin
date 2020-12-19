package com.example.tvseriesprojectapp.user

object UserClass{
    var name: String = "unauthorized"
    var role: Role = Role.USER
    var age: Int = 0
    var photoLink:String = ""

}

enum class Role(val role: String){
    ADMIN("admin"),
    USER("user")
}