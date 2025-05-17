package com.example.redditaddon.model

data class Publication(
    val `data`: Data,
    val kind: String
) {
    fun toPublicationItemList() : List<PublicationItem> {
        return this.data.children.map { item ->
            PublicationItem(
                id = item.data.name,
                author = item.data.author,
                commentsCount = item.data.num_comments,
                created = item.data.created,
                text = item.data.title,
                thumbNail = ThumbNail(
                    image = item.data.thumbnail,
                    thumbNailHeight = item.data.thumbnail_height.toDouble(),
                    thumbNailWidth = item.data.thumbnail_width.toDouble()
                ),
            )
        }
    }
}