package com.app.gong4.api

import com.app.gong4.DTO.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {
    /* 로그인 */
    @POST("/api/user/login")
    fun login(@Body body: RequestLoginBody) : Call<ResponseLoginBody>

    /* refresh token */
    @POST("/api/user/login/refresh")
    fun refreshToken(@Body body: RequestRefreshTokenBody) : Call<ResponseRefreshTokenBody>

    @POST("/api/user/join")
    fun signup(@Body body: RequestSignupBody) : Call<ResponseSignupBody>

    /* 비밀번호 찾기 */
    @PATCH("/api/user/mail/passwd")
    fun findPwd(@Body body: RequestFindPwdBody) : Call<ResponseFindPwdBody>
}