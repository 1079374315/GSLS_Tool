package com.gsls.gsls_tool

import com.gsls.gt.GT.Hibernate.GT_Bean
import com.gsls.gt.GT.Hibernate.GT_Key


data class UserBean(
    var userId: Int,
    var userName: String,
    var userAge: Int,
    var userSex: Boolean
)
