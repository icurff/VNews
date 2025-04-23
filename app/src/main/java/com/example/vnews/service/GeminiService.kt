package com.example.vnews.service

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {
    private val apiKey = "secretkey"

    private val generativeModel by lazy {

        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,

            )
    }

    suspend fun summarizeArticle(title: String, content: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
Bạn là một trợ lý AI có chuyên môn trong việc tóm tắt nội dung báo chí cho độc giả bận rộn.
Hãy phân tích và tóm tắt bài báo sau thành 3–5 ý chính, mỗi ý ngắn gọn, rõ ràng (tối đa 2 câu).
Yêu cầu quan trọng:
Giữ giọng văn trung lập, không cảm tính
Tập trung vào sự kiện, số liệu, nhân vật, địa điểm, thời gian, diễn biến chính
Không nêu lại tiêu đề, không đưa quan điểm cá nhân
Nếu có thông tin mơ hồ, ưu tiên diễn đạt khách quan thay vì suy diễn
Sử dụng ngôn ngữ phổ thông, dễ hiểu, tránh thuật ngữ chuyên ngành nếu không cần thiết
Tiêu đề: $title
Nội dung: $content
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                response.text ?: "Không thể tạo tóm tắt vào lúc này"
            } catch (e: Exception) {
                "Lỗi khi tạo tóm tắt: ${e.message}"
            }
        }
    }
}