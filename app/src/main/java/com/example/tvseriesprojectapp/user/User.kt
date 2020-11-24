package com.example.tvseriesprojectapp.user

object User{
    var name: String = "unAftorizeid"
    var role: Role = Role.USER
    var age: Int = 0
    var profileImageLink:String = ""

}

enum class Role(val role: String){
    ADMIN("admin"),
    USER("user")
}