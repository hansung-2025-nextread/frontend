package com.nextread.readpick.util

import android.util.Log

/**
 * 이미지 URL 관련 유틸리티 함수
 */
object ImageUtils {

    private const val TAG = "ImageUtils"

    /**
     * 알라딘 책 표지 이미지 URL을 고화질 버전으로 변환
     *
     * 알라딘 API는 다음과 같은 이미지 크기를 제공:
     * - _1.jpg: 작은 이미지 (75x100)
     * - _2.jpg: 중간 이미지 (150x200)
     * - _3.jpg: 큰 이미지 (300x400)
     *
     * @param imageUrl 원본 이미지 URL
     * @param size 원하는 크기 (1, 2, 3 중 선택, 기본값 3)
     * @return 변환된 이미지 URL
     */
    fun getHighQualityCoverUrl(imageUrl: String, size: Int = 2): String {
        // 임시: URL 변환 비활성화 - 원본 그대로 반환
        return imageUrl

        /* 주석 처리: 이미지가 안 보이는 문제 해결 후 다시 활성화
        if (imageUrl.isEmpty()) return imageUrl

        // 알라딘 이미지 URL인지 확인 (대소문자 무시)
        if (!imageUrl.contains("aladin", ignoreCase = true)) {
            // 알라딘 URL이 아니면 원본 그대로 반환
            return imageUrl
        }

        // _1.jpg, _2.jpg 등을 찾아서 _3.jpg로 변경
        val result = when {
            imageUrl.contains("_1.jpg") -> imageUrl.replace("_1.jpg", "_${size}.jpg")
            imageUrl.contains("_2.jpg") -> imageUrl.replace("_2.jpg", "_${size}.jpg")
            imageUrl.contains("_3.jpg") -> imageUrl // 이미 최대 크기
            // 대문자 JPG도 처리
            imageUrl.contains("_1.JPG") -> imageUrl.replace("_1.JPG", "_${size}.JPG")
            imageUrl.contains("_2.JPG") -> imageUrl.replace("_2.JPG", "_${size}.JPG")
            imageUrl.contains("_3.JPG") -> imageUrl
            else -> {
                // 패턴이 없으면 원본 반환
                Log.d(TAG, "알라딘 URL이지만 패턴을 찾을 수 없음: $imageUrl")
                imageUrl
            }
        }

        if (result != imageUrl) {
            Log.d(TAG, "이미지 URL 변환: $imageUrl -> $result")
        }

        return result
        */
    }
}
