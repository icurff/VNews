package com.example.vnews.data.data_provider

data class CategoryEntity(
    val id: Int,
    val name: String
)

data class ExtensionEntity(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val icon: String,
    val source: String
)

object Categories {
    val all = listOf(
        CategoryEntity(1, "Tin mới"),
        CategoryEntity(2, "Thế giới"),
        CategoryEntity(3, "Kinh tế"),
        CategoryEntity(4, "Đời sống"),
        CategoryEntity(5, "Sức khỏe"),
        CategoryEntity(6, "Văn hóa"),
        CategoryEntity(7, "Giải trí"),
        CategoryEntity(8, "Thể thao"),
        CategoryEntity(9, "Công nghệ")
    )

}

object ExtensionEntities {
    // Tin mới (category_id = 1)
    val tinMoi = listOf(
        ExtensionEntity(
            1,
            1,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/tin-moi-nhat.rss"
        ),
        ExtensionEntity(
            2,
            1,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/home.rss"
        ),
        ExtensionEntity(
            3,
            1,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/tin-moi-nhat.rss"
        ),
        ExtensionEntity(
            4,
            1,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/home.rss"
        ),
        ExtensionEntity(
            5,
            1,
            "Báo Pháp Luật",
            "https://static-cms-plo.epicdn.me/v4/web/styles/img/favicon.png",
            "https://plo.vn/rss/home.rss"
        ),
        ExtensionEntity(
            6,
            1,
            "Báo VietNamNet",
            "https://static.vnncdn.net/ico/favicon.ico",
            "https://vietnamnet.vn/thoi-su.rss"
        )
    )

    // Thế giới (category_id = 2)
    val theGioi = listOf(
        ExtensionEntity(
            7,
            2,
            "Báo Pháp Luật",
            "https://static-cms-plo.epicdn.me/v4/web/styles/img/favicon.png",
            "https://plo.vn/rss/quoc-te-8.rss"
        ),
        ExtensionEntity(
            8,
            2,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/the-gioi.rss"
        ),
        ExtensionEntity(
            9,
            2,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/the-gioi.rss"
        ),
        ExtensionEntity(
            10,
            2,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/the-gioi.rss"
        ),
        ExtensionEntity(
            11,
            2,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/the-gioi.rss"
        ),
        ExtensionEntity(
            12,
            2,
            "Báo VietNamNet",
            "https://static.vnncdn.net/ico/favicon.ico",
            "https://vietnamnet.vn/the-gioi.rss"
        )
    )

    // Kinh tế (category_id = 3)
    val kinhTe = listOf(
        ExtensionEntity(
            13,
            3,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/kinh-te.rss"
        ),
        ExtensionEntity(
            14,
            3,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/kinh-doanh.rss"
        ),
        ExtensionEntity(
            15,
            3,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/kinh-doanh.rss"
        ),
        ExtensionEntity(
            16,
            3,
            "Báo Pháp Luật",
            "https://static-cms-plo.epicdn.me/v4/web/styles/img/favicon.png",
            "https://plo.vn/rss/kinh-te-13.rss"
        ),
        ExtensionEntity(
            17,
            3,
            "Báo VietNamNet",
            "https://static.vnncdn.net/ico/favicon.ico",
            "https://vietnamnet.vn/kinh-doanh.rss"
        )
    )

    // Đời sống (category_id = 4)
    val doiSong = listOf(
        ExtensionEntity(
            18,
            4,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/nhip-song-tre.rss"
        ),
        ExtensionEntity(
            19,
            4,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/doi-song.rss"
        ),
        ExtensionEntity(
            20,
            4,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/gia-dinh.rss"
        ),
        ExtensionEntity(
            21,
            4,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/doi-song.rss"
        )
    )

    // Sức khỏe (category_id = 5)
    val sucKhoe = listOf(
        ExtensionEntity(
            22,
            5,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/suc-khoe.rss"
        ),
        ExtensionEntity(
            23,
            5,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/suc-khoe.rss"
        ),
        ExtensionEntity(
            24,
            5,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/suc-khoe.rss"
        ),
        ExtensionEntity(
            25,
            5,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/suc-khoe.rss"
        )
    )

    // Văn hóa (category_id = 6)
    val vanHoa = listOf(
        ExtensionEntity(
            26,
            6,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/van-hoa.rss"
        ),
        ExtensionEntity(
            27,
            6,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/van-hoa.rss"
        )
    )

    // Giải trí (category_id = 7)
    val giaiTri = listOf(
        ExtensionEntity(
            28,
            7,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/giai-tri.rss"
        ),
        ExtensionEntity(
            29,
            7,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/giai-tri.rss"
        ),
        ExtensionEntity(
            30,
            7,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/suc-khoe.rss"
        ),
        ExtensionEntity(
            31,
            7,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/giai-tri.rss"
        )
    )

    // Thể thao (category_id = 8)
    val theThao = listOf(
        ExtensionEntity(
            32,
            8,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/the-thao.rss"
        ),
        ExtensionEntity(
            33,
            8,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/the-thao.rss"
        ),
        ExtensionEntity(
            34,
            8,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/the-thao.rss"
        ),
        ExtensionEntity(
            35,
            8,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/the-thao.rss"
        )
    )

    // Công nghệ (category_id = 9)
    val congNghe = listOf(
        ExtensionEntity(
            36,
            9,
            "Báo Tuổi trẻ",
            "https://statictuoitre.mediacdn.vn/web_images/favicon.ico",
            "https://tuoitre.vn/rss/nhip-song-so.rss"
        ),
        ExtensionEntity(
            37,
            9,
            "Báo Thanh Niên",
            "https://static.thanhnien.com.vn/thanhnien.vn/image/favicon.ico",
            "https://thanhnien.vn/rss/cong-nghe.rss"
        ),
        ExtensionEntity(
            38,
            9,
            "Báo VnExpress",
            "https://s1.vnecdn.net/vnexpress/restruct/images/favicon.ico",
            "https://vnexpress.net/rss/cong-nghe.rss"
        ),
        ExtensionEntity(
            39,
            9,
            "Báo Dân Trí",
            "https://dantri.com.vn/favicon.ico",
            "https://dantri.com.vn/rss/cong-nghe.rss"
        )
    )


    fun getExtensionEntitiesByCategoryId(categoryId: Int): List<ExtensionEntity> {
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