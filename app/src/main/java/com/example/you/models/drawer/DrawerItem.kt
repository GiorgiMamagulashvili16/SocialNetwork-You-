package com.example.you.models.drawer

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections

data class DrawerItem(
    val type:Int,
    val action: NavDirections?,
    val icon:Int,
    val itemTitle:String
)
