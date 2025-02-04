package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test

class ChannelHandlerTest {

    private val handleChannelPostMock = mockk<HandleChannelPost>(relaxed = true)

    private val sut = ChannelHandler(handleChannelPostMock)

    @Test
    fun `checkUpdate returns false when there is no channel post nor edited channel post`() {
        val anyUpdateWithNoChannelPost = anyUpdate(channelPost = null, editedChannelPost = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoChannelPost)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is channel post`() {
        val anyUpdateWithChannelPost = anyUpdate(channelPost = anyMessage())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithChannelPost)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is edited channel post`() {
        val anyUpdateWithEditedChannelPost = anyUpdate(editedChannelPost = anyMessage())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithEditedChannelPost)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `channel post is properly dispatched to the handler function`() {
        val botMock = mockk<Bot>()
        val anyChannelPost = anyMessage()
        val anyUpdateWithChannelPost = anyUpdate(channelPost = anyChannelPost)

        sut.handlerCallback(botMock, anyUpdateWithChannelPost)

        val expectedChannelHandlerEnv = ChannelHandlerEnvironment(
            botMock,
            anyUpdateWithChannelPost,
            anyChannelPost,
            isEdition = false
        )
        verify { handleChannelPostMock.invoke(expectedChannelHandlerEnv) }
    }

    @Test
    fun `edited channel post is properly dispatched to the handler function`() {
        val botMock = mockk<Bot>()
        val anyEditedChannelPost = anyMessage()
        val anyUpdateWithEditedChannelPost = anyUpdate(editedChannelPost = anyEditedChannelPost)

        sut.handlerCallback(botMock, anyUpdateWithEditedChannelPost)

        val expectedChannelHandlerEnv = ChannelHandlerEnvironment(
            botMock,
            anyUpdateWithEditedChannelPost,
            anyEditedChannelPost,
            isEdition = true
        )
        verify { handleChannelPostMock.invoke(expectedChannelHandlerEnv) }
    }
}
