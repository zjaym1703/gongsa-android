package com.app.gong4.api

import com.app.gong4.DTO.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    /* 로그인 */
    @POST("/api/user/login")
    fun login(@Body body: RequestLoginBody) : Call<ResponseLoginBody>

    /* refresh token */
    @POST("/api/user/login/refresh")
    fun refreshToken(@Body body: RequestRefreshTokenBody) : Call<ResponseRefreshTokenBody>

    /* 회원가임 */
    @POST("/api/user/join")
    fun signup(@Body body: RequestSignupBody) : Call<ResponseSignupBody>

    /* 이메일 인증 */
    @PATCH("/api/user/mail/join")
    fun certifyEmail(@Body body: RequestCertifyEmailBody) : Call<ResponseCertifyEmailBody>
    @PATCH("api/user/code")
    fun confirmCode(@Body body: RequestAuthCodeBody) : Call<ResponseAuthCodeBody>

    /* 비밀번호 찾기 */
    @PATCH("/api/user/mail/passwd")
    fun findPwd(@Body body: RequestFindPwdBody) : Call<ResponseFindPwdBody>

    /* 환경설정 - 유저정보조회 */
    @GET("/api/user/mypage")
    fun userMyPage() : Call<ResponseMyPageInfoBody>

    /* 내 스터디그룹 랭킹 조회 */
    @GET("/api/study-group/my-ranking")
    fun myStudyGroupRanking() : Call<ResponseMyStudyGroupRankingBody>

    /* 특정 기기로 푸시 알림 전송되는지 테스트 */
    @GET("/api/push")
    fun userPushMessage(@Query("targetToken") targetToken:String) : Call<BaseResponse>
}