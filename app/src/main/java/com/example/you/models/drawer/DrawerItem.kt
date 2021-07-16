package com.example.you.models.drawer

import androidx.fragment.app.Fragment

data class DrawerItem(
    val type:Int,
    val fragmentId: Fragment?,
    val icon:Int,
    val itemTitle:String
)
