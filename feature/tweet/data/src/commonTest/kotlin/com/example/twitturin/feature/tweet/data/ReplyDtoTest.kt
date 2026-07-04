package com.example.twitturin.feature.tweet.data

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Parses a trimmed copy of a real `GET tweets/{id}/replies` payload (captured from the live
 * backend) and checks that the recursive [ReplyDto] tree round-trips into [Reply] with nesting
 * intact and the pointer fields (`tweet`/`parentTweet`/`parentReply`) ignored.
 */
class ReplyDtoTest {

    // Same settings as core:data HttpClientFactory's ContentNegotiation Json.
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val payload = """
        [
          {
            "content": "Answer",
            "likedBy": ["657f41612107c8081af6c9f2"],
            "tweet": "694d185f6d2f09b3f7177eaf",
            "parentTweet": "694d185f6d2f09b3f7177eaf",
            "author": {
              "major": "SE",
              "username": "junkidesu",
              "fullName": "Anwar Sh.",
              "kind": "student",
              "followingCount": 12,
              "id": "657f41612107c8081af6c9f2"
            },
            "createdAt": "2025-12-25T10:56:46.214Z",
            "updatedAt": "2025-12-25T10:56:46.214Z",
            "likes": 1,
            "isEdited": false,
            "replies": [
              {
                "content": "Second",
                "likedBy": [],
                "tweet": "694d185f6d2f09b3f7177eaf",
                "parentReply": "694d186e6d2f09b3f7177ec8",
                "author": {
                  "username": "invoker",
                  "fullName": "Salokhiddinov Mukhammadamin",
                  "id": "6581b2da3a45193233759a26"
                },
                "createdAt": "2025-12-25T10:56:57.532Z",
                "updatedAt": "2025-12-25T10:56:57.532Z",
                "likes": 0,
                "isEdited": false,
                "replies": [],
                "id": "694d18796d2f09b3f7177ede"
              }
            ],
            "id": "694d186e6d2f09b3f7177ec8"
          },
          {
            "content": "how you been ?",
            "likedBy": [],
            "tweet": "694d185f6d2f09b3f7177eaf",
            "parentTweet": "694d185f6d2f09b3f7177eaf",
            "author": null,
            "createdAt": "2026-01-21T15:42:32.715Z",
            "updatedAt": "2026-01-21T15:42:32.715Z",
            "likes": 0,
            "isEdited": false,
            "replies": [],
            "id": "6970f3e86d2f09b3f71782d1"
          }
        ]
    """.trimIndent()

    @Test
    fun repliesPayload_parsesAsRecursiveTree() {
        val dtos = json.decodeFromString<List<ReplyDto>>(payload)

        assertEquals(2, dtos.size)
        assertEquals("Answer", dtos[0].content)
        assertEquals(1, dtos[0].replies?.size)
        assertEquals("Second", dtos[0].replies?.first()?.content)
        assertTrue(dtos[1].replies.isNullOrEmpty())
    }

    @Test
    fun toReply_keepsNestingAndDefaultsMissingFields() {
        val replies = json.decodeFromString<List<ReplyDto>>(payload).map { it.toReply() }

        val root = replies[0]
        assertEquals("694d186e6d2f09b3f7177ec8", root.id)
        assertEquals("Anwar Sh.", root.author?.fullName)
        assertEquals(1, root.replies.size)
        assertEquals("694d18796d2f09b3f7177ede", root.replies[0].id)
        assertEquals("invoker", root.replies[0].author?.username)
        assertTrue(root.replies[0].replies.isEmpty())

        // Null author + empty lists fall back to safe defaults.
        val second = replies[1]
        assertEquals(null, second.author)
        assertTrue(second.likedBy.isEmpty())
        assertTrue(second.replies.isEmpty())
    }
}
