package com.cmd3.app


import com.intellij.util.ui.DrawUtil
import com.terminal.model.StyleState
import com.terminal.model.TerminalTextBuffer
import com.terminal.ui.TerminalPanel
import com.terminal.ui.UIUtil
import com.terminal.ui.settings.SettingsProvider
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver


class TerminalPanel (

    mySettingsProvider: SettingsProvider,
    styleState: StyleState,
    backBuffer: TerminalTextBuffer
) : TerminalPanel(mySettingsProvider, backBuffer, styleState) {
    override fun dispose() {
        //TODO
    }

    override fun setupAntialiasing(graphics: Graphics) {
        DrawUtil.setupComposite(graphics as Graphics2D)
        UIUtil.applyRenderingHints(graphics)
    }



    override fun drawImage(
        g: Graphics2D,
        image: BufferedImage,
        dx1: Int,
        dy1: Int,
        dx2: Int,
        dy2: Int,
        sx1: Int,
        sy1: Int,
        sx2: Int,
        sy2: Int
    ) {
        drawImage(g, image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null)
    }

    override fun createBufferedImage(width: Int, height: Int): BufferedImage {
        return DrawUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }


    companion object {
        fun drawImage(
            g: Graphics,
            image: Image,
            dx1: Int,
            dy1: Int,
            dx2: Int,
            dy2: Int,
            sx1: Int,
            sy1: Int,
            sx2: Int,
            sy2: Int,
            observer: ImageObserver?
        ) {

           g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer)

        }

    }
}


