package com.example.vnews.data.data_provider

import com.example.vnews.R

data class CategoryEntity(
    val id: Int,
    val nameResId: Int
)

data class DefaultExtension(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val icon: String,
    val source: String
)

object Categories {
    val all = listOf(
        CategoryEntity(1, R.string.category_latest),
        CategoryEntity(2, R.string.category_world),
        CategoryEntity(3, R.string.category_economy),
        CategoryEntity(4, R.string.category_life),
        CategoryEntity(5, R.string.category_health),
        CategoryEntity(6, R.string.category_culture),
        CategoryEntity(7, R.string.category_entertainment),
        CategoryEntity(8, R.string.category_sports),
        CategoryEntity(9, R.string.category_technology)
    )
}

object ExtensionEntities {
    // Tin mới (category_id = 1)
    val tinMoi = listOf(
        DefaultExtension(
            1,
            1,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/tin-moi-nhat.rss"
        ),
        DefaultExtension(
            2,
            1,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/home.rss"
        ),
//        DefaultExtension(
//            3,
//            1,
//            "Báo VnExpress",
//            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
//            "https://vnexpress.net/rss/tin-moi-nhat.rss"
//        ),
        DefaultExtension(
            3,
            1,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/home.rss"
        ),
        DefaultExtension(
            4,
            1,
            "Báo Pháp Luật",
            "https://static-cms-plo.epicdn.me/v4/web/styles/img/favicon.png",
            "https://plo.vn/rss/home.rss"
        ),
        DefaultExtension(
            5,
            1,
            "Báo VietNamNet",
            "https://static.vnncdn.net/ico/favicon.ico",
            "https://vietnamnet.vn/thoi-su.rss"
        )
    )

    // Thế giới (category_id = 2)
    val theGioi = listOf(
        DefaultExtension(
            7,
            2,
            "Báo Pháp Luật",
            "https://static-cms-plo.epicdn.me/v4/web/styles/img/favicon.png",
            "https://plo.vn/rss/quoc-te-8.rss"
        ),
        DefaultExtension(
            8,
            2,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/the-gioi.rss"
        ),
        DefaultExtension(
            9,
            2,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/the-gioi.rss"
        ),
        DefaultExtension(
            10,
            2,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/the-gioi.rss"
        ),
        DefaultExtension(
            11,
            2,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/the-gioi.rss"
        ),
        DefaultExtension(
            12,
            2,
            "Báo VietNamNet",
            "https://static.vnncdn.net/ico/favicon.ico",
            "https://vietnamnet.vn/the-gioi.rss"
        )
    )

    // Kinh tế (category_id = 3)
    val kinhTe = listOf(
        DefaultExtension(
            13,
            3,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/kinh-te.rss"
        ),
        DefaultExtension(
            14,
            3,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/kinh-doanh.rss"
        ),
        DefaultExtension(
            15,
            3,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/kinh-doanh.rss"
        ),
        DefaultExtension(
            16,
            3,
            "Báo Pháp Luật",
            "https://static-cms-plo.epicdn.me/v4/web/styles/img/favicon.png",
            "https://plo.vn/rss/kinh-te-13.rss"
        ),
        DefaultExtension(
            17,
            3,
            "Báo VietNamNet",
            "https://static.vnncdn.net/ico/favicon.ico",
            "https://vietnamnet.vn/kinh-doanh.rss"
        )
    )

    // Đời sống (category_id = 4)
    val doiSong = listOf(
        DefaultExtension(
            18,
            4,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/nhip-song-tre.rss"
        ),
        DefaultExtension(
            19,
            4,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/doi-song.rss"
        ),
        DefaultExtension(
            20,
            4,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/gia-dinh.rss"
        ),
        DefaultExtension(
            21,
            4,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/doi-song.rss"
        )
    )

    // Sức khỏe (category_id = 5)
    val sucKhoe = listOf(
        DefaultExtension(
            22,
            5,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/suc-khoe.rss"
        ),
        DefaultExtension(
            23,
            5,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/suc-khoe.rss"
        ),
        DefaultExtension(
            24,
            5,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/suc-khoe.rss"
        ),
        DefaultExtension(
            25,
            5,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/suc-khoe.rss"
        )
    )

    // Văn hóa (category_id = 6)
    val vanHoa = listOf(
        DefaultExtension(
            26,
            6,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/van-hoa.rss"
        ),
        DefaultExtension(
            27,
            6,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/van-hoa.rss"
        )
    )

    // Giải trí (category_id = 7)
    val giaiTri = listOf(
        DefaultExtension(
            28,
            7,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/giai-tri.rss"
        ),
        DefaultExtension(
            29,
            7,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/giai-tri.rss"
        ),
        DefaultExtension(
            30,
            7,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/suc-khoe.rss"
        ),
        DefaultExtension(
            31,
            7,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/giai-tri.rss"
        )
    )

    // Thể thao (category_id = 8)
    val theThao = listOf(
        DefaultExtension(
            32,
            8,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/the-thao.rss"
        ),
        DefaultExtension(
            33,
            8,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/the-thao.rss"
        ),
        DefaultExtension(
            34,
            8,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/the-thao.rss"
        ),
        DefaultExtension(
            35,
            8,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/the-thao.rss"
        )
    )

    // Công nghệ (category_id = 9)
    val congNghe = listOf(
        DefaultExtension(
            36,
            9,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/nhip-song-so.rss"
        ),
        DefaultExtension(
            37,
            9,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/cong-nghe.rss"
        ),
        DefaultExtension(
            38,
            9,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/cong-nghe.rss"
        ),
        DefaultExtension(
            39,
            9,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/cong-nghe.rss"
        )
    )


    fun getExtensionEntitiesByCategoryId(categoryId: Int): List<DefaultExtension> {
        return when (categoryId) {
            1 -> tinMoi
            2 -> theGioi
            3 -> kinhTe
            4 -> doiSong
            5 -> sucKhoe
            6 -> vanHoa
            7 -> giaiTri
            8 -> theThao
            9 -> congNghe
            else -> emptyList()
        }
    }
} 