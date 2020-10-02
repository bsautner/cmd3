/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.util.ui;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

public class DrawUtil {


    public static void drawLinePickedOut(Graphics graphics, int x, int y, int x1, int y1) {
        if (x == x1) {
            int minY = Math.min(y, y1);
            int maxY = Math.max(y, y1);
            graphics.drawLine(x, minY + 1, x1, maxY - 1);
        } else if (y == y1) {
            int minX = Math.min(x, x1);
            int maxX = Math.max(x, x1);
            graphics.drawLine(minX + 1, y, maxX - 1, y1);
        } else {
            drawLine(graphics, x, y, x1, y1);
        }
    }

    public static void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }



    public static Color getPanelBackground() {
        return UIManager.getColor("Panel.background");
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public static boolean isUnderDarcula() {
        return UIManager.getLookAndFeel().getName().contains("Darcula");
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public static boolean isUnderIntelliJLaF() {
        return UIManager.getLookAndFeel().getName().contains("IntelliJ");
    }




    public static void drawRectPickedOut(Graphics2D g, int x, int y, int w, int h) {
        g.drawLine(x + 1, y, x + w - 1, y);
        g.drawLine(x + w, y + 1, x + w, y + h - 1);
        g.drawLine(x + w - 1, y + h, x + 1, y + h);
        g.drawLine(x, y + 1, x, y + h - 1);
    }


    @NotNull
    public static BufferedImage createImage(int width, int height, int type) {
        return new BufferedImage(width, height, type);
    }

    @NotNull
    public static BufferedImage createImageForGraphics(int width, int height, int type) {
        return new BufferedImage(width, height, type);
    }



    private static final Supplier<Boolean> X_RENDER_ACTIVE = Suppliers.memoize(() -> {
        if (!SystemInfo.isXWindow) {
            return false;
        }
        try {
            final Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("sun.awt.X11GraphicsEnvironment");
            final Method method = clazz.getMethod("isXRenderAvailable");
            return (Boolean) method.invoke(null);
        } catch (Throwable e) {
            return false;
        }
    });

    /**
     * Configures composite to use for drawing text with the given graphics container.
     * <p/>
     * The whole idea is that <a href="http://en.wikipedia.org/wiki/X_Rendering_Extension">XRender-based</a> pipeline doesn't support
     * {@link AlphaComposite#SRC} and we should use {@link AlphaComposite#SRC_OVER} instead.
     *
     * @param g target graphics container
     */
    public static void setupComposite(@NotNull Graphics2D g) {
        g.setComposite(X_RENDER_ACTIVE.get() ? AlphaComposite.SrcOver : AlphaComposite.Src);
    }


}
