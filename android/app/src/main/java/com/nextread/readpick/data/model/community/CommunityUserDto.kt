package com.nextread.readpick.data.model.community

import kotlinx.serialization.Serializable

@Serializable
data class CommunityUserDto(
    val id: Long,
    val nickname: String,
    val profileImage: String? = null,
    val readBooksCount: Int = 0,
    val postsCount: Int = 0
) {
    companion object {
        /**
         * 게시글에서 사용자 정보를 추출하여 CommunityUserDto 생성
         */
        fun fromPost(post: CommunityPostDto, totalPosts: Int): CommunityUserDto {
            return CommunityUserDto(
                id = post.authorId,
                nickname = post.authorName,
                profileImage = post.authorPicture,
                readBooksCount = 0,
                postsCount = totalPosts
            )
        }
    }
}
