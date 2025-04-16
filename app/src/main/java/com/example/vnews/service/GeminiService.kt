package com.example.vnews.service

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {
    private val apiKey = "HIDDENT API KEY"

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
                    Bạn là trợ lý AI chuyên tóm tắt nội dung báo chí. Hãy tóm tắt bài báo này thành 3-5 điểm chính,
                    mỗi điểm không quá 2 câu. Hãy giữ tông trung lập và tập trung vào thông tin quan trọng nhất.

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